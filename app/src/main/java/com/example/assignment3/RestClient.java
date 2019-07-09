package com.example.assignment3;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class RestClient {
    private static final String BASE_URL = "http://10.0.2.2:8080/FIT5046Assignment1/webresources/";

    private static final String GOOGLE_API_KEY = "YOUR_KEY";
    private static final String GOOGLE_SEARCH_ID_cx = "YOUR_SEARCH_ID";


    public static String findUserByUsername(String username){
        final String methodPath = "hongdarestsw.credential/findByUsername/" + username;
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        //Making HTTP request
        try{
            url = new URL(BASE_URL + methodPath);
            //open the connection
            conn = (HttpURLConnection)url.openConnection();
            //set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            //set the connection method to GET
            conn.setRequestMethod("GET");
            //add http headers to set your response type to json
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            //Read the response
            Scanner inStream = new Scanner(conn.getInputStream());
            //read the input steream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            conn.disconnect();
        }
        return textResult;
    }

    public static String getPassword(String result){
        String password = null;
        try {
            JSONArray jsonArray = new JSONArray(result);
            if (jsonArray != null && jsonArray.length()>0){
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                password = jsonObject.getString("password");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return password;
    }

    public static ArrayList<String> getUsersNameAndId(String result){
        ArrayList<String> results = new ArrayList<String>();
        try {
            JSONArray jsonArray = new JSONArray(result);
            if (jsonArray != null && jsonArray.length()>0){
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                JSONObject users =  jsonObject.getJSONObject("users");
                String id = users.getString("id");
                String name = users.getString("name");
                results.add(id);
                results.add(name);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    public static ArrayList<String> getUserId(String result){
        ArrayList<String> results = new ArrayList<String>();
        try {
            JSONArray jsonArray = new JSONArray(result);
            if (jsonArray != null && jsonArray.length()>0){
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String id = jsonObject.getString("userId");
                results.add(id);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    public static String getUserFname(String result){
        String name = "";
        try {
            JSONObject jsonObject = new JSONObject(result);
            name = jsonObject.getString("name");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

    public static String findUserByEmail(String email){
        final String methodPath = "hongdarestsw.users/findByEmail/" + email;
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        //Making HTTP request
        try{
            url = new URL(BASE_URL + methodPath);
            //open the connection
            conn = (HttpURLConnection)url.openConnection();
            //set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            //set the connection method to GET
            conn.setRequestMethod("GET");
            //add http headers to set your response type to json
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            //Read the response
            Scanner inStream = new Scanner(conn.getInputStream());
            //read the input steream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            conn.disconnect();
        }
        return textResult;
    }

    public static void creatUser(Users user,String username, String password){
        //initialise
        URL url = null;
        HttpURLConnection conn = null;
        final String methodPath="hongdarestsw.users/" + username +"/" + password;
        try {
            Gson gson =new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").create();
            String stringUsersJson=gson.toJson(user);
            url = new URL(BASE_URL + methodPath);
            //open the connection
            conn = (HttpURLConnection) url.openConnection();
            //set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            //set the connection method to POST
            conn.setRequestMethod("POST");
            //set the output to true
            conn.setDoOutput(true);
            //set length of the data you want to send
            conn.setFixedLengthStreamingMode(stringUsersJson.getBytes().length);
            //add HTTP headers
            conn.setRequestProperty("Content-Type", "application/json");
            //Send the POST out
            PrintWriter out= new PrintWriter(conn.getOutputStream());
            out.print(stringUsersJson);
            out.close();
            Log.i("error",new Integer(conn.getResponseCode()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
    }

    public static void setGoal(Report report){
        //initialise
        URL url = null;
        HttpURLConnection conn = null;
        final String methodPath="hongdarestsw.report/";
        try {
            Gson gson =new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").create();
            String stringUsersJson=gson.toJson(report);
            url = new URL(BASE_URL + methodPath);
            //open the connection
            conn = (HttpURLConnection) url.openConnection();
            //set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            //set the connection method to POST
            conn.setRequestMethod("POST");
            //set the output to true
            conn.setDoOutput(true);
            //set length of the data you want to send
            conn.setFixedLengthStreamingMode(stringUsersJson.getBytes().length);
            //add HTTP headers
            conn.setRequestProperty("Content-Type", "application/json");
            //Send the POST out
            PrintWriter out= new PrintWriter(conn.getOutputStream());
            out.print(stringUsersJson);
            out.close();
            Log.i("error",new Integer(conn.getResponseCode()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
    }

    public static String findReportByUseridAndDate(Integer userid){
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String dateStr = simpleDateFormat.format(new Date());
        final String methodPath = "hongdarestsw.report/findByUseridAndDate/" + userid + "/" + dateStr;
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        //Making HTTP request
        try{
            url = new URL(BASE_URL + methodPath);
            //open the connection
            conn = (HttpURLConnection)url.openConnection();
            //set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            //set the connection method to GET
            conn.setRequestMethod("GET");
            //add http headers to set your response type to json
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            //Read the response
            Scanner inStream = new Scanner(conn.getInputStream());
            //read the input steream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            conn.disconnect();
        }
        return textResult;
    }

    public static ArrayList<Integer> getReportidAndGoal(String result){
        ArrayList<Integer> results = new ArrayList<Integer>();
        try {
            JSONArray jsonArray = new JSONArray(result);
            if (jsonArray != null && jsonArray.length()>0){
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                Integer id = jsonObject.getInt("id");
                Integer calorieGoal = jsonObject.getInt("calorieGoal");
                results.add(id);
                results.add(calorieGoal);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    public static void updateGoal(Integer reportid,Report report){
        //initialise
        URL url = null;
        HttpURLConnection conn = null;
        final String methodPath="hongdarestsw.report/" + reportid;
        try {
            Gson gson =new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").create();
            String stringUsersJson=gson.toJson(report);
            url = new URL(BASE_URL + methodPath);
            //open the connection
            conn = (HttpURLConnection) url.openConnection();
            //set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            //set the connection method to POST
            conn.setRequestMethod("PUT");
            //set the output to true
            conn.setDoOutput(true);
            //set length of the data you want to send
            conn.setFixedLengthStreamingMode(stringUsersJson.getBytes().length);
            //add HTTP headers
            conn.setRequestProperty("Content-Type", "application/json");
            //Send the POST out
            PrintWriter out= new PrintWriter(conn.getOutputStream());
            out.print(stringUsersJson);
            out.close();
            Log.i("error",new Integer(conn.getResponseCode()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
    }

    public static String findItemsByCategory(String category){
        final String methodPath = "hongdarestsw.food/findByCategory/" + category;
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        //Making HTTP request
        try{
            url = new URL(BASE_URL + methodPath);
            //open the connection
            conn = (HttpURLConnection)url.openConnection();
            //set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            //set the connection method to GET
            conn.setRequestMethod("GET");
            //add http headers to set your response type to json
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            //Read the response
            Scanner inStream = new Scanner(conn.getInputStream());
            //read the input steream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            conn.disconnect();
        }
        return textResult;
    }

    public static ArrayList<Food> getItems(String result){
        ArrayList<Food> items = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(result);
            if (jsonArray!=null && jsonArray.length()>0){
                for (int i =0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String itemName = jsonObject.getString("name");
                    int itemId = jsonObject.getInt("id");
                    Food food = new Food();
                    food.setId(itemId);
                    food.setName(itemName);
                    items.add(food);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    public static String googleSearch(String keyword,String[] params, String[] values){
        keyword = keyword.replace(" ", "+");
        URL url = null;
        HttpURLConnection connection = null;
        String textResult = "";
        String query_parameter = "";

        if (params!=null && values!=null){
            for (int i=0; i<params.length;i++){
                query_parameter += "&";
                query_parameter += params[i];
                query_parameter += "=";
                query_parameter += values[i];
            }
        }
        try{
            url = new URL("https://www.googleapis.com/customsearch/v1?key="+ GOOGLE_API_KEY+ "&cx="+ GOOGLE_SEARCH_ID_cx + "&q="+ keyword + query_parameter);
            connection = (HttpURLConnection)url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            Scanner scanner = new Scanner(connection.getInputStream());
            while (scanner.hasNextLine()) {
                textResult += scanner.nextLine();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally{
            connection.disconnect();
        }
        return textResult;
    }

    public static String getSnippet(String result){
        String snippet = null;
        try{
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("items");

            if (jsonArray != null && jsonArray.length()>0){
                snippet = jsonArray.getJSONObject(0).getString("snippet");
            }

        } catch (Exception e) {
            e.printStackTrace();
            snippet = "NO INFO FOUND";
        }
        return snippet;
    }

    public static String getImageSrc(String result){
        String imageSrc = null;
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("items");
            if (jsonArray != null && jsonArray.length()>0){
                imageSrc = jsonArray.getJSONObject(0).getString("link");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return imageSrc;
    }

    public static void addConsumption(Consumption consumption){
        //initialise
        URL url = null;
        HttpURLConnection conn = null;
        final String methodPath="hongdarestsw.consumption/";
        try {
            Gson gson =new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").create();
            String stringConsumptionJson=gson.toJson(consumption);
            url = new URL(BASE_URL + methodPath);
            //open the connection
            conn = (HttpURLConnection) url.openConnection();
            //set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            //set the connection method to POST
            conn.setRequestMethod("POST");
            //set the output to true
            conn.setDoOutput(true);
            //set length of the data you want to send
            conn.setFixedLengthStreamingMode(stringConsumptionJson.getBytes().length);
            //add HTTP headers
            conn.setRequestProperty("Content-Type", "application/json");
            //Send the POST out
            PrintWriter out= new PrintWriter(conn.getOutputStream());
            out.print(stringConsumptionJson);
            out.close();
            Log.i("error",new Integer(conn.getResponseCode()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
    }

    public static String findFoodByName(String name){
        final String methodPath = "hongdarestsw.food/findByName/" + name;
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        //Making HTTP request
        try{
            url = new URL(BASE_URL + methodPath);
            //open the connection
            conn = (HttpURLConnection)url.openConnection();
            //set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            //set the connection method to GET
            conn.setRequestMethod("GET");
            //add http headers to set your response type to json
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            //Read the response
            Scanner inStream = new Scanner(conn.getInputStream());
            //read the input steream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            conn.disconnect();
        }
        return textResult;
    }

    public static String findFoodFromAPI(String name){
        name = name.replace(" ", "+");
        URL url = null;
        HttpURLConnection connection = null;
        String textResult = "";
        try{
            url = new URL("https://api.edamam.com/api/food-database/parser?ingr="+ name +"&app_id=1d1398d3&app_key=b074b2ece9ca3aaca149b2a434676cd8");
            connection = (HttpURLConnection)url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            Scanner scanner = new Scanner(connection.getInputStream());
            while (scanner.hasNextLine()) {
                textResult += scanner.nextLine();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally{
            connection.disconnect();
        }
        return textResult;
    }

    public static Food getFoodFromAPIResult(String result){
        Food food = new Food();
        try{
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("parsed");
            if (jsonArray!=null && jsonArray.length()>0){
                JSONObject foodJsonObject = jsonArray.getJSONObject(0).getJSONObject("food");
                JSONObject nutrients = foodJsonObject.getJSONObject("nutrients");
                int calorieAmount = (int) nutrients.getDouble("ENERC_KCAL");
                int fat = (int) nutrients.getDouble("FAT");
                food.setCalorieAmount(calorieAmount);
                food.setFat(fat);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return food;
    }

    public static void addFoodIntoDB(Food food){
        //initialise
        URL url = null;
        HttpURLConnection conn = null;
        final String methodPath="hongdarestsw.food/";
        try {
            Gson gson =new Gson();
            String stringFoodJson=gson.toJson(food);
            url = new URL(BASE_URL + methodPath);
            //open the connection
            conn = (HttpURLConnection) url.openConnection();
            //set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            //set the connection method to POST
            conn.setRequestMethod("POST");
            //set the output to true
            conn.setDoOutput(true);
            //set length of the data you want to send
            conn.setFixedLengthStreamingMode(stringFoodJson.getBytes().length);
            //add HTTP headers
            conn.setRequestProperty("Content-Type", "application/json");
            //Send the POST out
            PrintWriter out= new PrintWriter(conn.getOutputStream());
            out.print(stringFoodJson);
            out.close();
            Log.i("error",new Integer(conn.getResponseCode()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
    }

    public static String findTotalCaloriesConsumed(int userid){
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String dateStr = simpleDateFormat.format(new Date());
        final String methodPath = "hongdarestsw.consumption/calculateTotalCaloriesConsumed/" + userid + "/" + dateStr;
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        //Making HTTP request
        try{
            url = new URL(BASE_URL + methodPath);
            //open the connection
            conn = (HttpURLConnection)url.openConnection();
            //set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            //set the connection method to GET
            conn.setRequestMethod("GET");
            //add http headers to set your response type to json
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            //Read the response
            Scanner inStream = new Scanner(conn.getInputStream());
            //read the input steream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            conn.disconnect();
        }
        return textResult;
    }

    public static Integer getTotalCaloriesConsumed(String result){
        Integer totalCaloriesConsumed = 0;
        try {
            JSONArray jsonArray = new JSONArray(result);
            if (jsonArray != null && jsonArray.length()>0){
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                totalCaloriesConsumed = jsonObject.getInt("totalCaloriesConsumed");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalCaloriesConsumed;
    }

    public static String findDailyCaloriesBurned(int userid){

        final String methodPath = "hongdarestsw.users/calculateDailyCaloriesBurned/" + userid;
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        //Making HTTP request
        try{
            url = new URL(BASE_URL + methodPath);
            //open the connection
            conn = (HttpURLConnection)url.openConnection();
            //set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            //set the connection method to GET
            conn.setRequestMethod("GET");
            //add http headers to set your response type to json
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            //Read the response
            Scanner inStream = new Scanner(conn.getInputStream());
            //read the input steream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            conn.disconnect();
        }
        return textResult;
    }

    public static Integer getDailyCaloriesBurned(String result){
        int totalCaloriesBurned = 0;
        try {
            JSONArray jsonArray = new JSONArray(result);
            if (jsonArray != null && jsonArray.length()>0){
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                totalCaloriesBurned = jsonObject.getInt("totalCaloriesBurned");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalCaloriesBurned;
    }

    public static String findCaloriesBurnedPerStep(int userid){

        final String methodPath = "hongdarestsw.users/calculateBurnedPerStep/" + userid;
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        //Making HTTP request
        try{
            url = new URL(BASE_URL + methodPath);
            //open the connection
            conn = (HttpURLConnection)url.openConnection();
            //set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            //set the connection method to GET
            conn.setRequestMethod("GET");
            //add http headers to set your response type to json
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            //Read the response
            Scanner inStream = new Scanner(conn.getInputStream());
            //read the input steream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            conn.disconnect();
        }
        return textResult;
    }

    public static double getCaloriesBurnedPerStep(String result){
        double caloriesBurnedPerStep = 0;
        try {
            JSONArray jsonArray = new JSONArray(result);
            if (jsonArray != null && jsonArray.length()>0){
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                caloriesBurnedPerStep = jsonObject.getDouble("caloriesBurnedPerStep");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return caloriesBurnedPerStep;
    }

    public static String findReportByDate(int userid, String dateStr){

        final String methodPath = "hongdarestsw.report/makeReprot/" + userid + "/" + dateStr;
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        //Making HTTP request
        try{
            url = new URL(BASE_URL + methodPath);
            //open the connection
            conn = (HttpURLConnection)url.openConnection();
            //set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            //set the connection method to GET
            conn.setRequestMethod("GET");
            //add http headers to set your response type to json
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            //Read the response
            Scanner inStream = new Scanner(conn.getInputStream());
            //read the input steream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            conn.disconnect();
        }
        return textResult;
    }

    public static ArrayList<Integer> getReportInfo(String result){
        ArrayList<Integer> reportInfo = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(result);
            if (jsonArray != null && jsonArray.length()>0){
                JSONObject jsonObject = jsonArray.getJSONObject(0);
//                caloriesBurnedPerStep = jsonObject.getDouble("caloriesBurnedPerStep");
                reportInfo.add(jsonObject.getInt("totalCaloriesBurned"));
                reportInfo.add(jsonObject.getInt("totalCaloriesConsumed"));
                reportInfo.add(jsonObject.getInt("remainingCalorie"));
            }else {
                reportInfo.add(0);
                reportInfo.add(0);
                reportInfo.add(0);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return reportInfo;
    }

    public static String findStepsByDate(int userid, String dateStr){

        final String methodPath = "hongdarestsw.report/findByUseridAndDate/" + userid + "/" + dateStr;
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        //Making HTTP request
        try{
            url = new URL(BASE_URL + methodPath);
            //open the connection
            conn = (HttpURLConnection)url.openConnection();
            //set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            //set the connection method to GET
            conn.setRequestMethod("GET");
            //add http headers to set your response type to json
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            //Read the response
            Scanner inStream = new Scanner(conn.getInputStream());
            //read the input steream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            conn.disconnect();
        }
        return textResult;
    }

    public static Integer getSteps(String result){
        int steps = 0;
        try{
            JSONArray jsonArray = new JSONArray(result);
            if (jsonArray != null && jsonArray.length()>0){
                JSONObject jsonObject = jsonArray.getJSONObject(0);
//                caloriesBurnedPerStep = jsonObject.getDouble("caloriesBurnedPerStep");
                steps = jsonObject.getInt("totalStepsTaken");

            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return steps;
    }

    public static String findReportByPeriod(int userid, String startDateStr ,String endDateStr){

        final String methodPath = "hongdarestsw.report/getReprotPeriod/" + userid + "/" + startDateStr + "/" + endDateStr;
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        //Making HTTP request
        try{
            url = new URL(BASE_URL + methodPath);
            //open the connection
            conn = (HttpURLConnection)url.openConnection();
            //set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            //set the connection method to GET
            conn.setRequestMethod("GET");
            //add http headers to set your response type to json
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            //Read the response
            Scanner inStream = new Scanner(conn.getInputStream());
            //read the input steream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            conn.disconnect();
        }
        return textResult;
    }

    public static ArrayList<Report> getReportsPeriod(String result){
        ArrayList<Report> reports = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(result);
            if (jsonArray!=null && jsonArray.length()>0){
                for (int i =0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int totalCaloriesBurned = jsonObject.getInt("totalCaloriesBurned");
                    int totalCaloriesConsumed = jsonObject.getInt("totalCaloriesConsumed");
                    int totalStepsTaken = jsonObject.getInt("totalStepsTaken");
                    Report report = new Report();
                    report.setTotalCaloriesBurned(totalCaloriesBurned);
                    report.setTotalCaloriesConsumed(totalCaloriesConsumed);
                    report.setStepsTaken(totalStepsTaken);
                    reports.add(report);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reports;
    }

    public static String findUserById(Integer id){
        final String methodPath = "hongdarestsw.users/" + id;
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        //Making HTTP request
        try{
            url = new URL(BASE_URL + methodPath);
            //open the connection
            conn = (HttpURLConnection)url.openConnection();
            //set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            //set the connection method to GET
            conn.setRequestMethod("GET");
            //add http headers to set your response type to json
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            //Read the response
            Scanner inStream = new Scanner(conn.getInputStream());
            //read the input steream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            conn.disconnect();
        }
        return textResult;
    }

    public static String getUserAddress(String result){
        String address = "";
        try{
            JSONObject jsonObject = new JSONObject(result);
            address = jsonObject.getString("address");
            } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return address;

    }

    public static String getNearParks(ArrayList<Double> position){
        final String apiUrl = "https://api.foursquare.com/v2/venues/search?client_id=YOUR_CLIENT_ID&client_secret=YOUR_CLIENT_SECRET&v=20190425&ll=" + position.get(0)+","+ position.get(1)+ "&radius=5000&categoryId=4bf58dd8d48988d163941735";
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        //Making HTTP request
        try{
            url = new URL(apiUrl);
            //open the connection
            conn = (HttpURLConnection)url.openConnection();
            //set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            //set the connection method to GET
            conn.setRequestMethod("GET");
            //add http headers to set your response type to json
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            //Read the response
            Scanner inStream = new Scanner(conn.getInputStream());
            //read the input steream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            conn.disconnect();
        }
        return textResult;

    }

    public static List<List<String>> getParkInfoList(String result){
        List<List<String>> parkInfoList = new ArrayList<>();
        try{
            JSONObject jsonObject = new JSONObject(result);
            JSONObject jsonResponse = jsonObject.getJSONObject("response");
            JSONArray venuesArray = jsonResponse.getJSONArray("venues");
            if (venuesArray!=null && venuesArray.length()>0){
                for (int i = 0; i < venuesArray.length(); i++){
                    JSONObject jsonParkInfo = venuesArray.getJSONObject(i);
                    List<String> parkInfo = new ArrayList<>();
                    String parkName = jsonParkInfo.getString("name");
                    parkInfo.add(parkName);
                    JSONObject jsonLocationInfo = jsonParkInfo.getJSONObject("location");
                    String lat = jsonLocationInfo.getString("lat");
                    String lng = jsonLocationInfo.getString("lng");
                    parkInfo.add(lat);
                    parkInfo.add(lng);
                    parkInfoList.add(parkInfo);
                }
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return parkInfoList;
    }

    public static void createReport(com.example.assignment3.ReportClassJavaDB.Report report){
        //initialise
        URL url = null;
        HttpURLConnection conn = null;
        final String methodPath="hongdarestsw.report/";
        try {
            Gson gson =new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").create();
            String stringUsersJson=gson.toJson(report);
            url = new URL(BASE_URL + methodPath);
            //open the connection
            conn = (HttpURLConnection) url.openConnection();
            //set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            //set the connection method to POST
            conn.setRequestMethod("POST");
            //set the output to true
            conn.setDoOutput(true);
            //set length of the data you want to send
            conn.setFixedLengthStreamingMode(stringUsersJson.getBytes().length);
            //add HTTP headers
            conn.setRequestProperty("Content-Type", "application/json");
            //Send the POST out
            PrintWriter out= new PrintWriter(conn.getOutputStream());
            out.print(stringUsersJson);
            out.close();
            Log.i("error",new Integer(conn.getResponseCode()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
    }

}
