package ro.lupii.assignment.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class CommService extends Service {
    public CommService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
