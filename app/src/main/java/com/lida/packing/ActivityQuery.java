package com.lida.packing;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.lida.packing.adapter.AdapterAlreadyIn;
import com.lida.packing.bean.ListBean;
import com.lida.packing.bean.PackBean;
import com.lida.packing.dialog.DialogChooseNumber;
import com.lida.packing.dialog.DialogChoosePack;
import com.lida.packing.dialog.DialogChooseType;
import com.lida.packing.utils.AnimatorUtils;
import com.lida.packing.utils.DBHelper;
import com.lida.packing.utils.InnerListView;
import com.lida.packing.utils.SharedPreferencesUtils;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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

/**
 * 查询页面
 * Created by Administrator on 2017/5/26.
 */

public class ActivityQuery extends BaseActivity {
    @BindView(R.id.tvType)
    TextView tvType;
    @BindView(R.id.tvPack)
    TextView tvPack;
    @BindView(R.id.tvNumber)
    TextView tvNumber;
    @BindView(R.id.btnSearch)
    Button btnSearch;
    @BindView(R.id.etCodeNum)
    EditText etCodeNum;
    @BindView(R.id.listView)
    InnerListView listView;

    private String type;
    private String packName;
    private String number;
    private String codeNum;
    private long waitTime = 2000;
    private long touchTime = 0;

    private List<PackBean> packData = new ArrayList<>();//仓库数据
    private String packId = "";//选择的仓库ID;
    private List<String> numberData = new ArrayList<>();//单据编号
    private String userId = "";

