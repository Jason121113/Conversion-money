package com.example.exchange;

public class fundspinner {
    private String name;
    private  String fund;
    private String fundName;
    private String currencyValue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrencyValue() {
        return currencyValue;
    }

    public void setCurrencyValue(String currencyValue) {
        this.currencyValue = currencyValue;
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public String getValueAmount() {
        return fund;
    }

    public void setValueAmount(String fund) {
        this.fund = fund;
    }
    @Override
    public String toString() {
        return name;
    }
}
