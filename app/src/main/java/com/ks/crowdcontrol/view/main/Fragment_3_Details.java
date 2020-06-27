package com.ks.crowdcontrol.view.main;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.ks.crowdcontrol.MainActivity;
import com.ks.crowdcontrol.R;
import com.ks.crowdcontrol.database.SupermarketDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Fragment_3_Details extends Fragment {
    private static final String TAG = "Fragment_Home";

    private Button btnNavFrag1;
    private LineChart chart;
    private SupermarketDTO currentSupermarketDTO;
    private TextView TVPersonCount, TVExpectedWaitingTime, TVTitle;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        //Logs for debugging
        Log.d(TAG, "onCreateView: started.");
        //View Setup
        View view = inflater.inflate(R.layout.fragment_3_details, container, false);
        btnNavFrag1 = (Button) view.findViewById(R.id.btnNavFrag1);
        TVPersonCount = view.findViewById(R.id.tV_person_count);
        TVExpectedWaitingTime = view.findViewById(R.id.tV_expected_wait_time);
        TVTitle = view.findViewById(R.id.tV_detail_title);
        chart = view.findViewById(R.id.chart);
        //Button Setup
        btnNavFrag1.setOnClickListener(view1 -> {
            ((MainActivity) Objects.requireNonNull(getActivity())).setViewPager(1);
        });
        currentSupermarketDTO = ((MainActivity) Objects.requireNonNull(getActivity())).getSupermarketDetailsID();
        if (currentSupermarketDTO != null) {
            //Text View Setup
            TVPersonCount.setText(new StringBuilder().append(currentSupermarketDTO.getCurrent_customers()).append("/").append(currentSupermarketDTO.getMax_customers()).append(" möglichen Kunden").toString());
            if (currentSupermarketDTO.getCurrent_customers() < currentSupermarketDTO.getMax_customers()) {
                TVExpectedWaitingTime.setText("Die voraussichtliche Wartezeit beträgt: " + "0 Minuten");
            } else {
                TVExpectedWaitingTime.setText("Die voraussichtliche Wartezeit beträgt: " + "5 Minuten");
            }
            TVTitle.setText(String.format("%s", currentSupermarketDTO.getName()));

            //Chart Setup
            List<Entry> entries = new ArrayList<>();
            //Map which contains Data from Supermarket
            int[] myNum = new int[24];
            ArrayList testArray= currentSupermarketDTO.getChartData();
            int count = 0;
            for(Object num : testArray){
                myNum[count] = Integer.parseInt(num.toString());
                count++;
            }

            Log.wtf("SupermarketDTO", currentSupermarketDTO.toString());

            for (int i = 0; i < 24; i++) {
                if (myNum[i] != 0)
                    entries.add(new Entry(i, myNum[i]));
            }

            LineDataSet dataSet = new LineDataSet(entries, currentSupermarketDTO.getName());
            dataSet.setColor(Color.BLUE);
            dataSet.setValueTextColor(Color.BLACK);
            LineData lineData = new LineData(dataSet);
            chart.setData(lineData);
            chart.invalidate();
        } else {
            Toast.makeText(getContext(), "No Supermarket available", Toast.LENGTH_SHORT).show();
        }
        return view;
    }
}