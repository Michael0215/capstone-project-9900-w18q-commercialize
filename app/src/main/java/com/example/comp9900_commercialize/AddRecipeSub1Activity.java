package com.example.comp9900_commercialize;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;



import com.example.comp9900_commercialize.databinding.ActivityAddRecipeSub1Binding;

public class AddRecipeSub1Activity extends AppCompatActivity {
    private ActivityAddRecipeSub1Binding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddRecipeSub1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners(){
        binding.btCancel.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), MainActivity.class)));
        binding.btNotice.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), NoticeActivity.class)));
        binding.btPre.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), AddRecipeActivity.class)));
        binding.btNext.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), AddRecipeSub2Activity.class)));
    }

}