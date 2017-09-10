package com.dzdy.cryptopia;

import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class StoredData {
    private static final List<String> DEFAULT_PAIRS =
            Collections.unmodifiableList(Arrays.asList("LTC_BTC", "ETH_BTC"));

    static List<String> getChosenPairs(SharedPreferences preferences) {
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

    static boolean addPair(String newPair, SharedPreferences preferences) {
        if (preferences.contains(newPair))
            return false;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(newPair, getChosenPairs(preferences).size());
        editor.apply();
        return true;
    }
}
