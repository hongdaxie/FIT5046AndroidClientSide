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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DisplayStepsFragment extends Fragment {
    View vDisplaySteps;
    List<HashMap<String , String >> stepsListArray;
    SimpleAdapter myListAdapter;
    ListView stepsList;

    HashMap<String , String > stepsMap = new HashMap<String , String>();
    String[] colHead = new String[]{"DATE","STEPS"};
    int[] dataCell = new int[]{R.id.date, R.id.steps};
    Integer userid;
    EditText setStepsEt;
    Button setStepsBtn;
    ReportDatabase reportDB;
    String currentDateStr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        vDisplaySteps = inflater.inflate(R.layout.fragment_steps, container, false);

        return vDisplaySteps;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        SharedPreferences spMyusername = getActivity().getSharedPreferences("names", Context.MODE_PRIVATE);
        String useridStr = spMyusername.getString("userid","DEFAULT");
        userid = Integer.parseInt(useridStr);


        stepsList = vDisplaySteps.findViewById(R.id.list_view);


        stepsListArray = new ArrayList<HashMap<String, String>>();
//        stepsMap.put("DATE",currentDateStr);
//        stepsMap.put("STEPS","1234");
//        stepsListArray.add(stepsMap);

        myListAdapter= new SimpleAdapter(getActivity(),stepsListArray,R.layout.list_view,colHead,dataCell);
        stepsList.setAdapter(myListAdapter);

        setStepsEt = vDisplaySteps.findViewById(R.id.etSetSteps);
        setStepsBtn = vDisplaySteps.findViewById(R.id.btn_setSteps);

        reportDB = Room.databaseBuilder(getActivity(),ReportDatabase.class,"ReportDatabase").fallbackToDestructiveMigration().build();



        GetStepsAsyncTask getStepsAsyncTask = new GetStepsAsyncTask();
        getStepsAsyncTask.execute();

        setStepsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newSteps = setStepsEt.getText().toString();
                SetStepsAsyncTask setStepsAsyncTask = new SetStepsAsyncTask();
                setStepsAsyncTask.execute(newSteps);
            }
        });

        Button deleteStepsBtn = vDisplaySteps.findViewById(R.id.btn_delete);
        deleteStepsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteStepsAsyncTask deleteStepsAsyncTask = new DeleteStepsAsyncTask();
                deleteStepsAsyncTask.execute();
            }
        });


        stepsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                System.out.println(position);
                SharedPreferences spPosition = getActivity().getSharedPreferences("stepsPosition",Context.MODE_PRIVATE);
                SharedPreferences.Editor ePosition = spPosition.edit();
                ePosition.putInt("position",position);
                ePosition.putInt("userid",userid);
                ePosition.apply();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, new UpdateStepsFragment()).commit();

            }
        });

    }


    public class GetStepsAsyncTask extends AsyncTask<Void,Void,List<HashMap<String, String>>>{
        @Override
        protected List<HashMap<String, String>> doInBackground(Void... voids) {
            List<Report> reports = reportDB.reportDao().getAllByUserid(userid);
            List<HashMap<String, String>> mapList = new ArrayList<HashMap<String, String>>();
            if (reports.size()>0 && reports != null){
                for (Report report:reports){
                    int steps;
                    String timeStr;
                    if (report.getStepsTaken() == null){
                        steps = 0;
                    }else {
                        steps = report.getStepsTaken();
                    }
                    HashMap<String, String> map = new HashMap<String, String>();
                    if (report.getTime() == null){
                        timeStr = "00:00:00";
                    }else {
                        timeStr = report.getTime();
                    }
                    map.put("DATE",timeStr);
                    map.put("STEPS",String.valueOf(steps));
//                    System.out.println(report.getCalorieGoal());
                    mapList.add(map);
                }
            }

            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> mapList) {
            if (mapList!=null && mapList.size()>0){
                stepsListArray = mapList;
                myListAdapter = new SimpleAdapter(getActivity(),stepsListArray,R.layout.list_view,colHead,dataCell);
                stepsList.setAdapter(myListAdapter);
            }
        }
    }

    private class SetStepsAsyncTask extends AsyncTask<String ,Void,HashMap<String, String>>{
        @Override
        protected HashMap<String, String> doInBackground(String... strings) {
            String newStepsStr = strings[0];
            if ("".equals(newStepsStr)){
                return null;
            }else {
                int newSteps = Integer.parseInt(newStepsStr);
                if (newSteps<1000000 && newSteps>0){
                    DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
                    String timeStr = dateFormat.format(new Date());
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("DATE",timeStr);
                    map.put("STEPS",String.valueOf(newSteps));
                    Report report = new Report();
                    report.setUserId(userid);
                    report.setStepsTaken(newSteps);

                    report.setTime(timeStr);
                    reportDB.reportDao().insert(report);
                    return map;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(HashMap<String, String> map) {
            if (map != null){
                stepsListArray.add(map);
                myListAdapter = new SimpleAdapter(getActivity(),stepsListArray,R.layout.list_view,colHead,dataCell);
                stepsList.setAdapter(myListAdapter);
            }else {
                setStepsEt.setError("You should eneter a number between 1 to 1000000!");
            }
        }
    }

    public class DeleteStepsAsyncTask extends AsyncTask<Void ,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            reportDB.reportDao().delete(userid);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            stepsListArray = new ArrayList<HashMap<String, String>>();
            myListAdapter = new SimpleAdapter(getActivity(),stepsListArray,R.layout.list_view,colHead,dataCell);
            stepsList.setAdapter(myListAdapter);
        }
    }


}
