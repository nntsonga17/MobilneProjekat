package elfak.mosis.cityexplorer

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.ProgressBar
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import elfak.mosis.cityexplorer.data.MyPlaces
import elfak.mosis.cityexplorer.model.UserViewModel


class MyPlacesFragment : Fragment() {

    private lateinit var MyOwnPlaces: ArrayList<MyPlaces>
    val sharedViewModel: UserViewModel by activityViewModels()
    lateinit var progressBar: ProgressBar
    lateinit var  listView: ListView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_places, container, false)
        MyOwnPlaces = ArrayList()
        progressBar = view.findViewById(R.id.loadMyPlaces)
        progressBar.visibility = View.VISIBLE
        listView= view.findViewById(R.id.placesView)

        DataBase.databasePlaces.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                MyOwnPlaces.clear()
                for (placeSnapshot in snapshot.children) {
                    val place = placeSnapshot.getValue(MyPlaces::class.java)
                    place?.let {
                        MyOwnPlaces.add(it)
                    }
                }

                updateListView()
            }

            override fun onCancelled(error: DatabaseError) {
                // Greška pri dohvaćanju podataka
                Log.e(TAG, "Error fetching Places data: ${error.message}")
            }
        })
        listView.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var myOwnPlace: String = p0?.adapter?.getItem(p2) as String
                sharedViewModel.pickedPlace=myOwnPlace
                sharedViewModel.updateDelete=true
                sharedViewModel.comment=false
                findNavController().navigate(R.id.action_MyPlacesFragment_to_DetailsFragment)
            }
        })

        return view
    }
    private fun updateListView() {
        val names = ArrayList<String>()

        for (name in MyOwnPlaces) {
            if (name.author == sharedViewModel.name) {
                names.add(name.name.toString())
            }
        }

        if (isAdded) {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, names)
            listView.adapter = adapter
            progressBar.visibility = View.GONE
        }
    }


}