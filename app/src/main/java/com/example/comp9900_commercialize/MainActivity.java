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
import com.example.comp9900_commercialize.bean.Collection;
import com.example.comp9900_commercialize.bean.ItemExplore;
import com.example.comp9900_commercialize.bean.Recipe;
import com.example.comp9900_commercialize.databinding.ActivityMainBinding;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private RecyclerView mList;
    private List<ItemExplore> mData;
    private StaggerAdapter mAdapter;
    private SwipeRefreshLayout refreshLayout;
    private FirebaseFirestore firebaseFirestore;
    private Preferences preferences;
    private Recipe recipe;

    private FirebaseFirestore db;
    private FirebaseUser user;
    private String like;
    private String otherLike;
    private String[] otherLikeArray;
    private List otherLikeList;
    private List likeList;
    private String[] likeArray;
    private double max;
    private List union;
    private List intersection;
    private List mostSimilarRecipes;
    //private String otherLikeEmail;
    private int count1;
    private int count2;
    private Map<String, Integer> allType;
    private String mostType;
    private int max1;
    private String temp1;
    private int temp2;
    private String recipetype;
    private Collection myCollection;
    private List allCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        //找到控件
        mList = binding.recyclerView;
        refreshLayout = binding.refreshLayout;
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

        mData = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference Ref = db.collection("collection").document(user.getEmail());
        DocumentReference Reference = db.collection("users").document(user.getEmail());

        Ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                myCollection = documentSnapshot.toObject(Collection.class);
                allCollection = myCollection.collectionList;
            }
        });



        Reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        like = document.get("Like List").toString();
                        likeArray = like.split(",");
                        likeList = Arrays.asList(likeArray);
                        allType = new HashMap<String, Integer>();
                        for(String recep : likeArray){
                            DocumentReference Ref = firebaseFirestore.collection("recipes").document(recep);
                            Ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            recipetype = document.get("recipeType").toString();
                                            //Toast.makeText(MainActivity.this, recipetype, Toast.LENGTH_SHORT).show();
                                            if(allType.containsKey(recipetype)){
                                                int old = allType.get(recipetype);
                                                allType.put(recipetype,old+1);
                                            }
                                            else{
                                                allType.put(recipetype,1);
                                            }
                                            mostType = "";
                                            max1 = 0;
                                            temp2 = 0;
                                            for (Map.Entry<String, Integer> entry : allType.entrySet()){
                                                temp1 = entry.getKey();
                                                temp2 = entry.getValue();
                                                //Toast.makeText(MainActivity.this, temp1, Toast.LENGTH_SHORT).show();
                                                //Toast.makeText(MainActivity.this, String.valueOf(temp2), Toast.LENGTH_SHORT).show();
                                                if(temp2 > max1){
                                                    max1 = temp2;
                                                    mostType = temp1;
                                                }
                                            }
                                            //Toast.makeText(MainActivity.this, mostType, Toast.LENGTH_SHORT).show();
                                            //Toast.makeText(MainActivity.this, String.valueOf(max1), Toast.LENGTH_SHORT).show();
                                            if(max1 == 1){
                                                mostType = "";
                                            }
                                            //Toast.makeText(MainActivity.this, mostType, Toast.LENGTH_SHORT).show();
                                            //Toast.makeText(MainActivity.this, String.valueOf(max1), Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
        Reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //Toast.makeText(MainActivity.this, "1", Toast.LENGTH_SHORT).show();
                        like = document.get("Like List").toString();
                        likeArray = like.split(",");
                        likeList = Arrays.asList(likeArray);
                        //user with no like
                        if(like == ""){
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
                                            }
                                            else { // error handling
                                                Toast.makeText(MainActivity.this, "Error getting documents.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                        //user with like
                        else{
                            //Toast.makeText(MainActivity.this, "2", Toast.LENGTH_SHORT).show();

                            db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        //Toast.makeText(MainActivity.this, "3", Toast.LENGTH_SHORT).show();

                                        max = 0;
                                        mostSimilarRecipes = new ArrayList<>();;
                                        for (QueryDocumentSnapshot document1 : task.getResult()) {
                                            //Toast.makeText(MainActivity.this, document1.get("E-mail").toString(), Toast.LENGTH_SHORT).show();

                                            otherLike = document1.get("Like List").toString();
                                            if (otherLike != "") {
                                                otherLikeArray = otherLike.split(",");
                                                otherLikeList = Arrays.asList(otherLikeArray);
                                                union = new ArrayList<>();
                                                intersection = new ArrayList<>();
                                                for (String s1 : likeArray) {
                                                    for (String s2 : otherLikeArray) {
                                                        if (s1.equals(s2)) {
                                                            intersection.add(s1);
                                                        }
                                                    }
                                                }

                                                for (String s1 : likeArray) {
                                                    if (union.contains(s1) == false) {
                                                        union.add(s1);
                                                    }
                                                }
                                                for (String s2 : otherLikeArray) {
                                                    if (union.contains(s2) == false) {
                                                        union.add(s2);
                                                    }
                                                }

                                                double length1 = Double.parseDouble(String.valueOf(intersection.size()));
                                                double length2 = Double.parseDouble(String.valueOf(union.size()));
                                                //Toast.makeText(MainActivity.this, String.valueOf(intersection.size() / union.size()), Toast.LENGTH_SHORT).show();
                                                //Toast.makeText(MainActivity.this, String.valueOf(intersection.size()), Toast.LENGTH_SHORT).show();
                                                //Toast.makeText(MainActivity.this, String.valueOf(union.size()), Toast.LENGTH_SHORT).show();


                                                if (length1 / length2 > max) {
                                                    if (document1.get("E-mail").toString().equals(user.getEmail()) == false) {
                                                        max = length1 / length2;
                                                        mostSimilarRecipes = otherLikeList;
                                                        //Toast.makeText(MainActivity.this, String.valueOf(max), Toast.LENGTH_SHORT).show();
                                                        //Toast.makeText(MainActivity.this, user.getEmail(), Toast.LENGTH_SHORT).show();

                                                        //Toast.makeText(MainActivity.this, document1.get("E-mail").toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        }
                                        //Toast.makeText(MainActivity.this, String.valueOf(max), Toast.LENGTH_SHORT).show();


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
                                                            count1 = 0;
                                                            count2 = 0;
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
                                                                //Toast.makeText(MainActivity.this, "4", Toast.LENGTH_SHORT).show();
                                                                //Toast.makeText(MainActivity.this, mostType, Toast.LENGTH_SHORT).show();
                                                                if(!allCollection.contains(recipe.recipeId)){
                                                                    if(count1 < 2){
                                                                        if(mostSimilarRecipes.contains(recipe.recipeId)){
                                                                            mData.add(count1,explore);
                                                                            count1 += 1;
                                                                            //Toast.makeText(MainActivity.this, "1", Toast.LENGTH_SHORT).show();
                                                                            //Toast.makeText(MainActivity.this, recipe.recipeName, Toast.LENGTH_SHORT).show();
                                                                        }
                                                                        else{
                                                                            if(count2 < 2){
                                                                                if(recipe.recipeType == mostType){
                                                                                    mData.add(count1+count2,explore);
                                                                                    count2 += 1;
                                                                                    //Toast.makeText(MainActivity.this, "2", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                                else{
                                                                                    mData.add(explore);
                                                                                    //Toast.makeText(MainActivity.this, "3", Toast.LENGTH_SHORT).show();
                                                                                    //Toast.makeText(MainActivity.this, recipe.recipeName, Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                            else{
                                                                                mData.add(explore);
                                                                                //Toast.makeText(MainActivity.this, "4", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    }
                                                                    else{
                                                                        if(count2 < 2){
                                                                            if(recipe.recipeType.equals(mostType)){
                                                                                mData.add(count1+count2,explore);
                                                                                count2 += 1;
                                                                                //Toast.makeText(MainActivity.this, "5", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                            else{
                                                                                mData.add(explore);
                                                                                //Toast.makeText(MainActivity.this, "6", Toast.LENGTH_SHORT).show();
                                                                                //Toast.makeText(MainActivity.this, recipe.recipeName, Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                        else{
                                                                            mData.add(explore);
                                                                            //Toast.makeText(MainActivity.this, "7", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                }

                                                            }
                                                            binding.progressBar.setVisibility(View.GONE);
                                                            binding.tvLoading.setVisibility(View.GONE);
                                                            showStagger(true, false);
                                                        }
                                                        else { // error handling
                                                            Toast.makeText(MainActivity.this, "Error getting documents.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                    }
                                }
                            });
                        }
                    }
                }
            }

        });
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
        //创建数据集合409824
        mData = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference Ref = db.collection("collection").document(user.getEmail());
        DocumentReference Reference = db.collection("users").document(user.getEmail());

        Ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                myCollection = documentSnapshot.toObject(Collection.class);
                allCollection = myCollection.collectionList;
            }
        });



        Reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        like = document.get("Like List").toString();
                        likeArray = like.split(",");
                        likeList = Arrays.asList(likeArray);
                        allType = new HashMap<String, Integer>();
                        for(String recep : likeArray){
                            DocumentReference Ref = firebaseFirestore.collection("recipes").document(recep);
                            Ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            recipetype = document.get("recipeType").toString();
                                            //Toast.makeText(MainActivity.this, recipetype, Toast.LENGTH_SHORT).show();
                                            if(allType.containsKey(recipetype)){
                                                int old = allType.get(recipetype);
                                                allType.put(recipetype,old+1);
                                            }
                                            else{
                                                allType.put(recipetype,1);
                                            }
                                            mostType = "";
                                            max1 = 0;
                                            temp2 = 0;
                                            for (Map.Entry<String, Integer> entry : allType.entrySet()){
                                                temp1 = entry.getKey();
                                                temp2 = entry.getValue();
                                                //Toast.makeText(MainActivity.this, temp1, Toast.LENGTH_SHORT).show();
                                                //Toast.makeText(MainActivity.this, String.valueOf(temp2), Toast.LENGTH_SHORT).show();
                                                if(temp2 > max1){
                                                    max1 = temp2;
                                                    mostType = temp1;
                                                }
                                            }
                                            //Toast.makeText(MainActivity.this, mostType, Toast.LENGTH_SHORT).show();
                                            //Toast.makeText(MainActivity.this, String.valueOf(max1), Toast.LENGTH_SHORT).show();
                                            if(max1 == 1){
                                                mostType = "";
                                            }
                                            //Toast.makeText(MainActivity.this, mostType, Toast.LENGTH_SHORT).show();
                                            //Toast.makeText(MainActivity.this, String.valueOf(max1), Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
        Reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //Toast.makeText(MainActivity.this, "1", Toast.LENGTH_SHORT).show();
                        like = document.get("Like List").toString();
                        likeArray = like.split(",");
                        likeList = Arrays.asList(likeArray);
                        //user with no like
                        if(like == ""){
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
                                            }
                                            else { // error handling
                                                Toast.makeText(MainActivity.this, "Error getting documents.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                        //user with like
                        else{
                            //Toast.makeText(MainActivity.this, "2", Toast.LENGTH_SHORT).show();

                            db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        //Toast.makeText(MainActivity.this, "3", Toast.LENGTH_SHORT).show();

                                        max = 0;
                                        mostSimilarRecipes = new ArrayList<>();;
                                        for (QueryDocumentSnapshot document1 : task.getResult()) {
                                            //Toast.makeText(MainActivity.this, document1.get("E-mail").toString(), Toast.LENGTH_SHORT).show();

                                            otherLike = document1.get("Like List").toString();
                                            if (otherLike != "") {
                                                otherLikeArray = otherLike.split(",");
                                                otherLikeList = Arrays.asList(otherLikeArray);
                                                union = new ArrayList<>();
                                                intersection = new ArrayList<>();
                                                for (String s1 : likeArray) {
                                                    for (String s2 : otherLikeArray) {
                                                        if (s1.equals(s2)) {
                                                            intersection.add(s1);
                                                        }
                                                    }
                                                }

                                                for (String s1 : likeArray) {
                                                    if (union.contains(s1) == false) {
                                                        union.add(s1);
                                                    }
                                                }
                                                for (String s2 : otherLikeArray) {
                                                    if (union.contains(s2) == false) {
                                                        union.add(s2);
                                                    }
                                                }

                                                double length1 = Double.parseDouble(String.valueOf(intersection.size()));
                                                double length2 = Double.parseDouble(String.valueOf(union.size()));
                                                //Toast.makeText(MainActivity.this, String.valueOf(intersection.size() / union.size()), Toast.LENGTH_SHORT).show();
                                                //Toast.makeText(MainActivity.this, String.valueOf(intersection.size()), Toast.LENGTH_SHORT).show();
                                                //Toast.makeText(MainActivity.this, String.valueOf(union.size()), Toast.LENGTH_SHORT).show();


                                                if (length1 / length2 > max) {
                                                    if (document1.get("E-mail").toString().equals(user.getEmail()) == false) {
                                                        max = length1 / length2;
                                                        mostSimilarRecipes = otherLikeList;
                                                        //Toast.makeText(MainActivity.this, String.valueOf(max), Toast.LENGTH_SHORT).show();
                                                        //Toast.makeText(MainActivity.this, user.getEmail(), Toast.LENGTH_SHORT).show();

                                                        //Toast.makeText(MainActivity.this, document1.get("E-mail").toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        }
                                        //Toast.makeText(MainActivity.this, String.valueOf(max), Toast.LENGTH_SHORT).show();


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
                                                            count1 = 0;
                                                            count2 = 0;
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
                                                                //Toast.makeText(MainActivity.this, "4", Toast.LENGTH_SHORT).show();
                                                                //Toast.makeText(MainActivity.this, mostType, Toast.LENGTH_SHORT).show();
                                                                if(!allCollection.contains(recipe.recipeId)){
                                                                    if(count1 < 2){
                                                                        if(mostSimilarRecipes.contains(recipe.recipeId)){
                                                                            mData.add(count1,explore);
                                                                            count1 += 1;
                                                                            //Toast.makeText(MainActivity.this, "1", Toast.LENGTH_SHORT).show();
                                                                            //Toast.makeText(MainActivity.this, recipe.recipeName, Toast.LENGTH_SHORT).show();
                                                                        }
                                                                        else{
                                                                            if(count2 < 2){
                                                                                if(recipe.recipeType == mostType){
                                                                                    mData.add(count1+count2,explore);
                                                                                    count2 += 1;
                                                                                    //Toast.makeText(MainActivity.this, "2", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                                else{
                                                                                    mData.add(explore);
                                                                                    //Toast.makeText(MainActivity.this, "3", Toast.LENGTH_SHORT).show();
                                                                                    //Toast.makeText(MainActivity.this, recipe.recipeName, Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                            else{
                                                                                mData.add(explore);
                                                                                //Toast.makeText(MainActivity.this, "4", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    }
                                                                    else{
                                                                        if(count2 < 2){
                                                                            if(recipe.recipeType.equals(mostType)){
                                                                                mData.add(count1+count2,explore);
                                                                                count2 += 1;
                                                                                //Toast.makeText(MainActivity.this, "5", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                            else{
                                                                                mData.add(explore);
                                                                                //Toast.makeText(MainActivity.this, "6", Toast.LENGTH_SHORT).show();
                                                                                //Toast.makeText(MainActivity.this, recipe.recipeName, Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                        else{
                                                                            mData.add(explore);
                                                                            //Toast.makeText(MainActivity.this, "7", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                }

                                                            }
                                                            binding.progressBar.setVisibility(View.GONE);
                                                            binding.tvLoading.setVisibility(View.GONE);
                                                            showStagger(true, false);
                                                        }
                                                        else { // error handling
                                                            Toast.makeText(MainActivity.this, "Error getting documents.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                    }
                                }
                            });
                        }
                    }
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
