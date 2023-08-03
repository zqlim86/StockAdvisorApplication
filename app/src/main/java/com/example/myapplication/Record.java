package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.Model.AverageCostRecord;
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

/**

 The Record activity allows users to record their stock transactions, including the date of the transaction,
 the ticker symbol of the stock, the quantity of the stock, and the price of the stock. Users can then choose to
 either "buy" or "sell" the stock, which will be reflected in their logbook. The backPage variable is used to
 determine which page the user should be directed to when they press the back button.

 */
public class Record extends AppCompatActivity {

    private String backPage;
    EditText dateRecord, tickerRecord, quantityRecord, priceRecord;
    Button sellButton, buyButton;
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        // Initialize UI elements
        dateRecord = findViewById(R.id.dateRecord);
        tickerRecord = findViewById(R.id.tickerRecord);
        quantityRecord = findViewById(R.id.quantityRecord);
        priceRecord = findViewById(R.id.priceRecord);
        sellButton = findViewById(R.id.sellButton);
        buyButton = findViewById(R.id.buyButton);
        backButton = findViewById(R.id.backToLogbookButton);


        //Get ticker symbol from user.
        Intent intent = getIntent();
        if (intent != null) {
            backPage = intent.getStringExtra("back_page");
        }

        // Define back button behavior based on the backPage variable
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //The application will direct user to the previous page based on backPage variable.
                if(backPage!=null && backPage.equals("BackToLogbookPage")){
                    finish();
                }else{
                    startActivity(new Intent(Record.this, ChartActivity.class));
                }

            }
        });

        // Define buy button behavior
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String date = dateRecord.getText().toString().trim();
                String ticker = tickerRecord.getText().toString().trim().toUpperCase();
                String quantityString = quantityRecord.getText().toString().trim();
                String priceString = priceRecord.getText().toString().trim();
                int quantity = Integer.parseInt(quantityString);
                double price = Double.parseDouble(priceString);

                if(date.isEmpty()){
                    dateRecord.setError("Email is required to login.");
                    dateRecord.requestFocus();
                    return;
                }
                if(ticker.isEmpty()){
                    tickerRecord.setError("Email is required to login.");
                    tickerRecord.requestFocus();
                    return;
                }
                if(quantityString.isEmpty()){
                    quantityRecord.setError("Email is required to login.");
                    quantityRecord.requestFocus();
                    return;
                }
                if(priceString.isEmpty()){
                    priceRecord.setError("Email is required to login.");
                    priceRecord.requestFocus();
                    return;
                }

                //Store the BUY in realtime database.
                ShareRecord buyRecord = new ShareRecord(date, ticker, quantity, price);

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String userID = currentUser.getUid();

                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                String shareRecordId = userRef.child("shareRecords").push().getKey();
                userRef.child("shareRecords").child(shareRecordId).setValue(buyRecord);
                Toast.makeText(Record.this, "Record Buy Successful!",Toast.LENGTH_LONG).show();

                //Retrieving BUY and SELL data to make calculations.
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //Retrieving BUY and SELL records.
                        List<ShareRecord> buyRecords = new ArrayList<>();
                        List<ShareRecord> sellRecords = new ArrayList<>();

                        for(DataSnapshot childSnapshot : snapshot.child("shareRecords").getChildren()){
                            ShareRecord shareRecord = childSnapshot.getValue(ShareRecord.class);

                            //Only add the ticker that user wants.
                            if(shareRecord.getTicker().equals(ticker)){
                                if(shareRecord.getQuantity()>=0){
                                    buyRecords.add(shareRecord);
                                }
                                else{
                                    sellRecords.add(shareRecord);
                                }
                            }
                        }

                        // Calculate and record the equity of the user per ticker.
                        double equity = 0;
                        double totalCost = 0;
                        double totalRevenue = 0;

                        for(ShareRecord shareRecord : buyRecords){
                            if (shareRecord.getTicker().equals(ticker)) {
                                totalCost += shareRecord.getTotal();
                            }
                        }

                        for(ShareRecord shareRecord : sellRecords){
                            if (shareRecord.getTicker().equals(ticker)) {
                                totalRevenue += shareRecord.getTotal();
                            }
                        }
                        equity = totalRevenue + totalCost;
                        System.out.println(equity);

                        //Record the equity in realtime database.
                        DatabaseReference equityRef = userRef.child("equity");
                        double finalEquity = equity;
                        System.out.println(finalEquity);
                        equityRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.hasChild(ticker)){

                                    // if the Equity object for this ticker already exists, update its value
                                    TotalEquity existingEquity = snapshot.child(ticker).getValue(TotalEquity.class);
                                    existingEquity.setEquity(finalEquity);
                                    equityRef.child(ticker).setValue(existingEquity);

                                }else{

                                    // if the Equity object for this ticker doesn't exist, create a new one and add it to the database
                                    TotalEquity newEquity = new TotalEquity(ticker, finalEquity);
                                    equityRef.child(ticker).setValue(newEquity);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        //Calculate and store new AverageCost after buying.
                        double avgCost = 0;
                        double totalAvgCost = 0;
                        double totalSellCost = 0;
                        int totalQuantity = 0;
                        int totalSellQuantity = 0;

                        for(ShareRecord shareRecord : buyRecords){
                            if (shareRecord.getTicker().equals(ticker)) {
                                totalAvgCost += shareRecord.getTotal();
                                totalQuantity += shareRecord.getQuantity();
                            }
                        }

                        for(ShareRecord shareRecord : sellRecords){
                            if (shareRecord.getTicker().equals(ticker)) {
                                totalSellCost += shareRecord.getTotal();
                                totalSellQuantity += shareRecord.getQuantity();
                            }

                        }

                        avgCost = (totalAvgCost + totalSellCost) / (totalQuantity + totalSellQuantity);
                        double finalAvgCost = avgCost;

                        //Store new AverageCost after buying.
                        DatabaseReference avgCostRef = userRef.child("AverageCostRecord");
                        avgCostRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.hasChild(ticker)){

                                    // if the AverageCost object for this ticker already exists, update its profit
                                    AverageCostRecord averageCostRecord = snapshot.child(ticker).getValue(AverageCostRecord.class);
                                    averageCostRecord.setAvgCost(finalAvgCost);
                                    avgCostRef.child(ticker).setValue(averageCostRecord);

                                }else{

                                    // if the AverageCost object for this ticker doesn't exist, create a new one and add it to the database
                                    AverageCostRecord newAverageCostRecord = new AverageCostRecord(ticker, finalAvgCost);
                                    avgCostRef.child(ticker).setValue(newAverageCostRecord);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String date = dateRecord.getText().toString().trim();
                String ticker = tickerRecord.getText().toString().trim().toUpperCase();
                String quantityString = quantityRecord.getText().toString().trim();
                String priceString = priceRecord.getText().toString().trim();
                int quantity = Integer.parseInt(quantityString);
                double price = Double.parseDouble(priceString);

                //edit date to auto generate later.
                if(date.isEmpty()){
                    dateRecord.setError("Email is required to login.");
                    dateRecord.requestFocus();
                    return;
                }
                if(ticker.isEmpty()){
                    tickerRecord.setError("Email is required to login.");
                    tickerRecord.requestFocus();
                    return;
                }
                if(quantityString.isEmpty()){
                    quantityRecord.setError("Email is required to login.");
                    quantityRecord.requestFocus();
                    return;
                }
                if(priceString.isEmpty()){
                    priceRecord.setError("Email is required to login.");
                    priceRecord.requestFocus();
                    return;
                }



                //Store the SELL in realtime database.
                ShareRecord sellRecord = new ShareRecord(date, ticker, -quantity, price);

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String userID = currentUser.getUid();

                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                String shareRecordId = userRef.child("shareRecords").push().getKey();
                userRef.child("shareRecords").child(shareRecordId).setValue(sellRecord);
                Toast.makeText(Record.this, "Record Sell Successful!",Toast.LENGTH_LONG).show();


                //Retrieving BUY and SELL data to make calculations.
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //Retrieving BUY and SELL records.
                        List<ShareRecord> buyRecords = new ArrayList<>();
                        List<ShareRecord> sellRecords = new ArrayList<>();

                        for(DataSnapshot childSnapshot : snapshot.child("shareRecords").getChildren()){
                            ShareRecord shareRecord = childSnapshot.getValue(ShareRecord.class);

                            //Only add the ticker that user wants.
                            if(shareRecord.getTicker().equals(ticker)){
                                if(shareRecord.getQuantity()>=0){
                                    buyRecords.add(shareRecord);
                                } else{
                                    sellRecords.add(shareRecord);
                                }
                            }
                        }

                        // Calculate and record the equity of the user per ticker.
                        double equity = 0;
                        double totalCost = 0;
                        double totalRevenue = 0;

                        for(ShareRecord shareRecord : buyRecords){
                            if (shareRecord.getTicker().equals(ticker)) {
                                totalCost += shareRecord.getTotal();
                            }
                        }

                        for(ShareRecord shareRecord : sellRecords){
                            if (shareRecord.getTicker().equals(ticker)) {
                                totalRevenue += shareRecord.getTotal();
                            }
                        }
                        equity = totalRevenue + totalCost;
                        System.out.println(equity);

                        //Record the equity in realtime database.
                        DatabaseReference equityRef = userRef.child("equity");
                        double finalEquity = equity;
                        System.out.println(finalEquity);
                        equityRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.hasChild(ticker)){

                                    // if the Equity object for this ticker already exists, update its value
                                    TotalEquity existingEquity = snapshot.child(ticker).getValue(TotalEquity.class);
                                    existingEquity.setEquity(finalEquity);
                                    equityRef.child(ticker).setValue(existingEquity);

                                }else{

                                    // if the Equity object for this ticker doesn't exist, create a new one and add it to the database
                                    TotalEquity newEquity = new TotalEquity(ticker, finalEquity);
                                    equityRef.child(ticker).setValue(newEquity);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        //Calculate and store new AverageCost after selling.
                        double avgCost = 0;
                        double totalAvgCost = 0;
                        double totalSellCost = 0;
                        int totalQuantity = 0;
                        int totalSellQuantity = 0;

                        for(ShareRecord shareRecord : buyRecords){
                            if (shareRecord.getTicker().equals(ticker)) {
                                totalAvgCost += shareRecord.getTotal();
                                totalQuantity += shareRecord.getQuantity();
                            }
                        }

                        for(ShareRecord shareRecord : sellRecords){
                            if (shareRecord.getTicker().equals(ticker)) {
                                totalSellCost += shareRecord.getTotal();
                                totalSellQuantity += shareRecord.getQuantity();
                            }

                        }

                        avgCost = (totalAvgCost + totalSellCost) / (totalQuantity + totalSellQuantity);
                        double finalAvgCost = avgCost;

                        //Store new AverageCost after selling.
                        DatabaseReference avgCostRef = userRef.child("AverageCostRecord");
                        avgCostRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.hasChild(ticker)){

                                    // if the AverageCost object for this ticker already exists, update its profit
                                    AverageCostRecord averageCostRecord = snapshot.child(ticker).getValue(AverageCostRecord.class);
                                    averageCostRecord.setAvgCost(finalAvgCost);
                                    avgCostRef.child(ticker).setValue(averageCostRecord);

                                }else{

                                    // if the AverageCost object for this ticker doesn't exist, create a new one and add it to the database
                                    AverageCostRecord newAverageCostRecord = new AverageCostRecord(ticker, finalAvgCost);
                                    avgCostRef.child(ticker).setValue(newAverageCostRecord);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


    }
}