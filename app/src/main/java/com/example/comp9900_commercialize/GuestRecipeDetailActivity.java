package com.example.comp9900_commercialize;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.comp9900_commercialize.databinding.ActivityGuestRecipeDetailBinding;
import com.example.comp9900_commercialize.bean.Recipe;
import com.example.comp9900_commercialize.utilities.MacroDef;
import com.example.comp9900_commercialize.utilities.Preferences;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class GuestRecipeDetailActivity extends AppCompatActivity {

    private ActivityGuestRecipeDetailBinding binding;
    private Preferences preferences;
    private FirebaseFirestore firebaseFirestore;
    private Recipe recipe;
    private LinearLayout linearLayoutIngredients;
    private LinearLayout linearLayoutProcedures;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private String id;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuestRecipeDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        linearLayoutIngredients = binding.llDetailIngredients;
        linearLayoutProcedures = binding.llDetailProcedures;
        init();
        loadData();
        setListeners();
    }

    private void init(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        preferences = new Preferences(getApplicationContext());
        firebaseFirestore = FirebaseFirestore.getInstance();

        db = FirebaseFirestore.getInstance();

        preferences = new Preferences(getApplicationContext());
        id = preferences.getString(MacroDef.KEY_RECIPE_ID);
        DocumentReference Ref = db.collection("recipes").document(id);
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
                    binding.tvType.setText("Type: " + recipe.recipeType);
                    binding.tvDifficultyDetail.setText("Difficulty: " + recipe.recipeDifficulty);
                    binding.tvScheduledTimeDetail.setText("Scheduled time: " + recipe.recipeScheduledTime);
                    binding.tvDescription.setText(recipe.recipeDescription);
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

    private void setListeners() {
        binding.btCancel.setOnClickListener(v -> onBackPressed());
    }
}