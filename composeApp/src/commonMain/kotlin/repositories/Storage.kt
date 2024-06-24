package repositories


data class Contact( val id: Int,val name: String, val status: String, val avatar: Int)
data class User( val id: Int,val name: String, val avatar: Int, val login: String, val password: String)

val CURRENT_USER = User(1, "Joey Tribbiani", Avatars.JOEY.drawable, "joey", "1234")

val CONTACTS = arrayListOf(
    Contact(2, "Chandler Bing", "Joey doesn't share food!", Avatars.CHANDLER.drawable),
    Contact(3, "Ross Geller", "UNAGI", Avatars.ROSS.drawable),
    Contact(4, "Rachel Green", "I got off the plane!", Avatars.RACHEL.drawable),
    Contact(5, "Phoebe Buffay", "Smelly cat, smelly cat..", Avatars.PHOEBE.drawable),
    Contact(6, "Monica Geller", "I need to clean up..", Avatars.MONICA.drawable),
    Contact(7, "Gunther", "They were on break :(", Avatars.GUNTHER.drawable),
    Contact(8, "Janice", "Oh. My. God", Avatars.JANICE.drawable),
    Contact(9, "Bob", "I wanna drink", Avatars.DEFAULT.drawable),
    Contact(10, "Marty McFly", "Back to the ...", Avatars.DEFAULT.drawable),
    Contact(12, "Emmet Brown", "Time fluid capacitor", Avatars.DEFAULT.drawable),
    Contact(13, "Friend1", "Time fluid capacitor", Avatars.DEFAULT.drawable),
    Contact(14, "Friend2", "Time fluid capacitor", Avatars.DEFAULT.drawable),
    Contact(15, "Friend3", "Time fluid capacitor", Avatars.DEFAULT.drawable),
    Contact(16, "Friend4", "Time fluid capacitor", Avatars.DEFAULT.drawable),
    Contact(17, "Friend5", "Time fluid capacitor", Avatars.DEFAULT.drawable),
    Contact(18, "Friend6", "Time fluid capacitor", Avatars.DEFAULT.drawable),
    Contact(19, "Friend7", "Time fluid capacitor", Avatars.DEFAULT.drawable),
    Contact(20, "Friend8", "Time fluid capacitor", Avatars.DEFAULT.drawable),
    Contact(21, "Friend9", "Time fluid capacitor", Avatars.DEFAULT.drawable),
    Contact(22, "Friend10", "Time fluid capacitor", Avatars.DEFAULT.drawable),
    Contact(23, "Friend11", "Time fluid capacitor", Avatars.DEFAULT.drawable),
    Contact(24, "Friend12", "Time fluid capacitor", Avatars.DEFAULT.drawable),
    Contact(25, "Friend13", "Time fluid capacitor", Avatars.DEFAULT.drawable),
    Contact(26, "Friend14", "Time fluid capacitor", Avatars.DEFAULT.drawable),
    Contact(27, "Friend15", "Time fluid capacitor", Avatars.DEFAULT.drawable),
    Contact(28, "Friend16", "Time fluid capacitor", Avatars.DEFAULT.drawable),
    Contact(29, "Friend17", "Time fluid capacitor", Avatars.DEFAULT.drawable),
    Contact(30, "Friend18", "Time fluid capacitor", Avatars.DEFAULT.drawable),
    Contact(31, "Friend19", "Time fluid capacitor", Avatars.DEFAULT.drawable),
    Contact(32, "Friend20", "Time fluid capacitor", Avatars.DEFAULT.drawable)
)

enum class Avatars(val drawable: Int) {
    CHANDLER(0),
    ROSS(1),
    MONICA(2),
    RACHEL(3),
    PHOEBE(4),
    GUNTHER(5),
    JOEY(6),
    JANICE(7),
    DEFAULT(8)
}