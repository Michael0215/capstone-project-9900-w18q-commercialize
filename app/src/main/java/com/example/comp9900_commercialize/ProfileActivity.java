package com.example.comp9900_commercialize;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.GridView;
import android.widget.Toast;


import com.example.comp9900_commercialize.adapters.ProfileRecipeAdapter;

import com.example.comp9900_commercialize.adapters.StaggerAdapter;
import com.example.comp9900_commercialize.bean.Datas;

import com.example.comp9900_commercialize.bean.ItemProfileRecipe;
import com.example.comp9900_commercialize.bean.Recipe;
import com.example.comp9900_commercialize.databinding.ActivityProfileBinding;
import com.example.comp9900_commercialize.utilities.MacroDef;
import com.example.comp9900_commercialize.utilities.Preferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseUser mUser;
    private ActivityProfileBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private Preferences preferences;
    private RecyclerView mList;
    private List<ItemProfileRecipe> mData;
    private ProfileRecipeAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private Recipe recipes;
    public static ProfileActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mList = binding.rcvAllPost;
        firebaseFirestore = FirebaseFirestore.getInstance();
        instance = this;
        init();
        loadData();
//        initData();
        setListeners();
    }

    private void init(){
        preferences = new Preferences(getApplicationContext());
    }

    private void loadData(){
        binding.tvUserName.setText(preferences.getString(MacroDef.KEY_USERNAME));
        binding.tvContactDetail.setText(preferences.getString(MacroDef.KEY_CONTACT));
        if (preferences.getString(MacroDef.KEY_AVATAR) != null){
            byte[] bytes = Base64.decode(preferences.getString(MacroDef.KEY_AVATAR), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            binding.rivUserPhoto.setImageBitmap(bitmap);
        }
        //创建数据集合
        mData = new ArrayList<>();
        //创建模拟数据
        // retrieve all the post in the firestore's table 'posts'
        CollectionReference posts = firebaseFirestore.collection("recipes");
        // order the post in creating time order
        Query query = posts.whereEqualTo("recipeContributorEmail",preferences.getString(MacroDef.KEY_EMAIL));
//        Query query = posts.orderBy("recipePublishTime", Query.Direction.DESCENDING); //.whereEqualTo("recipeContributorEmail","767831805@qq.com");
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // retrieve all posts in the 'posts' table
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ItemProfileRecipe recipe = new ItemProfileRecipe();
//                                                Toast.makeText(MainActivity.this, "Refresh Success!", Toast.LENGTH_SHORT).show();

                                recipes = document.toObject(Recipe.class);
                                byte[] bytes = Base64.decode(recipes.recipeCover, Base64.DEFAULT);
                                recipe.icon =  BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                                    if (mapElement.getKey().equals("recipeCover")){
//                                        byte[] bytes = Base64.decode(mapElement.getValue().toString(), Base64.DEFAULT);
//                                        recipe.icon =  BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                                    }
                                recipe.title = recipes.recipeName;
//                                    if (mapElement.getKey().equals("recipeName")){
//                                        recipe.title = mapElement.getValue().toString();
//                                    }
                                recipe.id = document.getId();
                                mData.add(recipe);
                            }
                            showGrid();
                        } else { // error handling

                            Toast.makeText(ProfileActivity.this, mUser.toString(), Toast.LENGTH_SHORT).show();
//                            Toast.makeText(ProfileActivity.this, "Error getting documents.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setListeners(){
        binding.btSetting.setOnClickListener(v -> {
                    startActivity(new Intent(getApplicationContext(), SettingActivity.class));
                });
        binding.ivNotice.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), ChatMainActivity.class)));
        binding.ivCollection.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), CollectionActivity.class)));
        binding.ivFollowing.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), FollowingActivity.class)));
        binding.ibExplore.setOnClickListener(v -> {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                });
        binding.ibSearch.setOnClickListener(v -> {
                    startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                    finish();
                });
        binding.ibCreate.setOnClickListener(v -> {
            preferences.putBoolean(MacroDef.KEY_MODE_CREATE, true);
            startActivity(new Intent(getApplicationContext(), AddRecipeActivity.class));
        });
        binding.ibSubscribe.setOnClickListener(v -> {
                    startActivity(new Intent(getApplicationContext(), SubscribeActivity.class));
                    finish();
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
                startActivity(new Intent(getApplicationContext(), AddRecipeActivity.class));
            }
        }) ;
    }



    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message,Toast.LENGTH_SHORT).show();
    }
    // 等数据库内有数据了就可以把下面这个函数注释掉了
//    private void initData() {
//        //list<Data>-->Adapter-->SetAdapter-->显示数据
//        //创建数据集合
//        mData = new ArrayList<>();
//        //创建模拟数据
//        for (int i = 0; i < Datas.icons.length; i++){
//            //创建数据对象
//            ItemProfileRecipe data = new ItemProfileRecipe();
//            data.id = String.valueOf(i);
//            @SuppressLint("ResourceType") InputStream img_icon = getResources().openRawResource(Datas.icons[i]);
//            data.icon = BitmapFactory.decodeStream(img_icon);
//            data.title = "我是第" + (i+1) + "个菜谱";
//            //添加到集合里头
//            mData.add(data);
//        }
//        showGrid();
//    }
}
