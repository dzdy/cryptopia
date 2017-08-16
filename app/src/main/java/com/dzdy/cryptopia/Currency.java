package com.dzdy.cryptopia;

import java.util.ArrayList;
import java.util.List;

class Currency {
    String name;
    String symbol;
    List<Currency> baseCurrencies;

    Currency(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
        this.baseCurrencies = new ArrayList<>();
    }

    void addBaseCurrency(Currency newCurrency) {
        baseCurrencies.add(newCurrency);
    }

    @Override
    public String toString() {
        return name;
    }
}
