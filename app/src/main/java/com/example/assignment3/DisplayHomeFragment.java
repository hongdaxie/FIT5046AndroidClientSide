package com.example.assignment3;

import android.app.Fragment;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DisplayHomeFragment extends Fragment implements View.OnClickListener{
    View vMain;
    TextView welcomeTv;
    Button btSetGoal;
    EditText setGoalEv;
    Integer userid;
    TextView feedbackTv;
    ReportDatabase reportDB;
    TextView tvUserReport;
    Button btnPostReport;

    Integer totalSteps;
    Integer calorieGoal;
    Integer dailyCaloriesBurned;
    Integer totalCaloriesConsumed;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vMain = inflater.inflate(R.layout.fragment_main, container, false);

        SharedPreferences spMyusername = getActivity().getSharedPreferences("names", Context.MODE_PRIVATE);
        String fname = spMyusername.getString("fname","DEFAULT");
        String useridStr = spMyusername.getString("userid","DEFAULT");
        userid = Integer.parseInt(useridStr);
        welcomeTv = (TextView) vMain.findViewById(R.id.welcomeTv);
        welcomeTv.setText("Welcome " + fname + " !");
        btSetGoal = vMain.findViewById(R.id.setGoalBt);
        setGoalEv = vMain.findViewById(R.id.goalSetEV);
        feedbackTv = vMain.findViewById(R.id.feedbackTv);
        reportDB = Room.databaseBuilder(getActivity(),ReportDatabase.class,"ReportDatabase").fallbackToDestructiveMigration().build();

        btSetGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String goalStr = setGoalEv.getText().toString().trim();
//                SetGoalAsyncTask setGoalAsyncTask = new SetGoalAsyncTask();
//                setGoalAsyncTask.execute(goal);
//                Integer goal = Integer.parseInt(goalStr);
                SetGoal setGoal = new SetGoal();
                setGoal.execute(goalStr);

            }
        });


        btnPostReport = vMain.findViewById(R.id.btn_post);
        btnPostReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostReportAsyncTask postReportAsyncTask = new PostReportAsyncTask();
                postReportAsyncTask.execute();
            }
        });


        return vMain;
    }

    @Override
    public void onClick(View v) {
    }

    private class SetGoal extends AsyncTask<String , Void,String>{
        @Override
        protected String doInBackground(String... goals) {
            String feedback = "";
            String goalStr = goals[0];
            if ("".equals(goalStr)){
                feedback = "You should input your goal!";
            }else {
                Integer goal = Integer.parseInt(goalStr);
                List<Report> reports = reportDB.reportDao().getAllByUserid(userid);
                if (reports.isEmpty() || reports == null){
                    //  System.out.println(reports.get(0).getCalorieGoal());
                    Report report = new Report();
                    report.setCalorieGoal(goal);
                    report.setUserId(userid);
                    reportDB.reportDao().insert(report);
                    feedback = "You have set the goal to " + goals[0];
                }
                else{
//                System.out.println("--- "+reports.get(0).getCalorieGoal());
                    for (Report report:reports){
                        report.setCalorieGoal(goal);
                        reportDB.reportDao().updateReport(report);
                    }
                    feedback = "You have updated the goal to " + goals[0];
                }
            }
            return feedback;

        }

        @Override
        protected void onPostExecute(String feedback) {
            if ("You should input your goal!".equals(feedback)){
                setGoalEv.setError(feedback);
            }
            feedbackTv.setText(feedback);
            GetTotalStepsAsyncTask getTotalStepsAsyncTask = new GetTotalStepsAsyncTask();
            getTotalStepsAsyncTask.execute();
        }
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        tvUserReport = vMain.findViewById(R.id.userReportTv);
        GetTotalStepsAsyncTask getTotalStepsAsyncTask = new GetTotalStepsAsyncTask();
        getTotalStepsAsyncTask.execute();
    }

    private class GetTotalStepsAsyncTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            List<Report> reports = reportDB.reportDao().getAllByUserid(userid);
            totalSteps = 0;

            if (reports != null && reports.size() >0 ){
                for (Report report:reports){
                    if (report.getStepsTaken() != null){
                        totalSteps += report.getStepsTaken();
                    }
                    if (report.getCalorieGoal() != null){
                        calorieGoal = report.getCalorieGoal();
                    }
                }
            }


            GenerateReportAsyncTask generateReportAsyncTask = new GenerateReportAsyncTask();
            generateReportAsyncTask.execute();

            return null;
        }
    }

    private class GenerateReportAsyncTask extends AsyncTask<Void,Void,ArrayList<Integer>>{
        double totalCaloriesBurned;
        @Override
        protected ArrayList<Integer> doInBackground(Void... Void) {
            String totalCaloriesConsumedResult = RestClient.findTotalCaloriesConsumed(userid);
            totalCaloriesConsumed = RestClient.getTotalCaloriesConsumed(totalCaloriesConsumedResult);
            String dailyCaloriesBurnedResult = RestClient.findDailyCaloriesBurned(userid);
            dailyCaloriesBurned  =RestClient.getDailyCaloriesBurned(dailyCaloriesBurnedResult);
            String caloriesBurnedPerStepResult = RestClient.findCaloriesBurnedPerStep(userid);
            double caloriesBurnedPerStep = RestClient.getCaloriesBurnedPerStep(caloriesBurnedPerStepResult);
            double caloriesBurnedBySteps = caloriesBurnedPerStep * totalSteps;
            totalCaloriesBurned = caloriesBurnedBySteps + dailyCaloriesBurned;



            ArrayList<Integer> reportInfo = new ArrayList<>();
            reportInfo.add(totalSteps);
            if (calorieGoal == null){
                calorieGoal = 0;
            }
            reportInfo.add(calorieGoal);
            reportInfo.add(totalCaloriesConsumed);
            return reportInfo;
        }

        @Override
        protected void onPostExecute(ArrayList<Integer> reportInfo) {
            tvUserReport.setText("Calorie goal today is : " + reportInfo.get(1) + "\n"
                                + "Total steps taken is : " + reportInfo.get(0) + "\n"
                                + "Total calories consumed is : " + reportInfo.get(2) + "\n"
                                + "Total calories burned is : " + Math.round(totalCaloriesBurned * 100.0) / 100.0
                    );
        }
    }


    public class PostReportAsyncTask extends AsyncTask<Void ,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            com.example.assignment3.ReportClassJavaDB.Report report = new com.example.assignment3.ReportClassJavaDB.Report();
            Users user = new Users();
            user.setId(userid);
            report.setUserId(user);
            report.setCalorieGoal(calorieGoal);
            report.setTotalCaloriesBurned(dailyCaloriesBurned);
            report.setTotalCaloriesConsumed(totalCaloriesConsumed);
            report.setTotalStepsTaken(totalSteps);
            report.setDate(new Date());
            RestClient.createReport(report);
            reportDB.reportDao().delete(userid);
            totalSteps = 0;
            totalCaloriesConsumed = 0;
            calorieGoal = 0;

            GenerateReportAsyncTask generateReportAsyncTask = new GenerateReportAsyncTask();
            generateReportAsyncTask.execute();
            return null;
        }
    }

