package elfak.mosis.cityexplorer

import elfak.mosis.cityexplorer.MyPlaces

data class UserData(
    var username: String? = null,
    val password: String? = null,
    val phoneNumber: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val imageUrl: String? = null,
    var places:ArrayList<MyPlaces> =ArrayList(),
    var points:Int?=0
)
