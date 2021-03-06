package com.sam_chordas.android.stockhawk.service;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.LinkedHashMap;


public class HistoryData implements Parcelable {
    private final static String TAG = HistoryData.class.getSimpleName();
    private float mMinPrice;
    private float mMaxPrice;
    private ArrayList<HistoryItem> mChartEntries;

    public HistoryData() {
        mChartEntries = new ArrayList<>();
    }

    public ArrayList<HistoryItem> getItems() {
        return mChartEntries;
    }

    public void addEntry(HistoryItem item) {
        mChartEntries.add(item);
    }

    private HistoryItem getItem(int index) {
        return mChartEntries.get(index);
    }

    public void setMinPrice(float price) {
        mMinPrice = price;
    }

    public float getMinPrice() {
        return mMinPrice;
    }

    public void setMaxPrice(float price) {
        mMaxPrice = price;
    }

    public float getMaxPrice() {
        return mMaxPrice;
    }

    public void addFormattedLabels(ChartLabel labelSet, boolean exactMatch) {
        for (LinkedHashMap.Entry<String, String> entry : labelSet.getEntrySet()) {
            String key = entry.getKey();
            String label = entry.getValue();
            int index = findMatchingTimestamp(key, exactMatch);
            if (index != -1) {
                getItem(index).setLabel(label);
            }
        }
    }

    private int findMatchingTimestamp(String key, boolean findInRange) {
        int index = -1;
        for (int i=0; i < mChartEntries.size(); i++) {
            HistoryItem item = mChartEntries.get(i);
            String timestamp = item.getTimeStamp();
            if (timestamp.equals(key)) {
                index = i;
                break;
            }
            // dont look for timestamp in range if key is date string that is likely to have exact match
            if (findInRange && timeStampInRange(key, timestamp)) {
                index = i;
                break;
            }
        }
        return index;
    }

    // find first matching timestamp in 1 hour range
    private boolean timeStampInRange(String keyString, String timeStampString) {
        long key = Long.parseLong(keyString);
        long timestamp = Long.parseLong(timeStampString);
        long max = timestamp + 3600;
        return inRange(key, timestamp, max);
    }

    private boolean inRange(long x, long min, long max) {
        return x >= min && x <= max;
    }

    private HistoryData(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(mMinPrice);
        dest.writeFloat(mMaxPrice);
        dest.writeList(mChartEntries);
    }

    private void readFromParcel(Parcel in) {
        mMinPrice = in.readFloat();
        mMaxPrice = in.readFloat();
        mChartEntries = in.readArrayList(HistoryItem.class.getClassLoader());
    }

    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public HistoryData createFromParcel(Parcel in) {
                    return new HistoryData(in);
                }
                public HistoryData[] newArray(int size) {
                    return new HistoryData[size];
                }
            };
}
