package com.dzdy.cryptopia;

class Pair {
    private String name;
    private Double askPrice;

    Pair(String name, Double askPrice) {
        this.name = name;
        this.askPrice = askPrice;
    }

    String getName() {
        return name;
    }

    Double getAskPrice() {
        return askPrice;
    }

    public static String pairName(String base, String curr) {
        return curr + "_" + base;
    }
}
