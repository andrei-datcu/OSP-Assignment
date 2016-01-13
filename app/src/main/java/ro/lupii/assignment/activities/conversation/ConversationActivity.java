package ro.lupii.assignment.activities.conversation;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
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
    final public static String KEY_NOTIFICATION_ID = "notification_ID";

    private ListView messageListView;
    private ImageView sendButton;
    private EditText sendMessageEdit;
    private User u;
    private BroadcastReceiver receiver;
    private ConversationArrayAdapter listAdapter;
    private ArrayList<Message> messages;
    private boolean mBound = false;
    private CommService mService = null;
    private NotificationManager mNotificationManager;

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
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        messageListView = (ListView) findViewById(R.id.message_list);
        u = getIntent().getParcelableExtra(KEY_USER);
        bindService(new Intent(this, CommService.class), mConnection, Context.BIND_AUTO_CREATE);

        sendMessageEdit = (EditText) findViewById(R.id.chat_edit_text);
        sendMessageEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    sendMessage(sendMessageEdit.getText().toString());
                    return true;
                }
                return false;
            }
        });

        sendMessageEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0)
                    disableSendButton();
                else
                    enableSendButton();
            }
        });

        messages = u.getAllMessages();
        listAdapter = new ConversationArrayAdapter(this, R.id.message_list, messages, u.getUsername());
        messageListView.setAdapter(listAdapter);
        messageListView.setSelection(messages.size() - 1);
        sendButton = (ImageView) findViewById(R.id.enter_chat);
        enableSendButton();
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(sendMessageEdit.getText().toString());
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(NEW_MESSAGE_ACTION);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Message m = intent.getParcelableExtra(KEY_MESSAGE);
                if (m.getUser().getUsername().equals(u.getUsername())) {
                    messages.add(m);
                    listAdapter.notifyDataSetChanged();
                    messageListView.setSelection(messages.size() - 1);
                    mNotificationManager.cancel(intent.getIntExtra(KEY_NOTIFICATION_ID, 0));
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
        JSONObject jObj = new JSONObject();
        JSONArray jArr = new JSONArray();
        String jsonObj = null;

        try {
            jArr.put(0, u.getUsername());
            jObj.put("users", jArr);
            jObj.put("message", message);
            jsonObj = jObj.toString();

            //TODO remove me
            Log.d("OSP", jObj.toString(1));
        } catch (JSONException e) {
            Log.e("OSP", "failed to populate JSON");
            return;
        }

        mService.sendMessage(jsonObj);
        messages.add(Message.buildMessage(message, u, true));
        listAdapter.notifyDataSetChanged();
        sendMessageEdit.setText("");
        disableSendButton();
    }

    private void disableSendButton() {
        sendButton.setImageResource(R.drawable.ic_chat_send);
        sendButton.setClickable(false);
    }

    private void enableSendButton() {
        sendButton.setImageResource(R.drawable.ic_chat_send_active);
        sendButton.setClickable(true);
    }
}
