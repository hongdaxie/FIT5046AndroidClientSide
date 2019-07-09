package com.example.assignment3;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DisplayDailyDietFragment extends Fragment {
    View vDisplayDailyDiet;
    ImageView reultIv;
    TextView reultTv;
    Integer userid;
    EditText evAddFood;
    Button btAddFood;
    List<String> itemsList;
    String category;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        vDisplayDailyDiet = inflater.inflate(R.layout.fragment_daily_diet, container, false);

        SharedPreferences spMyusername = getActivity().getSharedPreferences("names", Context.MODE_PRIVATE);
        String useridStr = spMyusername.getString("userid","DEFAULT");
        userid = Integer.parseInt(useridStr);

        reultTv = vDisplayDailyDiet.findViewById(R.id.tvSearchResult);
        reultIv = vDisplayDailyDiet.findViewById(R.id.ivSearchResult);
        evAddFood = vDisplayDailyDiet.findViewById(R.id.newFoodEt);
        btAddFood = vDisplayDailyDiet.findViewById(R.id.addNewFoodBt);

        List<String> categoryList = new ArrayList<String >();
        categoryList.add("Drink");
        categoryList.add("Meal");
        categoryList.add("Meat");
        categoryList.add("Snack");
        categoryList.add("Bread");
        categoryList.add("Cake");
        categoryList.add("Fruit");
        categoryList.add("Vegies");
        categoryList.add("Other");
        final Spinner sCategory = (Spinner) vDisplayDailyDiet.findViewById(R.id.category_spinner);
        final ArrayAdapter<String> sCategoryAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,categoryList);
        sCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sCategory.setAdapter(sCategoryAdapter);


        sCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = parent.getItemAtPosition(position).toString();
                GetItemsByCategoryAsyncTask getItemsByCategoryAsyncTask = new GetItemsByCategoryAsyncTask();
                getItemsByCategoryAsyncTask.execute(category);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newFoodName = evAddFood.getText().toString().trim();
                FindFoodByName findFoodByName = new FindFoodByName();
                findFoodByName.execute(newFoodName);
            }
        });

        return vDisplayDailyDiet;
    }


    private class GetItemsByCategoryAsyncTask extends AsyncTask<String ,Void, ArrayList<Food>>{
        @Override
        protected ArrayList<Food> doInBackground(String... strings) {
            String selectedCategory = strings[0];
            String result = RestClient.findItemsByCategory(selectedCategory);
            ArrayList<Food> itemsInDB = RestClient.getItems(result);
            return itemsInDB;
        }

        @Override
        protected void onPostExecute(ArrayList<Food> itemsInDB) {
            itemsList = new ArrayList<String>();
            itemsList.add(" ");
            if (itemsInDB.size() != 0){
                for (int i = 0; i < itemsInDB.size(); i++ ){
                    itemsList.add(itemsInDB.get(i).getName());
                }
            }
            final Spinner sItems = (Spinner) vDisplayDailyDiet.findViewById(R.id.items_spinner);
            final ArrayAdapter<String> sItemsAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,itemsList);
            sItemsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sItems.setAdapter(sItemsAdapter);

            sItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedItem = parent.getItemAtPosition(position).toString();
                    if (!(" ".equals(selectedItem))){
                        reultTv.setVisibility(View.VISIBLE);
                        reultIv.setVisibility(View.VISIBLE);
                        SearchAsyncTask searchAsyncTask = new SearchAsyncTask();
                        searchAsyncTask.execute(selectedItem);


//                        System.out.println(foodId);
                        AddConsumptionAsyncTask addConsumptionAsyncTask = new AddConsumptionAsyncTask();
                        addConsumptionAsyncTask.execute(selectedItem);

                    }else {
                        reultTv.setVisibility(View.INVISIBLE);
                        reultIv.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    private class SearchAsyncTask extends AsyncTask<String , Void, List<String>>{
        @Override
        protected List<String> doInBackground(String... strings) {
            List<String> resultList = new ArrayList<String>();

            String textSearchResult = RestClient.googleSearch(strings[0], new String[]{"num"}, new String[]{"1"});
            String imageSearchResult = RestClient.googleSearch(strings[0], new String[]{"num","searchType"}, new String[]{"1","image"});
            resultList.add(textSearchResult);
            resultList.add(imageSearchResult);
            return resultList;
        }

        @Override
        protected void onPostExecute(List<String> s) {
            String textResult = RestClient.getSnippet(s.get(0));
            reultTv.setText(textResult);
            String imageResult = RestClient.getImageSrc(s.get(1));
            GetImageFromURL getImageFromURL = new GetImageFromURL();
            getImageFromURL.execute(imageResult);
        }
    }

    private class GetImageFromURL extends AsyncTask<String, Void, Bitmap>{
        Bitmap bitmap;
        @Override
        protected Bitmap doInBackground(String... strings) {
            String urlDisplay = strings[0];
            bitmap = null;
            try{
                InputStream srt = new java.net.URL(urlDisplay).openStream();
                bitmap = BitmapFactory.decodeStream(srt);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            reultIv.setImageBitmap(bitmap);
        }
    }

    private class AddConsumptionAsyncTask extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... strings) {
            String result = RestClient.findFoodByName(strings[0]);
            Food food = RestClient.getItems(result).get(0);
            Consumption consumption = new Consumption();
            Users user = new Users();
            user.setId(userid);
            consumption.setFoodId(food);
            consumption.setUserId(user);
            consumption.setQuantity(1);
            consumption.setDate(new Date());
            RestClient.addConsumption(consumption);
//            itemsList.add(strings[0]); // add this new item into the itemList
            return null;
        }
    }

    private class FindFoodByName extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {
            String foodName = strings[0];
            String feedback = "";
            if ("".equals(foodName)){
                feedback = "You should enter a food name!";
            }else {
                String result = RestClient.findFoodByName(foodName);
                if ("[]".equals(result)){
                    String foodAPIResult = RestClient.findFoodFromAPI(foodName);
                    Food food = RestClient.getFoodFromAPIResult(foodAPIResult);
                    food.setName(foodName);
                    food.setCategory(category);
                    food.setServingAmount(BigDecimal.valueOf(1));
                    food.setServingUnit("100g");
                    RestClient.addFoodIntoDB(food);
                    itemsList.add(foodName);

                }else {

                    feedback = "The food is already exist in above!";
                }
            }
            return feedback;
        }

        @Override
        protected void onPostExecute(String s) {
            if (!("".equals(s))){
                evAddFood.setError(s);
            }

        }
    }



}
