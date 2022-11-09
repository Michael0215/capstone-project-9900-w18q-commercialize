package com.example.comp9900_commercialize.adapters;

import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp9900_commercialize.R;
import com.example.comp9900_commercialize.bean.Comment;
import com.example.comp9900_commercialize.bean.ItemFollow;
import com.example.comp9900_commercialize.bean.itemComment;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class CommentAdapter  extends RecyclerView.Adapter<CommentAdapter.SimpleHolder> {
    private List<itemComment> mData;
    private CommentAdapter.OnItemClickListener mOnItemClickListener;
    public CommentAdapter(List<itemComment> data){
        this.mData=data;
    }

    @NonNull
    @Override
    public SimpleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  View.inflate(parent.getContext(), R.layout.item_container_comment, null);
        return new SimpleHolder(view);
    }
//
    @Override
    public void onBindViewHolder(@NonNull SimpleHolder holder, int position) {
        holder.setData(mData.get(position),position);
    }

    @Override
    public int getItemCount() {
        if (mData != null){
            return mData.size();
        }
        return 0;
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public class SimpleHolder extends RecyclerView.ViewHolder {
        private TextView mComment;
        private int mPosition;
        private RoundedImageView mPhoto;
        private TextView mName;
        private TextView mDate;

        public SimpleHolder(@NonNull View itemView) {
            super(itemView);
            //找到图片控件
            mComment = itemView.findViewById(R.id.tv_comment);
            mPhoto=itemView.findViewById(R.id.riv_user_photo);
            mName=itemView.findViewById(R.id.tv_contributor_name);
            mDate=itemView.findViewById(R.id.tv_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mPosition);
                    }
                }
            });
        }
        public void setData(itemComment itemcomment, int position) {
            this.mPosition = position;
            mName.setText(itemcomment.username);
            if(itemcomment.avatar != null){
                byte[] bytes = Base64.decode(itemcomment.avatar, Base64.DEFAULT);
                mPhoto.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }
            mDate.setText(itemcomment.date);
            mComment.setText(itemcomment.comment);
        }

    }
}

