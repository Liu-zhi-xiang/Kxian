package com.gjmetal.app.ui.market.change;

import android.annotation.SuppressLint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.gjmetal.app.R;
import com.gjmetal.app.adapter.market.MyChooseAdapter;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseEvent;
import com.gjmetal.app.base.BaseFragment;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.manager.PushManager;
import com.gjmetal.app.model.market.RoomItem;
import com.gjmetal.app.ui.login.LoginActivity;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.GsonUtil;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.helper.OnStartDragListener;
import com.gjmetal.app.widget.helper.SimpleItemTouchHelperCallback;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.functions.Consumer;

/**
 * Description：已选
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-3-30 16:41
 */
public class HasChangeFragment extends BaseFragment implements OnStartDragListener {
    @BindView(R.id.lvDrag)
    RecyclerView lvDrag;
    @BindView(R.id.vEmpty)
    EmptyView vEmpty;
    private long beforeId=0;
    private int beforeSort=0;
    private ItemTouchHelper mItemTouchHelper;
    private List<RoomItem> mExChangesList;
    private MyChooseAdapter changeAdapter;
    private BaseCallBack baseCallBack;
    private boolean showClear=false;
    @Override
    protected int setRootView() {
        return R.layout.fragment_change_has;
    }

    public HasChangeFragment() {
    }

    @SuppressLint("ValidFragment")
    public HasChangeFragment(BaseCallBack baseCallBack) {
        this.baseCallBack = baseCallBack;
    }

