package com.ks.crowdcontrol.view.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ks.crowdcontrol.MainActivity;
import com.ks.crowdcontrol.R;
import com.ks.crowdcontrol.database.SupermarketDTO;
import com.ks.crowdcontrol.view.main.adapter.SupermarketAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static android.content.Context.LOCATION_SERVICE;


public class Fragment_1_Map extends Fragment implements SupermarketAdapter.SuperMarketListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraIdleListener {
    private static final String TAG = "Fragment_Map";
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 100;

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    private List<SupermarketDTO> supermarketDTOList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextView supermarketCountView;
    private SupermarketAdapter supermarketAdapter;

    private Map<String, Marker> markerMap;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap map;
    private boolean hasLocationPermission;
    private Location location;
    private EnumMap<SupermarketDTO.Type, BitmapDescriptor> markerIconMap;

    private MapView mapView;


    /**
     * Asks for the needed permission on start, otherwise we cant access the location of the user.
     * Is needed to show nearby orders.
     */
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getContext()));
        //Checks if the App has the needed permission to check the location
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            hasLocationPermission = true;
        } else {
            // Need to ask user for permission
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_1_map, container, false);
        Button btnNavFrag1 = view.findViewById(R.id.btnNavFrag1);
        recyclerView = view.findViewById(R.id.supermarket_recycler_view);
        supermarketCountView = view.findViewById(R.id.supermarket_count_text);

        Log.d(TAG, "onCreateView: started.");

        btnNavFrag1.setOnClickListener(view1 -> {
            Toast.makeText(getActivity(), "Going to Home", Toast.LENGTH_SHORT).show();
            ((MainActivity) Objects.requireNonNull(getActivity())).setViewPager(0);
        });

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mapView = view.findViewById(R.id.mapView3);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        initView();
        initializeMarker();
        if (hasLocationPermission) {
            requestCurrentLocation();
        }
        updateMarkers();
        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    public void onCameraIdle() {

    }


    private void initView() {


        //End Test
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), R.drawable.divider_horizontal));
        supermarketAdapter = new SupermarketAdapter(recyclerView.getContext(), (ArrayList<SupermarketDTO>) this.supermarketDTOList, this.location, this);
        recyclerView.setAdapter(supermarketAdapter);
        supermarketAdapter.notifyDataSetChanged();
    }

    private void initData() {
        supermarketDTOList.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        db.collection("supermarkets")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Log.wtf("fb", document.getId() + " => " + document.getData());
                            SupermarketDTO supermarketDTOTemp = new SupermarketDTO(document);
                            supermarketDTOList.add(supermarketDTOTemp);
                        }
                    } else {
                        Log.wtf("fb", "Error getting documents.", task.getException());
                    }
                    supermarketAdapter.notifyDataSetChanged();
                    if (supermarketDTOList.size() == 1)
                        supermarketCountView.setText("Es wurde " + supermarketDTOList.size() + "Supermarkt in deiner Nähe gefunden!");
                    else
                        supermarketCountView.setText("Es wurden " + supermarketDTOList.size() + "Supermärkte in deiner Nähe gefunden!");
                    updateMarkers();
                });
    }

    @Override
    public void onSupermarketClicked(String supermarketID) {
        SupermarketDTO supermarket = null;
        for(SupermarketDTO temp : supermarketDTOList){
            if(temp.getId().equals(supermarketID)){
                supermarket = temp;
            }
        }
        ((MainActivity) Objects.requireNonNull(getActivity())).setViewPager(3, supermarket);
    }


    /**
     * Request the last known location and zoom the map to that point.
     */
    private void requestCurrentLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    this.location = location;
                    sortDataByDistance(getMyLocation());
                    supermarketAdapter.notifyDataSetChanged();
                    Log.d("LOG_TAG", "Last known location: " + location);
                    if (location == null || map == null) {
                        return;
                    }
                    LatLng llPos = new LatLng(location.getLatitude(), location.getLongitude());
                    float zoom = 14f;
                    if (location.getAccuracy() > 10000) {
                        zoom = 5f;
                    } else if (location.getAccuracy() > 1000) {
                        zoom = 10f;
                    }
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(llPos, zoom));
                });
    }

    /**
     * Sorts List, so that the nearest Orders are on top.
     */
    private void sortDataByDistance(Location myLocation) {
        Collections.sort(this.supermarketDTOList, (o1, o2) -> {
            Location location1 = new Location("");
            location1.setLatitude(o1.getLatitude());
            location1.setLongitude(o1.getLongitude());
            Location location2 = new Location("");
            location2.setLatitude(o2.getLatitude());
            location2.setLongitude(o2.getLongitude());
            if (myLocation != null) {
                double distance1 = myLocation.distanceTo(location1);
                double distance2 = myLocation.distanceTo(location2);
                if (distance1 == distance2)
                    return 0;
                return distance1 < distance2 ? -1 : 1;
            }
            return 0; //doesn't compare in case we can't get our own position
        });
        supermarketAdapter.notifyDataSetChanged();
    }


    private Location getMyLocation() {
        LocationManager locationManager = (LocationManager) Objects.requireNonNull(getContext()).getSystemService(LOCATION_SERVICE);
        assert locationManager != null;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Ask for permission
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
            return null;
        }

        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    /**
     * Once the map is ready, it will jump to the current location.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.wtf("onMapReady", "map is ready");
        map = googleMap;
        map.setMinZoomPreference(12);
        LatLng ny = new LatLng(49.0024841, 12.081699);
        map.moveCamera(CameraUpdateFactory.newLatLng(ny));
        map.setOnMarkerClickListener(this);
        Log.wtf("supermarketList Size", String.valueOf(supermarketDTOList.size()));
        for (SupermarketDTO supermarketDTO : supermarketDTOList) {
            Marker marker = markerMap.get(supermarketDTO.getId());
            if (marker == null) {
                // Add new marker
                marker = map.addMarker(new MarkerOptions()
                        .flat(true)
                        .draggable(false)
                        .anchor(0.171875f, 0.9375f)
                        .title(String.valueOf(supermarketDTO.getListId()))
                        .position(new LatLng(supermarketDTO.getLatitude(), supermarketDTO.getLongitude()))
                );
                if (supermarketDTO.getType() == SupermarketDTO.Type.GROCERIES) {
                    //Google Marker
                    //marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    //Custom Marker
                    marker.setIcon(DrawableUtil.getBitmapDescriptor(getContext(), supermarketDTO.getType().getIcon(), supermarketDTO));
                } else {
                    //marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    marker.setIcon(DrawableUtil.getBitmapDescriptor(getContext(), supermarketDTO.getType().getIcon(), supermarketDTO));
                }
            }
            initializeMarker();
            updateMarkers();
        }
        /*
        map = googleMap;
        map.setMyLocationEnabled(hasLocationPermission);
        int paddingTop = getResources().getDimensionPixelSize(R.dimen.status_bar_height);
        map.setPadding(0, paddingTop, 0, 0);
        map.setOnMarkerClickListener(this);
        initializeMarker();
        if (hasLocationPermission) {
            requestCurrentLocation();
        }
        updateMarkers();
        map.setOnCameraIdleListener(this);
        */

    }


    /**
     * Initializes the markers used in the map.
     * <p>
     * <b>Do not call this method before the map is ready!</b>
     *
     * @see #onMapReady(GoogleMap)
     */
    private void initializeMarker() {
        Log.wtf("initializeMarker", "init");
        MapsInitializer.initialize(Objects.requireNonNull(getContext()));
        markerMap = new HashMap<>();
        markerIconMap = new EnumMap<>(SupermarketDTO.Type.class);
    }

    /**
     * Updates all markers on the map. Call this method when the orders have changed.
     */
    private void updateMarkers() {
        Log.wtf("updateMarkers", "update");

        if (map == null) {
            return;
        }
        //BitmapDescriptor descriptor = DrawableUtil.getBitmapDescriptor(this, urgency.getIconRes());
        //                        .icon(markerIconMap.get(order.getUrgency()))
        Set<String> oldOrderIds = new HashSet<>(markerMap.keySet());
        for (SupermarketDTO supermarketDTO : supermarketDTOList) {
            Marker marker = markerMap.get(supermarketDTO.getId());
            if (marker == null) {
                // Add new marker
                marker = map.addMarker(new MarkerOptions()
                        .flat(true)
                        .draggable(false)
                        .anchor(0.171875f, 0.9375f)
                        .title(String.valueOf(supermarketDTO.getListId()))
                        .position(new LatLng(supermarketDTO.getLatitude(), supermarketDTO.getLongitude()))
                );
                if (supermarketDTO.getType() == SupermarketDTO.Type.GROCERIES) {
                    //Google Marker
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    //Custom Marker
                    //marker.setIcon(DrawableUtil.getBitmapDescriptor(getContext(), supermarketDTO.getType().getIcon(), supermarketDTO));
                }
                marker.setTag(supermarketDTO.getId());
                markerMap.put(supermarketDTO.getId(), marker);
            } else {
                // Update existing marker
                marker.setPosition(new LatLng(supermarketDTO.getLatitude(), supermarketDTO.getLongitude()));
            }
            // Order is not old, no need to remove marker
            oldOrderIds.remove(supermarketDTO.getId());
        }
        // Remove old markers
        for (String id : oldOrderIds) {
            Marker marker = markerMap.remove(id);
            if (marker != null) {
                marker.remove();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }
        mapView.onSaveInstanceState(mapViewBundle);
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        updateMarkers();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String supermarketID = (String) marker.getTag();
        onSupermarketClicked(supermarketID);
        return true;
    }
}