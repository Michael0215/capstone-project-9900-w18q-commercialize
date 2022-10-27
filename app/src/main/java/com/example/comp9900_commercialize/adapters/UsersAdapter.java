package com.example.comp9900_commercialize.adapters;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp9900_commercialize.databinding.ItemContainerUserBinding;
import com.example.comp9900_commercialize.databinding.ActivityOtherProfileBinding;
import com.example.comp9900_commercialize.listeners.UserListener;
import com.example.comp9900_commercialize.models.User;

import android.graphics.Bitmap;
import android.util.Base64;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    // Adapter for the displaying E-mail and type of a user in 'Select user' page.
    private final List<User> users;
    private final UserListener userListener;


    // Constructor
    public UsersAdapter(List<User> users, UserListener userListener) {
        this.users = users;
        this.userListener = userListener;
    }

    // Create view for this page.
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new UserViewHolder(itemContainerUserBinding);
    }

    // Set data to fill containers with real values.
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setUserData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    // Extend basic ViewHolder, overwrite setData function to set what we want.
    class UserViewHolder extends RecyclerView.ViewHolder{
        ItemContainerUserBinding binding;


        UserViewHolder(ItemContainerUserBinding itemContainerUserBinding){
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;
        }

        void setUserData(User user){
            binding.textEmail.setText(user.name);
            binding.textType.setText(user.type);
            if(user.avatar != null) {
                binding.imageProfile.setImageBitmap(getUserImage(user.avatar));
            }
            binding.getRoot().setOnClickListener(v -> userListener.onUserClicked(user));
        }
    }

    private Bitmap getUserImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0, bytes.length);
    }
}
