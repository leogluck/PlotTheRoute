package com.example.leo.plottheroute.data;

import com.google.android.gms.maps.model.LatLng;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Leo on 25.01.2018
 */

public class LatLngMapper {

    public String mapLatLng(List<LatLng> pointsList) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < pointsList.size(); i++) {
            sb.append(pointsList.get(i).latitude);
            sb.append(",");
            sb.append(pointsList.get(i).longitude);
            if (i != pointsList.size() - 1) {
                sb.append(";");
            }
        }

        return sb.toString();
    }

    public List<LatLng> mapString(String points) {
        List<LatLng> pointsList = new LinkedList<>();

        String[] pointsArray = points.split(";");
        for (String pts : pointsArray) {
            String[] pointsLatLng = pts.split(",");
            LatLng latLng = new LatLng(Double.valueOf(pointsLatLng[0]),
                    Double.valueOf(pointsLatLng[1].toString()));
            pointsList.add(latLng);
        }

        return pointsList;
    }
}
