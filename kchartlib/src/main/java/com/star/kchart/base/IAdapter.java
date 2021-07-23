package com.star.kchart.base;

import android.database.DataSetObserver;

import java.util.Date;


/**
 * 数据适配器
 *
 */

public interface IAdapter {
    /**
     * 获取点的数目
     *
     * @return
     */
    int getCount();

    /**
     * 通过序号获取item
     *
     * @param position 对应的序号
     * @return 数据实体
     */
    Object getItem(int position);

    /**
     * 通过序号获取时间
     *
     * @param position
     * @return
     */
    Date getDate(int position);

    /**
     * 获取交易日
     * @param position
     * @return
     */
    Date getTradeDate(int position);


    /**
     * 注册一个数据观察者
     *
     * @param observer 数据观察者
     */
    void registerDataSetObserver(DataSetObserver observer);

    /**
     * 移除一个数据观察者
     *
     * @param observer 数据观察者
     */
    void unregisterDataSetObserver(DataSetObserver observer);

    /**
     * 当数据发生变化时调用
     */
    void notifyDataSetChanged();
}
