package com.gjmetal.app.adapter.alphametal;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.util.SparseBooleanArray;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
/**
 *
 * Description  AlphaMetal Adapter
 * Author puyantao
 * Email 1067899750@qq.com
 * Date 2018-11-12 14:51
 */

public class MyHelperAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> mList_Fragment = new ArrayList<>();
    private SparseBooleanArray mList_Need_Update = new SparseBooleanArray();
    private FragmentManager mFragmentManager;
    private Context mContent;

    public MyHelperAdapter(Context context, FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        mFragmentManager = fm;
        mList_Need_Update.clear();
        mList_Fragment.clear();
        this.mContent = context;
        if (fragments != null) {
            this.mList_Fragment.addAll(fragments);
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position); //得到缓存的fragment

        Boolean update = mList_Need_Update.get(position);
        if (update != null && update) {
            String fragmentTag = fragment.getTag(); //得到tag，这点很重要
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.remove(fragment); //移除旧的fragment
            fragment = getItem(position); //换成新的fragment
            ft.add(container.getId(), fragment, fragmentTag); //添加新fragment时必须用前面获得的tag，这点很重要
            ft.attach(fragment);
            ft.commit();
            mList_Need_Update.put(position, false); //清除更新标记（只有重新启动的时候需要去创建新的fragment对象），防止正常情况下频繁创建对象
        }

        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
//        try {
//            if (ValueUtil.isNotEmpty(object)) {
//                container.removeView((View) object);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }

    }

    public List<Fragment> getListFragment() {
        return mList_Fragment;
    }

    public void setListFragment(List<Fragment> list_Fragment) {
//        if(list_Fragment != null){
//            FragmentTransaction ft = mFragmentManager.beginTransaction();
//            for (int i = 0; i < mList_Fragment.size(); i++) {
//                Fragment fragment = (Fragment) mList_Fragment.get(i);
//                ft.remove(fragment);
//            }
//            ft.commit();
//            ft = null;
//            mFragmentManager.executePendingTransactions();
//        }
        mList_Need_Update.clear();
        this.mList_Fragment.clear();
        if (list_Fragment != null) {
            this.mList_Fragment.addAll(list_Fragment);
        }
        notifyDataSetChanged();
    }

    public void setListNeedUpdate(List<Fragment> fragments) {
        mList_Fragment.clear();
        if (fragments != null) {
            mList_Fragment.addAll(fragments);
        }
        mList_Need_Update.clear();
        for (int i = 0; i < mList_Fragment.size(); i++) {
            mList_Need_Update.put(i, true);
        }
    }

    @Override
    public Fragment getItem(int position) {
        if (mList_Fragment.size() < position) {
            return null;
        }
        return mList_Fragment.get(position);
    }


    @Override
    public int getCount() {
        return mList_Fragment.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        super.restoreState(state, loader);

    }
}
