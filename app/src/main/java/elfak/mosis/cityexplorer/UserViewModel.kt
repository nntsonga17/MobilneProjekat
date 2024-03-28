package elfak.mosis.cityexplorer

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class UserViewModel : ViewModel() {
    var id:Int=0
    var name:String=""
    var imageUrl:String=""
    var pickedPlace:String=""
    var longitude=""
    var latitude=""
    var lastLongitude=""
    var lastLatitude=""
    var user: UserData = UserData()
    var place: MyPlaces = MyPlaces()
    var updateDelete=false
    var comment=false
    //SVI KORISNICI IZ BAZE
    private var appUsers:ArrayList<UserData> = ArrayList()
    private val _users= MutableLiveData<ArrayList<UserData>>()
    val users: LiveData<ArrayList<UserData>>
        get()=_users
    //SVA MESTA IZ BAZE
    private var allMyPlaces:ArrayList<MyPlaces> = ArrayList()
    private val _myPlaces= MutableLiveData<ArrayList<MyPlaces>>()
    val myPlaces: LiveData<ArrayList<MyPlaces>>
        get()=_myPlaces
    //SVI KOMENTARI IZ BAZE
    private var allComments:ArrayList<Comments> = ArrayList()
    private val _comments= MutableLiveData<ArrayList<Comments>>()
    val comments: LiveData<ArrayList<Comments>>
        get()=_comments
    //VEZA 1 TO 1 KOMENTAR KORISNIK
    private var supported:ArrayList<UserComment> = ArrayList()
    private val _othersSupported=MutableLiveData<ArrayList<UserComment>>()
    val othersSupported:LiveData<ArrayList<UserComment>>
        get()=_othersSupported
    //NIZ FILITRIRANIH MESTA
    private var filteredPlaces : ArrayList<MyPlaces> = ArrayList()

    fun setFilteredPlaces(newFP: ArrayList<MyPlaces>) {
        filteredPlaces = newFP
    }
    fun getFilteredPlaces(): ArrayList<MyPlaces> {
        return filteredPlaces
    }
    ////////////KOORDINATE SVIH MESTA
    private  var coordinates:ArrayList<Coordinates> = ArrayList<Coordinates>()
    fun getKoordinate():ArrayList<Coordinates>
    {
        return coordinates
    }
    fun setKoordinate(coords:ArrayList<Coordinates>)
    {
        coordinates = coords
    }
    fun addOne(coords:Coordinates)
    {
        coordinates.add(coords)
    }

    init
    {
        DataBase.databaseUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val updatedUsers= ArrayList<UserData>()
                for(user in snapshot.children)
                {
                    val el=user.getValue(UserData::class.java)
                    el?.let {
                        updatedUsers.add(it)
                    }
                }
                appUsers.clear()
                appUsers.addAll(updatedUsers)
                _users.postValue(appUsers)

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(ContentValues.TAG,"Error loading user from database ${error.message}")
            }
        })
        //SVA MESTA UZMI IZ BAZE
        DataBase.databasePlaces.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val updatedPlaces = ArrayList<MyPlaces>()
                for (placeSnapshot in snapshot.children) {
                    val place = placeSnapshot.getValue(MyPlaces::class.java)
                    place?.let {
                        updatedPlaces.add(it)

                    }
                }

                allMyPlaces.clear()
                allMyPlaces.addAll(updatedPlaces)
                _myPlaces.postValue(allMyPlaces)

            }

            override fun onCancelled(error: DatabaseError) {

                Log.e(ContentValues.TAG, "Error fetching Places data: ${error.message}")
            }
        })
        //SVE KOMENTARE IZ BAZE UZMI
        DataBase.databaseComments.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var commentsList=ArrayList<Comments>()
                for(commentSnapshot in snapshot.children)
                {


                    val comment=commentSnapshot.getValue(Comments::class.java)
                    comment?.let {
                        //IF ZA MESTO SPECIFICNO CE BITI NAPISAN KASNIJE
                        commentsList.add(it)


                    }


                }
                allComments.clear()
                allComments.addAll(commentsList)
                _comments.postValue(allComments)

            }
            override fun onCancelled(error: DatabaseError) {
                Log.e(ContentValues.TAG, "Error fetching Places data: ${error.message}")
            }

        })
        //SVI KOJI SU PODRZALI KOMENTARE IZ BAZE
        DataBase.dataBaseOneToOne.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val updatedO= ArrayList<UserComment>()
                for(user in snapshot.children)
                {
                    val el=user.getValue(UserComment::class.java)
                    el?.let {
                        updatedO.add(it)
                    }
                }
                supported.clear()
                supported.addAll(updatedO)
                _othersSupported.postValue(supported)

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(ContentValues.TAG,"Error while loading user from database ${error.message}")
            }
        })
    }
}