    private AdapterAlreadyIn adapter;
    private List<ListBean> listData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        ButterKnife.bind(this);
        View view = LayoutInflater.from(_activity).inflate(R.layout.header,null);
        adapter = new AdapterAlreadyIn(_activity,listData);
        listView.addHeaderView(view);
        listView.setAdapter(adapter);
        tvType.addTextChangedListener(tvTypeWatcher);
        tvNumber.addTextChangedListener(tvNumberWatcher);
    }

    TextWatcher tvTypeWatcher = new TextWatcher() {
        String s1;
        String s2;
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            s1 = s.toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            s2 = s.toString();
            LogUtils.e(s1);
            LogUtils.e(s2);
            if(!s1.equals(s2)){
                tvPack.setText("");
                tvNumber.setText("");
                etCodeNum.setText("");
                listData.clear();
                adapter.notifyDataSetChanged();
            }
        }
    };

    TextWatcher tvNumberWatcher  = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(!"".equals(s.toString())){
                initList();
            }
        }
    };

    @OnClick({R.id.tvType, R.id.tvPack, R.id.tvNumber, R.id.btnSearch, R.id.btnCancel, R.id.ivSetting})
    public void onViewClicked(View view) {
        String sql = "";
        type = tvType.getText().toString();
        switch (view.getId()) {
            case R.id.tvType:
                new DialogChooseType(_activity, tvType, btnSearch).show();
                break;
            case R.id.tvPack:
                initPack();
                break;
            case R.id.tvNumber:
                if ("".equals(type)) {
                    Toast.makeText(_activity, "请选择出入库类型", Toast.LENGTH_SHORT).show();
                    return;
                }
                if ("入库扫描".equals(type)) {
                    sql = "select OrderNo from SaleOrder where IsNull(StatusFlag,0)=2 ";
                } else {
                    sql = "select OrderNo from SaleOrder where IsNull(StatusFlag,0)=3 ";
                }
                initNumber(sql);
                break;
            case R.id.btnSearch:
                onButtonClick("1");
                break;
            case R.id.btnCancel:
                onButtonClick("0");
                break;
            case R.id.ivSetting:
                startActivity(new Intent(this, ActivitySetting.class));
                break;
        }
    }

    /**
     * 获取列表数据
     */
    private void initList() {
        final String sql = "select PackageNo,OrderNo from SaleOrderPackage where InStoreFlag='1'";
        Observable.create(new ObservableOnSubscribe<List<ListBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<ListBean>> e) throws Exception {
                try {
                    Connection conn = DBHelper.getConn(_activity);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);
                    listData.clear();
                    while (rs.next()) {
                        ListBean bean = new ListBean();
                        bean.setCodeNum(rs.getString("PackageNo"));
                        bean.setNumber(rs.getString("OrderNo"));
                        bean.setUserId((String) SharedPreferencesUtils.getParam(_activity,"userId",""));
                        listData.add(bean);
                    }
                } catch (SQLException ee) {
                    ee.printStackTrace();
                }
                e.onNext(listData);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ListBean>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<ListBean> data) {
                LogUtils.e(data);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 按钮点击事件
     */
    private void onButtonClick(String btnType) {
        String typeId = "";//操作类型
        String strSQL = "";
        packName = tvPack.getText().toString();//仓库名称
        packId = (String) tvPack.getTag();//仓库ID
        number = tvNumber.getText().toString();//单据编号
        codeNum = etCodeNum.getText().toString();//包装箱条码
        userId = (String) SharedPreferencesUtils.getParam(_activity, "userId", "");//用户ID
        if ("".equals(type)) {
            Toast.makeText(_activity, "请选择出入库类型", Toast.LENGTH_SHORT).show();
            return;
        }
        if ("入库扫描".equals(type)) {
            strSQL = "update SaleOrderPackage set InStoreFlag = '" + btnType + "',InStoreEmployeeNo='" + userId + "' where OrderNo='" + number + "' and PackageNo='" + codeNum + "'";
        } else {
            strSQL = "update SaleOrderPackage set OutStoreFlag = '" + btnType + "',OutStoreEmployeeNo='" + userId + "' where OrderNo='" + number + "' and PackageNo='" + codeNum + "'";
        }
        LogUtils.e(strSQL);
        doWork(strSQL);
    }

    /**
     * 出入库操作
     */
    private void doWork(final String sql) {
        Observable.create(new ObservableOnSubscribe<Connection>() {
            @Override
            public void subscribe(ObservableEmitter<Connection> e) throws Exception {
                Connection conn = DBHelper.getConn(_activity);
                e.onNext(conn);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<Connection>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Connection connection) {
                        int i = 0;
                        try {
                            Statement stmt = connection.createStatement();
                            i = stmt.executeUpdate(sql);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        final int finalI = i;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (finalI == 1) {
//                                    tvType.setText("");
//                                    tvPack.setText("");
//                                    tvNumber.setText("");
//                                    etCodeNum.setText("");
                                    Toast.makeText(_activity, "操作成功！", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(_activity, "操作失败！", Toast.LENGTH_SHORT).show();
                                    AnimatorUtils.onVibrationView(btnSearch);
                                }
                            }
                        });
                        initList();
                    }

                    @Override
                    public void onError(Throwable e) {
                        AnimatorUtils.onVibrationView(btnSearch);
                        Toast.makeText(_activity, "连接数据库失败，请检查配置参数!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 获取仓库
     */
    private void initPack() {
        packData.clear();
        Observable<List<PackBean>> observable = Observable.create(new ObservableOnSubscribe<List<PackBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<PackBean>> e) throws Exception {
                try {
                    Connection conn = DBHelper.getConn(_activity);
                    String strSQL = "select * from Store";
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(strSQL);
                    while (rs.next()) {
                        PackBean bean = new PackBean();
                        bean.setStoreNo(rs.getString("StoreNo"));
                        bean.setStoreName(rs.getString("StoreName"));
                        packData.add(bean);
                    }
                } catch (SQLException ee) {
                    ee.printStackTrace();
                }
                e.onNext(packData);
            }
        });
        Observer<List<PackBean>> observer = new Observer<List<PackBean>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<PackBean> packBeen) {
                LogUtils.e(packBeen);
                new DialogChoosePack(_activity, packBeen, tvPack).show();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 获取单据
     */
    private void initNumber(final String sql) {
        numberData.clear();
        Observable<List<String>> observable = Observable.create(new ObservableOnSubscribe<List<String>>() {
            @Override
            public void subscribe(ObservableEmitter<List<String>> e) throws Exception {
                try {
                    Connection conn = DBHelper.getConn(_activity);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);
                    while (rs.next()) {
                        numberData.add(rs.getString("OrderNo"));
                    }
                } catch (SQLException ee) {
                    ee.printStackTrace();
                }
                e.onNext(numberData);
            }
        });
        Observer<List<String>> observer = new Observer<List<String>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<String> packBeen) {
                new DialogChooseNumber(_activity, numberData, tvNumber).show();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
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
                    finish();
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
