package com.example.comp9900_commercialize;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import com.example.comp9900_commercialize.bean.Follow;
import com.example.comp9900_commercialize.bean.ItemCollection;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.InputStream;
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
    private String user_email;
    private Collection myCollection;
    private Follow myFollow;
    
    private FirebaseFirestore db;
    private String id;
    private String oldLike;
    private TextView likeNum;
    private String newLikeNum;
    private String like;
    private List likeList;
    private String[] likeArray;
    private ImageButton good;
    private boolean judge;
    private List<String> collection_list;
    private List<String> follow_list;
    private boolean collected;
//
    private boolean followed;
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
        
        db = FirebaseFirestore.getInstance();
        DocumentReference Reference = db.collection("users").document(user.getEmail());
        DocumentReference rf_collection_list = db.collection("collection").document(user.getEmail());
        DocumentReference rf_follow_list = db.collection("follow").document(user.getEmail());

        preferences = new Preferences(getApplicationContext());
        likeNum = findViewById(R.id.tv_like_num);
        id = preferences.getString(MacroDef.KEY_RECIPE_ID);
        DocumentReference Ref = db.collection("recipes").document(id);
        
        Ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        oldLike = document.get("recipeLikesNum").toString();
                        likeNum.setText(oldLike);

                    }
                }
            }
        });
        Ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                recipe = documentSnapshot.toObject(Recipe.class);
                user_email = recipe.recipeContributorEmail;
            }
        });

        Reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        like = document.get("Like List").toString();
                        likeArray = like.split(",");
                        likeList = Arrays.asList(like.split(","));
                        judge = likeList.contains(id);
                        if(!judge){
                            binding.ibLike.setImageResource(R.drawable.ic_like);
                        }
                        else{
                            binding.ibLike.setImageResource(R.drawable.ic_like2);
                        }

                    }
                }
            }

        });

        rf_collection_list.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                myCollection = documentSnapshot.toObject(Collection.class);
                if (myCollection != null) {
                    collection_list = myCollection.collectionList;
                    if (!collection_list.isEmpty()) {
                        collected = collection_list.contains(id);
                    }
                    else{
                        collected = false;
                    }
                    if(!collected){
                        binding.ibCollection.setImageResource(R.drawable.ic_collection);
                    }
                    else{
                        binding.ibCollection.setImageResource(R.drawable.ic_collection2);
                    }
                }
            }
        });

        rf_follow_list.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                myFollow = documentSnapshot.toObject(Follow.class);
                if (myFollow != null) {
                    follow_list = myFollow.followList;
                    if (!follow_list.isEmpty()) {
                        followed = follow_list.contains(user_email);
                    }
                    else{
                        followed = false;
                    }
                    if(!followed){
                        binding.btFollow.setText("Follow");
                    }
                    else{
                        binding.btFollow.setText("Followed");
                    }
                }
            }
        });

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
                    if (preferences.getString(MacroDef.KEY_EMAIL).equals(recipe.recipeContributorEmail)) {
                        binding.tvContributorNameTitle.setOnClickListener(v -> {
                            Toast.makeText(getApplicationContext(), "You can not view your profile from here!", Toast.LENGTH_SHORT).show();
                        });
                        binding.rivUserPhoto.setOnClickListener(v -> {
                            Toast.makeText(getApplicationContext(), "You can not view your profile from here!", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        binding.tvContributorNameTitle.setOnClickListener(v -> {
                            preferences.putString(MacroDef.KEY_CONTRIBUTOR_EMAIL, recipe.recipeContributorEmail);
                            startActivity(new Intent(getApplicationContext(), OtherProfileActivity.class));
                        });
                        binding.rivUserPhoto.setOnClickListener(v -> {
                            preferences.putString(MacroDef.KEY_CONTRIBUTOR_EMAIL, recipe.recipeContributorEmail);
                            startActivity(new Intent(getApplicationContext(), OtherProfileActivity.class));
                        });
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
                startActivity(new Intent(getApplicationContext(), ChatMainActivity.class)));
        binding.ibCollection.setOnClickListener(view -> {
            String recipeId=preferences.getString(MacroDef.KEY_RECIPE_ID);
            //检查之前collection类是否含有数据.并添加新recipeId到里面去
            collectMainFunc();
        });
        binding.ibComment.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), CommentActivity.class)));
        binding.btFollow.setOnClickListener(view -> {
            //检查之前follow类是否含有数据.并添加新contributor email到里面去
            followMainFunc();
        });
        
        
        
        
        binding.ibLike.setOnClickListener(v -> {
            if(!judge){
                binding.ibLike.setImageResource(R.drawable.ic_like2);
                String newString;
                if(like == ""){
                    newString = id;
                }else{
                    newString = like + ',' + id;
                }
                int oldLike1 = Integer.parseInt(oldLike);
                int newLike1 = oldLike1 + 1;
                newLikeNum = String.valueOf(newLike1);
                likeNum.setText(newLikeNum);


                DocumentReference Ref = db.collection("recipes").document(id);
                Ref.update("recipeLikesNum", newLike1)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {@Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Like succeed", Toast.LENGTH_SHORT).show();
                        }
                        });

                DocumentReference Ref1 = db.collection("users").document(user.getEmail());
                Ref1
                        .update("Like List", newString)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        });
                judge = true;
                oldLike = newLikeNum;
            }else{
                binding.ibLike.setImageResource(R.drawable.ic_like);
                List<String> arrList = new ArrayList<String>(likeList);
                arrList.remove(id);

                String newString = String.join(",", arrList);

                int oldLike1 = Integer.parseInt(oldLike);
                int newLike1 = oldLike1 - 1;
                newLikeNum = String.valueOf(newLike1);
                likeNum.setText(newLikeNum);

                DocumentReference Ref = db.collection("recipes").document(id);
                Ref.update("recipeLikesNum", newLike1)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {@Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Delete like succeed", Toast.LENGTH_SHORT).show();
                        }
                        });

                DocumentReference Ref1 = db.collection("users").document(user.getEmail());
                Ref1
                        .update("Like List", newString)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        });
                judge = false;
                oldLike = newLikeNum;
                good.setImageResource(R.drawable.ic_like);
            }

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
                        binding.ibCollection.setImageResource(R.drawable.ic_collection);
                        showToast("Collection Denied");
                    } else if (myCollection.collectionList != null) {
                        myCollection.collectionList.add(recipeId);
                        binding.ibCollection.setImageResource(R.drawable.ic_collection2);
                        showToast("Collection Success");
                    }
                }
                else {
                    myCollection = new Collection(Collections.singletonList(recipeId));
                    binding.ibCollection.setImageResource(R.drawable.ic_collection2);
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

    private void followMainFunc() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        user_email = recipe.recipeContributorEmail;
        DocumentReference docRef = firebaseFirestore.collection("follow").document(preferences.getString(MacroDef.KEY_EMAIL));
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                myFollow = documentSnapshot.toObject(Follow.class);
                if(myFollow != null){
                    if (myFollow.followList != null && containFollow(myFollow, user_email)) {
                        myFollow.followList.remove(user_email);
                        binding.btFollow.setText("Follow");
                        showToast("Follow Denied");
                    } else if (myFollow.followList != null) {
                        myFollow.followList.add(user_email);
                        binding.btFollow.setText("Followed");
                        showToast("Follow Success");
                    }
                }
                else {
                    myFollow = new Follow(Collections.singletonList(user_email));
                    binding.btFollow.setText("Followed");
                    showToast("This is your first time follow someone");
                }
                String currentEmail=preferences.getString(MacroDef.KEY_EMAIL);
                firebaseFirestore.collection("follow").document(currentEmail)
                        .set(myFollow).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    private boolean containFollow(Follow myFollow, String user_email) {
        List<String> arr=myFollow.followList;
        return arr.contains(user_email);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message,Toast.LENGTH_SHORT).show();
    }

}
