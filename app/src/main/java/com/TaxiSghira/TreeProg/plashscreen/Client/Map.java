package com.TaxiSghira.TreeProg.plashscreen.Client;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import com.TaxiSghira.TreeProg.plashscreen.API.FireBaseClient;
import com.TaxiSghira.TreeProg.plashscreen.Both.PersonalInfo;
import com.TaxiSghira.TreeProg.plashscreen.Module.Demande;
import com.TaxiSghira.TreeProg.plashscreen.Module.Pickup;
import com.TaxiSghira.TreeProg.plashscreen.Module.UserLocation;
import com.TaxiSghira.TreeProg.plashscreen.Profile.Util_List;
import com.TaxiSghira.TreeProg.plashscreen.R;
import com.TaxiSghira.TreeProg.plashscreen.Service.LocationServiceUpdate;
import com.TaxiSghira.TreeProg.plashscreen.ui.FavorViewModel.FavorViewModel;
import com.TaxiSghira.TreeProg.plashscreen.ui.MapModelView.MapViewModel;
import com.TaxiSghira.TreeProg.plashscreen.ui.PersonalInfoModelView.PersonalInfoModelViewClass;
import com.airbnb.lottie.LottieAnimationView;
import com.jakewharton.rxbinding3.view.RxView;
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

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
    public Pickup pickup;
    TextView ListTaxiNum, ListChName, ListChNum;
    AlertDialog.Builder builder;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    private DirectionsRoute currentRoute;
    private NavigationMapRoute navigationMapRoute;
    private EditText WhereToGo;
    private ProgressDialog gProgress;
    private boolean mLocationPermissionGranted = false;
    MapViewModel mapViewModel;
    LottieAnimationView lottieAnimationView;
    LinearLayout bottom_sheet;
    FavorViewModel favorViewModel;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, "pk.eyJ1IjoidGhlc2hhZG93MiIsImEiOiJjazk5YWNzczYwMjJ2M2VvMGttZHRrajFuIn0.evtApMiwXCmCfyw5qUDT5Q");
        setContentView(R.layout.app_bar_map);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        checkMapServices();
        startService(new Intent(getApplicationContext(),LocationServiceUpdate.class));
        PersonalInfoModelViewClass personalInfoModelViewClass = ViewModelProviders.of(this).get(PersonalInfoModelViewClass.class);
        personalInfoModelViewClass.getClientInfo();
        mapViewModel = ViewModelProviders.of(this).get(MapViewModel.class);
        mapViewModel.GetChiforDataLocation();
        mapViewModel.GetAcceptDemandeList();
        favorViewModel = ViewModelProviders.of(this).get(FavorViewModel.class);

        findViewById(R.id.listAnim).setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), Util_List.class)));

        gProgress = new ProgressDialog(this);
        builder = new AlertDialog.Builder(this);
        WhereToGo = findViewById(R.id.editText2);
        lottieAnimationView = findViewById(R.id.progBar);

        bottom_sheet = findViewById(R.id.bottom_sheet);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        ListTaxiNum = findViewById(R.id.list_Taxi_num);
        ListChName = findViewById(R.id.list_Ch_Name);
        ListChNum = findViewById(R.id.list_Ch_num);

        personalInfoModelViewClass.getClientMutableLiveData().observe(this, client -> {
            if (client==null){
                buildAlertMessageNoDataFound();
            }
        });
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

    private boolean checkMapServices() {
        return isMapsEnabled();
    }

    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
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
    public void onBackPressed() { }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(Style.SATELLITE_STREETS, style -> {
            enableLocationComponent(style);
            addDestinationIconSymbolLayer(style);
            mapboxMap.addOnMapClickListener(Map.this);

            IconFactory iconFactory = IconFactory.getInstance(Map.this);
            Icon icon = iconFactory.fromResource(R.drawable.taxisymb);
            mapboxMap.addOnCameraMoveStartedListener(reason ->
                    mapViewModel.getChiforMutableLiveData().observe(this, chifor1 -> {
                        assert chifor1 != null;
                        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(chifor1.getLnt(), chifor1.getLng())).icon(icon));
                    })
            );
            findViewById(R.id.FindButton).setOnClickListener(v -> {
                Toast.makeText(getApplicationContext(), "المرجو الانتظار جاري البحت عن طريق مناسب", Toast.LENGTH_LONG).show();

                Point destinationPoint = null;
                try {
                    final Geocoder geocoder = new Geocoder(getApplicationContext());
                    final String locName = WhereToGo.getText().toString();

                    final List<Address> list = geocoder.getFromLocationName(locName, 1);
                    if (!(list == null || list.isEmpty())) {
                        final Address adress = list.get(0);
                        destinationPoint = Point.fromLngLat(adress.getLongitude(), adress.getLatitude());
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

            });
        });
    }

    private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage("destination-icon-id", BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_marker_icon_default));
        loadedMapStyle.addSource(new GeoJsonSource("destination-source-id"));
        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(iconImage("destination-icon-id"), iconIgnorePlacement(true));
        loadedMapStyle.addLayer(destinationSymbolLayer);
    }

    //driver maps
    private void addoriginIconSymbolLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage("destination-icon-id2", BitmapFactory.decodeResource(this.getResources(), R.drawable.map_marker_light));
        loadedMapStyle.addSource(new GeoJsonSource("destination-origin-id"));
        SymbolLayer destinationSymbolLayer2 = new SymbolLayer("destination-symbol-layer-id2", "destination-origin-id");
        destinationSymbolLayer2.withProperties(
                iconImage("destination-icon-id2"),
                iconIgnorePlacement(true)
        );
        loadedMapStyle.addLayer(destinationSymbolLayer2);
    }

    @SuppressWarnings({"MissingPermission"})
    @Override
    public boolean onMapClick(@NonNull LatLng point) {
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
                            // Toast.makeText(getApplicationContext(), "تم إنشاء الطريق", Toast.LENGTH_SHORT).show();

                            new Handler().postDelayed(() -> {
                                buildAlertMessageSearchOperation();
                            }, 1000);

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
        findViewById(R.id.findDriver).setVisibility(View.VISIBLE);
        RxView.clicks(findViewById(R.id.findDriver)).
                throttleFirst(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Unit>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(Unit unit) {
                        assert locationComponent.getLastKnownLocation() != null;
                        UserLocation userLocation = new UserLocation(locationComponent.getLastKnownLocation().getLatitude(), locationComponent.getLastKnownLocation().getLongitude());
                        Demande d1 = new Demande(FireBaseClient.getFireBaseClient().getUserLogEdInAccount().getDisplayName(), WhereToGo.getText().toString(), userLocation.getLnt(), userLocation.getLong());
                        mapViewModel.AddDemande(d1);
                        findViewById(R.id.findDriver).setVisibility(View.GONE);
                        lottieAnimationView.playAnimation();
                        mapViewModel.getAcceptMutableLiveData().observe(Map.this, pickup1 -> {
                            //lottieAnimationView.setVisibility(View.GONE);
                            //notify user that  he get accepted
                            bottom_sheet.setVisibility(View.VISIBLE);
                            ListTaxiNum.setText(pickup1.Taxi_num);
                            ListChName.setText(pickup1.Ch_Name);
                            ListChNum.setText(pickup1.Ch_num);
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
                                            favorViewModel.AddFAvor(Objects.requireNonNull(FireBaseClient.getFireBaseClient().getUserLogEdInAccount().getId())
                                                    , pickup1.Ch_Name, pickup1.Ch_num, pickup1.Taxi_num);
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
                                            startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse(pickup1.Ch_num)));
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            Timber.e(e);
                                        }

                                        @Override
                                        public void onComplete() {
                                        }
                                    });
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
        /*
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("هل تريد بدء عملبة البحت عن طاكسي?")
                .setIcon(R.drawable.ic_search_black_24dp)
                .setCancelable(false)
                .setPositiveButton("نعم", (dialog, which) -> {
                    gProgress.setMessage("المرجو الانتظار قليلا ⌛️");
                    gProgress.show();

                    assert locationComponent.getLastKnownLocation() != null;
                    UserLocation userLocation = new UserLocation(locationComponent.getLastKnownLocation().getLatitude(), locationComponent.getLastKnownLocation().getLongitude());
                    Demande d1 = new Demande(FireBaseClient.getFireBaseClient().getUserLogEdInAccount().getDisplayName(), WhereToGo.getText().toString(),userLocation.getLnt(),userLocation.getLong());
                    mapViewModel.AddDemande(d1);

                    gProgress.dismiss();
                    final AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                    builder2.setIcon(R.drawable.ic_search_black_24dp)
                            .setTitle("عملية البحت")
                            .setMessage("بدأت عملية البحت عن طاكسي\uD83D\uDE04\uD83D\uDE04")
                            .setPositiveButton("حسنا", (dialog2, which2) ->
                            mapViewModel.acceptMutableLiveData.observe(this,accept1 -> {
                                    //notify user that  he get accepted
                            }))
                            .setNegativeButton("رفض", (dialog1, which1) -> mapViewModel.DelateDemande());

                    new Handler().postDelayed(() -> {
                        final AlertDialog alert2 = builder2.create();
                        alert2.show();
                        }, 2000);
                })
                .setNegativeButton("لا",null);
        final AlertDialog alert = builder.create();
        alert.show();
        */
    }
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(this, loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING_GPS);
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
        compositeDisposable.clear();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}