package com.kierrapalmer.gem.ViewHolders;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kierrapalmer.gem.Models.Listing;
import com.kierrapalmer.gem.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.math.BigDecimal;
import java.net.URI;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by theuh on 4/12/2018.
 */

public class ListingsViewHolder extends  RecyclerView.ViewHolder {
    public TextView titleView;
    public TextView priceView;
    public ImageView imgView;


    private static final int MAX_WIDTH = 100;
    private static final int MAX_HEIGHT = 100;

    public ListingsViewHolder(View itemView) {
        super(itemView);

        titleView = itemView.findViewById(R.id.listing_title);
        priceView = itemView.findViewById(R.id.listing_price);
        imgView = itemView.findViewById(R.id.listing_image);
    }

    //Fills in listing information
    public void bindToListing(Listing list, Context context) {
        titleView.setText(list.getTitle());

        String stringPrice = list.getPrice();
        if(stringPrice!=null) {
            stringPrice = stringPrice.replace(",", "");
            stringPrice = stringPrice.replace("$", "");
            int price = Integer.parseInt(stringPrice);
            NumberFormat format = NumberFormat.getCurrencyInstance();
            format.setMaximumFractionDigits(0);
            priceView.setText(format.format(price));
        }

        String url = list.getPhotoURL();


        if(url != null) {
            Picasso.with(context)
                    .load(list.getPhotoURL())
                    .fit().centerCrop()
                    .into(imgView);
        }
        else{
            Picasso.with(context)
                    .load("https://firebasestorage.googleapis.com/v0/b/gemfirebaseproject.appspot.com/o/images%2Fdefault.jpg?alt=media&token=47b12154-2762-418d-8027-31169bcba04e")
                    .fit().centerCrop()
                    .into(imgView);
        }

    }


}
