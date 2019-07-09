package com.example.assignment3;

import android.app.Fragment;
import android.app.FragmentManager;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

public class UpdateStepsFragment extends Fragment {
    View vUpdateSteps;
    EditText updateStepsEt;
    Button updateStepsBtn;
    ReportDatabase reportDB;
    int userid;
    int position;
    Report currentReport;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        vUpdateSteps = inflater.inflate(R.layout.fragment_update_steps, container, false);

        return vUpdateSteps;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        SharedPreferences spPosition = getActivity().getSharedPreferences("stepsPosition", Context.MODE_PRIVATE);
        userid = (int) spPosition.getInt("userid",0);
        position = (int) spPosition.getInt("position",0);

        reportDB = Room.databaseBuilder(getActivity(),ReportDatabase.class,"ReportDatabase").fallbackToDestructiveMigration().build();

        updateStepsEt = vUpdateSteps.findViewById(R.id.etUpdateSteps);
        updateStepsBtn = vUpdateSteps.findViewById(R.id.btn_updateSteps);

        SetStepsAsyncTask setStepsAsyncTask = new SetStepsAsyncTask();
        setStepsAsyncTask.execute();


        updateStepsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newSteps = updateStepsEt.getText().toString();
                UpdateStepsAsyncTask updateStepsAsyncTask = new UpdateStepsAsyncTask();
                updateStepsAsyncTask.execute(newSteps);
            }
        });


    }

    private class SetStepsAsyncTask extends AsyncTask<Void,Void,Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {
            List<Report> reports = reportDB.reportDao().getAllByUserid(userid);
            Report report = reports.get(position);
            currentReport = report;
            int steps;
            if (report.getStepsTaken() == null){
                steps = 0;
            }else {
                steps = report.getStepsTaken();
            }
//            System.out.println(report.getId());
//            System.out.println(steps);
            return steps;
        }

        @Override
        protected void onPostExecute(Integer steps) {
            updateStepsEt.setText(String.valueOf(steps));
        }
    }

    private class UpdateStepsAsyncTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {
            String newStepsStr = strings[0];
            String feedback = "";
            if ("".equals(newStepsStr)){
                feedback = "You should input a number!";
            }else {
                int newSteps = Integer.parseInt(newStepsStr);
                if (newSteps>0 && newSteps < 1000000){
                    currentReport.setStepsTaken(newSteps);
                    reportDB.reportDao().updateReport(currentReport);
                }else {
                    feedback = "You should input a number between 1 to 1000000";
                }
            }
            return feedback;
        }

        @Override
        protected void onPostExecute(String feedback) {
            if (!("".equals(feedback))){
                updateStepsEt.setError(feedback);
            }else {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, new DisplayStepsFragment()).commit();
            }
        }
    }





}
