package com.example.comp9900_commercialize;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageButton;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.comp9900_commercialize.bean.Collection;
import com.example.comp9900_commercialize.bean.Recipe;
import com.example.comp9900_commercialize.databinding.ActivityAddRecipeBinding;
import com.example.comp9900_commercialize.databinding.ActivityRecipeDetailBinding;
import com.example.comp9900_commercialize.utilities.MacroDef;
import com.example.comp9900_commercialize.utilities.Preferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;

public class RecipeDetailActivity extends AppCompatActivity {

    private ActivityRecipeDetailBinding binding;
    private Preferences preferences;
    private FirebaseFirestore firebaseFirestore;
    private Recipe recipe;
    private LinearLayout linearLayoutIngredients;
    private LinearLayout linearLayoutProcedures;
    private static final String TAG="RecipeDetailActivity";
    private FirebaseUser user;
    private FirebaseAuth auth;
    private String recipeId;
    private Collection myCollection;
    
    
    private ActivityRecipeDetailBinding binding;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private Preferences preferences;
    private String id;
    private String oldLike;
    private TextView likeNum;
    private String newLikeNum;
    private String like;
    private List likeList;
    private String[] likeArray;
    private ImageButton good;
    private boolean judge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecipeDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        linearLayoutIngredients = binding.llDetailIngredients;
        linearLayoutProcedures = binding.llDetailProcedures;
        init();
        loadData();
        setListeners();
        
        likeNum = findViewById(R.id.tv_like_num);
        good = findViewById(R.id.ib_like);
    }



    private void init(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        preferences = new Preferences(getApplicationContext());
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void loadData(){
        DocumentReference docRef = firebaseFirestore.collection("recipes").document(preferences.getString(MacroDef.KEY_RECIPE_ID));
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                recipe = documentSnapshot.toObject(Recipe.class);
                if(recipe != null){
                    byte[] bytes = Base64.decode(recipe.recipeCover, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    binding.ivRecipeCoverImage.setImageBitmap(bitmap);
                    binding.tvRecipeTitle.setText(recipe.recipeName);
                    if(recipe.recipeContributorAvatar != null){
                        bytes = Base64.decode(recipe.recipeContributorAvatar, Base64.DEFAULT);
                        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        binding.rivUserPhoto.setImageBitmap(bitmap);
                    }
                    binding.tvContributorNameTitle.setText(recipe.recipeContributorName);
                    for(int i = 0; i < recipe.recipeIngredientList.size(); i++){
                        loadView(i, linearLayoutIngredients);
                    }
                    for(int i = 0; i < recipe.recipeStepList.size(); i++){
                        loadView(i, linearLayoutProcedures);
                    }
                }
            }
        });
    }

    private void loadView(int position, LinearLayout linearLayout) {

        if(linearLayout == linearLayoutIngredients){
            View ingredientView = getLayoutInflater().inflate(R.layout.row_display_ingredient, null, false);
            TextView ingredientName = (TextView) ingredientView.findViewById(R.id.tv_ingredient_name);
            TextView amount = (TextView) ingredientView.findViewById(R.id.tv_amounts);

            ingredientName.setText(recipe.recipeIngredientList.get(position).getIngredientName());
            amount.setText(recipe.recipeIngredientList.get(position).getAmount());

            linearLayout.addView(ingredientView);
        }
        else if(linearLayout == linearLayoutProcedures){
            View procedureView = getLayoutInflater().inflate(R.layout.row_display_procedure, null, false);
            TextView procedureDescription = (TextView) procedureView.findViewById(R.id.tv_procedure_description);
            ImageView image = (ImageView) procedureView.findViewById(R.id.iv_display_procedure_photo);

            procedureDescription.setText(recipe.recipeStepList.get(position).stepDescription);
            byte[] bytes = Base64.decode(recipe.recipeStepList.get(position).encodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            image.setImageBitmap(bitmap);

            linearLayout.addView(procedureView);
        }
    }

    private void setListeners(){
        binding.btCancel.setOnClickListener(v -> onBackPressed());
        binding.btNotice.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), NoticeActivity.class)));
        binding.tvContributorNameTitle.setOnClickListener(v -> {
            preferences.putString(MacroDef.KEY_CONTRIBUTOR_EMAIL, recipe.recipeContributorEmail);
            startActivity(new Intent(getApplicationContext(), OtherProfileActivity.class));
        });
        binding.rivUserPhoto.setOnClickListener(v -> {
            preferences.putString(MacroDef.KEY_CONTRIBUTOR_EMAIL, recipe.recipeContributorEmail);
            startActivity(new Intent(getApplicationContext(), OtherProfileActivity.class));
        });
        binding.ibCollection.setOnClickListener(view -> {
            String recipeId=preferences.getString(MacroDef.KEY_RECIPE_ID);
            //检查之前collection类是否含有数据.并添加新recipeId到里面去
            collectMainFunc();
        });
    }
    private void collectMainFunc() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        recipeId=preferences.getString(MacroDef.KEY_RECIPE_ID);
        DocumentReference docRef = firebaseFirestore.collection("collection").document(preferences.getString(MacroDef.KEY_EMAIL));
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                myCollection = documentSnapshot.toObject(Collection.class);
                if(myCollection != null){
                    if (myCollection.collectionList != null && contain(myCollection, recipeId)) {
                        myCollection.collectionList.remove(recipeId);
                        showToast("Collection Denied");
                    } else if (myCollection.collectionList != null) {
                        myCollection.collectionList.add(recipeId);
                        showToast("Collection Success");
                    }
                }
                else {
                    myCollection = new Collection(Collections.singletonList(recipeId));
                    showToast("This is your first time collect something");

                }
                String currentEmail=preferences.getString(MacroDef.KEY_EMAIL);
                firebaseFirestore.collection("collection").document(currentEmail)
                        .set(myCollection).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG,"successfully written!");
                            }
                        });
            }
        });
    }

    private boolean contain(Collection myCollection, String recipeId) {
        List<String> arr=myCollection.collectionList;
        return arr.contains(recipeId);
    }
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message,Toast.LENGTH_SHORT).show();
    }

}
