package ro.lupii.assignment.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class CommService extends Service implements SocketThread.OnMessageArrivedListener {
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    private SocketThread socketThread;

    @Override
    public void onMessageArrived(String message) {
        //TODO (Andrei) message received logic here
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public CommService getService() {
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
        socketThread = new SocketThread(this);
        socketThread.run();
    }

    public String login(String message) throws Exception {
        try {
            socketThread.sendMessage(message);
            return socketThread.readMessage();
        } catch (Exception e) {
            socketThread.sendMessage(message);
            return socketThread.readMessage();
        }
    }

    public void sendMessage(String message) {
        socketThread.sendMessage(message);
    }
}
