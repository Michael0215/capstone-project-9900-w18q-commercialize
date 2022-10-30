package com.example.comp9900_commercialize;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import com.example.comp9900_commercialize.adapters.ProfileRecipeAdapter;
import com.example.comp9900_commercialize.bean.ItemProfileRecipe;
import com.example.comp9900_commercialize.bean.Recipe;
import com.example.comp9900_commercialize.databinding.ActivityOtherProfileBinding;
import com.example.comp9900_commercialize.models.User;
import com.example.comp9900_commercialize.utilities.MacroDef;
import com.example.comp9900_commercialize.utilities.Preferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class OtherProfileActivity extends BaseActivity {

    private ActivityOtherProfileBinding binding;
    private Preferences preferences;
    private FirebaseFirestore firebaseFirestore;
    private List<ItemProfileRecipe> mData;
    private Recipe recipes;
    private RecyclerView mList;
    private ProfileRecipeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtherProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mList = this.findViewById(R.id.rcv_all_post);
        init();
        loadData();
        setListeners();
    }

    private void init(){

        firebaseFirestore = FirebaseFirestore.getInstance();
        preferences = new Preferences(getApplicationContext());

    }

    private void loadData(){

        mData = new ArrayList<>();
        CollectionReference posts = firebaseFirestore.collection("recipes");
        Query query = posts.whereEqualTo("recipeContributorEmail",preferences.getString(MacroDef.KEY_CONTRIBUTOR_EMAIL));
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // retrieve all posts in the 'posts' table
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ItemProfileRecipe recipe = new ItemProfileRecipe();
                                recipes = document.toObject(Recipe.class);
                                byte[] bytes = Base64.decode(recipes.recipeCover, Base64.DEFAULT);
                                recipe.icon =  BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                recipe.title = recipes.recipeName;
                                recipe.id = document.getId();
                                mData.add(recipe);
                            }
                            DocumentReference docRef = firebaseFirestore.collection("users").document(preferences.getString(MacroDef.KEY_CONTRIBUTOR_EMAIL));
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Map<String, Object> data = document.getData();
                                            if(data.get("Avatar") != null){
                                                byte[] bytes = Base64.decode((String)data.get("Avatar"), Base64.DEFAULT);
                                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                binding.rivUserPhoto.setImageBitmap(bitmap);
                                            }
                                            binding.tvUserName.setText(recipes.recipeContributorName);
                                            binding.tvContactDetail.setText((String)data.get("Contact Detail"));
                                        }
                                    }
                                }
                            });
                            showGrid();
                        }
                    }
                });


    }

    private void setListeners(){
        binding.btCancel.setOnClickListener(v -> onBackPressed());
        binding.btNotice.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), ChatMainActivity.class)));
        //click on 'chat' in OtherProfile page and jump to LiveChat page directly
        binding.ivNotice.setOnClickListener(v -> {
            User user = new User();
            user.email = recipes.recipeContributorEmail;
            user.name = recipes.recipeContributorName;
            user.avatar = recipes.recipeContributorAvatar;
            Intent intent = new Intent(getApplicationContext(), LiveChatActivity.class);
            intent.putExtra(MacroDef.KEY_USER, user);
            startActivity(intent);
        });
    }

    private void showGrid() {
        //准备布局管理器
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        //设置布局管理器的方向
//        layoutManager.setOrientation(isVertical ? LinearLayoutManager.VERTICAL : LinearLayoutManager.HORIZONTAL);
//        layoutManager.setReverseLayout(isReverse);
        //设置布局管理器到RecyclerView里
        mList.setLayoutManager(layoutManager);
        //创建适配器
        adapter = new ProfileRecipeAdapter(mData);
        //设置适配器
        mList.setAdapter(adapter);
        mList.postInvalidate();
        //初始化事件RecyclerView
        initListener();
    }

    private void initListener() {
        adapter.setOnItemClickListener(new ProfileRecipeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                preferences.putString(MacroDef.KEY_RECIPE_ID, mData.get(position).id);
                preferences.putBoolean(MacroDef.KEY_MODE_CREATE, false);
                startActivity(new Intent(getApplicationContext(), RecipeDetailActivity.class));
            }
        }) ;
    }

}