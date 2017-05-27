package com.lida.packing;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lida.packing.dialog.DialogInput;
import com.lida.packing.utils.SharedPreferencesUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 设置
 * Created by Administrator on 2017/5/26.
 */

public class ActivityDataSetting extends BaseActivity {
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.tvPort)
    TextView tvPort;
    @BindView(R.id.tvDataName)
    TextView tvDataName;
    @BindView(R.id.tvAccount)
    TextView tvAccount;
    @BindView(R.id.tvPass)
    TextView tvPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        String serverName = (String) SharedPreferencesUtils.getParam(_activity,"serverName","");
        String serverPort = (String) SharedPreferencesUtils.getParam(_activity,"serverPort","");
        String databaseName = (String) SharedPreferencesUtils.getParam(_activity,"databaseName","");
        String userName = (String) SharedPreferencesUtils.getParam(_activity,"userName","");
        String password = (String) SharedPreferencesUtils.getParam(_activity,"password","");
        tvAddress.setText(serverName);
        tvPort.setText(serverPort);
        tvDataName.setText(databaseName);
        tvAccount.setText(userName);
        tvPass.setText(password);
    }

    @OnClick({R.id.llAddress, R.id.llPort, R.id.llDataName, R.id.llAccount, R.id.llPass, R.id.btnBack, R.id.btnSave})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llAddress:
                new DialogInput(_activity,tvAddress).show();
                break;
            case R.id.llPort:
                new DialogInput(_activity,tvPort).show();
                break;
            case R.id.llDataName:
                new DialogInput(_activity,tvDataName).show();
                break;
            case R.id.llAccount:
                new DialogInput(_activity,tvAccount).show();
                break;
            case R.id.llPass:
                new DialogInput(_activity,tvPass).show();
                break;
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnSave:
                Toast.makeText(_activity,"保存成功",Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }
}
