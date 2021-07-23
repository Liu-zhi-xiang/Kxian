package com.gjmetal.app.adapter.alphametal;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.manager.ReadPermissionsManager;
import com.gjmetal.app.model.alphametal.LmeSettleModel;
import com.gjmetal.app.model.market.kline.Lem;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.ui.alphametal.lme.SwapDetailActivity;
import com.gjmetal.app.util.DateUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.autoText.AutofitTextView;
import com.gjmetal.star.kit.KnifeKit;
import com.star.kchart.lemview.GroupView;
import com.star.kchart.lemview.MainSpreadView;
import com.star.kchart.lemview.MainView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 * Description:
 * LEM升贴水 adapter 2 备用
 *
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/4/28  10:14
 */
public class LmeDifferenceInPriceAdapterTwo extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /**
     * 头视图
     */
    public static final int TYPE_HEADER = 0;
    /**
     * 正常的
     */
    public static final int TYPE_NORMAL = 1;

    public List<LmeSettleModel.DataListBean> metalSubjectList;
    private Context context;

    public LmeDifferenceInPriceAdapterTwo(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_lme_list_head, parent, false);
            return new HeaderViewHolder(view);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_lme_settlement_price, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == TYPE_HEADER) {
            if (holder instanceof HeaderViewHolder) {
                HeaderViewHolder holder1 = (HeaderViewHolder) holder;
                if (mLemModels != null) {
                    holder1.initView();
                    holder1.initLmeView(mLemModels);
                }
            }

        }

        final int pos = getRealPosition(holder);
        if (holder instanceof MyViewHolder) {
            MyViewHolder viewHolder = (MyViewHolder) holder;
            viewHolder.setData(pos - 1);
            if (position % 2 == 0) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.c25345B));
            } else {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.c1E3A65));
            }
        }
    }

    private String companyDay = "0";


    public void setMetalSubjectList(List<LmeSettleModel.DataListBean> metalSubjectList) {
        this.metalSubjectList = metalSubjectList;
    }

    public List<LmeSettleModel.DataListBean> getMetalSubjectList() {
        if (metalSubjectList == null) {
            return null;
        }
        return metalSubjectList;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        return metalSubjectList == null ? 1 : metalSubjectList.size() + 1;
    }

    public int getRealPosition(RecyclerView.ViewHolder holder) {
        return holder.getLayoutPosition();
    }

    private List<Lem> mLemModels;

    public void setmLemModels(List<Lem> mLemModels, String metalCode, String metalName, String companyDay) {
        this.mLemModels = mLemModels;
        this.metalCode = metalCode;
        this.metalName = metalName;
        this.companyDay = companyDay;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvTimeNum)
        AutofitTextView tvTimeNum;
        @BindView(R.id.tvPriceValue)
        AutofitTextView tvPriceValue;
        @BindView(R.id.tv3MDifference)
        AutofitTextView tv3MDifference;

        public MyViewHolder(View itemView) {
            super(itemView);
            KnifeKit.bind(this, itemView);
        }

        public void setData(int position) {
            LmeSettleModel.DataListBean bean = metalSubjectList.get(position);
            if (bean != null) {
                tv3MDifference.setText(bean.getPriceDiff());
                tvPriceValue.setText(bean.getPrice());
                tvTimeNum.setText(bean.getSourceDate());
            }
        }
    }

    private String metalCode, metalName;


    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rbDayComparison)
        RadioButton rbDayComparison;
        @BindView(R.id.rbWeekComparison)
        RadioButton rbWeekComparison;
        @BindView(R.id.rbTwoWeekComparison)
        RadioButton rbTwoWeekComparison;
        @BindView(R.id.rgTab)
        RadioGroup rgTab;
        @BindView(R.id.tvLmeTitle)
        TextView tvLmeTitle;
        @BindView(R.id.tvLmeTime)
        TextView tvLmeTime;
        @BindView(R.id.lme_view)
        GroupView lmeView;//LEM绝对价格走势图
        @BindView(R.id.lmeEmpty)
        EmptyView lmeEmpty;
        @BindView(R.id.lmeViewTwo)
        GroupView lmeViewTwo;//LEM调期结构走势图
        @BindView(R.id.lmeEmptyTwo)
        EmptyView lmeEmptyTwo;
        @BindView(R.id.tvSwapsDetails)
        TextView tvSwapsDetails;
        @BindView(R.id.tvLmeTitleTwo)
        TextView tvLmeTitleTwo;
        @BindView(R.id.tvLmeTimeTwo)
        TextView tvLmeTimeTwo;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            KnifeKit.bind(this, itemView);
        }


        private void initView() {
            switch (companyDay){
                case "0":
                    rbDayComparison.setChecked(true);
                    rbWeekComparison.setChecked(false);
                    rbTwoWeekComparison.setChecked(false);
                    break;
                case "1":
                    rbDayComparison.setChecked(false);
                    rbWeekComparison.setChecked(true);
                    rbTwoWeekComparison.setChecked(false);
                    break;
                case "2":
                    rbDayComparison.setChecked(false);
                    rbWeekComparison.setChecked(false);
                    rbTwoWeekComparison.setChecked(true);
                    break;
                    default:
                        break;
            }
            rgTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case R.id.rbDayComparison:
                            companyDay = "0";
                            if (onClickView != null) {
                                onClickView.onView(companyDay);
                            }
                            break;
                        case R.id.rbWeekComparison:
                            companyDay = "1";
                            if (onClickView != null) {
                                onClickView.onView(companyDay);
                            }
                            break;
                        case R.id.rbTwoWeekComparison:
                            companyDay = "2";
                            if (onClickView != null) {
                                onClickView.onView(companyDay);
                            }
                            break;
                        default:
                            break;
                    }
                }
            });
        }

        @OnClick(R.id.tvSwapsDetails)
        public void onViewClicked() {
            AppAnalytics.getInstance().onEvent(context,"alpha_LME_detail");//调期费详情点击量
            readLmePermission();
            if (onClickView != null) {
                onClickView.onView("-1");
            }
        }

        private void initLmeView(List<Lem> lmeModels) {
            if (lmeModels != null && lmeModels.size() > 0) {
                if (lmeEmpty == null || lmeView == null || lmeViewTwo == null || lmeEmptyTwo == null) {
                    return;
                }
                String time = DateUtil.getStringDateByLong(lmeModels.get(0).getTradeDate(), 2);
                lmeViewTwo.initData(lmeModels);
                switch (companyDay){
                    case "0":
                        setTextAddDrawView(context.getString(R.string.txt_day_comparison), context.getString(R.string.change_day), time);
                        lmeView.initData(lmeModels);
                        break;
                    case "1":
                        setTextAddDrawView(context.getString(R.string.txt_week_comparison), context.getString(R.string.change_week), time);
                        break;
                    case "2":
                        setTextAddDrawView(context.getString(R.string.txt_twoweek_comparison), context.getString(R.string.change_two_week), time);
                        break;
                        default:
                            break;
                }
                lmeEmpty.setVisibility(View.GONE);
                lmeEmptyTwo.setVisibility(View.GONE);
                lmeView.setVisibility(View.VISIBLE);
                lmeViewTwo.setVisibility(View.VISIBLE);
            } else {
                List<Lem> mLemModels = new ArrayList<>();
                lmeView.initData(mLemModels);
                lmeViewTwo.initData(mLemModels);
                lmeEmpty.setVisibility(View.VISIBLE);
                lmeEmpty.setNoData(Constant.BgColor.BLUE);
                lmeEmptyTwo.setVisibility(View.VISIBLE);
                lmeEmptyTwo.setNoData(Constant.BgColor.BLUE);
                lmeView.setVisibility(View.GONE);
                lmeViewTwo.setVisibility(View.GONE);
                tvLmeTitle.setText(metalName + "LEM调期结构");
                tvLmeTitleTwo.setText(metalName + "LEM绝对价");

            }
        }

        private void setTextAddDrawView(String str1, String str2, String data) {
            ((MainSpreadView) lmeViewTwo.getMainDraw()).setDateStr(str1, str2);

            tvLmeTitle.setText(metalName + context.getString(R.string.lem_swaps_structure));
            tvLmeTime.setText(data);
            tvLmeTitleTwo.setText(metalName + context.getString(R.string.lem_definitely_price));
            tvLmeTimeTwo.setText(data);
        }

        public void onDestroy() {
            if (lmeView.getMainDraw() != null) {
                ((MainView) lmeView.getMainDraw()).releaseMemory();
            }
            if (lmeViewTwo.getMainDraw() != null) {
                ((MainSpreadView) lmeViewTwo.getMainDraw()).releaseMemory();
            }
        }
    }

    private TabOnClickView onClickView;

    public void setOnClickView(TabOnClickView onClickView) {
        this.onClickView = onClickView;
    }

    public interface TabOnClickView {
        void onView(String type);
    }

    private void readLmePermission() {
        ReadPermissionsManager.readPermission(Constant.Spread.RECORD_CODE
                , Constant.POWER_PAI
                , ""
                , "/rest/lme/getRtLMEDetailVOList"
                , context
                , null
                , Constant.ApplyReadFunction.ZH_APP_AM_SPREAD_DETAIL, true, false).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) {
                if (s.equals(Constant.PermissionsCode.ACCESS.getValue())) {
                    SwapDetailActivity.launch((Activity) context, metalName, metalCode);
                }
            }
        });

    }
}
