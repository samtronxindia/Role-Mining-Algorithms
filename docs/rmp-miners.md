# Basic RMP, delta-approx RMP and MinNoise RMP

The legacy folders and files named here were removed from the working tree in the cleanup; they are in the git history (commit `cdee03a` and earlier).

Ports of the three legacy implementations in `Basic RMP Algorithm/`, `Delta RMP Algorithm/` and
`MinNoise RMP Algorithm/` onto the shared core. They implement the problem variants of Vaidya, Atluri
and Guo, "The role mining problem: finding a minimal descriptive set of roles" (SACMAT 2007):

- **Basic RMP**: find the fewest roles (PA) and a user assignment (UA) that reproduce the UPA exactly.
- **delta-approx RMP**: find the fewest roles whose reconstruction differs from the UPA in at most delta cells.
- **MinNoise RMP**: given a number of roles k, find k roles whose reconstruction differs from the UPA in as
  few cells as possible.

All three are greedy tilings. A *tile* is a set of permissions together with users who hold all of them;
its *uncovered area* is the number of its UPA cells that no earlier tile has covered yet.

| Class | `--algo` | Legacy entry point |
|---|---|---|
| `rolemining.miners.BasicRmp` | `basicrmp` | `BasicRMP/RunMain.java` |
| `rolemining.miners.DeltaRmp` | `deltarmp` | `DeltaRMP/RunDeltaRMP.java` |
| `rolemining.miners.MinNoiseRmp` | `minnoise` | `MinNoiseRMP/RunMinNoiseRMP.java` |

Shared helpers live in `rolemining.miners.rmp`:

- `RankedRoles`: candidate roles with their tiles, ranked by area (legacy `calcArea`/`sortRoles`).
- `Coverage`: the legacy `upaTilingMatrix`, with delta, under/over-assignment and the privilege percentages.
- `GreedyTiler`: picks the role with the largest uncovered area (legacy `calcFinalCR` + `updateUncoveredArea`).
- `PrefixTileMiner`: the legacy Basic RMP tile search (`AlgotithmLUTM.LUTM`).
- `RoleSteps`: MinNoise's singleton candidates and association step, and Delta RMP's post-processing.
- `CandidateSources`: where candidate roles come from.
- `Params`: boolean and list parameters.

The miners do no file I/O and print nothing. They keep no state between calls.

## Candidate roles

The legacy Delta and MinNoise RMP first ran the legacy FastMiner, then read its `CandidateRoles` file.
`DeltaRmp` and `MinNoiseRmp` (and only those) take a candidate source in their constructor:
`BiFunction<UpaMatrix, MinerOptions, List<BitSet>>`. The no-argument constructors use
`CandidateSources.defaultSource()`, which calls the ported `FastMiner.candidateRoles` and so returns
the same rows, in the same order, as the legacy `CandidateRoles` file. The miner's own options are
not passed on to FastMiner.

On the 500-app data that list has 14606 rows: 14471 distinct non-empty roles, the empty role and
134 duplicates. The duplicates come from `CalcCandidateRolesAndRatings.calcRating`, which skips the
element after every `iR.remove(i)`. Duplicates and the empty role make no difference to these miners
(`RankedRoles` drops them); the order matters only for breaking ties between candidates of equal area.

`CandidateSources.rowsAndPairwiseIntersections` is an independent generator of the same set, in a
different order (every distinct user row, then every new non-empty intersection of two rows). It is
used by the tests and by the parity harness as a cross-check: on the 500-app data every parity
result below is identical with either list.

## Shared behaviour (Delta RMP and MinNoise RMP)

- **Ranking.** A role's tile is every user whose row contains all of its permissions. Its area is
  |permissions| x |tile users|. Roles are sorted by area, largest first, and equal areas keep candidate order.
- **Greedy selection.** The next role is the unselected one with the largest uncovered area, and ties go to the
  higher-ranked role. Its whole tile is then granted. Because tiles are taken from the UPA, they never grant
  extra permissions. The port re-evaluates lazily (a priority queue of upper bounds) where the legacy code
  rescanned every candidate after each pick. The picks, ties included, are the same.
- **delta** is the legacy `updateDelta`: UPA cells not granted plus granted cells not in the UPA.
- **Privilege percentages** are the legacy `getPrivilege`. For each user, under = missing / (missing + held)
  and over = extra / (extra + held), where held = granted UPA cells. Each is averaged over all users and
  given as a percentage.
- **permissionCoverage** is the number of permissions granted to at least one user (legacy
  `getStaticPermissionCoverage`). **permissionsHeld** is the number of permissions held by at least one user.
  This is the "161" hard-coded in the legacy message "out of 161 permissions", and it is 161 on the 500-app data.

