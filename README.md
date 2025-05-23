# Design Explanation

**Why use LinkedHashMap to store matches?**
- Preserves insertion order, which supports stable and predictable iteration order for getSummary() when matches have the same score.
- Efficient lookups (O(1)) based on a normalized key, avoiding the need to search through a list.
- Ensures consistent behavior across operations like match removal and updates.

**Why overload startMatch()?**
- Allows custom initialization, in case the match needs to be recreated to avoid the need of updating it after creating a new one.

**Why use a record for NormalizedTeams?**
- Encapsulates normalization logic results (normalized home, away, and key) in a clear and immutable structure.
- Reduces parameter bloat and keeps the code cleaner and more readable.

**Why normalize team names?**
- Ensures idempotency: "mexico" vs "Mexico" vs " MeXico " will be treated the same.
- Avoids bugs where duplicates are inserted due to inconsistent formatting.
- Improves data integrity and user experience with consistent capitalization and spacing.

**Why is Match designed as a mutable class?**
- Score updates are a key of this program, mutability allows in-place updates to score.
- Start time and team names are immutable once set.

**Why not use a TreeSet instead of List in getSummary()?**
- Match is mutable, updating a match’s score after it’s added to a TreeSet can lead to unpredictable behavior.
- The summary only needs to be sorted when requested
- TreeSet would maintain ordering unnecessarily at all times, wasting resources and potentially introducing bugs.