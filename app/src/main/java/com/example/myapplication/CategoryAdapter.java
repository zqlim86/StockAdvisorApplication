package com.example.myapplication;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * A RecyclerView adapter that displays a list of categories and handles category button click events.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<String> categories; // List of categories to be displayed
    private OnCategoryClickListener listener; // Listener for category button click events

    /**
     Constructor for the CategoryAdapter
     @param categories List of categories to be displayed
     @param listener Listener for category button click events
     */
    public CategoryAdapter(List<String> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    /**
     Called when a new ViewHolder is created for a category item view
     @param parent The parent ViewGroup into which the new View will be added
     @param viewType The view type of the new View
     @return A new CategoryViewHolder
     */
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categories, parent, false);
        return new CategoryViewHolder(view);
    }

    /**
     Called to display the data at a specific position in the adapter
     @param holder The ViewHolder that holds the views to be bound
     @param position The position of the item within the adapter's data set
     */
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = categories.get(position);
        holder.categoryButton.setText(category);

        holder.categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCategoryClick(category);
            }
        });
    }

    /**
     Returns the total number of items in the data set held by the adapter.
     @return The total number of items in the adapter
     */
    @Override
    public int getItemCount() {
        return categories.size();
    }

    /**
     ViewHolder class that holds references to the views that will be bound
     */
    public static class CategoryViewHolder extends RecyclerView.ViewHolder {

        Button categoryButton;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryButton = itemView.findViewById(R.id.categoryButton);
        }
    }

    /**
     Setter for the category click listener
     @param listener Listener for category button click events
     */
    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    /**
     Interface for handling category button click events
     */
    public interface OnCategoryClickListener {
        void onCategoryClick(String category);
    }
}
