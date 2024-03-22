package com.example.ucpbooks;

import static com.example.ucpbooks.Constants.MAX_BYTES_PDF;

import android.app.Application;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

public class MyApplication extends Application {
    private static final String TAG_DOWNLOAD="Download_TAG";

    public void OnCreate(){
        super.onCreate();
    }
    public static void loadPdfSize(String pdfUrl, String pdfTitle, TextView size) {
        String TAG="PDF_SIZE_TAG";

        StorageReference storageReference= FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        storageReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                double bytes=storageMetadata.getSizeBytes();
                Log.d(TAG,"onSuccess:"+pdfTitle+""+bytes);
                double kb=bytes/1024;
                double mb=kb/1024;
                if (mb>=1){
                    size.setText(String.format("%.2f",mb)+"MB");

                }
                else if (kb>=1){
                    size.setText(String.format("%.2f",kb)+"KB");
                }else {
                    size.setText(String.format("%.2f",bytes)+"bytes");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"On Fail:"+e.getMessage());
            }
        });

    }
    public static void loadPdfFromUrlSinglePage(String pdfUrl, String pdfTitle, PDFView pdfView, ProgressBar progressBar) {
        String TAG="PDF_LOAD_SINGLE_TAG";

        StorageReference storageReference=FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        storageReference.getBytes(MAX_BYTES_PDF).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.d(TAG,"onSuccess:"+pdfTitle+"Sucessfuly Got The file");
                pdfView.fromBytes(bytes).pages(0).spacing(0).swipeHorizontal(false)
                        .enableSwipe(false).onError(new OnErrorListener() {
                            @Override
                            public void onError(Throwable t) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Log.d(TAG,"onError"+t.getMessage());
                            }
                        }).onPageError(new OnPageErrorListener() {
                            @Override
                            public void onPageError(int page, Throwable t) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Log.d(TAG,"On Page Error"+t.getMessage());
                            }
                        }).onLoad(new OnLoadCompleteListener() {
                            @Override
                            public void loadComplete(int nbPages) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Log.d(TAG,"Load Complete:Pdf loaded");
                            }
                        }).load();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"onFailure:Failed To Get url due to"+e.getMessage());

            }
        });
    }
    public static void loadCategory(String categoryId,TextView categoryTv) {

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Categories");
        databaseReference.child(categoryId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String category=""+snapshot.child("category").getValue();
                categoryTv.setText(category);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}
