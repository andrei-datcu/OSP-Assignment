package ro.lupii.assignment.activities.start;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ro.lupii.assignment.R;
import ro.lupii.assignment.data.OSPSocket;
import ro.lupii.assignment.data.User;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class LoginFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    //Keep track of the login task to ensure we can cancel it if requested.
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private EditText mConfirmPassword;
    private View mProgressView;
    private View mLoginFormView;
    private TextView errorText;

    private final static String KEY_ERR_VISIBLE = "ERR_VISIBLE";
    private final static String KEY_PROGRESS_VISIBLE = "PROGRESS_VISIBLE";

    private boolean progressShowing = false;
    private String lastError = null;
    private boolean createMode = false;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        mUsernameView = (EditText) v.findViewById(R.id.username);
        mPasswordView = (EditText) v.findViewById(R.id.password);
        mConfirmPassword = (EditText) v.findViewById(R.id.confirm_password);

        final Button mEmailSignInButton = (Button) v.findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputManager = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                View v = getActivity().getCurrentFocus();
                if (v != null)
                    inputManager.hideSoftInputFromWindow(v.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                attemptLogin();
            }
        });

        Button mNewAccountButton = (Button) v.findViewById(R.id.new_account);
        final LinearLayout login_only_view = (LinearLayout) v.findViewById(R.id.login_only_controls);
        mNewAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO (Andrei) Make logic to also reset createMode to false
                if (createMode) {
                    mEmailSignInButton.callOnClick();
                } else {
                    createMode = true;
                    login_only_view.setVisibility(View.GONE);
                    mConfirmPassword.setVisibility(View.VISIBLE);
                }
            }
        });

        mLoginFormView = v.findViewById(R.id.login_form);
        mProgressView = v.findViewById(R.id.login_progress);
        errorText = (TextView) v.findViewById(R.id.error_text);
        if (lastError != null) {
            errorText.setVisibility(View.VISIBLE);
            errorText.setText(lastError);
        }

        if (progressShowing)
            showProgress(true);
        return v;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);
        errorText.setVisibility(View.GONE);
        lastError = null;

        // Store values at the time of the login attempt.
        String email = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.mandatory_field));
            focusView = mPasswordView;
            cancel = true;
        }

        if (createMode && !password.equals(mConfirmPassword.getText().toString())) {
            mPasswordView.setError(getString(R.string.same_password));
            focusView = mConfirmPassword;
            cancel = true;
        }

        // Check for a valid user name
        if (TextUtils.isEmpty(email)) {
            mUsernameView.setError(getString(R.string.mandatory_field));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(email)) {
            mUsernameView.setError(getString(R.string.error_invalid_email));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    enum uri_states {STATE_USER, STATE_DOMAIN}
    private boolean isUsernameValid(String email) {
        //TODO(John): Replace this with your own logic
        uri_states state = uri_states.STATE_USER;
        char c;

        for (int i=0; i < email.length(); i++) {
            c = email.charAt(i);
            switch (state) {
                /* FIXME did you cover all cases?!*/
                case STATE_USER:
                    if (c == '@') {
                        state = uri_states.STATE_DOMAIN;
                        break;
                    }
                    if (!Character.isLetterOrDigit(c) && c != '.' && c != '-' && c != '_')
                        return false;
                    break;
                case STATE_DOMAIN:
                    if (!Character.isLetterOrDigit(c) && c!='.' && c != ':'/*IPV6*/) {
                        return false;
                    }
                    break;
            }
        }

        if (state != uri_states.STATE_DOMAIN)
            return false;

        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_ERR_VISIBLE, errorText.getVisibility() == View.VISIBLE);
        outState.putBoolean(KEY_PROGRESS_VISIBLE, mProgressView.getVisibility() == View.VISIBLE);
    }

    public void showProgress(final boolean show) {

        progressShowing = show;
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private List<User> users;
        private Exception e;

        private OSPSocket sock=null;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
            users = new ArrayList<>();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            JSONObject jobj = new JSONObject();
            JSONArray usersArr;
            String response="", rLine;

            try {
                //TODO(John) authentication logic here
                // you may use createMode here to check if you create or you login
                // also get all users list here
                sock = new OSPSocket();

                jobj.put("user", this.mEmail);
                jobj.put("pass", this.mPassword);

                if (createMode) {
                    jobj.put("create", true);
                }

                this.sock.writeString(jobj.toString());

                response = this.sock.readAll();

                /* FIXME if error from server (response != 0) here program dies */
                if (response == null) {
                    /* we're doomed */
                    lastError = "No response from the server";
                    return false;
                }

                jobj = new JSONObject(response);

                /* FIXME if error from server (response != 0) here program dies */
                if ((Integer)jobj.get("response") != 0) {
                    lastError = (String)jobj.get("message");
                    return false;
                }

                users = new ArrayList<User>();

                /* everything ok! get user list */
                usersArr = jobj.getJSONArray("users");
                for (int i=0; i < usersArr.length(); i++) {
                    if (this.mEmail.compareTo((String)usersArr.get(i)) == 0)
                        continue;
                    this.users.add(User.buildUser((String)usersArr.get(i)));
                }

                return true;
            } catch (Exception e) {
                this.e = e;
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                if (mListener != null)
                    mListener.onLogin(users);
            } else {
                lastError = "Plm" + e.getLocalizedMessage();
                errorText.setText(lastError);
                errorText.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onLogin(List<User> allUsers);
    }

}
