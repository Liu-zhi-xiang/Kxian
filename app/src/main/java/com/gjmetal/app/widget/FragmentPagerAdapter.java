package com.gjmetal.app.widget;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.gjmetal.star.log.XLog;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Description：
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-5-25 9:09
 */
public abstract class FragmentPagerAdapter extends PagerAdapter {
    private static final String TAG = "FragmentStatePagerAdapter";
    private final FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction = null;
    //    private ArrayList<Fragment.SavedState> mSavedState = new ArrayList();
//    private ArrayList<Fragment> mFragments = new ArrayList();
    private Map<Integer,Fragment.SavedState> mSavedState = new HashMap<>();
    private Map<Integer,Fragment> mFragments = new HashMap<>();
    private Fragment mCurrentPrimaryItem = null;

    public FragmentPagerAdapter(FragmentManager fm) {
        this.mFragmentManager = fm;
    }

    public abstract Fragment getItem(int var1);

    @Override
    public void startUpdate(ViewGroup container) {
    }

    public Fragment getFragment(int position){
        return this.mFragments.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment;
        if(this.mFragments.containsKey(position)) {
            fragment = this.mFragments.get(position);
            if(fragment != null) {
                return fragment;
            }
        }

        if(this.mCurTransaction == null) {
            this.mCurTransaction = this.mFragmentManager.beginTransaction();
        }

        fragment = this.getItem(position);
        if(this.mSavedState.containsKey(position)) {
            Fragment.SavedState fss = this.mSavedState.get(position);
            if(fss != null) {
                fragment.setInitialSavedState(fss);
            }
        }

//        while(this.mFragments.size() <= position) {
//            this.mFragments.add(null);
//        }

        fragment.setMenuVisibility(false);
        fragment.setUserVisibleHint(false);
        this.mFragments.put(position, fragment);
        this.mCurTransaction.add(container.getId(), fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment)object;
        if(this.mCurTransaction == null) {
            this.mCurTransaction = this.mFragmentManager.beginTransaction();
        }

//        while(this.mSavedState.size() <= position) {
//            this.mSavedState.add(null);
//        }

        this.mSavedState.put(position, this.mFragmentManager.saveFragmentInstanceState(fragment));
        this.mFragments.put(position, null);
        this.mCurTransaction.remove(fragment);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment)object;
        if(fragment != this.mCurrentPrimaryItem) {
            if(this.mCurrentPrimaryItem != null) {
                this.mCurrentPrimaryItem.setMenuVisibility(false);
                this.mCurrentPrimaryItem.setUserVisibleHint(false);
            }

            if(fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }

            this.mCurrentPrimaryItem = fragment;
        }

    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if(this.mCurTransaction != null) {
            this.mCurTransaction.commitAllowingStateLoss();
            this.mCurTransaction = null;
            this.mFragmentManager.executePendingTransactions();
        }

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment)object).getView() == view;
    }

    @Override
    public Parcelable saveState() {
        Bundle state = null;
        if(this.mSavedState.size() > 0) {
            state = new Bundle();
//            Fragment.SavedState[] i = new Fragment.SavedState[this.mSavedState.size()];
//            this.mSavedState.toArray(i);
//            this.mSavedState.values().toArray(i);
            Iterator<Integer> it = this.mSavedState.keySet().iterator();
            while(it.hasNext()){
                int i = it.next();
                state.putParcelable("s"+i,this.mSavedState.get(i));
            }
//            state.putParcelableArray("states", i);
        }

        Iterator<Integer> fit = this.mFragments.keySet().iterator();
        while (fit.hasNext()) {
            int var5 = fit.next();
            Fragment f = this.mFragments.get(var5);
            if(f != null) {
                if(state == null) {
                    state = new Bundle();
                }

                String key = "f" + var5;
                this.mFragmentManager.putFragment(state, key, f);
            }
        }

        return state;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        if(state != null) {
            Bundle bundle = (Bundle)state;
            bundle.setClassLoader(loader);
//            Parcelable[] fss = bundle.getParcelableArray("states");
            this.mSavedState.clear();
            this.mFragments.clear();
//            if(fss != null) {
//                for(int keys = 0; keys < fss.length; ++keys) {
//                    this.mSavedState.add((Fragment.SavedState)fss[keys]);
//                }
//            }

            Iterator<String> si = bundle.keySet().iterator();
            while (si.hasNext()){
                String skey = si.next();
                if(skey.startsWith("s")){
                    int p = Integer.parseInt(skey.substring(1));
                    this.mSavedState.put(p, (Fragment.SavedState) bundle.getParcelable(skey));
                }
            }

            Set var10 = bundle.keySet();
            Iterator i$ = var10.iterator();

            while(true) {
                while(true) {
                    String key ;
                    do {
                        if(!i$.hasNext()) {
                            return;
                        }

                        key = (String)i$.next();
                    } while(!key.startsWith("f"));

                    int index = Integer.parseInt(key.substring(1));
                    Fragment f = this.mFragmentManager.getFragment(bundle, key);
                    if(f != null) {
//                        while(this.mFragments.size() <= index) {
//                            this.mFragments.add(null);
//                        }

                        f.setMenuVisibility(false);
                        this.mFragments.put(index, f);
                    } else {
                        XLog.d("Bad fragment at key " + key);
                    }
                }
            }
        }
    }
}
