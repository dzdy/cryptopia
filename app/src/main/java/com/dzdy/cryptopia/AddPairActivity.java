package com.dzdy.cryptopia;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddPairActivity extends AppCompatActivity {
    List<String> chosenPairs;
    Map<String, Currency> allCurrencies;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pair);
        requestQueue = Volley.newRequestQueue(this);

        SharedPreferences pair_prefs = getSharedPreferences(getString(R.string.pair_prefs_file), 0);
        chosenPairs = StoredData.getChosenPairs(pair_prefs);
        loadAllCurrencies();
    }

    public void loadAllCurrencies() {
        allCurrencies = new HashMap<>();
        String url = "https://www.cryptopia.co.nz/api/GetTradePairs";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray allData = response.getJSONArray("Data");
                            for (int i = 0; i < allData.length(); i++) {
                                JSONObject pair = allData.getJSONObject(i);
                                addToAllCurrencies(pair.getString("Currency"),
                                        pair.getString("Symbol"),
                                        pair.getString("BaseCurrency"),
                                        pair.getString("BaseSymbol"));
                            }
                            Log.d("addpair", "currencies loaded");
                            populateCurrencyDropdown();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        CharSequence err_msg = getString(R.string.network_err);
                        Toast.makeText(getApplicationContext(), err_msg, Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });
        requestQueue.add(jsObjRequest);
    }

    void populateCurrencyDropdown() {
        ArrayAdapter<Currency> adapter = new ArrayAdapter<>(getBaseContext(),
                android.R.layout.simple_dropdown_item_1line,
                new ArrayList<>(allCurrencies.values()));
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        textView.setThreshold(1);
        textView.setAdapter(adapter);
    }

    void addToAllCurrencies(String name, String symbol, String baseName, String baseSymbol) {
        if (!allCurrencies.containsKey(name)) {
            allCurrencies.put(name, new Currency(name, symbol));
        }
        Currency currency = allCurrencies.get(name);
        currency.addBaseCurrency(new Currency(baseName, baseSymbol));
        allCurrencies.put(name, currency);
    }
}
