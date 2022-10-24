package com.example.comp9900_commercialize;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.comp9900_commercialize.adapters.CollectionAdapter;

import com.example.comp9900_commercialize.bean.Collection;
import com.example.comp9900_commercialize.bean.ItemCollection;

import com.example.comp9900_commercialize.bean.Recipe;
import com.example.comp9900_commercialize.databinding.ActivityCollectionBinding;
import com.example.comp9900_commercialize.utilities.MacroDef;
import com.example.comp9900_commercialize.utilities.Preferences;
import com.google.android.gms.tasks.OnCompleteListener;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class CollectionActivity extends AppCompatActivity {

    private static final String TAG = "CollectionActivity";

    private ActivityCollectionBinding binding;
    Collection myCollection;
    private List<String> myCollectionList;
    private FirebaseUser user;
    private String userEmail;
    private FirebaseFirestore db;
    private Preferences preferences;
    private RecyclerView mList;
    private CollectionAdapter adapter;
    private List<ItemCollection> mData;
    private FirebaseFirestore firebaseFirestore;
    private Recipe recipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCollectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mList = this.findViewById(R.id.rcv_all_collection);
        firebaseFirestore = FirebaseFirestore.getInstance();
        init();
        loadData();

        setListeners();

    }


    private void init() {
        preferences = new Preferences(getApplicationContext());
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }

    private void loadData() {

        //创建数据集合
        mData = new ArrayList<>();
        //创建模拟数据
        DocumentReference docRef = firebaseFirestore.collection("collection").document(preferences.getString(MacroDef.KEY_EMAIL));
        // retrieve all the post in the firestore's table 'posts'
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                myCollection = documentSnapshot.toObject(Collection.class);
                if (myCollection != null) {
                    myCollectionList = myCollection.collectionList;
                    if (!myCollectionList.isEmpty()) {
                        CollectionReference posts = firebaseFirestore.collection("recipes");
                        // order the post in creating time order
                        Query query = posts.whereIn(FieldPath.documentId(), myCollectionList);
//        Query query = posts.orderBy("recipePublishTime", Query.Direction.DESCENDING); //.whereEqualTo("recipeContributorEmail","767831805@qq.com");
                        query.get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            // retrieve all posts in the 'posts' table
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                ItemCollection recipe = new ItemCollection();
//                                                Toast.makeText(MainActivity.this, "Refresh Success!", Toast.LENGTH_SHORT).show();

                                                recipes = document.toObject(Recipe.class);
                                                byte[] bytes = Base64.decode(recipes.recipeCover, Base64.DEFAULT);
                                                recipe.icon = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                recipe.title = recipes.recipeName;
                                                if (recipes.recipeContributorAvatar != null) {
                                                    byte[] bytes_avatar = Base64.decode(recipes.recipeContributorAvatar, Base64.DEFAULT);
                                                    recipe.avatar = BitmapFactory.decodeByteArray(bytes_avatar, 0, bytes_avatar.length);
                                                } else {
                                                    @SuppressLint("ResourceType") InputStream img_avatar = getResources().openRawResource(R.drawable.default_avatar);
                                                    recipe.avatar = BitmapFactory.decodeStream(img_avatar);
                                                }
                                                recipe.tv_contributor_name = recipes.recipeContributorName;
                                                recipe.id = document.getId();
                                                mData.add(recipe);
                                            }
                                            showGrid();
                                        } else { // error handling
//                            Toast.makeText(CollectionActivity.this, mUser.toString(), Toast.LENGTH_SHORT).show();
                                            Toast.makeText(CollectionActivity.this, "Error getting documents.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }

                }
            }
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
        adapter = new CollectionAdapter(mData);
        //设置适配器
        mList.setAdapter(adapter);
        //初始化事件RecyclerView
        initListener();
    }

    private void initListener() {
        adapter.setOnItemClickListener(new CollectionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                preferences.putString(MacroDef.KEY_RECIPE_ID, mData.get(position).id);
//                Toast.makeText(CollectionActivity.this, "您点击的菜谱id为" + preferences.getString(MacroDef.KEY_RECIPE_ID), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), RecipeDetailActivity.class));
            }
        });
    }

    private void setListeners() {
        binding.btCancel.setOnClickListener(v ->
                onBackPressed());
        binding.btNotice.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), NoticeActivity.class)));
        userEmail = user.getEmail();
        //读取userEmail中所有collection
        DocumentReference docRef = db.collection("collection").document(userEmail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        //email-> 一个arraylist 里面存recipeId
        //adapter显示出来
    }
}
