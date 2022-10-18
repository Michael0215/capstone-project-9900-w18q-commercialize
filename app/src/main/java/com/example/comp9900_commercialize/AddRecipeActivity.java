package com.example.comp9900_commercialize;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.comp9900_commercialize.MainActivity;
import com.example.comp9900_commercialize.NoticeActivity;
import com.example.comp9900_commercialize.databinding.ActivityAddRecipeBinding;



public class AddRecipeActivity extends AppCompatActivity {
    private ActivityAddRecipeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddRecipeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners(){
        binding.btCancel.setOnClickListener(v ->
                finish());
        binding.btNotice.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), NoticeActivity.class)));
    }

}