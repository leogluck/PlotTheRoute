package com.example.leo.plottheroute.data;

import com.google.android.gms.maps.model.LatLng;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Leo on 25.01.2018
 */

public class LruCache {
    private List<LatLng> mList;

    public LruCache() {
        mList = new LinkedList<>();
    }

    public LruCache(List<LatLng> pointsList) {
        mList = pointsList;
    }

    public void add(LatLng latLng) {
        if (mList.size() == 10) {
            mList.remove(9);
        }
        mList.add(latLng);
    }

    public LatLng getPoint(int index) {
        return mList.get(index);
    }

    public List<LatLng> getAllPoints() {
        return mList;
    }
}
