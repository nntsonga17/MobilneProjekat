package elfak.mosis.cityexplorer

import android.content.pm.PackageManager
import org.osmdroid.config.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import elfak.mosis.cityexplorer.data.MyPlaces
import elfak.mosis.cityexplorer.model.LocationViewModel
import elfak.mosis.cityexplorer.model.UserViewModel
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class SearchObjectFragment : Fragment() {

    private  val sharedViewModel: UserViewModel by activityViewModels()
    private val locationViewModel: LocationViewModel by activityViewModels()
    private lateinit var map: MapView

    lateinit var placeName: EditText
    lateinit var author:EditText
    lateinit var commentPlace: EditText
    lateinit var gradePlace: EditText
    lateinit var progress: ProgressBar
    lateinit var search: Button
    lateinit var place: MyPlaces
    ////////////////////////////////////////
    lateinit var type: EditText
    private lateinit var latitude:EditText
    private lateinit var longitude:EditText
    private lateinit var dateP:EditText
    private lateinit var dateI:EditText
    private lateinit var tableLayout: TableLayout
    lateinit var radius: EditText
    ////////////////////////////////////////////
    var markerPointList=ArrayList<Marker>()
    private var placeArray:ArrayList<MyPlaces> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search_object, container, false)
        placeName=view.findViewById(R.id.FilterPlaceName)
        commentPlace=view.findViewById(R.id.FilterComment)
        gradePlace=view.findViewById(R.id.FilterGrade)
        author=view.findViewById(R.id.FilterAuthor)
        radius=view.findViewById(R.id.FilterRadius)
        search=view.findViewById(R.id.buttonFilterSearch)
        type=view.findViewById(R.id.FilterType)
        var filteredArrayPlacesPom= ArrayList<MyPlaces> ()
        latitude=view.findViewById(R.id.filterLatitude)
        longitude=view.findViewById(R.id.filterLongitude)
        dateP=view.findViewById(R.id.FilterDate)
        dateI=view.findViewById(R.id.FilterDateI)

        val nizObserver= Observer<ArrayList<MyPlaces>>{ newValue->
            placeArray=newValue


        }
        sharedViewModel.myPlaces.observe(viewLifecycleOwner,nizObserver)

        search.setOnClickListener {

            tableLayout.visibility = View.VISIBLE
            tableLayout.removeAllViews()
            val tableRow = TableRow(context)
            tableRow.setBackgroundColor(Color.parseColor("#51B435"))
            tableRow.setPadding(10.dpToPx(), 10.dpToPx(), 10.dpToPx(), 10.dpToPx())
            tableRow.gravity = Gravity.CENTER

            // Lista sa sadržajem za TextView elemente
            val labels = listOf(
                "Place name", "Author", "Description", "Grade",
                "Latitude", "Longitude", "Type", "Adding place date",
                "Last comment date","Update","Commentary"
            )

            // Dodavanje TextView elemenata u TableRow
            for (label in labels) {
                val textView = TextView(context)
                textView.layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                )
                textView.text = label
                textView.textSize = 20f
                textView.setTypeface(null, Typeface.BOLD)
                textView.setPadding(0, 0, 30.dpToPx(), 0)
                textView.gravity = Gravity.CENTER
                tableRow.addView(textView)
            }

            // Dodavanje TableRow u TableLayout

            tableLayout.addView(tableRow)
            filteredArrayPlacesPom=placeArray
            if(author.text.toString().isNotEmpty())
            {
                var pom=ArrayList<MyPlaces>()
                for(place in filteredArrayPlacesPom)
                {
                    if(place.author==author.text.toString())
                    {
                        pom.add(place)
                    }
                }
                filteredArrayPlacesPom=pom
            }
            if(placeName.text.toString().isNotEmpty())
            {
                filteredArrayPlacesPom=filteredArrayPlacesPom.filter {
                        place->
                    place.name==placeName.text.toString()
                }as ArrayList<MyPlaces>
            }
            if(commentPlace.text.toString().isNotEmpty())
            {
                filteredArrayPlacesPom=filteredArrayPlacesPom.filter {
                        place-> place.comment == commentPlace.text.toString()


                }as ArrayList<MyPlaces>
            }
            if(gradePlace.text.toString().isNotEmpty())
            {
                filteredArrayPlacesPom=filteredArrayPlacesPom.filter {
                        place->
                    place.grade==gradePlace.text.toString()

                }as ArrayList<MyPlaces>
            }
            if(latitude.text.toString().isNotEmpty())
            {
                filteredArrayPlacesPom=filteredArrayPlacesPom.filter {
                        place->
                    place.latitude==latitude.text.toString()
                }as ArrayList<MyPlaces>
            }
            if(longitude.text.toString().isNotEmpty())
            {
                filteredArrayPlacesPom=filteredArrayPlacesPom.filter {
                        place->
                    place.longitude==longitude.text.toString()
                }as ArrayList<MyPlaces>
            }
            if(type.text.toString().isNotEmpty())
            {
                filteredArrayPlacesPom=filteredArrayPlacesPom.filter {
                      place->
                    place.type==type.text.toString()
                }as ArrayList<MyPlaces>
            }
            if(dateP.text.toString().isNotEmpty())
            {
                filteredArrayPlacesPom=filteredArrayPlacesPom.filter {
                        place->
                    place.datetime==dateP.text.toString()
                }as ArrayList<MyPlaces>
            }
            if(dateI.text.toString().isNotEmpty())
            {
                filteredArrayPlacesPom=filteredArrayPlacesPom.filter {
                        place->
                    place.interactionDate==dateI.text.toString()
                }as ArrayList<MyPlaces>
            }
            sharedViewModel.setFilteredPlaces(filteredArrayPlacesPom)
            setUpMap()
            if(radius.text.toString().isEmpty()) {

                for (placeN in sharedViewModel.getFilteredPlaces()) {
                    val row = TableRow(context) // Kreiranje TableRow-a
                    val rowParams = TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT
                    )
                    row.layoutParams = rowParams
                    row.setBackgroundColor(Color.parseColor("#F0F7F7"))
                    row.setPadding(5.dpToPx(), 5.dpToPx(), 5.dpToPx(), 5.dpToPx())

                    val textParams = TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT
                    )
                    textParams.marginEnd = 30.dpToPx()
                    var update=Button(context)
                    var comment=Button(context)
                    update.text="Update"
                    comment.text="Comment"

                    val textArray = arrayOf(
                        placeN.name,
                        placeN.author,
                        placeN.comment,
                        placeN.grade.toString(),
                        placeN.latitude,
                        placeN.longitude,
                        placeN.type,
                        placeN.datetime,
                        placeN.interactionDate,

                        )
                    for (textValue in textArray) {
                        val textView = TextView(context)
                        textView.layoutParams = textParams
                        textView.text = textValue
                        textView.textAlignment= LinearLayout.TEXT_ALIGNMENT_CENTER
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                        row.addView(textView)
                    }
                    row.addView(update)
                    row.addView(comment)
                    update.setOnClickListener{
                        if(sharedViewModel.user.username==placeN.author)
                        {
                            val x = map.overlays[0] as MyLocationNewOverlay
                            var start = GeoPoint(
                                x.lastFix.latitude,
                                x.lastFix.longitude
                            )
                            val end=GeoPoint(placeN.latitude.toString().toDouble(),placeN.longitude.toString().toDouble())
                            if(start.distanceToAsDouble(end)<1000) {
                                sharedViewModel.pickedPlace = placeN.name.toString()
                                sharedViewModel.updateDelete=true
                                sharedViewModel.comment=false
                                findNavController().navigate(R.id.action_SearchObjectFragment_to_DetailsFragment)
                            }
                            else
                            {
                                Toast.makeText(context,"You are not close enough to comment", Toast.LENGTH_SHORT).show()
                            }
                        }
                        else
                        {
                            Toast.makeText(context,"You have to add place to be able to update it",Toast.LENGTH_SHORT).show()
                        }
                    }
                    comment.setOnClickListener{
                        var ourLoc=GeoPoint(locationViewModel.getThisLat(),locationViewModel.getThisLon())
                        var clickedLoc=GeoPoint(placeN.latitude.toString().toDouble(),placeN.longitude.toString().toDouble())
                        if(sharedViewModel.user.username!=placeN.author&&ourLoc.distanceToAsDouble(clickedLoc)<1000) {
                            locationViewModel.setLocationAndName(
                                placeN.longitude.toString(),
                                placeN.latitude.toString(),
                                placeN.name.toString(),
                                true
                            )
                            sharedViewModel.comment=true
                            sharedViewModel.updateDelete=false
                            findNavController().navigate(R.id.action_SearchObjectFragment_to_CommentGradeFragment)
                        }
                        else if(sharedViewModel.user.username==placeN.author)
                        {
                            Toast.makeText(context,"You can't comment a place you added",Toast.LENGTH_SHORT).show()
                        }
                        else
                        {
                            Toast.makeText(context,"You are too far away from a place to comment",Toast.LENGTH_SHORT).show()
                        }
                    }

                    tableLayout.addView(row)


                }
            }
        }

        return view
    }
    fun Int.dpToPx(): Int {
        val density = resources.displayMetrics.density
        return (this * density).toInt()
    }
    //Uvek mapa od ovde krece
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ctx = activity?.applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        map = requireView().findViewById(R.id.map2)
        map.setMultiTouchControls(true)


        setupLocation()

    }
    private fun setupLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            val requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    setMyLocationOverlay()

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
    private fun setUpMap() {
        var start = GeoPoint(
            locationViewModel.getThisLat(),
            locationViewModel.getThisLon()
        )

        var startPointMap:GeoPoint= start

        map.controller.setZoom(14.0)
        map.invalidate()

        for (point in markerPointList) {
            map.overlays.remove(point)
        }
        var startPoint: GeoPoint = GeoPoint(43.158495, 22.585555)
        map.controller.setZoom(15.0)
        if (radius.text.toString().isEmpty()) {
            setMyLocationOverlay()
            for (placeN in sharedViewModel.getFilteredPlaces()) {
                var startPoint1 = GeoPoint(
                    placeN.latitude.toString().toDouble(),
                    placeN.longitude.toString().toDouble()
                )
                val marker = Marker(map)
                marker.position = startPoint1
                marker.setAnchor(
                    Marker.ANCHOR_CENTER,
                    Marker.ANCHOR_BOTTOM
                ) // Postavljanje tačke spajanja markera
                marker.title = placeN.name
                marker.subDescription = "Place added by:${placeN.author}"
                map.overlays.add(marker)
                markerPointList.add(marker)
                map.invalidate()

            }
        } else {
            var newFilter=ArrayList<MyPlaces>()
            for (point in markerPointList) {
                map.overlays.remove(point)
            }
            val myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), map)
            myLocationOverlay.enableMyLocation()
            map.overlays.add(myLocationOverlay)
            map.invalidate()
            map.controller.setCenter(myLocationOverlay.myLocation)
            val x = map.overlays[0] as MyLocationNewOverlay
            var start = GeoPoint(
                locationViewModel.getThisLat(),
                locationViewModel.getThisLon()
            )
            for (placeM in sharedViewModel.getFilteredPlaces()) {
                var endPoint = GeoPoint(
                    placeM.latitude.toString().toDouble(),
                    placeM.longitude.toString().toDouble()
                )
                if (start.distanceToAsDouble(endPoint)<=radius.text.toString().toDouble()) {
                    Toast.makeText(requireContext(), "Object ${placeM.name}", Toast.LENGTH_SHORT).show()
                    var startPoint = GeoPoint(
                        placeM.latitude.toString().toDouble(),
                        placeM.longitude.toString().toDouble()
                    )
                    val marker = Marker(map)
                    marker.position = startPoint
                    marker.setAnchor(
                        Marker.ANCHOR_CENTER,
                        Marker.ANCHOR_BOTTOM
                    ) // Postavljanje tačke spajanja markera
                    marker.title = placeM.name
                    marker.subDescription = "Place added by:${placeM.author}"
                    map.overlays.add(marker)
                    markerPointList.add(marker)
                    map.invalidate()
                    newFilter.add(placeM)

                }


            }
            sharedViewModel.setFilteredPlaces(newFilter)
            drawTable()

        }

        map.controller.animateTo(startPointMap)
    }

    private fun drawTable()
    {
        tableLayout.removeAllViews()
        val tableRow = TableRow(context)
        tableRow.setBackgroundColor(Color.parseColor("#51B435"))
        tableRow.setPadding(10.dpToPx(), 10.dpToPx(), 10.dpToPx(), 10.dpToPx())
        tableRow.gravity = Gravity.CENTER
        // Lista sa sadržajem za TextView elemente
        val labels = listOf(
            "Place name", "Author", "Description", "Grade",
            "Latitude", "Longitude", "Type", "Adding place date",
            "Last comment date","Update","Commentary"
        )

        // Dodavanje TextView elemenata u TableRow
        for (label in labels) {
            val textView = TextView(context)
            textView.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
            textView.text = label
            textView.textSize = 20f
            textView.setTypeface(null, Typeface.BOLD)
            textView.setPadding(0, 0, 30.dpToPx(), 0)
            textView.gravity = Gravity.CENTER
            tableRow.addView(textView)
        }

        // Dodavanje TableRow u TableLayout

        tableLayout.addView(tableRow)
        for (placeV in sharedViewModel.getFilteredPlaces()) {
            val row = TableRow(context) // Kreiranje TableRow-a
            val rowParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
            )
            row.layoutParams = rowParams
            row.setBackgroundColor(Color.parseColor("#F0F7F7"))
            row.setPadding(5.dpToPx(), 5.dpToPx(), 5.dpToPx(), 5.dpToPx())

            val textParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
            textParams.marginEnd = 30.dpToPx()

            val textArray = arrayOf(
                placeV.name,
                placeV.author,
                placeV.comment,
                placeV.grade.toString(),
                placeV.latitude,
                placeV.longitude,
                placeV.type,
                placeV.datetime,
                placeV.interactionDate


            )
            var update=Button(context)
            var comment=Button(context)
            update.text="Update"
            comment.text="Comment"

            for (textValue in textArray) {
                val textView = TextView(context)
                textView.layoutParams = textParams
                textView.text = textValue
                textView.textAlignment=LinearLayout.TEXT_ALIGNMENT_CENTER
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                row.addView(textView)
            }
            row.addView(update)
            row.addView(comment)
            update.setOnClickListener{
                if(sharedViewModel.user.username==placeV.author)
                {
                    sharedViewModel.pickedPlace=placeV.name.toString()
                    findNavController().navigate(R.id.action_SearchObjectFragment_to_DetailsFragment)
                }
                else
                {
                    Toast.makeText(context,"You first need to add place to update it",Toast.LENGTH_SHORT).show()
                }
            }
            comment.setOnClickListener{
                if(sharedViewModel.user.username!=placeV.author) {
                    locationViewModel.setLocationAndName(
                        placeV.longitude.toString(),
                        placeV.latitude.toString(),
                        placeV.name.toString(),
                        true
                    )
                    findNavController().navigate(R.id.action_SearchObjectFragment_to_CommentGradeFragment)
                }
                else
                {
                    Toast.makeText(context,"You can't comment a place you added",Toast.LENGTH_SHORT).show()
                }
            }
            tableLayout.addView(row)


        }

    }


}