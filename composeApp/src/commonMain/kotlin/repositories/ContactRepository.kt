package repositories

object ContactRepository {
    fun getContact(id: Int) : Contact {
        return contacts.find { it.id == id }!!
    }

    fun getFirst(): Contact {
        return contacts.first()
    }
    fun getLast() : Contact {
        return contacts.last()
    }
    fun all() = contacts.toList()

    private val contacts = CONTACTS
}