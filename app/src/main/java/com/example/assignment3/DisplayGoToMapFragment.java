package com.example.assignment3;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapquest.mapping.maps.MapView;

import java.util.ArrayList;
import java.util.List;

public class DisplayGoToMapFragment extends Fragment {
    View vDisplayGoToMap;
    int userid;
    private MapboxMap mMapboxMap;
    private MapView mMapView;
    IconFactory iconFactory;
//    private final LatLng SAN_FRAN = new LatLng(37.7749, -122.4194);
    Icon blackIcon;
    Icon redIcon;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        vDisplayGoToMap = inflater.inflate(R.layout.fragment_to_map, container, false);
        SharedPreferences spMyusername = getActivity().getSharedPreferences("names", Context.MODE_PRIVATE);
        String useridStr = spMyusername.getString("userid","DEFAULT");
        userid = Integer.parseInt(useridStr);

        mMapView =(MapView) vDisplayGoToMap.findViewById(R.id.mapquestMapView);
        iconFactory = IconFactory.getInstance(getActivity());
        blackIcon = iconFactory.fromResource(R.drawable.black_marker);
        redIcon = iconFactory.fromResource(R.drawable.red_marker);


        return vDisplayGoToMap;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
//        mMapView.onCreate(savedInstanceState);
        GetUserAddress getUserAddress = new GetUserAddress();
        getUserAddress.execute();

    }

    private class GetUserAddress extends AsyncTask<Void,Void,ArrayList<Double>>{
        @Override
        protected ArrayList<Double> doInBackground(Void... voids) {
            ArrayList<Double> position = new ArrayList<>();
            String reult = RestClient.findUserById(userid);
            String address = RestClient.getUserAddress(reult);
            if (!("".equals(address))){
                position = getLocationFromAddress(address);
//                addMarker();
            }
            return position;
        }

        @Override
        protected void onPostExecute(ArrayList<Double> position) {

            GetNaerParkInfo getNaerParkInfo = new GetNaerParkInfo(position);
            getNaerParkInfo.execute();
        }
    }


    public ArrayList<Double> getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(getActivity());
        List<Address> address;
        ArrayList<Double> position = new ArrayList<>();

        try {
            address = coder.getFromLocationName(strAddress, 1);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            double lat = location.getLatitude();
            double lng = location.getLongitude();

            position.add(lat);
            position.add(lng);
            return position;
        } catch (Exception e) {
            return null;
        }
    }


    private class GetNaerParkInfo extends AsyncTask<Void,Void,List<List<String>>>{

        ArrayList<Double> position;

        public GetNaerParkInfo(ArrayList<Double> position) {
            this.position = position;
        }

        @Override
        protected List<List<String>> doInBackground(Void... voids) {
            String result = RestClient.getNearParks(position);
            List<List<String>> parkInfoList = RestClient.getParkInfoList(result);
            return parkInfoList;
        }

        @Override
        protected void onPostExecute(List<List<String>> parkInfoList) {
            if (parkInfoList!=null && parkInfoList.size()>0){
                mMapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(MapboxMap mapboxMap) {
                        mMapboxMap = mapboxMap;
                        mMapView.setStreetMode();
                        mMapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(position.get(0),position.get(1)), 11));
                        addMarker(mapboxMap,position,"Home","Welcome to your home!");
                        for (int i = 0; i < parkInfoList.size(); i++){
                            ArrayList<Double> parkPosition = new ArrayList<>();
                            parkPosition.add(Double.parseDouble(parkInfoList.get(i).get(1)));
                            parkPosition.add(Double.parseDouble(parkInfoList.get(i).get(2)));
                            addMarker(mapboxMap,parkPosition,parkInfoList.get(i).get(0),"Welcome to visit " + parkInfoList.get(i).get(0) + "!");
                        }
                    }
                });
            }

        }
    }


    private void addMarker(MapboxMap mapboxMap, ArrayList<Double> position, String name,String spnippet) {

        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(SAN_FRAN);


        LatLng placePosition = new LatLng(position.get(0),position.get(1));
        markerOptions.position(placePosition);
        markerOptions.title(name);
        markerOptions.snippet(spnippet);
        if ("Home".equals(name)){
            markerOptions.icon(redIcon);
            mapboxMap.addMarker(markerOptions);
        }else {
            markerOptions.icon(blackIcon);
            mapboxMap.addMarker(markerOptions);
        }

    }


    @Override
    public void onResume()
    {
        super.onResume();
        if (mMapView!=null){
            mMapView.onResume();
        }

    }
    @Override
    public void onPause()
    {
        super.onPause();
        if (mMapView!=null){
            mMapView.onPause();
        }
    }
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        if (mMapView!=null){
             mMapView.onDestroy();
         }
     }
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if (mMapView!=null){
            mMapView.onSaveInstanceState(outState);
        }

    }
}
