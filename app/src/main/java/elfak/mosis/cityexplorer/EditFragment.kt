package elfak.mosis.cityexplorer

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import elfak.mosis.cityexplorer.data.MyPlaces
import elfak.mosis.cityexplorer.model.LocationViewModel
import elfak.mosis.cityexplorer.model.MyPlacesViewModel


class EditFragment : Fragment() {
    private val locationViewModel: LocationViewModel by activityViewModels()
    private val myPlacesViewModel: MyPlacesViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit, container, false)
    }


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val editName: EditText = requireView().findViewById<EditText>(R.id.editmyplace_name_edit)
        val editDesc: EditText = requireView().findViewById<EditText>(R.id.editmyplace_desc_edit)
        val editLongitude: EditText = requireView().findViewById<EditText>(R.id.editmyplace_longitude_edit)
        val lonObserver = Observer<String> {newValue ->
            editLongitude.setText(newValue.toString())
        }
        locationViewModel.longitude.observe(viewLifecycleOwner, lonObserver)
        val editLatitude: EditText = requireView().findViewById<EditText>(R.id.editmyplace_latitude_edit)
        val latObserver = Observer<String> {newValue ->
            editLatitude.setText(newValue.toString())
        }
        locationViewModel.latitude.observe(viewLifecycleOwner,  latObserver)
        if (myPlacesViewModel.selected!=null){
            editName.setText(myPlacesViewModel.selected?.name)
            editDesc.setText(myPlacesViewModel.selected?.description)
        }
        val addButton: Button = requireView().findViewById<Button>(R.id.editmyplace_finished_button)
        addButton.isEnabled = false
        if (myPlacesViewModel.selected!=null){
            addButton.setText(R.string.editmyplace_save_label)
        }
        editName.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                addButton.isEnabled=(editName.text.length>0)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
        addButton.setOnClickListener{
            val name: String = editName.text.toString()
            val desc: String = editDesc.text.toString()
            val longitude: String = editLongitude.text.toString()
            val latitude: String = editLatitude.text.toString()
            if(myPlacesViewModel.selected!=null){
                myPlacesViewModel.selected?.name = name
                myPlacesViewModel.selected?.description = desc
                myPlacesViewModel.selected?.longitude = longitude
                myPlacesViewModel.selected?.latitude = latitude
            }
            else
                myPlacesViewModel.addPlace(MyPlaces(name, desc, longitude, latitude))
            myPlacesViewModel.selected = null
            locationViewModel.setLocation("", "")
            findNavController().popBackStack()
        }
        val cancelButton: Button = requireView().findViewById<Button>(R.id.editmyplace_cancel_button)
        cancelButton.setOnClickListener{
            myPlacesViewModel.selected = null
            locationViewModel.setLocation("", "")
            findNavController().popBackStack()
        }
        val setButton: Button = requireView().findViewById<Button>(R.id.editmyplace_location_button)
        setButton.setOnClickListener{
            locationViewModel.setLocation = true
            findNavController().navigate(R.id.action_EditFragment_to_MapFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}