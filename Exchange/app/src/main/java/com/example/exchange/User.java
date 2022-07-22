package com.example.exchange;

import com.google.firebase.firestore.DocumentSnapshot;

public class User {

    private String name = "",username = "",email = "",password = "",number = "",id = "",profile = "default", fundsPeso = "", fundsYuan = "", fundsEuro = "", fundsDollar = "";

    public User(String name, String username, String email, String password, String number, String id, String date, String profile, String fundsPeso, String fundsDollar, String fundsYuan, String fundsEuro){
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.number = number;
        this.id = id;
        this.profile = profile;
        this.fundsPeso = fundsPeso;
        this.fundsDollar = fundsDollar;
        this.fundsYuan = fundsYuan;
        this.fundsEuro = fundsEuro;
    }

    public User() {

    }

    public void parseData(DocumentSnapshot dataSnapshot) {
        id = dataSnapshot.getId();
        if (dataSnapshot.get("name") != null) {
            name = dataSnapshot.get("name").toString();
        }
        if (dataSnapshot.get("cellphone") != null) {
            number = dataSnapshot.get("cellphone").toString();
        }
        if (dataSnapshot.get("email") != null) {
            email = dataSnapshot.get("email").toString();
        }
        if (dataSnapshot.get("username") != null) {
            username = dataSnapshot.get("username").toString();
        }
        if (dataSnapshot.get("profile") != null) {
            profile = dataSnapshot.get("profile").toString();
        }
    }

    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getEmail()
    {
        return email;
    }
    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPassword()
    {
        return password;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getUsername()
    {
        return username;
    }
    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getCellphone()
    {
        return number;
    }
    public void setCellphone(String number)
    {
        this.number = number;
    }

    public String getID()
    {
        return id;
    }
    public void setID(String id)
    {
        this.id = id;
    }

    public String getProfile()
    {
        return profile;
    }
    public void setProfile(String profile)
    {
        this.profile = profile;
    }

    public String getFundsPeso()
    {
        return fundsPeso;
    }
    public void setFundsPeso(String fundsPeso)
    {
        this.fundsPeso = fundsPeso;
    }

    public String getFundsYuan()
    {
        return fundsYuan;
    }
    public void setFundsYuan(String fundsYuan)
    {
        this.fundsYuan = fundsYuan;
    }

    public String getFundsEuro()
    {
        return fundsEuro;
    }
    public void setFundsEuro(String fundsEuro)
    {
        this.fundsEuro = fundsEuro;
    }

    public String getFundsDollar()
    {
        return fundsDollar;
    }
    public void setFundsDollar(String fundsDollar)
    {
        this.fundsDollar = fundsDollar;
    }
}
