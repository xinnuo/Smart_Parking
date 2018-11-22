package com.ruanmeng.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.ruanmeng.park_inspector.R;

public class SoundHelper {

    private SoundPool soundPool;
    private int idRing01, idRing02, idRing03;

    private static SoundHelper helper;

    public SoundHelper(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SoundPool.Builder spb = new SoundPool.Builder();
            spb.setMaxStreams(10);
            soundPool = spb.build(); // 创建SoundPool对象
        } else {
            soundPool = new SoundPool(2, AudioManager.STREAM_SYSTEM, 5);
        }
        idRing01 = soundPool.load(context, /*R.raw.park_ring01*/-1, 1);
        idRing02 = soundPool.load(context, /*R.raw.park_ring02*/-2, 1);
        idRing03 = soundPool.load(context, /*R.raw.park_ring03*/-3, 1);
    }

    public static SoundHelper getInstande(Context context) {
        if (null == helper) {
            helper = new SoundHelper(context);
        }
        return helper;
    }

    public void palyOne() {
        soundPool.play(idRing01, 1, 1, 10, 0, 1);
    }

    public void palyTwo() {
        soundPool.play(idRing02, 1, 1, 0, 0, 1);
    }

    public void palyThree() {
        soundPool.play(idRing03, 1, 1, 0, 0, 1);
    }

}
