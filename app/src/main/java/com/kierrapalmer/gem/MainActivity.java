package com.kierrapalmer.gem;

import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.kierrapalmer.gem.Fragments.CreateAccountFragment;
import com.kierrapalmer.gem.Fragments.CreateListingFragment;
import com.kierrapalmer.gem.Fragments.EditAccountFragment;
import com.kierrapalmer.gem.Fragments.SignInFragment;
import com.kierrapalmer.gem.Fragments.ViewListingsFragment;

public class MainActivity extends AppCompatActivity implements
        SignInFragment.OnSignInListener, CreateAccountFragment.OnCreateAccountListener,
        CreateListingFragment.OnCreateListingListener,
        ViewListingsFragment.OnViewListings{
    private FirebaseAuth mAuth;
    private static final String TAG = "main";
    private FragmentManager fragmentManager;

    private ViewListingsFragment viewListingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment, new SignInFragment(), "fragSignin")
                .commit();
       /* List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());*/

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


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
            case R.id.search:               //search postings
                //TODO: Create search feature
                return true;
            case R.id.create_post:
                createListing();
                return true;
            case R.id.account:               //Account page
                //TODO: Create account details page
                return true;
            case R.id.signout:               //Sign out account
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void signOut(){
        SignInFragment signInFragment = (SignInFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        signInFragment.signOut();
    }


    public void createAccount(FirebaseAuth mAuth) {
        CreateAccountFragment createAccountFragment = new CreateAccountFragment();

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.fragment, createAccountFragment, "fragCreateAccount").addToBackStack(null).commit();

    }


    public void editAccount(){
        fragmentManager = getSupportFragmentManager();
        EditAccountFragment editAccountFragment = new EditAccountFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.fragment, editAccountFragment, "fragEditAccount").addToBackStack(null).commit();
    }

    public void createListing(){
        fragmentManager = getSupportFragmentManager();
        CreateListingFragment createListingFragment = new CreateListingFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.fragment, createListingFragment, "fragCreateListing").addToBackStack(null).commit();
    }

 /*   public void displayCategoryList(){
        fragmentManager = getSupportFragmentManager();
        CategoryListFragment categoryListFragment = new CategoryListFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, categoryListFragment, "fragDisplayCategoryList").addToBackStack(null).commit();
    }


    public void displayCategoryListItems(@Nullable String category){
        viewListingsList();
    }
*/


    public void viewListingsList() {
        viewListingsFragment = new ViewListingsFragment();
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.fragment, viewListingsFragment, "fragViewListing").addToBackStack(null).commit();
    }

   /* public String getCategory(){
        return category;
    }*/



}

