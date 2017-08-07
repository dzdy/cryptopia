package com.dzdy.cryptopia;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public RequestQueue requestQueue;

    private Adapter mAdapter;

    public static final List<String> DEFAULT_PAIRS = Collections.unmodifiableList(Arrays.asList("LTC_BTC", "ETH_BTC"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
                refreshLayout.setRefreshing(false);
            }
        });

        requestQueue = Volley.newRequestQueue(this);
        refreshData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshData() {
        final TextView timeTextView = (TextView) findViewById(R.id.timeTextView);
        timeTextView.setText(getResources().getString(R.string.time_prefix, new java.util.Date().toString()));

        List<String> chosenPairs = getChosenPairs();

        if (mAdapter == null) {
            RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(mLayoutManager);

            List<Pair> pairs = new ArrayList<>();
            for (String pair : chosenPairs) pairs.add(new Pair(pair, 0.0));
            mAdapter = new Adapter(pairs);
            mRecyclerView.setAdapter(mAdapter);
        }

        for (int i = 0; i < chosenPairs.size(); ++i) {
            updatePrice(i, chosenPairs.get(i));
        }
    }

    private void updatePrice(final int position, final String pairName) {
        String url = "https://www.cryptopia.co.nz/api/GetMarket/" + pairName;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Double askPrice = null;
                        try {
                            askPrice = response.getJSONObject("Data").getDouble("AskPrice");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mAdapter.updatePairPrice(position, new Pair(pairName, askPrice));
                        mAdapter.notifyItemChanged(position);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO add error message toast
                        error.printStackTrace();
                    }
                });
        requestQueue.add(jsObjRequest);
    }

    List<String> getChosenPairs() {
        SharedPreferences preferences = getSharedPreferences("pair_preferences", 0);
        Map<String, Integer> pairs = (HashMap<String, Integer>) preferences.getAll();
        List<String> result;

        if (pairs.size() == 0) {
            SharedPreferences.Editor editor = preferences.edit();
            for (int i = 0; i < DEFAULT_PAIRS.size(); ++i) editor.putInt(DEFAULT_PAIRS.get(i), i);
            editor.apply();
            result = DEFAULT_PAIRS;
        } else {
            String[] result_arr = new String[pairs.size()];
            for (Map.Entry<String, Integer> pair : pairs.entrySet())
                result_arr[pair.getValue()] = pair.getKey();
            result = Arrays.asList(result_arr);
        }
        return result;
    }
}
