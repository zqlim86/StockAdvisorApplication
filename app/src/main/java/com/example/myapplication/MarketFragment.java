package com.example.myapplication;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.Client.RetrofitClient;
import com.example.myapplication.Interface.GetStockInterface;
import com.example.myapplication.Model.Stock;
import com.example.myapplication.Model.StockProfile;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MarketFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener {

    private TextView marketTitle;
    private EditText tickerEditText;
    private Button showChartButton;
    private RecyclerView stockProfileRecyclerView;
    private StockProfileAdapter stockProfileAdapter;
    private List<StockProfile> stockProfiles = new ArrayList<>();

    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private List<String> categories = new ArrayList<>();
    private List<String> stockSymbols = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_market, container, false);

        marketTitle = view.findViewById(R.id.marketTitle);
        tickerEditText = view.findViewById(R.id.tickerEditText);
        showChartButton = view.findViewById(R.id.showChartButton);
        stockProfileRecyclerView = view.findViewById(R.id.stockProfileRecyclerView);
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        stockProfileRecyclerView.setLayoutManager(layoutManager);

        stockProfileAdapter = new StockProfileAdapter(getActivity(), stockProfiles);
        stockProfileRecyclerView.setAdapter(stockProfileAdapter);

        // List of categories
        categories.add("Top Gainers");
        categories.add("Top Losers");
        categories.add("Most Active");


        // Set up category RecyclerView and adapter
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        categoryRecyclerView.setLayoutManager(layoutManager2);
        categoryAdapter = new CategoryAdapter(categories, this::onCategoryClick);
        categoryRecyclerView.setAdapter(categoryAdapter);

        categoryAdapter.setOnCategoryClickListener(this);
        onCategoryClick("Top Gainers");

        //React to "Show Chart" Button.
        showChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tickerSymbol = tickerEditText.getText().toString().toUpperCase(Locale.ROOT);

                //Send ticker that user input to ChartActivity.
                Intent intent = new Intent(getActivity(), ChartActivity.class);
                intent.putExtra("ticker_symbol", tickerSymbol);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onCategoryClick(String category) {
        // Clear the stockProfiles list
        stockProfiles.clear();

        GetStockInterface getStockInterface = RetrofitClient.getRetrofitInstance().create(GetStockInterface.class);

        // Filter the stock symbols based on the selected category
//        List<String> stockSymbols = new ArrayList<>();
        switch (category) {
            case "Most Active":
//                Call<JsonArray> call =  getStockInterface.getMostActive();
//                call.enqueue(new Callback<JsonArray>() {
//                    @Override
//                    public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
//                        if(response.body() != null){
//                            JsonArray jsonArray = response.body().getAsJsonArray();
//
//                            for(int i=0; i<5; i++){
//                                JsonObject mostActive = jsonArray.get(i).getAsJsonObject();
//                                stockSymbols.add(mostActive.get("symbol").getAsString());
//                            }
//
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<JsonArray> call, Throwable t) {
//                        System.out.println("Failed to retrive Most Active." + t.getMessage());
//                    }
//                });
                marketTitle.setText("Most Active");
                stockSymbols = Arrays.asList("AAPL", "MSFT", "AMZN");
                break;
            case "Top Gainers":
//                Call<JsonArray> call2 =  getStockInterface.getTopGainers();
//                call2.enqueue(new Callback<JsonArray>() {
//                    @Override
//                    public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
//                        if(response.body() != null){
//                            JsonArray jsonArray = response.body().getAsJsonArray();
//
//                            for(int i=0; i<5; i++){
//                                JsonObject mostActive = jsonArray.get(i).getAsJsonObject();
//                                stockSymbols.add(mostActive.get("symbol").getAsString());
//                            }
//
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<JsonArray> call, Throwable t) {
//                        System.out.println("Failed to retrive Top Gainers." + t.getMessage());
//                    }
//                });

                marketTitle.setText("Top Gainers");
                stockSymbols = Arrays.asList("AAPL", "TSLA", "NFLX");
                break;
            case "Top Losers":
//                Call<JsonArray> call3 =  getStockInterface.getTopLosers();
//                call3.enqueue(new Callback<JsonArray>() {
//                    @Override
//                    public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
//                        if(response.body() != null){
//                            JsonArray jsonArray = response.body().getAsJsonArray();
//
//                            for(int i=0; i<5; i++){
//                                JsonObject mostActive = jsonArray.get(i).getAsJsonObject();
//                                stockSymbols.add(mostActive.get("symbol").getAsString());
//                            }
//
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<JsonArray> call, Throwable t) {
//                        System.out.println("Failed to retrive Top Losers." + t.getMessage());
//                    }
//                });

                marketTitle.setText("Top Losers");
                stockSymbols = Arrays.asList("GOOG", "MSFT");
                break;
        }

        // Fetch stock profiles for each symbol and add them to the list
        for (String symbol : stockSymbols) {
            setStockProfile(symbol);
        }

        // Notify the adapter that the data set has changed
        stockProfileAdapter.notifyDataSetChanged();
    }

    public void setStockProfile(String ticker){
        GetStockInterface getStockInterface = RetrofitClient.getRetrofitInstance().create(GetStockInterface.class);
        Call<List<StockProfile>> call =  getStockInterface.getStockProfile(ticker);

        call.enqueue(new Callback<List<StockProfile>>() {
            @Override
            public void onResponse(Call<List<StockProfile>> call, Response<List<StockProfile>> response) {
                List<StockProfile> data = response.body();
                if (data != null && data.size() > 0) {
                    stockProfiles.addAll(data);
                    stockProfileAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<StockProfile>> call, Throwable t) {

            }
        });
    }


}



