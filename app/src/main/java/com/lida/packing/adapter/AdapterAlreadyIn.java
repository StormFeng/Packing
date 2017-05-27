package com.lida.packing.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.lida.packing.R;
import com.lida.packing.bean.ListBean;
import com.lida.packing.bean.PackBean;
import com.lida.packing.utils.AnimatorUtils;
import com.lida.packing.utils.DBHelper;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 已入库
 * Created by Administrator on 2017/5/26.
 */

public class AdapterAlreadyIn extends BaseAdapter {

    private Context context;
    private List<ListBean> data;

    public AdapterAlreadyIn(Context context, List<ListBean> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvNumber.setText(data.get(position).getCodeNum());
        viewHolder.btnCalcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sql = "update SaleOrderPackage set InStoreFlag = '0',InStoreEmployeeNo='" + data.get(position).getUserId()
                        + "' where OrderNo='" + data.get(position).getNumber() + "' and PackageNo='" + data.get(position).getCodeNum() + "'";
                LogUtils.e(sql);
                doWork(sql,position);
            }
        });
        return convertView;
    }

    private void doWork(final String sql, final int position) {
        Observable.create(new ObservableOnSubscribe<Connection>() {
            @Override
            public void subscribe(ObservableEmitter<Connection> e) throws Exception {
                Connection conn = DBHelper.getConn(context);
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
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (finalI == 1) {
                                    Toast.makeText(context, "操作成功！", Toast.LENGTH_SHORT).show();
                                    data.remove(position);
                                    notifyDataSetChanged();
                                } else {
                                    Toast.makeText(context, "操作失败！", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(context, "连接数据库失败，请检查配置参数!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    static class ViewHolder {
        @BindView(R.id.tvNumber)
        TextView tvNumber;
        @BindView(R.id.btnCalcel)
        Button btnCalcel;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
