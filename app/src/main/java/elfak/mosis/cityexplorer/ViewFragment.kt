package elfak.mosis.cityexplorer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import elfak.mosis.cityexplorer.databinding.FragmentViewBinding
import elfak.mosis.cityexplorer.model.MyPlacesViewModel


class ViewFragment : Fragment() {
    private val MyPlacesViewModel: MyPlacesViewModel by activityViewModels()
    private var _binding: FragmentViewBinding? = null
    private val binding get() = _binding!!



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewmyplaceNameText.text = MyPlacesViewModel.selected?.name
        binding.viewmyplaceDescText.text = MyPlacesViewModel.selected?.description
        binding.viewmyplaceFinishedButton.setOnClickListener{
            MyPlacesViewModel.selected = null
            findNavController().navigate(R.id.action_ViewFragment_to_ListFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        MyPlacesViewModel.selected = null
    }

}