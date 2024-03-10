package elfak.mosis.cityexplorer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import elfak.mosis.cityexplorer.model.MyPlacesViewModel


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"




/**
 * A simple [Fragment] subclass.
 * Use the [EditFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private val myPlacesViewModel: MyPlacesViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater){
        inflater.inflate(R.menu.menu_main, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {

            R.id.action_my_places_list -> {
                this.findNavController().navigate(R.id.action_EditFragment_to_ListFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.action_new_place)
        item.isVisible = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val addButton: Button = requireView().findViewById<Button>(R.id.editmyplace_finished_button)
        addButton.setOnClickListener{
            val editName: EditText = requireView().findViewById<EditText>(R.id.editmyplace_name_edit)
            val name: String = editName.text.toString()
            val editDesc: EditText = requireView().findViewById<EditText>(R.id.editmyplace_desc_edit)
            val desc: String = editDesc.text.toString()
            myPlacesViewModel.addPlace(name, desc)
            findNavController().navigate(R.id.action_EditFragment_to_ListFragment)
        }
        val cancelButton: Button = requireView().findViewById<Button>(R.id.editmyplace_cancel_button)
        cancelButton.setOnClickListener{
            findNavController().navigate(R.id.action_EditFragment_to_ListFragment)
        }
    }
}