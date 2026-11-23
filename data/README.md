# Data files

All matrices are 0/1 user-permission matrices: one row per user, one column per permission, a 1 meaning the user holds the permission. In the Android data, a "user" is an app. Sizes below were counted by loading the files with `UpaMatrix.load`.

## android-500/

- `upa.csv`: 500 rows by 582 columns, 7598 cells set to 1. ASCII, LF line endings. Starts with a blank line, and each row ends with a trailing comma. Both are accepted by the loader.
- `permissions.csv`: 582 permission names on a single comma-separated line, in column order of `upa.csv`.
- `apps.txt`: 500 app names, one per line, in row order of `upa.csv`. UTF-8, LF line endings. 18 of the names contain commas (inside parentheses), which is why this file is one name per line and must not be read as comma-separated.

The three files are meant to be used together:

```
--input data/android-500/upa.csv --perms data/android-500/permissions.csv --users data/android-500/apps.txt
```

## android-5000/

- `upa-utf16.csv`: 5000 rows by 592 columns, 57982 cells set to 1. UTF-16 little endian with a byte order mark, CRLF line endings. Starts with a blank line, each row ends with a trailing comma, and the file ends after a final line break. No spaces were found in it. There is no name file for it: names are generated (`u0`..., `p0`...). Its 592 columns do not match the 582 permissions of `android-500/`, so `permissions.csv` must not be used with it.

## legacy/

- `combine-utf16.csv`: an older matrix export, 497 rows by 542 columns, 5108 cells set to 1. UTF-16 little endian with a byte order mark, CRLF line endings. Starts with a blank line, each row ends with a trailing comma, no spaces were found. No name file. Its dimensions differ from `android-500/` and it is kept for reference only.

## Origin

These files were consolidated from several duplicated copies in the original repository. Where the apps came from and when the data was collected is not recorded in the repository.
