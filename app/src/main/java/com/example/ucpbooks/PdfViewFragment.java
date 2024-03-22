package com.example.ucpbooks;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PdfViewFragment extends Fragment {

PDFView pdfView;
ProgressBar progressBar;
TextView textView;
ImageButton imageButton;
    private String bookId;
    private static final String TAG="PDF_VIEW_TAG";


    public PdfViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_pdf_view, container, false);
        pdfView=view.findViewById(R.id.pdfView);
        imageButton=view.findViewById(R.id.backpress);
        Bundle bundle=getArguments();
        bookId=bundle.getString("bookId");
        progressBar=view.findViewById(R.id.progressbar);
        textView=view.findViewById(R.id.subtitle);
        setupOnBackPressed();
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment=new PdfDetailfragment();
                fragment.setArguments(bundle);
                ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.framelayout,fragment, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });
        loadBookDetails();
        return view;

    }

    private void loadBookDetails() {
        Log.d(TAG,"LoadBookDetails:Loading");
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Books");
        databaseReference.child(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String pdfurl=""+snapshot.child("url").getValue();
                Log.d(TAG,"OnDataChange: PDFURL"+pdfurl);
                loadBookFromUrl(pdfurl);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadBookFromUrl(String pdfurl) {
        Log.d(TAG,"loadBookFromUrl:Get Pdf from Storage");
        StorageReference storageReference= FirebaseStorage.getInstance().getReferenceFromUrl(pdfurl);
        storageReference.getBytes(Constants.MAX_BYTES_PDF).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                pdfView.fromBytes(bytes).swipeHorizontal(false).onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        int currentpage=(page+1);
                        textView.setText(currentpage+"/"+pageCount);
                        Log.d(TAG,"onPageChanged : "+currentpage+"/"+pageCount);
                    }
                }).onError(new OnErrorListener() {
                    @Override
                    public void onError(Throwable t) {
                        Log.d(TAG,"onError"+t.getMessage());
                        Toast.makeText(getContext(), ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).onPageError(new OnPageErrorListener() {
                    @Override
                    public void onPageError(int page, Throwable t) {
                        Log.d(TAG,"onPageError"+t.getMessage());
                        Toast.makeText(getContext(), "Error On Page", Toast.LENGTH_SHORT).show();

                    }
                }).load();
                progressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"onFailure"+e.getMessage());
                progressBar.setVisibility(View.GONE);

            }
        });

    }



    private void setupOnBackPressed() {
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isEnabled()){
                    setEnabled(false);
                    requireActivity().onBackPressed();
                }
            }
        });

    }

}