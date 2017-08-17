package com.dzdy.cryptopia;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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
import java.util.concurrent.TimeUnit;

public class AddPairActivity extends AppCompatActivity {
    List<String> chosenPairs;
    Map<String, Currency> allCurrencies;
    private RequestQueue requestQueue;
    private AutoCompleteTextView currencyDropdown;
    private AutoCompleteTextView baseCurrencyDropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pair);
        requestQueue = Volley.newRequestQueue(this);
        loadAllCurrencies();
        CharSequence err_msg = getString(R.string.currencies_updated);
        Toast.makeText(getApplicationContext(), err_msg, Toast.LENGTH_SHORT).show();

        SharedPreferences pair_prefs = getSharedPreferences(getString(R.string.pair_prefs_file), 0);
        chosenPairs = StoredData.getChosenPairs(pair_prefs);

        currencyDropdown = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        baseCurrencyDropdown = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView2);
        currencyDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                InputMethodManager in = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                try { // Wait for keyboard to disappear
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                populateBaseCurrencyDropdown(((Currency)
                        adapterView.getItemAtPosition(i)).baseCurrencies);
            }
        });
        baseCurrencyDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                InputMethodManager in = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
            }
        });
        currencyDropdown.requestFocus();
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
        populateDropDown(new ArrayList<>(allCurrencies.values()), currencyDropdown);
    }

    void populateBaseCurrencyDropdown(List<Currency> currencies) {
        populateDropDown(currencies, baseCurrencyDropdown);
    }

    void populateDropDown(List<Currency> currencies, final AutoCompleteTextView dropdown) {
        ArrayAdapter<Currency> adapter = new ArrayAdapter<>(getBaseContext(),
                android.R.layout.simple_dropdown_item_1line, currencies);
        dropdown.setAdapter(adapter);
        dropdown.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) dropdown.showDropDown();
            }
        });
        dropdown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                dropdown.showDropDown();
                return false;
            }
        });
        dropdown.clearFocus();
        dropdown.requestFocus();
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
