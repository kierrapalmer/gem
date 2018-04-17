package com.kierrapalmer.gem.Fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.kierrapalmer.gem.ListingDetailActivity;
import com.kierrapalmer.gem.ViewHolders.ListingsViewHolder;
import com.kierrapalmer.gem.Models.Listing;
import com.kierrapalmer.gem.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewListingsFragment extends Fragment {
    private View rootview;
    private static final String TAG = "PostListFragment";
    private ImageView imgView;
    private String category;
    private DatabaseReference mDatabase;

    private FirebaseRecyclerAdapter<Listing, ListingsViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private OnViewListings mCallback;

    public ViewListingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview =  inflater.inflate(R.layout.fragment_all_listings, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();


        mDatabase = FirebaseDatabase.getInstance().getReference();
        imgView = rootview.findViewById(R.id.listing_image);

        mRecycler = rootview.findViewById(R.id.posts_list);
        return rootview;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new GridLayoutManager(getContext(),2);
        mRecycler.setLayoutManager(mManager);


        Query listingsQuery = getQuery();
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Listing>()
                .setQuery(listingsQuery, Listing.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<Listing, ListingsViewHolder>(options) {

            @Override
            public ListingsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new ListingsViewHolder(inflater.inflate(R.layout.item_listing, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(ListingsViewHolder viewHolder, int position, final Listing model) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch ListingDetailActivity
                        Intent intent = new Intent(getContext(), ListingDetailActivity.class);
                        intent.putExtra("postKey", postKey);
                        //Start Listing Detail activity
                        startActivityForResult(intent, 2);
                    }
                });

                // Bind Post to ViewHolder
                viewHolder.bindToListing(model, getContext());
            }
        };
        mRecycler.setAdapter(mAdapter);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==2){
            mCallback.createListing();
        }
        else if(requestCode==3){
            mCallback.editAccount();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }

        mCallback.setToolbarText("BUY");
        mRecycler = rootview.findViewById(R.id.posts_list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }


    public Query getQuery(){
       return FirebaseDatabase.getInstance().getReference().child("listings").limitToLast(50);
    }






    /*--------------------------------
    Interface
     -------------------------------*/
    public interface OnViewListings    {
        public void createListing();
        public void setToolbarText(String text);
        public void editAccount();
        }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnViewListings) activity;
        } catch (ClassCastException e)        {
            throw new ClassCastException(activity.toString() +
                    "must implement OnViewListings.");
        }
    }
}
