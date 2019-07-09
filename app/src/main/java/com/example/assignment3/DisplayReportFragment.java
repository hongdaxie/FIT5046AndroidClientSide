package com.example.assignment3;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DisplayReportFragment  extends Fragment{
    View vDisplayReport;
    Button btnSelectDate;
    Integer userid;
    String reportName[] = {"CaloriesBurned","CaloriesConsumed","remainingCalorie"};

    BarChart barChart;

    Button btnStartDate;
    Button btnEndDate;

    String startDateStr;
    String endDateStr;
    TextView tvSelectDateFeedback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        vDisplayReport = inflater.inflate(R.layout.fragment_report, container, false);

        SharedPreferences spMyusername = getActivity().getSharedPreferences("names", Context.MODE_PRIVATE);
        String useridStr = spMyusername.getString("userid","DEFAULT");
        userid = Integer.parseInt(useridStr);

        btnStartDate = (Button) vDisplayReport.findViewById(R.id.selectStartDate_btn);
        tvSelectDateFeedback = (TextView) vDisplayReport.findViewById(R.id.selectDateFeedbackTv);
        barChart = vDisplayReport.findViewById(R.id.barChart);

        btnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                startDateStr = year + "-" + (month +1) + "-" + day;

                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });


        btnEndDate = (Button) vDisplayReport.findViewById(R.id.selectEndDate_btn);

        btnEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                endDateStr = year + "-" + (month +1) + "-" + day;

                                GetReportPeriodAsyncTask getReportPeriodAsyncTask = new GetReportPeriodAsyncTask();
                                getReportPeriodAsyncTask.execute();


                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });



        btnSelectDate = (Button) vDisplayReport.findViewById(R.id.selectDate_btn);

        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                                String dateStr = year + "-" + (month +1) + "-" + day;
                                GetReportAsyncTask getReportAsyncTask = new GetReportAsyncTask();
                                getReportAsyncTask.execute(dateStr);
                                System.out.println(year + "-" + (month +1) + "-" + day);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });
        return vDisplayReport;
    }

    private class GetReportAsyncTask extends AsyncTask<String,Void,Void>{
        @Override
        protected Void doInBackground(String... strings) {
            String textResult = RestClient.findReportByDate(userid,strings[0]);
            ArrayList<Integer> pieData = RestClient.getReportInfo(textResult);
            String caloriesBurnedPerStepResult = RestClient.findCaloriesBurnedPerStep(userid);
            double caloriesBurnedPerStep = RestClient.getCaloriesBurnedPerStep(caloriesBurnedPerStepResult);
            String totalStepsResult = RestClient.findStepsByDate(userid,strings[0]);
            Integer totalSteps = RestClient.getSteps(totalStepsResult);
            Integer caloriesBurnedBySteps = (int) Math.round(caloriesBurnedPerStep * totalSteps);
            Integer totalCaloriesBurned = caloriesBurnedBySteps + pieData.get(0);
            pieData.set(0,totalCaloriesBurned);
            Integer remaining = pieData.get(2)+ caloriesBurnedBySteps;
            pieData.set(2,remaining);
            setupPieChart(pieData);
            return null;
        }
    }

    private class GetReportPeriodAsyncTask extends AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void... voids) {

            String feedback = "";
            if (startDateStr!=null && (!"".equals(startDateStr))){
                feedback = "";
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                Date startDate = null;
                Date endDate = null;
                try {
                    startDate = sdf.parse(startDateStr);
                    endDate = sdf.parse(endDateStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar cal1 = Calendar.getInstance();
                cal1.setTime(startDate);
                Calendar cal2 = Calendar.getInstance();
                cal2.setTime(endDate);

                if (cal1.before(cal2)){
                    feedback = "";
                    String reportsResult = RestClient.findReportByPeriod(userid,startDateStr,endDateStr);
                    ArrayList<Report> reports = RestClient.getReportsPeriod(reportsResult);
                    String caloriesBurnedPerStepResult = RestClient.findCaloriesBurnedPerStep(userid);
                    double caloriesBurnedPerStep = RestClient.getCaloriesBurnedPerStep(caloriesBurnedPerStepResult);

                    ArrayList<Integer> barDateCaloriesBurned = new ArrayList<>();
                    ArrayList<Integer> barDateCaloriesConsumed = new ArrayList<>();
//            Integer caloriesBurnedBySteps = (int) Math.round(caloriesBurnedPerStep * totalSteps);
                    for (Report report : reports){
                        Integer caloriesBurnedBySteps = (int) Math.round(caloriesBurnedPerStep * report.getStepsTaken());
                        Integer caloriesBurned = caloriesBurnedBySteps + report.getTotalCaloriesBurned();
                        Integer caloriesConsumed = report.getTotalCaloriesConsumed();
                        barDateCaloriesBurned.add(caloriesBurned);
                        barDateCaloriesConsumed.add(caloriesConsumed);
                    }
                    setupBarChart(barDateCaloriesBurned,barDateCaloriesConsumed);
                }else {
                    feedback = "Your start date should be before end date!";
                }
            }else {
                feedback = "Please select start date first!";
            }

            return feedback;
        }

        @Override
        protected void onPostExecute(String feedback) {
            tvSelectDateFeedback.setText(feedback);
        }
    }

    private void setupPieChart(ArrayList<Integer> pieData){
        List<PieEntry> pieEntries = new ArrayList<>();
        for (int i = 0; i < 3; i ++){
            pieEntries.add(new PieEntry(pieData.get(i), reportName[i]));
        }
        PieDataSet dataSet = new PieDataSet(pieEntries,"Report");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData date = new PieData(dataSet);

        //get the chart
        PieChart chart = (PieChart) vDisplayReport.findViewById(R.id.pieChart);
        chart.setData(date);
        chart.invalidate();
    }

    private void setupBarChart(ArrayList<Integer> barDateCaloriesBurned, ArrayList<Integer> barDateCaloriesConsumed){
        ArrayList<BarEntry> barEntries1 = new ArrayList<>();
        ArrayList<BarEntry> barEntries2 = new ArrayList<>();
//        barEntries1.add(new BarEntry((0),0));
//        barEntries2.add(new BarEntry((0),0));


        for (int i =0 ; i < barDateCaloriesBurned.size(); i++){
            barEntries1.add(new BarEntry((i+1),barDateCaloriesBurned.get(i)));
            barEntries2.add(new BarEntry((i+1),barDateCaloriesConsumed.get(i)));
        }

        BarDataSet barDataSet1 = new BarDataSet(barEntries1,"CaloriesBurned");
        barDataSet1.setColor(Color.RED);
        barDataSet1.setDrawValues(true);

        BarDataSet barDataSet2 = new BarDataSet(barEntries2,"CaloriesConsumed");
        barDataSet2.setColor(Color.BLUE);
        barDataSet2.setDrawValues(true);

        BarData data = new BarData(barDataSet1,barDataSet2);
        barChart.setData(data);

        barChart.setDragEnabled(true);
        barChart.setVisibleXRangeMaximum(3);

        float barSpace = 0.1f;
        float groupSpace= 0.5f;

        data.setBarWidth(0.15f);
        barChart.groupBars(0.25f,groupSpace,barSpace);
        barChart.invalidate();

    }



}
