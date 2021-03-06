package com.example.androidapp.Fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.Activities.NewClientActivity;
import com.example.androidapp.Activities.RevenueChartActivity;
import com.example.androidapp.Data.AppDatabase;
import com.example.androidapp.Data.ClientData.Client;
import com.example.androidapp.Data.ClientData.ClientViewModel;
import com.example.androidapp.Data.DayRevenueData.DayRevenue;
import com.example.androidapp.Data.MonthRevenueData.MonthRevenue;
import com.example.androidapp.Data.MonthRevenueData.MonthRevenueViewModel;
import com.example.androidapp.Data.ProductType.ProductType;
import com.example.androidapp.Data.ProductType.ProductTypeListAdapter;
import com.example.androidapp.Data.ProductType.ProductTypeViewModel;
import com.example.androidapp.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChartFragment extends Fragment {
    private Button moreInfoBtn;
    private View dayCol;
    private View monthCol;

    private TextView tvMonthRev;
    private TextView tvDayRev;
    private TextView tvTotalMonthOrders;

    private PieChart pieChart;

    private ProductTypeViewModel productTypeViewModel;
    private MonthRevenueViewModel monthRevenueViewModel;

    private ProductTypeListAdapter productTypeListAdapter;

    private ArrayList<Integer> colorList;
    private List<ProductType> productTypeList;
    private List<Double> productTypePercentage;
    private List<Integer> productTypeOrders;

    private Date nowDate;
    private DateFormat formatter;
    private String strCurrentMonth;
    private String strCurrentDay;

    private final double TOTAL_HEIGHT = 160;
    private double monthColHeight = 130;
    private double dayColHeight = 100;

    //Test number for display chart
    private double totalMonthRev;
    private int totalMonthOrders;
    private double dayRevenue;
    private double monthRevenue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chart, container, false);
        productTypeList = getListCategory();

        //Date related
        nowDate = Calendar.getInstance().getTime();
        formatter = new SimpleDateFormat("MM/yyyy");
        strCurrentMonth = formatter.format(nowDate);
        formatter = new SimpleDateFormat("dd/MM/yyyy");
        strCurrentDay = formatter.format(nowDate);

        initUI(v);

        //Calculate necessary data
        calculateColHeight();
        calculateProductTypePercentage();
        calculateProductTypeOrders();

        //set up "bar view"
        displayBarChart();
        displayCurrentMonthRevenue();

        //set up pie chart
        loadColors();
        setUpPieChart();
        loadPieChartData();
        displayCurrentMonthTotalOrders();

    
        //create recycler view and adapter
        RecyclerView rcvData = v.findViewById(R.id.pie_chart_note);
        rcvData.setLayoutManager(new LinearLayoutManager(v.getContext()));
        productTypeListAdapter = new ProductTypeListAdapter(getContext(), productTypeList, colorList, productTypePercentage, productTypeOrders);
        rcvData.setAdapter(productTypeListAdapter);

        moreInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RevenueChartActivity.class);
                activityResultLauncher.launch(intent);
            }
        });

        return v;
    }

    private void initUI(View view){
        moreInfoBtn = view.findViewById(R.id.more_info_btn);
        dayCol = view.findViewById(R.id.col_day);
        monthCol = view.findViewById(R.id.col_month);
        pieChart = view.findViewById(R.id.pie_chart);
        tvDayRev = view.findViewById(R.id.revenue_day);
        tvMonthRev = view.findViewById(R.id.revenue_month);
        tvTotalMonthOrders = view.findViewById(R.id.total_orders);
    }

    private static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    //Temporary algorithm
    private void calculateColHeight(){
        List<MonthRevenue> monthRevenueList = AppDatabase.getInstance(getActivity()).monthRevenueDao().getAllMonthRevenues();
        List<DayRevenue> dayRevenueList = AppDatabase.getInstance(getActivity()).dayRevenueDao().getAllDayRevenues();

        monthRevenue = getMonth(monthRevenueList, strCurrentMonth).getMonthRevenue();
        dayRevenue = getDay(dayRevenueList, strCurrentDay).getDayRevenue();

        double totalRevenue = dayRevenue + monthRevenue;
        double dayRatio = dayRevenue / totalRevenue;
        double monthRatio = monthRevenue / totalRevenue;

        dayColHeight = TOTAL_HEIGHT * dayRatio;
        monthColHeight = TOTAL_HEIGHT * monthRatio;
    }

    private void displayBarChart(){
        monthCol.getLayoutParams().height = dpToPx((int) monthColHeight);
        dayCol.getLayoutParams().height = dpToPx((int) dayColHeight);
        tvDayRev.setText(String.valueOf(dayRevenue));
        tvMonthRev.setText(String.valueOf(monthRevenue));
    }

    private void setUpPieChart(){
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(40);
        pieChart.setTransparentCircleRadius(45);
        pieChart.setDrawEntryLabels(false);
        pieChart.setUsePercentValues(true);
//        pieChart.setEntryLabelTextSize(12);
//        pieChart.setEntryLabelColor(Color.BLACK);
//        pieChart.setCenterText("Month revenue");
//        pieChart.setCenterTextSize(24);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
    }

    private void loadColors(){
        colorList = new ArrayList<>();
        int[] colors = getContext().getResources().getIntArray(R.array.colors);
        for (int i = 0; i < productTypeList.size(); i++){
            colorList.add(colors[i]);
        }
    }

    //Temporary
    private void calculateProductTypePercentage(){
        productTypePercentage = new ArrayList<>();
        for (int i = 0; i < productTypeList.size(); i++){
            productTypePercentage.add(Double.valueOf(30*i));
        }

//        productTypePercentage.add(Double.valueOf(30));
//        productTypePercentage.add(Double.valueOf(20));
//        productTypePercentage.add(Double.valueOf(50));
    }

    //Temporary
    private void calculateProductTypeOrders(){
        productTypeOrders = new ArrayList<>();
        for (int i = 0; i < productTypeList.size(); i++) {
            productTypeOrders.add(10 * i);
        }

//        productTypeOrders.add(26);
//        productTypeOrders.add(30);
//        productTypeOrders.add(15);
    }

    private void loadPieChartData(){
        //Entries
        ArrayList<PieEntry> entries = new ArrayList<>();
        //value i is for testing
        int i = 1;
        for (ProductType productType : productTypeList){
            entries.add(new PieEntry(0 * i, productType.getName()));
            i *= 2;
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colorList);

        PieData data = new PieData(dataSet);
        data.setDrawValues(false);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(10);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.invalidate();
        //Animation
        //pieChart.animateY(1400, Easing.EaseInOutQuad);
    }

    private void displayCurrentMonthTotalOrders(){
        monthRevenueViewModel = new ViewModelProvider(this).get(MonthRevenueViewModel.class);
        monthRevenueViewModel.getAllMonthRevenuesLive().observe(getActivity(), new Observer<List<MonthRevenue>>() {
            @Override
            public void onChanged(List<MonthRevenue> monthRevenues) {
                totalMonthOrders = getMonth(monthRevenues, strCurrentMonth).getNumberOfOrders();
                tvTotalMonthOrders.setText(String.valueOf(totalMonthOrders).trim());
            }
        });
    }

    private void displayCurrentMonthRevenue() {
        monthRevenueViewModel = new ViewModelProvider(this).get(MonthRevenueViewModel.class);
        monthRevenueViewModel.getAllMonthRevenuesLive().observe(getActivity(), new Observer<List<MonthRevenue>>() {
            @Override
            public void onChanged(List<MonthRevenue> monthRevenues) {
                totalMonthRev = getMonth(monthRevenues, strCurrentMonth).getMonthRevenue();
                tvMonthRev.setText(String.valueOf(totalMonthRev).trim());
            }
        });
    }



    private List<ProductType> getListCategory() {
        //view model
        productTypeViewModel = new ViewModelProvider(this).get(ProductTypeViewModel.class);
        return productTypeViewModel.getAllProductType();
    }

    private MonthRevenue getMonth(List<MonthRevenue> monthRevenueList, String currentDate) {
        MonthRevenue temp = new MonthRevenue("", 0, 0);
        for (MonthRevenue monthRevenue : monthRevenueList) {
            if (monthRevenue.getCurrentDate().equals(currentDate)) {
                temp = monthRevenue;
            }
        }
        return temp;
    }

    private DayRevenue getDay(List<DayRevenue> dayRevenueList, String currentDate) {
        DayRevenue temp = new DayRevenue("", 0, 0);
        for (DayRevenue dayRevenue : dayRevenueList) {
            if (dayRevenue.getCurrentDate().equals(currentDate)) {
                temp = dayRevenue;
            }
        }
        return temp;
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == -1) {
                        Intent intent = result.getData();
                    }
                }
            }
    );
}
