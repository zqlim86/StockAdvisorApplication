package com.example.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.ShareRecord;
import com.example.myapplication.Model.TotalEquity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The PortfolioAdapter class is responsible for setting up the RecyclerView for displaying the portfolio of the user.
 * It extends the RecyclerView.Adapter and has a ViewHolder class that holds the views for each portfolio item.
 */
public class PortfolioAdapter extends RecyclerView.Adapter<PortfolioAdapter.ViewHolder> {

    Context context;
    private List<TotalEquity> totalEquities;
    private double mTotalEquityValue;

    /**
     Constructor for the PortfolioAdapter class that initializes the context, the list of total equities, and calculates the total equity value.
     @param context the context of the adapter
     @param totalEquities the list of total equities to display in the RecyclerView
     */
    public PortfolioAdapter(Context context, List<TotalEquity> totalEquities) {
        this.context = context;
        this.totalEquities = totalEquities;
        mTotalEquityValue = 0;

        for (TotalEquity totalEquity : totalEquities) {
            mTotalEquityValue += totalEquity.getEquity();
        }
    }

    /**
     Creates a new ViewHolder for the portfolio item by inflating the item_portfolio layout.
     @param parent the parent ViewGroup that the ViewHolder will be attached to
     @param viewType the type of view
     @return the ViewHolder object for the portfolio item
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_portfolio, parent, false);
        return new ViewHolder(view);
    }

    /**
     Binds the data for the portfolio item to the ViewHolder.
     @param holder the ViewHolder object for the portfolio item
     @param position the position of the portfolio item in the list
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TotalEquity totalEquity = totalEquities.get(position);

        double currentTickerValue = totalEquity.getEquity();
        double percentage = currentTickerValue / mTotalEquityValue * 100;

        holder.textViewHigh.setText(totalEquity.getTicker());
        holder.dailyHigh.setText(String.format(Locale.getDefault(), "$%.2f (%.0f%%)", currentTickerValue, percentage));
        holder.progressBar.setProgress((int)percentage);
    }

    /**
     Returns the number of items in the list of total equities.
     @return the number of items in the list of total equities
     */
    @Override
    public int getItemCount() {
        return totalEquities.size();
    }

    /**
     The ViewHolder class for the portfolio item that holds the views for the ticker, equity value, and progress bar.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewHigh;
        TextView dailyHigh;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewHigh = itemView.findViewById(R.id.textViewHigh);
            dailyHigh = itemView.findViewById(R.id.dailyHigh);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
