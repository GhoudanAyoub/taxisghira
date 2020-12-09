package com.TaxiSghira.TreeProg.plashscreen.Client;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.TaxiSghira.TreeProg.plashscreen.Authentication.PersonalInfo;
import com.TaxiSghira.TreeProg.plashscreen.Callback.IFirebaseDriverInfoListener;
import com.TaxiSghira.TreeProg.plashscreen.Callback.IFirebaseFailedListener;
import com.TaxiSghira.TreeProg.plashscreen.Commun.Common;
import com.TaxiSghira.TreeProg.plashscreen.Module.Chifor;
import com.TaxiSghira.TreeProg.plashscreen.Module.Client;
import com.TaxiSghira.TreeProg.plashscreen.Module.Demande;
import com.TaxiSghira.TreeProg.plashscreen.Module.DriverGeoModel;
import com.TaxiSghira.TreeProg.plashscreen.Module.GeoQueryModel;
import com.TaxiSghira.TreeProg.plashscreen.Module.Pickup;
import com.TaxiSghira.TreeProg.plashscreen.Module.UserLocation;
import com.TaxiSghira.TreeProg.plashscreen.R;
import com.TaxiSghira.TreeProg.plashscreen.Service.LocationServiceUpdate;
import com.TaxiSghira.TreeProg.plashscreen.di.FireBaseClient;
import com.TaxiSghira.TreeProg.plashscreen.ui.MapViewModel;
import com.TaxiSghira.TreeProg.plashscreen.ui.Util_List;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.rxbinding3.view.RxView;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

