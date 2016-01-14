package ro.lupii.assignment.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import ro.lupii.assignment.R;
import ro.lupii.assignment.activities.conversation.ConversationActivity;
import ro.lupii.assignment.data.Message;
import ro.lupii.assignment.data.User;

public class CommService extends Service implements SocketThread.OnMessageArrivedListener {
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    private SocketThread socketThread;
    private int notificationID = 100;

    @Override
    public void onMessageArrived(String message) {
        //TODO (John) parse JSON here
        JSONObject jsonObject;
        Message m = null;
        Intent i = null;

        try {
            jsonObject = new JSONObject(message);
            if (jsonObject.getInt("type") != 1)
                return;

            User u = User.buildUser(jsonObject.getString("user"));
            m = Message.buildMessage(jsonObject.getString("message"), u, false);
            i = new Intent(ConversationActivity.NEW_MESSAGE_ACTION);
            i.putExtra(ConversationActivity.KEY_MESSAGE, m);
            i.putExtra(ConversationActivity.KEY_NOTIFICATION_ID, notificationID);
            publishNotification(u, notificationID++);
            sendBroadcast(i);
        } catch (JSONException e) {
            Log.e("OSP", "Failed to read JSON");
            return;
        }

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

    private Notification buildNotification(User u) {
        Intent notificationIntent = new Intent(this, ConversationActivity.class);
        notificationIntent.putExtra(ConversationActivity.KEY_USER, u);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        return new Notification.Builder(this).
                setContentTitle(getString(R.string.new_message)).
                setSmallIcon(R.drawable.ic_message).
                setContentText(getString(R.string.from) + u.getUsername()).
                setAutoCancel(true).
                setContentIntent(pendingIntent).build();
    }

    private void publishNotification(User u, int id) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(id, buildNotification(u));
    }
}
