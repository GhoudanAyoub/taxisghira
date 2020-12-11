package com.TaxiSghira.TreeProg.plashscreen.Room;

import androidx.lifecycle.LiveData;

import com.TaxiSghira.TreeProg.plashscreen.Callback.Doa;
import com.TaxiSghira.TreeProg.plashscreen.Module.Chifor;

import java.util.List;

import javax.inject.Inject;

public class Repository {
    private Doa doa;

    @Inject
    public Repository(Doa doa) {
        this.doa = doa;
    }

    public void InsertData(Chifor chifor){doa.InsertData(chifor);}
    public void DeleteData(int id){doa.DeleteData(id);}
    public LiveData<List<Chifor>> GetData(){return doa.GetData();}
}
