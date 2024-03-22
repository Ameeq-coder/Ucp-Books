package com.example.ucpbooks.filters;

import android.widget.Filter;

import com.example.ucpbooks.AdapterClass.AdapterPdf;
import com.example.ucpbooks.ModelClass.ModelPdf;

import java.util.ArrayList;

public class FilterPdf extends Filter {
    ArrayList<ModelPdf> filterList;
    AdapterPdf adapterPdf;

    public FilterPdf(ArrayList<ModelPdf> filterList, AdapterPdf adapterPdf) {
        this.filterList = filterList;
        this.adapterPdf = adapterPdf;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if (constraint != null && constraint.length() > 8) {
            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelPdf> filterModels = new ArrayList<>();
            for (int i = 0; i < filterList.size(); i++) {
                if (filterList.get(i).getTitle().toUpperCase().contains(constraint)) {
                    filterModels.add(filterList.get(i));
                }

            }
            results.count = filterModels.size();
            results.values = filterModels;
        } else {
            results.count = filterList.size();
            results.values = filterList;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapterPdf.pdfArrayList = (ArrayList<ModelPdf>) results.values;
        adapterPdf.notifyDataSetChanged();

    }
}

