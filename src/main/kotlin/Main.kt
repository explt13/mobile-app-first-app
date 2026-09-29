import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Дилей чтобы имитировать загрузку данных из сети
suspend fun loadNewBooks(): List<Book> {
    delay(1500)
    return listOf(
        Book(6, "The Hobbit", 1937, "J.R.R. Tolkien", 310, "Fantasy"),
        Book(7, "Dune", 1965, "Frank Herbert", 412, "Sci-Fi")
    )
}

fun printItems(list: List<LibraryItem>) {
    if (list.isEmpty()) {
        println("Nothing found.")
        return
    }
    // полиморфизм: describe() будет вызван для каждого конкретного типа предмета
    for (item in list) {
        println(item.describe())
    }
}

fun readNumber(text: String): Int? {
    print(text)
    return readlnOrNull()?.trim()?.toIntOrNull()
}

fun borrowMenu(library: Library, readers: List<Reader>) {
    println("Readers:")
    readers.forEach { println("  ${it.id}. ${it.name}") }

    val readerId = readNumber("Enter reader id: ")
    val reader = readers.find { it.id == readerId }
    if (reader == null) {
        println("No such reader.")
        return
    }

    val itemId = readNumber("Enter item id: ") ?: -1
    val result = library.borrowItem(itemId, reader)

    when (result) {
        is BorrowResult.Success ->
            println("${reader.name} took \"${result.item.title}\". Return it in ${result.days} days.")
        is BorrowResult.NotAvailable ->
            println("Sorry, \"${result.title}\" is already borrowed.")
        is BorrowResult.NotFound ->
            println("Item with this id doesn't exist.")
    }
}

fun showStatistics(library: Library) {
    val all = library.getAllItems()
    val books = library.getBooks()
    val magazines = all.size - books.size

    println("Total items: ${all.size} (books: ${books.size}, magazines: $magazines)")
    println("Genres: ${library.getGenres()}")
    println("Total pages of all books: ${library.totalPages()}")

    if (books.isNotEmpty()) {
        val averageYear: Double = books.map { it.year }.average()
        println("Average year of books: %.1f".format(averageYear))

        val oldest = books.minByOrNull { it.year }
        println("Oldest book: ${oldest?.title} (${oldest?.year})")
    }

    val borrowed = library.getBorrowed()
    if (borrowed.isEmpty()) {
        println("Nobody has borrowed anything yet.")
    } else {
        println("Borrowed items:")
        for ((itemId, reader) in borrowed) {
            println("  item $itemId -> ${reader.name}")
        }
    }
}

fun main() = runBlocking {
    val library = Library("City Library")

    library.addItem(Book(1, "1984", 1949, "George Orwell", 328, "Dystopia"))
    library.addItem(Book(2, "Harry Potter and the Philosopher's Stone", 1997, "J.K. Rowling", 223, "Fantasy"))
    library.addItem(Book(3, "Clean Code", 2008, "Robert Martin", 464, "Programming"))
    library.addItem(Magazine(4, "National Geographic", 2023, 5))
    library.addItem(Magazine(5, "Forbes", 2024, 12))

    val readers = listOf(
        Reader(1, "Aibek", 20),
        Reader(2, "Dana", 19),
        Reader(3, "Timur", 22)
    )

    println("Welcome to ${library.name}!")
    println("Loading new books...")

    // simple coroutine, main waits for it with join()
    val job = launch {
        val newBooks = loadNewBooks()
        library.addItems(newBooks)
        println("${newBooks.size} new books added.")
    }
    job.join()

    var running = true
    while (running) {
        println()
        println("1 - Show all items")
        println("2 - Show available items")
        println("3 - Search books by author")
        println("4 - Borrow item")
        println("5 - Return item")
        println("6 - Statistics")
        println("0 - Exit")
        print("Choose: ")

        when (readlnOrNull()?.trim()) {
            "1" -> printItems(library.getAllItems())
            "2" -> printItems(library.findItems { it.isAvailable })
            "3" -> {
                print("Author name: ")
                val author = readlnOrNull()?.trim() ?: ""
                val found = library.findItems { it is Book && it.author.contains(author, ignoreCase = true) }
                printItems(found)
            }
            "4" -> borrowMenu(library, readers)
            "5" -> {
                val id = readNumber("Enter item id: ") ?: -1
                if (library.returnItem(id)) {
                    println("Item returned, thank you!")
                } else {
                    println("This item wasn't borrowed or doesn't exist.")
                }
            }
            "6" -> showStatistics(library)
            "0" -> {
                println("Bye!")
                running = false
            }
            else -> println("Wrong option, try again.")
        }
    }
}
