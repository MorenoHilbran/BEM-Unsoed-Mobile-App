package com.example.bemunsoed.ui.webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.bemunsoed.R
import com.google.android.material.appbar.MaterialToolbar

class WebViewFragment : Fragment() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var toolbar: MaterialToolbar

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_webview, container, false)

        // Inisialisasi view
        webView = view.findViewById(R.id.web_view)
        progressBar = view.findViewById(R.id.progress_bar)
        toolbar = view.findViewById(R.id.toolbar)

        // Ambil URL dari arguments
        val url = arguments?.getString("url") ?: "https://apps.bem-unsoed.com"

        // Tentukan title berdasarkan URL
        val title = when {
            url.contains("soedtify", true) -> "Soedtify"
            url.contains("e-magz", true) -> "E-Magz"
            url.contains("komik", true) -> "Komik"
            else -> "BEM Unsoed"
        }
        toolbar.title = title

        // Tombol back di toolbar
        toolbar.setNavigationOnClickListener {
            if (webView.canGoBack()) {
                webView.goBack()
            } else {
                requireActivity().onBackPressed()
            }
        }

        // Setup WebView
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                progressBar.visibility = View.GONE
            }
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progressBar.visibility = if (newProgress < 100) View.VISIBLE else View.GONE
            }
        }

        webView.loadUrl(url)

        // Tangani tombol back dari perangkat
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (webView.canGoBack()) {
                        webView.goBack()
                    } else {
                        // Matikan callback dan kirim event ke activity
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            }
        )

        return view
    }
}
