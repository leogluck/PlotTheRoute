package com.example.leo.plottheroute.ui;

import static com.example.leo.plottheroute.ui.RouteMapsActivity.POSITION;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.leo.plottheroute.R;
import com.example.leo.plottheroute.data.LruCache;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PointsListActivity extends AppCompatActivity {

    private RecyclerView mPointsListView;
    private Geocoder mGeocoder;
    private List<Address> mAddressList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points_list);

        mPointsListView = findViewById(R.id.pointsList);
        mPointsListView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mPointsListView.setLayoutManager(layoutManager);
        mGeocoder = new Geocoder(this, Locale.getDefault());

        showPoints(LruCache.getInstance().getAllPoints());
    }

    public void showPoints(List<LatLng> points) {
        RecyclerView.Adapter adapter = new PointsListAdapter(points);
        mPointsListView.setAdapter(adapter);
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

    class PointsListAdapter extends RecyclerView.Adapter<PointsListAdapter.ViewHolder> {
        private List<LatLng> mPoints;

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView mItemKey;
            TextView mAddress;

            ViewHolder(View view) {
                super(view);
                mItemKey = view.findViewById(R.id.item_key);
                mAddress = view.findViewById(R.id.address);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int pos = getAdapterPosition();
                        Intent intent = new Intent();
                        intent.putExtra(POSITION, pos);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
            }
        }

        private String getItemKey(int position) {
            int addressNumber = ++position;
            return "Address " + addressNumber + ":";
        }

        PointsListAdapter(List<LatLng> points) {
            mPoints = points;
        }

        @Override
        public PointsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                int viewType) {
            View pointsView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.points_list_layout, parent, false);
            return new ViewHolder(pointsView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mItemKey.setText(getItemKey(position));
            holder.mAddress.setText(getAddress(mPoints.get(position)));
        }

        @Override
        public int getItemCount() {
            return mPoints.size();
        }
    }
}
