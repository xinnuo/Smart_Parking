package com.ruanmeng.park_inspector

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.toNotInt
import kotlinx.android.synthetic.main.activity_preview.*
import org.json.JSONObject
import java.text.DecimalFormat

class PreviewActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)
        init_title("车位状况预览")

        getData()
    }

    @Suppress("DEPRECATION")
    override fun init_title() {
        super.init_title()
        preview_chart.apply {
            //设置为TRUE的话，图标中的数据自动变为percent
            setUsePercentValues(true)
            description.isEnabled = false
            //设置额外的偏移量(在图表视图周围)
            // setExtraOffsets(5f, 10f, 5f, 5f)
            //设置滑动减速摩擦系数，在0~1之间
            dragDecelerationFrictionCoef = 0.95f

            isDrawHoleEnabled = true
            setHoleColor(resources.getColor(R.color.transparent))
            holeRadius = 52f

            //设置隐藏饼图上文字
            setDrawEntryLabels(false)
            //设置隐藏注解文字
            legend.isEnabled = false

            //设置无数据时显示的文本
            setNoDataText("")
            setNoDataTextColor(resources.getColor(R.color.black))

            animateY(1000, Easing.EasingOption.EaseInOutQuad)
        }
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.find_ctn_parking)
                .tag(this@PreviewActivity)
                .headers("token", getString("token"))
                .execute(object : StringDialogCallback(baseContext) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body()).optJSONObject("object") ?: JSONObject()

                        val empCount = obj.optString("emp").toNotInt()
                        val expCount = obj.optString("exp").toNotInt()
                        val pingCount = obj.optString("ping").toNotInt()
                        val allCount = empCount + expCount + pingCount

                        if (allCount > 0) {
                            val empStr = DecimalFormat("0.00").format((empCount * 1.0 / allCount) * 100.00)
                            val expStr = DecimalFormat("0.00").format((pingCount * 1.0 / allCount) * 100.00)
                            val pingStr = DecimalFormat("0.00").format((expCount * 1.0 / allCount) * 100.00)

                            preview_percent1.text = "$empStr%"
                            preview_percent2.text = "$expStr%"
                            preview_percent3.text = "$pingStr%"

                            setChartData(ArrayList<PieEntry>().apply {
                                add(PieEntry(empStr.toFloat(), "空车位"))
                                add(PieEntry(pingStr.toFloat(), "正在停车"))
                                add(PieEntry(expStr.toFloat(), "异常车位"))
                            })
                        }
                    }

                })
    }

    private fun setChartData(entries: ArrayList<PieEntry>) {
        val dataSet = PieDataSet(entries, "车位")
        dataSet.sliceSpace = 10f
        dataSet.selectionShift = 5f
        dataSet.setColors(
                Color.parseColor("#49B7F1"),
                Color.parseColor("#FF9900"),
                Color.parseColor("#FF5B5B"))

        val data = PieData(dataSet)

        //设置百分比
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)
        data.setDrawValues(false)

        preview_chart.data = data
    }
}
