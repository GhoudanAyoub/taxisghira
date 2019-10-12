package com.TaxiSghira.TreeProg.plashscreen.Client;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.TaxiSghira.TreeProg.plashscreen.Both.PersonalInfo;
import com.TaxiSghira.TreeProg.plashscreen.Both.PutPhone;
import com.TaxiSghira.TreeProg.plashscreen.Module.Accept;
import com.TaxiSghira.TreeProg.plashscreen.Module.Chifor;
import com.TaxiSghira.TreeProg.plashscreen.Operation.Op;
import com.TaxiSghira.TreeProg.plashscreen.Profile.Util_List;
import com.TaxiSghira.TreeProg.plashscreen.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillExtrusionHeight;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

public class Map extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnMapClickListener, PermissionsListener {


    public static String id;
    DatabaseReference databaseReference,databaseReference2,databaseReference3,databaseReference4,databaseReference5;
    TextView Ch_Name, TaxiNum, Ch_Num;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    private DirectionsRoute currentRoute;
    private NavigationMapRoute navigationMapRoute;
    private EditText WhereToGo;
    AlertDialog.Builder builder;
    private ProgressDialog gProgress;
    public Accept accept;
    Chifor chifor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "sk.eyJ1IjoidGhlc2hhZG93MiIsImEiOiJjanpjbzBkOGQwN2NwM2VxZ3hsd2k4YzZiIn0.4uzrxcklTabdmuS3RN9Stw");
        setContentView(R.layout.app_bar_map);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        findViewById(R.id.UtilTAxi).setVisibility(View.GONE);
        findViewById(R.id.textViewtad).setVisibility(View.GONE);
        findViewById(R.id.listAnim).setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), Util_List.class)));


        gProgress = new ProgressDialog(this);
        builder = new AlertDialog.Builder(this);
        WhereToGo = findViewById(R.id.editText2);
        databaseReference = FirebaseDatabase.getInstance().getReference("Favor");
        databaseReference2 = FirebaseDatabase.getInstance().getReference("Demande");
        databaseReference3 = FirebaseDatabase.getInstance().getReference("Accept");
        databaseReference4 = FirebaseDatabase.getInstance().getReference("Chifor");
        databaseReference5 = FirebaseDatabase.getInstance().getReference("Client");
        databaseReference4.keepSynced(true);
        databaseReference3.keepSynced(true);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        Ch_Name = findViewById(R.id.list_Ch_Name);
        Ch_Num = findViewById(R.id.list_Ch_num);
        TaxiNum = findViewById(R.id.list_Taxi_num);

        databaseReference5.orderByChild("gmail").equalTo(PutPhone.gmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) { }
                    else {
                    builder.setIcon(R.drawable.ic_account_circle_black);
                    builder.setTitle("المعلومات الشخصية");
                    builder.setMessage("المرجو ملأ معلوماتكم الشخصية");
                    builder.setPositiveButton("حسنا", (dialog, which) -> startActivity(new Intent(getApplicationContext(), PersonalInfo.class)));
                    builder.setNegativeButton("",null);
                    builder.show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference3.orderByChild("ClientName").equalTo(PutPhone.name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                        accept = dataSnapshot1.getValue(Accept.class);
                        try {
                            TaxiNum.setText(accept.Taxi_num);
                            Ch_Name.setText(accept.Ch_Name);
                            Ch_Num.setText(accept.Ch_num);
                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                    findViewById(R.id.UtilTAxi).setVisibility(View.VISIBLE);
                    findViewById(R.id.textViewtad).setVisibility(View.VISIBLE);
                    findViewById(R.id.Favories).setOnClickListener(v -> Op.AddFAvor(databaseReference, PutPhone.gmail, accept.Ch_Name, accept.Ch_num,accept.Taxi_num));
                findViewById(R.id.Rate).setOnClickListener(v->{
                    LayoutInflater inflater = getLayoutInflater();
                    builder.setIcon(R.drawable.ic_gesture_black_24dp);
                    builder.setTitle("التقييم");
                    View dialogLayout = inflater.inflate(R.layout.alert_dialog_with_ratingbar, null);
                    final RatingBar ratingBar = dialogLayout.findViewById(R.id.ratingBar);
                    builder.setView(dialogLayout);
                    builder.setPositiveButton("OK", (dialogInterface, i) -> Toast.makeText(getApplicationContext(), "شكرا لتقيمكم", Toast.LENGTH_SHORT).show());
                    builder.show();
                });}
                    else{findViewById(R.id.UtilTAxi).setVisibility(View.GONE);
                    findViewById(R.id.textViewtad).setVisibility(View.GONE); }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    @Override
    public void onBackPressed() { }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        findViewById(R.id.returnAnim).setOnClickListener(v -> mapboxMap.getLocationComponent());
        mapboxMap.setStyle(getString(R.string.navigation_guidance_day), style -> {
            enableLocationComponent(style);
            addDestinationIconSymbolLayer(style);
            mapboxMap.addOnMapClickListener(Map.this);


            IconFactory iconFactory = IconFactory.getInstance(Map.this);
            Icon icon = iconFactory.fromResource(R.drawable.taxisymb);
            mapboxMap.addOnCameraMoveStartedListener(reason -> databaseReference4.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            chifor = dataSnapshot1.getValue(Chifor.class);
                        }
                        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(chifor.getLant(), chifor.getLong())).icon(icon));
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            }));

            findViewById(R.id.FindButton).setOnClickListener(v -> {

                Point destinationPoint = null;
                Toast.makeText(getApplicationContext(), "المرجو الانتظار جاري البحت عن طريق مناسب", Toast.LENGTH_SHORT).show();
                try {
                    final Geocoder geocoder = new Geocoder(getApplicationContext());
                    final String locName = WhereToGo.getText().toString();
                    //**************************
                    final List<Address> list = geocoder.getFromLocationName(locName, 1);
                    if (!(list == null || list.isEmpty())) {
                        final Address adress = list.get(0);
                        destinationPoint = Point.fromLngLat(adress.getLongitude(), adress.getLatitude());
                    } else {
                        System.out.println("Geocode backend not present");
                    }
                    //****************************************
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Point originPoint = null;
                try {
                    originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                            locationComponent.getLastKnownLocation().getLatitude());
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Check Your Connection Internet And Try again", Toast.LENGTH_SHORT);
                    startActivity(new Intent(getApplicationContext(), Map.class));
                }
                GeoJsonSource source = mapboxMap.getStyle().getSourceAs("destination-source-id");
                if (source != null) {
                    source.setGeoJson(Feature.fromGeometry(destinationPoint));
                }

                getRoute(originPoint, destinationPoint);

            });
        });
    }

    private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage("destination-icon-id", BitmapFactory.decodeResource(this.getResources(), R.drawable.map_marker_light));
        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
        loadedMapStyle.addSource(geoJsonSource);
        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(
                iconImage("destination-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        );
        loadedMapStyle.addLayer(destinationSymbolLayer);
    }

    @SuppressWarnings({"MissingPermission"})
    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        return true;
    }

    private void getRoute(Point origin, Point destination) {
        try {
            NavigationRoute.builder(this)
                    .accessToken(Mapbox.getAccessToken())
                    .origin(origin)
                    .destination(destination)
                    .build()
                    .getRoute(new Callback<DirectionsResponse>() {
                        @Override
                        public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                            Toast.makeText(getApplicationContext(), "Route Generated :)", Toast.LENGTH_SHORT).show();

                            new Handler().postDelayed(()->{
                                builder.setIcon(R.drawable.ic_search_black_24dp);
                                builder.setTitle("عملية البحت");
                                builder.setMessage("هل تريد بدء عملبة البحت عن طاكسي?");
                                builder.setPositiveButton("نعم", (dialog, which) -> {
                                    gProgress.setMessage("المرجو الانتظار قليلا ⌛️");
                                    Op.AddDemande(databaseReference2, PutPhone.name,WhereToGo.getText().toString(),locationComponent.getLastKnownLocation().getLatitude(),locationComponent.getLastKnownLocation().getLongitude());

                                    //waitiing room !!!!!!!!!
                                    builder.setIcon(R.drawable.ic_search_black_24dp);
                                    builder.setTitle("عملية البحت");
                                    builder.setMessage("بدأت عملية البحت عن طاكسي\uD83D\uDE04\uD83D\uDE04");
                                    builder.setNegativeButton("حسنا",(dialog2,which2)-> databaseReference3.orderByChild("ClientName").equalTo(PutPhone.name).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()){
                                                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                                                    accept = dataSnapshot1.getValue(Accept.class);
                                                }
                                                TaxiNum.setText(accept.Taxi_num);
                                                Ch_Name.setText(accept.Ch_Name);
                                                Ch_Num.setText(accept.Ch_num);
                                                findViewById(R.id.UtilTAxi).setVisibility(View.VISIBLE);
                                                findViewById(R.id.textViewtad).setVisibility(View.VISIBLE);
                                                findViewById(R.id.Favories).setOnClickListener(v -> Op.AddFAvor(databaseReference, PutPhone.gmail, accept.Ch_Name, accept.Ch_num,accept.Taxi_num));
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                                    }));
                                    builder.setPositiveButton("رفض",(dialog1,which1)-> databaseReference2.orderByChild("ClientName").equalTo(PutPhone.name).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()){
                                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                        ds.getRef().removeValue();
                                                    }}
                                                }
                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                }
                                            }));
                                    new Handler().postDelayed(()-> builder.show(),2000);
                                });
                                builder.setNegativeButton("لا",null);
                                builder.show();
                            },2000);


                            if (response.body() == null) {
                                Toast.makeText(getApplicationContext(), "No routes found, make sure you set the right user and access token.", Toast.LENGTH_SHORT).show();
                                return;
                            } else if (response.body().routes().size() < 1) {
                                Toast.makeText(getApplicationContext(), "No routes found", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            currentRoute = response.body().routes().get(0);
                            if (navigationMapRoute != null) {
                                navigationMapRoute.removeRoute();
                            } else {
                                navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                            }
                            navigationMapRoute.addRoute(currentRoute);

                        }

                        @Override
                        public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                            Toast.makeText(getApplicationContext(), "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "المرجو تشغيل GPS", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(this, loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent(Objects.requireNonNull(mapboxMap.getStyle()));
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}