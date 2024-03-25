package elfak.mosis.cityexplorer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import elfak.mosis.cityexplorer.model.UserViewModel


class InfoFragment : Fragment() {
    lateinit var name: TextView
    lateinit var picture: ImageView
    lateinit var lastname:TextView
    lateinit var phoneNumber:TextView
    lateinit var email:TextView
    lateinit var points:TextView
    private val user: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_info,container,false)
        //INICIJALIZACIJA PROMENLJIVIH
        picture=view.findViewById(R.id.picInfo)
        name=view.findViewById(R.id.infoName)
        lastname=view.findViewById(R.id.infoLastname)
        phoneNumber=view.findViewById(R.id.infoPhonenumber)
        email=view.findViewById(R.id.infoUsername)
        points=view.findViewById(R.id.infoPoints)
        if(user.user.imageUrl!="")
        {
            Glide.with(requireContext())
                .load(user.user.imageUrl)
                .into(picture)
        }
        name.text=user.user.firstName
        lastname.text=user.user.lastName
        phoneNumber.text=user.user.phoneNumber.toString()
        email.text=user.user.username
        points.text=user.user.points.toString()


        return view
    }





}