Legacy defects fixed in the shared code:

1. `CalculateAreaAndSort.sortRoles` / `CalcAreaAndSortRoles.sortRoles` start each search at `maxArea = 0,
   maxAreaIndex = 0`, so every zero-area candidate became another copy of candidate 0. The port drops
   zero-area candidates and duplicates. This cannot change a selection: a later copy is never picked before
   the earlier one, and afterwards its uncovered area is 0.
2. `calcFinalCR` (both miners) uses the same `0/0` start when picking. If no candidate has uncovered area left,
   it **re-adds the top-ranked role**, once per remaining loop iteration. The port stops instead. In legacy
   MinNoise this happens whenever an interim association leaves only over-assignment. I confirmed it on small
   matrices with the 120/130/134 constants lowered: in 28 of 380 random cases the legacy output was the port's
   output followed by copies of the first role.
3. `getPrivilege` divides 0 by 0 for a user with no permissions (under) or with nothing granted (over). That
   makes the whole average NaN. On the 500-app data, 2 apps are granted nothing at `delta=350` and 2 at
   `roles=10`, so the legacy code printed NaN as the over-privilege percentage there. Such a user now
   contributes 0.
4. `initializeFinalCR` always adds the top role before checking the stopping rule. The port adds none when
   none is needed: delta already reached, or `roles=0`.

## BasicRmp (`basicrmp`)

Covers the UPA exactly. Each step finds the first permission (by column) that some user holds but has not been
granted. It then considers only tiles whose permissions are a **contiguous column range** starting there, takes
the range with the largest uncovered area (the shortest on ties), and grants it to every user holding the whole
range. It stops when everything is covered. Each tile is one role, and its users are the role's assignment.

- **Parameters:** none.
- **Result:** roles in the order found, with user assignments (legacy `PA.txt` and `UA.txt`). The reconstruction
  distance is always 0. It is reported by the core metrics, as the legacy "Distance".
- **Stats:** none of its own. The legacy code printed only the role count, distance and permission count, and
  all three are in the core metrics.
- **Legacy behaviour kept:** the contiguous-range search in `AlgotithmLUTM.LUTM`. The legacy LUTM builds
  "conditional databases" but never feeds `nextConditionalUPA` back into `conditionalUPA`. They therefore only
  decide when to stop extending the range, and they never stop before the range's tile is empty, so the port
  stops when the tile is empty. This is much narrower than the Largest Tile Mining of Geerts et al. that the paper
  builds on, which considers any permission set. It is still a valid exact cover, so it was kept, and
  `PrefixTileMiner` documents it. A greedy over FastMiner candidates instead (`deltarmp` with `delta=0
  nonOverlapping=false`) needs 202 roles on the 500-app data, against 173.
- **Legacy defects fixed:** when there was nothing to cover (an all-zero matrix), the do-while in
  `RMP.RMPExecute` stored one empty tile, using the static `indexOfFirstPermissionLocated = 0`. The port
  returns no roles. An empty matrix crashed the legacy code on `UPA.get(0)`.
- **Dropped:** writing `ReconstructedUPA.txt` and `RoleCount.txt`. `RoleCount.txt` was written twice with the same
  content, and the core metrics replace both.
- **Parity:**
  - I ran the legacy code itself (a scratch copy with the hard-coded paths replaced) on the 500-app data. It gives
    173 roles, distance 0, and **PA.txt and UA.txt byte-identical** to the port's output.
  - It matches the hand-derived result on `small-4x7.csv` (3 roles).
  - It gave identical PA and UA on 270 random small matrices.
- **Runtime (500-app data):** 23–27 ms in `./rolemine`. The legacy code took about 5.6 s on the same machine.

## DeltaRmp (`deltarmp`)

Ranks the candidate roles by area, then greedily adds the role with the largest uncovered area until at most
`delta` UPA cells are uncovered. A user gets a role exactly when they hold all its permissions, so roles never
grant extra permissions, and delta is the number of uncovered UPA cells. By default it then applies the legacy
post-processing: each permission stays only in the first selected role that has it.

- **Parameters:**
  - `delta` = 350: the legacy `calcFinalCR(350)`. Stop once delta <= this value.
  - `nonOverlapping` = true: the legacy post-processing in `DeltaRMPAlgorithm.calcFinalCR`. Roles left empty
    are dropped.
  - `traceBelow` = 4000: the legacy `if (delta < 4000) callPrinter()`. Once delta is below this value, report
    `roles:delta` after each step.
- **Result:** roles in selection order (post-processed if `nonOverlapping`), with no explicit assignment. The core
  derives it: a user holds a role when they hold all its permissions. The legacy code wrote no UA either.
