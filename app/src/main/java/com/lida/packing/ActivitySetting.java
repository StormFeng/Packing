package com.lida.packing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lida.packing.dialog.DialogIfSignOut;
import com.lida.packing.utils.SharedPreferencesUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/5/27.
 */

public class ActivitySetting extends BaseActivity {
    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.tvDataSetting)
    TextView tvDataSetting;
    @BindView(R.id.tvOut)
    TextView tvOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btnBack, R.id.tvDataSetting, R.id.tvOut})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.tvDataSetting:
                startActivity(new Intent(this,ActivityDataSetting.class));
                break;
            case R.id.tvOut:
                new DialogIfSignOut(_activity).show();
                break;
        }
    }
}
