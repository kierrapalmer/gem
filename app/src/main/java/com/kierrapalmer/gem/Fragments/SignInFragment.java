package com.kierrapalmer.gem.Fragments;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kierrapalmer.gem.R;

import java.util.Arrays;
import java.util.List;

import static java.lang.System.err;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment{
    private View rootView;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private OnSignInListener mCallback;
    private DatabaseReference mDatabase;

    private static final int RC_SIGN_IN = 1;

    private static final String TAG = "authTag";

    private TextInputEditText edtEmail;
    private TextInputEditText edtPassword;

    private Button btnSignOut;
    private Button btnSignIn;
    private Button btnCreateAccount;
    private List<AuthUI.IdpConfig> providers;
    private CallbackManager callbackManager;

    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView= inflater.inflate(R.layout.fragment_sign_in, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        btnSignIn = rootView.findViewById(R.id.btnSignIn);
        edtEmail = rootView.findViewById(R.id.edtEmail);
        edtPassword = rootView.findViewById(R.id.edtPassword);

        providers = Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());


        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
            }
        };

        btnSignIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                user = mAuth.getCurrentUser();
                if (user!=null){
                    Log.d(TAG,"User is already logged in");
                    loggedInAlert();
                }
                else{
                    signIn();
                }
            }
        });


        return rootView;
    }

    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        user = mAuth.getCurrentUser();
        if (user!=null){
            Log.d(TAG,user.getEmail()+" User is logged in");
            //mCallback.switchFragments("signIn", "createListing");
        }
        else{
            Log.d(TAG,"User is not logged in");
        }
    }
    public void onStop(){
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void signOut(){
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        if(user == null)
            Log.d(TAG, "User is not logged in");

    }

    public void signIn(){
        mAuth.signInWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString())
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        String s = edtEmail.getText().toString();
                                String s2 = edtPassword.getText().toString();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, user.getEmail()+" user signed in");
                            mCallback.viewListingsList();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void loggedInAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Add the buttons
                builder.setPositiveButton("Sign Out", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked signout button
                        signOut();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog, just dismiss dialog
                    }
                });
        AlertDialog dialog = builder.create();
    }





    /*--------------------------------
Interface
 -------------------------------*/
    public interface OnSignInListener    {
        public void createAccount();
        public void createListing();
        public void viewListingsList();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnSignInListener) activity;
        } catch (ClassCastException e)        {
            throw new ClassCastException(activity.toString() +
                    "must implement OnSignInListener.");
        }
    }

}



