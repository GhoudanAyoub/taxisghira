package com.TaxiSghira.TreeProg.plashscreen.Client;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import com.TaxiSghira.TreeProg.plashscreen.API.FireBaseClient;
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
import com.TaxiSghira.TreeProg.plashscreen.Profile.Util_List;
import com.TaxiSghira.TreeProg.plashscreen.R;
import com.TaxiSghira.TreeProg.plashscreen.Service.LocationServiceUpdate;
import com.TaxiSghira.TreeProg.plashscreen.ui.FavorViewModel;
import com.TaxiSghira.TreeProg.plashscreen.ui.MapViewModel;
import com.TaxiSghira.TreeProg.plashscreen.ui.PersonalInfoModelViewClass;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.GeoPoint;
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

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kotlin.Unit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

public class Map extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnMapClickListener, PermissionsListener, IFirebaseDriverInfoListener, IFirebaseFailedListener {


    public static String id;
    TextView ListTaxiNum, ListChName, ListChNum, GoingTO, ComingFrom;
    MapViewModel mapViewModel;
    LinearLayout bottom_sheet;
    FavorViewModel favorViewModel;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    private DirectionsRoute currentRoute;
    private NavigationMapRoute navigationMapRoute;
    private TextInputLayout WhereToGo;
    private boolean mLocationPermissionGranted = false;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String refreshedToken = FirebaseInstanceId.getInstance().getToken();
    private Client Current_Client;
    private Demande d1;

    //online System
    private DatabaseReference currentUserRef, ClientLocationRef, driver_location_ref;
    private GeoFire geoFire;


    private static final String TAG = "LocationService";
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private double distances = 1.0;
    private double LIMIT_RANGE = 10.0;
    private Location previousLocation, CurrentLocation;
    private Boolean firstTime = true;
    private String cityName;

    //Listeners
    IFirebaseDriverInfoListener iFirebaseDriverInfoListener;
    IFirebaseFailedListener iFirebaseFailedListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoidGhlc2hhZG93MiIsImEiOiJjazk5YWNzczYwMjJ2M2VvMGttZHRrajFuIn0.evtApMiwXCmCfyw5qUDT5Q");
        setContentView(R.layout.app_bar_map);
        FirebaseApp.initializeApp(getApplicationContext());

        checkMapServices();
        views();
        init();

        //Log.d("FIREBASETOKEN", refreshedToken);
        PersonalInfoModelViewClass personalInfoModelViewClass = ViewModelProviders.of(this).get(PersonalInfoModelViewClass.class);
        mapViewModel = ViewModelProviders.of(this).get(MapViewModel.class);
        favorViewModel = ViewModelProviders.of(this).get(FavorViewModel.class);

