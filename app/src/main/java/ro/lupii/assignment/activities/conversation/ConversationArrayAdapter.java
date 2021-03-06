package ro.lupii.assignment.activities.conversation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ro.lupii.assignment.R;
import ro.lupii.assignment.data.Message;

/**
 * Created by andrei on 1/10/16.
 */
public class ConversationArrayAdapter extends ArrayAdapter<Message> {
    private String username;

    public ConversationArrayAdapter(Context context, int resource, List<Message> objects, String username) {
        super(context, resource, objects);
        this.username = username;
    }

    private class ViewInfo {
        public TextView messageView;
        public TextView dateView;
        public String message;
        public boolean ownMessage;

        void setMessage(String message) {
            this.message = message;
            this.messageView.setText(message);
        }

        void setDate(Date date) {
            Date now = new Date();
            SimpleDateFormat cmpFmt = new SimpleDateFormat("yyyyMMdd"), fmt = null;
            if (cmpFmt.format(now).equals(cmpFmt.format(date))) {
                // same day, show only hour
                fmt = new SimpleDateFormat("HH:mm");
            } else {
                fmt  = new SimpleDateFormat("d MMM");
            }
            dateView.setText(fmt.format(date));
        };
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Message m = getItem(position);

        convertView = null; //TODO(Andrei) figure out why this is needed - it should work without
        ViewInfo viewInfo = convertView == null ? new ViewInfo() : (ViewInfo) convertView.getTag();

        if (convertView == null || viewInfo.ownMessage != m.isOwnMessage()) {
            LayoutInflater inflater =
                    (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(m.isOwnMessage() ? R.layout.chat_user2_item : R.layout.chat_user1_item, parent, false);

            if (!m.isOwnMessage()) {
                TextView userView = (TextView) convertView.findViewById(R.id.reply_author);
                userView.setText(this.username);
            }

            viewInfo.messageView = (TextView) convertView.findViewById(R.id.message_text);
            viewInfo.dateView = (TextView) convertView.findViewById(R.id.time_text);
            viewInfo.ownMessage = m.isOwnMessage();
            convertView.setTag(viewInfo);
        }
        viewInfo.setMessage(m.getMessage());
        viewInfo.setDate(m.getDate());
        return convertView;
    }
}
