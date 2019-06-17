package airport.transfer.sale.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.view.View
import android.webkit.*
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_web.*
import kotlinx.android.synthetic.main.toolbar.*
import airport.transfer.sale.Constants
import airport.transfer.sale.R
import airport.transfer.sale.rest.Api
import airport.transfer.sale.rest.models.response.CreateOrderResponse
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EActivity
import java.net.URLDecoder


@EActivity(R.layout.activity_web)
open class WebViewActivity : BaseActivity(){

    var realm: Realm? = null

    @SuppressLint("SetJavaScriptEnabled")
    @AfterViews
    fun ready(){
        realm = Realm.getDefaultInstance()
        home.setImageResource(R.drawable.back)
        home.setOnClickListener { onBackPressed() }
        with(webView){
            webChromeClient = object: WebChromeClient() {

                override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                    try{
                        val response = Gson().fromJson(consoleMessage?.message(), CreateOrderResponse::class.java)
                        if (response.status == Constants.Status.SUCCESS) {
                            response?.order?.let { order ->
                                realm?.let {
                                    if (!it.isClosed) it.executeTransaction { it.copyToRealmOrUpdate(order) }
                                }
                            }
                            val data = Intent()
                            response.payment?.message?.let{ data.putExtra(Constants.EXTRA_KEY, it) }
                            setResult(Activity.RESULT_OK, data)
                            finish()
                        } /*else {
                            val data = Intent()
                            response.payment?.message?.let{ data.putExtra(Constants.EXTRA_KEY, it) }
                            setResult(Activity.RESULT_FIRST_USER, data)
                            finish()
                        }*/
                    } catch (ignored: JsonSyntaxException){
                    }
                    return true
                }
            }
            settings.javaScriptEnabled = true
            if (Build.VERSION.SDK_INT >= 21) {
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
            webViewClient = object : WebViewClient() {

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    System.out.println("pageStarted $url")
                    if (url?.contains(if (Constants.isTestServer) Api.TEST_BASE_URL else Api.BASE_URL) == true) {
                        val result = url.substringBefore("?").substringAfterLast("/")
                        System.out.println("result $result")
                        if (result == "success") {
                            val data = Intent()
                            setResult(Activity.RESULT_OK, data)
                            data.putExtra(Constants.EXTRA_KEY, getString(R.string.payment_success))
                            finish()
                        } else if (result == "fail"){
                            val data = Intent()
                            url.substringAfterLast("=").let { data.putExtra(Constants.EXTRA_KEY, URLDecoder.decode(it, "UTF-8")) }
                            setResult(Activity.RESULT_FIRST_USER, data)
                            finish()
                        }
                    }
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    progressBar.visibility = View.GONE
                    view?.loadUrl("javascript:console.log(document.body.getElementsByTagName('pre')[0].innerHTML);")
                }

            }
            val postParams = intent.getStringExtra(Constants.EXTRA_POST)
            if (postParams == null) loadUrl(intent.getStringExtra(Constants.EXTRA_KEY))
            else postUrl(intent.getStringExtra(Constants.EXTRA_KEY), postParams.replace("+", "%2B").toByteArray())
        }
    }

    override fun onDestroy() {
        realm = Realm.getDefaultInstance()
        super.onDestroy()
    }
}