// Все что может быть показано в консоли должно реализовывать этот интерфейс
interface Describable {
    fun describe(): String
}

// Базовый класс для всех предметов библиотеки (книги, журналы)
abstract class LibraryItem(val id: Int, val title: String, val year: Int) : Describable {
    var isAvailable: Boolean = true

    // Книги и журналы могут иметь разное количество дней на возврат, поэтому делаем абстрактный метод
    abstract fun loanDays(): Int
}

class Book(
    id: Int,
    title: String,
    year: Int,
    val author: String,
    val pages: Int,
    val genre: String
) : LibraryItem(id, title, year) {

    override fun loanDays(): Int = 14

    override fun describe(): String {
        val status = if (isAvailable) "available" else "borrowed"
        return "[$id] Book: \"$title\" by $author ($year), $pages pages, $genre - $status"
    }
}

class Magazine(
    id: Int,
    title: String,
    year: Int,
    val issueNumber: Int
) : LibraryItem(id, title, year) {

    // Журналы на неделю меньше :)
    override fun loanDays(): Int = 7

    override fun describe(): String {
        val status = if (isAvailable) "available" else "borrowed"
        return "[$id] Magazine: \"$title\" #$issueNumber ($year) - $status"
    }
}

data class Reader(val id: Int, val name: String, val age: Int)

// Результат попытки взять предмет из библиотеки
sealed class BorrowResult {
    data class Success(val item: LibraryItem, val days: Int) : BorrowResult()
    data class NotAvailable(val title: String) : BorrowResult()
    object NotFound : BorrowResult()
}
