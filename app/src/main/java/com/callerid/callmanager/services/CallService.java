package com.callerid.callmanager.services;

import android.app.Service;
import android.telecom.Call;
import android.telecom.InCallService;
import android.util.Log;

public class CallService extends InCallService {
    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);
        Log.e("MyInCallService", "Call added: " + call.toString());
    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        Log.e("MyInCallService", "Call removed: " + call.toString());
    }
}
