package airport.transfer.sale.ui.fragment

import kotlinx.android.synthetic.main.fragment_use_switch.*
import airport.transfer.sale.R
import airport.transfer.sale.ui.fragment.drawer.TransferFragment
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EFragment

@EFragment(R.layout.fragment_use_switch)
open class UseSwitchFragment : BaseFragment(){

    @AfterViews
    fun ready(){
        contentLayout.setOnClickListener { (parentFragment as TransferFragment).hideTutor() }
    }
}