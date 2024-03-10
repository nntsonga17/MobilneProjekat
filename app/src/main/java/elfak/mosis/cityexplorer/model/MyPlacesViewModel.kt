package elfak.mosis.cityexplorer.model

import androidx.lifecycle.ViewModel

class MyPlacesViewModel: ViewModel() {
     var myPlacesList: ArrayList<String> = ArrayList<String>()
     fun addPlace (place: String, desc: String){
         myPlacesList.add(place)
     }

}
