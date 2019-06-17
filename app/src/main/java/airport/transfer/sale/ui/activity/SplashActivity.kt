package airport.transfer.sale.ui.activity

import android.content.Intent
import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_splash.*
import airport.transfer.sale.Constants
import airport.transfer.sale.R
import airport.transfer.sale.fromHtmlCompat
import airport.transfer.sale.getHowToUseLink
import airport.transfer.sale.mvp.presenter.SplashPresenter
import airport.transfer.sale.mvp.view.SplashView
import airport.transfer.sale.rest.models.response.model.v2.Address
import airport.transfer.sale.storage.Preferences

class SplashActivity : BaseActivity(), SplashView {

    @InjectPresenter
    lateinit var presenter: SplashPresenter

    private var realm: Realm? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isTaskRoot
                && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
                && intent.action == Intent.ACTION_MAIN) {
            finish()
            return
        }
        setContentView(R.layout.activity_splash)
        realm = Realm.getDefaultInstance()
        presenter.loadData()
        termsOfUse.text = getString(R.string.read_terms_and_conditions).fromHtmlCompat()
        termsOfUse.setOnClickListener {
            val webIntent = Intent(this, WebViewActivity_::class.java)
            webIntent.putExtra(Constants.EXTRA_KEY, getHowToUseLink())
            startActivity(webIntent)
        }
        val token = Preferences.getAuthToken(this)
        val pushToken = Preferences.getPushToken(this)
        if (token != null && pushToken != null){
            presenter.addPushToken(token, pushToken)
        }
    }

    override fun onForcedAirportReceived(airport: Address?) {
        realm?.let{
            if (!it.isClosed) it.executeTransaction {
                if (airport != null){
                    airport.isForcedAirport = true
                    airport.isAirport = true
                    it.copyToRealm(airport)
                } else {
                    it.where(Address::class.java)
                            .equalTo("isForcedAirport", true)
                            .findAll()
                            ?.forEach { it.deleteFromRealm() }
                }
            }
        }
        val mainIntent = Intent(this, MainActivity_::class.java)
        mainIntent.putExtra("click_action", intent.getStringExtra("click_action"))
        startActivity(mainIntent)
        finish()
    }

    override fun onDestroy() {
        realm?.close()
        super.onDestroy()
    }

}