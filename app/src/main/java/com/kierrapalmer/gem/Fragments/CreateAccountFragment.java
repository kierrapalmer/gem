package com.kierrapalmer.gem.Fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kierrapalmer.gem.Models.User;
import com.kierrapalmer.gem.R;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateAccountFragment extends Fragment {
    private View rootview;

    private OnCreateAccountListener mCallback;
    private TextInputEditText edtEmail, edtPassword, edtFirst, edtPhone;
    private Button btnCreate;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private static final String TAG = "authTag";

    public CreateAccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview =  inflater.inflate(R.layout.fragment_create_account, container, false);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        edtEmail = rootview.findViewById(R.id.edtEmailCreate);
        edtPassword = rootview.findViewById(R.id.edtPasswordCreate);
        edtFirst = rootview.findViewById(R.id.edtFirst);
        edtPhone = rootview.findViewById(R.id.edtPhone);
        btnCreate = rootview.findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                createAccount(mAuth);
            }
        });

        InputFilter filter = new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.length() > 0) {

                    if (!Character.isDigit(source.charAt(0)))
                        return "";
                    else {
                        if (dstart == 3) {
                            return source + ") ";
                        } else if (dstart == 0) {
                            return "(" + source;
                        } else if ((dstart == 5) || (dstart == 9))
                            return "-" + source;
                        else if (dstart >= 14)
                            return "";
                    }

                } else {

                }

                return null;

            }
        };

        edtPhone.setFilters(new InputFilter[] { filter });


        return rootview;
    }


    public void createAccount(final FirebaseAuth mAuth){
        mAuth.createUserWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString())
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            if(edtPhone.getText().toString().length() < 10)
                                Toast.makeText(getActivity(), "Invalid Phone Number(xxx-xxx-xxxx)",
                                        Toast.LENGTH_SHORT).show();
                            else{
                                FirebaseUser fUser =  mAuth.getCurrentUser();
                                writeNewUser(fUser.getUid());
                                Toast.makeText(getActivity(), "Account Created",
                                        Toast.LENGTH_SHORT).show();
                                mCallback.viewListingsList();
                            }
                            Log.d(TAG, "User " + edtEmail.getText().toString() + " account created");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "createUserWithEmail:failure", task.getException());
                            if(edtPassword.getText().toString().length() < 6)
                                Toast.makeText(getActivity(), "Password too short.",
                                        Toast.LENGTH_SHORT).show();
                            else
                                 Toast.makeText(getActivity(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                        }

                    }
                });


    }

    private void writeNewUser(String userId) {
        User user = new User(edtEmail.getText().toString(), edtPassword.getText().toString(), edtFirst.getText().toString(), edtPhone.getText().toString());
        mDatabase.child("users").child(userId).setValue(user);
    }

    public interface OnCreateAccountListener   {
        public void viewListingsList();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnCreateAccountListener) activity;
        } catch (ClassCastException e)        {
            throw new ClassCastException(activity.toString() +
                    "must implement onCreateAccountListener.");
        }
    }


}