@AndroidEntryPoint
public class Map extends AppCompatActivity
        implements OnMapReadyCallback, MapboxMap.OnMapClickListener, PermissionsListener,
        IFirebaseDriverInfoListener, IFirebaseFailedListener, LocationListener {

    @BindView(R.id.textView5)
    TextView WelcomeText;
    @BindView(R.id.textView)
    TextView ComingFrom;
    @BindView(R.id.textView2)
    TextView GoingTO;
    @BindView(R.id.list_Taxi_num)
    TextView ListTaxiNum;
    @BindView(R.id.list_Ch_Name)
    TextView ListChName;
    @BindView(R.id.list_Ch_num)
    TextView ListChNum;
    @BindView(R.id.editText2)
    TextInputLayout WhereToGo;
    @BindView(R.id.findDriver2)
    Button findDriver2;
    @BindView(R.id.DeleteDemand)
    Button DeleteDemand;
    @BindView(R.id.layout_location_display_info)
    LinearLayout layout_location_display_info;
    @BindView(R.id.layout_driver_display_info)
    LinearLayout layout_driver_display_info;
    @BindView(R.id.listAnim)
    FloatingActionButton listAnim;


    MapView mapView;
    public static String id;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    private DirectionsRoute currentRoute;
    private LocationManager manager;
    private NavigationMapRoute navigationMapRoute;
    private boolean mLocationPermissionGranted = false;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Client Current_Client;
    private Demande d1;
    MapViewModel mapViewModel;
    private  LocationRequest locationRequest;

    //online System
    private DatabaseReference ClientLocationRef, driver_location_ref;
    private GeoFire geoFire;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback locationCallback;
    private double distances = 1000.0;
    private double LIMIT_RANGE = 10.0;
    private Location previousLocation, CurrentLocation;
    private Boolean firstTime = true;
    private String cityName, bestProvider;
    public Criteria criteria;

    //Listeners
    IFirebaseDriverInfoListener iFirebaseDriverInfoListener;
    IFirebaseFailedListener iFirebaseFailedListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoidGhlc2hhZG93MiIsImEiOiJjazk5YWNzczYwMjJ2M2VvMGttZHRrajFuIn0.evtApMiwXCmCfyw5qUDT5Q");
        setContentView(R.layout.app_bar_map);
        FirebaseApp.initializeApp(getApplicationContext());
        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);

        views();
        CheckMyData();
        //Log.d("FIREBASE", refreshedToken);
        if (isMapsEnabled()) {
            init();
        } else {
            buildAlertMessageNoGps();
        }
        mapViewModel.getClientInfo();
        mapViewModel.GetPickDemand();
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

    }

    private void CheckMyData() {
        mapViewModel.getClientMutableLiveData().observe(this,client -> {
            if (client == null)
                buildAlertMessageNoDataFound();
            else
                Current_Client = client;
        });
    }

    private void views() {
        ButterKnife.bind(this, findViewById(android.R.id.content));
        Common.SetWelcomeMessage(WelcomeText);
        mapView = findViewById(R.id.mapView);
        findViewById(R.id.progBar).setVisibility(View.GONE);
        findDriver2.setVisibility(View.GONE);
        layout_driver_display_info.setVisibility(View.GONE);
        layout_location_display_info.setVisibility(View.GONE);
        DeleteDemand.setVisibility(View.GONE);
        listAnim.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Util_List.class)));

        manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        bestProvider = String.valueOf(manager.getBestProvider(criteria, true));
    }

    private void init() {
        iFirebaseDriverInfoListener = this;
        iFirebaseFailedListener = this;

        BuildLocationRequest();
        BuildLocationCallBack();
        UpdateLocation();
        
        loadAvailableDrivers();
    }

    private void UpdateLocation() {
        if (mFusedLocationClient == null){
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }
    }

    private void BuildLocationCallBack() {
        if (locationCallback == null){
            locationCallback = new LocationCallback() {
                @SuppressLint("CheckResult")
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    Location location = locationResult.getLastLocation();

                    if (location != null) {
                        Common.SetWelcomeMessage(WelcomeText);
                        startService(new Intent(getApplicationContext(), LocationServiceUpdate.class));
                        UploadLocation(location);
                        //Find Button For Lunch Search Request
                        RxView.clicks(findViewById(R.id.FindButton))
                                .throttleFirst(5, TimeUnit.SECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(unit -> {
                                    Snackbar.make(findViewById(android.R.id.content), getString(R.string.lookingForBestRoute), Snackbar.LENGTH_SHORT).show();
                                    Point destinationPoint = null;
                                    try {
                                        final Geocoder geocoder = new Geocoder(getApplicationContext());
                                        final String locName = Objects.requireNonNull(WhereToGo.getEditText()).getText().toString();
                                        final List<Address> list = geocoder.getFromLocationName(locName, 1);
                                        if (!(list == null || list.isEmpty())) {
                                            final Address address = list.get(0);
                                            destinationPoint = Point.fromLngLat(address.getLongitude(), address.getLatitude());
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    Point originPoint = null;
                                    try {
                                        assert locationComponent.getLastKnownLocation() != null;
                                        originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                                                locationComponent.getLastKnownLocation().getLatitude());
                                    } catch (Exception e) {
                                        Timber.e(e);
                                        startActivity(new Intent(getApplicationContext(), Map.class));
                                    }
                                    //destination point
                                    GeoJsonSource dest = Objects.requireNonNull(mapboxMap.getStyle()).getSourceAs("destination-source-id");
                                    if (dest != null) {
                                        dest.setGeoJson(Feature.fromGeometry(destinationPoint));
                                    }
                                    //location point
                                    GeoJsonSource origin = Objects.requireNonNull(mapboxMap.getStyle()).getSourceAs("destination-origin-id");
                                    if (origin != null) {
                                        origin.setGeoJson(Feature.fromGeometry(originPoint));
                                    }
                                    try {
                                        getRoute(originPoint, destinationPoint, location);
                                    } catch (Exception e) {
                                        Timber.e(e);
                                    }
                                }, Throwable::printStackTrace);
                    }
                    //if user Has Change Location Cal and Load Driver Again
                    if (firstTime) {
                        previousLocation = CurrentLocation = locationResult.getLastLocation();
                        firstTime = false;
                    } else {
                        previousLocation = CurrentLocation;
                        CurrentLocation = locationResult.getLastLocation();
                    }
                    if (previousLocation.distanceTo(CurrentLocation) / 1000 < LIMIT_RANGE)
                        loadAvailableDrivers();
                }
            };
        }
    }

    private void BuildLocationRequest() {
        if (locationRequest == null) {
            locationRequest = new LocationRequest();
            locationRequest.setSmallestDisplacement(10f);
            locationRequest.setInterval(5000);
            locationRequest.setFastestInterval(3000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
    }

    private void loadAvailableDrivers() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Timber.e(getString(R.string.permission_not_granted));
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnFailureListener(Throwable::printStackTrace)
                .addOnSuccessListener(location -> {
                    //load All Drivers in City
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    List<Address> addressList;
                    try {
                        addressList = geocoder.getFromLocation(location.getLatitude()
                                , location.getLongitude(), 1);
                        cityName = addressList.get(0).getLocality();

                        //query
                        driver_location_ref = FirebaseDatabase.getInstance()
                                .getReference(Common.Drivers_LOCATION_REFERENCES)
                                .child(cityName);
                        GeoFire geoFire = new GeoFire(driver_location_ref);
                        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(location.getLatitude(), location.getLongitude()), distances);
                        geoQuery.removeAllListeners();

                        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                            @Override
                            public void onKeyEntered(String key, GeoLocation location) {
                                //Common.driversFound.add(new DriverGeoModel(key, location));
                                if (!Common.driversFound.containsKey(key))
                                    Common.driversFound.put(key, new DriverGeoModel(key, location));
                            }

                            @Override
                            public void onKeyExited(String key) {

                            }

                            @Override
                            public void onKeyMoved(String key, GeoLocation location) {

                            }

                            @Override
                            public void onGeoQueryReady() {
                                if (distances <= LIMIT_RANGE) {
                                    distances++;
                                    loadAvailableDrivers();
                                } else {
                                    distances = 1000.0;
                                    addDriverMarker();
                                }
                            }

                            @Override
                            public void onGeoQueryError(DatabaseError error) {
                                Timber.e(error.getMessage());
                            }
                        });

                        //listen to new Driver In Range
                        driver_location_ref.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                if (snapshot.hasChildren()) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        GeoQueryModel geoQueryModel = dataSnapshot.getValue(GeoQueryModel.class);
                                        assert geoQueryModel != null;
                                        GeoLocation geoLocation = new GeoLocation(geoQueryModel.getL().get(0), geoQueryModel.getL().get(1));
                                        DriverGeoModel driverGeoModel = new DriverGeoModel(dataSnapshot.getKey(), geoLocation);
                                        Location newDriverLocation = new Location("");
                                        newDriverLocation.setLatitude(geoLocation.latitude);
                                        newDriverLocation.setLongitude(geoLocation.longitude);
                                        float newDistance = location.distanceTo(newDriverLocation) / 1000;
                                        if (newDistance <= LIMIT_RANGE)
                                            FindDriversByID(driverGeoModel);
                                    }
                                }
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void addDriverMarker() {
        if (Common.driversFound.size() > 0) {
            Observable.fromIterable(Common.driversFound.keySet())
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(key -> FindDriversByID(Objects.requireNonNull(Common.driversFound.get(key)))
                            , throwable -> Snackbar.make(findViewById(android.R.id.content), Objects.requireNonNull(throwable.getMessage()), Snackbar.LENGTH_SHORT).show()
                            , () -> Timber.e(getString(R.string.gotSomeDriverInfor)));
        } else {
            Snackbar.make(findViewById(android.R.id.content), getString(R.string.driver_not_Found), Snackbar.LENGTH_SHORT).show();
        }
    }

    private void FindDriversByID(DriverGeoModel driverGeoModel) {
        FireBaseClient.getFireBaseClient().getFirebaseDatabase()
                .getReference(Common.Chifor_DataBase_Table)
                .orderByChild("id")
                .equalTo(driverGeoModel.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                driverGeoModel.setChifor(dataSnapshot.getValue(Chifor.class));
                                Common.driversFound.get(driverGeoModel.getKey()).setChifor(dataSnapshot.getValue(Chifor.class));
                                iFirebaseDriverInfoListener.onDriverInfoLoadSuccess(driverGeoModel);
                            }
                        } else
                            iFirebaseFailedListener.onFirebaseLoadFailed(getString(R.string.not_found_key));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        iFirebaseFailedListener.onFirebaseLoadFailed(error.getMessage());
                    }
                });
    }

    private void init(Location locationComponent) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(locationComponent.getLatitude(), locationComponent.getLongitude(), 1);
            String city = addressList.get(0).getLocality();
            ClientLocationRef = FireBaseClient.getFireBaseClient().getDatabaseReference().child(Common.CLIENT_LOCATION_REFERENCES)
                    .child(city);
        } catch (IOException e) {
            e.printStackTrace();
        }
        geoFire = new GeoFire(ClientLocationRef);
    }

    private void buildAlertMessageNoDataFound() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.persnalDataRequest))
                .setIcon(R.drawable.ic_account_circle_black)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.okey), (dialog, which) -> startActivity(new Intent(getApplicationContext(), PersonalInfo.class)));
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled() {
        manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return false;
        }
        return true;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_gps_off_black_24dp)
                .setMessage(getString(R.string.ActivatGpsRequest))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.okey), (dialog, id) -> startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 333));
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    334);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!mLocationPermissionGranted) {
            getLocationPermission();
        }
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
            enableLocationComponent(style);
            addDestinationIconSymbolLayer(style);
            mapboxMap.addOnMapClickListener(Map.this);

        });

        //get current Location animation
        findViewById(R.id.floatingActionButton).setOnClickListener(t -> {
            assert locationComponent.getLastKnownLocation() != null;
            try {
                CameraPosition position = new CameraPosition
                        .Builder()
                        .target(new LatLng(locationComponent.getLastKnownLocation().getLatitude(),
                                locationComponent.getLastKnownLocation().getLongitude()))
                        .zoom(17)
                        .bearing(180).tilt(30)
                        .build();
                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //Move Location
        BuildLocationRequest();
        BuildLocationCallBack();
        UpdateLocation();

        //getAcceptData
        mapViewModel.getAcceptMutableLiveData().observe(Map.this, this::ShowDriverDashboard);
    }

    @SuppressLint("CheckResult")
    private void ShowDriverDashboard(Pickup pickup1) {
        try {
            Timber.tag("wtf").e("Inside");
            findViewById(R.id.progBar).setVisibility(View.GONE);
            layout_location_display_info.setVisibility(View.GONE);
            layout_driver_display_info.setVisibility(View.VISIBLE);
            ListTaxiNum.setText(pickup1.getChifor().getTaxi_NUM());
            ListChName.setText(pickup1.getChifor().getFullname());
            ListChNum.setText(pickup1.getChifor().getPhone());
            RxView.clicks(findViewById(R.id.Favories))
                    .throttleFirst(5, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(unit -> {
                                mapViewModel.InsertData(pickup1.getChifor());
                                Toast.makeText(getApplicationContext(), getString(R.string.AddedToFavor), Toast.LENGTH_SHORT).show();
                            }
                            , Throwable::printStackTrace);
            RxView.clicks(findViewById(R.id.calls))
                    .throttleFirst(5, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(unit -> startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse(pickup1.getChifor().getPhone())))
                            , Throwable::printStackTrace);
        } catch (Throwable t) {
            Timber.e(t);
        }
    }

    private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage("destination-icon-id", BitmapFactory.decodeResource(this.getResources(), R.drawable.map_marker_dark));
        loadedMapStyle.addSource(new GeoJsonSource("destination-source-id"));
        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(iconImage("destination-icon-id"), iconIgnorePlacement(true));
        loadedMapStyle.addLayer(destinationSymbolLayer);
    }

    @SuppressWarnings({"MissingPermission"})
    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(point.getLatitude(), point.getLongitude(), 1);
            if (addressList.size() > 0) {
                Objects.requireNonNull(WhereToGo.getEditText()).setText(addressList.get(0).getAddressLine(0));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void getRoute(Point origin, Point destination, Location location) {
        try {
            assert Mapbox.getAccessToken() != null;
            NavigationRoute.builder(this)
                    .accessToken(Mapbox.getAccessToken())
                    .origin(origin)
                    .destination(destination)
                    .build()
                    .getRoute(new Callback<DirectionsResponse>() {
                        @SuppressLint("CheckResult")
                        @Override
                        public void onResponse(@NotNull Call<DirectionsResponse> call, @NotNull Response<DirectionsResponse> response) {
                            Completable.timer(500,
                                    TimeUnit.MILLISECONDS,
                                    AndroidSchedulers.mainThread())
                                    .subscribe(() -> buildAlertMessageSearchOperation(location));
                            if (response.body() == null) {
                                Toast.makeText(getApplicationContext(), getString(R.string.NoRouteWasFound), Toast.LENGTH_SHORT).show();
                                return;
                            } else if (response.body().routes().size() < 1) {
                                Toast.makeText(getApplicationContext(), getString(R.string.NoRouteWasFound), Toast.LENGTH_SHORT).show();
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
                        public void onFailure(@NotNull Call<DirectionsResponse> call, @NotNull Throwable throwable) {
                            Timber.e(throwable);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("CheckResult")
    private void buildAlertMessageSearchOperation(@NotNull Location location) {
        layout_location_display_info.setVisibility(View.VISIBLE);
        findDriver2.setVisibility(View.VISIBLE);
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> currentAddress = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (currentAddress.size() > 0) {
                ComingFrom.setText(currentAddress.get(0).getAddressLine(0));
                GoingTO.setText(Objects.requireNonNull(WhereToGo.getEditText()).getText());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        UserLocation userLocation = new UserLocation(location.getLatitude(), location.getLongitude());
        d1 = new Demande(Common.Current_Client_Id, Common.Current_Client_DispalyName, Objects.requireNonNull(WhereToGo.getEditText()).getText().toString(),
                Current_Client.getCity(), userLocation.getLnt(), userLocation.getLong());

        RxView.clicks(findDriver2).
                throttleFirst(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(unit -> {
                            mapViewModel.AddDemand(d1);
                            findDriver2.setVisibility(View.GONE);
                            DeleteDemand.setVisibility(View.VISIBLE);
                            findViewById(R.id.progBar).setVisibility(View.VISIBLE);
                        }
                        , Throwable::printStackTrace);
        RxView.clicks(DeleteDemand)
                .throttleFirst(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(unit -> RemoveDemand(d1), Throwable::printStackTrace);
    }

    private void UploadLocation(Location locationComponent) {
        //updateLocation
        if (locationComponent != null) {
            init(locationComponent);
            geoFire.setLocation(Common.Current_Client_Id,
                    new GeoLocation(locationComponent.getLatitude(),
                            locationComponent.getLongitude()),
                    (key, error) -> {
                        if (error != null)
                            Timber.e(error.getMessage());
                        else
                            Snackbar.make(findViewById(android.R.id.content), getString(R.string.Looking), Snackbar.LENGTH_LONG);
                    });
        }
    }

    public void RemoveDemand(Demande demande) {
        findViewById(R.id.progBar).setVisibility(View.GONE);
        FirebaseDatabase.getInstance().getReference(Common.Demande_DataBase_Table)
                .child(demande.getCity())
                .orderByChild("clientId")
                .equalTo(Common.Current_Client_Id)
                .removeEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                                dataSnapshot1.getRef().removeValue();
                            layout_location_display_info.setVisibility(View.GONE);
                            Snackbar.make(findViewById(android.R.id.content), R.string.you_canceled_your_demand, Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Timber.e(databaseError.getMessage());
                    }
                });
    }

    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions
                            .builder(this, loadedMapStyle)
                            .useDefaultLocationEngine(true)
                            .locationEngineRequest(new LocationEngineRequest.Builder(750)
                                    .setFastestInterval(750)
                                    .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                                    .build())
                            .build());
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
                return;
            }
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING_GPS);
            locationComponent.setRenderMode(RenderMode.COMPASS);

            LocationComponentOptions locationComponentOptions =
                    LocationComponentOptions.builder(this)
                            .trackingGesturesManagement(true)
                            .bearingTintColor(Color.YELLOW)
                            .accuracyAlpha(.4f)
                            .build();
            LocationComponentActivationOptions locationComponentActivationOptions = LocationComponentActivationOptions
                    .builder(this, loadedMapStyle)
                    .locationComponentOptions(locationComponentOptions)
                    .build();

            locationComponent.activateLocationComponent(locationComponentActivationOptions);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionGranted = false;
        if (requestCode == 334) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            }
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(getApplicationContext(), R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent(Objects.requireNonNull(mapboxMap.getStyle()));
        } else {
            Toast.makeText(getApplicationContext(), R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
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
        stopService(new Intent(getApplicationContext(), LocationServiceUpdate.class));
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        mFusedLocationClient.removeLocationUpdates(locationCallback);
        stopService(new Intent(getApplicationContext(), LocationServiceUpdate.class));
        geoFire.removeLocation(Common.Current_Client_Id);
        compositeDisposable.clear();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDriverInfoLoadSuccess(DriverGeoModel driverGeoModel) {
        if (!Common.marerList.containsKey(driverGeoModel.getKey()))
            Common.marerList.put(driverGeoModel.getKey(),
                    mapboxMap.addMarker(new MarkerOptions()
                            .position(new LatLng(driverGeoModel.getGeoLocation().latitude,
                                    driverGeoModel.getGeoLocation().longitude))
                            .title(driverGeoModel.getChifor().getFullname())
                            .snippet(driverGeoModel.getChifor().getPhone())
                            .icon(IconFactory.getInstance(Map.this).fromResource(R.drawable.car2))));
        if (!TextUtils.isEmpty(cityName)) {
            DatabaseReference driverLocation = FireBaseClient.getFireBaseClient()
                    .getDatabaseReference()
                    .child(Common.Drivers_LOCATION_REFERENCES)
                    .child(cityName)
                    .child(driverGeoModel.getKey());
            driverLocation.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.hasChildren()) {
                        if (Common.marerList.get(driverGeoModel.getKey()) != null)
                            Common.marerList.get(driverGeoModel.getKey()).remove();
                        Common.marerList.remove(driverGeoModel.getKey());
                        driverLocation.removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Timber.e(error.getMessage());
                }
            });
        }

    }

    @Override
    public void onFirebaseLoadFailed(String message) {
        Timber.e(message);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        //remove location callback:
        manager.removeUpdates(this);
    }
}