class Library(val name: String) {

    private val items = mutableListOf<LibraryItem>()

    // key = item id, value = кто взял предмет (Reader)
    private val borrowedBy = mutableMapOf<Int, Reader>()

    fun addItem(item: LibraryItem) {
        items.add(item)
    }

    fun addItems(newItems: List<LibraryItem>) {
        items.addAll(newItems)
    }

    fun getAllItems(): List<LibraryItem> = items

    fun getBorrowed(): Map<Int, Reader> = borrowedBy

    // Функция поиска предметов по условию
    fun findItems(condition: (LibraryItem) -> Boolean): List<LibraryItem> {
        return items.filter(condition)
    }

    fun borrowItem(itemId: Int, reader: Reader): BorrowResult {
        val item = items.find { it.id == itemId }

        if (item == null) {
            return BorrowResult.NotFound
        }
        if (!item.isAvailable) {
            return BorrowResult.NotAvailable(item.title)
        }

        item.isAvailable = false
        borrowedBy[itemId] = reader
        return BorrowResult.Success(item, item.loanDays())
    }

    fun returnItem(itemId: Int): Boolean {
        val item = items.find { it.id == itemId }
        if (item == null || item.isAvailable) {
            return false
        }
        item.isAvailable = true
        borrowedBy.remove(itemId)
        return true
    }

    fun getBooks(): List<Book> = items.filterIsInstance<Book>()

    // Сет, чтобы не было повторов жанров
    fun getGenres(): Set<String> {
        return getBooks().map { it.genre }.toSet()
    }

    fun totalPages(): Int {
        val books = getBooks()
        if (books.isEmpty()) return 0
        return books.map { it.pages }.reduce { sum, pages -> sum + pages }
    }
}
