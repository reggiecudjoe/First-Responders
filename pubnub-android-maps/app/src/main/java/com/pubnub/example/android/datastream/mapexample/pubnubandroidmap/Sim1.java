package com.pubnub.example.android.datastream.mapexample.pubnubandroidmap;

/**
 * Created by Reggie on 10/7/2017.
 */

import com.google.android.gms.maps.model.Marker;
import com.pubnub.api.callbacks.SubscribeCallback;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

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

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class Sim1 extends AppCompatActivity implements OnMapReadyCallback {
    public static final String TAG = Sim1.class.getName();

    public static final String DATASTREAM_PREFS = "com.pubnub.example.android.datastream.mapexample.DATASTREAM_PREFS";
    public static final String DATASTREAM_UUID = "com.pubnub.example.android.datastream.mapexample.DATASTREAM_UUID";

    public static final String PUBLISH_KEY = "pub-c-06ffc4af-724e-4f80-a295-e3224d244e32";
    public static final String SUBSCRIBE_KEY = "sub-c-76162f2a-ab2b-11e7-b4e4-2675c721e615";
    public static final String CHANNEL_NAME = "maps-channel";

    private GoogleMap mMap;

    private PubNub mPubNub;

    private SharedPreferences mSharedPrefs;

    private Marker mMarker;
    private Marker wat;

    private List<LatLng> mPoints = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPrefs = getSharedPreferences(DATASTREAM_PREFS, MODE_PRIVATE);

        setContentView(R.layout.sim1);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initPubNub();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(17.0f);
        LatLng focus = new LatLng(38.896520,-77.009087);
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
                    String watson = map.get("watson");
                    updateLocation(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)), Double.parseDouble(watson));

//                    if (Double.parseDouble(watson) == 999){
//                        Sim1.this.wat = mMap.addMarker(new MarkerOptions()
//                                .position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)))
//                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_alert)));
//                    }

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

    private void updateLocation(final LatLng location, final double watty) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPoints.add(location);

                    if (watty == 999){
                        Sim1.this.wat = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(38.896520+.00050, -77.009087))
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_alert)));
                    }
                if (Sim1.this.mMarker != null) {
                    Sim1.this.mMarker.setPosition(location);
                } else {
                    Sim1.this.mMarker = mMap.addMarker(new MarkerOptions()
                            .position(location)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car)));
                }

                mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
            }
        });
    }


}