        personalInfoModelViewClass.getClientInfo();
        mapViewModel.GetPickDemand();


        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        personalInfoModelViewClass.getClientMutableLiveData().observe(this, client -> {
            if (client == null)
                buildAlertMessageNoDataFound();
            else
                Current_Client = client;
        });

    }

    private void init() {
        iFirebaseDriverInfoListener = this;
        iFirebaseFailedListener = this;

        locationRequest = new LocationRequest();
        locationRequest.setSmallestDisplacement(10f);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback(){
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    Location location = locationResult.getLastLocation();

                    if (location != null) {
                        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                        startService(new Intent(getApplicationContext(), LocationServiceUpdate.class));
                        UploadLocation(location);

                        //Find Button For Lunch Search Request
                        RxView.clicks(findViewById(R.id.FindButton))
                                .throttleFirst(5, TimeUnit.SECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<Unit>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {
                                        compositeDisposable.add(d);
                                    }

                                    @Override
                                    public void onNext(Unit unit) {
                                        Toast.makeText(getApplicationContext(), "المرجو الانتظار جاري البحت عن طريق مناسب", Toast.LENGTH_LONG).show();
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
                                            getRoute(originPoint, destinationPoint,location);
                                        } catch (Exception e) {
                                            Timber.e(e);
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Timber.e(e);
                                        compositeDisposable.clear();
                                    }

                                    @Override
                                    public void onComplete() {
                                    }
                                });
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
                    else {
                        // do noting
                    }
                }
        };

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        loadAvailableDrivers();
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
                        addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
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
                                Common.driversFound.add(new DriverGeoModel(key, location));
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
                                    distances = 1.0;
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
                                GeoLocation geoLocation = new GeoLocation(geoQueryModel.getL().get(0),geoQueryModel.getL().get(1));
                                DriverGeoModel driverGeoModel = new DriverGeoModel(snapshot.getKey(),geoLocation);
                                Location newDriverLocation = new Location("");
                                newDriverLocation.setLatitude(geoLocation.latitude);
                                newDriverLocation.setLongitude(geoLocation.longitude);
                                float newDistance = location.distanceTo(newDriverLocation)/1000;
                                if (newDistance <= LIMIT_RANGE)
                                    FindDriversByID(driverGeoModel);
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

    private void addDriverMarker() {
        if (Common.driversFound.size() > 0) {
            Observable.fromIterable(Common.driversFound)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(driverGeoModel -> FindDriversByID(driverGeoModel),
                            throwable -> Timber.e(throwable.getMessage()), () -> { });
        } else {
            Timber.i(getString(R.string.driver_not_Found));
        }
    }

    private void FindDriversByID(DriverGeoModel driverGeoModel) {
        FireBaseClient.getFireBaseClient().getFirebaseDatabase()
                .getReference(Common.Chifor_DataBase_Table)
                .child(driverGeoModel.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            driverGeoModel.setChifor(snapshot.getValue(Chifor.class));
                            iFirebaseDriverInfoListener.onDriverInfoLoadSuccess(driverGeoModel);
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

    private void views() {
        WhereToGo = findViewById(R.id.editText2);
        bottom_sheet = findViewById(R.id.bottom_sheet);
        ListTaxiNum = findViewById(R.id.list_Taxi_num);
        ListChName = findViewById(R.id.list_Ch_Name);
        ListChNum = findViewById(R.id.list_Ch_num);
        mapView = findViewById(R.id.mapView);
        ComingFrom = findViewById(R.id.textView);
        GoingTO = findViewById(R.id.textView2);
        findViewById(R.id.LyoutLoti).setVisibility(View.GONE);
        findViewById(R.id.findDriver).setVisibility(View.GONE);
        findViewById(R.id.location_panel).setVisibility(View.GONE);
        findViewById(R.id.progBar).setVisibility(View.GONE);
        findViewById(R.id.listAnim).setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), Util_List.class)));

    }

    private void buildAlertMessageNoDataFound() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("المرجو ملأ معلوماتكم الشخصية")
                .setIcon(R.drawable.ic_account_circle_black)
                .setCancelable(false)
                .setPositiveButton("حسنا", (dialog, which) -> startActivity(new Intent(getApplicationContext(), PersonalInfo.class)));
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void checkMapServices() {
        isMapsEnabled();
    }

    public void isMapsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_gps_off_black_24dp)
                .setMessage("المرجو تشغيل GPS")
                .setCancelable(false)
                .setPositiveButton("حسنا", (dialog, id) -> startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 333));
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

        mapboxMap.setStyle(Style.LIGHT, style -> {
            enableLocationComponent(style);
            addDestinationIconSymbolLayer(style);
            mapboxMap.addOnMapClickListener(Map.this);

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

            //getAcceptData
            mapViewModel.getAcceptMutableLiveData().observe(Map.this, this::ShowDriverDashboard);

        });
    }

    private void ShowDriverDashboard(Pickup pickup1) {
        try {
            Timber.tag("wtf").e("Inside");
            findViewById(R.id.progBar).setVisibility(View.GONE);
            findViewById(R.id.LyoutLoti).setVisibility(View.GONE);
            bottom_sheet.setVisibility(View.VISIBLE);
            ListTaxiNum.setText(pickup1.getTaxi_num());
            ListChName.setText(pickup1.getCh_Name());
            ListChNum.setText(pickup1.getCh_num());
            RxView.clicks(findViewById(R.id.Favories))
                    .throttleFirst(5, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Unit>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onNext(Unit unit) {
                            favorViewModel.AddFAvor(Objects.requireNonNull(Common.Current_Client_Id)
                                    , pickup1.getCh_Name(), pickup1.getCh_num(), pickup1.getTaxi_num(), Common.Current_Client_DispalyName);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Timber.e(e);
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
            RxView.clicks(findViewById(R.id.calls))
                    .throttleFirst(5, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Unit>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onNext(Unit unit) {
                            startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse(pickup1.getCh_num())));
                        }

                        @Override
                        public void onError(Throwable e) {
                            Timber.e(e);
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        } catch (Throwable t) {
            Timber.e(t);
        }
    }

    private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage("destination-icon-id", BitmapFactory.decodeResource(this.getResources(), R.drawable.maps_and_flags));
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
                        @Override
                        public void onResponse(@NotNull Call<DirectionsResponse> call, @NotNull Response<DirectionsResponse> response) {
                            Completable.timer(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                                    .subscribe(() -> buildAlertMessageSearchOperation(location));
                            if (response.body() == null) {
                                Toast.makeText(getApplicationContext(), "لم يتم العثور على مسارات", Toast.LENGTH_SHORT).show();
                                return;
                            } else if (response.body().routes().size() < 1) {
                                Toast.makeText(getApplicationContext(), "لم يتم العثور على مسارات", Toast.LENGTH_SHORT).show();
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

    private void buildAlertMessageSearchOperation(Location location) {
        assert location != null;

        findViewById(R.id.findDriver).setVisibility(View.VISIBLE);
        findViewById(R.id.location_panel).setVisibility(View.VISIBLE);
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

        RxView.clicks(findViewById(R.id.findDriver2)).
                throttleFirst(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Unit>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(Unit unit) {
                        UserLocation userLocation = new UserLocation(location.getLatitude(), location.getLongitude());
                        d1 = new Demande(Common.Current_Client_Id, Common.Current_Client_DispalyName, Objects.requireNonNull(WhereToGo.getEditText()).getText().toString(), Current_Client.getCity(), userLocation.getLnt(), userLocation.getLong());
                        mapViewModel.AddDemand(d1);
                        findViewById(R.id.findDriver).setVisibility(View.GONE);
                        findViewById(R.id.location_panel).setVisibility(View.GONE);
                        findViewById(R.id.progBar).setVisibility(View.VISIBLE);
                        //findViewById(R.id.LyoutLoti).setVisibility(View.VISIBLE);

                        RxView.clicks(findViewById(R.id.imageViewCancel)).
                                throttleFirst(3, TimeUnit.SECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<Unit>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {
                                        compositeDisposable.add(d);
                                    }

                                    @Override
                                    public void onNext(Unit unit) {
                                        RemoveDemand(d1);
                                        navigationMapRoute.removeRoute();
                                        WhereToGo.getEditText().setText("");
                                        findViewById(R.id.LyoutLoti).setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Timber.e(e);
                                    }

                                    @Override
                                    public void onComplete() {
                                    }
                                });

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
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
                            Snackbar.make(Objects.requireNonNull(findViewById(android.R.id.content)), "يبحث .....", Snackbar.LENGTH_LONG);
                    });
        }
    }

    public void RemoveDemand(Demande demande) {
        geoFire.getDatabaseReference()
                .child(Common.Demande_DataBase_Table)
                .child(demande.getCity())
                .orderByChild("clientId")
                .equalTo(Common.Current_Client_Id)
                .removeEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                                dataSnapshot1.getRef().removeValue();
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
        if (requestCode == 334) {// If request is cancelled, the result arrays are empty.
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
        Completable.timer(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe(() -> mapViewModel.getAcceptMutableLiveData().observe(Map.this, this::ShowDriverDashboard));
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
        //remove  demand
        // geoFire.getDatabaseReference().child(Common.Demande_DataBase_Table).child(d1.getCity()).removeValue();
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
        if (!TextUtils.isEmpty(cityName)){
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
        Snackbar.make(findViewById(android.R.id.content),message, Snackbar.LENGTH_LONG).show();
        Timber.e(message);
    }
}