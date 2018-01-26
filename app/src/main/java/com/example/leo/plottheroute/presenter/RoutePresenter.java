package com.example.leo.plottheroute.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.example.leo.plottheroute.R;
import com.example.leo.plottheroute.data.LatLngMapper;
import com.example.leo.plottheroute.data.LruCache;
import com.example.leo.plottheroute.data.SharedPrefsManager;
import com.example.leo.plottheroute.model.Northeast;
import com.example.leo.plottheroute.model.Route;
import com.example.leo.plottheroute.model.RouteDetails;
import com.example.leo.plottheroute.model.Southwest;
import com.example.leo.plottheroute.network.RouteAPI;
import com.example.leo.plottheroute.network.RouteService;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.PolyUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Leo on 24.01.2018
 */

public class RoutePresenter {

    private RouteView mRouteView;
    private Context mContext;
    private List<Route> mRoutes;
    private LatLngMapper mLatLngMapper;


    public RoutePresenter(Context context, RouteView routeView) {
        this.mContext = context;
        this.mRouteView = routeView;
        mLatLngMapper = new LatLngMapper();
    }

    public void centerRoute() {
        LatLngBounds bounds = getLatLngBounds(mRoutes);
        int padding = 100;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        mRouteView.centerRoute(cu);
    }

    public void requestRoute(LatLng startPoint, LatLng endPoint) {
        RouteService routeService = RouteAPI.getClient().create(RouteService.class);

        Call<RouteDetails> call = routeService.getDirection(latLngConverting(startPoint),
                latLngConverting(endPoint),
                mContext.getString(R.string.google_maps_key));

        call.enqueue(new Callback<RouteDetails>() {
            @Override
            public void onResponse(Call<RouteDetails> call,
                    Response<RouteDetails> response) {
                mRoutes = response.body().getRoutes();

                if (!mRoutes.isEmpty()) {
                    List<LatLng> decodedPath = getDecodedPath(mRoutes);
                    LatLngBounds bounds = getLatLngBounds(mRoutes);
                    int padding = 100;
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    mRouteView.showRoute(decodedPath, cu);
                }
            }

            @Override
            public void onFailure(Call<RouteDetails> call, Throwable t) {
                Log.d("DEBUG", t.getMessage());
            }
        });
    }

    public void getPrevPositions() {
        String points = SharedPrefsManager.getInstance().getPrefs(mContext);
        if (!TextUtils.isEmpty(points)) {
            List<LatLng> latLngList = mLatLngMapper.mapString(points);
            LruCache.getInstance().setLruCashe(latLngList);
        }
    }

    public void addPositionToCache(LatLng latLng) {
        LruCache.getInstance().add(latLng);
    }

    public void savePositions() {
        String points = mLatLngMapper.mapLatLng(LruCache.getInstance().getAllPoints());
        SharedPrefsManager.getInstance().savePrefs(mContext, points);
    }

    public void showAddressListActivity(int id) {
        mRouteView.startPointListActivity(id);
    }

    private String latLngConverting(LatLng latLng) {
        String mapPoint = Double.toString(latLng.latitude) + "," + Double.toString(
                latLng.longitude);
        return mapPoint;
    }

    @NonNull
    private LatLngBounds getLatLngBounds(List<Route> routes) {
        Northeast ne = routes.get(0).getBounds().getNortheast();
        Southwest sw = routes.get(0).getBounds().getSouthwest();
        LatLng neLatLng = new LatLng(ne.getLat(), ne.getLng());
        LatLng swLatLng = new LatLng(sw.getLat(), sw.getLng());
        return new LatLngBounds(swLatLng, neLatLng);
    }

    private List<LatLng> getDecodedPath(List<Route> routes) {
        return PolyUtil.decode(routes
                .get(0).getOverviewPolyline().getPoints());
    }

    public interface RouteView {

        void showRoute(List<LatLng> decodedPath, CameraUpdate cu);

        void centerRoute(CameraUpdate cu);

        void startPointListActivity(int id);
    }
}


