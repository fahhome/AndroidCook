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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.xml.transform.Result;

public class MainActivity extends AppCompatActivity {

    Button click;
    TextView textview;
    public  static int  isanycookavailable = 0 ,iscookavailable = 0;
    public  static ProgressBar  progbar;
    public static ListView listView;
    public static String jsondata = "";
    private static String tag = "fahmid";
    public static CustomAdapter cookAdapter;
    private LocationManager locationManager;
    private LocationListener locationListener;
    public static DatabaseReference fahmid, fahmid2;
    public static String firstcookname, secondcookname, fphone, sphone;
    // double latval , longval ;
    private static double latvals = 0.0, longvals = 0.0;

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public void nocook(){
        Toast.makeText(MainActivity.this,"Sorry! No Cooks available near your location currently",Toast.LENGTH_LONG).show();
    }

    public static void func(final Context context) {
        Log.d(tag, "after postonexecute");
       // Log.d(tag, jsondata);

       // cookAdapter.clear();



        fahmid = FirebaseDatabase.getInstance().getReference().child("Cooks");

        fahmid.addListenerForSingleValueEvent(new ValueEventListener() {



            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Kitchen k = ds.getValue(Kitchen.class);
                    Cook newcook = new Cook(k.getName(), k.getPhone());

                    Log.d(tag + "newcook",String.valueOf(newcook.getLatitude()));

                    Location locationA = new Location("point A");
                    locationA.setLatitude(latvals);
                    locationA.setLongitude(longvals);

                    Location locationB = new Location("point B");
                    locationB.setLatitude(Double.parseDouble(k.getLatpos()));
                    locationB.setLongitude(Double.parseDouble(k.getLongPos()));

                    newcook.setLatitude(Double.parseDouble(k.getLatpos()));
                    newcook.setLongitude(Double.parseDouble(k.getLongPos()));

                    Log.d(tag + "newcook2",String.valueOf(newcook.getLatitude()));

                    float distance = locationA.distanceTo(locationB);
                    Log.d(tag,String.valueOf(distance));
                   if( (distance / 1000) < 4.12)
                    {
                        isanycookavailable = 1;
                        if(k.getIsactive().equalsIgnoreCase("true"))
                        {
                            iscookavailable =1 ;
                            cookAdapter.add(newcook);
                        }
                    }

            }
                progbar.setVisibility(View.GONE);
              if(isanycookavailable == 0){
                  Toast.makeText(context,"Sorry! No Cooks available near your location currently",Toast.LENGTH_LONG).show();
              }
              if(isanycookavailable ==1 && iscookavailable == 0){
                  Toast.makeText(context,"Sorry! All cooks near you are unavailabe at the moment",Toast.LENGTH_LONG).show();
              }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem logoutitem = menu.findItem(R.id.menulogout);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            logoutitem.setEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menulogout:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.menuhelp:
                startActivity(new Intent(this, Help.class));
                break;
            case R.id.menuorder :
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Intent intent3 =  new Intent(this,LoginUserActivity.class);
                    startActivity(intent3);
                }
                else{
                    Intent intent2 =  new Intent(this,Profile.class);
                    startActivity(intent2);
                }
                break;
        }

        return true;
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
        progbar = (ProgressBar)findViewById(R.id.progressbar1);

        if( !isOnline()  ){
            Toast.makeText(this,"Please turn on your Internet connection",Toast.LENGTH_LONG).show();
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                // textview.append("\n " + location.getLatitude() + " " + location.getLongitude());
                Log.d(tag, String.valueOf(location.getLatitude()));
                Log.d(tag, String.valueOf(location.getLongitude()));
                latvals = location.getLatitude();
                longvals = location.getLongitude();
                //if(listView.emp)
                if (cookAdapter.getCount() == 0) {
                } else {
                    // listView.removeAllViews();
                    cookAdapter.clear();
                }
                //fetchData process = new fetchData();
                //process.execute();
                func(getApplicationContext());
                cookAdapter.notifyDataSetChanged();

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
                intent.putExtra("mobilenumber", tempCook.getContact());
                intent.putExtra("latitude", String.valueOf(latvals));
                intent.putExtra("longitude", String.valueOf(longvals));
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

                progbar.setVisibility(View.VISIBLE);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, locationListener);


                //fetchData process = new fetchData();
                //process.execute();

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
        public void clear() {
            super.clear();
            list.clear();
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

            convertView = getLayoutInflater().inflate(R.layout.customlayout, null);
            TextView txt1 = (TextView) convertView.findViewById(R.id.textView2);
            TextView txt2 = (TextView) convertView.findViewById(R.id.textView3);

            Location locationA = new Location("point A");
            locationA.setLatitude(latvals);
            locationA.setLongitude(longvals);



            Cook cook = (Cook) this.getItem(position);

            Location locationB = new Location("point B");
            locationB.setLatitude(cook.getLatitude());
            locationB.setLongitude(cook.getLongitude());

            float distance = locationA.distanceTo(locationB);
            float distanceinkm = distance/1000;
            txt1.setText(cook.getName());
            txt2.setText(" " + distanceinkm + "Km");
            txt1.setTextColor(Color.RED);

            return convertView;
        }
    }

}