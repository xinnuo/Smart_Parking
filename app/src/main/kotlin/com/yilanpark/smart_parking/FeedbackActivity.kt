package com.yilanpark.smart_parking

import android.os.Bundle
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.yilanpark.base.BaseActivity
import com.yilanpark.base.getString
import com.yilanpark.base.setOneClickListener
import com.yilanpark.base.showToast
import com.yilanpark.share.BaseHttp
import com.yilanpark.utils.ActivityStack
import kotlinx.android.synthetic.main.activity_feedback.*
import org.jetbrains.anko.sdk25.listeners.textChangedListener

class FeedbackActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        init_title("反馈")
    }

    override fun init_title() {
        super.init_title()
        feedback_submit.setBackgroundResource(R.drawable.rec_bg_ova_d0d0d0)
        feedback_submit.isClickable = false

        feedback_content.textChangedListener {
            onTextChanged { _, _, _, _ ->
                if (feedback_content.text.isNotBlank()) {
                    feedback_submit.setBackgroundResource(R.drawable.rec_bg_ova_blue)
                    feedback_submit.isClickable = true
                } else {
                    feedback_submit.setBackgroundResource(R.drawable.rec_bg_ova_d0d0d0)
                    feedback_submit.isClickable = false
                }
            }
        }

        feedback_submit.setOneClickListener {
            OkGo.post<String>(BaseHttp.leave_message_sub)
                    .tag(this@FeedbackActivity)
                    .isMultipart(true)
                    .headers("token", getString("token"))
                    .params("content", feedback_content.text.trim().toString())
                    .execute(object : StringDialogCallback(baseContext) {

                        override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                            showToast(msg)
                            ActivityStack.screenManager.popActivities(this@FeedbackActivity::class.java)
                        }

                    })
        }
    }
}
