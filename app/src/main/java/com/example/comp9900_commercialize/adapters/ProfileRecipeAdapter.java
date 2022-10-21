package com.example.comp9900_commercialize.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.example.comp9900_commercialize.R;
import com.example.comp9900_commercialize.bean.ItemProfileRecipe;

import java.util.List;

public class ProfileRecipeAdapter extends RecyclerView.Adapter<ProfileRecipeAdapter.InnerHolder> {

    private final List<ItemProfileRecipe> mData;
    private ProfileRecipeAdapter.OnItemClickListener mOnItemClickListener;



    public ProfileRecipeAdapter(List<ItemProfileRecipe> data){
        this.mData = data;
    }


    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_container_profile_recipe,null);
        return new InnerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        holder.setData(mData.get(position),position);
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView image;
        private int mPosition;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_recipe);
            image = itemView.findViewById(R.id.iv_recipe);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mPosition);
                    }
                }
            });

        }

        public void setData(ItemProfileRecipe itemProfileRecipe, int position) {
            this.mPosition = position;
            title.setText(itemProfileRecipe.title);
            image.setImageBitmap(itemProfileRecipe.icon);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        //手动设置监听，其实就是要设置一个接口，一个回调的接口
        this.mOnItemClickListener = listener;
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}
