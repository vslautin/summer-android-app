package airport.transfer.sale.ui.fragment.drawer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.fragment_web.*
import airport.transfer.sale.Constants
import airport.transfer.sale.R
import airport.transfer.sale.ui.fragment.BaseFragment
import android.net.http.SslError
import android.webkit.SslErrorHandler
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EFragment

@EFragment(R.layout.fragment_web)
open class WebViewFragment : BaseFragment() {

    companion object {
        fun newInstance(url: String) : WebViewFragment {
            val args = Bundle()
            args.putString(Constants.EXTRA_KEY, url)
            val fragment = WebViewFragment_.builder().build()
            fragment.arguments = args
            return fragment
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @AfterViews
    fun ready(){
        with(webView){
            webChromeClient = WebChromeClient()
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    progressBar?.visibility = View.GONE
                }

                override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                    handler?.proceed()
                }
            }
            loadUrl(arguments.getString(Constants.EXTRA_KEY))
        }
    }
}