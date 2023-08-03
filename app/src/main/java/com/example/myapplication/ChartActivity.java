package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.service.autofill.Dataset;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Client.NewsSentimentClient;
import com.example.myapplication.Client.RetrofitClient;
import com.example.myapplication.Interface.GetStockInterface;
import com.example.myapplication.Interface.NewsSentimentInterface;
import com.example.myapplication.Interface.PredictionInterface;
import com.example.myapplication.Model.AverageCostRecord;
import com.example.myapplication.Model.Prediction;
import com.example.myapplication.Model.Sentiment;
import com.example.myapplication.Model.ShareRecord;
import com.example.myapplication.Model.Stock;
import com.example.myapplication.Model.StockInfo;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChartActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    private String ticker;
    private CombinedChart mCombinedChart;
    private TextView actualClose, stockDate, avgCostTextView, predictedClose;
    private LineDataSet predictionLineDataSet;
    private LineDataSet avgCostDataSet;
    private CandleDataSet candleDataSet;
    private ArrayList<String> xVals = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        //Get ticker symbol from user.
        Intent intent = getIntent();
        if (intent != null) {
            ticker = intent.getStringExtra("ticker_symbol");
        }

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChartActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("switch_to_fragment", "market");
                startActivity(intent);
            }
        });

        Button recordTransactionButton = findViewById(R.id.chartRecordTransactionButton);
        recordTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChartActivity.this, Record.class));
            }
        });

        mCombinedChart = findViewById(R.id.combinedChart);
        mCombinedChart.setDrawBorders(false);
        mCombinedChart.setPinchZoom(true);
        mCombinedChart.setBorderColor(getResources().getColor(R.color.green));
        mCombinedChart.setNoDataText("Loading Data...");
        Paint p = mCombinedChart.getPaint(Chart.PAINT_INFO);
        p.setColor(getResources().getColor(R.color.backgroundwhite));

        YAxis yAxis = mCombinedChart.getAxisLeft();
        YAxis rightAxis = mCombinedChart.getAxisRight();
        yAxis.setDrawGridLines(true);
        rightAxis.setDrawGridLines(true);

        XAxis xAxis = mCombinedChart.getXAxis();
        xAxis.setDrawGridLines(true);
        xAxis.setDrawLabels(true);
        xAxis.setLabelCount(3,true);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setAvoidFirstLastClipping(false);

        rightAxis.setTextColor(Color.WHITE);
        yAxis.setDrawGridLines(false);
        yAxis.setDrawLabels(false);


        Legend l = mCombinedChart.getLegend();
        l.setEnabled(false);
        mCombinedChart.getDescription().setEnabled(false);

        CombinedData combinedData = new CombinedData();

        getNews(ticker);
        getStockInfo(ticker);

        // Get Stock Price API
        final GetStockInterface[] getStockInterface = {RetrofitClient.getRetrofitInstance().create(GetStockInterface.class)};
        Call<List<Stock>> call = getStockInterface[0].getStockQuote4h(ticker);

        // Call API, Get stock data and plot it in candlestick chart
        call.enqueue(new Callback<List<Stock>>() {
            @Override
            public void onResponse(Call<List<Stock>> call, Response<List<Stock>> response) {

                List<Stock> data = response.body();
                ArrayList<CandleEntry> yValsCandleStick= new ArrayList<>();
                System.out.println("Size:"+data.size());

                if(data.size() > 0){
                    Collections.reverse(data);
                    for (int i = 0; i < data.size(); i++) {
                        Stock stock = data.get(i);
                        xVals.add(stock.getDate());
                        yValsCandleStick.add(new CandleEntry(i, (float)stock.getHigh(), (float)stock.getLow(), (float)stock.getOpen(), (float)stock.getClose()));
                    }

                    xAxis.setLabelCount(3);
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(xVals));

                    candleDataSet = new CandleDataSet(yValsCandleStick, "DataSet 1");
                    candleDataSet.setColor(Color.rgb(80, 80, 80));
                    candleDataSet.setShadowColor(getResources().getColor(R.color.lightgrey));
                    candleDataSet.setShadowWidth(1f);
                    candleDataSet.setDecreasingColor(getResources().getColor(R.color.red));
                    candleDataSet.setDecreasingPaintStyle(Paint.Style.FILL);
                    candleDataSet.setIncreasingColor(getResources().getColor(R.color.green));
                    candleDataSet.setIncreasingPaintStyle(Paint.Style.FILL);
                    candleDataSet.setNeutralColor(Color.LTGRAY);
                    candleDataSet.setDrawValues(false);

                    CandleData candleData = new CandleData(candleDataSet);

                    //Plot True Average Cost.
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    String userID = currentUser.getUid();
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                    DatabaseReference avgCostRef = userRef.child("AverageCostRecord");

                    avgCostRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            List<Entry> avgCostEntry = new ArrayList<>();
                            double test=0;
                            //this needs to change later from "AAPL" to `ticker` to be dynamic.
                            if(snapshot.hasChild(ticker)){
                                AverageCostRecord averageCostRecord = snapshot.child(ticker).getValue(AverageCostRecord.class);
                                for(int i = 0; i<data.size(); i++){
                                    avgCostEntry.add(new Entry(i, (float)averageCostRecord.getAvgCost()));
                                    test = averageCostRecord.getAvgCost();
                                }
                            }

                            System.out.println("Average Cost:" + test);



                            //Prediction Line Data
                            PredictionInterface predictionInterface = com.example.myapplication.Client.PredictionClient.getPredictionInstance().create(PredictionInterface.class);
                            Call<List<Prediction>> callPrediction = predictionInterface.getPrediction(ticker);

                            callPrediction.enqueue(new Callback<List<Prediction>>() {
                                @Override
                                public void onResponse(Call<List<Prediction>> call, Response<List<Prediction>> response) {
                                    List<Prediction> data = response.body();
                                    List<Entry> lineEntry = new ArrayList<>();

                                    if(data.size()>0){
                                        int count = data.size()-1; //223 items

                                        for(int i = 0; i < data.size(); i++){
                                            if(count == 0){
                                                Prediction prediction = data.get(i);
                                                lineEntry.add(new Entry(yValsCandleStick.size() + 1, (float) prediction.getClosePrice()));
                                                System.out.println(prediction.getClosePrice());
                                                count--;
                                            }else{
                                                Prediction prediction = data.get(i);
                                                lineEntry.add(new Entry(yValsCandleStick.size() - count, (float) prediction.getClosePrice()));
                                                System.out.println(prediction.getClosePrice());
                                                count--;
                                            }
                                        }

                                        avgCostDataSet = new LineDataSet(avgCostEntry, "True Average Cost"); // Label is optional
                                        avgCostDataSet.setColor(Color.RED);
                                        avgCostDataSet.setLineWidth(1.5f);
                                        avgCostDataSet.setDrawCircles(false);
                                        avgCostDataSet.setDrawValues(false);

                                        predictionLineDataSet = new LineDataSet(lineEntry, "Prediction"); // Label is optional
                                        predictionLineDataSet.setColor(Color.YELLOW);
                                        predictionLineDataSet.setLineWidth(1.5f);
                                        predictionLineDataSet.setDrawCircles(false);
                                        predictionLineDataSet.setDrawValues(false);

                                        LineData lineData = new LineData();
                                        lineData.addDataSet(avgCostDataSet);
                                        lineData.addDataSet(predictionLineDataSet);

                                        combinedData.setData(lineData);
                                        combinedData.setData(candleData);
                                        mCombinedChart.setData(combinedData);
                                        mCombinedChart.setVisibleXRange(0f,50f);
                                        mCombinedChart.moveViewToX(xVals.size());
                                        mCombinedChart.invalidate();

                                    }
                                }

                                @Override
                                public void onFailure(Call<List<Prediction>> call, Throwable t) {
                                    System.out.println("Prediction API Call Error: " + t.getMessage());
                                }
                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            System.out.println("Failed to get true average cost from database.");

                        }
                    });

                }
            }

            @Override
            public void onFailure(Call<List<Stock>> call, Throwable t) {
                System.out.println("Stock Price API Call Error: " + t.getMessage());
            }
        });


        mCombinedChart.setOnChartValueSelectedListener(this);


    }

    // Display value in the chart under the key statistic section.
    @Override
    public void onValueSelected(Entry entry, Highlight highlight) {
        // Retrieve the selected data and display it
        int index = (int) highlight.getX();
        if (index >= 0 && index < candleDataSet.getEntryCount()+1) {

            float avgCost = avgCostDataSet.getEntryForIndex(index).getY();
            float actualPrice = candleDataSet.getEntryForIndex(index).getClose();
            String date = xVals.get(index);

            System.out.println(index);

            // Assign variables to widget's id.
            stockDate = findViewById(R.id.stockDate);
            actualClose = findViewById(R.id.actualClose);
            predictedClose = findViewById(R.id.predictedClose);
            avgCostTextView = findViewById(R.id.avgCost);

            String actualPriceStr = String.format(Locale.US, "$%.2f", actualPrice);
            String avgCostStr = String.format(Locale.US, "$%.2f", avgCost);
            stockDate.setText(date);
            actualClose.setText(actualPriceStr);
            avgCostTextView.setText(avgCostStr);

            int predictedIndex = predictionLineDataSet.getEntryCount() - candleDataSet.getEntryCount() + index;
            if (predictedIndex >= 0 && predictedIndex < predictionLineDataSet.getEntryCount()) {
                float predictedPrice = predictionLineDataSet.getEntryForIndex(predictedIndex).getY();
                predictedClose.setText(String.format(Locale.US, "$%.2f", predictedPrice));
            } else {
                predictedClose.setText("-");
            }

            System.out.println("Date: " + date +"\nActual Price: " + actualPrice + "\nAverage Cost: " + avgCost);
        }
    }

    @Override
    public void onNothingSelected() {

    }

    // Navigate user back to the previous page if back button is pressed.
    @Override
    public void onBackPressed() {
        Fragment fragment = new MarketFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainActivity, fragment);
        fragmentTransaction.commit();
    }

    // Retrieve stock data from API and display on the page.
    public void getStockInfo(String ticker){
        GetStockInterface getStockInfoInterface = RetrofitClient.getRetrofitInstance().create(GetStockInterface.class);
        Call <List<StockInfo>> call = getStockInfoInterface.getStockInfo(ticker);

        TextView textTicker = findViewById(R.id.textTicker);
        TextView textTickerName = findViewById(R.id.textTickerName);
        TextView currentPrice = findViewById(R.id.currentPrice);
        TextView changesPercentage = findViewById(R.id.changesPercentage);
        TextView dailyHigh = findViewById(R.id.dailyHigh);
        TextView dailyLow = findViewById(R.id.dailyLow);
        TextView dailyVolume = findViewById(R.id.dailyVolume);
        TextView textPeRatio = findViewById(R.id.peRatio);

        call.enqueue(new Callback<List<StockInfo>>() {
            @Override
            public void onResponse(Call<List<StockInfo>> call, Response<List<StockInfo>> response) {
                List<StockInfo> data = response.body();
                StockInfo stockInfo = data.get(0);

                String tickerSymbol = stockInfo.getSymbol();
                String tickerName = stockInfo.getName();
                double currentPriceData = stockInfo.getPrice();
                String currentPriceText = String.format(Locale.US, "$%.2f", stockInfo.getPrice());
                String currentChangesText = String.format(Locale.US, "$%.2f", stockInfo.getChangesPercentage());
                String dailyHighText = String.format(Locale.US, "$%.2f", stockInfo.getDayHigh());
                String dailyLowText = String.format(Locale.US, "$%.2f", stockInfo.getDayLow());
                String dailyVolumeText = String.format(Locale.US, "$%,d", stockInfo.getVolume());

                textTicker.setText(tickerSymbol);
                textTickerName.setText(tickerName);
                currentPrice.setText(currentPriceText);
                changesPercentage.setText(currentChangesText);
                dailyHigh.setText(dailyHighText);
                dailyLow.setText(dailyLowText);
                dailyVolume.setText(dailyVolumeText);

                GetStockInterface getEpsInterface = RetrofitClient.getRetrofitInstance().create(GetStockInterface.class);
                Call<JsonArray> callFinancial = getEpsInterface.getFinancials(ticker);
                callFinancial.enqueue(new Callback<JsonArray>() {
                    @Override
                    public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                        if (response.body() != null) {
                            JsonArray jsonArray = response.body().getAsJsonArray();
                            JsonObject financials = jsonArray.get(0).getAsJsonObject();
                            double eps = financials.get("eps").getAsDouble();
                            double peRatio = currentPriceData / eps;

                            String peRatioString = String.format(Locale.US, "%.2f", peRatio);
                            textPeRatio.setText(peRatioString);
                        }else{
                            System.out.println("Failed to retrieve eps 1.0.\n");
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonArray> call, Throwable t) {
                        System.out.println("Failed to retrieve eps 2.0.\n" + t.getMessage());
                    }
                });
            }

            @Override
            public void onFailure(Call<List<StockInfo>> call, Throwable t) {
                System.out.println("Failed to retrieve stock info. \n" + t.getMessage());
            }
        });

    }

    // Retrieve news sentiment data from API and display on the page.
    public void getNews(String ticker){

        TextView latestSentiment = findViewById(R.id.newsSentiment);

        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);

        System.out.println(formattedDate);

        NewsSentimentInterface newsSentimentInterface = NewsSentimentClient.getNewsSentimentInstance().create(NewsSentimentInterface.class);
        Call<JsonObject> call = newsSentimentInterface.getNewsSentiment(ticker, "2023-01-01", formattedDate, "642e6f1b5958d1.90264888");

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                if (jsonObject != null) {
                    JsonArray sentimentArray = jsonObject.getAsJsonArray(ticker + ".US");
                    JsonObject firstSentiment = sentimentArray.get(0).getAsJsonObject();

                    double normalized = firstSentiment.get("normalized").getAsDouble();
                    double normalizedPercentage = ((normalized + 1) / 2 * 100);
                    String formattedPercentage = String.format(Locale.getDefault(), "%.2f%%", normalizedPercentage);

                    if(normalizedPercentage < 50.0){
                        latestSentiment.setTextColor(getColor(R.color.sellButtonColor));
                        latestSentiment.setText(formattedPercentage);
                    }else{
                        latestSentiment.setTextColor(getColor(R.color.green));
                        latestSentiment.setText(formattedPercentage);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

    }
}