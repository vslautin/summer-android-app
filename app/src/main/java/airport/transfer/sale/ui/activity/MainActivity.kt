package airport.transfer.sale.ui.activity

import android.app.Activity
import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.widget.TextView
import com.arellomobile.mvp.presenter.InjectPresenter
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import airport.transfer.sale.*
import airport.transfer.sale.mvp.model.CreditCard
import airport.transfer.sale.mvp.presenter.AuthPresenter
import airport.transfer.sale.mvp.presenter.PaymentPresenter
import airport.transfer.sale.mvp.view.AuthView
import airport.transfer.sale.mvp.view.PaymentView
import airport.transfer.sale.rest.models.response.model.User
import airport.transfer.sale.rest.models.response.model.v2.Order
import airport.transfer.sale.storage.Preferences
import airport.transfer.sale.ui.fragment.drawer.*
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EActivity
import java.util.*

@EActivity(R.layout.activity_main)
open class MainActivity : BaseActivity(), AuthView, PaymentView {

    var currentFragment: Fragment? = null

    @InjectPresenter
    lateinit var presenter: AuthPresenter

    @InjectPresenter
    lateinit var payPresenter: PaymentPresenter

    private var realm: Realm? = null

    @AfterViews
    fun ready() {
        realm = Realm.getDefaultInstance()
        setupToolbar()
        setupDrawer()
        val pInfo = packageManager?.getPackageInfo(packageName, 0)
        versionText.text = String.format(Locale.ENGLISH, "%s %s", "ver.", pInfo?.versionName)
    }

    private fun setupToolbar() {
        title = ""
        setTransferFragmentCurrent()
        home.setOnClickListener {
            if (drawerLayout.isDrawerOpen(drawer)) drawerLayout.closeDrawer(drawer)
            else {
                drawerLayout.openDrawer(drawer)
                hideKeyboard(this, contentLayout.findFocus())
            }
        }
    }

    private fun setupDrawer() {
        drawer.setNavigationItemSelectedListener { item ->
            drawerLayout.closeDrawer(drawer)
            item.isChecked = true
            when (item.itemId) {
                R.id.params -> {
                    if (Preferences.getAuthToken(this) != null) currentFragment = PersonalInfoFragment_.builder().build()
                    else {
                        showAuthToChatDialog()
                        return@setNavigationItemSelectedListener true
                    }
                }
                R.id.payment -> {
                    if (Preferences.getAuthToken(this) != null) currentFragment = PaymentMethodsFragment_.builder().build()
                    else {
                        showAuthToChatDialog()
                        return@setNavigationItemSelectedListener true
                    }
                }
                R.id.feedback -> {
                    if (Preferences.getAuthToken(this) != null) currentFragment = FeedbackFragment_.builder().build()
                    else {
                        showAuthToChatDialog()
                        return@setNavigationItemSelectedListener true
                    }
                }
                R.id.rules -> {
                    currentFragment = WebViewFragment.newInstance(getTermsLink())
                }
                R.id.logout -> {
                    showLogoutDialog()
                    return@setNavigationItemSelectedListener true
                }
                else -> {
                    currentFragment = TransferFragment_.builder().build()
                }
            }
            supportFragmentManager.beginTransaction().replace(R.id.contentLayout, currentFragment).commit()
            return@setNavigationItemSelectedListener true
        }
        setupDrawerHeader()
        /*drawer.getHeaderView(0).findViewById(R.id.contactCenterPhone)?.setOnClickListener {
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contactCenterPhone.text)))
            drawerLayout.closeDrawer(drawer)
        }*/
    }

