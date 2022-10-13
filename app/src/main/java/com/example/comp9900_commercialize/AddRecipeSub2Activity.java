package com.example.comp9900_commercialize;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.comp9900_commercialize.databinding.ActivityAddRecipeSub2Binding;



public class AddRecipeSub2Activity extends AppCompatActivity {
    private ActivityAddRecipeSub2Binding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddRecipeSub2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners(){
        binding.btCancel.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), MainActivity.class)));
        binding.btNotice.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), NoticeActivity.class)));
        binding.btPre.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), AddRecipeSub1Activity.class)));
        binding.btPublish.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), MainActivity.class)));
    }
}