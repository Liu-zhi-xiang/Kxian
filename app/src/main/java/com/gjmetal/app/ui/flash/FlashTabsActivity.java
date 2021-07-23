package com.gjmetal.app.ui.flash;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseEvent;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.event.FlashTabsEvent;
import com.gjmetal.app.manager.PushManager;
import com.gjmetal.app.model.flash.FlashChooseNoAllTag;
import com.gjmetal.app.model.my.User;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.app.widget.flowlayout.FlowDragLayout;
import com.gjmetal.app.widget.flowlayout.OnTagClickListener;
import com.gjmetal.app.widget.flowlayout.TagInfo;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.kit.KnifeKit;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 *  Description:
 *  资讯标签
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/9/25  10:17
 *
 */

public class FlashTabsActivity extends BaseActivity {
    @BindView(R.id.flow_use)
    FlowDragLayout mFlow_use;
    @BindView(R.id.flow_unuse)
    TagFlowLayout mFlow_unuse;
    @BindView(R.id.tvYesChose)
    TextView tvYesChose;
    @BindView(R.id.tvNoChose)
    TextView tvNoChose;
    @BindView(R.id.llTop)
    LinearLayout llTop;
    @BindView(R.id.scrollTop)
    ScrollView scrollTop;
    @BindView(R.id.vEmpty)
    EmptyView vEmpty;
    @BindView(R.id.tv_commit)
    TextView tvCommit;

