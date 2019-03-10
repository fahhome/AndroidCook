package com.homecookssec35.androidcook;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.xml.transform.Result;

public class MainActivity extends AppCompatActivity {

    Button click;
    TextView textview;
    public static ListView listView;
    public static String jsondata = "";
    private static String tag = "fahmid";
    public static CustomAdapter cookAdapter;
    private LocationManager locationManager;
    private LocationListener locationListener;
   // double latval , longval ;
    private static  double latvals=0.0, longvals=0.0;
    public static void func() {
        Log.d(tag, "after postonexecute");
        Log.d(tag, jsondata);
        try {

            JSONArray JA = new JSONArray(jsondata);
            Log.d(tag , String.valueOf(latvals));
            Log.d(tag , String.valueOf(longvals));
            Location locationA = new Location("point A");
            locationA.setLatitude(latvals);
            locationA.setLongitude(longvals);
            for (int i = 0; i < JA.length(); i++) {
                JSONObject JO = (JSONObject) JA.get(i);

                String thisname = (String) JO.get("name");
                String thiscontact = (String) JO.get("contact");
                String latpos = (String)JO.get("latpos");
                String longpos = (String)JO.get("longpos");
                double latposval = Double.parseDouble(latpos);
                double longposval = Double.parseDouble(longpos);

                Log.d(tag, String.valueOf(latposval));
                Log.d(tag, String.valueOf(longposval));

              /* Location locationA = new Location("point A");
                locationA.setLatitude(latvals);
                locationA.setLongitude(longvals); */

                Location locationB = new Location("point B");
                locationB.setLatitude(latposval);
                locationB.setLongitude(longposval);

                float distance = locationA.distanceTo(locationB);
                Log.d(tag,String.valueOf(distance));

                if( (distance/1000) < 4.12)
                {
                    Log.d(tag , "inside");
                    Cook c = new Cook(thisname, thiscontact);
                    Log.d(tag, "name is : " + c.getName());
                    cookAdapter.add(c);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cookAdapter = new CustomAdapter(this, R.layout.customlayout);
        click = (Button) findViewById(R.id.button);
        textview = (TextView) findViewById(R.id.textView);
        listView = (ListView) findViewById(R.id.fetcheddata);
        listView.setAdapter(cookAdapter);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                textview.append("\n " + location.getLatitude() + " " + location.getLongitude());
                Log.d(tag, String.valueOf(location.getLatitude()));
                Log.d(tag, String.valueOf(location.getLongitude()));
                latvals = location.getLatitude();
                longvals = location.getLongitude() ;

                 fetchData process = new fetchData();
                 process.execute();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                // If GPS is not enabled .
                Log.d(tag, "gps not enabled");
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                requestPermissions(new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
                }, 10);


                return;
            }
        } else {
            configureButton();
        }
        //   locationManager.requestLocationUpdates("gps", 5000, 10, locationListener);

     /*   click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchData process = new fetchData();
                process.execute();
            }
        }); */

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cook tempCook = (Cook) listView.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra("Cookname", tempCook.getName());
                startActivity(intent);
            }
        });
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    configureButton();
                return;
        }
    }
        
    private void configureButton() {

        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, locationListener);
               // fetchData process = new fetchData();
               // process.execute();

            }
        });

    }
     class CustomAdapter extends ArrayAdapter {

         List list = new ArrayList();

         public CustomAdapter(@NonNull Context context, int resource) {
             super(context, resource);
         }

         @Override
         public int getCount() {
             return list.size();
         }


         public void add(@Nullable Cook object) {
             super.add(object);
             list.add(object);
         }

         @Override

         // getItem and getitemId are
         public Object getItem(int position) {
             return list.get(position);
         }

         @Override
         public long getItemId(int position) {
             return 0;
         }

         @Override
         public View getView(int position, View convertView, ViewGroup parent) {

             convertView = getLayoutInflater().inflate(R.layout.customlayout,null);
             TextView txt1 = (TextView)convertView.findViewById(R.id.textView2);
             TextView txt2 = (TextView)convertView.findViewById(R.id.textView3);

             Cook cook =  (Cook)this.getItem(position);

             txt1.setText(cook.getName());
             txt2.setText(cook.getContact());
             txt1.setTextColor(Color.RED);

             return convertView;
         }
     }
}
