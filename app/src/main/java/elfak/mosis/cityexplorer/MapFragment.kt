package elfak.mosis.cityexplorer

import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class MapFragment : Fragment() {
    private lateinit var map: MapView

    private val locationViewModel: LocationViewModel by activityViewModels()
    private val sharedViewModel: UserViewModel by activityViewModels()
    private var placesArray: ArrayList<MyPlaces> = ArrayList()
    private var othersPlaces: ArrayList<MyPlaces> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        val arrayObserver = Observer<ArrayList<MyPlaces>> { newValue ->
            placesArray = newValue
            othersPlaces.clear()
            for (m in placesArray) {
                if (m.author != sharedViewModel.name) {
                    othersPlaces.add(m)
                }
            }
            //Kad izabere samo mapu
            if (locationViewModel.viewObject == true) {
                setMyLocationOverlay()
                markAllPlaces()

            } else {
                //Za dodavanje objekata
                if (locationViewModel.addObject == true) {

                    setMyLocationOverlay()
                    markAllPlaces()

                    setOnMapClickOverlay()


                }
                //Kad izabere Neki objekat iz liste objekata
                else if (locationViewModel.oneObject == true) {
                    markObjectOnMap()
                    setOnMapClickOverlay()

                    setMyLocationOverlay()


                } else if (locationViewModel.commentObject == true) {
                    //KAD IZABERE DA KOMENTARISE OBJEKAT
                    markOthersPlaces()
                    allowClicksOnOthersLocations()
                    setMyLocationOverlay()
                } else {
                    setMyLocationOverlay()
                }
            }
        }
        sharedViewModel.myPlaces.observe(viewLifecycleOwner, arrayObserver)

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ctx = activity?.applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        map = requireView().findViewById(R.id.map)
        map.setMultiTouchControls(true)


        setupLocation()

    }

    private fun setupLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            val requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    setMyLocationOverlay()
                    setOnMapClickOverlay()

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
        map.overlays.add(myLocationOverlay)
        map.controller.setCenter(myLocationOverlay.myLocation)

    }

    private fun setOnMapClickOverlay()
    {
        val myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), map)
        myLocationOverlay.enableMyLocation()
        map.overlays.add(myLocationOverlay)
        map.controller.setCenter(myLocationOverlay.myLocation)
        //OVAJ DEO KODA SLUZI ZA OMOGUCAVANJE KLIKA NA SVOJU LOKACIJU IZNAD JE DA OBELEZI TRENUTNU LOKACIJU
        var recive=object:MapEventsReceiver{
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                val clickedPoint = p ?: return false


                var startPoint =GeoPoint(myLocationOverlay.myLocation.latitude ,myLocationOverlay.myLocation.longitude)
                var endPoint = GeoPoint( clickedPoint.latitude , clickedPoint.longitude)

                if ( startPoint.distanceToAsDouble(endPoint)<= 60) {
                    if (locationViewModel.addObject == true) {
                        for (placeN in placesArray) {
                            var userPoint = GeoPoint(placeN.latitude!!.toDouble(), placeN.longitude!!.toDouble())
                            if (endPoint.distanceToAsDouble(userPoint) < 60) {
                                Toast.makeText(
                                    context,
                                    "Place already added to this location",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return false


                            }
                        }
                        val lon = clickedPoint.longitude.toString()
                        val lati = clickedPoint.latitude.toString()
                        if(sharedViewModel.updateDelete==true) {
                            locationViewModel.setLocation(lon, lati)
                        }
                        if(sharedViewModel.comment==true)
                        {
                            locationViewModel.setLocationComment(lon,lati)
                        }

                        findNavController().popBackStack()
                        return true

                    }
                    else if(locationViewModel.oneObject==true)
                    {
                        for (placeN2 in placesArray) {
                            var userPoint = GeoPoint(placeN2.latitude!!.toDouble(), placeN2.longitude!!.toDouble())
                            if(placeN2.name!=sharedViewModel.pickedPlace) {
                                if (endPoint.distanceToAsDouble(userPoint) < 60) {
                                    Toast.makeText(
                                        context,
                                        "Place already added to this location",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return false


                                }
                            }
                        }
                        val lon = clickedPoint.longitude.toString()
                        val lati = clickedPoint.latitude.toString()
                        locationViewModel.setLocation(lon, lati)
                        findNavController().popBackStack()
                        return true

                    }
                    else
                    {
                        return false
                    }
                }
                else {
                    Toast.makeText(context,"Can't pick this place because you are not on that location",Toast.LENGTH_SHORT).show()
                    return false

                }
            }
            override fun longPressHelper(p: GeoPoint?): Boolean {
                return false
            }
        }

        var overlayEvents=MapEventsOverlay(recive)
        map.overlays.add(overlayEvents)

    }

    private fun markAllPlaces() {
        for (myPlace in placesArray) {
            if (myPlace != null && myPlace.latitude != null && myPlace.longitude != null) {
                val sPoint = GeoPoint(myPlace.latitude!!.toDouble(), myPlace.longitude!!.toDouble())
                sharedViewModel.addOne(Coordinates(myPlace.latitude!!.toDouble(), myPlace.longitude!!.toDouble(), myPlace.name.toString()))
                val marker = Marker(map)
                marker.position = sPoint
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                marker.title = myPlace.name
                marker.subDescription = "Place added by: ${myPlace.author}<br>Type: ${myPlace.type}<br>Grade: ${myPlace.grade}<br>"
                map.overlays.add(marker)
                map.invalidate()
            } else {
                Log.e(TAG, "Latitude or longitude is null for place: $myPlace")
            }
        }
    }
    private fun markOthersPlaces() {
        if (placesArray.isNullOrEmpty()) {
            // Dodajte kod za upravljanje situacijom kada placesArray nije inicijalizovan ili je prazan
            return
        }
        for (myPlace in placesArray) {
            if (myPlace.author != sharedViewModel.name && myPlace.latitude != null && myPlace.longitude != null) {
                sharedViewModel.addOne(
                    Coordinates(
                        myPlace.latitude!!.toDouble(),
                        myPlace.longitude!!.toDouble(),
                        myPlace.name.toString()
                    )
                )
                val sPoint = GeoPoint(myPlace.latitude!!.toDouble(), myPlace.longitude!!.toDouble())
                val marker = Marker(map)
                marker.position = sPoint
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                marker.title = myPlace.name
                marker.subDescription = "Place added by: ${myPlace.author}<br>Type: ${myPlace.type}<br>Grade: ${myPlace.grade}<br>"
                map.overlays.add(marker)
                map.invalidate()
            }
        }
    }
    private fun allowClicksOnOthersLocations() {
        val myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), map)
        myLocationOverlay.enableMyLocation()
        map.overlays.add(myLocationOverlay)
        map.controller.setCenter(myLocationOverlay.myLocation)
        Toast.makeText(context,"Other users places showing",Toast.LENGTH_SHORT).show()
        var recive = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                val clickedPoint = p ?: return false
                var startPoint = GeoPoint(myLocationOverlay.myLocation.latitude, myLocationOverlay.myLocation.longitude)
                var endPoint = GeoPoint(clickedPoint.latitude, clickedPoint.longitude)
                if(startPoint.distanceToAsDouble(endPoint)<1000)
                {
                    var exists=false
                    var i:Int=0
                    for(placeN in othersPlaces)
                    {
                        if (placeN.latitude != null && placeN.longitude != null) {
                            var objPoint = GeoPoint(placeN.latitude!!.toDouble(), placeN.longitude!!.toDouble())
                            if(endPoint.distanceToAsDouble(objPoint)<=60)
                            {
                                exists=true
                                val lon = clickedPoint.longitude.toString()
                                val lati = clickedPoint.latitude.toString()
                                if(sharedViewModel.comment==true) {
                                    locationViewModel.setLocationAndNameComment(
                                        lon,
                                        lati,
                                        placeN.name.toString(),
                                        true
                                    )
                                }
                                else if(sharedViewModel.updateDelete==true)
                                {
                                    locationViewModel.setLocationAndName(lon,lati,placeN.name.toString(),true)
                                }
                                findNavController().popBackStack()
                                return true
                            }                        }



                    }
                    if(exists==false)
                    {
                        Toast.makeText(context,"No objects on this location",Toast.LENGTH_SHORT).show()
                        return false

                    }
                    return false

                }
                else
                {
                    Toast.makeText(context,"Can't comment objects further than 1km",Toast.LENGTH_SHORT).show()
                    return false
                }

            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                return false
            }


        }
        var overlayEvents=MapEventsOverlay(recive)
        map.overlays.add(overlayEvents)

    }
    private fun markObjectOnMap()
    {
        var startPointN= GeoPoint(sharedViewModel.latitude.toDouble(),sharedViewModel.longitude.toDouble())
        val marker = Marker(map)
        marker.position = startPointN
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM) // Postavljanje tačke spajanja markera
        marker.title = sharedViewModel.pickedPlace
        marker.subDescription = "Place added by:${sharedViewModel.name}"
        map.overlays.add(marker)
        map.invalidate()

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

        //Kad izabere samo mapu
        if(locationViewModel.viewObject==true)
        {
            setMyLocationOverlay()
            markAllPlaces()

        }
        else {
            //Za dodavanje objekata
            if (locationViewModel.addObject==true)
            {

                setMyLocationOverlay()
                markAllPlaces()

                setOnMapClickOverlay()


            }
            //Kad izabere Neki objekat iz liste objekata
            else if(locationViewModel.oneObject==true)
            {
                markAllPlaces()
                setOnMapClickOverlay()

                setMyLocationOverlay()


            }
            else if(locationViewModel.commentObject==true)
            {
                markOthersPlaces()
                allowClicksOnOthersLocations()
                setMyLocationOverlay()
            }

            else
            {
                setMyLocationOverlay()
            }
        }
        map.controller.animateTo(startPoint)

    }


    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }


}