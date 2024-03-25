package elfak.mosis.cityexplorer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import elfak.mosis.cityexplorer.model.LocationViewModel
import elfak.mosis.cityexplorer.model.UserViewModel


class CommentGradeFragment : Fragment() {
    private  lateinit var back: Button
    private lateinit var send: Button
    private lateinit var allComments: Button
    private lateinit var openMap:Button
    private lateinit var comment: EditText
    private lateinit var grade:EditText
    private lateinit var progress:ProgressBar
    private lateinit var progressDatabase:ProgressBar
    private lateinit var progressPicture: ProgressBar
    val locationViewModel: LocationViewModel by activityViewModels()
    val sharedViewModel: UserViewModel by activityViewModels()
    private lateinit var type: TextView
    private lateinit var typeGrade:TextView
    private lateinit var author:TextView
    private lateinit var latitude:TextView
    private lateinit var longitude:TextView
    private lateinit var name:TextView
    private lateinit var picture: ImageView
    private lateinit var textType:TextView
    private lateinit var textGrade:TextView
    private lateinit var textAuthor:TextView
    private lateinit var buttonInfo:Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_comment_grade, container, false)
        //INICIJALIZACIJA PROMENLJIVIH KOMPONENTAMA
        back = view.findViewById(R.id.komentarNazad)
        send = view.findViewById(R.id.komentarPosalji)
        allComments = view.findViewById(R.id.buttonSviKomentari)
        comment = view.findViewById(R.id.editKomentar)
        grade = view.findViewById(R.id.komentarOcena)
        latitude = view.findViewById(R.id.komentarLatituda)
        longitude = view.findViewById(R.id.konetarLongituda)
        name = view.findViewById(R.id.komentarNaziv)
        openMap = view.findViewById(R.id.buttonOpenMap)
        progress = view.findViewById(R.id.upisKomentaraPB)
        progressDatabase = view.findViewById(R.id.komentarProgres)
        buttonInfo = view.findViewById(R.id.buttonInfo)
        progressPicture = view.findViewById(R.id.slikaProgres)
        val bazaObserver= Observer<Boolean>{ newValue->
            buttonInfo.isEnabled=newValue
            allComments.isEnabled=newValue
        }
        locationViewModel.database.observe(viewLifecycleOwner,bazaObserver)
        val lonObserver= Observer<String>{newValue->
            longitude.text=newValue.toString()
            sharedViewModel.longitude=longitude.text.toString()


        }
        locationViewModel.longitudeComment.observe(viewLifecycleOwner,lonObserver)
        val latiObserver= Observer<String>{newValue->
            latitude.setText(newValue.toString())
            sharedViewModel.latitude=latitude.text.toString()

        }
        locationViewModel.latitudeComment.observe(viewLifecycleOwner,latiObserver)
        val nameObserver= Observer<String> {newValue->
            name.text=newValue.toString()
            //locationViewModel.nazivMesta=newValue.toString()
            locationViewModel.placeName.observe(viewLifecycleOwner,nameObserver)
            //INICIJALIZACIJA KOMPONENATA ZA PRIKAZ IZ BAZE
            type=view.findViewById(R.id.commentType)
            typeGrade.visibility=View.GONE
            author=view.findViewById(R.id.commentAuthor)
            author.visibility=View.GONE
            picture=view.findViewById(R.id.commentPic)
            picture.visibility=View.GONE
            textType=view.findViewById(R.id.textType)
            textType.visibility=View.GONE
            textGrade=view.findViewById(R.id.textGrade)
            textGrade.visibility=View.GONE
            textAuthor=view.findViewById(R.id.textAuthor)
            textAuthor.visibility=View.GONE

            DataBase.databasePlaces.child(name.text.toString()).get().addOnCompleteListener{task->
                if(task.isSuccessful==true)
                {
                    var snapShot=task.result
                    if(snapShot.exists()==false)
                    {
                        name.text=""
                        latitude.text=""
                        longitude.text=""
                        send.isEnabled=false
                        allComments.isEnabled=false
                        buttonInfo.isEnabled=false

                    }
                }
            }
            openMap.setOnClickListener{
                locationViewModel.addObject=false
                locationViewModel.viewObject=false
                locationViewModel.oneObject=false
                locationViewModel.commentObject=true
                findNavController().navigate(R.id.action_CommentGradeFragment_to_MapFragment)

            }
            back.setOnClickListener{
                findNavController().navigate(R.id.action_CommentGradeFragment_to_HomeFragment)
            }

            buttonInfo.setOnClickListener{
                try {
                    progressDatabase.visibility=View.VISIBLE
                    DataBase.databasePlaces.child(name.text.toString().replace(".", "").replace("#", "")
                        .replace("$", "").replace("[", "").replace("]", "")).get().addOnSuccessListener {dataShot->
                        progressDatabase.visibility=View.GONE

                        if(dataShot.exists())
                        {
                            progressPicture.visibility=View.VISIBLE
                            val imageName=dataShot.child("img").value.toString()
                            if(imageName!="")
                            {
                                Glide.with(requireContext())
                                    .load(imageName)
                                    .into(picture)
                                progressPicture.visibility=View.GONE
                                picture.visibility=View.VISIBLE
                            }

                            type.text=dataShot.child("teren").value.toString()
                            type.visibility=View.VISIBLE
                            textType.visibility=View.VISIBLE
                            typeGrade.text=dataShot.child("ocena").value.toString()
                            typeGrade.visibility=View.VISIBLE
                            textGrade.visibility=View.VISIBLE
                            author.text=dataShot.child("autor").value.toString()
                            author.visibility=View.VISIBLE
                            textAuthor.visibility=View.VISIBLE

                        }

                    }.addOnFailureListener {exception->
                        progressDatabase.visibility=View.GONE
                        Toast.makeText(context,exception.toString(),Toast.LENGTH_LONG).show()

                    }

                }
                catch (excption:Exception)
                {
                    Toast.makeText(context,excption.toString(),Toast.LENGTH_LONG).show()

                }


            }
    }


}