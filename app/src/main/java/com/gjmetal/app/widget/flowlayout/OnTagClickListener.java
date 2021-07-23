package com.gjmetal.app.widget.flowlayout;


/**
 *
 * Description:
 * 标签的点击接口
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/4/30  9:09
 *
 */
public interface OnTagClickListener {
    void onTagClick(TagInfo tagInfo,int i);

    void onTagDelete(TagInfo tagInfo,int d);
}