    protected void initView() {
        //注册
        BusProvider.getBus().register(this);
        getFutures(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void BaseEvent(BaseEvent baseEvent) {
        if (baseEvent.isRefreshMyChoose() || baseEvent.isLogin()) {
            getFutures(false);
        }
    }


    private void initDragListView() {
        if (!isAdded()) {
            return;
        }
        changeAdapter = new MyChooseAdapter(getActivity(), this, mExChangesList, new MyChooseAdapter.ClickCallBack() {
            @Override
            public void onDelete(int index) {
                if (ValueUtil.isListEmpty(mExChangesList)) {
                    return;
                }
                List<Integer> longList = new ArrayList<>();
                longList.add(mExChangesList.get(index).getId());
                delFavoritesCode(longList);
            }

            @Override
            public void onToTop(int position) {
                resetSortFavoritesCode(mExChangesList.get(position).getId(), mExChangesList.get(0).getSort());
                //置顶
                RoomItem change = mExChangesList.remove(position);
                mExChangesList.add(0, change);
                changeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onMove(long id, int toSort) {
                if(beforeId==id&&beforeSort==toSort){
                    return;
                }
                resetSortFavoritesCode(id, toSort);
            }
        });

        lvDrag.setLayoutManager(new LinearLayoutManager(getActivity()));
        lvDrag.setHasFixedSize(true);
        lvDrag.setAdapter(changeAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(changeAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(lvDrag);
    }


    /**
     * 自选
     * @param showLoading
     */
    private void getFutures(final boolean showLoading) {
        if (showLoading) {
            DialogUtil.waitDialog(getContext());
        }
        Api.getMarketService().getFutures("future-quote")
                .compose(XApi.<BaseModel<List<RoomItem>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<RoomItem>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<RoomItem>>>() {
                    @Override
                    public void onNext(BaseModel<List<RoomItem>> listBaseModel) {
                        if (showLoading) {
                            DialogUtil.dismissDialog();
                        }
                        SharedUtil.clearData(Constant.CHANGE_DATA);
                        if(listBaseModel!=null&&ValueUtil.isListNotEmpty(listBaseModel.getData())){
                            showClear=true;
                            if (vEmpty != null) {
                                vEmpty.setVisibility(View.GONE);
                            }
                            if (lvDrag != null) {
                                lvDrag.setVisibility(View.VISIBLE);
                            }
                            mExChangesList = new ArrayList<>();
                            mExChangesList.addAll(listBaseModel.getData());
                            String strJson = GsonUtil.toJson(mExChangesList);
                            SharedUtil.put(Constant.CHANGE_DATA, Constant.HAS_CHNAGE_LIST, strJson);//保存选中数据
                            initDragListView();
                        }else {
                            showClear=false;
                            if (lvDrag != null) {
                                lvDrag.setVisibility(View.GONE);
                            }
                            GjUtil.clearHasChange();
                            if (vEmpty != null) {
                                vEmpty.setVisibility(View.VISIBLE);
                                vEmpty.showAddHint(Constant.BgColor.BLUE, R.mipmap.ic_future_add_nor, R.string.txt_add_my_change, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        baseCallBack.back(v);
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        if (showLoading) {
                            DialogUtil.dismissDialog();
                        }
                        GjUtil.showEmptyHint(getActivity(),Constant.BgColor.BLUE,error, vEmpty, new BaseCallBack() {
                            @Override
                            public void back(Object obj) {
                                getFutures(true);
                            }
                        });
                    }
                });
    }

    public boolean getShowClear(){
        return this.showClear;
    }

    /**
     * 排序
     *
     * @param id
     * @param sort
     */
    private void resetSortFavoritesCode(long id, int sort) {
        beforeId=id;
        beforeSort=sort;
        Api.getMarketService().resetSortFavoritesCode(id, sort)
                .compose(XApi.<BaseModel>getApiTransformer())
                .compose(XApi.<BaseModel>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel>() {
                    @Override
                    public void onNext(BaseModel listBaseModel) {
                        GjUtil.onRefreshMarket();
                        getFutures(false);
                    }
                    @SuppressWarnings("unchecked")
                    @Override
                    protected void onFail(NetError error) {
                        if (error != null && error.getType().equals(Constant.ResultCode.TOKEN_ERROR.getValue())) {
                            LoginActivity.launch(getActivity());
                            showClear=false;
                            if (lvDrag != null) {
                                lvDrag.setVisibility(View.GONE);
                            }
                            GjUtil.clearHasChange();
                            if (vEmpty != null) {
                                vEmpty.setVisibility(View.VISIBLE);
                                vEmpty.showAddHint(Constant.BgColor.BLUE, R.mipmap.ic_future_add_nor, R.string.txt_add_my_change, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        baseCallBack.back(v);
                                    }
                                });
                            }
                        }else {
                            GjUtil.showEmptyHint(getActivity(),Constant.BgColor.BLUE,error, vEmpty, new BaseCallBack() {
                                @Override
                                public void back(Object obj) {

                                }
                            },lvDrag);
                        }
                    }
                });
    }

    /**
     * 删除
     *
     * @param list
     */
    private void delFavoritesCode(final List<Integer> list) {
        DialogUtil.waitDialog(getActivity());
        Api.getMarketService().delFavoritesCode(list)
                .compose(XApi.<BaseModel>getApiTransformer())
                .compose(XApi.<BaseModel>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel>() {
                    @Override
                    public void onNext(BaseModel listBaseModel) {
                        GjUtil.onRefreshMarket();
                        GjUtil.onRefreshAllFuture();
                        ToastUtil.showToast(listBaseModel.getMessage());
                        getFutures(false);
                        DialogUtil.dismissDialog();

                    }

                    @Override
                    protected void onFail(NetError error) {
                        DialogUtil.dismissDialog();
                    }
                });
    }


    /**
     * 清空
     */
    private void delFavoritesCodeAll() {
        DialogUtil.waitDialog(getActivity());
        Api.getMarketService().delFavoritesCodeAll()
                .compose(XApi.<BaseModel>getApiTransformer())
                .compose(XApi.<BaseModel>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel>() {
                    @Override
                    public void onNext(BaseModel listBaseModel) {
                        DialogUtil.dismissDialog();
                        GjUtil.onRefreshMarket();
                        getFutures(false);
                        GjUtil.clearHasChange();
                        ToastUtil.showToast(listBaseModel.getMessage());
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToast(error.getMessage());
                        DialogUtil.dismissDialog();
                    }
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.getBus().unregister(this);
    }

    /**
     * 清空
     */
    public void clearAllChoose() {
        delFavoritesCodeAll();
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

}