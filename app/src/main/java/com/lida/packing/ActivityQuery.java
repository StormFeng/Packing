package com.lida.packing;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.lida.packing.bean.PackBean;
import com.lida.packing.dialog.DialogChooseNumber;
import com.lida.packing.dialog.DialogChoosePack;
import com.lida.packing.dialog.DialogChooseType;
import com.lida.packing.utils.DBHelper;

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

    private String type;
    private String packName;
    private String number;
    private String codeNum;

    private List<PackBean> packData = new ArrayList<>();//仓库数据
    private List<String> numberData = new ArrayList<>();//单据编号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tvType, R.id.tvPack, R.id.tvNumber, R.id.btnSearch})
    public void onViewClicked(View view) {
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
                String sql = "";
                if ("入库扫描".equals(type)) {
                    sql = "select OrderNo from SaleOrder where IsNull(StatusFlag,0)=2 ";
                } else {
                    sql = "select OrderNo from SaleOrder where IsNull(StatusFlag,0)=3 ";
                }
                initNumber(sql);
                break;
            case R.id.btnSearch:
                if ("".equals(type)) {
                    Toast.makeText(_activity, "请选择出入库类型", Toast.LENGTH_SHORT).show();
                    return;
                }
                if ("入库扫描".equals(type)) {
                    sql = "";
                } else {
                    sql = "";
                }
                packName = tvPack.getText().toString();
                number = tvNumber.getText().toString();
                codeNum = etCodeNum.getText().toString();

                break;
        }
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
                    Connection conn = DBHelper.getConn();
                    String strSQL = "select * from Store";
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(strSQL);
                    while (rs.next()) {
                        LogUtils.e("rs:" + rs);
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
                    Connection conn = DBHelper.getConn();
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
}
