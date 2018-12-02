package kwa.pravaah.database;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

import kwa.pravaah.SMSListener;
import kwa.pravaah.model.message;


public class Pushdata extends AsyncTask<String, Void, String> {


    public AsyncResponse delegate = null;
    DbManager db;
    JSONObject postDataParams;
    URL url;
    public Pushdata(AsyncResponse asyncResponse) {
        delegate = asyncResponse;
    }

    protected void onPreExecute(){}

    protected String doInBackground(String... arg0) {


        try{

            String no = SMSListener.num;
            Cursor cursor=db.getsheet(no);
            if(cursor.getCount()!=0) {

                cursor.moveToFirst();
                String id = cursor.getString(cursor.getColumnIndex(db.SHEET_ID));

                 url = new URL("https://script.google.com/macros/s/AKfycbyECzKI9C68PKXE72G-PyZytMoBHQu69qWgZ_CA1J8SPdm3irw/exec");
                JSONObject postDataParams = new JSONObject();


               // String id = "1ztDzwD-mkA3XOXXC_kju8kq-hf4BUZ3mS4nkqwOrU-Q";

                postDataParams.put("number", arg0[0]);
                postDataParams.put("power", arg0[1]);
                postDataParams.put("pump", arg0[2]);
                postDataParams.put("err", arg0[3]);
                postDataParams.put("eventhrs", arg0[4]);
                postDataParams.put("auto", arg0[5]);
                postDataParams.put("tm", arg0[6]);
                postDataParams.put("id", id);


                Log.e("params", postDataParams.toString());
            }
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

               int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                } else {
                    return new String("false : " + responseCode);
                }
            }


        catch(Exception e){
            return new String("Exception: " + e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(String result) {

        delegate.processFinish(result);

    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }

}
