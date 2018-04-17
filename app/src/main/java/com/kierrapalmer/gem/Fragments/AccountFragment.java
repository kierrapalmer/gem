package com.kierrapalmer.gem.Fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.kierrapalmer.gem.R;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {
    private View rootview;
    private OnAccountFragment mCallback;

    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_account, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();


        Button btnSignIn = rootview.findViewById(R.id.btnSignIn2);
        Button btnCreateAccount = rootview.findViewById(R.id.btnCreateAccount2);
        btnSignIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mCallback.signin();
            }
        });
        btnCreateAccount.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mCallback.createAccount();
            }
        });
        return rootview;
    }

    public interface OnAccountFragment    {
        public void createAccount();
        public void signin();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnAccountFragment) activity;
        } catch (ClassCastException e)        {
            throw new ClassCastException(activity.toString() +
                    "must implement OnAccountFragment.");
        }
    }

}
