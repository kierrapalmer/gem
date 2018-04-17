package com.kierrapalmer.gem;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kierrapalmer.gem.Models.Listing;
import com.kierrapalmer.gem.Models.User;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.text.NumberFormat;

public class ListingDetailActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;

    private TextView tvTitle, tvPrice, tvZip, tvName, tvDesc;
    private Button btnCall, btnEmail;
    private ImageView imgView;
    private DatabaseReference mListing;
    private FirebaseUser mUser;
    private User user;
    private Listing listing;
    private DatabaseReference mLisitingReference;
    private ValueEventListener mListingListener;
    private String mPostKey;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private static final String TAG = "Listing";
    private static final int MAX_WIDTH = 200;
    private static final int MAX_HEIGHT = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        tvTitle = findViewById(R.id.detail_title);
        tvPrice = findViewById(R.id.detail_price);
        tvZip = findViewById(R.id.detail_zip);
        tvName = findViewById(R.id.detail_name);
        tvDesc = findViewById(R.id.detail_desc);

        btnCall = findViewById(R.id.detail_call);
        btnEmail = findViewById(R.id.detail_email);

        mPostKey = getIntent().getStringExtra("postKey");
        if (mPostKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();
            }
        };

        mListing = FirebaseDatabase.getInstance().getReference()
                .child("listings").child(mPostKey);


    }

    /*------------Toolbar ------------*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.create_post:
                setResult(2,getIntent());
                finish();//finishing activity
                return true;
            case R.id.account:
                setResult(3,getIntent());
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
        mUser = mAuth.getCurrentUser();
        if (mUser==null){
            Log.d(TAG, "No User logged in");
        }
        displayListingDetails();


    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove post value event listener
        if (mListingListener != null) {
            mListing.removeEventListener(mListingListener);
        }

    }



    public void displayListingDetails(){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get List object and use the values to update the UI
                listing = dataSnapshot.getValue(Listing.class);
                tvTitle.setText(listing.getTitle());

                String stringPrice = listing.getPrice();
                if(stringPrice!=null) {
                    stringPrice = stringPrice.replace(",", "");
                    stringPrice = stringPrice.replace("$", "");
                    int price = Integer.parseInt(stringPrice);
                    NumberFormat format = NumberFormat.getCurrencyInstance();
                    format.setMaximumFractionDigits(0);
                    tvPrice.setText(format.format(price));
                }
                tvZip.setText(listing.getZip());
                tvDesc.setText(listing.getDesc());
                getListingUser();
                displayPicture();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(ListingDetailActivity.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mListing.addValueEventListener(postListener);

        //Copy to delete later
        mListingListener = postListener;

    }

    public void getListingUser(){
        final String userId = listing.getUserId();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        user = dataSnapshot.getValue(User.class);
                        if (mUser == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " connected to listing is null");
                            Toast.makeText(ListingDetailActivity.this,"Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Set Name fields
                            tvName.setText(user.getFirst());
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    public void displayPicture(){

        imgView = findViewById(R.id.detail_image);

        if(listing.getPhotoURL()==null) {
            Picasso.with(this)
                    .load("https://firebasestorage.googleapis.com/v0/b/gemfirebaseproject.appspot.com/o/images%2Fdefault.jpg?alt=media&token=47b12154-2762-418d-8027-31169bcba04e")
                    .fit().centerCrop()
                    .into(imgView);
        }
        else{
            Picasso.with(this)
                    .load(listing.getPhotoURL())
                    .fit().centerCrop()
                    .into(imgView);
        }


    }




}
