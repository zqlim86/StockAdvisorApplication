package com.example.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.StockProfile;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class StockProfileAdapter extends RecyclerView.Adapter<StockProfileAdapter.ViewHolder> {
    private List<StockProfile> stockProfiles;
    private Context context;

    // Constructor to initialize the adapter with context and list of StockProfile items
    public StockProfileAdapter(Context context, List<StockProfile> stockProfiles) {
        this.context = context;
        this.stockProfiles = stockProfiles;
    }

    // Method to update the data in the adapter and notify the RecyclerView about the change
    public void setData(List<StockProfile> newData) {
        this.stockProfiles = newData;
        notifyDataSetChanged();
    }

    // onCreateViewHolder() method to inflate the layout for each item in the RecyclerView
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_stockprofile, parent, false);
        return new ViewHolder(view);
    }

    // onBindViewHolder() method to bind the data to the views in the layout for each item in the RecyclerView
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StockProfile stockProfile = stockProfiles.get(position);

        String imageUrl = stockProfile.getImage();
        String profileNameText = stockProfile.getCompanyName();
        String profileTickerText = stockProfile.getSymbol();
        String profilePriceText = String.format(Locale.US, "$%.2f", stockProfile.getPrice());
        String profileChangesText = String.format(Locale.US, "$%.2f", stockProfile.getChanges());

        //Set Conditions for Changes' Text & Symbol.
        if(stockProfile.getChanges() < 0){
            // if the change is negative, set the text color to red and show a down arrow
            holder.profileChanges.setTextColor(context.getResources().getColor(R.color.sellButtonColor));
            holder.profileSymbolChanges.setImageResource(R.drawable.ic_arrowdown);
        }else{
            // if the change is positive, set the text color to green and show an up arrow
            holder.profileChanges.setTextColor(context.getResources().getColor(R.color.green));
            holder.profileSymbolChanges.setImageResource(R.drawable.ic_arrowup);
        }

        // use Picasso library to load the image from the url into the ImageView
        Picasso.get().load(imageUrl).into(holder.profileImage);

        // set the company name, symbol, price and changes to the corresponding TextViews
        holder.profileTicker.setText(profileTickerText);
        holder.profileName.setText(profileNameText);
        holder.profilePrice.setText(profilePriceText);
        holder.profileChanges.setText(profileChangesText);
    }

    // getItemCount() method to get the size of the list of StockProfile items
    @Override
    public int getItemCount() {
        return stockProfiles.size();
    }

    // ViewHolder class to hold the views for each item in the RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView profileImage;
        public ImageView profileSymbolChanges;
        public TextView profileChanges;
        public TextView profileTicker;
        public TextView profileName;
        public TextView profilePrice;

        // constructor to initialize the views
        public ViewHolder(View view) {
            super(view);
            profileImage = view.findViewById(R.id.profileImage);
            profileSymbolChanges = view.findViewById(R.id.profileSymbolChanges);
            profileChanges = view.findViewById(R.id.profileChanges);
            profileTicker = view.findViewById(R.id.profileTicker);
            profileName = view.findViewById(R.id.profileName);
            profilePrice = view.findViewById(R.id.profilePrice);
        }
    }
}

