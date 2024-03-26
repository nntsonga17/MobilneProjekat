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
import elfak.mosis.cityexplorer.data.MyPlaces
import elfak.mosis.cityexplorer.data.UserData
import elfak.mosis.cityexplorer.model.LocationViewModel
import elfak.mosis.cityexplorer.model.UserViewModel
import java.util.Calendar


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
        back = view.findViewById(R.id.commentBack)
        send = view.findViewById(R.id.commentSend)
        allComments = view.findViewById(R.id.buttonAllComments)
        comment = view.findViewById(R.id.editComment)
        grade = view.findViewById(R.id.commentGrade)
        latitude = view.findViewById(R.id.commentLatitude)
        longitude = view.findViewById(R.id.commentLongitude)
        name = view.findViewById(R.id.commentName)
        openMap = view.findViewById(R.id.buttonOpenMap)
        progress = view.findViewById(R.id.writeCommentsPB)
        progressDatabase = view.findViewById(R.id.commentProgress)
        buttonInfo = view.findViewById(R.id.buttonInfo)
        progressPicture = view.findViewById(R.id.picProgress)
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
        val nameObserver= Observer<String> { newValue ->
            name.text = newValue.toString()
            //locationViewModel.nazivMesta=newValue.toString()
        }
            locationViewModel.placeName.observe(viewLifecycleOwner, nameObserver )
            //INICIJALIZACIJA KOMPONENATA ZA PRIKAZ IZ BAZE
            type=view.findViewById(R.id.commentType)
            typeGrade.visibility=View.GONE
            author=view.findViewById(R.id.commentAuthor)
            author.visibility=View.GONE
            picture=view.findViewById(R.id.commentPic)
            picture.visibility=View.GONE
            textType=view.findViewById(R.id.TextType)
            textType.visibility=View.GONE
            textGrade=view.findViewById(R.id.TextGrade)
            textGrade.visibility=View.GONE
            textAuthor=view.findViewById(R.id.TextAuthor)
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

                            type.text=dataShot.child("type").value.toString()
                            type.visibility=View.VISIBLE
                            textType.visibility=View.VISIBLE
                            typeGrade.text=dataShot.child("grade").value.toString()
                            typeGrade.visibility=View.VISIBLE
                            textGrade.visibility=View.VISIBLE
                            author.text=dataShot.child("author").value.toString()
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
            send.setOnClickListener{
                var exists=true
                DataBase.databasePlaces.child(name.text.toString()).get().addOnCompleteListener{task->
                    if(task.isSuccessful==true)
                    {
                        var snapShot=task.result
                        if(snapShot.exists()==false)
                        {
                            exists=false
                        }
                    }
                }
                if(exists==true){
                    if(name.text.toString().isEmpty()||grade.text.toString().isEmpty()||grade.text.toString().toInt()>10||grade.text.toString().toInt()<1||comment.text.toString().isEmpty())
                    {
                        Toast.makeText(context,"All fields required or wrong grade",Toast.LENGTH_SHORT).show()

                    }
                    else
                    {
                        progress.visibility = View.VISIBLE
                        var id =
                            name.text.toString() + grade.text.toString() + comment.text.toString() + sharedViewModel.name.toString()
                        id = id.replace(".", "").replace("#", "").replace("$", "").replace("[", "")
                            .replace("]", "").replace("@", "")
                        var instance = Calendar.getInstance()
                        var month = instance.get(Calendar.MONTH).toInt() + 1
                        var date = instance.get(Calendar.DAY_OF_MONTH)
                            .toString() + "." + month.toString() + "." + instance.get(Calendar.YEAR)
                        var time = instance.get(Calendar.HOUR_OF_DAY).toString() + ":" + instance.get(
                            Calendar.MINUTE
                        )
                        var commentary: Comments = Comments(
                            id,
                            sharedViewModel.name,
                            name.text.toString(),
                            grade.text.toString().toInt(),
                            comment.text.toString(),
                            date,
                            time,
                            0,
                            0
                        )
                        DataBase.databaseComments.child(id).setValue(commentary).addOnCompleteListener {
                            Toast.makeText(context, "Comment added", Toast.LENGTH_SHORT)
                                .show()
                            progress.visibility = View.GONE
                            DataBase.databaseUsers.child(
                                sharedViewModel.name.replace(".", "").replace("#", "")
                                    .replace("$", "").replace("[", "").replace("]", "")
                            ).get().addOnSuccessListener { snapshotU ->
                                if (snapshotU.exists()) {
                                    sharedViewModel.user = UserData(
                                        snapshotU.child("username").value.toString(),
                                        snapshotU.child("password").value.toString(),
                                        snapshotU.child("firstName").value.toString(),
                                        snapshotU.child("lastName").value.toString(),
                                        snapshotU.child("phoneNumber").value.toString(),
                                        snapshotU.child("imageUrl").value.toString(),
                                        ArrayList(),
                                        snapshotU.child("points").value.toString().toIntOrNull()
                                    )
                                    sharedViewModel.user.points = sharedViewModel.user.points?.plus(5)
                                    DataBase.databaseUsers.child(
                                        sharedViewModel.name.replace(".", "").replace("#", "")
                                            .replace("$", "").replace("[", "").replace("]", "")
                                    ).setValue(sharedViewModel.user).addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "You earned 5 points",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        grade.text.clear()
                                        comment.text.clear()
                                        DataBase.databasePlaces.child(name.text.toString()).get()
                                            .addOnSuccessListener { sna ->
                                                if (sna.exists()) {
                                                    var place: MyPlaces = MyPlaces(
                                                        sna.child("name").value.toString(),
                                                        sna.child("comment").value.toString(),
                                                        sna.child("grade").value.toString(),
                                                        sna.child("author").value.toString(),
                                                        sna.child("longitude").value.toString(),
                                                        sna.child("latitude").value.toString(),
                                                        sna.child("type").value.toString(),
                                                        sna.child("img").value.toString(),
                                                        sna.child("dateTime").value.toString()
                                                    )
                                                    var exectMonth = instance.get(Calendar.MONTH) + 1
                                                    var date = instance.get(Calendar.DAY_OF_MONTH)
                                                        .toString() + "." + exectMonth.toString() + "." + instance.get(
                                                        Calendar.YEAR
                                                    )
                                                    var vreme = instance.get(Calendar.HOUR_OF_DAY)
                                                        .toString() + ":" + instance.get(Calendar.MINUTE)
                                                    var datumVreme = date + " u " + vreme
                                                    place.interactionDate = datumVreme
                                                    DataBase.databasePlaces.child(name.text.toString())
                                                        .setValue(place).addOnSuccessListener {

                                                        }
                                                }

                                            }
                                    }.addOnFailureListener {
                                        Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }.addOnFailureListener {
                                Toast.makeText(
                                    context,
                                    "Error",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }.addOnFailureListener {
                            Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
                        }
                    }

                }
                else{
                    Toast.makeText(context,"Chosen place is no longer in the database",Toast.LENGTH_LONG).show()

                }
            }
            allComments.setOnClickListener{
                sharedViewModel.pickedPlace=name.text.toString()
                findNavController().navigate(R.id.action_CommentGradeFragment_to_CommentPlaceFragment)
            }
            return view
        }

    }


