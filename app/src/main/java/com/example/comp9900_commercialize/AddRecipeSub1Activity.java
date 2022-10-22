package com.example.comp9900_commercialize;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.comp9900_commercialize.bean.Ingredient;
import com.example.comp9900_commercialize.bean.Recipe;
import com.example.comp9900_commercialize.databinding.ActivityAddRecipeSub1Binding;
import com.example.comp9900_commercialize.utilities.MacroDef;
import com.example.comp9900_commercialize.utilities.Preferences;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AddRecipeSub1Activity extends AppCompatActivity implements View.OnClickListener {

    private ActivityAddRecipeSub1Binding binding;
    private LinearLayout linearLayout;
    private Preferences preferences;
    public static AddRecipeSub1Activity instance = null;
    List<Ingredient> ingredientList = new ArrayList<>();
    Recipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddRecipeSub1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        instance = this;
        recipe = (Recipe) this.getIntent().getSerializableExtra("recipe");
        linearLayout = binding.llIngredients;
        init();
        if(!preferences.getBoolean(MacroDef.KEY_MODE_CREATE)){
            loadData();
        }
        setListeners();
    }

    private void init(){
        preferences = new Preferences(getApplicationContext());
    }

    private void setListeners(){
        binding.btCancel.setOnClickListener(v -> {
            preferences.putBoolean(MacroDef.KEY_MODE_CREATE, true);
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            preferences.putString(MacroDef.KEY_RECIPE_NAME, null);
            preferences.putString(MacroDef.KEY_RECIPE_DESCRIPTION, null);
            if(new AddRecipeSub2Activity().instance != null){
                new AddRecipeSub2Activity().instance.finish();
            }
            new AddRecipeActivity().instance.finish();
            finish();
        });
        binding.btPre.setOnClickListener(v -> {
                onBackPressed();
        });

        binding.btNext.setOnClickListener(v ->{
            if(checkIfValidAndRead()){
                recipe.setRecipeIngredientList(ingredientList);
                recipe.setRecipeDifficulty(binding.spnTypeDifficulty.getSelectedItem().toString().trim());
                recipe.setRecipeScheduledTime(binding.spnTypeTime.getSelectedItem().toString().trim());
                Intent intent = new Intent(AddRecipeSub1Activity.this, AddRecipeSub2Activity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("recipe", recipe);
                intent.putExtras(bundle);
                startActivity(intent);
            }

        });
        binding.btAddIngredient.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        addView();
    }

    private void addView() {

        View ingredientView = getLayoutInflater().inflate(R.layout.row_add_ingredient, null, false);
        EditText ingredientName = (EditText)ingredientView.findViewById(R.id.et_ingredient_name);
        EditText amount = (EditText)ingredientView.findViewById(R.id.et_amounts);
        ImageView remove = (ImageView)ingredientView.findViewById(R.id.iv_remove_ingredient);

        remove.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                removeView(ingredientView);
            }
        });
        linearLayout.addView(ingredientView);

    }

    private void loadView(int position) {

        View ingredientView = getLayoutInflater().inflate(R.layout.row_add_ingredient, null, false);
        EditText ingredientName = (EditText)ingredientView.findViewById(R.id.et_ingredient_name);
        EditText amount = (EditText)ingredientView.findViewById(R.id.et_amounts);
        ImageView remove = (ImageView)ingredientView.findViewById(R.id.iv_remove_ingredient);

        ingredientName.setText(recipe.recipeIngredientList.get(position).getIngredientName());
        amount.setText(recipe.recipeIngredientList.get(position).getAmount());

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

    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), AddRecipeActivity.class));
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message,Toast.LENGTH_SHORT).show();
    }

    public boolean checkIfValidAndRead() {
        ingredientList.clear();
        boolean result = true;
        if (binding.spnTypeDifficulty.getSelectedItemPosition()!=0
                && binding.spnTypeTime.getSelectedItemPosition()!=0){
        }else{
            result = false;
        }
        for(int i = 0; i < linearLayout.getChildCount(); i++){
            View ingredientView = linearLayout.getChildAt(i);
            EditText ingredientName = (EditText)ingredientView.findViewById(R.id.et_ingredient_name);
            EditText amount = (EditText)ingredientView.findViewById(R.id.et_amounts);
            Ingredient ingredient = new Ingredient();
            if(!ingredientName.getText().toString().equals("")){
                ingredient.setIngredientName(ingredientName.getText().toString());
            }else{
                result = false;
                break;
            }
            if(!amount.getText().toString().equals("")){
                ingredient.setAmount(amount.getText().toString());
            }else{
                result = false;
                break;
            }
            ingredientList.add(ingredient);
        }
        if(ingredientList.size() == 0){
            result = false;
            showToast("Add Ingredients First!");
        }else if(!result){
            showToast("Input cannot be empty!");
        }
        return result;
    }

    private void loadData(){
        if(recipe != null){
            for(int i = 0; i < recipe.recipeIngredientList.size(); i++){
            loadView(i);
//            binding.spnTypeDifficulty.setSelection(recipe.recipeDifficulty);
//            binding.etRecipeDescription.setText(recipe.recipeDescription);
            }
        }
    }

}