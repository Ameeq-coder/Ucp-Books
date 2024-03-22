package com.example.ucpbooks;



import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ucpbooks.AdapterClass.AdapterPdf;
import com.example.ucpbooks.ModelClass.ModelPdf;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class PdfList extends Fragment {
public ArrayList<ModelPdf>  pdfArrayList;
public AdapterPdf adapterPdf;
EditText editText;
RecyclerView recyclerView;
String categoryId,categoryTitle;
TextView textView;
ImageButton imageButton;
    private static final String TAG="PDF_LIST_TAG";


    public PdfList() {

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pdf_list, container, false);
        textView=view.findViewById(R.id.categoryh);
        editText=view.findViewById(R.id.searchbar);
        recyclerView=view.findViewById(R.id.bookrvs);
        imageButton=view.findViewById(R.id.backrv);
        Bundle bundle=getArguments();
        categoryId= bundle.getString("Category Id");
        categoryTitle=bundle.getString("Category Title");
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             Fragment fragment=new SemesterLists();
                fragment.setArguments(bundle);
                ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.framelayout,fragment, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });
        loadpdflist();
        textView.setText(categoryTitle);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapterPdf.getFilter().filter(s);
                }catch (Exception e){
                    Log.d(TAG,"onTextChanged:"+e.getMessage());

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }
public void loadpdflist(){
pdfArrayList=new ArrayList<>();
DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Books");
databaseReference.orderByChild("categoryId").equalTo(categoryId).addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
pdfArrayList.clear();
for (DataSnapshot dataSnapshot:snapshot.getChildren()){
ModelPdf modelPdf=dataSnapshot.getValue(ModelPdf.class);
pdfArrayList.add(modelPdf);
Log.d(TAG,"on Data Changed"+modelPdf.getId()+""+modelPdf.getTitle());
}
        adapterPdf=new AdapterPdf(getContext(),pdfArrayList);
        recyclerView.setAdapter(adapterPdf);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
});
    }

    private OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
            public void handleOnBackPressed() {
            Bundle bundle=getArguments();
            Fragment fragment=new SemesterLists();
            fragment.setArguments(bundle);
            ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.framelayout,fragment, "findThisFragment")
                    .addToBackStack(null)
                    .commit();
        }
    };

    // Register the callback in the onCreate() method
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    // Unregister the callback in the onDestroy() method
    @Override
    public void onDestroy() {
        super.onDestroy();
        onBackPressedCallback.remove();
    }



}