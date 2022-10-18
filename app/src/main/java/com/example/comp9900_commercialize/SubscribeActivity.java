package com.example.comp9900_commercialize;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.comp9900_commercialize.databinding.ActivitySubscribeBinding;

public class SubscribeActivity extends AppCompatActivity {

    private ActivitySubscribeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubscribeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners(){
        binding.ibExplore.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), MainActivity.class)));
        binding.ibSearch.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SearchActivity.class)));
        binding.ibCreate.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), AddRecipeActivity.class)));
        binding.ibSubscribe.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SubscribeActivity.class)));
        binding.ibProfile.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class)));
        binding.btNotice.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), NoticeActivity.class)));
    }
}