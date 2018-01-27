package com.example.leo.plottheroute.data;

import com.google.android.gms.maps.model.LatLng;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Leo on 25.01.2018
 */

public class LruCache {
    private static final LruCache lruCache = new LruCache();
    private List<LatLng> mList = new LinkedList<>();

    private LruCache() {
    }

    public static LruCache getInstance() {
        return lruCache;
    }

    public void setLruCashe(List<LatLng> pointsList) {
        mList = pointsList;
    }

    public void add(LatLng latLng) {
        if (mList.size() == 10) {
            mList.remove(0);
        }
        mList.add(latLng);
    }

    public List<LatLng> getAllPoints() {
        return mList;
    }
}