    private List<FlashChooseNoAllTag> mUsList = new ArrayList<>();//使用
    private List<FlashChooseNoAllTag> mUnUsList = new ArrayList<>();//未使用
    private ArrayList<TagInfo> myTagInfos = new ArrayList<>();
    private TagAdapter mTabUnUserAdapter;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_flash_tabs);
        KnifeKit.bind(this);
        initTitleSyle(Titlebar.TitleSyle.LEFT_BTN, "订阅分类");
        tvYesChose.setText("我的分类");
        tvNoChose.setText("点击添加分类");

        mFlow_use.setOnTagClickListener(new OnTagClickListener() {
            @Override
            public void onTagClick(TagInfo tagInfo, int i) {
                mUnUsList.add(myTagInfos.get(i).childTagListBean);
                mUsList.remove(i);//实际已无用
                mTabUnUserAdapter.notifyDataChanged();
            }

            @Override
            public void onTagDelete(TagInfo tagInfo, int d) {

            }
        });
        getUncheckData();
        BusProvider.getBus().register(this);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void BaseEvent(BaseEvent baseEvent) {
        if (baseEvent.isLogin() && User.getInstance().isLoginIng()) {
            getUncheckData();
        }
    }
    @Override
    protected void fillData() {
    }

    //获取全部
    private void getUncheckData() {
        DialogUtil.waitDialog(this);
        Api.getFlashService().queryUserTag().
                compose(XApi.<BaseModel<List<FlashChooseNoAllTag>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<FlashChooseNoAllTag>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<FlashChooseNoAllTag>>>() {
                    @Override
                    public void onNext(BaseModel<List<FlashChooseNoAllTag>> listBaseModel) {
                        DialogUtil.dismissDialog();
                        if (scrollTop != null) {
                            scrollTop.setVisibility(View.VISIBLE);
                        }
                        if (tvCommit != null) {
                            tvCommit.setVisibility(View.VISIBLE);
                        }
                        if (vEmpty != null) {
                            vEmpty.setVisibility(View.GONE);
                        }
                        if (ValueUtil.isListEmpty(listBaseModel.getData())) {
                            if (scrollTop != null) {
                                scrollTop.setVisibility(View.GONE);
                            }
                            if (tvCommit != null) {
                                tvCommit.setVisibility(View.GONE);
                            }
                            vEmpty.setVisibility(View.VISIBLE);
                            vEmpty.setNoData(Constant.BgColor.WHITE);
                            return;
                        }

                        if (listBaseModel.getData() != null) {
                            for (int i = 0; i < listBaseModel.getData().size(); i++) {

                                if (listBaseModel.getData().get(i).isSelected()) {//已选标签
                                    mUsList.add(listBaseModel.getData().get(i));
                                } else {
                                    mUnUsList.add(listBaseModel.getData().get(i));
                                }
                            }
                            checkTabs();
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        GjUtil.showEmptyHint(context, Constant.BgColor.WHITE, error, vEmpty, new BaseCallBack() {
                            @Override
                            public void back(Object obj) {
                                getUncheckData();
                            }
                        }, scrollTop, tvCommit);
                        DialogUtil.dismissDialog();
                    }
                });
    }

    //检查标签
    private void checkTabs() {
        if (mFlow_unuse == null) {
            return;
        }
        mFlow_unuse.setAdapter(mTabUnUserAdapter = new TagAdapter<FlashChooseNoAllTag>(mUnUsList) {
            @Override
            public View getView(FlowLayout parent, int position, FlashChooseNoAllTag s) {
                LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.item_tab,
                        mFlow_unuse, false);
                TextView tv = linearLayout.findViewById(R.id.tv_tab);
                try {
                    if (ValueUtil.isNotEmpty(s) && ValueUtil.isStrNotEmpty(s.getTagName())) {
                        tv.setText(s.getTagName());
                    } else {
                        tv.setText("");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return linearLayout;
            }

            @Override
            public void onSelected(int position, View view) {
                super.onSelected(position, view);
                mUsList.add(mUnUsList.get(position));//实际已无用
                TagInfo tagInfo = new TagInfo();
                tagInfo.type = TagInfo.TYPE_TAG_USER;
                tagInfo.tagName = mUnUsList.get(position).getTagName();
                tagInfo.tagId = mUnUsList.get(position).getId() + "";
                tagInfo.setChildTagListBean(mUnUsList.get(position));
                if (mFlow_use != null) {
                    mFlow_use.addTag(tagInfo, true);
                }
                mUnUsList.remove(position);
                mTabUnUserAdapter.notifyDataChanged();
            }
        });
        updateMyTagUi();

    }

    //FlowDragLayout设置数据源
    private void updateMyTagUi() {
        myTagInfos.addAll(addTags(mUsList, TagInfo.TYPE_TAG_USER));
        mFlow_use.setTags(myTagInfos);
        initTagDrag();
    }

    public List<TagInfo> addTags(List<FlashChooseNoAllTag> stringArray, int type) {
        List<TagInfo> list = new ArrayList<>();
        TagInfo tagInfo;
        String name;
        if (stringArray != null && stringArray.size() > 0) {
            for (int i = 0; i < stringArray.size(); i++) {
                name = stringArray.get(i).getTagName();
                tagInfo = new TagInfo();
                tagInfo.type = type;
                tagInfo.tagName = name;
                tagInfo.tagId = stringArray.get(i).getId() + "";
                tagInfo.setChildTagListBean(stringArray.get(i));
                list.add(tagInfo);
            }
        }
        return list;
    }


    /**
     * 完成
     */
    @OnClick(R.id.tv_commit)
    public void commit() {
        DialogUtil.waitDialog(this);
        List<String> mIDList = new ArrayList<>();
        for (int i = 0; i < myTagInfos.size(); i++) {
            mIDList.add(myTagInfos.get(i).tagId);
        }

        Api.getFlashService().addUserFlashTag(mIDList)
                .compose(XApi.<BaseModel>getApiTransformer())
                .compose(XApi.<BaseModel>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel>() {
                    @Override
                    public void onNext(BaseModel listBaseModel) {
                        DialogUtil.dismissDialog();
                        if (listBaseModel.getCode().equals(Constant.ResultCode.SUCCESS.getValue())) {
                            FlashTabsEvent flashTabsEvent = new FlashTabsEvent();
                            flashTabsEvent.setFlash(true);
                            BusProvider.getBus().post(flashTabsEvent);
                            finish();
                        } else {
                            ToastUtil.showToast(listBaseModel.getErrorMsg());
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        DialogUtil.dismissDialog();
                        ToastUtil.showToast(ValueUtil.isStrNotEmpty(error.getMessage()) ? error.getMessage() : "提交失败");
                    }
                });
    }

    private void initTagDrag() {
        mFlow_use.enableDragAndDrop();
        mFlow_use.setIsEdit(true);
    }

    private void initTagDefault() {
        mFlow_use.setDefault();
        mFlow_use.setIsEdit(false);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getBus().unregister(this);

    }

}
