package com.lida.packing;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.lida.packing.utils.AppManager;

/**
 * Created by Administrator on 2017/5/26.
 */

public class BaseActivity extends Activity {

    protected Activity _activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        _activity = this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }
}