//
//
//    public class InsertDatabase extends AsyncTask<String,Void,Void>{
//        @Override
//        protected Void doInBackground(String... strings) {
//
//        }
//    }



//    private class SetGoalAsyncTask extends AsyncTask<String ,Void, String>{
//        @Override
//        protected String doInBackground(String... strings) {
//            Integer goal = Integer.parseInt(strings[0]);
//            Date date = new Date();
//            Integer userId = Integer.parseInt(userid);
//
//            String result = RestClient.findReportByUseridAndDate(userId);
//
//            String feedback = "";
//
//            Users user = new Users();
//            user.setId(userId);
//            Report report = new Report();
//            report.setDate(date);
//            report.setCalorieGoal(goal);
//            report.setUserId(user);
//
//
//            //insert
//            if ("[]".equals(result)){
//
//                RestClient.setGoal(report);
//                feedback = "You daily goal has been set to " + setGoalEv.getText().toString();
//            }else { //update
//                ArrayList<Integer> idAndGoal =  RestClient.getReportidAndGoal(result);
//                Integer previousGoal = idAndGoal.get(1);
//                Integer id = idAndGoal.get(0);
//                report.setId(id);
//                RestClient.updateGoal(id,report);
//                feedback = "You daily goal was "+ String.valueOf(previousGoal) + ",and it has been updated to" + String.valueOf(goal);
//            }
//            return feedback;
//        }
//
//        @Override
//        protected void onPostExecute(String feedback) {
//            feedbackTv.setText(feedback);
//        }
//    }
}
