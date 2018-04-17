package com.kierrapalmer.gem;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kierrapalmer.gem.Fragments.AccountFragment;
import com.kierrapalmer.gem.Fragments.CreateAccountFragment;
import com.kierrapalmer.gem.Fragments.CreateListingFragment;
import com.kierrapalmer.gem.Fragments.EditAccountFragment;
import com.kierrapalmer.gem.Fragments.SignInFragment;
import com.kierrapalmer.gem.Fragments.ViewListingsFragment;
import com.kierrapalmer.gem.Models.User;

public class MainActivity extends AppCompatActivity implements
        SignInFragment.OnSignInListener, CreateAccountFragment.OnCreateAccountListener,
        AccountFragment.OnAccountFragment, CreateListingFragment.OnCreateListingListener,
        ViewListingsFragment.OnViewListings, EditAccountFragment.OnEditAccountListener{
    private FirebaseAuth mAuth;
    private static final String TAG = "main";
    private FragmentManager fragmentManager;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ViewListingsFragment viewListingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
            }
        };

    }
    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        user = mAuth.getCurrentUser();
        //Direct to Account page if no user logged in
        if (user!=null){
            Log.d(TAG,user.getEmail()+" User is logged in");
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment, new ViewListingsFragment(), "fragViewListings")
                    .addToBackStack(null)
                    .commit();
        }
        //Direct to Listings page if user logged in
        else{
            Log.d(TAG,"No user is logged in");
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment, new AccountFragment(), "fragAccount")
                    .addToBackStack(null)
                    .commit();
        }
    }
    public void onStop(){
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
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
                createListing();
                return true;
            case R.id.account:               //Account page
                editAccount();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void setToolbarText(String text){
        TextView tvToolbar = findViewById(R.id.tvToolbar);
        tvToolbar.setText(text);
    }



    /*------------------------
    Interface methods/switch fragments
     -----------------------*/

    public void signout(){
        SignInFragment signInFragment = new SignInFragment();
        signInFragment.signOut();
        Toast.makeText(this,"Signed out",
                Toast.LENGTH_SHORT).show();

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment, new AccountFragment(), "fragAccount")
                .addToBackStack(null)
                .commit();
    }


    public void signin() {
        SignInFragment signInFragment = new SignInFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment, signInFragment, "fragSignIn")
                .addToBackStack(null)
                .commit();
    }

    public void createAccount() {
        CreateAccountFragment createAccountFragment = new CreateAccountFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment, createAccountFragment, "fragCreateAccount")
                .addToBackStack(null)
                .commit();
    }

    public void editAccount(){
        fragmentManager = getSupportFragmentManager();
        EditAccountFragment editAccountFragment = new EditAccountFragment();
        fragmentManager.beginTransaction()
            .replace(R.id.fragment, editAccountFragment, "fragEditAccount")
            .addToBackStack(null)
            .commit();
    }

    public void createListing(){
        fragmentManager = getSupportFragmentManager();
        CreateListingFragment createListingFragment = new CreateListingFragment();
        fragmentManager.beginTransaction()
           .replace(R.id.fragment, createListingFragment, "fragCreateListing")
           .addToBackStack(null)
           .commit();
    }

    public void createListing(String url){
        fragmentManager = getSupportFragmentManager();
        CreateListingFragment createListingFragment = new CreateListingFragment();
        createListingFragment.setURL(url);
        fragmentManager.beginTransaction()
                .replace(R.id.fragment, createListingFragment, "fragCreateListing")
                .addToBackStack(null)
                .commit();
    }


    public void viewListingsList() {
        viewListingsFragment = new ViewListingsFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
            .replace(R.id.fragment, viewListingsFragment, "fragViewListings")
            .addToBackStack(null)
            .commit();
    }

    public void refreshListings(){
        viewListingsFragment = new ViewListingsFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment, viewListingsFragment, "fragViewListings")
                .addToBackStack(null)
                .commit();
    }





}

