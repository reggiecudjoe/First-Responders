package com.pubnub.example.android.datastream.mapexample.pubnubandroidmap;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.common.base.Throwables;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Sim2 extends AppCompatActivity implements OnMapReadyCallback {
    public static final String TAG = Sim2.class.getName();

    public static final String DATASTREAM_PREFS = "com.pubnub.example.android.datastream.mapexample.DATASTREAM_PREFS";
    public static final String DATASTREAM_UUID = "com.pubnub.example.android.datastream.mapexample.DATASTREAM_UUID";

    public static final String PUBLISH_KEY = "pub-c-06ffc4af-724e-4f80-a295-e3224d244e32";
    public static final String SUBSCRIBE_KEY = "sub-c-76162f2a-ab2b-11e7-b4e4-2675c721e615";
    public static final String CHANNEL_NAME = "maps-channel";

    private GoogleMap mMap;

    private PubNub mPubNub;

    private SharedPreferences mSharedPrefs;

    private Marker mMarker;
    private Marker truckMarker;
    private Polyline mPolyline;

    private List<LatLng> mPoints = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPrefs = getSharedPreferences(DATASTREAM_PREFS, MODE_PRIVATE);

        setContentView(R.layout.sim2);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initPubNub();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(17.0f);
        LatLng focus = new LatLng(38.897347,-77.011232);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(focus));
    }

    private final void initPubNub() {
        PNConfiguration config = new PNConfiguration();

        config.setPublishKey(PUBLISH_KEY);
        config.setSubscribeKey(SUBSCRIBE_KEY);
        config.setSecure(true);

        this.mPubNub = new PubNub(config);

        this.mPubNub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                // no status handler for simplicity
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                try {
                    Log.v(TAG, JsonUtil.asJson(message));

                    Map<String, String> map = JsonUtil.convert(message.getMessage(), LinkedHashMap.class);
                    String lat = map.get("lat");
                    String lng = map.get("lng");
                    String truckLat = map.get("truckLat");
                    String truckLng = map.get("truckLng");

                    updateLocation(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)));
                    updateTruck(new LatLng(Double.parseDouble(truckLat), Double.parseDouble(truckLng)));

                } catch (Exception e) {
                    throw Throwables.propagate(e);
                }
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {
                // no presence handler for simplicity
            }
        });


        this.mPubNub.subscribe().channels(Arrays.asList(CHANNEL_NAME)).execute();
    }

    private void updateLocation(final LatLng location) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPoints.add(location);

                if (Sim2.this.mMarker != null) {
                    Sim2.this.mMarker.setPosition(location);
                } else {
                    Sim2.this.mMarker = mMap.addMarker(new MarkerOptions()
                            .position(location)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car)));
                }

            }
        });
    }

    private void updateTruck(final LatLng location) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPoints.add(location);

                if (Sim2.this.truckMarker != null) {
                    Sim2.this.truckMarker.setPosition(location);
                } else {
                    Sim2.this.truckMarker = mMap.addMarker(new MarkerOptions()
                            .position(location)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_truck)));
                }
              mMap.moveCamera(CameraUpdateFactory.newLatLng(location));



            }
        });
    }
}
