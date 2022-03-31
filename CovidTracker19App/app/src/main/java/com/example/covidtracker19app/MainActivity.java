package com.example.covidtracker19app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    CountryCodePicker countryCodePicker;
    TextView mtodaytotal,mtotal,mactive,mtodayactive,mrecovered,mtodayrecovered,mdeaths,mtodaydeaths;

    String country;
    TextView mfilter;
    Spinner spinner;
    String[] types={"cases","active","deaths","recovered"};
    private List<ModelClass> modelClassList;
    private List<ModelClass> modelClassList2;
    PieChart mpiechart;
    private RecyclerView recyclerView;
    com.example.covidtracker19app.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        countryCodePicker=findViewById(R.id.ccp);
        mtodayactive=findViewById(R.id.todayactive);
        mactive=findViewById(R.id.activecase);
        mdeaths=findViewById(R.id.deathcase);
        mtodaydeaths=findViewById(R.id.todaydeath);
        mrecovered=findViewById(R.id.recoveredcase);
        mtodayrecovered=findViewById(R.id.todayrecovered);
        mtodaytotal=findViewById(R.id.todaytotal);
        mpiechart=findViewById(R.id.piechart);
        spinner=findViewById(R.id.spinner);
        mfilter=findViewById(R.id.filter);
        recyclerView=findViewById(R.id.recyclerview);
        modelClassList=new ArrayList<>();
        modelClassList2=new ArrayList<>();

        spinner.setOnItemSelectedListener(this);
        ArrayAdapter arrayAdapter=new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(arrayAdapter);

        ApiUtilities.getAPIInterface().getCountryData().enqueue(new Callback<List<ModelClass>>() {
            @Override
            public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
                modelClassList.addAll(response.body());
            }

            @Override
            public void onFailure(Call<List<ModelClass>> call, Throwable t) {

            }
        });

        adapter=new Adapter(getApplicationContext(),modelClassList2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        countryCodePicker.setAutoDetectedCountry(true);
        country=countryCodePicker.getSelectedCountryName();
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                country=countryCodePicker.getSelectedCountryName();
                fetchdata();
            }
        });

        fetchdata();

    }

    private void fetchdata() {

        ApiUtilities.getAPIInterface().getCountryData().enqueue(new Callback<List<ModelClass>>() {
            @Override
            public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
                modelClassList.addAll(response.body());
                for(int i=0;i<modelClassList.size();i++){

                    if(modelClassList.get(i).getCountry().equals(country)){
                        mactive.setText((modelClassList.get(i).getActive()));
                        mtodaydeaths.setText((modelClassList.get(i).getTodayDeaths()));
                        mtodayrecovered.setText((modelClassList.get(i).getTodayRecovered()));
                        mtodaytotal.setText((modelClassList.get(i).getTodayCases()));
                        mtotal.setText((modelClassList.get(i).getCases()));
                        mdeaths.setText((modelClassList.get(i).getDeaths()));
                        mrecovered.setText((modelClassList.get(i).getRecovered()));

                        int active,recovered,deaths,total;

                        active=Integer.parseInt(modelClassList.get(i).getActive());
                        recovered=Integer.parseInt(modelClassList.get(i).getRecovered());
                        deaths=Integer.parseInt(modelClassList.get(i).getDeaths());
                        total=Integer.parseInt(modelClassList.get(i).getCases());

                        updategraph(total,active,recovered,deaths);
                    };
                }
            }

            @Override
            public void onFailure(Call<List<ModelClass>> call, Throwable t) {

            }
        });



    }

    private void updategraph(int cases, int active, int recovered, int deaths) {
        mpiechart.clearChart();
        mpiechart.addPieSlice(new PieModel("Confirm",cases, Color.parseColor("#FFB701")));
        mpiechart.addPieSlice(new PieModel("Active",cases, Color.parseColor("#FF4CAF50")));
        mpiechart.addPieSlice(new PieModel("Recovered",cases, Color.parseColor("#38ACCD")));
        mpiechart.addPieSlice(new PieModel("Deaths",cases, Color.parseColor("#F55C47")));
        mpiechart.startAnimation();

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

        String item=types[position];
        mfilter.setText(item);
        Adapter.filter(item);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}