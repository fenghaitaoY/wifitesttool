package com.example.wifitest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PopupWindowAdapter extends BaseAdapter {

    private List<String> mData;
    private Context mContext;
    private OnRemoveclickListener mListener;

    public PopupWindowAdapter(Context context, List<String> list){
        mContext = context;
        mData = list;
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.popup_list_item, null);
            holder.textView = convertView.findViewById(R.id.pop_tv);
            holder.imageView = convertView.findViewById(R.id.pop_img);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView.setText(mData.get(position));
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "shanchu", Toast.LENGTH_SHORT).show();
                mData.remove(position);
                mListener.onSuccess(position);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }


    interface OnRemoveclickListener{
        void onSuccess(int position);
    }


    public void setOnRemoveClickListen(OnRemoveclickListener listener){
        mListener = listener;
    }


    class ViewHolder{
        TextView textView;
        ImageView imageView;
    }

}
