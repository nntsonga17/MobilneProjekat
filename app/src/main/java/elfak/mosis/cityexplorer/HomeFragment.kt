package elfak.mosis.cityexplorer

import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay




class HomeFragment : Fragment() {

        lateinit var auth: FirebaseAuth
        lateinit var textName:TextView
        lateinit var database: DatabaseReference
        val sharedViewModel: UserViewModel by activityViewModels()
        val locationViewModel: LocationViewModel by activityViewModels()
        lateinit var load: ProgressBar
        lateinit var myPlacesU:Button
        lateinit var addPlace:Button
        lateinit var profilePic: ImageView
        lateinit var commentPlace:Button
        lateinit var myComments:Button
        lateinit var search:Button
        private lateinit var map: MapView
        private var placesArray:ArrayList<MyPlaces> = ArrayList()
        private lateinit var lastnameDatabase:TextView



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view=inflater.inflate(R.layout.fragment_home,container,false)
        textName=view.findViewById(R.id.textViewUserHome)
        auth=FirebaseAuth.getInstance()
        load=view.findViewById(R.id.loadNamepic)
        profilePic=view.findViewById(R.id.profilePic)
        lastnameDatabase=view.findViewById(R.id.textViewUserLastname)
        locationViewModel.setLocationAndNameComment("","","",false)
        locationViewModel.setLocationAndName("","","",false)
        setHasOptionsMenu(true)
        try {
            load.visibility=View.VISIBLE
            database = FirebaseDatabase.getInstance().getReference("Users")
            val key = sharedViewModel.name.replace(".", "").replace("#", "").replace("$", "").replace("[", "").replace("]", "")
            database.child(key).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    textName.text = snapshot.child("name").value.toString()
                    sharedViewModel.user= UserData(snapshot.child("username").value.toString(),snapshot.child("password").value.toString(),snapshot.child("firstName").value.toString(),snapshot.child("lastName").value.toString(),snapshot.child("phoneNumber").value.toString(),snapshot.child("imageUrl").value.toString(),ArrayList(),snapshot.child("points").value.toString().toInt())
                    lastnameDatabase.text=snapshot.child("lastName").value.toString()
                    val imageName=snapshot.child("imageUrl").value.toString()
                    if(imageName!="")
                    {
                        Glide.with(requireContext())
                            .load(imageName)
                            .into(profilePic)
                    }

                    load.visibility=View.GONE
                }
            }.addOnFailureListener { exception ->
                // Handle the exception here
                Log.e(TAG, "Error getting data from Firebase: ${exception.message}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error accessing Firebase: ${e.message}")

        }
        myPlacesU=view.findViewById(R.id.buttonMyPlaces)
        myPlacesU.setOnClickListener{
            findNavController().navigate(R.id.action_HomeFragment_to_MyPlacesFragment)
        }
        addPlace=view.findViewById(R.id.buttonAddObject)
        addPlace.setOnClickListener{
            sharedViewModel.updateDelete=true
            sharedViewModel.comment=false
            findNavController().navigate(R.id.action_HomeFragment_to_EditFragment)
        }

        commentPlace=view.findViewById(R.id.buttonKomentarisiOcena)
        commentPlace.setOnClickListener{
            sharedViewModel.comment=true
            sharedViewModel.updateDelete=false
            findNavController().navigate(R.id.action_HomeFragment_to_CommentGradeFragment)
        }
        myComments=view.findViewById(R.id.buttonMyComments)
        myComments.setOnClickListener{
            findNavController().navigate(R.id.action_HomeFragment_to_MyCommentFragment)
        }
        search=view.findViewById(R.id.buttonSearch)
        search.setOnClickListener{
            findNavController().navigate(R.id.action_HomeFragment_to_SearchObjectFragment)
        }
        var seeUserList=view.findViewById<Button>(R.id.buttonUserList)
        seeUserList.setOnClickListener{
            findNavController().navigate(R.id.action_HomeFragment_to_ListFragment)
        }
        val nizObserver= Observer<ArrayList<MyPlaces>>{ newValue->
            placesArray=newValue

            pinOnMap()
        }
        sharedViewModel.myPlaces.observe(viewLifecycleOwner,nizObserver)

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ctx = activity?.applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        map = requireView().findViewById(R.id.mapGPS)
        map.setMultiTouchControls(true)
        var startPoint: GeoPoint = GeoPoint(locationViewModel.getThisLat(),locationViewModel.getThisLon())
        map.controller.setCenter(startPoint)


        setupLocation()

    }


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)


    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater){
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.LoginFragment)
        {
            auth.signOut()
            findNavController().navigate(R.id.action_HomeFragment_to_LoginFragment)

        }
        if(item.itemId==R.id.InfoFragment)
        {
            findNavController().navigate(R.id.action_HomeFragment_to_InfoFragment)
        }
        if(item.itemId==R.id.EditUserFragment)
        {
            findNavController().navigate(R.id.action_HomeFragment_to_EditUserFragment)

        }

        return NavigationUI.onNavDestinationSelected(item!!,requireView().findNavController())||super.onOptionsItemSelected(item)
    }
    private fun setupLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            val requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    setMyLocationOverlay()
                    pinOnMap()

                }
            }

            // Pokretanje zahtjeva za dozvolom
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)

        } else {
            setUpMap()
        }
    }
    private fun setMyLocationOverlay() {
        val myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), map)
        myLocationOverlay.enableMyLocation()
        myLocationOverlay.enableFollowLocation()
        map.overlays.add(myLocationOverlay)
        map.controller.setCenter(myLocationOverlay.myLocation)
    }
    private fun pinOnMap() {
        for (myplace in placesArray) {
            if (myplace != null && myplace.latitude != null && myplace.longitude != null) {
                val sPoint = GeoPoint(myplace.latitude!!.toDouble(), myplace.longitude!!.toDouble())
                val marker = Marker(map)
                marker.position = sPoint
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                marker.title = myplace.name
                marker.subDescription = "Added by: ${myplace.author}<br>Grade: ${myplace.grade}"
                map.overlays.add(marker)
                map.invalidate()
            } else {
                Log.e(TAG, "Latitude or longitude is null for place: $myplace")
            }
        }
    }
    private fun setUpMap()
    {

        val myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), map)
        myLocationOverlay.enableMyLocation()
        myLocationOverlay.enableFollowLocation()
        map.overlays.add(myLocationOverlay)
        map.controller.setCenter(myLocationOverlay.myLocation)
        map.invalidate()
        var start = GeoPoint(
            locationViewModel.getThisLat(),
            locationViewModel.getThisLon()
        )

        var startPoint:GeoPoint= start

        map.controller.setZoom(14.0)
        map.invalidate()

        pinOnMap()
        map.controller.animateTo(startPoint)



    }
}