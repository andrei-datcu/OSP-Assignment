package ormconfig;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

/**
 * Created by andrei on 9/14/15.
 */
public class DBConfigUtil extends OrmLiteConfigUtil {

    /**
     * To make this work in Android Studio, you may need to update your
     * Run Configuration as explained here:
     *   http://stackoverflow.com/a/17332546
     */

    final public static Class<?> classes[] = new Class<?>[]{
            ro.lupii.assignment.data.User.class,
            ro.lupii.assignment.data.Message.class,
    };

    public static void main(String[] args) throws Exception {
        writeConfigFile("ormlite_config.txt", classes);
    }
}
