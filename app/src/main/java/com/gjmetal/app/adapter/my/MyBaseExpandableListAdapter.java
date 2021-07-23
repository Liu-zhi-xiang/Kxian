package com.gjmetal.app.adapter.my;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.model.my.WarnGroup;
import com.gjmetal.app.model.my.WarnItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Description Wraning数据源
 * Author puyantao
 * Email 1067899750@qq.com
 * Date 2018-11-19 10:55
 */

public class MyBaseExpandableListAdapter extends BaseExpandableListAdapter {
    private ArrayList<WarnGroup> mGroupDatas;
    private List<WarnItem> mChildItems;
    private Context mContext;
    private OnDeleteItemListener mOnDeleteItemListener;

    public MyBaseExpandableListAdapter(Context mContext, ArrayList<WarnGroup> groups) {
        this.mContext = mContext;
        this.mGroupDatas = groups;
    }

    @Override
    public int getGroupCount() {
        return mGroupDatas.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mGroupDatas.get(groupPosition).getWarnItems().size();
    }

    @Override
    public WarnGroup getGroup(int groupPosition) {
        return mGroupDatas.get(groupPosition);
    }

    @Override
    public WarnItem getChild(int groupPosition, int childPosition) {
        return mGroupDatas.get(groupPosition).getWarnItems().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    //取得用于显示给定分组的视图. 这个方法仅返回分组的视图对象
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        ViewHolderGroup groupHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.wraning_hander_item, parent, false);
            groupHolder = new ViewHolderGroup();
            groupHolder.tvWarnHander = convertView.findViewById(R.id.tvWarnHander);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (ViewHolderGroup) convertView.getTag();
        }
        groupHolder.tvWarnHander.setText(mGroupDatas.get(groupPosition).getgName());
        return convertView;
    }

    //取得显示给定分组给定子位置的数据用的视图
    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolderItem itemHolder;
        mChildItems = mGroupDatas.get(groupPosition).getWarnItems();
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.warning_item, parent, false);
            itemHolder = new ViewHolderItem();
            itemHolder.ivWraningItem = convertView.findViewById(R.id.ivWraningItem);
            itemHolder.tvWarningItem = convertView.findViewById(R.id.tvWarningItem);
            itemHolder.rlWarnLine = convertView.findViewById(R.id.rlWarnLine);
            convertView.setTag(itemHolder);
        } else {
            itemHolder = (ViewHolderItem) convertView.getTag();
        }
        if (childPosition == mChildItems.size() - 1) {
            itemHolder.rlWarnLine.setVisibility(View.VISIBLE);
        } else {
            itemHolder.rlWarnLine.setVisibility(View.GONE);
        }
        itemHolder.tvWarningItem.setText(mChildItems.get(childPosition).getiName());
        itemHolder.ivWraningItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnDeleteItemListener != null) {
                    mOnDeleteItemListener.onDeleteItem(v, groupPosition, childPosition);
                }
            }
        });
        return convertView;
    }

    //设置子列表是否可选中
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    private static class ViewHolderGroup {
        private TextView tvWarnHander;
    }

    private static class ViewHolderItem {
        private RelativeLayout ivWraningItem;
        private TextView tvWarningItem;
        private RelativeLayout rlWarnLine;
    }

    public void addDatas(ArrayList<WarnGroup> groupDatas) {
        mGroupDatas.clear();
        mGroupDatas.addAll(groupDatas);
        notifyDataSetChanged();
    }


    public void setDeleteItem(OnDeleteItemListener deleteItemListener) {
        if (deleteItemListener != null) {
            this.mOnDeleteItemListener = deleteItemListener;
        }
    }

    public interface OnDeleteItemListener {
        void onDeleteItem(View view, int groupPostion, int childPostion);
    }

}














