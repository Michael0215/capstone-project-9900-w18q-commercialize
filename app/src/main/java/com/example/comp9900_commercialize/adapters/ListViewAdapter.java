package com.example.comp9900_commercialize.adapters;

import android.view.View;
import android.view.ViewGroup;

import com.example.comp9900_commercialize.R;
import com.example.comp9900_commercialize.bean.ItemExplore;

import java.util.List;

public class ListViewAdapter extends RecyclerViewBaseAdapter {

    public ListViewAdapter(List<ItemExplore> data) {
        super(data);
    }

    @Override
    protected View getSubView(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_list_view, null);
        return view;
    }
}
