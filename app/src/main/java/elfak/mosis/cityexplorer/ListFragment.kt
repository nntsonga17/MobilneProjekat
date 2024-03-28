package elfak.mosis.cityexplorer

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import java.util.ArrayList



class ListFragment : Fragment() {

    private lateinit var tableLayout: TableLayout
    private val sharedViewModel: UserViewModel by activityViewModels()
    private var userArray:ArrayList<UserData> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_list, container, false)
        tableLayout = view.findViewById(R.id.tableRank)
        drawTable()
        val nizObserver= Observer<ArrayList<UserData>>{ newValue->
            userArray=newValue

            drawTable()
        }
        sharedViewModel.users.observe(viewLifecycleOwner,nizObserver)

        return view


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
            "Place","First Name", "Last Name", "Username", "Phone number",
            "Total points"
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
        for(i in 0 until userArray.size)
        {
            for(j in i+1 until userArray.size)
            {
                if(userArray[i].points!!.toInt()<userArray[j].points!!.toInt())
                {
                    var pom=userArray[i]
                    userArray[i]=userArray[j]
                    userArray[j]=pom
                }
            }

        }
        var rank=1
        for (user in userArray) {
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

                user.firstName,
                user.lastName,
                user.username,
                user.phoneNumber.toString(),
                user.points.toString(),

                )
            val textView = TextView(context)
            textView.layoutParams = textParams
            textView.text = rank.toString()
            rank++
            textView.textAlignment= LinearLayout.TEXT_ALIGNMENT_CENTER
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            row.addView(textView)

            for (textValue in textArray) {
                val textView = TextView(context)
                textView.layoutParams = textParams
                textView.text = textValue
                textView.textAlignment=LinearLayout.TEXT_ALIGNMENT_CENTER
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                row.addView(textView)
            }
            if(user.username==sharedViewModel.user.username)
            {
                row.setBackgroundColor(Color.parseColor("#ADABA5"))

            }

            tableLayout.addView(row)


        }

    }
    fun Int.dpToPx(): Int {
        val density = resources.displayMetrics.density
        return (this * density).toInt()
    }

}