    fun setupDrawerHeader(){
        val authButton = drawer.getHeaderView(0)
        if (Preferences.getAuthToken(this) != null) {
            val user = getUser(realm) ?: User()
            if (TextUtils.isEmpty(user.email) || TextUtils.isEmpty(user.firstName)) {
                val intent = Intent(this, RegistrationActivity_::class.java)
                intent.putExtra(Constants.EXTRA_NAME, user.firstName ?: "")
                intent.putExtra(Constants.EXTRA_MAIL, user.email ?: "")
                startActivityForResult(intent, Constants.REQUEST_REGISTRATION)
            } else {
                (authButton.findViewById<TextView>(R.id.drawerTitle) as TextView).text =
                        if (user.lastName === null) user.firstName else (user.lastName + " " + user.firstName).trim()
                (authButton.findViewById<TextView>(R.id.drawerSubtitle) as TextView).text = user.email
                drawer.menu.findItem(R.id.logout).isVisible = true
            }
            authButton?.setOnClickListener{
                setParamsFragmentCurrent()
            }
        } else {
            drawer.menu.findItem(R.id.logout).isVisible = false
            authButton?.setOnClickListener {
                startActivityForResult(Intent(this, RegistrationActivity_::class.java), Constants.REQUEST_REGISTRATION)
                drawerLayout.closeDrawer(drawer)
            }
            (authButton.findViewById<TextView>(R.id.drawerTitle) as TextView).setText(R.string.authorize)
            (authButton.findViewById<TextView>(R.id.drawerSubtitle) as TextView).setText(R.string.auth_creation)
        }
    }

    private fun showAuthToChatDialog(){
        AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setTitle(R.string.need_auth)
                .setPositiveButton(R.string.authorize) { dialog, which ->
                    startActivityForResult(Intent(this, RegistrationActivity_::class.java), Constants.REQUEST_REGISTRATION)
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
    }

    private fun showLogoutDialog(){
        AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setTitle(R.string.logout_title)
                .setPositiveButton(R.string.ok) { dialog, which -> presenter.logout(Preferences.getAuthToken(this)!!)}
                .setNegativeButton(R.string.cancel, null)
                .show()
    }

    override fun onLogout(){
        Preferences.logout(this, realm)
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            it.delete(Order::class.java)
            it.delete(CreditCard::class.java)
        }
        realm.close()
        drawer.menu.findItem(R.id.logout).isVisible = false
        setupDrawerHeader()
        /*authButton.visibility = View.VISIBLE
        authButton?.setOnClickListener {
            startActivityForResult(Intent(this, RegistrationActivity_::class.java), Constants.REQUEST_REGISTRATION)
            drawerLayout.closeDrawer(drawer)
        }*/
        setTransferFragmentCurrent()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_REGISTRATION) {
            /*val authButton = drawer.getHeaderView(0).findViewById(R.id.authorizationButton)
            authButton.visibility = View.GONE*/
            setupDrawerHeader()
            if (currentFragment != null && currentFragment is TransferFragment){
                setTransferFragmentCurrent()
                getCards()
            }
        }
    }

    fun getCards(){
        Preferences.getAuthToken(this)?.let{ payPresenter.getCards(it) }
    }

    private fun setTransferFragmentCurrent(){
        currentFragment = TransferFragment_.builder().build()
        supportFragmentManager.beginTransaction().replace(R.id.contentLayout, currentFragment).commit()
        drawer.menu.findItem(R.id.order).isChecked = true
    }

    private fun setParamsFragmentCurrent(){
        currentFragment = PersonalInfoFragment_.builder().build()
        supportFragmentManager.beginTransaction().replace(R.id.contentLayout, currentFragment).commit()
        drawer.menu.findItem(R.id.params).isChecked = true
        drawerLayout.closeDrawer(drawer)
    }

    override fun onCreditCardsReceived(cards: List<CreditCard>) {
        realm?.let {
            if (!it.isClosed) {
                it.executeTransaction {
                    if (cards.isEmpty()) it.delete(CreditCard::class.java)
                    else {
                        it.copyToRealmOrUpdate(cards)
                    }
                }
            } }
    }

    override fun onDestroy() {
        realm?.close()
        super.onDestroy()
    }

}
