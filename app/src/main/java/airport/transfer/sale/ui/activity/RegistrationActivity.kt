package airport.transfer.sale.ui.activity

import android.app.Activity
import android.text.TextUtils
import kotlinx.android.synthetic.main.toolbar.*
import airport.transfer.sale.Constants
import airport.transfer.sale.R
import airport.transfer.sale.ui.fragment.drawer.registration.RegistrationFragment_
import airport.transfer.sale.ui.fragment.drawer.registration.UserEmailFragment
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EActivity

@EActivity(R.layout.activity_with_container)
open class RegistrationActivity : BaseActivity(){

    @AfterViews
    fun ready() {
        home.setImageResource(R.drawable.back)
        home.setOnClickListener { onBackPressed() }
        val name = intent.getStringExtra(Constants.EXTRA_NAME)
        val mail = intent.getStringExtra(Constants.EXTRA_MAIL)
        if (name != null || mail != null) showRegistrationNextStep(name, mail)
        else showFragment(RegistrationFragment_())
    }

    fun showRegistrationNextStep(userName: String?, userEmail: String?) {
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(userEmail)) {
            val fragment = UserEmailFragment.newInstance(userName, userEmail)
            supportFragmentManager.beginTransaction().add(R.id.contentLayout, fragment)
                    .addToBackStack(Constants.REGISTRATION).commit()
        } else {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}