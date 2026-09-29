# Library App

A small console application written in Kotlin. It simulates a city library:
you can see books and magazines, search books by author, borrow and return items
and look at some simple statistics. At the start the app "loads" new books
from a server using a coroutine (it's just a delay, there is no real server).

## How to run
Clone the repository and from the terminal run:

```
./gradlew run
```

Requirements: JDK 17 or newer.

## Where the requirements are

| Requirement | Where |
|---|---|
| Variables, data types | `Main.kt` - `running` (Boolean), `averageYear` (Double), ids (Int), titles (String) |
| Conditions | `if` in `Library.borrowItem()`, `when` for the menu in `main()` |
| Loops | `while (running)` menu loop in `main()`, `for` loops in `printItems()` and `showStatistics()` |
| List | list of items in `Library`, list of readers in `main()` |
| Set | `Library.getGenres()` returns a `Set<String>` |
| Map | `borrowedBy` in `Library` (item id -> reader) |
| map / filter / reduce | `Library.totalPages()` (map + reduce), `Library.findItems()` (filter), `getGenres()` (map) |
| Functions, higher-order functions, lambdas | `Library.findItems(condition: (LibraryItem) -> Boolean)`, called with lambdas in `main()` |
| Classes and objects | `Library`, `Book`, `Magazine`, objects created in `main()` |
| Inheritance | `Book` and `Magazine` extend abstract class `LibraryItem` |
| Interfaces and polymorphism | `Describable` interface, `describe()` and `loanDays()` work differently for each class |
| Data class | `Reader` in `Models.kt` |
| Sealed class | `BorrowResult` (Success / NotAvailable / NotFound), handled with `when` in `borrowMenu()` |
| Suspend function and coroutine | `loadNewBooks()` is suspend, it's started with `launch` inside `runBlocking` in `main()` |

## Files

- `Models.kt` - interface, classes, data class, sealed class
- `Library.kt` - library logic and collection operations
- `Main.kt` - entry point, menu and coroutine
