package com.lida.packing.utils;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.CycleInterpolator;

/**
 * Created by Administrator on 2017/5/27.
 */

public class AnimatorUtils {
    /** 震荡View左右 */
    public static void onVibrationView(View v) {
        ObjectAnimator mObjectAnimator = ObjectAnimator.ofFloat(v, "TranslationX", 0.0F, 15.0F);
        mObjectAnimator.setInterpolator(new CycleInterpolator(5f));
        mObjectAnimator.setDuration(300);
        mObjectAnimator.start();
    }
}
