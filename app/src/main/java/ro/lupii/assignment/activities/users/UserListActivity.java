package ro.lupii.assignment.activities.users;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import ro.lupii.assignment.R;
import ro.lupii.assignment.activities.conversation.ConversationActivity;
import ro.lupii.assignment.data.User;

public class UserListActivity extends AppCompatActivity {

    public static final String KEY_USERLIST = "userlist";

    ArrayList<User> allUsers;
    ListView mUserListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        mUserListView = (ListView) findViewById(R.id.user_list);

        Intent i = getIntent();
        allUsers = i.getParcelableArrayListExtra(KEY_USERLIST);

        UserArrayAdapter adapter = new UserArrayAdapter(this, R.id.user_list, allUsers);
        mUserListView.setAdapter(adapter);
        mUserListView.setClickable(true);
        mUserListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(UserListActivity.this, ConversationActivity.class);
                i.putExtra(ConversationActivity.KEY_USER, allUsers.get(position));
                startActivity(i);
            }
        });
    }
}
