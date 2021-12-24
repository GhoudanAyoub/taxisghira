package com.TaxiSghira.TreeProg.plashscreen.Room;

import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.Query;

import com.TaxiSghira.TreeProg.plashscreen.Callback.Doa;
import com.TaxiSghira.TreeProg.plashscreen.Callback.IFCMService;
import com.TaxiSghira.TreeProg.plashscreen.Callback.IGoogleAPI;
import com.TaxiSghira.TreeProg.plashscreen.Module.Chifor;
import com.TaxiSghira.TreeProg.plashscreen.Module.FCMResponse;
import com.TaxiSghira.TreeProg.plashscreen.Module.FCMSendData;
import com.TaxiSghira.TreeProg.plashscreen.Module.YourLocations;
import com.TaxiSghira.TreeProg.plashscreen.Module.routes;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

public class Repository {
    private final Doa doa;
    private final IFCMService ifcmService;
    private final IGoogleAPI iGoogleAPI;

    @Inject
    public Repository(Doa doa, IFCMService ifcmService, IGoogleAPI iGoogleAPI) {
        this.doa = doa;
        this.ifcmService = ifcmService;
        this.iGoogleAPI = iGoogleAPI;
    }

    public void InsertData(Chifor chifor) {
        doa.InsertData(chifor);
    }

    public void InsertLocation(YourLocations yourLocations){doa.InsertLocation(yourLocations);}

    public void DeleteData(int id) {
        doa.DeleteData(id);
    }

    public LiveData<List<Chifor>> GetData() {
        return doa.GetData();
    }

    public LiveData<List<YourLocations>> GetLocation(){return doa.GetLocation();}

    public Observable<routes> getDirections(String profile, String to, String access_token) {
        return iGoogleAPI.getDirection(profile, to, access_token);
    }

    public Observable<FCMResponse> sendNotification(FCMSendData body) {
        return ifcmService.sendNotification(body);
    }
}
