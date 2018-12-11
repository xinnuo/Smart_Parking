package com.yilanpark.park_inspector

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.compress.Luban
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.yilanpark.base.*
import com.yilanpark.share.BaseHttp
import com.yilanpark.share.Const
import com.yilanpark.utils.ActivityStack
import com.yilanpark.utils.trimString
import com.yilanpark.view.FullyGridLayoutManager
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_wrong.*
import net.idik.lib.slimadapter.SlimAdapter
import org.jetbrains.anko.collections.forEachWithIndex
import java.io.File
import java.util.ArrayList
import com.yilanpark.R
class WrongActivity : BaseActivity() {

    private var selectList = ArrayList<LocalMedia>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wrong)
        init_title("设备维护")

        selectList.add(LocalMedia().apply { compressPath = "" })
        (wrong_grid.adapter as SlimAdapter).updateData(selectList)
    }

    override fun init_title() {
        super.init_title()
        wrong_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0_r5)
        wrong_submit.isClickable = false

        wrong_content.addTextChangedListener(this@WrongActivity)

        wrong_grid.apply {
            layoutManager = FullyGridLayoutManager(baseContext, 3)
            adapter = SlimAdapter.create()
                    .register<LocalMedia>(R.layout.item_wrong_grid) { data, injector ->
                        injector.with<ImageView>(R.id.item_wrong_img) {
                            it.setImageURL(data.compressPath, R.mipmap.index_icon14)
                        }
                                .visibility(R.id.item_wrong_del, if (data.compressPath.isEmpty()) View.GONE else View.VISIBLE)
                                .clicked(R.id.item_wrong_del) { _ ->
                                    selectList.remove(data)
                                    if (selectList.none { it.compressPath.isEmpty() })
                                        selectList.add(LocalMedia().apply { compressPath = "" })
                                    (adapter as SlimAdapter).notifyDataSetChanged()
                                }
                                .clicked(R.id.item_wrong_img) { _ ->
                                    PictureSelector.create(this@WrongActivity)
                                            .openGallery(PictureMimeType.ofImage())
                                            .theme(R.style.picture_customer_style)
                                            .maxSelectNum(3)
                                            .minSelectNum(1)
                                            .imageSpanCount(4)
                                            .selectionMode(PictureConfig.MULTIPLE)
                                            .previewImage(true)
                                            .previewVideo(false)
                                            .isCamera(true)
                                            .imageFormat(PictureMimeType.PNG)
                                            .isZoomAnim(true)
                                            .setOutputCameraPath(Const.SAVE_FILE)
                                            .compress(true)
                                            .glideOverride(160, 160)
                                            .enableCrop(false)
                                            .withAspectRatio(4, 3)
                                            .hideBottomControls(true)
                                            .compressSavePath(cacheDir.absolutePath)
                                            .freeStyleCropEnabled(false)
                                            .circleDimmedLayer(false)
                                            .showCropFrame(true)
                                            .showCropGrid(true)
                                            .isGif(false)
                                            .openClickSound(false)
                                            .selectionMedia(selectList.filter { it.compressPath.isNotEmpty() })
                                            .previewEggs(true)
                                            .minimumCompressSize(100)
                                            .isDragFrame(false)
                                            .forResult(PictureConfig.CHOOSE_REQUEST)
                                }
                    }
                    .attachTo(this)
        }
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.wrong_submit -> {
                if (selectList.none { it.compressPath.isNotEmpty() }) {
                    showToast("请上传异常照片")
                    return
                }

                OkGo.post<String>(BaseHttp.add_abnormal)
                        .tag(this@WrongActivity)
                        .isMultipart(true)
                        .headers("token", getString("token"))
                        .params("abnormalInfo", wrong_content.text.trimString())
                        .apply {
                            selectList.filter { it.compressPath.isNotEmpty() }.forEachWithIndex { index, localMedia ->
                                params("img${index + 1}", File(localMedia.compressPath))
                            }
                        }
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                showToast(msg)
                                ActivityStack.screenManager.popActivities(this@WrongActivity::class.java)
                            }

                        })
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun compressImgs() {
        Flowable.just(selectList)
                .map {
                    return@map Luban.with(baseContext)
                            .setTargetDir(cacheDir.absolutePath)
                            .ignoreBy(400)
                            .loadLocalMedia(it)
                            .get()
                }
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { showLoadingDialog() }
                .doFinally { cancelLoadingDialog() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { list ->
                    list.forEachWithIndex { index, file ->
                        selectList[index].compressPath = file.absolutePath
                    }

                    if (selectList.size < 3) selectList.add(LocalMedia().apply { compressPath = "" })
                    (wrong_grid.adapter as SlimAdapter).updateData(selectList)
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data) as ArrayList<LocalMedia>
                    // LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的

                    compressImgs()
                }
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (wrong_content.text.isNotBlank()) {
            wrong_submit.setBackgroundResource(R.drawable.rec_bg_blue_r5)
            wrong_submit.isClickable = true
        } else {
            wrong_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0_r5)
            wrong_submit.isClickable = false
        }
    }

}
