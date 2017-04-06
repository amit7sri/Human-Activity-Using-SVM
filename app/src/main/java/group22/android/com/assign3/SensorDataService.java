package group22.android.com.assign3;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import static group22.android.com.assign3.GlobalConstants.Activity_type;
import static group22.android.com.assign3.GlobalConstants.sensorCount;
import static group22.android.com.assign3.GlobalConstants.valueHolder;
import static group22.android.com.assign3.GlobalConstants.valueHolderClassify;

/**
 * Created by Lakshmisagar on 3/22/2017.
 */

public class SensorDataService extends Service implements SensorEventListener{
    private static String TAG = SensorDataService.class.getName().toString();
    private SensorManager sensorManager;
    private Sensor accl_Sensor;
    private int timeInMillis = 1000;
    static SQLiteDatabase sqLiteDatabase;
    static ContentValues values;
    MyDB myDB;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("SAGAR   "+TAG,"onStartCommand()");
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("SAGAR  "+TAG,"onCreate()");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accl_Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this,accl_Sensor,timeInMillis);
        values = new ContentValues();

        myDB = new MyDB(this, "Assign3_Group22_DB", null, 1);
        sqLiteDatabase = myDB.getWritableDatabase();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("SAGAR   "+TAG,"onDestroy()");
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("SAGAR","onSensorChanged()");
        Sensor sensor = event.sensor;
        if(sensor.getType()==Sensor.TYPE_ACCELEROMETER && sensorCount < 50){
            //DataValues accelrometerData = new DataValues(event.values[0],event.values[1],event.values[2]);
            if(Activity_type.equals("classify")){
                valueHolderClassify.add(new DataValues(event.values[0],event.values[1],event.values[2]));
            }else {
                valueHolder.add(new DataValues(event.values[0],event.values[1],event.values[2]));
                Log.d(TAG, "valueHolder size  " + valueHolder.size());
            }
            sensorCount++;
        }else if(sensorCount>=50 && sqLiteDatabase != null) {
            sensorCount++;
            if(sensorManager!=null) {
                sensorManager.unregisterListener(this);
            }
            if(!Activity_type.equals("classify")) {
                MyDB.insertIntoDB(sqLiteDatabase);
            }
        }
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}
