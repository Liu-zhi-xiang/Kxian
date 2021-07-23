package com.gjmetal.app.adapter.spot.expand;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by hgh on 2018/3/30.
 */

public interface ExpandAdapterInterface {

    int getGroupCount();

    int getChildCount(int groupPosition);

    Object getGroup(int groupPosition);

    Object getChild(int groupPosition, int childPosition);

    View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent);

    View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent);


}
