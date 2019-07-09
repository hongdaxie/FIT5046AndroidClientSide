package com.example.assignment3;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    EditText etUsername;
    EditText etPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView register = (TextView)findViewById(R.id.lnkSignup);
        register.setMovementMethod(LinkMovementMethod.getInstance());
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });

        etUsername = (EditText) findViewById(R.id.input_username);
        etPassword = (EditText) findViewById(R.id.input_password);
        Button btnLogin = (Button) findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                if (usernameInputValidation(username) && passwordInputValidation(password)){
                    String[] loginInform = new String[2];
                    loginInform[0] = username;
                    loginInform[1] = password;
                    LoginAsyncTask loginAsyncTask = new LoginAsyncTask();
                    loginAsyncTask.execute(loginInform);
                }
            }
        });

    }

    private class LoginAsyncTask extends AsyncTask<String, Void, Boolean>{
        @Override
        protected Boolean doInBackground(String... strings) {

            String result = RestClient.findUserByUsername(strings[0]);
            String passwordInput = strings[1];
            String passwordInDB = RestClient.getPassword(result);
            Boolean success = false;
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

            if (generatedPassword.equals(passwordInDB)){
                success = true;
            }
            return success;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success){
                Log.i("Signin","Login successful");
                String username = etUsername.getText().toString().trim();
                GetInfoAsyncTask getInfoAsyncTask= new GetInfoAsyncTask();
                getInfoAsyncTask.execute(username);


            }else {
                etPassword.setError("Username or password error!");
            }
        }
    }


    private class GetInfoAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String userResult = RestClient.findUserByUsername(strings[0]);
            ArrayList<String> result =  RestClient.getUsersNameAndId(userResult);
            String id;
            String firstName;
            if (result.size()==0){
                 id = RestClient.getUserId(userResult).get(0);
                 Integer idInt = Integer.parseInt(id);
                 String result1 = RestClient.findUserById(idInt);
                 firstName = RestClient.getUserFname(result1);
            }else {
                id = result.get(0);
                firstName= result.get(1);
            }
            Intent intent = new Intent(MainActivity.this,HomeActivity.class);
            intent.putExtra("userid",id);
            intent.putExtra("username",strings[0]);
            intent.putExtra("fname",firstName);
//            intent.putExtra("id",id);
            startActivity(intent);
            return null;
        }
    }




    public boolean usernameInputValidation(String username){
        if ("".equals(username )|| username ==null){
            etUsername.setError("You should input username!");
            return false;
        }
        else {
            return true;
        }
    }

    public boolean passwordInputValidation(String password){
        if ("".equals(password) || password == null){
            etPassword.setError("You should enter your password!");
            return false;
        }else {
            return true;
        }
    }



}
