package com.dzdy.cryptopia;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

public class AddPairActivity extends AppCompatActivity {
    List<String> chosenPairs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pair);

        SharedPreferences pair_prefs = getSharedPreferences(getString(R.string.pair_prefs_file), 0);
        chosenPairs = StoredData.getChosenPairs(pair_prefs);
    }
}
