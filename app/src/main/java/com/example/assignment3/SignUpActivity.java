package com.example.assignment3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SignUpActivity extends AppCompatActivity implements DatePickerFragment.DatePickerListener{

    private EditText fNameEt;
    private EditText sNameEt;
    private EditText emailEt;
    private EditText dobEt;
    private EditText heightEt;
    private EditText weightEt;
    private EditText addressEv;
    private EditText postcodeEv;
    private EditText stepsPerMileEv;
    private EditText usernameEv;
    private EditText passwordEv;
    private boolean usernameOrPasswordExist;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);


        fNameEt = (EditText) findViewById(R.id.fnEt);
        sNameEt = (EditText) findViewById(R.id.snEt);
        emailEt = (EditText) findViewById(R.id.emailEt);
        heightEt = (EditText) findViewById(R.id.heightEt);
        weightEt = (EditText) findViewById(R.id.weightEt);
        addressEv = (EditText) findViewById(R.id.addressEt);
        postcodeEv = (EditText) findViewById(R.id.postcodeEt);
        stepsPerMileEv = (EditText) findViewById(R.id.stepsPerMileEt);
        usernameEv = (EditText) findViewById(R.id.usernameEt);
        passwordEv = (EditText) findViewById(R.id.passwordEt);



        TextView login = (TextView)findViewById(R.id.lnkLogin);
        login.setMovementMethod(LinkMovementMethod.getInstance());
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        dobEt = (EditText) findViewById(R.id.dobEt);
        dobEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.setCancelable(false);
                datePickerFragment.show(getSupportFragmentManager(),"date Picker");
            }
        });

        final RadioGroup genderGp = (RadioGroup) findViewById(R.id.radioGrp);



        List<Integer> activityLvList = new ArrayList<Integer>();
        for (int i=1; i<6 ;i++ ){
            activityLvList.add(i);
        }
        final Spinner sActivityLv = (Spinner) findViewById(R.id.activityLv_spinner);
        final ArrayAdapter<Integer> spinnerAdapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item, activityLvList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sActivityLv.setAdapter(spinnerAdapter);


        Button btnRegister = (Button) findViewById(R.id.btn_Register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fNameInput = fNameEt.getText().toString().trim();
                String sNameInput = sNameEt.getText().toString().trim();
                String emailInput = emailEt.getText().toString().trim();
                String heightInput = heightEt.getText().toString().trim();
                String weightInput = weightEt.getText().toString().trim();
                String addressInput = addressEv.getText().toString().trim();
                String postcodeInput = postcodeEv.getText().toString().trim();
                String stepsPerMileInput = stepsPerMileEv.getText().toString().trim();
                String usernameInput = usernameEv.getText().toString().trim();
                String passwordInput = passwordEv.getText().toString().trim();
                String dobInput = dobEt.getText().toString().trim();
                //get gender from radio button
                int genderId = genderGp.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) findViewById(genderId);
                String genderSelected = radioButton.getText().toString();
                // get level of activity from spinner
                String activityLvSelected = sActivityLv.getSelectedItem().toString();
                if (validateNameInput(fNameInput,fNameEt) && validateNameInput(sNameInput,sNameEt) && emailInputValidation(emailInput) && heightInputValidation(heightInput) && weightInputValidation(weightInput) && validateAddress(addressInput) && validateStepsPerMileInput(stepsPerMileInput) && validateUsername(usernameInput) && validatePostcodeInput(postcodeInput) && validateNameInput(passwordInput,passwordEv) && validateDobInput(dobInput)){
                    ArrayList<String> registerInfo = new ArrayList<>();
                    registerInfo.add(fNameInput);
                    registerInfo.add(sNameInput);
                    registerInfo.add(emailInput);
                    registerInfo.add(dobInput);
                    registerInfo.add(heightInput);
                    registerInfo.add(weightInput);
                    registerInfo.add(genderSelected);
                    registerInfo.add(addressInput);
                    registerInfo.add(postcodeInput);
                    registerInfo.add(activityLvSelected);
                    registerInfo.add(stepsPerMileInput);
                    registerInfo.add(usernameInput);

                    String generatedPassword = null;
                    try{
                        // Create MessageDigest instance for MD5
                        MessageDigest md = MessageDigest.getInstance("MD5");
                        //Add password bytes to digest
                        md.update(passwordInput.getBytes());
                        //Get the hash's bytes
                        byte[] bytes = md.digest();
                        //This bytes[] has bytes in decimal format;
                        //Convert it to hexadecimal format
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i< bytes.length ;i++)
                        {
                            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
                        }
                        //Get complete hashed password in hex format
                        generatedPassword = sb.toString();

                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    registerInfo.add(generatedPassword);

                    CheckUsernameEmail checkUsernameEmail = new CheckUsernameEmail();
                    checkUsernameEmail.execute(registerInfo);
                }
            }
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR,year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = cal.getTime();
        String dateStr = dateFormat.format(date);
        dobEt.setText(dateStr);
    }



    public boolean validateNameInput(String inputStr,EditText editText){
        if ("".equals(inputStr) || inputStr == null){
            editText.setError("You should enter your this field!");
            return false;
        }else if(inputStr.length()> 20){
            editText.setError("Your input is too long!");
            return false;
        }
        else {
            return true;
        }
    }

    public boolean emailInputValidation(String email){
        if ("".equals(email )|| email ==null){
            emailEt.setError("You should input email!");
            return false;
        }else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEt.setError("Please input legal your email address!");
            return false;
        }
        else {
            return true;
        }
    }





    public boolean heightInputValidation(String heightStr){
        if ("".equals(heightStr) || heightStr ==null){
            heightEt.setError("You should input your height!");
            return false;
        }else{
            int height = Integer.parseInt(heightStr);
            if (height<50 || height >300){
                heightEt.setError("You height is out of range!");
                return false;
            }else {
                return true;
            }
        }
    }

    public boolean weightInputValidation(String weightStr){
        if ("".equals(weightStr) || weightStr ==null){
            heightEt.setError("You should input your weight!");
            return false;
        }else{
            int height = Integer.parseInt(weightStr);
            if (height<10 || height >1000){
                heightEt.setError("You weight is out of range!");
                return false;
            }else {
                return true;
            }
        }
    }

    public boolean validateAddress(String addressStr){
        if ("".equals(addressStr) || addressStr == null){
            addressEv.setError("You should enter your address!");
            return false;
        }else if(addressStr.length()> 50){
            addressEv.setError("Your input is too long!");
            return false;
        }
        else {
            return true;
        }
    }

    public boolean validateStepsPerMileInput(String stepsPerMileInput){
        if ("".equals(stepsPerMileInput) || stepsPerMileInput ==null){
            heightEt.setError("You should input your steps per mile!");
            return false;
        }else{
            int stepsPerMile = Integer.parseInt(stepsPerMileInput);
            if (stepsPerMile<100 || stepsPerMile >10000){
                heightEt.setError("You weight is out of range!");
                return false;
            }else {
                return true;
            }
        }
    }

    public boolean validateUsername(String username){
        if ("".equals(username) || username == null){
            usernameEv.setError("You should enter your address!");
            return false;
        }else if (username.length()> 50){
            usernameEv.setError("Your input is too long!");
            return false;
        } else {
            return true;
        }
    }

    public boolean validatePostcodeInput(String postcodeStr){
        if ("".equals(postcodeStr) || postcodeStr == null){
            postcodeEv.setError("You should enter your address!");
            return false;
        }else {
            if (postcodeStr.length()!=4){
                postcodeEv.setError("You should enter 4 digital postcode!");
                return false;
            }else {
                return true;
            }

        }
    }

    public boolean validateDobInput(String dobInput)  {
        if ("".equals(dobInput) || dobInput == null){
            dobEt.setError("You should enter your birthday!");
            return false;
        }else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dob = null;
            Date startDate = null;
            Date endDate = null;
            try {
                dob = dateFormat.parse(dobInput);
                startDate = dateFormat.parse("1900-01-01");
                endDate = dateFormat.parse("2001-4-29");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (dob.after(startDate) && dob.before(endDate)){
                return true;
            }else {
                dobEt.setError("You should older than 18!");
                return false;
            }
        }
    }

    private class CheckUsernameEmail extends AsyncTask<ArrayList<String>, Void, List>{
        @Override
        protected List doInBackground(ArrayList<String>... lists) {
            ArrayList<String> registerInfo = lists[0];
            String username = registerInfo.get(11);
            String email = registerInfo.get(2);
            String usernameResult = RestClient.findUserByUsername(username);
            String emailResult = RestClient.findUserByEmail(email);
            List<String> errorList = new ArrayList<>();
            if (!"[]".equals(usernameResult) || !"[]".equals(emailResult)){
                if (!"[]".equals(usernameResult)){
                    errorList.add("usernameErr");
                }
                if (!"[]".equals(emailResult)){
                    errorList.add("emailErr");
                }
            } else {
                RegisterAsyncTask registerAsyncTask = new RegisterAsyncTask();
                registerAsyncTask.execute(registerInfo);
            }
            return errorList;
        }

        @Override
        protected void onPostExecute(List errorList) {
            if (errorList.size()==0){
                return;
            }else if ("usernameErr".equals(errorList.get(0))){
                usernameEv.setError("username is already exist!");
                if (errorList.size()==2){
                    emailEt.setError("Email is already exist!");
                }
                return;
            }else if ("emailErr".equals(errorList.get(0))){
                emailEt.setError("Email is already exist!");
            }

        }
    }


    private class RegisterAsyncTask extends AsyncTask<ArrayList<String>, Void, Boolean> {
        @Override
        protected Boolean doInBackground(ArrayList<String>... lists) {
            ArrayList<String> registerInfo = lists[0];
            Date dob = null;

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                dob = dateFormat.parse(registerInfo.get(3));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Integer height = Integer.valueOf(registerInfo.get(4));
            BigDecimal weight =new BigDecimal(registerInfo.get(5));
            Short levelOfActivity = Short.valueOf(registerInfo.get(9));
            Integer stepsPerMile = Integer.valueOf(registerInfo.get(10));
            Users user = new Users(registerInfo.get(0),registerInfo.get(1),registerInfo.get(2),dob,height,weight,registerInfo.get(6),registerInfo.get(7),registerInfo.get(8),levelOfActivity,stepsPerMile);
            RestClient.creatUser(user,registerInfo.get(11),registerInfo.get(12));
            return true;  //username
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success){
                Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                startActivity(intent);
            }

        }
    }


}
