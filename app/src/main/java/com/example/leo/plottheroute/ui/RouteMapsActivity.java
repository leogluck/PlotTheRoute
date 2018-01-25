package com.example.leo.plottheroute.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.leo.plottheroute.R;
import com.example.leo.plottheroute.presenter.RoutePresenter;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class RouteMapsActivity extends FragmentActivity implements OnMapReadyCallback,
        RoutePresenter.RouteView {

    private GoogleMap mMap;
    private LatLng mLocation;

    private EditText startPoint;
    private EditText endPoint;
    private Button btnShowRoute;
    private Button btnCenterRoute;
    private Button btnPointsListOne;
    private Button btnPointsListTwo;
    private Marker startMarker;
    private Marker endMarker;
    private boolean mLocationPermissionGranted;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private RoutePresenter mRoutePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_maps);

        getLocationPermission();

        init();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void init() {

        mLocation = new LatLng(50.431782, 30.516382);
        mRoutePresenter = new RoutePresenter(this, this);

        startPoint = findViewById(R.id.startPoint);
        endPoint = findViewById(R.id.endPoint);
        btnShowRoute = findViewById(R.id.btnPlotRoute);
        btnCenterRoute = findViewById(R.id.btnCenterRoute);
        btnPointsListOne = findViewById(R.id.btnPointsListOne);
        btnPointsListTwo = findViewById(R.id.btnPointsListTwo);

        startPoint.setShowSoftInputOnFocus(false);
        endPoint.setShowSoftInputOnFocus(false);

        btnShowRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (startMarker != null && endMarker != null) {
                    mRoutePresenter.requestRoute(startMarker.getPosition(),
                            endMarker.getPosition());
                }
            }
        });

        btnCenterRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRoutePresenter.centerRoute();
            }
        });

        btnPointsListOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRoutePresenter.showAddressListActivity();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mMap != null) {
            initMap();
        }
        mRoutePresenter.readSharedPrefs();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mRoutePresenter.saveSharedPrefs();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
            @NonNull String permissions[],
            @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    initMap();
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                mRoutePresenter.addLruCache(latLng);

                if (startPoint.isFocused()) {
                    if (startMarker != null) {
                        startMarker.remove();
                    }
                    startMarker = mMap.addMarker(new MarkerOptions().position(latLng));
                    startPoint.setText(latLng.toString());
                }
                if (endPoint.isFocused()) {
                    if (endMarker != null) {
                        endMarker.remove();
                    }
                    endMarker = mMap.addMarker(new MarkerOptions().position(latLng));
                    endPoint.setText(latLng.toString());
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void initMap() {
        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            LocationManager locationManager = (LocationManager) getSystemService(
                    Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            Location location = locationManager.getLastKnownLocation(
                    locationManager.getBestProvider(criteria, false));
            mLocation = new LatLng(location.getLatitude(), location.getLongitude());
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLocation, 11));
    }

    private void getLocationPermission() {

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void showRoute(List<LatLng> decodedPath, CameraUpdate cu) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(startMarker.getPosition()));
        mMap.addMarker(new MarkerOptions().position(endMarker.getPosition()));
        mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
        mMap.animateCamera(cu);
    }

    @Override
    public void centerRoute(CameraUpdate cu) {
        mMap.animateCamera(cu);
    }
}