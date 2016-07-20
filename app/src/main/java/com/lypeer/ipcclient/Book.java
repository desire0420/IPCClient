package com.lypeer.ipcclient;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lypeer on 2016/7/16.
 */
public class Book implements Parcelable{

    private String name;

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private int price;

    public Book(){}

    public Book(Parcel in) {
        name = in.readString();
        price = in.readInt();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(price);
    }

    public void readFromParcel(Parcel dest) {
        setName(dest.readString());
        setPrice(dest.readInt());
    }

    @Override
    public String toString() {
        return "name : " + getName() + " , price : " + getPrice();
    }
}
