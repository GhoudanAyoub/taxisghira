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
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import com.TaxiSghira.TreeProg.plashscreen.Commun.Common;
import com.TaxiSghira.TreeProg.plashscreen.Module.Chifor;
import com.TaxiSghira.TreeProg.plashscreen.Module.Client;
import com.TaxiSghira.TreeProg.plashscreen.Module.Demande;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
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
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import kotlin.Unit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

public class Map extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnMapClickListener, PermissionsListener {


    public static String id;
    TextView ListTaxiNum, ListChName, ListChNum, GoingTO, ComingFrom;
    AlertDialog.Builder builder;
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
    private DatabaseReference currentUserRef, ClientLocationRef;
    private GeoFire geoFire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoidGhlc2hhZG93MiIsImEiOiJjazk5YWNzczYwMjJ2M2VvMGttZHRrajFuIn0.evtApMiwXCmCfyw5qUDT5Q");
        setContentView(R.layout.app_bar_map);

        FirebaseApp.initializeApp(getApplicationContext());
        checkMapServices();
        views();
        startService(new Intent(getApplicationContext(), LocationServiceUpdate.class));

        Log.d("FIREBASETOKEN", refreshedToken);
        PersonalInfoModelViewClass personalInfoModelViewClass = ViewModelProviders.of(this).get(PersonalInfoModelViewClass.class);
        mapViewModel = ViewModelProviders.of(this).get(MapViewModel.class);
        favorViewModel = ViewModelProviders.of(this).get(FavorViewModel.class);

        personalInfoModelViewClass.getClientInfo();
        mapViewModel.GetChiforDataLocation();
        mapViewModel.GetPickDemand();

        findViewById(R.id.listAnim).setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), Util_List.class)));
        findViewById(R.id.LyoutLoti).setVisibility(View.GONE);
        findViewById(R.id.findDriver).setVisibility(View.GONE);
        findViewById(R.id.location_panel).setVisibility(View.GONE);
        findViewById(R.id.progBar).setVisibility(View.GONE);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        builder = new AlertDialog.Builder(this);
        personalInfoModelViewClass.getClientMutableLiveData().observe(this, client -> {
            if (client == null)
                buildAlertMessageNoDataFound();
            else
                Current_Client = client;
        });

        findViewById(R.id.floatingActionButton2).setOnClickListener(view -> {
            mapViewModel.getAcceptMutableLiveData().observe(Map.this, this::ShowDriverDashboard);
            mapView.getMapAsync(this);
            startActivity(new Intent(getApplicationContext(), Map.class));
        });

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> mapViewModel.getAcceptMutableLiveData().observe(Map.this, this::ShowDriverDashboard));
    }

    private void init(LocationComponent locationComponent) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(locationComponent.getLastKnownLocation().getLatitude(), locationComponent.getLastKnownLocation().getLongitude(), 1);
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
            mapboxMap.addOnMapClickListener(Map.this);;
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
            //Show List of Drivers
            mapboxMap.addOnCameraMoveStartedListener(reason ->
                    mapViewModel.getChiforMutableLiveData().observe(this, chifor1 -> {
                        assert chifor1 != null;
                        for (Chifor chifor : chifor1) {
                            mapboxMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(chifor.getLnt(), chifor.getLng()))
                                    .title(chifor.getFullname())
                                    .snippet(chifor.getPhone())
                                    .icon(IconFactory.getInstance(Map.this).fromResource(R.drawable.car2)));
                        }
                    })
            );


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
                            ListenMoreDrivers();
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
                                getRoute(originPoint, destinationPoint);
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
        });
    }

    private void ListenMoreDrivers(){
        //Todo : Need To be Changed To CLIENT_LOCATION_REFERENCES
        try {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addressList = geocoder.getFromLocation(locationComponent.getLastKnownLocation().getLatitude(), locationComponent.getLastKnownLocation().getLongitude(), 1);
            String Client_city = addressList.get(0).getLocality();
            FireBaseClient.getFireBaseClient().getDatabaseReference()
                    .child(Common.Drivers_LOCATION_REFERENCES)
                    .child(Client_city)
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                            if (dataSnapshot.exists()) {
                                try {
                                    for (DataSnapshot Ds1 : dataSnapshot.getChildren()) {
                                        Chifor ch1 = Ds1.getValue(Chifor.class);
                                        Common.Drivers_Locations_List.add(ch1);
                                        mapboxMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(ch1.getLnt(), ch1.getLng()))
                                                .title(ch1.getFullname())
                                                .snippet(ch1.getPhone())
                                                .icon(IconFactory.getInstance(Map.this).fromResource(R.drawable.car2)));
                                    }
                                } catch (Throwable t) {
                                    Timber.e(t);
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

    private void getRoute(Point origin, Point destination) {
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
                                    .subscribe(() -> buildAlertMessageSearchOperation());
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

    private void buildAlertMessageSearchOperation() {
        assert locationComponent.getLastKnownLocation() != null;

        findViewById(R.id.findDriver).setVisibility(View.VISIBLE);
        findViewById(R.id.location_panel).setVisibility(View.VISIBLE);
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> currentAddress = geocoder.getFromLocation(locationComponent.getLastKnownLocation().getLatitude(), locationComponent.getLastKnownLocation().getLongitude(), 1);
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
                        UploadLocation();
                        UserLocation userLocation = new UserLocation(locationComponent.getLastKnownLocation().getLatitude(), locationComponent.getLastKnownLocation().getLongitude());
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

    private void UploadLocation() {
        //updateLocation
        if (locationComponent.getLastKnownLocation() != null) {
            init(locationComponent);
            geoFire.setLocation(Common.Current_Client_Id,
                    new GeoLocation(locationComponent.getLastKnownLocation().getLatitude(),
                            locationComponent.getLastKnownLocation().getLongitude()),
                    (key, error) -> {
                        if (error != null)
                            Timber.e(error.getMessage());
                        else
                            Snackbar.make(Objects.requireNonNull(this.getCurrentFocus()), "يبحث .....", Snackbar.LENGTH_LONG)
                                    .show();
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
                            dataSnapshot.getRef().removeValue();
                            Timber.i("DemandRemovedDone");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Timber.e(databaseError.getMessage());
                    }
                });
    }

    private void enableLocationComponent(@NonNull Style loadedMapStyle)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         {
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
        mapView.onStart();}

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        Completable.timer(500,TimeUnit.MILLISECONDS,AndroidSchedulers.mainThread())
                .subscribe(()-> mapViewModel.getAcceptMutableLiveData().observe(Map.this, this::ShowDriverDashboard));
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
}