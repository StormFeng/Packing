package com.lida.packing;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.lida.packing.utils.AnimatorUtils;
import com.lida.packing.utils.AppManager;
import com.lida.packing.utils.DBHelper;
import com.lida.packing.utils.SharedPreferencesUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ActivityLogin extends BaseActivity {

    @BindView(R.id.button)
    Button btnLogin;
    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.etPass)
    EditText etPass;

    private String name;
    private String pass;
    private long waitTime = 2000;
    private long touchTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        String serverName = (String) SharedPreferencesUtils.getParam(_activity, "serverName", "");
        String serverPort = (String) SharedPreferencesUtils.getParam(_activity, "serverPort", "");
        String databaseName = (String) SharedPreferencesUtils.getParam(_activity, "databaseName", "");
        String userName = (String) SharedPreferencesUtils.getParam(_activity, "userName", "");
        String password = (String) SharedPreferencesUtils.getParam(_activity, "password", "");
        if ("".equals(serverName) || "".equals(serverPort) || "".equals(databaseName) || "".equals(userName) || "".equals(password)) {
            Toast.makeText(_activity, "请先完成数据库参数配置", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ActivityLogin.this, ActivityDataSetting.class));
            return;
        }
        if (!"".equals(SharedPreferencesUtils.getParam(_activity, "pass", ""))) {
            startActivity(new Intent(ActivityLogin.this, ActivityQuery.class));
            finish();
        }
    }

    private void initConnected() {
        Observable.create(new ObservableOnSubscribe<Connection>() {
            @Override
            public void subscribe(ObservableEmitter<Connection> e) throws Exception {
                Connection conn = DBHelper.getConn(_activity);
                e.onNext(conn);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Connection>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(final Connection connection) {
                        final String strSQL = "select EmployeeNo from Employee where EmployeeCode='" + name + "'" +
                                " and Password='" + pass + "' and IsNull(CanLoginSystem,0)=1";
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Statement stmt = connection.createStatement();
                                    ResultSet rs = stmt.executeQuery(strSQL);
                                    while (rs.next()) {
                                        String userId = rs.getString("EmployeeNo");
                                        LogUtils.e(userId);
                                        SharedPreferencesUtils.setParam(_activity, "userId", userId);
                                        SharedPreferencesUtils.setParam(_activity, "name", name);
                                        SharedPreferencesUtils.setParam(_activity, "pass", pass);
                                        startActivity(new Intent(ActivityLogin.this, ActivityQuery.class));
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }

                    @Override
                    public void onError(Throwable e) {
                        AnimatorUtils.onVibrationView(btnLogin);
                        Toast.makeText(_activity, "连接数据库失败，请检查配置参数!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @OnClick({R.id.button, R.id.btnSetting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button:
                String serverName = (String) SharedPreferencesUtils.getParam(_activity,"serverName","");
                String serverPort = (String) SharedPreferencesUtils.getParam(_activity,"serverPort","");
                String databaseName = (String) SharedPreferencesUtils.getParam(_activity,"databaseName","");
                String userName = (String) SharedPreferencesUtils.getParam(_activity,"userName","");
                String password = (String) SharedPreferencesUtils.getParam(_activity,"password","");
                if("".equals(serverName)||"".equals(serverPort)||"".equals(databaseName)||"".equals(userName)||"".equals(password)){
                    Toast.makeText(_activity,"请先完成数据库参数配置",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ActivityLogin.this, ActivityDataSetting.class));
                    return;
                }
                name = etName.getText().toString();
                pass = etPass.getText().toString();
                if("".equals(name)){
                    Toast.makeText(_activity,"用户名不能为空！",Toast.LENGTH_SHORT).show();
                    AnimatorUtils.onVibrationView(etName);
                    return;
                }
                if("".equals(pass)){
                    Toast.makeText(_activity,"密码不能为空！",Toast.LENGTH_SHORT).show();
                    AnimatorUtils.onVibrationView(etPass);
                    return;
                }
                initConnected();
                break;
            case R.id.btnSetting:
                startActivity(new Intent(ActivityLogin.this, ActivityDataSetting.class));
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && KeyEvent.KEYCODE_BACK == keyCode) {
                long currentTime = System.currentTimeMillis();
                if ((currentTime - touchTime) >= waitTime) {
                    Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                    touchTime = currentTime;
                } else {
                    if(DBHelper.conn!=null){
                        DBHelper.conn = null;
                    }
                    AppManager.getAppManager().appExit(_activity);
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
