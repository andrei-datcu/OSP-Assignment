package ro.lupii.assignment.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.field.DatabaseField;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrei on 1/9/16.
 */
public class User implements Parcelable{
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(unique = true)
    private String username;

    private Boolean favorite = true;

    public int getId() {
        return id;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public User() {
        //Empty constructor needed by ORMLITE
    }

    private User(String username) {
        this.username = username;
        this.favorite = false;
    }

    public static User buildUser(String username) {
        List<User> queryResult = dao.queryForEq("username", username);
        if (queryResult.size() == 0)
            return new User(username);
        else return queryResult.get(0);
    }

    private static RuntimeExceptionDao<User, Integer> dao;
    public static void setDao(RuntimeExceptionDao<User, Integer> dao) {
        User.dao = dao;
    }

    public void deleteFavorite() {
        dao.delete(this);
    }

    public void makeFavorite() {
        dao.create(this);
    }

    public static List<User> getAllFavorites() {
        ArrayList<User> result = new ArrayList<>();

        for (User u : dao)
            result.add(u);

        return result;
    }

    public static int totalFavoriteCount() {
        return (int)dao.countOf();
    }

    @Override
    public boolean equals(Object o) {
        return o.getClass().equals(getClass()) && id == ((User)o).id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public String getUsername() {
        return username;
    }


    protected User(Parcel in) {
        id = in.readInt();
        username = in.readString();
        favorite = in.readByte() != 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(username);
        dest.writeByte((byte)(favorite ? 1: 0));
    }
}
