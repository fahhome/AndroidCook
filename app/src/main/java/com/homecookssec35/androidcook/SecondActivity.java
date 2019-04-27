package com.homecookssec35.androidcook;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {


    public static ListView secondlistView ;
    public static String jsondishes;
    private static String tag =  "fahmidsecondactivity";
    public static CustomAdapter dishAdapter ;
    public static Button placeorderbutton ;
    public static Button testButton ;
    public static String selectedcookname = "";
    public static DatabaseReference fahmid ;
    public static String mobilenumber ;
    public static String latitude,longitude ;

    public static void afterjsonretrieval(){
        Log.d(tag,"after postonexecute");
        Log.d(tag, selectedcookname);

         //dishAdapter.clear();
         fahmid = FirebaseDatabase.getInstance().getReference().child("Cooks").child(selectedcookname).child("Products");
        fahmid.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()){
                   Product prod = ds.getValue(Product.class);
                   Dish d = new Dish(Integer.parseInt(prod.getId()),Integer.parseInt(prod.getCookId()),prod.getName(),prod.getDescription(),Double.parseDouble(prod.getUnitprice()),Boolean.parseBoolean(prod.getActive()),prod.getCode(),prod.getEstimatedtime());
                   if(prod.getActive().equalsIgnoreCase("true"))
                   {
                       dishAdapter.add(d);
                   }
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
        setContentView(R.layout.activity_second);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent receiveIntent = getIntent();
        selectedcookname = receiveIntent.getStringExtra("Cookname");
        mobilenumber = receiveIntent.getStringExtra("mobilenumber");
        latitude = receiveIntent.getStringExtra("latitude");
        longitude = receiveIntent.getStringExtra("longitude");
        dishAdapter = new CustomAdapter(this,R.layout.customproductslayout);
        secondlistView =  (ListView)findViewById(R.id.listdishes);
        secondlistView.setAdapter(dishAdapter);
        this.afterjsonretrieval();
    }

    class fetchProducts  extends AsyncTask<Void,Void,Void>{

        String data = "";
        @Override
        protected Void doInBackground(Void... voids) {
            try {

                URL url = new URL("https://api.myjson.com/bins/c3ay8");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while (null != line) {
                    line = bufferedReader.readLine();
                    Log.d(tag,"fahmidhere");
                    Log.d(tag,line + " " + selectedcookname);
                    data = data + line;

                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute (Void aVoid) {
            super.onPostExecute(aVoid);

            jsondishes=data;
            afterjsonretrieval();
        }

    }

    class CustomAdapter extends ArrayAdapter {

        private  Context  context;

        public CustomAdapter(@NonNull Context context, int resource, Context context1) {
            super(context, resource);
            this.context = context1;
        }

        List list = new ArrayList();

        public CustomAdapter(@NonNull Context context, int resource) {
            super(context, resource);
        }

        @Override
        public int getCount() {
            return list.size();
        }


        public void add(@Nullable Dish object) {
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

            convertView = getLayoutInflater().inflate(R.layout.customproductslayout,null);
            TextView txt1 = (TextView)convertView.findViewById(R.id.textView4);
            TextView txt2 = (TextView)convertView.findViewById(R.id.textView5);
            TextView txt3 = (TextView)convertView.findViewById(R.id.textView6);
            TextView txt4 = (TextView)convertView.findViewById(R.id.estimatedtext);
            ImageView imageview = (ImageView) convertView.findViewById(R.id.imageView);

            final Dish thisdish =  (Dish)this.getItem(position);

            txt1.setText(thisdish.getNameOfDish());
            txt2.setText(thisdish.getUnitPrice()+ "");
            txt3.setText(thisdish.getDescription());
            txt4.setText(thisdish.getEstimatedtime());
            txt1.setTextColor(Color.RED);
            placeorderbutton = (Button)convertView.findViewById(R.id.button3);
            placeorderbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent2 =  new Intent(SecondActivity.this,OrderingDetails.class);
                    intent2.putExtra("unitprice" , String.valueOf(thisdish.getUnitPrice()) );
                    intent2.putExtra("dishname" , thisdish.getNameOfDish() );
                    intent2.putExtra("mobilenumber", mobilenumber);
                    intent2.putExtra("latitude",latitude);
                    intent2.putExtra("longitude",longitude);
                    intent2.putExtra("cookname" , selectedcookname);
                    startActivity(intent2);

                }
            });
            final Context c = getApplicationContext();
            int  resourceid = c.getResources().getIdentifier("drawable/"+thisdish.getProductCode(), null, c.getPackageName());
            imageview.setImageResource(resourceid);
            return convertView;
        }
    }
}
