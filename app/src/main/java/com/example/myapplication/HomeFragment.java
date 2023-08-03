package com.example.myapplication;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Model.ShareRecord;
import com.example.myapplication.Model.TotalEquity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        TextView equityText = view.findViewById(R.id.equityText);

        PieChart pieChart = view.findViewById(R.id.portfolioPieChart);
        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleRadius(80f);
        pieChart.setTransparentCircleRadius(85f);
        pieChart.setRotationEnabled(false);
        pieChart.setUsePercentValues(true);

        RecyclerView stockRecyclerView = view.findViewById(R.id.portfolioRecyclerView);
        stockRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));


        // Retrieve ShareRecord objects from Firebase database
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userID = currentUser.getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userID);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<TotalEquity> portfolioList = new ArrayList<>();
                HashMap<String, Double> tickerTotalMap = new HashMap<>();
                double totalEquity = 0;

                for (DataSnapshot dataSnapshot : snapshot.child("equity").getChildren()){
                    TotalEquity totalEquities = dataSnapshot.getValue(TotalEquity.class);
                    String ticker = totalEquities.getTicker();
                    double equityValue = totalEquities.getEquity();
                    tickerTotalMap.put(ticker, equityValue);
                    totalEquity += equityValue;
                }

                equityText.setText(String.format(Locale.getDefault(), "$%.2f", totalEquity));

                for(Map.Entry<String, Double> entry : tickerTotalMap.entrySet()){
                    TotalEquity totalEquityObject = new TotalEquity(entry.getKey(), entry.getValue());
                    portfolioList.add(totalEquityObject);
                }

                setData(pieChart, tickerTotalMap);
                // Set up recycler view
                PortfolioAdapter portfolioAdapter = new PortfolioAdapter(getContext(), portfolioList);
                stockRecyclerView.setAdapter(portfolioAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    /**
     Sets the data for a given PieChart based on the provided HashMap.
     @param pieChart The PieChart for which the data is being set
     @param tickerTotalMap The HashMap containing the ticker symbols and total values for each ticker
     */
    private void setData(PieChart pieChart, HashMap<String, Double> tickerTotalMap) {
        List<PieEntry> entries = new ArrayList<>();

        for (Map.Entry<String, Double> entry : tickerTotalMap.entrySet()) {
            entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
        }

        if (getContext() != null){
            pieChart.getDescription().setEnabled(false);
            pieChart.setHoleRadius(50f);
            pieChart.setHoleColor(getResources().getColor(R.color.backgroundwhite));
            pieChart.setTransparentCircleRadius(60f);
            pieChart.setAlpha(0.9f);

            Legend legend = pieChart.getLegend();
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            legend.setOrientation(Legend.LegendOrientation.VERTICAL);
            legend.setDrawInside(false);
            legend.setTextColor(getResources().getColor(R.color.backgroundwhite));
            legend.setTextSize(15f);
            legend.setEnabled(false);

            PieDataSet dataSet = new PieDataSet(entries, "");
            dataSet.setColors(ColorTemplate.getHoloBlue());
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);
            PieData pieData = new PieData(dataSet);
            pieData.setValueTextSize(15f);
            pieData.setValueTextColor(getResources().getColor(R.color.backgroundwhite));

            Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.montserratsemibold);
            pieChart.setCenterTextTypeface(typeface);
            pieChart.setCenterText("My Equity");
            pieChart.setCenterTextSize(20f);
            pieChart.setCenterTextColor(getResources().getColor(R.color.black));


            pieChart.setData(pieData);
            pieChart.invalidate();
        }

    }

}