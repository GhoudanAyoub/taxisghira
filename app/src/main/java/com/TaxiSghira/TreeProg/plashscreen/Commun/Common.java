package com.TaxiSghira.TreeProg.plashscreen.Commun;

import android.widget.TextView;

import com.TaxiSghira.TreeProg.plashscreen.API.FireBaseClient;
import com.TaxiSghira.TreeProg.plashscreen.Module.Chifor;
import com.TaxiSghira.TreeProg.plashscreen.Module.DriverGeoModel;
import com.mapbox.mapboxsdk.annotations.Marker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Common {

    public static final String Current_Client_Id = FireBaseClient.getFireBaseClient().getFirebaseUser().getUid();
    public static final String Current_Client_DispalyName = FireBaseClient.getFireBaseClient().getFirebaseUser().getDisplayName();
    public static final String Current_Client_Gmail = FireBaseClient.getFireBaseClient().getFirebaseUser().getEmail();
    public static final String Favor_DataBase_Table = "Favor";
    public static final String Chifor_DataBase_Table = "Chifor";
    public static final String Demande_DataBase_Table = "Demande";
    public static final String Pickup_DataBase_Table = "Pickup";
    public static final String Client_DataBase_Table = "Client";

    public static final String Client_Id_String = "id";
    public static final String Gmail_String = "gmail";
    public static final String ClientName_String = "ClientName";
    public static final String CLIENT_LOCATION_REFERENCES = "ClientLocation";
    public static final String Drivers_LOCATION_REFERENCES = "DriversLocation";
    public static final String city = "city";
    public static List<Chifor> Drivers_Locations_List = new ArrayList<>();
    public static Set<DriverGeoModel> driversFound = new HashSet<>();
    public static HashMap<String, Marker> marerList = new HashMap<String, com.mapbox.mapboxsdk.annotations.Marker>();

    public static void SetWelcomeMessage(TextView textView){
        int houre = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (houre>=1 && houre <= 12 )
            textView.setText(new StringBuilder(" صباح الخير "+Current_Client_DispalyName));
        else
            textView.setText(new StringBuilder(" مساء الخير "+Current_Client_DispalyName));
    }
}

