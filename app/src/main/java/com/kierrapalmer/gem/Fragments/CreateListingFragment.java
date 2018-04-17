package com.kierrapalmer.gem.Fragments;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kierrapalmer.gem.Models.Listing;
import com.kierrapalmer.gem.Models.User;
import com.kierrapalmer.gem.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateListingFragment extends DialogFragment {
    private View rootview;
    private String title, price, description, zipcode, phone, email;
    private String category;
    private Button btnUpload;
    private CheckBox chkbox;
    private ImageView imgview;
    private TextView txtEdit;
    private Spinner spinner;
    private User user;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    private static final String TAG = "listing";
    private String key;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private StorageReference ref;
    private OnCreateListingListener mCallback;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private DatabaseReference mDatabase;
    private String url;

    private static final int MAX_WIDTH = 200;
    private static final int MAX_HEIGHT = 200;

    public CreateListingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview =  inflater.inflate(R.layout.fragment_create_listing, container, false);
        setHasOptionsMenu(true);

        //Firebase info
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();
            }
        };

        //Set category selector
        spinner = (Spinner) rootview.findViewById(R.id.categorySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //Initial variables
        btnUpload = rootview.findViewById(R.id.btnUpload);
        imgview = rootview.findViewById(R.id.imgView);
        txtEdit = rootview.findViewById(R.id.txtEdit);
        txtEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mCallback.editAccount();
            }
        });
        chkbox = rootview.findViewById(R.id.chkbox);
        chkbox.setChecked(true);
        chkbox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mCallback.editAccount();
            }
        });

        setPicture(null);                                       //sets default picture
        key = mDatabase.child("listings").push().getKey();      //unique key for new listing
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        return rootview;
    }

    private void setPicture(@Nullable String picURL){
        if(picURL==null) {
            Picasso.with(getContext())
                    .load("https://firebasestorage.googleapis.com/v0/b/gemfirebaseproject.appspot.com/o/images%2Fdefault.jpg?alt=media&token=47b12154-2762-418d-8027-31169bcba04e")
                    .fit()
                    .centerCrop()
                    .into(imgview);
        }
        else{
            Picasso.with(getContext())
                    .load(picURL)
                    .fit()
                    .centerCrop()
                    .into(imgview);
        }
    }


    /*---------Create new listing-------------------*/
    private void submitListing() {
        final String userId = mUser.getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        user = dataSnapshot.getValue(User.class);
                        if (mUser == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(getContext(),"Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post
                            writeNewListing();
                            Toast.makeText(getActivity(), "Item posted", Toast.LENGTH_SHORT).show();
                            mCallback.refreshListings();
                            dismiss();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }
    private void writeNewListing() {
       String sRef;

        TextInputEditText edtTitle = rootview.findViewById(R.id.edtTitle);
        title = edtTitle.getText().toString();
        TextInputEditText edtPrice = rootview.findViewById(R.id.edtPrice);
        price = edtPrice.getText().toString();
        TextInputEditText edtDesc = rootview.findViewById(R.id.edtDesc);
        description = edtDesc.getText().toString();
        TextInputEditText edtZip = rootview.findViewById(R.id.edtZip);
        zipcode = edtZip.getText().toString();

        category = spinner.getSelectedItem().toString();

        phone = mUser.getPhoneNumber();
        email = mUser.getEmail();

        if(!chkbox.isChecked()){
            mCallback.editAccount();
            chkbox.setChecked(true);
        }
        if(title == null || price == null || description == null || zipcode == null){
            Toast.makeText(getContext(),"All fields are required",
                    Toast.LENGTH_SHORT).show();
        }
        if(key == null){
            Log.d(TAG, "no listing key");
        }

        String mUserId = mUser.getUid();
        Listing list = new Listing(key, mUserId, url, title, price, category, description, zipcode, phone, email);

        mDatabase.child("listings").child(key).setValue(list);
    }

    /*---------END Create new listing-------------------*/


    /*----
    Image upload code written with help from
    https://code.tutsplus.com/tutorials/image-upload-to-firebase-in-android-application--cms-29934
     -----*/
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                imgview.setImageBitmap(bitmap);
                uploadImage();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }




    //---Upload Image to Firebase Storage-----//
    private void uploadImage() {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            try {
                ref = storageReference.child("images/" + key + "/" + UUID.randomUUID().toString());

                Bitmap bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos); //Decreases image size
                byte[] data = baos.toByteArray();
                //uploading the image
                UploadTask uploadTask2 = ref.putBytes(data);
                uploadTask2.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        url = taskSnapshot.getDownloadUrl().toString();
                        progressDialog.dismiss();
                        mCallback.createListing(url);
                        Toast.makeText(getActivity(), "Upload successful", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Upload Failed -> " + e, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        mUser = mAuth.getCurrentUser();
        if (mUser==null){
            Log.d(TAG, "No User logged in");
        }
        if(url != null){
            setPicture(url);
        }
       mCallback.setToolbarText("SELL");
    }
    public void onStop(){
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    public void setURL(String url){
        this.url = url;
    }





    /*--------------------------------
        Full Screen Dialog
      -------------------------------*/
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.menu_account, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.menu_save:
                submitListing();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




    /*--------------------------------
    Interface
     -------------------------------*/

    public interface OnCreateListingListener    {
        public void editAccount();
        public void setToolbarText(String text);
        public void createListing(String url);
        public void refreshListings();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnCreateListingListener) activity;
        } catch (ClassCastException e)        {
            throw new ClassCastException(activity.toString() +
                    "must implement OnCreatListing.");
        }
    }
}
