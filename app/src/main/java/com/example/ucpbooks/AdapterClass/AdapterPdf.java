package com.example.ucpbooks.AdapterClass;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ucpbooks.ModelClass.ModelPdf;
import com.example.ucpbooks.MyApplication;
import com.example.ucpbooks.PdfDetailfragment;
import com.example.ucpbooks.PdfList;
import com.example.ucpbooks.R;
import com.example.ucpbooks.databinding.RowPdfBinding;
import com.example.ucpbooks.filters.FilterPdf;
import com.github.barteksc.pdfviewer.PDFView;

import java.util.ArrayList;

public class AdapterPdf extends RecyclerView.Adapter<AdapterPdf.HolderPdf> implements Filterable {
    private Context context;
    public ArrayList<ModelPdf> pdfArrayList,filterList;
    private RowPdfBinding binding;
    private static final String TAG="PDF_ADAPTER";
    private FilterPdf filter;
    private ProgressDialog progressDialog;

    public AdapterPdf(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList = pdfArrayList;
        progressDialog=new ProgressDialog(context);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @NonNull
    @Override
    public AdapterPdf.HolderPdf onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding=RowPdfBinding.inflate(LayoutInflater.from(context),parent,false);
        return new HolderPdf(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPdf.HolderPdf holder, int position) {
        ModelPdf  modelPdf=pdfArrayList.get(position);
        String pdfId=modelPdf.getId();
        String categoryId=modelPdf.getCategoryId();
        String title=modelPdf.getTitle();
        String description=modelPdf.getDescription();
        String pdfUrl=modelPdf.getUrl();
        holder.title.setText(title);
        holder.desc.setText(description);

        MyApplication.loadCategory(""+categoryId,holder.category
        );

        MyApplication.loadPdfFromUrlSinglePage(
                ""+pdfUrl,
                ""+title,
                holder.pdfView,
                holder.progressBar);
        MyApplication.loadPdfSize(
                ""+pdfUrl,
                ""+title,
                holder.size
        );
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new PdfDetailfragment();
                Bundle bundle=new Bundle();
                bundle.putString("bookId",pdfId);
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
        return pdfArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter==null){
            filter=new FilterPdf(filterList,this);
        }
        return filter;
    }

    public class HolderPdf extends RecyclerView.ViewHolder {
        PDFView pdfView;
        ProgressBar progressBar;
        TextView title,desc,size,category;
        public HolderPdf(@NonNull View itemView) {
            super(itemView);
            pdfView=binding.pdfview;
            progressBar=binding.progressbars;
            title=binding.btitle;
            desc=binding.description;
            size=binding.sizes;
            category=binding.bcategory;

        }
    }


}
