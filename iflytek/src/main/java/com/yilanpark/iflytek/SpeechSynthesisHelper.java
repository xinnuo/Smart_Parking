package com.yilanpark.iflytek;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

public class SpeechSynthesisHelper {

    private Context context;
    //语音合成对象
    private SpeechSynthesizer mSpeech;
    /**
     * 初始化监听。
     */
    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            if (code == ErrorCode.SUCCESS) {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            } else Log.e("InitListener", "初始化失败，错误码：" + code);
        }
    };

    private SpeechSynthesisHelper(Context context) {
        this.context = context;
        initParam();
    }

    public static SpeechSynthesisHelper getInstance(Context ctx) {
        return new SpeechSynthesisHelper(ctx);
    }

    /**
     * 参数设置
     */
    private void initParam() {
        //初始化合成对象
        mSpeech = SpeechSynthesizer.createSynthesizer(context, mInitListener);

        //清空参数
        mSpeech.setParameter(SpeechConstant.PARAMS, null);
        //设置听写引擎（在线、本地、混合）
        mSpeech.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        //设置是否抛出合成数据：{null, 0, 1}
        mSpeech.setParameter(SpeechConstant.TTS_DATA_NOTIFY, "1");
        //设置在线朗读者
        mSpeech.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
        //设置合成语速
        mSpeech.setParameter(SpeechConstant.SPEED, "50");
        //设置合成音调
        mSpeech.setParameter(SpeechConstant.PITCH, "50");
        //设置合成音量
        mSpeech.setParameter(SpeechConstant.VOLUME, "50");
        //设置播放器音频流类型（0 通话, 1 系统, 2 铃声, 3 音乐, 4 闹铃, 5 通知）
        mSpeech.setParameter(SpeechConstant.STREAM_TYPE, "5");
        //设置播放合成音频打断音乐播放，默认为true
        mSpeech.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
        //数字发音方式（0 自动，不确定时按值、 1 值、 2 串、 3 自动，不确定时按串）
        mSpeech.setParameter("rdn", "2");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        // mSpeech.setParameter(SpeechConstant.AUDIO_FORMAT, "pcm");
        // mSpeech.setParameter(SpeechConstant.TTS_AUDIO_PATH, context.getCacheDir().getAbsolutePath() + "/msc/tts.pcm");
    }

    public int startSpeaking(String text) {
        return mSpeech.startSpeaking(text, mSynthesizerListener);
    }

    public void stopSpeaking() {
        if (mSpeech != null) mSpeech.stopSpeaking();
    }

    public void pauseSpeaking() {
        if (mSpeech != null) mSpeech.pauseSpeaking();
    }

    public void resumeSpeaking() {
        if (mSpeech != null) mSpeech.resumeSpeaking();
    }

    public void destroySpeaking() {
        if (mSpeech != null) {
            mSpeech.stopSpeaking();
            //退出时释放连接
            mSpeech.destroy();
        }
    }

    /**
     * 合成回调监听
     */
    private SynthesizerListener mSynthesizerListener = new SynthesizerListener() {

        private String syncTag = "SynthesizerListener";

        @Override
        public void onSpeakBegin() {
            Log.e(syncTag, "朗读开始");
        }

        @Override
        public void onSpeakPaused() {
            Log.e(syncTag, "朗读暂停");
        }

        @Override
        public void onSpeakResumed() {
            Log.e(syncTag, "朗读恢复");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
            Log.e(syncTag, "合成进度" + percent + "%");
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            Log.e(syncTag, "朗读进度" + percent + "%");
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                Log.e(syncTag, "朗读完成");
            } else {
                Log.e(syncTag, "朗读异常：" + error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int eventType, int i1, int i2, Bundle bundle) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(syncTag, "session id =" + sid);
            //	}
        }
    };

}
