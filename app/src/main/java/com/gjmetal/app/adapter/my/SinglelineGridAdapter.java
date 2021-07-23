package com.gjmetal.app.adapter.my;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.gjmetal.app.R;


import java.util.List;

import cn.finalteam.galleryfinal.model.PhotoInfo;


public class SinglelineGridAdapter extends BaseAdapter {
	private List<PhotoInfo> mList;
	private LayoutInflater inflater;
	private int mScreenWidth;
	private Context context;
	private static  int maxSize=9;
	OnClickDeleteListener onClickDeleteListener;
	public interface  OnClickDeleteListener{
		void delete(int x);
	}

	public SinglelineGridAdapter(Context context, int maxSize, List<PhotoInfo> mList) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		SinglelineGridAdapter.maxSize =maxSize;
		this.mList = mList;
	}

	public void setOnClickDeleteListener(OnClickDeleteListener onClickDeleteListener) {
		this.onClickDeleteListener = onClickDeleteListener;
	}

	public void setmList(List<PhotoInfo> mList) {
		this.mList = mList;

	}

	public int getCount() {

		if (mList!=null){
			if(mList.size() == maxSize){
				return maxSize;
			}
			return (mList.size()+1 );
		}
			return 1;

	}

	public PhotoInfo getItem(int arg0) {
		return mList.get(arg0);
	}

	public long getItemId(int arg0) {
		return arg0;
	}
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

			convertView = inflater.inflate(R.layout.item_single_line_grid,
					parent, false);
			holder = new ViewHolder();
			holder.image = convertView
					.findViewById(R.id.item_grida_image);
			holder.delete=convertView.findViewById(R.id.ivDelete);
			holder.delete.setVisibility(View.GONE);
			if (position < mList.size()){
				Log.e("Aaaaa", "apapter" + position);
				PhotoInfo mediaBean = mList.get(position);
				String path = null;
					path = mediaBean.getPhotoPath();
					if (mediaBean!=null) {
//				Picasso.with(context).invalidate(new File(path.toString()));
				Glide.with(context)
						.load(path)
						.centerCrop()
						.into(holder.image);
						holder.delete.setVisibility(View.VISIBLE);
					}
				holder.delete.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mList.remove(position);
						if (onClickDeleteListener!=null)
							onClickDeleteListener.delete(position);
						notifyDataSetChanged();
					}
				});
			}
		Log.e("aaaa","position=="+position+"size"+mList.size());
		return convertView;
	}

	public class ViewHolder {
		public ImageView image,delete;
	}


}