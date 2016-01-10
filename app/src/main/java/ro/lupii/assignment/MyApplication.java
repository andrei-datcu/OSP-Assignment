package ro.lupii.assignment;

import android.app.Application;
import android.content.Intent;

import ro.lupii.assignment.data.db.DatabaseManager;
import ro.lupii.assignment.services.CommService;

/**
 * Created by andrei on 1/9/16.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize the singletons so their instances
        // are bound to the application process.
        DatabaseManager.init(this);
        startService(new Intent(this, CommService.class));
    }
}
