package com.TaxiSghira.TreeProg.plashscreen.Commun;

import androidx.constraintlayout.solver.widgets.Helper;

import com.TaxiSghira.TreeProg.plashscreen.API.FireBaseClient;
import com.TaxiSghira.TreeProg.plashscreen.Module.Chifor;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

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
}

