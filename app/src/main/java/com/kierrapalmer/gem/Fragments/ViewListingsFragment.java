package com.kierrapalmer.gem.Fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.kierrapalmer.gem.ListingDetailActivity;
import com.kierrapalmer.gem.ListingsViewHolder;
import com.kierrapalmer.gem.Models.Listing;
import com.kierrapalmer.gem.R;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewListingsFragment extends Fragment {
    private View rootview;
    private static final String TAG = "PostListFragment";
    private ImageView imgView;
    private static final int MAX_WIDTH = 100;
    private static final int MAX_HEIGHT = 100;
    private String category;

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]

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
                        startActivity(intent);
                    }
                });



                // Bind Post to ViewHolder
                viewHolder.bindToListing(model, getContext());
            }
        };
        mRecycler.setAdapter(mAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
        mRecycler = rootview.findViewById(R.id.posts_list);
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





   /* public String setCategory(String category){
        return this.category = category;
    }
    public Query getQuery(String category){
        return FirebaseDatabase.getInstance().getReference().child("listings").limitToLast(50);
    }
*/

    public interface OnViewListings    {
       // public String getCategory();
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
