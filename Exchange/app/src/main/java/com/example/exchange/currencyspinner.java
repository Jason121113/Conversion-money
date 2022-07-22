package com.example.exchange;

public class currencyspinner {
    private String name;

    private  String fund;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValueAmount(String fund) {
        this.fund = fund;
    }

    public String getValueAmount() {
        return fund;
    }


    @Override
    public String toString() {
        return name;
    }


}
