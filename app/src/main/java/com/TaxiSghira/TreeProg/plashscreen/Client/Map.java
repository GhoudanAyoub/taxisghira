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
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import com.TaxiSghira.TreeProg.plashscreen.API.FireBaseClient;
import com.TaxiSghira.TreeProg.plashscreen.Both.PersonalInfo;
import com.TaxiSghira.TreeProg.plashscreen.Module.Accept;
import com.TaxiSghira.TreeProg.plashscreen.Module.Demande;
import com.TaxiSghira.TreeProg.plashscreen.Module.UserLocation;
import com.TaxiSghira.TreeProg.plashscreen.Profile.Util_List;
import com.TaxiSghira.TreeProg.plashscreen.R;
import com.TaxiSghira.TreeProg.plashscreen.Service.LocationServiceUpdate;
import com.TaxiSghira.TreeProg.plashscreen.ui.MapModelView.MapViewModel;
import com.TaxiSghira.TreeProg.plashscreen.ui.PersonalInfoModelView.PersonalInfoModelViewClass;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

public class Map extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnMapClickListener, PermissionsListener {


    public static String id;
    public Accept accept;
    TextView Ch_Name, TaxiNum, Ch_Num;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, "pk.eyJ1IjoidGhlc2hhZG93MiIsImEiOiJjanZzNjZ4YnEyNWY1M3lsZTkzY2dsbTRyIn0.kRrltAjtWtJIlviacEL5og");
        setContentView(R.layout.app_bar_map);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        findViewById(R.id.listAnim).setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), Util_List.class)));

        startService(new Intent(getApplicationContext(),LocationServiceUpdate.class));
        PersonalInfoModelViewClass personalInfoModelViewClass = ViewModelProviders.of(this).get(PersonalInfoModelViewClass.class);
        personalInfoModelViewClass.getClientInfo();
        mapViewModel = ViewModelProviders.of(this).get(MapViewModel.class);
        mapViewModel.GetChiforDataLocation();
        mapViewModel.GetAcceptDemandeList();


        checkMapServices();

        gProgress = new ProgressDialog(this);
        builder = new AlertDialog.Builder(this);
        WhereToGo = findViewById(R.id.editText2);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        Ch_Name = findViewById(R.id.list_Ch_Name);
        Ch_Num = findViewById(R.id.list_Ch_num);
        TaxiNum = findViewById(R.id.list_Taxi_num);

        personalInfoModelViewClass.clientMutableLiveData.observe(this, client -> {
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
        builder.setMessage("المرجو تشغيل GPS")
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
                    mapViewModel.chiforMutableLiveData.observe(this,chifor1 -> {
                        assert chifor1 != null;
                        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(chifor1.getLant(), chifor1.getLong())).icon(icon));
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

                    //use servce to  update data to  base every 30s
                } catch (Exception e) {
                    startActivity(new Intent(getApplicationContext(), Map.class));
                }
                GeoJsonSource source = Objects.requireNonNull(mapboxMap.getStyle()).getSourceAs("destination-source-id");
                if (source != null) {
                    source.setGeoJson(Feature.fromGeometry(destinationPoint));
                }
                getRoute(originPoint, destinationPoint);

            });
        });
    }

    private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage("destination-icon-id", BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_marker_icon_default));
        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
        loadedMapStyle.addSource(geoJsonSource);
        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(
                iconImage("destination-icon-id"),
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
            assert Mapbox.getAccessToken() != null;
            NavigationRoute.builder(this)
                    .accessToken(Mapbox.getAccessToken())
                    .origin(origin)
                    .destination(destination)
                    .build()
                    .getRoute(new Callback<DirectionsResponse>() {
                        @Override
                        public void onResponse(@NotNull Call<DirectionsResponse> call, @NotNull Response<DirectionsResponse> response) {
                            Toast.makeText(getApplicationContext(), "تم إنشاء الطريق", Toast.LENGTH_SHORT).show();

                            new Handler().postDelayed(() -> {
                                buildAlertMessageSearchOperation();
                            }, 2000);

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
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("هل تريد بدء عملبة البحت عن طاكسي?")
                .setIcon(R.drawable.ic_search_black_24dp)
                .setCancelable(false)
                .setPositiveButton("نعم", (dialog, which) -> {
                    gProgress.setMessage("المرجو الانتظار قليلا ⌛️");

                    assert locationComponent.getLastKnownLocation() != null;
                    UserLocation userLocation = new UserLocation(locationComponent.getLastKnownLocation().getLatitude(), locationComponent.getLastKnownLocation().getLongitude());
                    Demande d1 = new Demande(FireBaseClient.getFireBaseClient().getUserLogEdInAccount().getDisplayName(), WhereToGo.getText().toString(),userLocation.getLnt(),userLocation.getLong());
                    mapViewModel.AddDemande(d1);

                    //waitiing room !!!!!!!!!
                    final AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                    builder2.setIcon(R.drawable.ic_search_black_24dp)
                            .setTitle("عملية البحت")
                            .setMessage("بدأت عملية البحت عن طاكسي\uD83D\uDE04\uD83D\uDE04")
                            .setNegativeButton("حسنا", (dialog2, which2) ->
                            mapViewModel.acceptMutableLiveData.observe(this,accept1 -> {
                                    //notify user that  he get accepted
                            }))
                            .setPositiveButton("رفض", (dialog1, which1) -> mapViewModel.DelateDemande());

                    new Handler().postDelayed(() -> {
                        final AlertDialog alert2 = builder2.create();
                        alert2.show();
                        }, 2000);
                })
                .setNegativeButton("لا",null);
        final AlertDialog alert = builder.create();
        alert.show();
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
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}