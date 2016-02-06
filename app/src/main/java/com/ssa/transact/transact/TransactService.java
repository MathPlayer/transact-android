package com.ssa.transact.transact;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TransactService extends Service {
    private final TransactBinder binder = new TransactBinder();

    public TransactService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        binder.onCreate(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return (binder);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        binder.onDestroy();
    }
}
