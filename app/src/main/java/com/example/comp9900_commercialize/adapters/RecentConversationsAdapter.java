package com.example.comp9900_commercialize.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp9900_commercialize.databinding.ItemContainerRecentConversionBinding;
import com.example.comp9900_commercialize.listeners.ConversionListener;
import com.example.comp9900_commercialize.models.ChatMessage;
import com.example.comp9900_commercialize.models.User;

import java.util.List;

public class RecentConversationsAdapter extends RecyclerView.Adapter<RecentConversationsAdapter.ConversionViewHolder>{

    // Adapter for recent chat page.
    private final List<ChatMessage> chatMessages;
    private final ConversionListener conversionListener;

    // Constructor
    public RecentConversationsAdapter(List<ChatMessage> chatMessages, ConversionListener conversionListener) {
        this.chatMessages = chatMessages;
        this.conversionListener = conversionListener;
    }

    // Create view for this page.
    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversionViewHolder(
                ItemContainerRecentConversionBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    // Set data to fill containers with real values.
    @Override
    public void onBindViewHolder(@NonNull ConversionViewHolder holder, int position) {
        holder.setData(chatMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    // Extend basic ViewHolder, overwrite setData function to set what we want.
    class ConversionViewHolder extends RecyclerView.ViewHolder {

        ItemContainerRecentConversionBinding binding;

        ConversionViewHolder(ItemContainerRecentConversionBinding itemContainerRecentConversionBinding) {
            super(itemContainerRecentConversionBinding.getRoot());
            binding = itemContainerRecentConversionBinding;
        }

        void setData(ChatMessage chatMessage) {
            if (chatMessage.conversionImage != null) {
                binding.imageProfile.setImageBitmap(getConversionImage(chatMessage.conversionImage));
            }
            binding.textName.setText(chatMessage.conversionName);
            binding.textRecentMessage.setText(chatMessage.message);
            binding.getRoot().setOnClickListener(v -> {
                User user = new User();
                user.email = chatMessage.conversionEmail;
                conversionListener.onConversionClicked(user);
            });
        }
    }

    private Bitmap getConversionImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}