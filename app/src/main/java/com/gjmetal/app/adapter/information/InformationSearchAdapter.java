package com.gjmetal.app.adapter.information;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.manager.ReadPermissionsManager;
import com.gjmetal.app.model.information.InformationContentBean;
import com.gjmetal.app.model.webview.WebViewBean;
import com.gjmetal.app.ui.information.InformationWebViewActivity;
import com.gjmetal.app.util.DateUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.TextUtil;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.util.ValueUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.droidlover.xrecyclerview.RecyclerAdapter;
import io.reactivex.functions.Consumer;

/**
 * Description：资讯搜索
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-12-12 17:32
 */

public class InformationSearchAdapter extends RecyclerAdapter<InformationContentBean.ListBean, InformationSearchAdapter.ViewHodler> {

    private String mKey;
    private String cacheReadKey;
    public InformationSearchAdapter(Context context,String cacheReadKey,String key) {
        super(context);
        this.mKey=key;
        this.cacheReadKey=cacheReadKey;
    }

    @Override
    public ViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHodler(LayoutInflater.from(context).inflate(R.layout.item_infomation_search, parent, false));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(ViewHodler holder, final int position) {
        holder.llResultHint.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
        final InformationContentBean.ListBean bean = data.get(position);
        if (ValueUtil.isStrNotEmpty(bean.getTitle())) {
            holder.tvSearchTitle.setText(Html.fromHtml(TextUtil.matcherSearchTitle(bean.getTitle(),mKey)));
        } else {
            holder.tvSearchTitle.setText("");
        }
        if (!TextUtils.isEmpty(bean.getVip())&&bean.getVip().equals("Y")){
            holder.tvVip.setVisibility(View.VISIBLE);
        }else {
            holder.tvVip.setVisibility(View.GONE);
        }
        //设置标题已读未读颜色
        holder.tvSearchTitle.setTextColor(bean.isHasRead()?ContextCompat.getColor(context,R.color.c7A7E82):ContextCompat.getColor(context,R.color.c202239));
        holder.tvInforTime.setText(GjUtil.diffDate(DateUtil.getStringDateByLong(bean.getPushTime(),5), TimeUtils.date2String(new Date(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))));
        String provide = null;
        if(ValueUtil.isStrNotEmpty(bean.getProvide())){
            provide=bean.getProvide().replace(" ","");
        }
        holder.tvAuthor.setText(ValueUtil.isStrEmpty(provide) ? "-" : provide);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bean.getVip().equals("Y")){
                        ReadPermissionsManager.readPermission(Constant.News.RECORD_NEWS_CODE
                                ,Constant.POWER_RECORD
                                , Constant.News.RECORD_NEWS_MODULE
                                ,context
                                ,null
                                ,Constant.ApplyReadFunction.ZH_APP_APP_NWES_VIP,true,false).subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) {
                                if (s.equals(Constant.PermissionsCode.ACCESS.getValue())){
                                    GjUtil.cacheSearchHistory(mKey);
                                    bean.setHasRead(true);
                                    notifyDataSetChanged();
                                    if(ValueUtil.isStrNotEmpty(cacheReadKey)){
                                        GjUtil.setInforMationReadStasus(cacheReadKey,bean.getNewsId());
                                    }
                                    InformationWebViewActivity.launch((Activity) context, bean,new WebViewBean(bean.getTitle(),Constant.ReqUrl.getInforMationUrl(bean.getDetailsUrl())),Constant.IntentFrom.INFORMATION);
                                }
                            }
                        });
                }else {
                    GjUtil.cacheSearchHistory(mKey);
                    bean.setHasRead(true);
                    notifyDataSetChanged();
                    if(ValueUtil.isStrNotEmpty(cacheReadKey)){
                        GjUtil.setInforMationReadStasus(cacheReadKey,bean.getNewsId());
                    }
                    InformationWebViewActivity.launch((Activity) context, bean,new WebViewBean(bean.getTitle(),Constant.ReqUrl.getInforMationUrl(bean.getDetailsUrl())),Constant.IntentFrom.INFORMATION);

                }

            }
        });
    }

    public void setSearchKey(String key){
        this.mKey=key;
        notifyDataSetChanged();
    }

    public class ViewHodler extends RecyclerView.ViewHolder {
        @BindView(R.id.llResultHint)
        LinearLayout llResultHint;
        @BindView(R.id.tvSearchTitle)
        TextView tvSearchTitle;
        @BindView(R.id.tvAuthor)
        TextView tvAuthor;
        @BindView(R.id.tvInforTime)
        TextView tvInforTime;
        @BindView(R.id.tvVip)
        TextView tvVip;
        public ViewHodler(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
