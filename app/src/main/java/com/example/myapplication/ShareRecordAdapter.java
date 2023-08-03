package com.example.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.ShareRecord;

import java.util.List;

/**
 The ShareRecordAdapter class is responsible for adapting the share records to be displayed in a RecyclerView.
 */
public class ShareRecordAdapter extends RecyclerView.Adapter<ShareRecordAdapter.ViewHolder> {
    private List<ShareRecord> shareRecords;
    Context context;

    /**
     Constructor for the ShareRecordAdapter class.
     @param context the context of the activity or fragment that is using the adapter
     @param shareRecords the list of ShareRecord objects to be displayed in the RecyclerView
     */
    public ShareRecordAdapter(Context context, List<ShareRecord> shareRecords) {
        this.context = context;
        this.shareRecords = shareRecords;
    }

    /**
     Sets the data to be displayed in the RecyclerView.
     @param newData the new list of ShareRecord objects to be displayed
     */
    public void setData(List<ShareRecord> newData) {
        this.shareRecords = newData;
        notifyDataSetChanged();
    }

    /**
     Creates a new ViewHolder for the RecyclerView.
     @param parent the parent ViewGroup of the ViewHolder
     @param viewType the type of the view to be displayed
     @return the new ViewHolder for the RecyclerView
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sharerecords, parent, false);
        return new ViewHolder(view);
    }

    /**
     Binds the data to the views in the ViewHolder.
     @param holder the ViewHolder to bind the data to
     @param position the position of the data in the list
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShareRecord shareRecord = shareRecords.get(position);

        holder.textViewDate.setText(shareRecord.getDate());
        holder.textViewTicker.setText(shareRecord.getTicker());
        holder.textViewQuantity.setText(String.valueOf(shareRecord.getQuantity()));
        holder.textViewPrice.setText(String.valueOf(shareRecord.getPrice()));
        holder.textViewTotalValue.setText(String.valueOf(shareRecord.getTotal()));
    }

    /**
     Gets the number of items in the list.
     @return the number of items in the list
     */
    @Override
    public int getItemCount() {
        return shareRecords.size();
    }

    /**
     The ViewHolder class for the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewDate;
        public TextView textViewTicker;
        public TextView textViewPrice;
        public TextView textViewQuantity;
        public TextView textViewTotalValue;

        public ViewHolder(View view) {
            super(view);
            textViewDate = view.findViewById(R.id.dateLogbook);
            textViewTicker = view.findViewById(R.id.tickerLogbook);
            textViewQuantity = view.findViewById(R.id.quantityLogbook);
            textViewPrice = view.findViewById(R.id.priceLogbook);
            textViewTotalValue = view.findViewById(R.id.totalLogbook);
        }
    }
}
