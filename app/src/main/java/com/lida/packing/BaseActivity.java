package com.lida.packing;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by Administrator on 2017/5/26.
 */

public class BaseActivity extends Activity {
    protected Activity _activity;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _activity = this;
    }
}
