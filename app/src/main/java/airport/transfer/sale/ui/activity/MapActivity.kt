package airport.transfer.sale.ui.activity

import android.view.View
import kotlinx.android.synthetic.main.activity_with_container.*
import kotlinx.android.synthetic.main.toolbar.*
import airport.transfer.sale.R
import airport.transfer.sale.ui.fragment.order.MapFragment_
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EActivity

@EActivity(R.layout.activity_with_container)
open class MapActivity : BaseActivity() {

    @AfterViews
    fun ready(){
        toolbarShadow.visibility = View.GONE
        home.setImageResource(R.drawable.back)
        home.setOnClickListener { onBackPressed() }
        showFragment(MapFragment_())
    }
}