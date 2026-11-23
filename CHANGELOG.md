# Changelog

## Unreleased

- Cleanup of the repository: a shared core (matrix loading, miner interface, metrics, result files) and a single command line (`./rolemine`) replace the separate per-algorithm programs with hard-coded paths and copied loaders.
- The algorithms (CompleteMiner, FastMiner, Basic RMP, delta-approx RMP, MinNoise RMP) are ported onto the shared core.
- JDK-only build with `make` (no Maven or Gradle), plus plain-Java tests run by `make test`.
- Data files consolidated from duplicated copies into `data/`.
- Added README, `data/README.md`, `CITATION.cff`, this changelog, a to-do list and a GitHub Actions workflow that runs the tests.
