package com.TaxiSghira.TreeProg.plashscreen.ui;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.TaxiSghira.TreeProg.plashscreen.Module.DriverGeoModel;
import com.TaxiSghira.TreeProg.plashscreen.Module.EventBus.DeclineRequestAndRemoveTripFromDriver;
import com.TaxiSghira.TreeProg.plashscreen.Module.EventBus.DeclineRequestFromDriver;
import com.TaxiSghira.TreeProg.plashscreen.Module.EventBus.DriverAcceptTripEvent;
import com.TaxiSghira.TreeProg.plashscreen.Module.EventBus.DriverCompleteTrip;
import com.TaxiSghira.TreeProg.plashscreen.Module.GeoQueryModel;
import com.TaxiSghira.TreeProg.plashscreen.Module.Trip;
import com.TaxiSghira.TreeProg.plashscreen.R;
import com.TaxiSghira.TreeProg.plashscreen.Room.FireBaseClient;
import com.TaxiSghira.TreeProg.plashscreen.Service.LocationServiceUpdate;
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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
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
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.hilt.android.AndroidEntryPoint;
import es.dmoral.toasty.Toasty;
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
@SuppressLint("NonConstantResourceId")
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
    @BindView(R.id.BottomContainerHolder)
    LinearLayout BottomContainerHolder;
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
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Client Current_Client;
    MapViewModel mapViewModel;
    private LocationRequest locationRequest;

    //online System
    private DatabaseReference ClientLocationRef, driver_location_ref;
    private GeoFire geoFire;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback locationCallback;
    private double distances = 1000.0;
    private final double LIMIT_RANGE = 10.0;
    private Location previousLocation, CurrentLocation, MyLocation;
    private Boolean firstTime = true;
    private String cityName;

    //Listeners
    IFirebaseDriverInfoListener iFirebaseDriverInfoListener;
    IFirebaseFailedListener iFirebaseFailedListener;
    private DriverGeoModel LastDriverCall;
    private LatLng Destination_point;
    private Trip pickup12;
    private Marker DistinationMarker, OriginMarker;
    private Handler hendler;
    private float v;
    private double lat, lng;
    private int index, next;
    private LatLng start, end;
    private String DriverOldLocation;
    private boolean isNextLaunch = false;


    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoidGhlc2hhZG93MiIsImEiOiJjazk5YWNzczYwMjJ2M2VvMGttZHRrajFuIn0.evtApMiwXCmCfyw5qUDT5Q");
        setContentView(R.layout.app_bar_map);

        if (FirebaseInstanceId.getInstance().getToken() != null)
            UserUtils.UpdateToken(this, FirebaseInstanceId.getInstance().getToken());
        FirebaseApp.initializeApp(getApplicationContext());
        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);

        views();
        mapViewModel.getClientInfo();
        CheckMyData();

        if (isMapsEnabled()) {
            init();
        } else {
            buildAlertMessageNoGps();
        }
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        RxView.clicks(findDriver2).
                throttleFirst(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(unit -> {
                    FindNearByDrivers(MyLocation);
                    findDriver2.setVisibility(View.GONE);
                    DeleteDemand.setVisibility(View.VISIBLE);
                    findViewById(R.id.progBar).setVisibility(View.VISIBLE);
                }, Throwable::printStackTrace);
        RxView.clicks(DeleteDemand)
                .throttleFirst(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(unit -> {
                    findViewById(R.id.progBar).setVisibility(View.GONE);
                    navigationMapRoute.removeRoute();
                    layout_location_display_info.setVisibility(View.GONE);
                    BottomContainerHolder.setVisibility(View.GONE);
                    Toasty.success(getApplicationContext(), getString(R.string.you_canceled_your_demand), Toasty.LENGTH_SHORT).show();
                }, Throwable::printStackTrace);
        RxView.clicks(findViewById(R.id.Favories))
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(unit -> {
                    mapViewModel.InsertData(pickup12.getChifor());
                    Toasty.success(getApplicationContext(), getString(R.string.AddedToFavor), Toasty.LENGTH_SHORT).show();
                }, Throwable::printStackTrace);
        RxView.clicks(findViewById(R.id.calls))
                .throttleFirst(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(unit -> startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse(pickup12.getChifor().getPhone()))), Throwable::printStackTrace);
    }

    private void CheckMyData() {
        mapViewModel.getClientMutableLiveData().observe(this, client -> {
            if (client == null)
                buildAlertMessageNoDataFound();
            else
                Current_Client = client;
        });
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

    private void views() {
        ButterKnife.bind(this, findViewById(android.R.id.content));
        Common.SetWelcomeMessage(WelcomeText);
        mapView = findViewById(R.id.mapView);
        findViewById(R.id.progBar).setVisibility(View.GONE);
        findDriver2.setVisibility(View.GONE);
        BottomContainerHolder.setVisibility(View.GONE);
        layout_driver_display_info.setVisibility(View.GONE);
        layout_location_display_info.setVisibility(View.GONE);
        DeleteDemand.setVisibility(View.GONE);
        listAnim.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Util_List.class)));

        manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
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
        if (mFusedLocationClient == null) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            loadAvailableDrivers();
        }
    }

    private void BuildLocationCallBack() {
        if (locationCallback == null) {
            locationCallback = new LocationCallback() {
                @SuppressLint("CheckResult")
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    Location location = locationResult.getLastLocation();

                    if (location != null) {
                        Common.SetWelcomeMessage(WelcomeText);
                        startService(new Intent(getApplicationContext(), LocationServiceUpdate.class));
                        init(location);
                        UploadLocation(location);
                        //Find Button For Lunch Search Request
                        RxView.clicks(findViewById(R.id.FindButton))
                                .throttleFirst(3, TimeUnit.SECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(unit -> {
                                    Toasty.info(getApplicationContext(), getString(R.string.lookingForBestRoute), Toasty.LENGTH_SHORT).show();
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
                    try {
                        if (location != null) {
                            cityName = LocationUtils.getAddressFromLocation(getApplicationContext(), location);
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
                                    GeoQueryModel geoQueryModel = snapshot.getValue(GeoQueryModel.class);
                                    if (geoQueryModel != null) {
                                        GeoLocation geoLocation = new GeoLocation(geoQueryModel.getL().get(0), geoQueryModel.getL().get(1));
                                        DriverGeoModel driverGeoModel = new DriverGeoModel(snapshot.getKey(), geoLocation);
                                        Location newDriverLocation = new Location("");
                                        newDriverLocation.setLatitude(geoLocation.latitude);
                                        newDriverLocation.setLongitude(geoLocation.longitude);
                                        float newDistance = location.distanceTo(newDriverLocation) / 1000;
                                        if (newDistance <= LIMIT_RANGE)
                                            FindDriversByID(driverGeoModel);
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
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void addDriverMarker() {
        if (Common.driversFound.size() > 0) {
            Observable.fromIterable(Common.driversFound.keySet())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(key -> FindDriversByID(Common.driversFound.get(key)), Throwable::printStackTrace);
        }
    }

    private void FindDriversByID(DriverGeoModel driverGeoModel) {
        FireBaseClient.getFireBaseClient().getFirebaseDatabase()
                .getReference(Common.Chifor_DataBase_Table)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Chifor chifor = dataSnapshot.getValue(Chifor.class);
                                if (chifor != null && chifor.getId().equals(driverGeoModel.getKey())) {
                                    driverGeoModel.setChifor(chifor);
                                    Common.driversFound.get(driverGeoModel.getKey()).setChifor(chifor);
                                    iFirebaseDriverInfoListener.onDriverInfoLoadSuccess(driverGeoModel);
                                }
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
        try {
            String city = LocationUtils.getAddressFromLocation(getApplicationContext(), locationComponent);
            ClientLocationRef = FireBaseClient.getFireBaseClient().getDatabaseReference().child(Common.CLIENT_LOCATION_REFERENCES)
                    .child(city);
        } catch (Exception e) {
            e.printStackTrace();
        }
        geoFire = new GeoFire(ClientLocationRef);
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
                            Toasty.info(getApplicationContext(), getString(R.string.Looking), Toasty.LENGTH_SHORT);
                    });
        }
    }

    public boolean isMapsEnabled() {
        manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
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
            try {
                if (locationComponent.getLastKnownLocation() != null) {
                    CameraPosition position = new CameraPosition
                            .Builder()
                            .target(new LatLng(locationComponent.getLastKnownLocation().getLatitude(),
                                    locationComponent.getLastKnownLocation().getLongitude()))
                            .zoom(17)
                            .bearing(180).tilt(30)
                            .build();
                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 2000);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toasty.warning(getApplicationContext(), getString(R.string.LocationNotFound), Toasty.LENGTH_SHORT).show();
            }
        });

        //Move Location
        BuildLocationRequest();
        BuildLocationCallBack();
        UpdateLocation();
    }

    @SuppressLint("CheckResult")
    private void ShowDriverDashboard(Trip pickup1) {
        try {
            navigationMapRoute.removeRoute();
            WhereToGo.getEditText().getText().clear();
            pickup12 = pickup1;
            Timber.tag("wtf").e("Inside");
            findViewById(R.id.progBar).setVisibility(View.GONE);
            layout_location_display_info.setVisibility(View.GONE);
            layout_driver_display_info.setVisibility(View.VISIBLE);
            ListTaxiNum.setText(pickup1.getChifor().getTaxi_NUM());
            ListChName.setText(pickup1.getChifor().getFullname());
            ListChNum.setText(pickup1.getChifor().getPhone());
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
            Destination_point = point;
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
                                Toasty.warning(getApplicationContext(), getString(R.string.NoRouteWasFound), Toasty.LENGTH_SHORT).show();
                                return;
                            } else if (response.body().routes().size() < 1) {
                                Toasty.warning(getApplicationContext(), getString(R.string.NoRouteWasFound), Toasty.LENGTH_SHORT).show();
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
        MyLocation = location;
        layout_location_display_info.setVisibility(View.VISIBLE);
        findDriver2.setVisibility(View.VISIBLE);
        BottomContainerHolder.setVisibility(View.VISIBLE);
        DeleteDemand.setVisibility(View.GONE);
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> currentAddress = geocoder.getFromLocation(MyLocation.getLatitude(), MyLocation.getLongitude(), 1);
            if (currentAddress.size() > 0) {
                ComingFrom.setText(currentAddress.get(0).getAddressLine(0));
                GoingTO.setText(Objects.requireNonNull(WhereToGo.getEditText()).getText());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void FindNearByDrivers(Location location) {
        if (Common.driversFound.size() > 0) {
            float min_distance = 0;
            DriverGeoModel foundDriver = null;
            Location currentRiderLocation = new Location("");
            currentRiderLocation.setLatitude(location.getLatitude());
            currentRiderLocation.setLongitude(location.getLongitude());
            for (String key : Common.driversFound.keySet()) {
                Location DriverLocation = new Location("");
                DriverLocation.setLatitude(Objects.requireNonNull(Common.driversFound.get(key)).getGeoLocation().latitude);
                DriverLocation.setLongitude(Objects.requireNonNull(Common.driversFound.get(key)).getGeoLocation().longitude);

                if (min_distance == 0) {
                    min_distance = DriverLocation.distanceTo(currentRiderLocation);
                    if (!Objects.requireNonNull(Common.driversFound.get(key)).isDecline()) {
                        foundDriver = Common.driversFound.get(key);
                        break;
                    } else
                        continue;

                } else if (DriverLocation.distanceTo(currentRiderLocation) < min_distance) {
                    min_distance = DriverLocation.distanceTo(currentRiderLocation);
                    if (!Objects.requireNonNull(Common.driversFound.get(key)).isDecline()) {
                        foundDriver = Common.driversFound.get(key);
                        break;
                    } else
                        continue;
                }
            }
            if (foundDriver != null) {
                UserUtils.sendRequestToDriver(mapViewModel, Destination_point, WhereToGo.getEditText().getText().toString(), foundDriver, currentRiderLocation, Current_Client);
                LastDriverCall = foundDriver;
            } else {
                Toasty.info(getApplicationContext(), getString(R.string.No_Driver_Accept_Request), Toasty.LENGTH_SHORT).show();
                LastDriverCall = null;
                navigationMapRoute.removeRoute();
                layout_location_display_info.setVisibility(View.GONE);
                BottomContainerHolder.setVisibility(View.GONE);
            }

        } else {
            Toasty.info(getApplicationContext(), getString(R.string.driver_not_Found), Toasty.LENGTH_SHORT).show();
            LastDriverCall = null;
            finish();
        }
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
                Toasty.warning(this, R.string.user_location_permission_explanation, Toasty.LENGTH_LONG).show();
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
        Toasty.warning(getApplicationContext(), R.string.user_location_permission_explanation, Toasty.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent(Objects.requireNonNull(mapboxMap.getStyle()));
        } else {
            Toasty.warning(getApplicationContext(), R.string.user_location_permission_not_granted, Toasty.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        if (isNextLaunch) {
            loadAvailableDrivers();
        } else
            isNextLaunch = true;
    }


    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
        mapView.onStop();
        if (EventBus.getDefault().hasSubscriberForEvent(DeclineRequestFromDriver.class))
            EventBus.getDefault().removeStickyEvent(DeclineRequestFromDriver.class);
        if (EventBus.getDefault().hasSubscriberForEvent(DriverAcceptTripEvent.class))
            EventBus.getDefault().removeStickyEvent(DriverAcceptTripEvent.class);
        if (EventBus.getDefault().hasSubscriberForEvent(DeclineRequestAndRemoveTripFromDriver.class))
            EventBus.getDefault().removeStickyEvent(DeclineRequestAndRemoveTripFromDriver.class);
        if (EventBus.getDefault().hasSubscriberForEvent(DriverCompleteTrip.class))
            EventBus.getDefault().removeStickyEvent(DriverCompleteTrip.class);
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
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
        compositeDisposable.clear();
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
                        if (Common.driversFound != null && Common.driversFound.size() > 0)
                            Common.driversFound.remove(driverGeoModel.getKey());
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
        manager.removeUpdates(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void OnDeclineDriverRequest(DeclineRequestFromDriver event) {
        if (LastDriverCall != null) {
            Common.driversFound.get(LastDriverCall.getKey()).setDecline(true);
            FindNearByDrivers(CurrentLocation);
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void OnDeclineAndRemoveTripDriverRequest(DeclineRequestAndRemoveTripFromDriver event) {
        if (LastDriverCall != null) {
            Common.driversFound.get(LastDriverCall.getKey()).setDecline(true);
            finish();
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void OnAcceptDriverEvent(DriverAcceptTripEvent event) {
        FirebaseDatabase.getInstance()
                .getReference(Common.Pickup_DataBase_Table)
                .child(event.getTripKey())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Trip trip = dataSnapshot.getValue(Trip.class);
                                if (trip != null) {
                                    ShowDriverDashboard(trip);
                                    SubscribeDriversMoving(trip);
                                    initDriverForMoving(event.getTripKey(), trip);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void OnDriverCompleteTrip(DriverCompleteTrip event) {
        Common.messagingstyle_Notification(getApplicationContext(), new Random().nextInt(),
                getString(R.string.TripDone), event.getKey() + getString(R.string.YouArrived));
        finish();
    }

    private void initDriverForMoving(String tripKey, Trip trip) {
        DriverOldLocation = trip.getCurrentLng() + "," + trip.getCurrentLat();
        FirebaseDatabase.getInstance()
                .getReference(Common.Pickup_DataBase_Table)
                .child(tripKey)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Trip NewTrip = dataSnapshot.getValue(Trip.class);
                                if (NewTrip != null) {
                                    String DriverNewLocation = NewTrip.getCurrentLng() + "," + NewTrip.getCurrentLat();
                                    if (!DriverOldLocation.equals(DriverNewLocation))
                                        MoveMarkerAnimation(DistinationMarker, DriverOldLocation, DriverNewLocation);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void MoveMarkerAnimation(Marker marker, String from, String to) {

        compositeDisposable.add(
                mapViewModel.getDirections("driving",
                        from + ";" + to
                        , "pk.eyJ1IjoidGhlc2hhZG93MiIsImEiOiJjazk5YWNzczYwMjJ2M2VvMGttZHRrajFuIn0.evtApMiwXCmCfyw5qUDT5Q"));

        mapViewModel.getRouteLiveData()
                .observe(this, routes -> {
                    try {
                        PolylineOptions blackPolylineOptions = null;
                        List<LatLng> polylineList = null;
                        Polyline blackPolyline = null;
                        for (int i = 0; i < routes.size(); i++) {

                            String point = routes.get(i).getGeometry();
                            polylineList = Common.decodePoly(point);


                            blackPolylineOptions = new PolylineOptions();
                            blackPolylineOptions.color(Color.WHITE);
                            blackPolylineOptions.width(5);
                            blackPolylineOptions.addAll(polylineList);
                            blackPolyline = mapboxMap.addPolyline(blackPolylineOptions);

                            Bitmap icon = UserUtils.CreateIconWithDuration(getApplicationContext(), String.valueOf(routes.get(0).getDuration()));
                            OriginMarker.setIcon(IconFactory.getInstance(Map.this).fromBitmap(icon));
                            //Moving
                            hendler = new Handler();
                            index = -1;
                            next = 1;
                            List<LatLng> finalPolylineList = polylineList;
                            hendler.postDelayed(() -> {
                                if (index < finalPolylineList.size() - 2) {
                                    index++;
                                    next = index + 1;
                                    start = finalPolylineList.get(index);
                                    end = finalPolylineList.get(next);
                                }
                                ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 1);
                                valueAnimator.setDuration(1500);
                                valueAnimator.setInterpolator(new LinearInterpolator());
                                valueAnimator.addUpdateListener(value -> {
                                    v = value.getAnimatedFraction();
                                    lng = v * end.getLongitude() + (1 - v) * start.getLongitude();
                                    lat = v * end.getLatitude() + (1 - v) * start.getLatitude();
                                    LatLng newPos = new LatLng(lat, lng);
                                    marker.setPosition(newPos);
                                    mapboxMap.moveCamera(CameraUpdateFactory.newLatLng(newPos));
                                });
                                valueAnimator.start();
                                if (index < finalPolylineList.size() - 2)
                                    hendler.postDelayed((Runnable) this, 1500);
                                else if (index < finalPolylineList.size() - 1) {

                                }
                            }, 1500);
                            DriverOldLocation = to;

                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                });
    }

    private void SubscribeDriversMoving(Trip trip) {
        compositeDisposable.add(
                mapViewModel.getDirections("driving",
                        trip.getOrigin() + ";" + trip.getCurrentLng() + "," + trip.getCurrentLat()
                        , "pk.eyJ1IjoidGhlc2hhZG93MiIsImEiOiJjazk5YWNzczYwMjJ2M2VvMGttZHRrajFuIn0.evtApMiwXCmCfyw5qUDT5Q"));

        mapViewModel.getRouteLiveData()
                .observe(this, routes -> {
                    try {
                        PolylineOptions blackPolylineOptions = null;
                        List<LatLng> polylineList = null;
                        Polyline blackPolyline = null;
                        for (int i = 0; i < routes.size(); i++) {

                            String point = routes.get(i).getGeometry();
                            polylineList = Common.decodePoly(point);


                            blackPolylineOptions = new PolylineOptions();
                            blackPolylineOptions.color(Color.WHITE);
                            blackPolylineOptions.width(5);
                            blackPolylineOptions.addAll(polylineList);
                            blackPolyline = mapboxMap.addPolyline(blackPolylineOptions);


                            LatLng origin = new LatLng(
                                    Double.parseDouble(trip.getOrigin().split(",")[1]),
                                    Double.parseDouble(trip.getOrigin().split(",")[0]));

                            LatLng destination = new LatLng(trip.getCurrentLng(), trip.getCurrentLat());

                            LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                    .include(origin)
                                    .include(destination)
                                    .build();

                            addPickUpMarkerWithDuration(routes.get(0).getDuration(), origin);
                            addDestinationDriverMarker(destination);

                            mapboxMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 160));
                            mapboxMap.moveCamera(CameraUpdateFactory.zoomTo(mapboxMap.getCameraPosition().zoom - 1));

                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                });

    }

    private void addDestinationDriverMarker(LatLng destination) {
        DistinationMarker = mapboxMap.addMarker(new MarkerOptions()
                .position(destination)
                .icon(IconFactory.getInstance(Map.this).fromResource(R.drawable.car2)));
    }

    private void addPickUpMarkerWithDuration(double duration, LatLng origin) {
        Bitmap icon = UserUtils.CreateIconWithDuration(getApplicationContext(), String.valueOf(duration));
        OriginMarker = mapboxMap.addMarker(new MarkerOptions()
                .icon(IconFactory.getInstance(Map.this).fromBitmap(icon))
                .position(origin));
    }
}