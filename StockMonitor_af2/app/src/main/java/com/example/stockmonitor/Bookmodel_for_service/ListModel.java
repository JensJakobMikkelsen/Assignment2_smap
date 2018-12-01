package com.example.stockmonitor.Bookmodel_for_service;

import android.app.Application;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Entity(tableName = "ListModel_table")
public class ListModel implements Parcelable {


        @PrimaryKey(autoGenerate = true)
        private int id;

        @ColumnInfo(name = "name")
        private String name;

        @ColumnInfo(name = "price")
        private String price;

        @ColumnInfo(name = "c_price")
        private String c_price;

        @ColumnInfo(name = "sector")
        private String sector;

        @ColumnInfo(name = "amount")
        private String amount;

        @ColumnInfo(name = "original_price")
        public double original_price;

        @ColumnInfo(name = "timeStamp")
        private String timeStamp;

        @ColumnInfo(name = "bought_for")
        public double bought_for;

       @ColumnInfo(name = "primaryExchange")
        private String primary_exchange;

       @ColumnInfo(name = "checkBoxChecked")
        public int checkBoxChecked = 5;

       @ColumnInfo(name = "symbol")
        private String symbol;

    @ColumnInfo(name = "companyName")
        private String companyName;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public double getBought_for() {
        return bought_for;
    }

    public void setBought_for(double bought_for) {
        this.bought_for = bought_for;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getSector() {
        return sector;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setcheckBoxChecked(int checkBoxChecked) {
        this.checkBoxChecked = checkBoxChecked;
    }

    public int getcheckBoxChecked() {
        return checkBoxChecked;
    }

    public ListModel()
        {
            Long tsLong = System.currentTimeMillis()/1000;
            timeStamp = getDateCurrentTimeZone(tsLong);
        }

    //Fra stackOverflow

    public String getDateCurrentTimeZone(long timestamp) {
        try{
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getTimeZone("Denmark");
            calendar.setTimeInMillis(timestamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currentTimeZone = (Date) calendar.getTime();
            return sdf.format(currentTimeZone);
        }catch (Exception e) {
        }
        return "";
    }


    public ListModel(String name, String price_, String c_price, String primary_exchange, String amount) {
            this.name = name;
            this.price = price_;
            this.c_price = c_price;
            this.primary_exchange = primary_exchange;
            this.amount = amount;

            Long tsLong = System.currentTimeMillis()/1000;
            timeStamp = getDateCurrentTimeZone(tsLong);

        }

    public void setOriginal_price(double original_price) {
        this.original_price = original_price;
    }

    public double getOriginal_price() {
        return original_price;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setPrice(String price_) {

        this.price = price_;
    }
    public String getPrice() {
        return price;
    }
    public void setC_price(String c_price) {
        this.c_price = c_price;
    }
    public String getC_price() {
        return c_price;
    }
    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
    public String getTimeStamp() {
        return timeStamp;
    }
    public String getPrimary_exchange() {
        return primary_exchange;
    }
    public void setPrimary_exchange(String primary_exchange) {
        this.primary_exchange = primary_exchange;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAmount() {
        return amount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.name);
        out.writeString(this.price);
        out.writeString(this.c_price);

        out.writeString(this.amount);
        out.writeDouble(this.original_price);

        out.writeString(this.timeStamp);
        out.writeString(this.primary_exchange);
        out.writeInt(this.checkBoxChecked);

    }

    protected ListModel (Parcel in)
    {
        this.name = in.readString();
        this.price = in.readString();
        this.c_price = in.readString();

        this.amount = in.readString();
        this.original_price = in.readDouble();

        this.timeStamp = in.readString();
        this.primary_exchange = in.readString();
        this.checkBoxChecked = in.readInt();

    }

    public static final Parcelable.Creator<ListModel> CREATOR = new Parcelable.Creator<ListModel>() {
        public ListModel createFromParcel(Parcel in) {
            return new ListModel(in);
        }
        public ListModel[] newArray(int size) {
            return new ListModel[size];
        }
    };
}