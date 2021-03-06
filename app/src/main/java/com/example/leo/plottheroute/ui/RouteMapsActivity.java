package com.example.leo.plottheroute.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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
import com.example.leo.plottheroute.data.LruCache;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class RouteMapsActivity extends FragmentActivity implements OnMapReadyCallback,
        RoutePresenter.RouteView, View.OnClickListener {

    private GoogleMap mMap;
    private Geocoder mGeocoder;
    List<Address> mAddressList;

    public final static String POSITION = "POSITION";

    private EditText startPoint;
    private EditText endPoint;
    private Marker startMarker;
    private Marker endMarker;
    private boolean mLocationPermissionGranted;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private RoutePresenter mRoutePresenter;
    private int buttonID;

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
        mRoutePresenter = new RoutePresenter(this, this);
        mGeocoder = new Geocoder(this, Locale.getDefault());

        startPoint = findViewById(R.id.startPoint);
        endPoint = findViewById(R.id.endPoint);
        Button btnShowRoute = findViewById(R.id.btnPlotRoute);
        Button btnCenterRoute = findViewById(R.id.btnCenterRoute);
        Button btnPointsListOne = findViewById(R.id.btnPointsListOne);
        Button btnPointsListTwo = findViewById(R.id.btnPointsListTwo);

        startPoint.setShowSoftInputOnFocus(false);
        endPoint.setShowSoftInputOnFocus(false);

        btnShowRoute.setOnClickListener(this);
        btnCenterRoute.setOnClickListener(this);
        btnPointsListOne.setOnClickListener(this);
        btnPointsListTwo.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mMap != null) {
            initMap();
        }
        mRoutePresenter.getPrevPositions();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mRoutePresenter.savePositions();
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
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        initMap();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                mRoutePresenter.addPositionToCache(latLng);

                if (startPoint.isFocused()) {
                    if (startMarker != null) {
                        startMarker.remove();
                    }
                    startMarker = mMap.addMarker(new MarkerOptions().position(latLng));
                    startPoint.setText(getAddress(latLng));
                }
                if (endPoint.isFocused()) {
                    if (endMarker != null) {
                        endMarker.remove();
                    }
                    endMarker = mMap.addMarker(new MarkerOptions().position(latLng));
                    endPoint.setText(getAddress(latLng));
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
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
        }
    }

    private String getAddress(LatLng latLng) {
        String address;

        try {
            mAddressList = mGeocoder.getFromLocation(
                    latLng.latitude,
                    latLng.longitude,
                    1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mAddressList != null && mAddressList.size() != 0) {
            address = mAddressList.get(0).getAddressLine(0);
        } else {
            address = "Error: No Address Found";
        }
        return address;
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

    @Override
    public void startPointListActivity(int id) {
        buttonID = id;
        Intent intent = new Intent(this, PointsListActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            int position;
            switch (buttonID) {
                case R.id.btnPointsListOne:
                    position = data.getIntExtra(POSITION, -1);
                    if (position != -1) {
                        if (startMarker != null) {
                            startMarker.remove();
                        }
                        startMarker = mMap.addMarker(
                                new MarkerOptions().position(LruCache.getInstance()
                                        .getAllPoints().get(position)));
                        startPoint.setText(getAddress(startMarker.getPosition()));
                    }
                    break;

                case R.id.btnPointsListTwo:
                    position = data.getIntExtra(POSITION, -1);
                    if (position != -1) {
                        if (endMarker != null) {
                            endMarker.remove();
                        }
                        endMarker = mMap.addMarker(
                                new MarkerOptions().position(LruCache.getInstance()
                                        .getAllPoints().get(position)));
                        endPoint.setText(getAddress(endMarker.getPosition()));
                    }
                    break;
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPlotRoute:
                if (startMarker != null && endMarker != null) {
                    mRoutePresenter.requestRoute(startMarker.getPosition(),
                            endMarker.getPosition());
                }
                break;

            case R.id.btnCenterRoute:
                mRoutePresenter.centerRoute();
                break;

            case R.id.btnPointsListOne:
                mRoutePresenter.showAddressListActivity(R.id.btnPointsListOne);
                break;

            case R.id.btnPointsListTwo:
                mRoutePresenter.showAddressListActivity(R.id.btnPointsListTwo);
                break;
        }
    }
}
