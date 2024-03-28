package elfak.mosis.cityexplorer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LocationViewModel : ViewModel() {
    private val _placeName = MutableLiveData<String>("")
    private val _database = MutableLiveData<Boolean>(false)
    private var thisLat: Double=0.0
    private var thisLon: Double=0.0
    fun setCoordinates(lat:Double,lon:Double)
    {
        thisLat=lat
        thisLon=lon
    }
    fun getThisLat():Double{return thisLat}
    fun getThisLon():Double{return thisLon}
    val placeName:LiveData<String>
        get()=_placeName
    val database:LiveData<Boolean>
        get()=_database
    private val _longitude = MutableLiveData<String>("")
    val longitude:LiveData<String>
        get()=_longitude
    private val _latitude = MutableLiveData<String>("")
    val latitude:LiveData<String>
        get()=_latitude
    var addObject: Boolean=false
    var viewObject: Boolean=false
    var oneObject:Boolean=false
    var commentObject:Boolean=false
    private val _latitudeComment = MutableLiveData<String>("")
    val latitudeComment:LiveData<String>
        get()=_latitudeComment
    private val _longitudeComment=MutableLiveData<String>("")
    val longitudeComment:LiveData<String>
        get()=_longitudeComment
    fun setLocation(long:String,lati:String)
    {
        _longitude.value=long
        _latitude.value=lati
    }
    fun setLocationAndName(long:String,lati:String,PlaceName:String,database:Boolean)
    {
        _longitude.value=long
        _latitude.value=lati
        _placeName.value=PlaceName
        _database.value=database

    }
    fun setLocationAndNameComment(lon:String,lat:String,PlaceName:String,database:Boolean)
    {
        _longitudeComment.value=lon
        _latitudeComment.value=lat
        _placeName.value=PlaceName
        _database.value=database

    }
    fun setLocationComment(lon:String,lat:String)
    {
        _longitudeComment.value=lon
        _latitudeComment.value=lat

    }
    fun setName(name:String)
    {
        _placeName.value=name
    }

}