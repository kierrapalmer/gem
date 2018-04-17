package com.kierrapalmer.gem.Fragments;


import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
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
import com.kierrapalmer.gem.CircleTransform;
import com.kierrapalmer.gem.Models.User;
import com.kierrapalmer.gem.R;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditAccountFragment extends DialogFragment {
    private View rootview;
    private EditText edtEmail, edtFirst, edtPhone, edtPassword;
    private TextView tvLogout;

    private User dUser;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser mUser;
    private String userId;
    private OnEditAccountListener mCallback;

    private static final String TAG = "authTag";

    public EditAccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_edit_account, container, false);
        setHasOptionsMenu(true);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUser = mAuth.getCurrentUser();
        userId = mUser.getUid();

        edtFirst = rootview.findViewById(R.id.editAccountFirst);
        edtPhone = rootview.findViewById(R.id.editAccountPhone);
        edtEmail = rootview.findViewById(R.id.editAccountEmail);
        edtPassword = rootview.findViewById(R.id.edtAccountPassword);

        tvLogout = rootview.findViewById(R.id.tvLogout);
        tvLogout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mCallback.signout();
            }
        });

        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        dUser = dataSnapshot.getValue(User.class);
                        edtFirst.setText(dUser.getFirst());
                        edtPhone.setText(dUser.getPhone());
                        edtEmail.setText(dUser.getEmail());
                        edtPassword.setText(dUser.getPassword());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });

        ImageView imgView = rootview.findViewById(R.id.account_image);
        Picasso.with(getContext())
                .load("https://firebasestorage.googleapis.com/v0/b/gemfirebaseproject.appspot.com/o/images%2Fdefault-user-image.png?alt=media&token=f6a3a768-58d6-4941-a721-f891aba771c5")
               .transform(new CircleTransform())
                .fit().centerCrop()
                .into(imgView);


        return rootview;
    }

    @Override
    public void onStart() {
        super.onStart();
        mCallback.setToolbarText("PROFILE");
    }

    private void editAccountSave(){
        User user = new User(edtEmail.getText().toString(), dUser.getPassword(), edtFirst.getText().toString(), edtPhone.getText().toString());
        mDatabase.child("users").child(userId).setValue(user);

        Toast.makeText(getContext(),"Info updated",
                Toast.LENGTH_SHORT).show();
    }

    //Create full screen dialog
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
        switch (item.getItemId()){
            case R.id.menu_save:
                editAccountSave();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }


    /*--------------------------------
    Interface
     -------------------------------*/
    public interface OnEditAccountListener    {
        public void signout();
        public void setToolbarText(String text);
        }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnEditAccountListener) activity;
        } catch (ClassCastException e)        {
            throw new ClassCastException(activity.toString() +
                    "must implement OnEditAccountListener.");
        }
    }



}
