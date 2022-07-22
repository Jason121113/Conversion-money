package com.example.exchange;

public class Transaction {

    private String reference = "", type = "", user = "", remark = "", recepientid = "", recepientname = "", date = "", amount = "", timestamp = "";
//    private Long timestamp;

    public Transaction(String reference, String type, String user, String remark, String amount, String date, String timestamp){
        this.reference = reference;
        this.type = type;
        this.user = user;
        this.remark = remark;
        this.amount = amount;
        this.date = date;
        this.timestamp = timestamp;
    }

    public Transaction() {

    }

    public String getReference()
    {
        return reference;
    }
    public void setReference(String reference)
    {
        this.reference = reference;
    }

    public String getType()
    {
        return type;
    }
    public void setType(String type)
    {
        this.type = type;
    }

    public String getUser()
    {
        return user;
    }
    public void setUser(String user)
    {
        this.user = user;
    }

    public String getRemark()
    {
        return remark;
    }
    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getAmount()
    {
        return amount;
    }
    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getTimestamp()
    {
        return timestamp;
    }
    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getDate()
    {
        return date;
    }
    public void setDate(String date)
    {
        this.date = date;
    }


}
