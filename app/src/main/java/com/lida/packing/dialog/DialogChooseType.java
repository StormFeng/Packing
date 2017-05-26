package com.lida.packing.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.IdRes;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.lida.packing.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 选择类别
 * Created by WeiQingFeng on 2016/10/28 0028.
 */

public class DialogChooseType extends Dialog {

    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;

    private Context context;
    private onTypeSelectedListener listener;
    private TextView tv;
    private Button btn;


    public DialogChooseType(Context context, TextView tv, Button button) {
        super(context, R.style.diy_dialog);
        this.tv = tv;
        this.btn = button;
        init(context);
    }

    public DialogChooseType(Context context, int themeResId) {
        super(context, R.style.diy_dialog);
        init(context);
    }

    public void setTypeSelectedListener(onTypeSelectedListener listener){
        this.listener=listener;
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
        View v = View.inflate(context, R.layout.dialog_choosesex, null);
        this.setContentView(v);
        ButterKnife.bind(this, v);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton button = (RadioButton) findViewById(checkedId);
                if(listener!=null){
                    listener.select(button.getText().toString());
                }
                tv.setText(button.getText().toString());
                btn.setText("确定"+button.getText().toString().replace("扫描",""));
                dismiss();
            }
        });
    }

    public interface onTypeSelectedListener{
        void select(String s);
    }
}
