package com.example.comp9900_commercialize;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;


import com.example.comp9900_commercialize.databinding.ActivityAddRecipeSub1Binding;

import java.util.ArrayList;
import java.util.List;

public class AddRecipeSub1Activity extends AppCompatActivity implements View.OnClickListener {
    private ActivityAddRecipeSub1Binding binding;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddRecipeSub1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        linearLayout = binding.llIngredients;
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
        binding.btAddIngredient.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        addView();
    }

    private void addView() {

        View ingredientView = getLayoutInflater().inflate(R.layout.row_add_ingredient, null, false);
        EditText ingredientName = (EditText)ingredientView.findViewById(R.id.et_ingredient_name);
        EditText amounts = (EditText)ingredientView.findViewById(R.id.et_amounts);
        ImageView remove = (ImageView)ingredientView.findViewById(R.id.iv_remove_ingredient);

        remove.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                removeView(ingredientView);
            }
        });

        linearLayout.addView(ingredientView);

    }

    private void removeView(View view) {
        linearLayout.removeView(view);
    }
}