package com.dzdy.cryptopia;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class StoredData {
    private static final List<String> DEFAULT_PAIRS =
            Collections.unmodifiableList(Arrays.asList("ETH_BTC", "LTC_BTC"));

    static List<String> getChosenPairs(SharedPreferences preferences) {
        Map<String, Integer> pairs = (HashMap<String, Integer>) preferences.getAll();

        if (pairs.size() == 0) {
            storeDefaults(preferences);
            return DEFAULT_PAIRS;
        }
        String[] result_arr = new String[pairs.size()];
        for (Map.Entry<String, Integer> pair : pairs.entrySet())
            result_arr[pair.getValue()] = pair.getKey();
        return Arrays.asList(result_arr);
    }

    static boolean addPair(String newPair, SharedPreferences preferences) {
        if (preferences.contains(newPair))
            return false;

        ArrayList<String> pairs = new ArrayList<>(getChosenPairs(preferences));
        pairs.add(pairs.size(), newPair);
        Collections.sort(pairs);
        SharedPreferences.Editor editor = preferences.edit();
        for (int i = 0; i < pairs.size(); ++i)
            editor.putInt(pairs.get(i), i);
        editor.apply();
        return true;
    }

    static void delPair(int position, SharedPreferences preferences) {
        ArrayList<String> pairs = new ArrayList<>(getChosenPairs(preferences));
        pairs.remove(position);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        for (int i = 0; i < pairs.size(); ++i)
            editor.putInt(pairs.get(i), i);
        editor.apply();
    }

    static void storeDefaults(SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        for (int i = 0; i < DEFAULT_PAIRS.size(); ++i)
            editor.putInt(DEFAULT_PAIRS.get(i), i);
        editor.apply();
    }
}
