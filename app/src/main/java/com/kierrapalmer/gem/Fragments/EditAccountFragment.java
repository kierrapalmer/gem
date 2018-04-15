package com.kierrapalmer.gem.Fragments;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kierrapalmer.gem.Models.User;
import com.kierrapalmer.gem.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditAccountFragment extends DialogFragment {
    private View rootview;
    private EditText edtEmail, edtFirst, edtPhone;
    private Button btnSave;

    private User dUser;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser mUser;
    private String userId;

    private static final String TAG = "authTag";

    public EditAccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_edit_account, container, false);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUser = mAuth.getCurrentUser();
        userId = mUser.getUid();

        edtFirst = rootview.findViewById(R.id.editAccountFirst);
        edtPhone = rootview.findViewById(R.id.editAccountPhone);
        edtEmail = rootview.findViewById(R.id.editAccountEmail);

        btnSave = rootview.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                editAccountSave();
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

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });



        return rootview;
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
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }






}
