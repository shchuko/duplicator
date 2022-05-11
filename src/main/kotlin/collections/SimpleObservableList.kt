package collections


class SimpleObservableList<T>(
    private val onAdd: (element: T) -> Unit,
    private val onRemove: (index: Int) -> Unit,
    private vararg val alwaysPresentValues: T
) {
    private val internalList = mutableListOf<T>()

    init {
        alwaysPresentValues.forEach(::add)
    }

    fun add(element: T) {
        internalList.add(element)
        onAdd(element)
    }

    fun removeAt(index: Int) {
        internalList.removeAt(index)
        onRemove(index)
    }

    fun remove(element: T) {
        internalList.forEachIndexed { index, listElement ->
            if (element == listElement) {
                removeAt(index)
                return
            }
        }
        throw NoSuchElementException()
    }

    fun clear() {
        repeat(internalList.size) {
            removeAt(0)
        }
        alwaysPresentValues.forEach(::add)
    }

    fun toList(): List<T> = internalList
}
