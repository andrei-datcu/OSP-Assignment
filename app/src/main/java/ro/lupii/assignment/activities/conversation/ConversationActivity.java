package ro.lupii.assignment.activities.conversation;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

import ro.lupii.assignment.R;
import ro.lupii.assignment.data.Message;
import ro.lupii.assignment.data.User;
import ro.lupii.assignment.services.CommService;

public class ConversationActivity extends AppCompatActivity {

    final public static String KEY_USER = "user";
    final public static String KEY_MESSAGE = "message";
    final public static String NEW_MESSAGE_ACTION = "NEW_MESSAGE";

    private ListView messageListView;
    private ImageView sendButton;
    private User u;
    private BroadcastReceiver receiver;
    private ConversationArrayAdapter listAdapter;
    private ArrayList<Message> messages;
    private boolean mBound = false;
    private CommService mService = null;

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            CommService.LocalBinder binder = (CommService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        messageListView = (ListView) findViewById(R.id.message_list);
        u = getIntent().getParcelableExtra(KEY_USER);
        bindService(new Intent(this, CommService.class), mConnection, Context.BIND_AUTO_CREATE);

        final EditText sendMessageEdit = (EditText) findViewById(R.id.chat_edit_text);
        sendMessageEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    Log.d("OSPPP", "Should send now...");
                    sendMessage(sendMessageEdit.getText().toString());
                    return true;
                }
                return false;
            }
        });

        messages = u.getAllMessages();
        if (messages != null)
            listAdapter = new ConversationArrayAdapter(this, R.id.message_list, messages, u.getUsername());
        else
            listAdapter = new ConversationArrayAdapter(this, R.id.message_list, u.getUsername());

        messageListView.setAdapter(listAdapter);
        sendButton = (ImageView) findViewById(R.id.enter_chat);

        IntentFilter filter = new IntentFilter();
        filter.addAction(NEW_MESSAGE_ACTION);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Message m = intent.getParcelableExtra(KEY_MESSAGE);
                if (m.getUser().getUsername().equals(u.getUsername())) {
                    messages.add(m);
                    listAdapter.notifyDataSetChanged();
                }
            }
        };
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        unbindService(mConnection);
    }

    private void sendMessage(String message) {
        //TODO(John) send message logic here
        String jsonObj = null;

        mService.sendMessage(jsonObj);
        messages.add(Message.buildMessage(message, u, true));
        listAdapter.notifyDataSetChanged();
        sendButton.setImageResource(R.drawable.ic_chat_send);
        sendButton.setClickable(false);
    }
}
