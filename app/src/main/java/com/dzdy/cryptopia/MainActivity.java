package com.dzdy.cryptopia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public RequestQueue requestQueue;

    private PairAdapter mPairAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

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
        timeTextView.setText(getString(R.string.time_prefix, new java.util.Date().toString()));

        SharedPreferences pair_prefs = getSharedPreferences(getString(R.string.pair_prefs_file), 0);
        List<String> chosenPairs = StoredData.getChosenPairs(pair_prefs);

        if (mPairAdapter == null) {
            RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(mLayoutManager);

            List<Pair> pairs = new ArrayList<>();
            for (String pair : chosenPairs) pairs.add(new Pair(pair, 0.0));
            mPairAdapter = new PairAdapter(pairs);
            mRecyclerView.setAdapter(mPairAdapter);
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
                        mPairAdapter.updatePairPrice(position, new Pair(pairName, askPrice));
                        mPairAdapter.notifyItemChanged(position);
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

    public void addPair(View view) {
        Intent intent = new Intent(this, AddPairActivity.class);
        startActivity(intent);
    }
}
