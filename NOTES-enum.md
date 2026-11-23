# CompleteMiner and FastMiner (subset enumeration)

Ports of the legacy `Complete Miner Algorithm/completeMiner.java` and
`Fast Miner Algorithm/prelimnaryCalculations/*.java` (entry point
`fastMinerTwo.java`). Both implement algorithms from Vaidya, Atluri and Warner,
"RoleMiner: mining roles using subset enumeration" (CCS 2006).

- Classes: `rolemining.miners.CompleteMiner` (`completeminer`),
  `rolemining.miners.FastMiner` (`fastminer`). The shared code is in
  `rolemining.miners.enumeration` (`Enumerator`, `CandidateSet`,
  `EnumerationResult`, `Settings`, `MinerSupport`).
- Tests: `src/test/java/rolemining/miners/enumeration/`. Parity digests:
  `src/test/resources/enumeration/legacy-500.properties`.

## Shared pipeline

Both miners run the same three phases (legacy `initPhase`, `secondPhase`,
`thirdPhase` (CompleteMiner only) and `calcRating`):

1. **Initial roles.** These are the distinct user rows, in the order each first
   appears. Each has an *original count*: the number of users whose row is
   exactly that role.
2. **Generated roles.** Intersections of initial roles (see each miner below).
   Each generated role gets a *count*.
3. **Candidate roles.** This is the result. It lists, in this order:
   - the initial roles that are also generated roles, in initial-role order (the
     two copies become one candidate);
   - the other initial roles, in initial-role order;
   - the other generated roles, in the order they were generated.

   For each candidate the legacy code computes a *rating* at a range of
   priorities p: `originalCount * p + count`. A generated-only candidate has
   originalCount 0, so its rating is its count. An initial-only candidate has
   count 0, so its rating is originalCount * p.

`mine()` returns the candidate roles in that order. With `order=rating`, they are
sorted instead by rating at `rankPriority`, highest first; ties keep the order
above. `mine()` also returns the exact user assignment: a role belongs to every
user whose permissions include all of the role's permissions, and an empty role
belongs to nobody. This is the same definition `Metrics.assignments` applies
when no assignment is given; it is computed in the miner only because it is
faster that way. The ratings, original counts, counts, generated roles and
FastMiner's contributor lists are available from `enumerate(upa, options)`,
which returns an `EnumerationResult`.

## Parameters (both miners unless noted)

| name | default | meaning |
|---|---|---|
| `legacy` | `false` | `true` reproduces the original programs exactly, defects included (see "Legacy defects"): the seeded all-zero role is kept, users with no permissions become an all-zero initial role, the merge step skips the role after each merge, and CompleteMiner's priorities are shifted by one step. |
| `minPriority` | `10` | smallest priority p |
| `priorityStep` | `10` | step between priorities |
| `maxPriority` | `100` | largest priority. The legacy programs called `calcRating(100)`. |
| `order` | `legacy` | `legacy` (the order above) or `rating` |
| `rankPriority` | = `maxPriority` | the priority used by `order=rating` |
| `maxGeneratedRoles` | `1000000` | CompleteMiner only. Stops with an error (`Enumerator.LimitExceededException`, an `IllegalArgumentException`) when more generated roles would be needed. `0` means no limit. |

Unknown parameter names are rejected by `mine()`.

## Stats reported (`MiningResult.stat`)

