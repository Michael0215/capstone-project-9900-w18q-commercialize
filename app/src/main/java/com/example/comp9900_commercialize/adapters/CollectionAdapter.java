package com.example.comp9900_commercialize.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp9900_commercialize.R;
import com.example.comp9900_commercialize.bean.ItemCollection;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.InnerHolder>{

    private List<ItemCollection> mData;
    private OnItemClickListener mOnItemClickListener;

    public CollectionAdapter(List<ItemCollection> data){
        this.mData = data;
    }

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view =  View.inflate(parent.getContext(), R.layout.item_container_collection, null);

        return new InnerHolder(view);
    }

    //这个方法用于绑定holder的，一般用来设置数据
    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        //在这里设置数据
        holder.setData(mData.get(position), position);
    }

    //返回图片个数
    @Override
    public int getItemCount() {
        if (mData != null){
            return mData.size();
        }
        return 0;
    }

    /**
     * 编写回调的步骤
     * 1、创建这个接口
     * 2、定义接口内部的方法
     * 3、提供设置接口的方法
     * 4、接口方法的调用
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        //手动设置监听，其实就是要设置一个接口，一个回调的接口
        this.mOnItemClickListener = listener;
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        private ImageView mIcon;
        private TextView mTitle,mContributor;
        private RoundedImageView mAvatar;
        private int mPosition;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            //找到图片控件
            mIcon =  itemView.findViewById(R.id.iv_recipe);
            mTitle =  itemView.findViewById(R.id.tv_recipe);
            mContributor = itemView.findViewById(R.id.tv_contributor_name);
            mAvatar = itemView.findViewById(R.id.riv_user_photo);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mPosition);
                    }
                }
            });
        }

        //这个方法用于设置数据
        public void setData(ItemCollection itemExplore, int position) {
            this.mPosition = position;
            //开始设置数据
            mIcon.setImageBitmap(itemExplore.icon);
            mTitle.setText(itemExplore.title);
            mContributor.setText(itemExplore.tv_contributor_name);
            mAvatar.setImageBitmap(itemExplore.avatar);
        }
    }
}
