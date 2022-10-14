package com.example.comp9900_commercialize;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.comp9900_commercialize.adapters.ListViewAdapter;
import com.example.comp9900_commercialize.adapters.StaggerAdapter;
import com.example.comp9900_commercialize.bean.Datas;
import com.example.comp9900_commercialize.bean.ItemBean;
import com.example.comp9900_commercialize.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private RecyclerView mList;
    private List<ItemBean> mData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        //找到控件
        mList = (RecyclerView) this.findViewById(R.id.recycler_view);
        //准备数据
        initData();
    }

    //只是方法用于初始化模拟数据
    private void initData() {
        //list<Data>-->Adapter-->SetAdapter-->显示数据
        //创建数据集合
        mData = new ArrayList<>();
        //创建模拟数据
        for (int i = 0; i < Datas.icons.length; i++){
            //创建数据对象
            ItemBean data = new ItemBean();
            data.icon = Datas.icons[i];
            data.title = "我是第" + (i+1) + "张图";
            //添加到集合里头
            mData.add(data);
        }
        //RecyclerView需要设置样式，其实就是设置布局管理器,在这里设置瀑布流，线性流，还是网格流
        //线性流布局管理器
//        showLinear();
        //瀑布流布局管理器
        showStagger(true, false);
    }
    private void showLinear() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mList.setLayoutManager(layoutManager);
        //创建适配器
        ListViewAdapter adapter = new ListViewAdapter(mData);
        //设置到RecyclerView里头
        mList.setAdapter(adapter);
    }

    private void showStagger(boolean isVertical, boolean isReverse) {
        //准备布局管理器
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, isVertical?StaggeredGridLayoutManager.VERTICAL:StaggeredGridLayoutManager.HORIZONTAL);
        //设置布局管理器的方向
        layoutManager.setReverseLayout(isReverse);
        //设置布局管理器到RecyclerView里
        mList.setLayoutManager(layoutManager);
        //创建适配器
        StaggerAdapter adapter = new StaggerAdapter(mData);
        //设置适配器
        mList.setAdapter(adapter);
    }

    private void setListeners(){
        binding.ibSearch.setOnClickListener(v -> {
                    startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                    finish();
                });
        binding.ibCreate.setOnClickListener(v -> {
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
                startActivity(new Intent(getApplicationContext(), NoticeActivity.class)));
    }


}