package com.TaxiSghira.TreeProg.plashscreen.Commun;

import com.TaxiSghira.TreeProg.plashscreen.API.FireBaseClient;

public class Commun {

    public static final String Current_Client_Id = FireBaseClient.getFireBaseClient().getUserLogEdInAccount().getId();
    public static final String Current_Client_DispalyName = FireBaseClient.getFireBaseClient().getUserLogEdInAccount().getDisplayName();
}
