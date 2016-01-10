package ro.lupii.assignment.activities.users;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;

import ro.lupii.assignment.R;
import ro.lupii.assignment.data.User;

/**
 * Created by andrei on 1/9/16.
 */
public class UserArrayAdapter extends ArrayAdapter<User>{
    public UserArrayAdapter(Context context, int resource, List<User> objects) {
        super(context, resource, objects);
    }

    private static class ViewInfo {
        public TextView userName;
        public ToggleButton favoriteButton;

        private class UserContainer {
            public User u;
        }
        private UserContainer user;

        public ViewInfo(TextView userName, ToggleButton favBtn, User u) {
            this.user = new UserContainer();
            this.userName = userName;
            this.favoriteButton = favBtn;
            this.favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user.u.setFavorite(favoriteButton.isChecked());
                }
            });
            setUser(u);
        }

        public void setUser(User user) {
            this.user.u = user;
            this.favoriteButton.setChecked(user.getFavorite());
            this.userName.setText(user.getUsername());
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final User u = getItem(position);
        ViewInfo viewInfo;

        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.user_list_item, parent, false);

            TextView username = (TextView) convertView.findViewById(R.id.usernametv);
            ToggleButton favButton = (ToggleButton) convertView.findViewById(R.id.favButton);
            viewInfo = new ViewInfo(username, favButton, u);
            convertView.setTag(viewInfo);
        } else {
            viewInfo = (ViewInfo) convertView.getTag();
            viewInfo.setUser(u);
        }
        return convertView;
    }
}
