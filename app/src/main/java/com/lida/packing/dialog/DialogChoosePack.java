package com.lida.packing.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.TextView;

import com.lida.packing.R;
import com.lida.packing.adapter.AdapterChoosePack;
import com.lida.packing.bean.PackBean;
import com.lida.packing.utils.InnerListView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 选择仓库
 * Created by WeiQingFeng on 2016/10/28 0028.
 */

public class DialogChoosePack extends Dialog {

    @BindView(R.id.listView)
    InnerListView listView;

    private Context context;
    private List<PackBean> data;
    private TextView tv;

    public DialogChoosePack(Context context, List<PackBean> data, TextView tv) {
        super(context, R.style.diy_dialog);
        this.data = data;
        this.tv = tv;
        init(context);
    }

    public DialogChoosePack(Context context, int themeResId) {
        super(context, R.style.diy_dialog);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        Window w = this.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        w.setAttributes(lp);
        this.setCanceledOnTouchOutside(true);
        View v = View.inflate(context, R.layout.dialog_choosepack, null);
        this.setContentView(v);
        ButterKnife.bind(this, v);
        listView.setAdapter(new AdapterChoosePack(context,data));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tv.setText(data.get(position).getStoreName());
                tv.setTag(data.get(position).getStoreNo());
                dismiss();
            }
        });
    }
}