| stat | meaning (legacy name) |
|---|---|
| `legacyMode` | value of `legacy` |
| `usersWithoutPermissions` | users with an all-zero row |
| `initialRoles` | number of initial roles ("IR SIZE", "Orig count size") |
| `generatedRoles` | number of generated roles before the merge ("GR SIZE", "Count Size") |
| `mergedRoles` | initial roles merged with an equal generated role (FastMiner's `commonElements`) |
| `candidateRoles` | number of candidates ("Size of rating", "Size of CR") |
| `duplicateCandidates` | candidates equal to an earlier candidate. This is 0 unless `legacy=true`. |
| `initialPermissionsUsed` | distinct permissions in the initial roles ("Used permissions IR") |
| `generatedPermissionsUsed` | distinct permissions in the generated roles ("Used permissions GR") |
| `priorities` | the priority range used, e.g. `10..100 step 10` |
| `order` | `legacy` or `rating@<rankPriority>` |

## CompleteMiner

The generated roles are the intersections of every subset of two or more
initial roles. The enumeration follows the legacy loop order. For each initial
role i:

1. intersect role i with every later initial role;
2. then intersect role i with every generated role found so far, including roles
   added during this pass.

This loop does produce every subset intersection; `DefinitionTest` checks this
against brute force. A generated role's count is the number of users whose row
is a proper superset of it (legacy `thirdPhase`). Users whose row is exactly the
role are excluded from the count; they are its original count.

The number of intersections can grow exponentially, so `maxGeneratedRoles`
limits the run. Inside the enumeration roles are stored as `long[]` words in an
insertion-ordered open-addressing hash table, so the intersection loop does not
allocate memory.

## FastMiner

The generated roles are the intersections of each pair of initial roles i < j,
in loop order. A generated role's count is the total original count of the
initial roles whose pairwise intersections produced it (its *contributors*:
legacy `listOfContributors`). Each contributor is counted once.

### `FastMiner.candidateRoles(UpaMatrix, MinerOptions)` (for Delta RMP and MinNoise RMP)

This method returns the candidate roles exactly as the legacy program wrote them
to `CandidateRoles`: the same rows in the same order. For that reason `legacy`
defaults to **true** here, unlike in `mine()`. On the 500-app data:

- it returns 14606 roles;
- the 0/1 rendering (a blank first line, then `v,v,...,v,` per row) is byte-identical to `Output/Fast Miner output/CandidateRoles`;
- the list contains the seeded all-zero role at index 417 (0-based), right after the 182 merged and 235 unmerged initial roles;
- 134 rows repeat an earlier row (legacy merge defect, see below).

Passing `legacy=false` gives the corrected set: 14471 roles, the same as
`mine(...).roles()` in the default order. It contains the same distinct
non-empty roles as the legacy file. All other options are ignored, so a caller
may pass its own options object.

## Legacy defects

The first five are corrected by default. `legacy=true` brings back 1 to 4. A
crash case like 5 cannot be reproduced; the port returns a result instead.

1. **The merge skips the role after each merge.** Found in
   `CalcCandidateRolesAndRatings.calcRating` and `completeMiner.calcRating`.
   `iR.remove(i)` runs inside `for (i = 0; i < iR.size(); i++)` and is followed
   by `break`, so the next initial role moves into slot i and is never checked.
   Effects on the 500-app data:
   - 316 initial roles are also generated roles, but only 182 were merged;
   - the other 134 appear twice in CR: once as an initial role (rating
     `origCount * p`) and once as a generated role (rating `count`), instead of
     once with `origCount * p + count`;
   - this holds for both miners and accounts for the 134 duplicate rows of
     `CandidateRoles`.
2. **An all-zero role is seeded into the generated roles.** Found in
   `PhaseTwo.secondPhase` and `completeMiner.secondPhase`, which call
   `gR.add(...)` with zeros (and, in FastMiner, `count.add(0)`), apparently so
   that `gR.get(0)` exists for its width. An empty role grants nothing, yet it
   ended up in CR. In FastMiner it also collects every empty pairwise
   intersection. On the 500-app data, FastMiner gives it count 0 (no pair is
   disjoint) and CompleteMiner gives it count 500. The port drops it, so the
   generated-role counts are one lower: FastMiner 14370 (legacy 14371) and
   CompleteMiner 240904 (legacy 240905).
3. **CompleteMiner's priorities are shifted by one step.** In
   `completeMiner.calcRating`, `priority = priority + 10` runs before the
   rating is added, so `calcRating(100)` used p = 20, 30, ..., 110. FastMiner's
   copy of the method uses 10, ..., 100. The port uses `minPriority..maxPriority`
   for both, i.e. 10..100.
4. **Users with no permissions become an all-zero initial role** (both
   `initPhase` methods). The port leaves them out and reports
   `usersWithoutPermissions`. The 500-app data has none.
5. **Crashes.**
   - `completeMiner.thirdPhase` grows `count` with `count.add(i, ...)` only when a
     proper superset exists. If every user is empty, `count` stays empty and
     `calcRating` throws `IndexOutOfBoundsException`.
   - Both programs throw on an empty input (`uP.get(0)`).
   - The port handles both cases; see `EdgeCaseTest`.

Not ported:

- `fm()` always discards the first line of the input. This works only because
  `combineNew.csv` (now `data/android-500/upa.csv`) starts with a blank line.
  Given a file without one, the legacy code would silently drop the first user.
  `UpaMatrix.load` skips blank lines instead.
- `completeMiner.checkIr/checkGr/checkCr` compare every element with itself and
  so always print "Problem in ..."; `FewChecks` in FastMiner fixed this. These
  debug checks are not ported.
- The rows are compared with `!=` on boxed `Integer`s. This is correct only
  because of the Integer cache for 0 and 1; it has no effect on results.

Deliberate choices kept:

- the count definitions above;
- the rating `origCount * p + count`;
- the 10-step priority grid up to 100 (now parameters);
- the candidate order.

FastMiner side effect: a merged initial role that also contributed to its own
generated role (because it is a subset of another initial role) counts its own
users twice: once in `origCount * p` and once in `count`. This is how the
legacy program behaves, and the port keeps it.

## Parity with the legacy outputs (500-app data, `legacy=true`)

Each check rebuilds a legacy file from the port's result in the legacy text
format and compares its SHA-256 with the original file's. They run in
`LegacyParityTest`.

| legacy file | result |
|---|---|
| `Fast Miner output/CandidateRoles` (17 MB, 14606 rows) | byte-identical (via `FastMiner.candidateRoles`) |
| `Fast Miner output/ratings` | byte-identical |
| `Fast Miner output/PermCR` | byte-identical |
| `Fast Miner output/output` (permission and app distributions, IR sizes, orig counts, GR sizes, counts, GR SIZE 14371) | byte-identical |
| `Fast Miner output/numberofPermCR` | not comparable: empty, because `eachCrPermissions` is never filled |
| `Complete Miner output/output` (incl. GR SIZE 240905, the full Count list, "1st GR") | byte-identical |
| `Complete Miner output/ratings` (241140 rows, p = 20..110) | byte-identical |
| `Complete Miner output/IRPerms` | byte-identical |
| `Complete Miner output/NumPermCR` (permissions per CR) | byte-identical |
| `Complete Miner output/CRVertical` (per-permission count over CR) | byte-identical |
| `Complete Miner output/IRVertical`, `GRVertical` | not comparable: they contain `java.io.PrintWriter@...` (the writer object was printed instead of the list) |

The committed CompleteMiner outputs came from a slightly different version of
`completeMiner.java` than the one committed:

- the committed code writes `GRPerms` and `PermCR` (both missing from `Output/`)
  and an empty `NumPermCR`;
- it does not write `CRVertical`, `IRVertical` or `GRVertical`.

The formats of `NumPermCR` and `CRVertical` were inferred from the files. They
match byte for byte.

Default mode (corrected), 500-app data:

| | initial | generated | merged | candidates | duplicates |
|---|---|---|---|---|---|
| FastMiner | 417 | 14370 | 316 | 14471 | 0 |
| CompleteMiner | 417 | 240904 | 316 | 241005 | 0 |

In each case the candidates are the legacy candidates' distinct non-empty
roles (checked in `LegacyParityTest`).

## Runtime (measured on this machine: 2 cores, JDK 21)

500-app data, default options. `millis` covers `mine()`, including the exact
assignment. Wall time covers the whole `./rolemine ... --out DIR` run: JVM
start, loading, metrics and writing the output files.

| miner | `mine()` | wall, whole CLI run | output written |
|---|---|---|---|
| FastMiner | 129 to 225 ms | 2.8 s | 34 MB |
| CompleteMiner | 2.6 to 3.4 s | 5.5 s | 480 MB, of which `assignments.csv` is 363 MB |

The legacy-mode runs take about the same time.

5000-app data (`data/android-5000/upa-utf16.csv`):

- FastMiner: 3025 initial roles, 246073 generated roles, 246299 candidates, in
  8.5 s of `mine()`.
- CompleteMiner: stops with the `maxGeneratedRoles` error after about 6 s,
  having passed 1,000,000 generated roles. It was not run without the limit.

The whole `make test` suite, including the 500-app parity runs of both miners,
takes about 10 s once compiled.
