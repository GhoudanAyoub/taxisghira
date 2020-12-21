package com.TaxiSghira.TreeProg.plashscreen.Commun;

import android.widget.TextView;

import com.TaxiSghira.TreeProg.plashscreen.Module.Chifor;
import com.TaxiSghira.TreeProg.plashscreen.Module.DriverGeoModel;
import com.TaxiSghira.TreeProg.plashscreen.Room.FireBaseClient;
import com.google.firebase.auth.FirebaseAuth;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Common {

    public static final String Current_Client_Id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    public static final String Current_Client_DispalyName = FireBaseClient.getFireBaseClient().getFirebaseUser().getDisplayName();
    public static final String Current_Client_Gmail = FireBaseClient.getFireBaseClient().getFirebaseUser().getEmail();
    public static final String Favor_DataBase_Table = "Favor";
    public static final String Chifor_DataBase_Table = "Chifor";
    public static final String Demande_DataBase_Table = "Demande";
    public static final String Pickup_DataBase_Table = "Trip";
    public static final String Client_DataBase_Table = "Client";
    public static final String city = "city";
    public static final String Client_Id_String = "id";
    public static final String Gmail_String = "gmail";
    public static final String ClientName_String = "ClientName";
    public static final String NOTI_TITLE = "title";
    public static final String NOTI_BODY= "body";
    public static final String DRIVER_TOKEN_REFERENCE = "DriverToken";
    public static final String CLIENT_TOKEN_REFERENCE = "ClientToken";
    public static final String Drivers_LOCATION_REFERENCES = "DriversLocation";
    public static final String CLIENT_LOCATION_REFERENCES = "ClientLocation";
    public static final String CLIENT_DATA = "ClientData" ;
    public static final String REQUEST_DRIVER_TITLE = "RequestDriver";
    public static final String RIDER_KEY = "RiderKey" ;
    public static final String DRIVER_KEY = "DriverKey" ;
    public static final String REQUEST_DRIVER_DECLINE = "Decline";
    public static final String RIDER_PICK_UP_LOCATION = "RequestLocation";
    public static final String RIDER_PICKUP_LOCATION_STRING = "PickupLocationString";
    public static final String RIDER_DESTINATION_STRING = "destinationLocationString";
    public static final String RIDER_DESTINATION = "destinationLocation";
    public static final String REQUEST_DRIVER_ACCEPT = "Accept";
    public static final String TRIP_KEY = "TripKey" ;


    public static List<Chifor> Drivers_Locations_List = new ArrayList<>();
    public static Map<String,DriverGeoModel> driversFound = new HashMap<>();
    public static HashMap<String, Marker> marerList = new HashMap<String, com.mapbox.mapboxsdk.annotations.Marker>();

    public static void SetWelcomeMessage(TextView textView){
        int houre = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (houre>=1 && houre <= 12 )
            textView.setText(new StringBuilder(" صباح الخير "+Current_Client_DispalyName));
        else
            textView.setText(new StringBuilder(" مساء الخير "+Current_Client_DispalyName));
    }


    //DECODE POLY
    public static List<LatLng> decodePoly(String encoded) {
        List poly = new ArrayList();
        int index=0,len=encoded.length();
        int lat=0,lng=0;
        while(index < len)
        {
            int b,shift=0,result=0;
            do{
                b=encoded.charAt(index++)-63;
                result |= (b & 0x1f) << shift;
                shift+=5;

            }while(b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1):(result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do{
                b = encoded.charAt(index++)-63;
                result |= (b & 0x1f) << shift;
                shift +=5;
            }while(b >= 0x20);
            int dlng = ((result & 1)!=0 ? ~(result >> 1): (result >> 1));
            lng +=dlng;

            LatLng p = new LatLng((((double)lat / 1E5)),
                    (((double)lng/1E5)));
            poly.add(p);
        }
        return poly;
    }
}

