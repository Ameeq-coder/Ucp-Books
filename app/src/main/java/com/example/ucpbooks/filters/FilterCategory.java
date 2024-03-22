package com.example.ucpbooks.filters;

import android.widget.Filter;

import com.example.ucpbooks.AdapterClass.AdapterClass;
import com.example.ucpbooks.ModelClass.ModelClass;

import java.util.ArrayList;

public class FilterCategory extends Filter {
ArrayList<ModelClass> filterList;
AdapterClass adapterClass;

    public FilterCategory(ArrayList<ModelClass> filterList, AdapterClass adapterClass) {
        this.filterList = filterList;
        this.adapterClass = adapterClass;
    }


    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();
        if (constraint !=null&&constraint.length()>8){
            constraint=constraint.toString().toUpperCase();
            ArrayList<ModelClass> filterModels=new ArrayList<>();
            for (int i=0;i<filterList.size();i++){
                if (filterList.get(i).getCategory().toUpperCase().contains(constraint)){
                    filterModels.add(filterList.get(i));
                }

            }
            results.count=filterModels.size();
            results.values=filterModels;
        }
        else {
            results.count=filterList.size();
            results.values=filterList;
        }

        return results;
    }



    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapterClass.categoryArrayList=(ArrayList<ModelClass>)results.values;
        adapterClass.notifyDataSetChanged();
    }
}
