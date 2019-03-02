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
    String selectedcookname = "";
    public static void afterjsonretrieval(){
        Log.d(tag,"after postonexecute");
        Log.d(tag, jsondishes);

        try {

            JSONArray JA = new JSONArray(jsondishes);
            for (int i = 0; i < JA.length(); i++) {
                JSONObject JO = (JSONObject) JA.get(i);

                int id = (int)JO.get("id");
                String thisproductcode = (String) JO.get("code");
                String thisdescription = (String) JO.get("description");
                String thisdishname = (String)JO.get("name");
                double dishunitprice = (double) JO.get("unitPrice");
                int dishcookid = (int) JO.get("cookId");
                boolean dishactive = (boolean)JO.get("active");
                if(dishcookid == 2)
                {
                    Dish d = new Dish(id,dishcookid,thisdishname,thisdescription,dishunitprice,dishactive,thisproductcode.toLowerCase());
                    Log.d(tag, "name is : " + d.getNameOfDish());
                    dishAdapter.add(d);
                }

            }

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent receiveIntent = getIntent();
        selectedcookname = receiveIntent.getStringExtra("Cookname");

        dishAdapter = new CustomAdapter(this,R.layout.customproductslayout);
        secondlistView =  (ListView)findViewById(R.id.listdishes);
        testButton = (Button)findViewById(R.id.button2);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(tag,"inside on click");
               if(FirebaseAuth.getInstance().getCurrentUser() != null){
                   Log.d(tag,"if condition");
                   Intent intent2 =  new Intent(SecondActivity.this,Profile.class);
                   startActivity(intent2);

               }
               else{
                   Log.d(tag,"else condition");
                  // Intent intent2 =  new Intent(SecondActivity.this,LoginUserActivity.class);
                   Intent intent3 =  new Intent(SecondActivity.this,SignUpActivity.class);
                   startActivity(intent3);
               // Log.d(tag,"on click");
               }

            }
        });
        secondlistView.setAdapter(dishAdapter);
        fetchProducts fp = new fetchProducts();
        fp.execute();
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
            ImageView imageview = (ImageView) convertView.findViewById(R.id.imageView);

            final Dish thisdish =  (Dish)this.getItem(position);

            txt1.setText(thisdish.getNameOfDish());
            txt2.setText(thisdish.getUnitPrice()+ "");
            txt3.setText(thisdish.getDescription());
            txt1.setTextColor(Color.RED);
            placeorderbutton = (Button)convertView.findViewById(R.id.button3);
            placeorderbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent2 =  new Intent(SecondActivity.this,OrderingDetails.class);
                    intent2.putExtra("unitprice" , "100");
                    intent2.putExtra("dishname", "Biryani");
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
