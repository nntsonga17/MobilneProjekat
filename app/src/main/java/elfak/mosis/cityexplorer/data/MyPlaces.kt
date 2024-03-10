package elfak.mosis.cityexplorer.data

data class MyPlaces(var  name: String, var description: String){
    override fun toString(): String = name
}
