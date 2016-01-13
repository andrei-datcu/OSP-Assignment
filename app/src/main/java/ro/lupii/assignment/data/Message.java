package ro.lupii.assignment.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.field.DatabaseField;

/**
 * Created by andrei on 1/10/16.
 */
public class Message implements Parcelable {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true)//, foreignAutoRefresh = true, columnName = "user_id", canBeNull = false)
    private User user;

    @DatabaseField
    private String message;

    @DatabaseField
    private boolean iSaidIt;

    public Message() {
        // Needed by ORMLITE
    }

    private Message(String message, User user, boolean ownMessage) {
        this.message = message;
        this.user = user;
        this.iSaidIt = ownMessage;
    }

    public static Message buildMessage(String message, User user, boolean ownMessage) {
        Message m = new Message(message, user, ownMessage);
        dao.create(m);
        return m;
    }

    private static RuntimeExceptionDao<Message, Integer> dao;
    public static void setDao(RuntimeExceptionDao<Message, Integer> dao) {
        Message.dao = dao;
    }

    public boolean isOwnMessage() {
        return iSaidIt;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }

    protected Message(Parcel in) {
        id = in.readInt();
        message = in.readString();
        iSaidIt = in.readByte() != 0;
        user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(message);
        dest.writeByte((byte)(iSaidIt ? 1: 0));
        dest.writeParcelable(getUser(), flags);
    }
}
