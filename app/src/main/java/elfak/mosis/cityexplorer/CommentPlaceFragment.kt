package elfak.mosis.cityexplorer

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import elfak.mosis.cityexplorer.data.UserData
import elfak.mosis.cityexplorer.model.LocationViewModel
import elfak.mosis.cityexplorer.model.UserViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class CommentPlaceFragment : Fragment() {

    val sharedViewModel: UserViewModel by activityViewModels()
    var commentsArray:ArrayList<Comments> = ArrayList()
    private var personArray= ArrayList<UserComment>()
    private val locationViewModel: LocationViewModel by activityViewModels()
    private lateinit var main: LinearLayout

    

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_comment_place, container, false)
        main=view.findViewById<LinearLayout>(R.id.main)
        drawCommentForm()
        val nizObserver= Observer<ArrayList<Comments>>{ newValue->
            commentsArray=newValue

            drawCommentForm()
        }
        sharedViewModel.comments.observe(viewLifecycleOwner,nizObserver)
        val personArrayObserver=Observer<ArrayList<UserComment>>{ newValue->
            personArray=newValue
            drawCommentForm()


        }
        sharedViewModel.othersSupported.observe(viewLifecycleOwner,personArrayObserver)




        return view
    }
    private fun drawCommentForm()
    {
        main.removeAllViews()

        var howMany=0
        val layout=LinearLayout(context)
        main.addView(layout)
        for(member in commentsArray)
        {
            if(member.place.toString()==locationViewModel.placeName.value.toString())
            {
                howMany++
                layout.orientation=LinearLayout.VERTICAL
                val layoutParamsLayout = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layout.layoutParams= layoutParamsLayout
                layout.layoutParams = layoutParamsLayout
                var authorText=TextView(context)
                authorText.layoutParams=LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                authorText.text="Author"
                authorText.textSize=25f
                authorText.setTypeface(null, Typeface.BOLD)
                var authorN= TextView(context)
                authorN.layoutParams=LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                authorN.text=member.author
                authorN.textSize=25f
                var gradeText=TextView(context)
                gradeText.layoutParams=LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                gradeText.text="Grade"
                gradeText.textSize=25f
                gradeText.setTypeface(null,Typeface.BOLD)
                var gradeN=TextView(context)
                gradeN.layoutParams=LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                gradeN.text=member.grade.toString()
                gradeN.textSize=25f
                var commentText=TextView(context)
                commentText.layoutParams=LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                commentText.text="Comment"
                commentText.textSize=25f
                commentText.setTypeface(null,Typeface.BOLD)
                var commentN=TextView(context)
                var layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                commentN.layoutParams=layoutParams
                commentN.text=member.comment.toString()
                commentN.textSize=20f
                var dateTimeN=TextView(context)
                dateTimeN.layoutParams=LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                dateTimeN.text=member.date+" u "+member.time
                dateTimeN.textSize=20f
                var separator= Button(context)
                var layoutParamsDugme=LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT

                )
                var horizontal=LinearLayout(context)
                horizontal.layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)
                horizontal.orientation=LinearLayout.HORIZONTAL
                var positiveN=Button(context)
                var negativeN=Button(context)
                var layoutPara1=LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)
                var layoutPara2=LinearLayout.LayoutParams(98.dpToPx(),68.dpToPx())
                positiveN.layoutParams=layoutPara2
                negativeN.layoutParams=layoutPara2
                positiveN.text="+"
                var positiveText=TextView(context)
                positiveText.textSize=30f
                positiveText.text=member.positive.toString()
                layoutPara1.setMargins(0, 0, 128.dpToPx(), 0)
                positiveText.layoutParams=layoutPara1
                positiveN.setPadding(16.dpToPx(),16.dpToPx(),16.dpToPx(),16.dpToPx())
                negativeN.setPadding(16.dpToPx(),16.dpToPx(),16.dpToPx(),16.dpToPx())
                positiveN.textSize=30f
                negativeN.text="-"
                negativeN.textSize=30f
                var negativeText=TextView(context)
                negativeText.layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)
                negativeText.textSize=30f
                negativeText.text=member.negative.toString()
                positiveN.setTextColor(resources.getColor(R.color.green))
                negativeN.setTextColor(resources.getColor(R.color.red))
                positiveText.setTextColor(resources.getColor(R.color.green))
                negativeText.setTextColor(resources.getColor(R.color.red))
                horizontal.addView(positiveN)
                horizontal.addView(positiveText)
                horizontal.addView(negativeText)
                horizontal.addView(negativeN)
                separator.layoutParams=layoutParamsDugme
                separator.text=""
                positiveN.hint=member.id
                for(check in personArray)
                {
                    if(check.idUser==sharedViewModel.name&&check.idComment==member.id&&check.supported==true)
                    {
                        positiveN.setBackgroundColor(resources.getColor(R.color.lightGreen))

                    }
                    else if(check.idUser==sharedViewModel.name&&check.idComment==member.id&&check.supported==false)
                    {
                        negativeN.setBackgroundColor(resources.getColor(R.color.lightRed))

                    }
                }
                positiveN.setOnClickListener{

                    DataBase.databaseComments.child(positiveN.hint.toString()).get().addOnSuccessListener { snapshot->
                        if(snapshot.exists())
                        {
                            var commentDatabase=Comments(snapshot.child("id").value.toString(),snapshot.child("author").value.toString(),snapshot.child("place").value.toString(),snapshot.child("grade").value.toString().toInt(),snapshot.child("comment").value.toString(),snapshot.child("date").value.toString(),snapshot.child("time").value.toString(),snapshot.child("positive").value.toString().toInt(),snapshot.child("negative").value.toString().toInt())
                            var neutral=false
                            var changes=false
                            var idUser=""
                            for(supportedN in personArray)
                            {
                                //VEC JE TAJ KORISNIK ZA TAJ KOMENTAR STAVIO POZITIVAN
                                if(supportedN.idComment==positiveN.hint&&supportedN.idUser==sharedViewModel.name&&supportedN.supported==true)
                                {
                                    neutral=true
                                    changes=false
                                    idUser=supportedN.id
                                    break



                                }
                                //VEC JE TAJ KORISNIK ZA TAJ KOMENTAR STAVIO NEGATIVAN
                                else if(supportedN.idUser==sharedViewModel.name&&supportedN.idUser==positiveN.hint&&supportedN.supported==false)
                                {
                                    //ODUZMI PRETHODNO DODAT NEGATIVNI I OBRISI GA IZ BAZE TAJ 1 TO 1 VEZU
                                    neutral=false
                                    changes=true
                                    idUser=supportedN.id
                                    break



                                }


                            }
                            if(neutral==false&&changes==false)
                            {
                                commentDatabase.positive = commentDatabase.positive.plus(1)
                                DataBase.databaseComments.child(commentDatabase.id)
                                    .setValue(commentDatabase).addOnSuccessListener {
                                        var id = System.currentTimeMillis().toString()
                                        var pozitivan = UserComment(
                                            id,
                                            sharedViewModel.name,
                                            positiveN.hint.toString(),
                                            true
                                        )
                                        DataBase.dataBaseOneToOne.child(id).setValue(pozitivan)
                                            .addOnSuccessListener {
                                                DataBase.databaseUsers.child(
                                                    sharedViewModel.name.replace(".", "")
                                                        .replace("#", "")
                                                        .replace("$", "").replace("[", "")
                                                        .replace("]", "")
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
                                                            snapshotU.child("points").value.toString()
                                                                .toIntOrNull()
                                                        )
                                                        sharedViewModel.user.points =
                                                            sharedViewModel.user.points?.plus(2)
                                                        DataBase.databaseUsers.child(
                                                            sharedViewModel.name.replace(".", "")
                                                                .replace("#", "")
                                                                .replace("$", "").replace("[", "")
                                                                .replace("]", "")
                                                        ).setValue(sharedViewModel.user)
                                                            .addOnSuccessListener {
                                                                Toast.makeText(
                                                                    context,
                                                                    "You earned 2 points ",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()

                                                            }

                                                    }
                                                }
                                            }
                                    }


                            }
                            else if(neutral==true)
                            {
                                commentDatabase.positive=commentDatabase.positive.minus(1)
                                //AKO JE PRETHODNO TAJ KOMENTAR PODRZAO INDIKATOR JE TRUE

                                DataBase.dataBaseOneToOne.child(idUser).removeValue().addOnSuccessListener {
                                    DataBase.databaseComments.child(commentDatabase.id).setValue(commentDatabase).addOnSuccessListener {
                                        DataBase.databaseUsers.child(
                                            sharedViewModel.name.replace(".", "")
                                                .replace("#", "")
                                                .replace("$", "").replace("[", "")
                                                .replace("]", "")
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
                                                    snapshotU.child("points").value.toString()
                                                        .toIntOrNull()
                                                )
                                                sharedViewModel.user.points =
                                                    sharedViewModel.user.points?.minus(2)
                                                DataBase.databaseUsers.child(
                                                    sharedViewModel.name.replace(".", "")
                                                        .replace("#", "")
                                                        .replace("$", "").replace("[", "")
                                                        .replace("]", "")
                                                ).setValue(sharedViewModel.user)
                                                    .addOnSuccessListener {
                                                        Toast.makeText(
                                                            context,
                                                            "You lost 2 points",
                                                            Toast.LENGTH_SHORT
                                                        ).show()

                                                    }

                                            }
                                        }
                                    }


                                }

                            }
                            else if(changes==true)
                            {
                                commentDatabase.negative=commentDatabase.negative.minus(1)
                                commentDatabase.positive=commentDatabase.positive.plus(1)
                                DataBase.dataBaseOneToOne.child(idUser).removeValue().addOnSuccessListener {DataBase.databaseComments.child(commentDatabase.id).setValue(commentDatabase).addOnSuccessListener {
                                    var id = System.currentTimeMillis().toString()
                                    var pozitivan = UserComment(
                                        id,
                                        sharedViewModel.name,
                                        positiveN.hint.toString(),
                                        true
                                    )
                                    DataBase.dataBaseOneToOne.child(id).setValue(pozitivan)
                                        .addOnSuccessListener {



                                        }

                                } }

                            }

                        }

                    }
                }
                negativeN.hint=member.id
                negativeN.setOnClickListener{

                    DataBase.databaseComments.child(negativeN.hint.toString()).get().addOnSuccessListener { snapshot->
                        if(snapshot.exists())
                        {
                            var neutral2=false
                            var change2=false
                            var commentId=""
                            var commentDatabase=Comments(snapshot.child("id").value.toString(),snapshot.child("author").value.toString(),snapshot.child("place").value.toString(),snapshot.child("grade").value.toString().toInt(),snapshot.child("comment").value.toString(),snapshot.child("date").value.toString(),snapshot.child("time").value.toString(),snapshot.child("positive").value.toString().toInt(),snapshot.child("negative").value.toString().toInt())
                            for(supported2 in personArray)
                            {
                                //VEC JE TAJ KORISNIK ZA TAJ KOMENTAR STAVIO NEGATIVAN
                                if(supported2.idComment==positiveN.hint&&supported2.idUser==sharedViewModel.name&&supported2.supported==false)
                                {
                                    neutral2=true
                                    change2=false
                                    commentId=supported2.id
                                    break



                                }
                                else if(supported2.idUser==sharedViewModel.name&&supported2.idComment==negativeN.hint&&supported2.supported==true)
                                {
                                    neutral2=false
                                    change2=true
                                    commentId=supported2.id
                                    break

                                }


                            }
                            if(neutral2==false&&change2==false) {
                                commentDatabase.negative = commentDatabase.negative.plus(1)

                                DataBase.databaseComments.child(commentDatabase.id)
                                    .setValue(commentDatabase).addOnSuccessListener {
                                        var id = System.currentTimeMillis().toString()
                                        var negative = UserComment(
                                            id,
                                            sharedViewModel.name,
                                            negativeN.hint.toString(),
                                            false
                                        )
                                        DataBase.dataBaseOneToOne.child(id).setValue(negative)
                                            .addOnSuccessListener {
                                                DataBase.databaseUsers.child(
                                                    sharedViewModel.name.replace(".", "")
                                                        .replace("#", "")
                                                        .replace("$", "").replace("[", "")
                                                        .replace("]", "")
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
                                                            snapshotU.child("points").value.toString()
                                                                .toIntOrNull()
                                                        )
                                                        sharedViewModel.user.points =
                                                            sharedViewModel.user.points?.plus(2)
                                                        DataBase.databaseUsers.child(
                                                            sharedViewModel.name.replace(".", "")
                                                                .replace("#", "")
                                                                .replace("$", "").replace("[", "")
                                                                .replace("]", "")
                                                        ).setValue(sharedViewModel.user)
                                                            .addOnSuccessListener {
                                                                Toast.makeText(
                                                                    context,
                                                                    "You earned 2 points",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()

                                                            }

                                                    }
                                                }
                                            }


                                    }
                            }
                            else if(neutral2==true)
                            {
                                commentDatabase.negative=commentDatabase.negative.minus(1)
                                DataBase.dataBaseOneToOne.child(commentId).removeValue().addOnSuccessListener{
                                    DataBase.databaseComments.child(commentDatabase.id).setValue(commentDatabase).addOnSuccessListener {
                                        DataBase.databaseUsers.child(
                                            sharedViewModel.name.replace(".", "")
                                                .replace("#", "")
                                                .replace("$", "").replace("[", "")
                                                .replace("]", "")
                                        ).get().addOnSuccessListener { snapshotU ->
                                            if (snapshotU.exists()) {
                                                sharedViewModel.user = UserData(
                                                    snapshotU.child("username").value.toString(),
                                                    snapshotU.child("password").value.toString(),
                                                    snapshotU.child("firstName").value.toString(),
                                                    snapshotU.child("lastName").value.toString(),
                                                    snapshotU.child("phoneNumber").value.toString(),
                                                    snapshotU.child("imageUr").value.toString(),
                                                    ArrayList(),
                                                    snapshotU.child("points").value.toString()
                                                        .toIntOrNull()
                                                )
                                                sharedViewModel.user.points =
                                                    sharedViewModel.user.points?.minus(2)
                                                DataBase.databaseUsers.child(
                                                    sharedViewModel.name.replace(".", "")
                                                        .replace("#", "")
                                                        .replace("$", "").replace("[", "")
                                                        .replace("]", "")
                                                ).setValue(sharedViewModel.user)
                                                    .addOnSuccessListener {
                                                        Toast.makeText(
                                                            context,
                                                            "You lost 2 points",
                                                            Toast.LENGTH_SHORT
                                                        ).show()

                                                    }

                                            }
                                        }
                                    }

                                }

                            }
                            else if(change2==true)
                            {
                                commentDatabase.positive=commentDatabase.positive.minus(1)
                                commentDatabase.negative=commentDatabase.negative.plus(1)
                                DataBase.dataBaseOneToOne.child(commentId).removeValue().addOnSuccessListener {
                                    var id=System.currentTimeMillis()
                                    var new=UserComment(id.toString(),sharedViewModel.name,negativeN.hint.toString(),false)
                                    DataBase.dataBaseOneToOne.child(id.toString()).setValue(new).addOnSuccessListener { DataBase.databaseComments.child(commentDatabase.id).setValue(commentDatabase).addOnSuccessListener {

                                    } }
                                }




                            }

                        }

                    }

                }
                layout.addView(authorText)
                layout.addView(authorN)
                layout.addView(gradeText)
                layout.addView(gradeN)
                layout.addView(commentText)
                layout.addView(commentN)
                layout.addView(dateTimeN)
                layout.addView(horizontal)
                layout.addView(separator)
                layout.setBackgroundColor(Color.parseColor("#C5BCC6"))



            }


        }
        if(howMany==0)
        {
            var message=TextView(context)
            message.layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
            message.textAlignment=TextView.TEXT_ALIGNMENT_CENTER
            message.textSize=40f
            message.gravity=LinearLayout.TEXT_ALIGNMENT_CENTER
            message.text="No comments for place ${locationViewModel.placeName.value.toString()} "
            main.addView(message)
        }

    }
    private fun Int.dpToPx(): Int {
        val density = resources.displayMetrics.density
        return (this * density).toInt()
    }


}