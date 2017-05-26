package com.lida.packing;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.lida.packing.utils.DBHelper;
import com.lida.packing.utils.SharedPreferencesUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityLogin extends BaseActivity {

    @BindView(R.id.button)
    Button btnLogin;
    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.etPass)
    EditText etPass;

    private String name;
    private String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if(!"".equals(SharedPreferencesUtils.getParam(_activity,"pass",""))){
            startActivity(new Intent(ActivityLogin.this, ActivityQuery.class));
            finish();
        }
    }

    private void initConnected() {
        try {
            Connection conn = DBHelper.getConn();
            String strSQL = "select EmployeeNo from Employee where EmployeeCode='" + name + "'" +
                    " and Password='" + pass + "' and IsNull(CanLoginSystem,0)=1";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(strSQL);
            while (rs.next()) {
                SharedPreferencesUtils.setParam(_activity,"name",name);
                SharedPreferencesUtils.setParam(_activity,"pass",pass);
                startActivity(new Intent(ActivityLogin.this, ActivityQuery.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.button)
    public void onViewClicked() {
        name = etName.getText().toString();
        pass = etPass.getText().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                initConnected();
            }
        }).start();
    }
}
