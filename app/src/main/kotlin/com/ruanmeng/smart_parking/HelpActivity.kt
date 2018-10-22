package com.ruanmeng.smart_parking

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.isWeb
import org.jetbrains.anko.browse
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.webView
import org.json.JSONObject

class HelpActivity : BaseActivity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = webView {
            overScrollMode = View.OVER_SCROLL_NEVER

            //支持javascript
            settings.javaScriptEnabled = true
            //设置可以支持缩放
            settings.setSupportZoom(true)
            //自适应屏幕
            settings.loadWithOverviewMode = true
            settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            isHorizontalScrollBarEnabled = false

            //设置出现缩放工具
            settings.builtInZoomControls = true
            settings.displayZoomControls = false

            //设置是否使用缓存
            settings.setAppCacheEnabled(true)
            settings.domStorageEnabled = true

            webChromeClient = WebChromeClient()
            webViewClient = object : WebViewClient() {

                /* 这个事件，将在用户点击链接时触发。
                 * 通过判断url，可确定如何操作，
                 * 如果返回true，表示我们已经处理了这个request，
                 * 如果返回false，表示没有处理，那么浏览器将会根据url获取网页
                 */
                @Suppress("DEPRECATION")
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {


                    if (!url.isWeb()) return true

                    if (url.isNotEmpty() && url.endsWith("apk")) browse(url)
                    else {
                        view.loadUrl(url)
                        return true
                    }
                    return super.shouldOverrideUrlLoading(view, url)
                }

            }
        }
        init_title("帮助与反馈", "反馈")

        getData()
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.tv_nav_right -> startActivity<FeedbackActivity>()
        }
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.help_center)
                .tag(this@HelpActivity)
                .params("htmlKey", "xml")
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body())
                        val str = "<!doctype html><html>\n" +
                                "<meta charset=\"utf-8\">" +
                                "<style type=\"text/css\">" +
                                "body{ padding:0; margin:0; }\n" +
                                ".con{ width:95%; margin:0 auto; color:#666; padding:0.5em 0; overflow:hidden; display:block; font-size:0.92em; line-height:1.8em; }\n" +
                                ".con h1,h2,h3,h4,h5,h6{ font-size:1em; }\n " +
                                "img{ width:auto; max-width: 100% !important; height:auto !important; margin:0 auto; display:block; }\n" +
                                "*{ max-width:100% !important; }\n" +
                                "</style>\n" +
                                "<body style=\"padding:0; margin:0; \">" +
                                "<div class=\"con\">" +
                                obj.optString("help") +
                                "</div>" +
                                "</body>" +
                                "</html>"

                        webView.loadDataWithBaseURL(BaseHttp.baseImg, str, "text/html", "utf-8", "")
                    }

                })
    }
}