- **Stats:**
  - `delta`: reached, computed from the tiling before post-processing, as in the legacy code.
  - `deltaTarget`, and `deltaReached` (false when the candidates cannot cover enough).
  - `rolesSelected`: before post-processing. `roles` in the metrics is after it.
  - `overPrivilegePercent` (always 0 here) and `underPrivilegePercent`.
  - `permissionCoverage`, `permissionsHeld`.
  - `candidates`: ranked, non-zero-area, distinct.
  - `nonOverlapping`.
  - `deltaTrace`: replaces the legacy `PA_Delta_<delta>.txt` snapshot files.
- **What delta means:** with `nonOverlapping=false`, delta equals the core `distance` exactly (asserted in
  `DeltaRmpTest` on random matrices and on the 500-app data). With the post-processing on (the default), the
  roles no longer reproduce the tiling. On the 500-app data, delta is 349, but the core distance of the 26
  post-processed roles is 3871. This is the legacy behaviour, kept as the default. Use
  `nonOverlapping=false` for roles that are an actual delta-approximate decomposition.
- **Deviation:** the stopping rule. The legacy loop stops when `delta < tempDelta`. The paper's definition, and the
  port, stop at delta <= `delta`. The two differ only when delta lands exactly on the target. On the 500-app data
  it goes 359 → 349, so the default run is the same either way. To reproduce a legacy run with target T, pass
  `delta=T-1`.
- **Parity:**
  - The legacy `Delta RMP roles.csv` (74 roles, not post-processed) is **exactly the first 74 roles** of the
    port's run, in order, with either candidate list. Delta is 488 at that point.
  - With `delta=350` the run continues to 87 roles and delta 349. That 74-role file is what the legacy code would
    print for a target between 489 and 501 (as `delta=488..500` in the port), or it is the 74-role snapshot
    `PA_Delta_488.txt`, which was never post-processed. Which of the two produced it cannot be told from the outputs.
  - On 250 random small matrices, with the candidates the legacy FastMiner wrote for each, the post-processed roles
    (empty ones removed), delta, permission coverage and privilege percentages (where not NaN) all matched.
- **Runtime (500-app data, default candidates, delta=350):** about 210 ms in `./rolemine`, including generating
  14471 candidates. About 65 ms with the legacy candidate list already loaded. `delta=0` takes about 210 ms
  (202 roles).

## MinNoiseRmp (`minnoise`)

Adds a single-permission role for every permission to the candidates, unless one is already there
(legacy `UpdateCR.addIndividualPermToCR`, appended in permission order). It then ranks the candidates and
greedily picks up to `roles` roles. Finally it gives each role to every user holding more than
`associateThreshold` of its permissions, granting the whole role, so the user's missing ones become
over-assignment (legacy `associateAdditionalRoles`). Delta then counts both missing and extra permissions. The
association step is a legacy addition, not part of the paper's greedy tiling.

- **Parameters:**
  - `roles` = 10: the number of roles k. The legacy code required it as `args[0]` and had no default.
  - `associateThreshold` = 0.5: the legacy `common > 0.5 * numPerm` (strict). 1 disables association.
  - `interimAssociations` = `120,130`: the legacy `finalCR.size() >= 120` / `>= 130` blocks. When the number of
    selected roles reaches one of these values, and more roles are still to be picked, association also runs at
    that point and affects the later picks. `none` disables.
  - `roleCap` = 134: the legacy `finalCR.size() >= 134` block, which associates and breaks. At most this many roles
    are returned whatever `roles` says. 0 disables.
  - `refreshAfterAssociation` = true: see the fix below. false reproduces the legacy stale pick.
- **Stopping:** after `min(roles, roleCap)` roles; when delta is 0 (the legacy `delta < 1`); or when no candidate
  has uncovered area left (the legacy code re-added the top role, defect 2 above).
- **Result:** roles in selection order, with user assignments: the tile users plus the association step's users
  (the legacy `TilingUsers`). The core `distance` therefore equals `delta`.
- **Stats:**
  - `delta`: after association.
  - `deltaBeforeAssociation`: the greedy result alone.
  - `overPrivilegePercent`, `underPrivilegePercent`.
  - `permissionCoverage`.
  - `rolePermissions`: the legacy "Perm Cov. #2", distinct permissions in the roles.
  - `permissionsHeld`.
  - `rolesRequested`.
  - `associatedAssignments`: user-role assignments added by association.
  - `interimAssociationRuns`.
  - `candidates`.
  - `singletonCandidatesAdded`.
