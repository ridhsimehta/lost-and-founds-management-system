# Lost and Found Management System

Java Swing + JDBC desktop app, plus a browser demo in `netlify-demo/`.

## Run the Java app
1. Download the `sqlite-jdbc` jar (xerial) and place it in this folder as `sqlite-jdbc.jar`.
   (It also needs `slf4j-api.jar` on newer versions; the 3.36.x build needs only itself.)
2. Compile (Linux/macOS):
   `mkdir -p out && javac -cp sqlite-jdbc.jar -d out $(find src -name "*.java")`
   Windows (PowerShell):
   `mkdir out; javac -cp sqlite-jdbc.jar -d out (Get-ChildItem -Recurse src -Filter *.java).FullName`
3. Run: `java -cp "out:sqlite-jdbc.jar" lostfound.Main` (use `;` instead of `:` on Windows).

Tables (`users`, `items`, `claims`) are created automatically in `lostfound.db`.
To use MySQL instead, change the URL in `Main.java`, add the MySQL connector, and replace
`AUTOINCREMENT` with `AUTO_INCREMENT` in `Repository.java`.

## Modules
| # | Module | Where |
|---|--------|-------|
| 1 | Lost item registration | `ItemFormPanel` (LOST) |
| 2 | Found item registration | `ItemFormPanel` (FOUND) |
| 3 | Automatic item matching | `MatchingService` thread, `Item.matchScore` |
| 4 | Claim verification | `MatchesPanel`, `Claim.verify` |
| 5 | Returned items report | `ReportPanel` |

## Syllabus concepts
- Unit 1: classes `Item`, `Claim`, `User`; String comparison in `matchScore` and `verify`
- Unit 2: `Item` to `LostItem`/`FoundItem`, interface `Matchable`, package `lostfound.core`
- Unit 3: `ItemNotFoundException`, `MatchingService` running in a background thread
- Unit 4: `Repository` with `PreparedStatement` and `ResultSet`
- Unit 5: Swing with `BorderLayout`, `GridLayout`, `FlowLayout`, and action events

## Deploy the demo on Netlify
Drag the `netlify-demo` folder onto https://app.netlify.com/drop, or connect a Git repo
(`netlify.toml` already sets the publish folder).
