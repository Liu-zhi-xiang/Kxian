package com.gjmetal.app.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.star.kit.KnifeKit;

import butterknife.BindView;

/**
 *
 *  申请订阅view
 * @author liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/4/23  15:11
 */
public class NullPermissionView extends LinearLayout {
    @BindView(R.id.tvVipServiceone)
    TextView tvVipServiceone;
    @BindView(R.id.tvVipServicetwo)
    TextView tvVipServicetwo;
    @BindView(R.id.tvVipServicePhoneOne)
    TextView tvVipServicePhoneOne;
    @BindView(R.id.tvVipServicePhonetwo)
    TextView tvVipServicePhonetwo;
    @BindView(R.id.tvApplyForRead)
    Button tvApplyForRead;

    OnClickListeners  onClickListeners;
    public NullPermissionView(Context context) {
        super(context);
        setupView(context);
    }

    public void setOnClickListeners(OnClickListeners onClickListeners) {
        this.onClickListeners = onClickListeners;
    }

    public NullPermissionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupView(context);
    }

    public NullPermissionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupView(context);
    }

    private void setupView(Context context) {
        inflate(context, R.layout.view_null_permission,this);
        KnifeKit.bind(this);
        tvApplyForRead.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListeners!=null)
                onClickListeners.getClick(v);
            }
        });
    }

   public interface  OnClickListeners {
      void   getClick(View view);
    }
}
