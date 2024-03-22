package com.example.ucpbooks;

import static androidx.core.content.ContextCompat.getSystemService;
import static com.example.ucpbooks.Constants.MAX_BYTES_PDF;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;


public class PdfDetailfragment extends Fragment {

    String bookId,bookTitle,bookUrl;

    private static final String TAG_DOWNLOAD="Download_TAG";
    Button read,download;
    TextView categorytitle,sizes,descriptions,bookt;
    PDFView pdfView;
    ProgressBar progressBar;
    ImageButton imageButton;

    public PdfDetailfragment() {

    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_pdf_detailfragment, container, false);
        read=view.findViewById(R.id.readbook);
        imageButton=view.findViewById(R.id.back);
        bookt=view.findViewById(R.id.title);
        categorytitle=view.findViewById(R.id.categorytitle);
        pdfView=view.findViewById(R.id.pdfview);
        sizes=view.findViewById(R.id.sizes);
        descriptions=view.findViewById(R.id.descriptions);
        progressBar=view.findViewById(R.id.progressbar);
        Bundle bundle=getArguments();
        bookId=bundle.getString("bookId");
        loadBookDetails();
        download=view.findViewById(R.id.bookdownload);
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
        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new PdfViewFragment();
                Bundle bundle=new Bundle();
                bundle.putString("bookId",bookId);
                fragment.setArguments(bundle);
                ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.framelayout,fragment, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG_DOWNLOAD,"Checking Permisssion");
                if(ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG_DOWNLOAD,"Permission Granted You May Download Now");
                    downloadBook(getContext(),""+bookId,""+bookTitle,""+bookUrl);
                }else {
                    Log.d(TAG_DOWNLOAD,"Permission Not Granted,request Permission");
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }

            }

        });
        return view;




    }
    private ActivityResultLauncher<String> requestPermissionLauncher=
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),isGranted->{
                if (isGranted){
                    Log.d(TAG_DOWNLOAD,"Permission Granted :");
                    downloadBook(getContext(),""+bookId,""+bookTitle,""+bookUrl);

                }else {
                    Log.d(TAG_DOWNLOAD,"Permission Denied");
                    Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();

                }

            });
    public void downloadBook(Context context, String bookId, String bookTitle, String bookUrl){
        Log.d(TAG_DOWNLOAD,"downloadbook:downloading book....");
        String nameWithExtension=bookTitle+".pdf";
        Log.d(TAG_DOWNLOAD,"downloadbook:NAME"+nameWithExtension);
        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(bookUrl));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI|DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("Download");
        Toast.makeText(context, "Download Started Check The Notification Bar", Toast.LENGTH_SHORT).show();
        request.setDescription("Downloading "+bookTitle);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,""+System.currentTimeMillis());
        DownloadManager manager=(DownloadManager)getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
        StorageReference storageReference= FirebaseStorage.getInstance().getReferenceFromUrl(bookUrl);
        storageReference.getBytes(MAX_BYTES_PDF).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.d(TAG_DOWNLOAD,"OnSuccess: Book Downloaded");
                saveDownloadBook(context,request,bytes,nameWithExtension,bookId);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {


            }
        });

    }
    private static void saveDownloadBook(Context context, DownloadManager.Request request, byte[] bytes, String nameWithExtension, String bookId) {
        Log.d(TAG_DOWNLOAD,"saveDownloadBook: Saving Download Book");
        try{
            File downloadsFolder=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            downloadsFolder.mkdirs();
            String filepath=downloadsFolder.getPath()+"/"+nameWithExtension;
            FileOutputStream outputStream= new FileOutputStream(filepath);
            outputStream.write(bytes);
            outputStream.close();
            Toast.makeText(context, "Saved To Download", Toast.LENGTH_SHORT).show();
            Log.d(TAG_DOWNLOAD,"saveDownloadBook:Saved to Download Folder");
            request.notify();
        }
        catch (Exception e){
            Log.d(TAG_DOWNLOAD,"SaveDownloadBook:Failed To Download Folder "+e.getMessage());

        }

    }
    private void loadBookDetails() {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Books");
        databaseReference.child(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookTitle=""+snapshot.child("title").getValue();
                String description=""+snapshot.child("description").getValue();
                String categoryId=""+snapshot.child("categoryId").getValue();
                bookUrl=""+snapshot.child("url").getValue();
                String timestamp=""+snapshot.child("timestamp").getValue();

                MyApplication.loadCategory(""+categoryId,
                        categorytitle);
                MyApplication.loadPdfFromUrlSinglePage(""+bookUrl,
                        ""+bookTitle,
                        pdfView,
                        progressBar);
                MyApplication.loadPdfSize(""+bookUrl,
                        ""+bookTitle,
                        sizes);
                bookt.setText(bookTitle);
                descriptions.setText(description);
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