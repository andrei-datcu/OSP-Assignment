package ro.lupii.assignment.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import ro.lupii.assignment.activities.conversation.ConversationActivity;
import ro.lupii.assignment.data.Message;

public class CommService extends Service implements SocketThread.OnMessageArrivedListener {
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    private SocketThread socketThread;

    @Override
    public void onMessageArrived(String message) {
        //TODO (John) parse JSON here

        Message m = null;
        Intent i = new Intent(ConversationActivity.NEW_MESSAGE_ACTION);
        i.putExtra(ConversationActivity.KEY_MESSAGE, m);
        sendBroadcast(i);
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
        socketThread.start();
    }

    @Override
    public void onDestroy() {
        socketThread.closeThread();
        socketThread.interrupt();
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
