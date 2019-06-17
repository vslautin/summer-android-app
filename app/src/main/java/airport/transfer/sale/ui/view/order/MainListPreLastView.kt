package airport.transfer.sale.ui.view.order

import android.content.Context
import android.view.View
import kotlinx.android.synthetic.main.view_main_list_item_middle.view.*
import airport.transfer.sale.R
import org.androidannotations.annotations.EViewGroup

@EViewGroup(R.layout.view_main_list_item_middle)
open class MainListPreLastView(context: Context) : MainListMiddleView(context) {

    override fun adjustLine() {
        preLastLine.visibility = View.VISIBLE
        middleLine.visibility = View.GONE
    }
}