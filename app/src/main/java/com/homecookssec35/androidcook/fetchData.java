package com.homecookssec35.androidcook;

import android.os.AsyncTask;
import android.util.Log;

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

public class fetchData  extends AsyncTask<Void,Void,Void> {

    String data = "";
    String dataParsed = "";
    String singleParsed = "";
    String  tag = "fahmid";



    @Override
    protected Void doInBackground(Void... voids) {


        /*try {
            URL url = new URL("https://api.myjson.com/bins/1h8ev6");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while (line != null) {
                line = bufferedReader.readLine();
                data = data + line;
            }
         /*
            JSONArray JA = new JSONArray(data);
            for (int i = 0; i < JA.length(); i++) {
                JSONObject JO = (JSONObject) JA.get(i);
                singleParsed = "Name:" + JO.get("name") + "\n" +
                        "Password2:" + JO.get("password") + "\n" +
                        "Contact2:" + JO.get("contact") + "\n" +
                        "Country:" + JO.get("country") + "\n";

                dataParsed = dataParsed + singleParsed + "\n";
                String thisname =  (String)JO.get("name");
                String thiscontact =  (String)JO.get("contact");

                Cook c = new Cook(thisname,thiscontact);
                Log.d(tag,"name is : " + c.getName());

            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } */
       return null;
    }
        @Override
        protected void onPostExecute (Void aVoid) {
        super.onPostExecute(aVoid);

        MainActivity.jsondata=data;
        //MainActivity.func();
    }
}

