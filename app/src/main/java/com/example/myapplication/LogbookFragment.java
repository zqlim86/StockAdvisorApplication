package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.myapplication.Model.ShareRecord;
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

/* This is a fragment for displaying the logbook of a user's share transactions.
It includes functionality to retrieve data from the Firebase database, display it in a RecyclerView,
and allow the user to filter the transactions by ticker symbol using an EditText and Button.
 */
public class LogbookFragment extends Fragment {

    List<ShareRecord> shareRecord = new ArrayList<>();
    ShareRecordAdapter shareRecordAdapter = new ShareRecordAdapter(getActivity(), shareRecord);
    List<ShareRecord> shareRecordsList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logbook, container, false);

        // Get references to the EditText and Button
        EditText tickerSearchText = view.findViewById(R.id.tickerSearchText);
        Button showLogbookButton = view.findViewById(R.id.showLogbookButton);
        Button recordTransactionButton = view.findViewById(R.id.recordTransactionButton);

        // Set OnClickListener to showLogbookButton
        showLogbookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ticker = tickerSearchText.getText().toString().trim().toUpperCase(Locale.ROOT);
                searchShareRecords(ticker);
            }
        });

        // Set OnClickListener to recordTransactionButton
        recordTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String backPage = "BackToLogbookPage";

                //Send backPage variable to record transaction page.
                Intent intent = new Intent(getActivity(), Record.class);
                intent.putExtra("back_page", backPage);
                startActivity(intent);
            }
        });

        //Initializing RecyclerView.
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewLogBook);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.setAdapter(shareRecordAdapter);

        // Retrieve ShareRecord objects from Firebase database
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userID = currentUser.getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userID);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                shareRecordsList.clear(); // Clear the list before adding new data
                for (DataSnapshot snapshot : dataSnapshot.child("shareRecords").getChildren()) {
                    ShareRecord shareRecord = snapshot.getValue(ShareRecord.class);
                    shareRecordsList.add(shareRecord);
                }
                shareRecordAdapter.setData(shareRecordsList); // Set the data to the adapter
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error retrieving share records: " + databaseError.getMessage());
            }
        });


        return view;
    }

    // Function to filter the list of ShareRecords by ticker symbol
    private void searchShareRecords(String ticker) {
        List<ShareRecord> filteredShareRecords = new ArrayList<>();
        for (ShareRecord shareRecord : shareRecordsList) {
            if (shareRecord.getTicker().equalsIgnoreCase(ticker)) {
                filteredShareRecords.add(shareRecord);
            }
        }
        shareRecordAdapter.setData(filteredShareRecords); // Set the data to the adapter
    }
}