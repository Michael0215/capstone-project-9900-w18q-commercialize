package com.example.comp9900_commercialize;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.comp9900_commercialize.databinding.ActivityEditProfileBinding;


public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners(){
        binding.btCancel.setOnClickListener(v ->
                finish());
    }

}