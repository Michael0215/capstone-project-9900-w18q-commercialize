package com.example.comp9900_commercialize;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.comp9900_commercialize.adapters.StaggerAdapter;
import com.example.comp9900_commercialize.bean.ItemExplore;
import com.example.comp9900_commercialize.bean.Recipe;
import com.example.comp9900_commercialize.databinding.ActivityMainBinding;
import com.example.comp9900_commercialize.utilities.MacroDef;
import com.example.comp9900_commercialize.utilities.Preferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private RecyclerView mList;
    private List<ItemExplore> mData;
    private StaggerAdapter mAdapter;
    private SwipeRefreshLayout refreshLayout;
    private FirebaseFirestore firebaseFirestore;
    private Preferences preferences;
    private Recipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        //找到控件
        mList = this.findViewById(R.id.recycler_view);
        refreshLayout = this.findViewById(R.id.refresh_layout);
        firebaseFirestore = FirebaseFirestore.getInstance();
        preferences = new Preferences(getApplicationContext());
        //准备数据
        initData();

        handlerDownPullUpdate();
    }

    private void refresh(){
        //clear all posts in the listview
        mData.clear();
        // retrieve all the post in the firestore's table 'posts'
        CollectionReference posts = firebaseFirestore.collection("recipes");
        // order the post in creating time order
        Query query = posts.orderBy("recipeLikesNum", Query.Direction.DESCENDING);
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // retrieve all posts in the 'posts' table
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ItemExplore explore = new ItemExplore();
                                recipe = document.toObject(Recipe.class);
                                 if(recipe.recipeContributorAvatar != null){
                                     byte[] bytes = Base64.decode(recipe.recipeContributorAvatar, Base64.DEFAULT);
                                     explore.avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                 } else {
                                     @SuppressLint("ResourceType") InputStream img_avatar = getResources().openRawResource(R.drawable.default_avatar);
                                     explore.avatar = BitmapFactory.decodeStream(img_avatar);
                                 }
                                byte[] bytes = Base64.decode(recipe.recipeCover, Base64.DEFAULT);
                                explore.icon =  BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                explore.tv_contributor_name = recipe.recipeContributorName;
                                explore.tv_like_num = String.valueOf(recipe.recipeLikesNum);
                                explore.tv_comment_num = String.valueOf(recipe.recipeCommentsNum);
                                explore.title = recipe.recipeName;
                                explore.id = document.getId();
                                explore.icon_comment = R.drawable.ic_comment;
                                explore.icon_like = R.drawable.ic_like;
                                mData.add(explore);
                            }
                            binding.progressBar.setVisibility(View.GONE);
                            binding.tvLoading.setVisibility(View.GONE);
                            showStagger(true, false);
                        } else { // error handling
                            Toast.makeText(MainActivity.this, "Error getting documents.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
//                        mAdapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);
        Toast.makeText(MainActivity.this, "Refresh Succeed!", Toast.LENGTH_SHORT).show();
    }

    private void handlerDownPullUpdate() {
        refreshLayout.setEnabled(true);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                },2000);
            }
        });

    }

    //只是方法用于初始化模拟数据
    private void initData() {
        //list<Data>-->Adapter-->SetAdapter-->显示数据
        //创建数据集合
        mData = new ArrayList<>();
        CollectionReference posts = firebaseFirestore.collection("recipes");
        // order the post in creating time order
        Query query = posts.orderBy("recipeLikesNum", Query.Direction.DESCENDING);
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // retrieve all posts in the 'posts' table
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ItemExplore explore = new ItemExplore();
                                recipe = document.toObject(Recipe.class);
                                if(recipe.recipeContributorAvatar != null){
                                    byte[] bytes = Base64.decode(recipe.recipeContributorAvatar, Base64.DEFAULT);
                                    explore.avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                } else {
                                    @SuppressLint("ResourceType") InputStream img_avatar = getResources().openRawResource(R.drawable.default_avatar);
                                    explore.avatar = BitmapFactory.decodeStream(img_avatar);
                                }
                                byte[] bytes = Base64.decode(recipe.recipeCover, Base64.DEFAULT);
                                explore.icon =  BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                explore.tv_contributor_name = recipe.recipeContributorName;
                                explore.tv_like_num = String.valueOf(recipe.recipeLikesNum);
                                explore.tv_comment_num = String.valueOf(recipe.recipeCommentsNum);
                                explore.title = recipe.recipeName;
                                explore.id = document.getId();
                                explore.icon_comment = R.drawable.ic_comment;
                                explore.icon_like = R.drawable.ic_like;
                                mData.add(explore);
                            }
                            showStagger(true, false);
                        } else { // error handling
                            Toast.makeText(MainActivity.this, "Error getting documents.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showStagger(boolean isVertical, boolean isReverse) {
        //准备布局管理器
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, isVertical?StaggeredGridLayoutManager.VERTICAL:StaggeredGridLayoutManager.HORIZONTAL);
        //设置布局管理器的方向
        layoutManager.setReverseLayout(isReverse);
        //设置布局管理器到RecyclerView里
        mList.setLayoutManager(layoutManager);
        //创建适配器
        mAdapter = new StaggerAdapter(mData);
        //设置适配器
        mList.setAdapter(mAdapter);
        binding.progressBar.setVisibility(View.GONE);
        binding.tvLoading.setVisibility(View.GONE);
        mList.postInvalidate();
        //初始化事件RecyclerView
        initListener();
    }


    //RecyclerView doesn't have clickOnListener
    private void initListener() {
        mAdapter.setOnItemClickListener(new StaggerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //这里处理图片的点击事件，该干嘛就干嘛，跳转的就跳转。。。
                preferences.putString(MacroDef.KEY_RECIPE_ID, mData.get(position).id);
//                Toast.makeText(MainActivity.this, "您点击的是第" + (position+1) + "个菜谱", Toast.LENGTH_SHORT).show();
//                Toast.makeText(MainActivity.this, "您点击的菜谱id为" + preferences.getString(MacroDef.KEY_RECIPE_ID), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), RecipeDetailActivity.class));
            }
        }) ;
    }

    private void setListeners(){
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
        binding.ibProfile.setOnClickListener(v -> {
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    finish();
                });
        binding.btNotice.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), ChatMainActivity.class)));
    }
}