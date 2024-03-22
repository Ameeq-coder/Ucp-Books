package com.example.ucpbooks.AdapterClass;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ucpbooks.PdfList;
import com.example.ucpbooks.R;
import com.example.ucpbooks.filters.FilterCategory;
import com.example.ucpbooks.ModelClass.ModelClass;
import com.example.ucpbooks.databinding.SemesterCardBinding;

import java.util.ArrayList;

public class AdapterClass extends RecyclerView.Adapter<AdapterClass.HolderCategory> implements Filterable {
public Context context;
public ArrayList<ModelClass> categoryArrayList,filterList;
public SemesterCardBinding binding;

public FilterCategory filter;

    public AdapterClass(Context context, ArrayList<ModelClass> categoryArrayList) {
        this.context = context;
        this.categoryArrayList = categoryArrayList;
        this.filterList = categoryArrayList;
    }

    @NonNull
    @Override
    public AdapterClass.HolderCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding=SemesterCardBinding.inflate(LayoutInflater.from(context),parent,false);
        return new HolderCategory(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterClass.HolderCategory holder, @SuppressLint("RecyclerView") int position) {
        ModelClass modelClass=categoryArrayList.get(position);
        String id= modelClass.getId();
        String category= modelClass.getCategory();
        String uid= modelClass.getUid();
        long timestamp=modelClass.getTimestamp();

        holder.categorytitle.setText(category);
holder.itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Fragment fragment = new PdfList();
        Bundle bundle=new Bundle();
        bundle.putString("Category Id",id);
        bundle.putString("Category Title",category);

        fragment.setArguments(bundle);
        ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction()
                .replace(R.id.framelayout,fragment, "findThisFragment")
                .addToBackStack(null)
                .commit();

    }
});

    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    @Override
    public Filter getFilter() {
if (filter==null){
filter=new FilterCategory(filterList,this);
}
        return filter;
    }

    public class HolderCategory extends RecyclerView.ViewHolder {
        TextView categorytitle;
        ImageView arrow;
        public HolderCategory(@NonNull View itemView) {
            super(itemView);
            categorytitle=binding.categorytitle;
            arrow=binding.imgs;

        }
    }


}
