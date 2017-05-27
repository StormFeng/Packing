package com.lida.packing.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lida.packing.R;
import com.lida.packing.utils.SharedPreferencesUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 选择类别
 * Created by WeiQingFeng on 2016/10/28 0028.
 */

public class DialogInput extends Dialog {

    @BindView(R.id.etContent)
    EditText etContent;
    @BindView(R.id.btnOk)
    Button btnOk;
    @BindView(R.id.btnCancel)
    Button btnCancel;
    private Context context;
    private onConfirmClickListener listener;
    private TextView tv;


    public DialogInput(Context context, TextView tv) {
        super(context, R.style.diy_dialog);
        this.tv = tv;
        init(context);
    }

    public DialogInput(Context context, int themeResId) {
        super(context, R.style.diy_dialog);
        init(context);
    }

    public void setTypeSelectedListener(onConfirmClickListener listener) {
        this.listener = listener;
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
        View v = View.inflate(context, R.layout.dialog_input, null);
        this.setContentView(v);
        ButterKnife.bind(this, v);
    }

    @OnClick({R.id.btnOk, R.id.btnCancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnOk:
                String s = etContent.getText().toString();
                if("".equals(s)){
                    Toast.makeText(context,"输入内容不能为空！",Toast.LENGTH_SHORT).show();
                    return;
                }
                tv.setText(s);
                switch (tv.getId()){
                    case R.id.tvAddress:
                        SharedPreferencesUtils.setParam(context,"serverName",s);
                        break;
                    case R.id.tvPort:
                        SharedPreferencesUtils.setParam(context,"serverPort",s);
                        break;
                    case R.id.tvDataName:
                        SharedPreferencesUtils.setParam(context,"databaseName",s);
                        break;
                    case R.id.tvAccount:
                        SharedPreferencesUtils.setParam(context,"userName",s);
                        break;
                    case R.id.tvPass:
                        SharedPreferencesUtils.setParam(context,"password",s);
                        break;
                }
                dismiss();
                break;
            case R.id.btnCancel:
                dismiss();
                break;
        }

    }

    public interface onConfirmClickListener {
        void onConfirmClicked(View v, String s);
    }
}
