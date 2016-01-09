package ro.lupii.assignment.activities.start;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

import ro.lupii.assignment.R;
import ro.lupii.assignment.data.User;

public class StartActivity extends AppCompatActivity implements
        LoginFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

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
        // TODO (Andrei) start new activity here showing all users
    }
}
