package com.example.comp9900_commercialize;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.example.comp9900_commercialize.adapters.ProfileRecipeAdapter;
import com.example.comp9900_commercialize.adapters.SubscribeAdapter;
import com.example.comp9900_commercialize.bean.LastFeed;
import com.example.comp9900_commercialize.bean.Recipe;
import com.example.comp9900_commercialize.databinding.ActivitySubscribeBinding;
import com.example.comp9900_commercialize.utilities.MacroDef;
import com.example.comp9900_commercialize.utilities.Preferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SubscribeActivity extends AppCompatActivity {

    private ActivitySubscribeBinding binding;
    private Preferences preferences;
    private FirebaseFirestore firebaseFirestore;
    private List<Recipe> mData;
    private List<String> followList;
    private RecyclerView recyclerView;
    private SubscribeAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private LastFeed lastFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubscribeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        recyclerView = binding.rcvSubscribe;
        refreshLayout = binding.refreshLayout;
        init();
        handlerDownPullUpdate();
        setListeners();
//        getFollowList();
    }

    private void handlerDownPullUpdate() {
        refreshLayout.setEnabled(true);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getFollowList();
                    }
                },2000);
            }
        });
    }

    private void setListeners(){
        binding.ibExplore.setOnClickListener(v -> {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                });
        binding.ibSearch.setOnClickListener(v -> {
                    startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                    finish();
                });
        binding.ibCreate.setOnClickListener(v -> {
            if(preferences.getString(MacroDef.KEY_USER_TYPE).equals("Contributor")){
                preferences.putBoolean(MacroDef.KEY_MODE_CREATE, true);
                startActivity(new Intent(getApplicationContext(), AddRecipeActivity.class));
            }else{
                Toast.makeText(SubscribeActivity.this, "You can not create a recipe as an explorer", Toast.LENGTH_SHORT).show();
            }
        });
        binding.ibProfile.setOnClickListener(v -> {
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    finish();
                });
        binding.btNotice.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), ChatMainActivity.class)));
    }

    private void init(){
        preferences = new Preferences(getApplicationContext());
//        preferences.putBoolean(MacroDef.KEY_NEWS_FEED, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        followList = new ArrayList<String>();
        getFollowList();
    }

    private void getFollowList(){

        CollectionReference collectionReference = firebaseFirestore.collection("follow");
        Query query = collectionReference.whereEqualTo(FieldPath.documentId(),preferences.getString(MacroDef.KEY_EMAIL));
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                followList = (List<String>) document.getData().get("followList");
                                loadData();
                            }
                        }else { // error handling
//                            Toast.makeText(SubscribeActivity.this, "Error getting documents."+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        refreshLayout.setRefreshing(false);
        Toast.makeText(SubscribeActivity.this, "Refresh Succeed!", Toast.LENGTH_SHORT).show();
    }

    private void loadData(){
        mData = new ArrayList<Recipe>();
        CollectionReference collectionReference = firebaseFirestore.collection("recipes");
        try{
            Query query = collectionReference.whereIn("recipeContributorEmail", followList).orderBy("recipePublishTime", Query.Direction.DESCENDING);;
            query.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                // retrieve all posts in the 'posts' table
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Recipe recipe = document.toObject(Recipe.class);
                                    mData.add(recipe);
                                }
                                showRecycler();
                            } else { // error handling
//                            Toast.makeText(SubscribeActivity.this, "Error getting documents."+task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }catch (Exception e){
            showRecycler();
        }
    }

    private void showRecycler() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SubscribeAdapter(mData);
        recyclerView.setAdapter(adapter);
        binding.progressBar.setVisibility(View.GONE);
        binding.tvLoading.setVisibility(View.GONE);
        recyclerView.postInvalidate();
        initListener();
        try{
            updateLastFeed();
        }catch (Exception e){}
//        System.out.println(mData.get(0).recipeName+mData.get(1).recipeName);

    }

    private void initListener() {
        adapter.setOnItemClickListener(new SubscribeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                preferences.putString(MacroDef.KEY_RECIPE_ID, mData.get(position).recipeId);
                preferences.putBoolean(MacroDef.KEY_MODE_CREATE, false);
                startActivity(new Intent(getApplicationContext(), RecipeDetailActivity.class));
            }
        }) ;
    }

    private void updateLastFeed(){
        lastFeed = new LastFeed();
        lastFeed.latestTime = mData.get(0).recipePublishTime;
        firebaseFirestore.collection("lastFeed").document(preferences.getString(MacroDef.KEY_EMAIL)).set(lastFeed)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@io.reactivex.rxjava3.annotations.NonNull Exception e) {
                    }
                });
    }

}