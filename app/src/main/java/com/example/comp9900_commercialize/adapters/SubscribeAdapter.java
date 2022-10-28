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
import com.example.comp9900_commercialize.bean.Recipe;

import java.util.List;

public class SubscribeAdapter extends RecyclerView.Adapter<SubscribeAdapter.SimpleHolder> {

    private final List<Recipe> mData;
    private SubscribeAdapter.OnItemClickListener mOnItemClickListener;
    public SubscribeAdapter(List<Recipe> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public SimpleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_container_subscribe,null);
        return new SimpleHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleHolder holder, int position) {
        holder.setData(mData.get(position),position);
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public class SimpleHolder extends RecyclerView.ViewHolder {
        private ImageView avatar;
        private TextView contributor;
        private TextView description;
        private ImageView cover;
        private TextView likeNum;
        private TextView commentNum;
        private TextView datetime;
        private int mPosition;

        public SimpleHolder(@NonNull View itemView)  {
            super(itemView);
            avatar = itemView.findViewById(R.id.riv_user_photo_subscribe);
            contributor = itemView.findViewById(R.id.tv_contributor_name);
            description = itemView.findViewById(R.id.tv_recipe);
            cover = itemView.findViewById(R.id.iv_recipe);
            likeNum = itemView.findViewById(R.id.tv_like_num);
            commentNum = itemView.findViewById(R.id.tv_comment_num);
            datetime = itemView.findViewById(R.id.tv_datetime);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mPosition);
                    }
                }
            });

        }

        public void setData(Recipe recipe, int position) {
            this.mPosition = position;
            if(recipe.recipeContributorAvatar != null){
                byte[] bytes = Base64.decode(recipe.recipeContributorAvatar, Base64.DEFAULT);
                avatar.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }
            contributor.setText(recipe.recipeContributorName);
            description.setText(recipe.recipeDescription);
            byte[] bytes = Base64.decode(recipe.recipeCover, Base64.DEFAULT);
            cover.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            likeNum.setText(String.valueOf(recipe.recipeLikesNum));
            commentNum.setText(String.valueOf(recipe.recipeCommentsNum));
            datetime.setText(recipe.recipePublishTime);
        }
    }

    public void setOnItemClickListener(SubscribeAdapter.OnItemClickListener listener) {
        //手动设置监听，其实就是要设置一个接口，一个回调的接口
        this.mOnItemClickListener = listener;
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}