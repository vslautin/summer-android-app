package airport.transfer.sale.ui.activity

import android.content.res.ColorStateList
import android.graphics.Color
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import com.arellomobile.mvp.MvpAppCompatActivity
import kotlinx.android.synthetic.main.toolbar.*
import airport.transfer.sale.R
import airport.transfer.sale.makeToast
import airport.transfer.sale.mvp.view.BaseView


open class BaseActivity : MvpAppCompatActivity(), BaseView{

    override fun onStart() {
        super.onStart()
        setSupportActionBar(toolbar)
        title = ""
    }

    override fun onResume() {
        super.onResume()
        home?.let { ViewCompat.setBackgroundTintList(home, ColorStateList.valueOf(Color.WHITE)) }
    }

    fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.contentLayout, fragment).commit()
    }

    override fun onError(code: Int, message: String) {
        makeToast(message)
    }
}