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
        binding.ibExplore.setOnClickListener(v -> {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                });
        binding.ibSearch.setOnClickListener(v -> {
                    startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                    finish();
                });
        binding.ibCreate.setOnClickListener(v -> {
                    startActivity(new Intent(getApplicationContext(), AddRecipeActivity.class));
                });
        binding.ibProfile.setOnClickListener(v -> {
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    finish();
                });
        binding.btNotice.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), ChatMainActivity.class)));
    }
}