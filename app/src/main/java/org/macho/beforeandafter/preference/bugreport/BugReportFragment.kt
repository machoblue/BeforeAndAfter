package org.macho.beforeandafter.preference.bugreport

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.bug_report_frag.*
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.di.ActivityScoped
import javax.inject.Inject


@ActivityScoped
class BugReportFragment @Inject constructor(): DaggerFragment()  {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bug_report_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webView.loadUrl(getString(R.string.bug_report_url))
        enableJavaScript()
        preventChromeFromOpening()
    }

    private fun enableJavaScript() {
        // JavaScriptを有効化
        webView.getSettings().setJavaScriptEnabled(true);

        // Web Storage を有効化
        webView.getSettings().setDomStorageEnabled(true);

        // HTML5 Video support のため
        // Hardware acceleration on
        this.activity?.getWindow()?.setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

    }

    private fun preventChromeFromOpening() {
        webView.webViewClient = object: WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                return super.shouldOverrideUrlLoading(view, request)
            }
        }
    }
}