- **Legacy defect fixed (changes results, switchable):** after an interim association, the legacy code made the
  next pick on uncovered areas computed *before* that association. `updateUncoveredArea` is not called after
  `associateAdditionalRoles`. The port recomputes by default. `refreshAfterAssociation=false` restores the
  legacy pick, and the legacy outputs of every run that associated mid-way need it (below).
- **Harmless legacy oddities:**
  - The `else if` branch counting common permissions in `associateAdditionalRoles` can never fire.
  - At the cap the legacy code associated twice (inside `calcFinalCR` and again in `main`). Association is
    idempotent, so the port runs it once.
- **Parity:** all **38 of 38** files in `Output/Min Nosie RMP output/` are reproduced exactly: every role in
  order, plus the delta in the file name. This holds with both the legacy candidate file and the default
  generator. The outputs evidently come from four different procedures:
  - **Plain runs, default settings** (no interim association before k): R = 5, 10 (4878), 12, 20 (7075),
    30 (8633), 40 (9744), 50 (11303), 58, 60 (12442), 70 (13089), 77, 80 (14265), 90 (15125), 100 (15298),
    110, 120. That is 16 files.
  - **The current legacy code** (interim at 120 and 130, cap 134, stale pick): `PA_R_130_Delta_20685`,
    `PA_R_134_Delta_20911`. These need `refreshAfterAssociation=false`. With the fix (the port's default),
    R=130 gives delta 20701 and R=134 gives delta 21034, and both agree with the files for the first 120 roles.
  - **No interim association at all**, evidently a version before the 120/130 blocks: `PA_R_130_Delta_19868`
    (`interimAssociations=none`).
  - **Chained checkpoint runs:** association at each earlier checkpoint, each followed by a stale pick. That is
    what calling `calcFinalCR` again on the static state does, and what the 120/130 blocks do.
    - Every 5 (`interimAssociations=5,10,15,20,25,30,35,40 refreshAfterAssociation=false`): R = 10 (5461),
      15 (6403), 20 (6801), 25 (7302), 30 (7624), 35 (7898), 40 (9540), 45 (10278).
    - Every 10 (`10,20,...,90`): R = 20 (7079), 30 (7668), 40 (8527), 50 (11052), 60 (11383), 70 (13903),
      80 (15807), 90 (18045), 100 (20192).
    - `77,94`: R = 94 (16742), 134 (24742).

  What cannot be determined: the exact code or driver used for the chained runs. Several checkpoint sets reproduce
  some of the files. The sets listed are ones I found that reproduce every file of their family. Nor can it be
  determined why the 134-role cap exists.

  I also matched the port against the legacy code on 250 random small matrices (default constants) and on 380 with
  the 120/130/134 constants lowered to 2/3/5. In each case the port used the candidates the legacy FastMiner wrote
  for that matrix, with `interimAssociations=2,3 roleCap=5 refreshAfterAssociation=false`. Roles, delta and the
  privilege percentages (where not NaN) were identical, except for the re-added-top-role cases of defect 2.
- **Runtime (500-app data, default candidates):** about 150–160 ms for `roles=10`, 224 ms for `roles=134`, and
  228 ms for `roles=300 roleCap=0` (stops at 149 roles, when nothing is left uncovered).

## Tests and parity harness

- `src/test/java/rolemining/miners/rmp/*Test.java` (35 test methods, about 3 s for `make test`):
  - Hand-checked small matrices with candidates passed in directly: ranking, ties, post-processing, privilege,
    association, interim and stale picks.
  - The `small-4x7.csv` fixture.
  - Invariants: Basic RMP has distance 0; Delta RMP's delta equals the core distance without post-processing;
    MinNoise never returns more roles than asked or than candidates, and its delta equals the core distance.
    The lazy greedy picks the same roles as a full rescan, including after external grants.
  - Edge cases: empty matrix, all-zero rows, unreachable targets, bad parameters.
  - Runs on the 500-app data that reproduce `Delta RMP roles.csv` and `PA_R_5_Delta_4491`, `PA_R_20_Delta_7075`
    and (stale pick) `PA_R_10_Delta_5461`. Copies of these files are in `src/test/resources/rmp/`;
    `delta-rmp-roles-74.txt` is the CSV converted to one role per line.
- Differential runs against the legacy code itself were done once, outside the repository. A scratch copy of the
  legacy sources was compiled with the `C:\Users\...` paths replaced, and each random case was run through the legacy
  FastMiner and the three legacy entry points. That copy is not committed.
- `LegacyParity` (in the test tree, not a test) runs all comparisons above against the untouched legacy repository:
  `java -cp out/main:out/test rolemining.miners.rmp.LegacyParity /path/to/legacy/Role-Mining-Algorithms`.
  It takes about 27 s. It reads the 17 MB `CandidateRoles` file from there; that file is not copied into this
  repository.
