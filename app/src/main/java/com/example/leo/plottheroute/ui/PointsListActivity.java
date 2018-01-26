package com.example.leo.plottheroute.ui;

import static com.example.leo.plottheroute.ui.RouteMapsActivity.POSITION;

import android.content.Intent;
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

import java.util.List;

public class PointsListActivity extends AppCompatActivity {

    private RecyclerView mPointsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points_list);

        mPointsListView = findViewById(R.id.pointsList);
        mPointsListView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mPointsListView.setLayoutManager(layoutManager);
        showPoints(LruCache.getInstance().getAllPoints());
    }

    public void showPoints(List<LatLng> points) {
        RecyclerView.Adapter adapter = new PointsListAdapter(points);
        mPointsListView.setAdapter(adapter);
    }

    class PointsListAdapter extends RecyclerView.Adapter<PointsListAdapter.ViewHolder> {
        private List<LatLng> mPoints;

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView mLat;
            TextView mLng;

            ViewHolder(View view) {
                super(view);
                mLat = view.findViewById(R.id.latitude);
                mLng = view.findViewById(R.id.longitude);

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
            holder.mLat.setText(Double.toString(mPoints.get(position).latitude));
            holder.mLng.setText(Double.toString(mPoints.get(position).longitude));
        }

        @Override
        public int getItemCount() {
            return mPoints.size();
        }
    }
}
