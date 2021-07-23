package com.gjmetal.app.ui.alphametal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.gjmetal.app.base.BaseFragment;
import com.gjmetal.star.log.XLog;


/**
 *
 * Description: 懒加载Fragment
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/5/27  13:59
 *
 */

public abstract class DelayerFragment extends BaseFragment {
    private boolean isFragmentVisible;
    private boolean isFirstVisible;
    private View rootView;
    protected String name;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariable();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //如果setUserVisibleHint()在rootView创建前调用时，那么
        //就等到rootView创建完后才回调onFragmentVisibleChange(true)
        //保证onFragmentVisibleChange()的回调发生在rootView创建完成之后，以便支持ui操作
        if (rootView == null) {
            rootView = view;
            if (getUserVisibleHint()) {
                if (isFirstVisible){
                    onFragmentFirstVisible();
                    isFirstVisible = false;
                }
            }
        }
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume(){
        super.onResume();
        if (getUserVisibleHint()&&!isFragmentVisible){
            onFragmentVisibleChange(true);
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (isFragmentVisible)
        onFragmentVisibleChange(false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //setUserVisibleHint()有可能在fragment的生命周期外被调用
        if (rootView == null) {
            return;
        }
        if (isFirstVisible && isVisibleToUser) {
            onFragmentFirstVisible();
            isFirstVisible = false;
        }
        if (isVisibleToUser&&!isFragmentVisible) {
            onFragmentVisibleChange(true);
            return;
        }
        if (isFragmentVisible&&!isVisibleToUser) {
            onFragmentVisibleChange(false);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        initVariable();
    }
    private void initVariable() {
        isFirstVisible = true;
        isFragmentVisible = false;
        rootView = null;
    }


    /**

     * Fragment显示与隐藏，
     *
     * @param isVisible true  不可见 -> 可见
     *                  false 可见  -> 不可见
     */
    protected int indexs=0;
    protected void onFragmentVisibleChange(boolean isVisible)
    {
        isFragmentVisible=isVisible;
        XLog.e("Fragment",this.getClass().getSimpleName()+"--------Change-------"+indexs+"--------"+isFragmentVisible+"==="+name);
    }

    /**
     * 在fragment首次可见时回调，可在这里进行加载数据，保证只在第一次打开Fragment时才会加载数据，
     *
     */
    protected void onFragmentFirstVisible() {
        XLog.e("Fragment",this.getClass().getSimpleName()+"+++++++isFragmentVisible++++++"+isFirstVisible+"==="+name);
    }

    protected boolean isFragmentVisible() {
        return isFragmentVisible;
    }
}
