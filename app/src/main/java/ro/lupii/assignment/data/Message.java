package ro.lupii.assignment.data;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.field.DatabaseField;

/**
 * Created by andrei on 1/10/16.
 */
public class Message {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "user_id")
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
}
