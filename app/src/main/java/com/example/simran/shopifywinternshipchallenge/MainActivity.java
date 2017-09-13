package com.example.simran.shopifywinternshipchallenge;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private String data = null;
    private JSONArray orders = null;

    private class FetchDataTask extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONArray doInBackground(String... strings) {

            String url = strings[0];
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
                data = response.body().string();
            } catch (IOException e) {
                Log.e("RESPONSE", "Response error", e);
            }

            try {
                JSONObject jsonObj = new JSONObject(data);
                orders = jsonObj.getJSONArray("orders");
            } catch (JSONException e) {
                Log.e("ORDERS_JSON", "JSON Error while obtaining orders", e);
            }

            return orders;
        }

        @Override
        protected void onPostExecute(JSONArray orders) {
            double napoleonTotal = 0.00;
            int bronzeBags = 0;

            int length = orders.length();
            for (int i = 0; i < length; ++i) {
                try {
                    JSONObject order = orders.getJSONObject(i);
                    String email = order.getString("email");
                    if (email.equals("napoleon.batz@gmail.com")) {
                        double price = Double.valueOf(order.getString("total_price"));
                        napoleonTotal += price;
                    }
                    JSONArray itemsArray = order.getJSONArray("line_items");
                    int lineItemsLength = itemsArray.length();
                    for (int j = 0; j < lineItemsLength; ++j) {
                        JSONObject item = itemsArray.getJSONObject(j);
                        String title = item.getString("title");
                        if (title.equals("Awesome Bronze Bag")) {
                            bronzeBags++;
                        }
                    }
                } catch (JSONException E) {
                    throw new RuntimeException(E);
                }
            }

            TextView bronzeBagsTextView = (TextView) findViewById(R.id.bronze_bags);
            bronzeBagsTextView.setText(Integer.toString(bronzeBags));

            TextView napoleonTextView = (TextView) findViewById(R.id.napoleon);
            napoleonTextView.setText("$" + Double.toString(napoleonTotal));
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new FetchDataTask().execute(getString(R.string.url));
    }

}
