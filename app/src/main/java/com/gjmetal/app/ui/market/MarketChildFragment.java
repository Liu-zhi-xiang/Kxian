package com.gjmetal.app.ui.market;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.adapter.market.FutureAdapter;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseEvent;
import com.gjmetal.app.base.BaseFragment;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.event.SocketEvent;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.manager.SocketManager;
import com.gjmetal.app.model.market.Future;
import com.gjmetal.app.model.market.MenuCheckState;
import com.gjmetal.app.model.market.RoomItem;
import com.gjmetal.app.model.my.User;
import com.gjmetal.app.model.socket.MarketSocketResult;
import com.gjmetal.app.ui.login.LoginActivity;
import com.gjmetal.app.ui.market.change.AddMarketTagActivity;
import com.gjmetal.app.util.AppUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.GsonUtil;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.MyScrollListener;
import com.gjmetal.app.widget.popuWindow.MarketPopWindow;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.log.XLog;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.gjmetal.app.manager.SocketManager.TAG;

/**
 * Description：子视图
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-3-28  17:14
 */

public class MarketChildFragment extends BaseFragment implements MyScrollListener.HideScrollListener {
    @BindView(R.id.rvFutureChild)
    RecyclerView rvFutureChild;
    @BindView(R.id.vEmpty)
    EmptyView vEmpty;
    @BindView(R.id.viewTab)
    View viewTab;//列表栏
    @BindView(R.id.tvFutureUpOrDown)
    TextView tvFutureUpOrDown;//成交量
    @BindView(R.id.tvFutureVolume)
    TextView tvFutureVolume;
    @BindView(R.id.tvFutureName)
    TextView tvFutureName;
    @BindView(R.id.tvFutureBestNew)
    TextView tvFutureBestNew;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.tvMyExChange)
    TextView tvMyExChange;
    @BindView(R.id.llExChange)
    LinearLayout llExChange;
    @BindView(R.id.vRight)
    View vRight;
    private int checkUpOrDown;//是否选中涨跌/涨幅
    private int checkVolume;//成交量/持仓量
    private List<Future.SubItem> subItemList;
    private FutureAdapter futureAdapter;
    private List<RoomItem> futures = new ArrayList<>();
    private int id;
    private int index = 0;
    private int parentIndex;//父的容器位置
    private LinearLayoutManager mLayoutManager;
    private MarketPopWindow marketPopWindow;
    private Future future;
    private String roomCode = null;
    private int mainPageSelected;
    private int marketIndex;
    //需要定位的地方，从小到大排列，需要和tab对应起来，长度一样
    private boolean isScrolled = false;
    private List<Integer> scorllPos = new ArrayList<>();
    private String type;
    private String name;
    private List<RoomItem> showList = new ArrayList<>();
    private List<String> roomList = new ArrayList<>();
    private int pageSize = AppUtil.getPageSize(46);//当前页显示多少条数据
    private int socketPushCount = 0;
    private boolean scrollSelected;
    private int pagePos;

    @Override
    protected int setRootView() {
        return R.layout.fragment_market_child;
    }

    @SuppressLint("ValidFragment")
    public MarketChildFragment(int parentIndex, int index, Future future, List<Future.SubItem> subItemList) {
        if (ValueUtil.isEmpty(future)) {
            return;
        }
        if (future.getId() == -1) {//自选
            type = Constant.MenuType.MINUS_ONE.getValue();
        } else {
            if (ValueUtil.isStrNotEmpty(future.getType())) {
                type = future.getType();
            }
        }
        if (ValueUtil.isStrEmpty(type)) {
            return;
        }
        this.future = future;
        this.parentIndex = parentIndex;
        this.index = index;
        this.subItemList = subItemList;
        id = future.getId();
        if (ValueUtil.isListNotEmpty(subItemList)) {//有二级菜单时
            name = subItemList.get(index).getName();
            roomCode = subItemList.get(index).getRoomCode();
            String defRoom = subItemList.get(0).getRoomCode();//第一个房间
            scorllPos.add(0);
            for (Future.SubItem bean : subItemList) {
                if (!defRoom.equals(bean.getRoomCode())) {
                    scorllPos.add(futures.size());
                }
                if (!bean.getType().equals(Constant.MenuType.FIVE.getValue())) {
                    if (bean.getRoomItem().size() > 0) {
                        for (RoomItem roomItem : bean.getRoomItem()) {
                            roomItem.setRoomCode(bean.getRoomCode());
                            futures.add(roomItem);
                        }
                    }
                }
            }

        } else {
            if (ValueUtil.isStrNotEmpty(future.getRoomCode())) {
                roomCode = future.getRoomCode();
            }
            if (future.getRoomItem().size() > 0) {
                for (RoomItem roomItem : future.getRoomItem()) {
                    roomItem.setRoomCode(roomCode);
                    this.futures.add(roomItem);
                }
            }

        }
        if (ValueUtil.isNotEmpty(future) && ValueUtil.isListNotEmpty(subItemList)) {
            AppAnalytics.getInstance().onEvent(getActivity(), "market_" + future.getId() + "_" + subItemList.get(0).getId() + "_click", "行情-各交易所-各品种-点击量");
        }

    }


    public MarketChildFragment() {
    }

    public void initView() {
        BusProvider.getBus().register(this);//注册EventBus
        if (type.equals(Constant.MenuType.FIVE.getValue())) {//利率
            vRight.setVisibility(View.VISIBLE);
            showLl();
            getRate();
        } else {
            vRight.setVisibility(View.GONE);
            GjUtil.setRightDrawable(getActivity(), tvFutureUpOrDown, R.mipmap.icon_market_change);
            GjUtil.setRightDrawable(getActivity(), tvFutureVolume, R.mipmap.icon_market_change);
            //成交量切换
            tvFutureUpOrDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tvFutureUpOrDown.getText().equals(getString(R.string.volume))) {
                        tvFutureUpOrDown.setText(getString(R.string.interest));
                        checkVolume = 1;
                    } else {
                        checkVolume = 0;
                        tvFutureUpOrDown.setText(getString(R.string.volume));
                    }
                    futureAdapter.changeItem(checkUpOrDown, checkVolume);
                    GjUtil.saveMarketMenuCheck(new MenuCheckState(String.valueOf(id), checkUpOrDown, checkVolume));
                }
            });

            //涨幅
            tvFutureVolume.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tvFutureVolume.getText().equals(getString(R.string.updownPercent))) {
                        checkUpOrDown = 0;
                        tvFutureVolume.setText(getString(R.string.upDown));
                    } else {
                        checkUpOrDown = 1;
                        tvFutureVolume.setText(getString(R.string.updownPercent));
                    }
                    futureAdapter.changeItem(checkUpOrDown, checkVolume);
                    GjUtil.saveMarketMenuCheck(new MenuCheckState(String.valueOf(id), checkUpOrDown, checkVolume));
                }
            });
        }
        mLayoutManager = new LinearLayoutManager(getContext());
        futureAdapter = new FutureAdapter(getActivity(), type, checkUpOrDown, checkVolume, future, new FutureAdapter.CallBackLongClickLister() {
            @Override
            public void OnLongClick(View view, final int position, final RoomItem bean) {
                marketPopWindow = new MarketPopWindow(getActivity(), view, false, new MarketPopWindow.OnClickListener() {
                    @Override
                    public void onTop() {//置顶
                        resetSortFavoritesCode(bean.getId(), futureAdapter.getDataSource().get(0).getSort());
                    }

                    @Override
                    public void onDelete() {
                        try {
                            List<Integer> longList = new ArrayList<>();
                            longList.add(bean.getId());
                            delFavoritesCode(longList, position);//删除
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onEdit() {

                    }
                });

            }
        });
        futureAdapter.setData(futures);
        addTabLayout();
        if (rvFutureChild == null) {
            return;
        }
        rvFutureChild.setLayoutManager(mLayoutManager);
        rvFutureChild.setAdapter(futureAdapter);
        if (type.equals(Constant.MenuType.MINUS_ONE.getValue())) {//自选
            initMyChooseScroll();
        } else {
            initCommon();
        }
        if (index == Constant.POSITION_0 && parentIndex == Constant.POSITION_0) {
            onRefresh();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void BaseEvent(BaseEvent baseEvent) {
        if (!isAdded()) {
            return;
        }
        if (baseEvent.isRefreshMarketMain()) {
            if (type.equals(Constant.MenuType.MINUS_ONE.getValue()) && User.getInstance().isLoginIng()) {//自选
                getFutures(true);
            }
        } else if (baseEvent.isStartMarketTimer()) {
            if (!isSelectedPage()) {
                return;
            }
            checkMenuState();
            if (type.equals(Constant.MenuType.MINUS_ONE.getValue()) && User.getInstance().isLoginIng()) {
                getFutures(false);
            } else {
                if (type.equals(Constant.MenuType.FIVE.getValue())) {
                    getRate();
                } else {
                    addRoom();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void SocketEvent(SocketEvent socketEvent) {
        if (!AppUtil.isActivityRunning(getActivity())) {
            return;
        }
        if (!isSelectedPage()) {
            return;
        }
        if (socketEvent.isConnectSuccess()) {//重连
            if (mainPageSelected == Constant.POSITION_0 && marketIndex == parentIndex && rvFutureChild != null) {
                tryRequest();
            }
        }
        if (socketEvent.isPush()) {//推送数据
            notifiySocketData(socketEvent);
        }
    }

    /**
     * 判断当前界面是否被选中
     *
     * @return
     */
    private boolean isSelectedPage() {
        marketIndex = SharedUtil.getInt(Constant.MARKET_PAGE_INDEX_1);
        mainPageSelected = SharedUtil.getInt(Constant.MAIN_PAGE_SELECTED);
//        XLog.d(TAG,"mainPageSelected="+mainPageSelected+"/marketIndex="+marketIndex);
        if (isAdded() && mainPageSelected == Constant.POSITION_0 && marketIndex == parentIndex && rvFutureChild != null && getUserVisibleHint()) {
            return true;
        }
        return false;
    }

    private void initCommon() {
        llExChange.setVisibility(View.GONE);
        rvFutureChild.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //0停止 ，12都是滑动
                int top = mLayoutManager.findFirstVisibleItemPosition();
                int bottom = mLayoutManager.findLastVisibleItemPosition();
                if (newState == 0) {
                    isScrolled = false;
                    if (ValueUtil.isListNotEmpty(futures)) {
                        if (ValueUtil.isListNotEmpty(showList)) {
                            showList.clear();
                        }
                        showList.addAll(futures.subList(top, bottom));
                    }
                    getVisiberViewRoom();
                } else {
                    isScrolled = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                try {
                    int top = mLayoutManager.findFirstVisibleItemPosition();
                    int bottom = mLayoutManager.findLastVisibleItemPosition();
                    if (tabLayout == null) {
                        return;
                    }
                    //这个主要是recyclerview滑动时让tab定位的方法
                    if (isScrolled) {
                        int pos = 0;
                        if (bottom == futures.size() - 1) {
                            //先判断滑到底部，tab定位到最后一个
                            pos = scorllPos.size() - 1;
                        } else if (top == scorllPos.get(scorllPos.size() - 1)) {
                            pos = tabLayout.getTabCount() - 2;
                        } else {
                            for (int i = 0; i < scorllPos.size() - 1; i++) {
                                if (top > scorllPos.get(i) && top < scorllPos.get(i + 1)) {
                                    pos = i;
                                    break;
                                } else if (top == scorllPos.get(i)) {
                                    pos = i;
                                    break;
                                }
                            }
                        }

                        if (pos != 0) {
                            if (pos == scorllPos.size() - 1) {
                                if (tabLayout.getTabAt(pos) != null) {
                                    tabLayout.getTabAt(pos).select();
                                }
                            }
                            tabLayout.setScrollPosition(pos, 0f, true);
                        } else {
                            if (scorllPos.size() == 2 && top == 0) {
                                if (tabLayout.getTabAt(pos) != null) {
                                    tabLayout.getTabAt(pos).select();
                                }
                            } else {
                                if (top != 0) {
                                    return;
                                }
                            }
                            tabLayout.setScrollPosition(pos, 0f, true);
                        }
                        if (pos != tabLayout.getSelectedTabPosition()) {
                            scrollSelected = true;
                            if (tabLayout.getTabAt(pos) != null) {
                                tabLayout.getTabAt(pos).select();
                            }
                        } else {
                            scrollSelected = false;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获取当前可见的房间号
     */
    private void getVisiberViewRoom() {
        if (ValueUtil.isListNotEmpty(subItemList)) {
            if (ValueUtil.isListNotEmpty(futures)) {
                if (ValueUtil.isListNotEmpty(showList)) {
                    String firstRoom = showList.get(0).getRoomCode();
                    if (ValueUtil.isListNotEmpty(roomList)) {
                        roomList.clear();
                        roomList.add(firstRoom);
                    }
                    for (RoomItem item : showList) {
                        if (ValueUtil.isStrNotEmpty(item.getRoomCode()) && !firstRoom.equals(item.getRoomCode())) {
                            firstRoom = item.getRoomCode();
                            roomList.add(item.getRoomCode());
                        }
                    }
                    String[] strings = new String[roomList.size()];
                    roomList.toArray(strings);
                    List<String> cacheList = SocketManager.getInstance().getRoomList();
                    if (ValueUtil.isListNotEmpty(cacheList) && ValueUtil.isListEquals(cacheList, roomList)) {
                        return;
                    }
                    SocketManager.getInstance().addRoom(roomList.toArray(strings));
                }
            }
        } else {
            SocketManager.getInstance().addRoom(future.getRoomCode());
        }

    }

    private void initMyChooseScroll() {
        rvFutureChild.addOnScrollListener(new MyScrollListener(this));
        rvFutureChild.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (llExChange == null) {
                            return;
                        }
                        int lastCompletelyVisibleItemPosition = mLayoutManager.findLastCompletelyVisibleItemPosition();
                        boolean b = lastCompletelyVisibleItemPosition < futureAdapter.getItemCount() - 1;
                        if (b) {
                            if (ValueUtil.isListNotEmpty(futures) && User.getInstance().isLoginIng()) {
                                llExChange.setVisibility(View.VISIBLE);
                            } else {
                                llExChange.setVisibility(View.GONE);
                            }
                        } else {
                            llExChange.setVisibility(View.GONE);
                        }
                    }
                });
    }

    @Override
    public void onHide() {
        // 隐藏动画--属性动画
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) llExChange.getLayoutParams();
        llExChange.animate().translationY(llExChange.getHeight() + layoutParams.bottomMargin).setInterpolator(new AccelerateInterpolator(3));
    }

    @Override
    public void onShow() {
        // 显示动画--属性动画
        llExChange.animate().translationY(0).setInterpolator(new DecelerateInterpolator(3));

    }

    //自选管理
    @OnClick(R.id.llExChange)
    public void myExChange() {
        if (User.getInstance().isLoginIng()) {
            if (futureAdapter == null) {
                return;
            }
            String strJson = GsonUtil.toJson(futureAdapter.getDataSource());
            SharedUtil.put(Constant.CHANGE_DATA, Constant.HAS_CHNAGE_LIST, strJson);//保存选中数据
            AddMarketTagActivity.launch(getActivity(), true);
        } else {
            GjUtil.closeMarketTimer();
            LoginActivity.launch(getActivity());
        }
    }

    /**
     * 取出本地选择菜单的标记状态
     */
    private void checkMenuState() {
        if (futureAdapter == null || tvFutureVolume == null || tvFutureUpOrDown == null) {
            return;
        }
        MenuCheckState menuCheckState = GjUtil.getMarketMenuCheck(id);
        if (type.equals(Constant.MenuType.FIVE.getValue())) {//利率
            showLl();
        } else {
            if (ValueUtil.isEmpty(menuCheckState)) {
                tvFutureUpOrDown.setText(getString(R.string.volume));//默认成交量
                tvFutureVolume.setText(getString(R.string.upDown));//默认涨跌
                checkUpOrDown = 0;
                checkVolume = 0;
                futureAdapter.changeItem(checkUpOrDown, checkVolume);
                GjUtil.saveMarketMenuCheck(new MenuCheckState(String.valueOf(id), checkUpOrDown, checkVolume));
            } else {
                //涨跌
                if (menuCheckState.getCheckUpOrDown() == 0) {
                    tvFutureVolume.setText(getString(R.string.upDown));
                    checkUpOrDown = 0;
                } else {
                    checkUpOrDown = 1;
                    tvFutureVolume.setText(getString(R.string.updownPercent));
                }
                //成交量
                if (menuCheckState.getCheckVolume() == 0) {
                    checkVolume = 0;
                    tvFutureUpOrDown.setText(getString(R.string.volume));
                } else {
                    checkVolume = 1;
                    tvFutureUpOrDown.setText(getString(R.string.interest));
                }
                futureAdapter.changeItem(checkUpOrDown, checkVolume);
            }
        }
    }

    /**
     * 利率
     */
    private void showLl() {
        GjUtil.setRightDrawable(getActivity(), tvFutureUpOrDown, null);
        GjUtil.setRightDrawable(getActivity(), tvFutureVolume, null);
        tvFutureVolume.setText(getString(R.string.upDown));
        tvFutureUpOrDown.setText(getString(R.string.updownPercent));
    }

    public void addTabLayout() {
        if (ValueUtil.isListEmpty(subItemList) || ValueUtil.isListNotEmpty(subItemList) && subItemList.size() == 1) {
            tabLayout.setVisibility(View.GONE);
            return;
        }
        tabLayout.setVisibility(View.VISIBLE);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (!isSelectedPage()) {
                    return;
                }
                if (scrollSelected) {
                    scrollSelected = false;
                    return;
                }
                int pos = tab.getPosition();
                if (type.equals(Constant.MenuType.FIVE.getValue())) {//利率
                    if (ValueUtil.isListNotEmpty(subItemList)) {
                        name = subItemList.get(pos).getName();
                    }
                    index = pos;
                } else {
                    if (ValueUtil.isListNotEmpty(scorllPos) && ValueUtil.isListNotEmpty(subItemList)) {
                        if (pos > scorllPos.size() || pos == scorllPos.size()) {
                            pos = scorllPos.size() - 1;
                        }
                        isScrolled = true;
                        mLayoutManager.scrollToPositionWithOffset(scorllPos.get(pos), 0);
                        index = pos;
                        int start = scorllPos.get(pos);
                        int end = scorllPos.get(pos) + pageSize;
                        if (ValueUtil.isListNotEmpty(roomList)) {
                            roomList.clear();
                        }
                        if (ValueUtil.isListNotEmpty(showList)) {
                            showList.clear();
                        }
                        if (futures.size() > end || futures.size() == end) {
                            showList.addAll(futures.subList(start, end));
                        } else {
                            showList.addAll(futures.subList(start, futures.size() - 1));
                        }
                        String firstRoom = showList.get(0).getRoomCode();
                        roomList.add(firstRoom);
                        for (RoomItem item : showList) {
                            if (ValueUtil.isStrNotEmpty(item.getRoomCode()) && !firstRoom.equals(item.getRoomCode())) {
                                firstRoom = item.getRoomCode();
                                roomList.add(item.getRoomCode());
                            }
                        }
                        String[] strings = new String[roomList.size()];
                        roomList.toArray(strings);
                        SocketManager.getInstance().addRoomForMarket(roomList.toArray(strings));
                        isScrolled = false;
                    }
                }
                if (type.equals(Constant.MenuType.FIVE.getValue())) {//利率
                    getRate();
                }
                AppAnalytics.getInstance().onEvent(getActivity(), "market_" + future.getId() + "_" + subItemList.get(index).getId() + "_click", "行情-各交易所-各品种-点击量");
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        ArrayList<String> tabList = new ArrayList<>();
        for (Future.SubItem bean : subItemList) {
            tabList.add(bean.getName());
            tabLayout.addTab(tabLayout.newTab().setText(bean.getName()));
        }
        tabLayout.getTabAt(0).select();
        tabLayout.setTabRippleColor(ColorStateList.valueOf(getContext().getResources().getColor(R.color.transparent)));
    }


    @Override
    public void onResume() {
        super.onResume();
        pagePos = SharedUtil.getInt(Constant.MAIN_PAGE_SELECTED);
    }

    /**
     * 显示刷新列表
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            mainPageSelected = SharedUtil.getInt(Constant.MAIN_PAGE_SELECTED);
            if (mainPageSelected == Constant.POSITION_0 && rvFutureChild != null) {
                onRefresh();
            }
        } else {
            if (marketPopWindow != null && marketPopWindow.isShowing()) {
                marketPopWindow.dismiss();
            }
        }
    }

    private void onRefresh() {
        if (!isSelectedPage()) {
            return;
        }
        socketPushCount = 0;
        XLog.e(SocketManager.TAG, "刷新------------roomCode=" + roomCode + "/type=" + type);
        if (type.equals(Constant.MenuType.MINUS_ONE.getValue())) {//自选
            getFutures(false);
        } else if (type.equals(Constant.MenuType.FIVE.getValue())) {//利率
            getRate();
        } else {
            getFirstContractList();
        }

    }

    /**
     * 利率
     */
    private void getRate() {
        Api.getMarketService().getRate(name)
                .compose(XApi.<BaseModel<List<RoomItem>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<RoomItem>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<RoomItem>>>() {
                    @Override
                    public void onNext(BaseModel<List<RoomItem>> listBaseModel) {
                        if (viewTab == null || rvFutureChild == null || vEmpty == null) {
                            return;
                        }
                        viewTab.setVisibility(ValueUtil.isListNotEmpty(listBaseModel.getData()) ? View.VISIBLE : View.GONE);
                        if (ValueUtil.isListEmpty(listBaseModel.getData())) {
                            rvFutureChild.setVisibility(View.GONE);
                            if (ValueUtil.isListNotEmpty(futures)) {
                                futures.clear();
                            }
                            myChooseDataView();
                        } else {
                            rvFutureChild.setVisibility(View.VISIBLE);
                            vEmpty.setVisibility(View.GONE);
                            if (ValueUtil.isListNotEmpty(futures)) {
                                futures.clear();
                            }
                            futures.addAll(listBaseModel.getData());
                            futureAdapter.setData(futures);
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        GjUtil.checkActState(getActivity());
                        if (error != null && error.getType().equals(Constant.ResultCode.TOKEN_ERROR.getValue())) {
                            LoginActivity.launch(getActivity());
                            myChooseDataView();
                        } else {
                            showAgainLoad(error);
                        }

                    }
                });
    }

    /**
     * 自选
     */
    private void getFutures(boolean againLoad) {
        if (User.getInstance().isLoginIng()) {
            if (againLoad) {//是否需要重新加载
                loadMyFutures();
            } else {
                if (ValueUtil.isListEmpty(futures) || ValueUtil.isStrEmpty(roomCode)) {
                    loadMyFutures();
                } else {
                    SocketManager.getInstance().addRoom(roomCode);
                }
            }

        } else {
            myChooseDataView();
        }
    }

    private void loadMyFutures() {
        Api.getMarketService().getFutures("future-quote")
                .compose(XApi.<BaseModel<List<RoomItem>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<RoomItem>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<RoomItem>>>() {
                    @Override
                    public void onNext(BaseModel<List<RoomItem>> listBaseModel) {

                        initMyChooseScroll();
                        GjUtil.checkActState(getActivity());
                        if (viewTab == null || rvFutureChild == null || vEmpty == null) {
                            return;
                        }
                        viewTab.setVisibility(ValueUtil.isListNotEmpty(listBaseModel.getData()) ? View.VISIBLE : View.GONE);
                        if (ValueUtil.isListEmpty(listBaseModel.getData())) {
                            rvFutureChild.setVisibility(View.GONE);
                            if (ValueUtil.isListNotEmpty(futures)) {
                                futures.clear();
                            }
                            myChooseDataView();
                        } else {
                            rvFutureChild.setVisibility(View.VISIBLE);
                            vEmpty.setVisibility(View.GONE);
                            if (ValueUtil.isListNotEmpty(futures)) {
                                futures.clear();
                            }
                            futures.addAll(listBaseModel.getData());
                            futureAdapter.setData(futures);

                            if (!isSelectedPage()) {
                                return;
                            }
                            roomCode = futures.get(0).getRoomCode();
                            if (futures.size() > pageSize || futures.size() == pageSize) {
                                showList.addAll(futures.subList(0, pageSize));
                            } else {
                                showList.addAll(futures);
                            }
                            String firstRoom = showList.get(0).getRoomCode();
                            roomList.add(firstRoom);
                            SocketManager.getInstance().addRoom(roomCode);
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        GjUtil.checkActState(getActivity());
                        if (ValueUtil.isListEmpty(futures)) {
                            if (error != null && error.getType().equals(Constant.ResultCode.TOKEN_ERROR.getValue())) {
                                LoginActivity.launch(getActivity());
                                myChooseDataView();
                            } else {
                                showAgainLoad(error);
                            }
                        }
                    }
                });
    }


    /**
     * 第一次获取合约列表名字,然后连接Socket
     */
    private void getFirstContractList() {
        if (viewTab == null || rvFutureChild == null || vEmpty == null || futureAdapter == null) {
            return;
        }
        if (ValueUtil.isListEmpty(futures)) {
            viewTab.setVisibility(View.GONE);
            rvFutureChild.setVisibility(View.GONE);
            vEmpty.setVisibility(View.VISIBLE);
            vEmpty.setNoData(Constant.BgColor.BLUE);
        } else {
            vEmpty.setVisibility(View.GONE);
            viewTab.setVisibility(View.VISIBLE);
            rvFutureChild.setVisibility(View.VISIBLE);
            futureAdapter.setData(futures);
            //加入当前可见item的房间
            if (ValueUtil.isListNotEmpty(futures)) {
                if (ValueUtil.isListNotEmpty(roomList)) {
                    String[] strings = new String[roomList.size()];
                    roomList.toArray(strings);
                    SocketManager.getInstance().addRoom(roomList.toArray(strings));
                } else {
                    if (futures.size() > pageSize || futures.size() == pageSize) {
                        showList.addAll(futures.subList(0, pageSize));
                    } else {
                        showList.addAll(futures);
                    }
                    String firstRoom = showList.get(0).getRoomCode();
                    roomList.add(firstRoom);
                    for (RoomItem item : showList) {
                        if (ValueUtil.isStrNotEmpty(item.getRoomCode()) && !firstRoom.equals(item.getRoomCode())) {
                            firstRoom = item.getRoomCode();
                            roomList.add(item.getRoomCode());
                        }
                    }
                    String[] strings = new String[roomList.size()];
                    roomList.toArray(strings);
                    SocketManager.getInstance().addRoom(roomList.toArray(strings));
                }

            }
        }
    }


    /**
     * 重试
     */
    private void tryRequest() {
        if (ValueUtil.isStrNotEmpty(type) && type.equals(Constant.MenuType.FIVE.getValue()) || type.equals(Constant.MenuType.MINUS_ONE.getValue())) {//利率、自选
            if (type.equals(Constant.MenuType.MINUS_ONE.getValue()) && User.getInstance().isLoginIng()) {
                if (ValueUtil.isListEmpty(futures)) {
                    getFutures(true);
                } else {
                    addRoom();
                }
            } else if (type.equals(Constant.MenuType.FIVE.getValue())) {
                getRate();
            }
        } else {
            addRoom();
        }
    }

    /**
     * 加入房间
     */
    private void addRoom() {
        if (!isSelectedPage() || !getUserVisibleHint()) {
            return;
        }
        if (viewTab == null || rvFutureChild == null || vEmpty == null || roomCode == null) {
            return;
        }
        if (ValueUtil.isListNotEmpty(futures)) {
            viewTab.setVisibility(View.VISIBLE);
            rvFutureChild.setVisibility(View.VISIBLE);
            vEmpty.setVisibility(View.GONE);
            if (ValueUtil.isListNotEmpty(roomList)) {
                String[] strings = new String[roomList.size()];
                roomList.toArray(strings);
                SocketManager.getInstance().addRoom(roomList.toArray(strings));
            } else {
                SocketManager.getInstance().addRoom(roomCode);
            }
        } else {
            if (type.equals(Constant.MenuType.MINUS_ONE.getValue())) {
                myChooseDataView();
            } else {
                viewTab.setVisibility(View.GONE);
                rvFutureChild.setVisibility(View.GONE);
                vEmpty.setVisibility(View.VISIBLE);
                vEmpty.setNoData(Constant.BgColor.BLUE);
            }
        }
    }


    /**
     * 实时接收Socket推送数据，更新界面
     *
     * @param socketEvent
     */
    private void notifiySocketData(SocketEvent socketEvent) {
        socketPushCount++;
//        XLog.d(TAG, "socket 推送次数-------" + future.getName() + "/" + socketPushCount);
        if(Constant.IS_TEST){
            if (ValueUtil.isStrNotEmpty(future.getName()) && future.getName().equals("主力合约")) {
                SharedUtil.putInt(Constant.SOCKET_PUSH_COUNT, socketPushCount);
            }
        }
        if (isScrolled) {
            return;
        }
        String resultJson = socketEvent.getJsonArray()[0].toString();
        Gson gson = new Gson();
        MarketSocketResult socketResult = gson.fromJson(resultJson, MarketSocketResult.class);
        if (ValueUtil.isEmpty(socketResult) || ValueUtil.isStrEmpty(socketResult.getRoom())) {
            return;
        }
        if (ValueUtil.isListEmpty(roomList) || ValueUtil.isListEmpty(futures)) {
            return;
        }
        if (!roomList.contains(socketResult.getRoom().toLowerCase()) && !roomList.contains(socketResult.getRoom())) {
            return;
        }
        RoomItem futureItem = socketResult.getData();
        //判断推送合约是否在码表里
        for (int i = 0; i < futures.size(); i++) {
            RoomItem item = futures.get(i);
            if (ValueUtil.isStrNotEmpty(futureItem.getContract()) && ValueUtil.isStrNotEmpty(item.getContract()) && item.getContract().equals(futureItem.getContract())) {
                if (ValueUtil.isStrNotEmpty(item.getName())) {
                    if (item.getLast().equals(futureItem.getLast()) && item.getUpdown().equals(futureItem.getUpdown()) && item.getPercent().equals(futureItem.getPercent()) && item.getVolume().equals(futureItem.getVolume()) && item.getInterest().equals(futureItem.getInterest()) && item.getUpLimit().equals(futureItem.getUpLimit()) && item.getLoLimit().equals(futureItem.getLoLimit())) {
                        return;
                    }
                    if (ValueUtil.isStrNotEmpty(item.getLast()) && !item.getLast().equals(futureItem.getLast()) && !item.getLast().equals("- -") && ValueUtil.isStrNotEmpty(futureItem.getLast()) && !futureItem.getLast().equals("- -")) {
                        item.setState(1);
                    } else {
                        item.setState(null);
                    }
                    item.setLast(futureItem.getLast());
                    item.setPercent(futureItem.getPercent());
                    item.setVolume(futureItem.getVolume());
                    item.setInterest(futureItem.getInterest());
                    item.setUpLimit(futureItem.getUpLimit());
                    item.setLoLimit(futureItem.getLoLimit());
                    item.setUpdown(futureItem.getUpdown());
                    futures.set(i, item);
                }
                rvFutureChild.getItemAnimator().setChangeDuration(0);
                futureAdapter.notifyItemChanged(i);
                //最新数据波动背景变色
                int finalI = i;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (ValueUtil.isListNotEmpty(futures)) {
                            item.setState(null);
                            futures.set(finalI, item);
                            futureAdapter.notifyItemChanged(finalI);
                        }
                    }
                }, 300);
                break;
            }
        }
    }

    /**
     * 自选管理
     */
    private void myChooseDataView() {
        if (ValueUtil.isListNotEmpty(futures)) {
            return;
        }
        viewTab.setVisibility(View.GONE);
        rvFutureChild.setVisibility(View.GONE);
        llExChange.setVisibility(View.GONE);
        vEmpty.setVisibility(View.VISIBLE);
        vEmpty.showAddHint(Constant.BgColor.BLUE, R.mipmap.ic_future_add_nor, R.string.txt_add_my_change, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (User.getInstance().isLoginIng()) {
                    AddMarketTagActivity.launch(getActivity(), false);
                } else {
                    LoginActivity.launch((Activity) getContext());
                }
            }
        });

    }


    /**
     * 加载失败
     *
     * @param error
     */
    private void showAgainLoad(NetError error) {
        GjUtil.showEmptyHint(getActivity(), Constant.BgColor.BLUE, error, vEmpty, new BaseCallBack() {
            @Override
            public void back(Object obj) {
                onRefresh();
            }
        }, rvFutureChild, viewTab);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.getBus().unregister(this);
    }

    /**
     * 排序
     *
     * @param id
     * @param sort
     */
    private void resetSortFavoritesCode(long id, int sort) {
        Api.getMarketService().resetSortFavoritesCode(id, sort)
                .compose(XApi.<BaseModel>getApiTransformer())
                .compose(XApi.<BaseModel>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel>() {
                    @Override
                    public void onNext(BaseModel listBaseModel) {
                        getFutures(true);
                    }

                    @Override
                    protected void onFail(NetError error) {
                    }
                });
    }

    /**
     * 删除
     *
     * @param list
     */
    private void delFavoritesCode(List<Integer> list, int position) {
        Api.getMarketService().delFavoritesCode(list)
                .compose(XApi.<BaseModel>getApiTransformer())
                .compose(XApi.<BaseModel>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel>() {
                    @Override
                    public void onNext(BaseModel listBaseModel) {
                        if (futureAdapter.getDataSource().size() == 1) {
                            getFutures(true);
                        } else {
                            futureAdapter.removeElement(position);
                            futures.remove(position);
                        }
                        futureAdapter.notifyDataSetChanged();
                        initMyChooseScroll();
                    }

                    @Override
                    protected void onFail(NetError error) {
                        if (error != null && error.getType().equals(Constant.ResultCode.TOKEN_ERROR.getValue())) {
                            LoginActivity.launch(getActivity());
                        }
                    }
                });
    }

}
