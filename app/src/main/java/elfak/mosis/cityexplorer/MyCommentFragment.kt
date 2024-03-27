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
import elfak.mosis.cityexplorer.model.UserViewModel


class MyCommentFragment : Fragment() {
    val sharedViewModel:UserViewModel by activityViewModels()
    private lateinit var main: LinearLayout
    private var commentArray = ArrayList<Comments>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_comment, container, false)
        main=view.findViewById<LinearLayout>(R.id.basic)
        drawCommentForm()
        val arrayObserver= Observer<ArrayList<Comments>>{ newValue->
            commentArray=newValue

            drawCommentForm()
        }
        sharedViewModel.comments.observe(viewLifecycleOwner,arrayObserver)



        return view
    }
    private fun drawCommentForm()
    {
        main.removeAllViews()
        var homMany=0
        var layout=LinearLayout(context)
        main.addView(layout)
        for(clan in commentArray)
        {
            if(clan.author==sharedViewModel.name) {
                homMany++
                layout.orientation = LinearLayout.VERTICAL
                val layoutParamsLayout = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layout.layoutParams = layoutParamsLayout
                layout.layoutParams = layoutParamsLayout
                var placeText = TextView(context)
                placeText.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                placeText.text = "Place name"
                placeText.textSize = 25f
                placeText.setTypeface(null, Typeface.BOLD)

                var placeN = TextView(context)
                placeN.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                placeN.text = clan.place.toString()
                placeN.textSize = 25f


                var authorText = TextView(context)
                authorText.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                authorText.text = "Author"
                authorText.textSize = 25f
                authorText.setTypeface(null, Typeface.BOLD)
                var author = TextView(context)
                author.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                author.text = clan.author
                author.textSize = 25f
                var gradeText = TextView(context)
                gradeText.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                gradeText.text = "Grade"
                gradeText.textSize = 25f
                gradeText.setTypeface(null, Typeface.BOLD)
                var gradeN = TextView(context)
                gradeN.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                gradeN.text = clan.grade.toString()
                gradeN.textSize = 25f
                var commentText = TextView(context)
                commentText.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                commentText.text = "Comment"
                commentText.textSize = 25f
                commentText.setTypeface(null, Typeface.BOLD)
                var commentN = TextView(context)
                var layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                commentN.layoutParams = layoutParams
                commentN.text = clan.comment.toString()
                commentN.textSize = 20f
                var dateTimeN = TextView(context)
                dateTimeN.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                dateTimeN.text = clan.date + " u " + clan.time
                dateTimeN.textSize = 20f
                var horizontal=LinearLayout(context)
                horizontal.layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
                horizontal.orientation=LinearLayout.HORIZONTAL
                var layoutPara1=LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)
                var positive=TextView(context)
                positive.layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)
                positive.textSize=25f
                positive.text="Positive="
                positive.setTypeface(null, Typeface.BOLD)
                var negative=TextView(context)
                negative.layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)
                negative.textSize=25f
                negative.text="Negative="
                negative.setTypeface(null, Typeface.BOLD)
                var positiveText=TextView(context)
                positiveText.textSize=25f
                positiveText.text=clan.positive.toString()
                layoutPara1.setMargins(0, 0, 90.dpToPx(), 0)
                positiveText.layoutParams=layoutPara1
                var negativeText=TextView(context)
                negativeText.layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)
                negativeText.textSize=25f
                negativeText.text=clan.negative.toString()
                positive.setTextColor(resources.getColor(R.color.green))
                negative.setTextColor(resources.getColor(R.color.red))

                var delete = Button(context)
                var layoutParamsButton = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                delete.layoutParams = layoutParamsButton
                delete.hint = clan.id
                delete.text = "Delete"
                delete.setOnClickListener {
                    DataBase.databaseComments.child(delete.hint.toString()).removeValue()
                        .addOnSuccessListener {
                            // findNavController().navigate(R.id.action_svojiKomentariFragment_to_homeFragment)
                        }.addOnFailureListener { exception ->
                            Toast.makeText(
                                context,
                                exception.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }

                }
                layout.addView(placeText)
                layout.addView(placeN)
                layout.addView(authorText)
                layout.addView(author)
                layout.addView(gradeText)
                layout.addView(gradeN)
                layout.addView(commentText)
                layout.addView(commentN)
                layout.addView(dateTimeN)
                horizontal.addView(positive)
                horizontal.addView(positiveText)
                horizontal.addView(negative)
                horizontal.addView(negativeText)
                layout.addView(horizontal)
                layout.addView(delete)
                layout.setBackgroundColor(Color.parseColor("#C5BCC6"))
            }


        }
        if(homMany==0)
        {
            var message=TextView(context)
            message.layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
            message.textAlignment=TextView.TEXT_ALIGNMENT_CENTER
            message.textSize=40f
            message.gravity=LinearLayout.TEXT_ALIGNMENT_CENTER
            message.text="You didn't add a comment"
            main.addView(message)
        }






    }
    private fun Int.dpToPx(): Int {
        val density = resources.displayMetrics.density
        return (this * density).toInt()
    }


}