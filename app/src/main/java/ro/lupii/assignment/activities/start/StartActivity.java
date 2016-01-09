package ro.lupii.assignment.activities.start;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import ro.lupii.assignment.R;
import ro.lupii.assignment.data.User;
import ro.lupii.assignment.activities.users.UserListActivity;

public class StartActivity extends AppCompatActivity implements
        LoginFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //TODO (Andrei) remove this shit
        //ArrayList<User> users = new ArrayList<>();
        //users.add(User.buildUser("andrei"));
        //users.add(User.buildUser("john"));
        //users.add(User.buildUser("marinela"));
        //Intent i = new Intent(this, UserListActivity.class);
        //i.putParcelableArrayListExtra(UserListActivity.KEY_USERLIST, users);
        //startActivity(i);
        //finish();
        //shit ends here

        if (savedInstanceState == null) {
            showFragment(new LoginFragment(), true);
        }
    }

    private void showFragment(Fragment f, boolean animate) {
        FragmentTransaction tr = getFragmentManager().beginTransaction();
        if (animate)
            tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        tr.replace(R.id.fragment_container, f);
        tr.commit();
    }

    @Override
    public void onLogin(List<User> allUsers) {
        Intent i = new Intent(this, UserListActivity.class);
        ArrayList<User> users = new ArrayList<>();
        users.addAll(allUsers);
        i.putParcelableArrayListExtra(UserListActivity.KEY_USERLIST, users);
        startActivity(i);
    }
}
