package airport.transfer.sale.ui.view

import android.content.Context
import android.view.View
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.view_tab.view.*
import airport.transfer.sale.R
import org.androidannotations.annotations.EViewGroup


@EViewGroup(R.layout.view_tab)
open class TasksTabView(context: Context) : RelativeLayout(context) {

    fun bind(text: String, counter: Int){
        titleText.text = text
        if (counter == 0) counterText.visibility = View.GONE
        else {
            counterText.visibility = View.VISIBLE
            counterText.text = counter.toString()
        }
    }
}