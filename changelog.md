### Fixed

* Fixed a critical server crash (NullPointerException) that could occur when checking mission progress, often triggered by blocks from other mods (e.g., the Create Saw).
* Player mission data is now initialized on-demand, preventing timing issues and ensuring mission data is always safe to access.
* Hardened the mission loading system; the server will now log an error and skip invalid missions instead of crashing due to malformed or incomplete mission JSON files.
* Improved overall stability by adding comprehensive null-safety checks across the entire mission system.