package ro.lupii.assignment.data.db;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;


import ro.lupii.assignment.data.User;

/**
 * Created by andrei on 9/14/15.
 */
public class DatabaseManager {
    private static DatabaseHelper helper = null;

    public static void init(Context c) {
        if (helper == null) {
            helper = OpenHelperManager.getHelper(c, DatabaseHelper.class);
            initModelClasses();
        }
    }

    private static void initModelClasses() {
        User.setDao((RuntimeExceptionDao<User, Integer>)
                helper.getRuntimeExceptionDao(User.class));
    }

    private static void terminateModelClasses() {
        User.setDao(null);
    }

    public static void terminate() {
        helper.close();
        OpenHelperManager.releaseHelper();
        terminateModelClasses();
    }

    public static DatabaseHelper getHelper () {
        return helper;
    }
}
