package ro.lupii.assignment.activities.users;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ro.lupii.assignment.R;
import ro.lupii.assignment.activities.conversation.ConversationActivity;
import ro.lupii.assignment.activities.start.StartActivity;
import ro.lupii.assignment.data.User;
import ro.lupii.assignment.services.CommService;

public class UserListActivity extends AppCompatActivity {

    public static final String KEY_USERLIST = "userlist";
    public static final String KEY_USER = "myuser";

    ArrayList<User> allUsers, displayedUsers;
    ListView mUserListView;
    UserArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        LayoutInflater inflater = (LayoutInflater) actionBar
                .getThemedContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View customActionBarView = inflater.inflate(R.layout.user_action_bar, null);
        TextView usernameTextView = (TextView) customActionBarView.findViewById(R.id.user_name);
        ImageView logoutButton = (ImageView) customActionBarView.findViewById(R.id.logout);

        actionBar.setDisplayOptions(
                ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
                        | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setCustomView(customActionBarView,
                new ActionBar.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));

        setContentView(R.layout.activity_user_list);
        mUserListView = (ListView) findViewById(R.id.user_list);

        Intent i = getIntent();
        if (i.hasExtra(KEY_USERLIST))
            allUsers = i.getParcelableArrayListExtra(KEY_USERLIST);
        else
            allUsers = User.getAllUsers();
        displayedUsers = new ArrayList<>();
        displayedUsers.addAll(allUsers);

        adapter = new UserArrayAdapter(this, R.id.user_list, displayedUsers);
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

        usernameTextView.setText(CommService.myUsername);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(UserListActivity.this, CommService.class));
                startActivity(new Intent(UserListActivity.this, StartActivity.class));
                finish();
            }
        });
    }

    public void onChangeSelection(View v) {
        displayedUsers.clear();
        CheckBox cb = (CheckBox)v;
        for (User u : allUsers)
            if (cb.isChecked() && u.getFavorite() || !cb.isChecked())
                displayedUsers.add(u);
        adapter.notifyDataSetChanged();
    }
}
