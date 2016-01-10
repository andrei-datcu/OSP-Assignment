package ro.lupii.assignment.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class CommService extends Service {
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        CommService getService() {
            // Return this instance of LocalService so clients can call public methods
            return CommService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {

    }
}
