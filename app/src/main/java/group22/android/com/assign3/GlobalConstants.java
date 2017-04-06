package group22.android.com.assign3;

import java.util.ArrayList;

/**
 * Created by Lakshmisagar on 3/22/2017.
 */

public class GlobalConstants {
    static String TableName  = "SonsorTable";
    static String Col_ID = "id";
    static String Accel_X  = "Accel_X";
    static String Accel_Y  = "Accel_Y";
    static String Accel_Z  = "Accel_Z";
    static String ActivityLabel  = "Activity_Label";
    static StringBuilder CREATE_TABLE = new StringBuilder("CREATE TABLE "+ TableName +"(" + Col_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, ");
    static String DELETE_TABLE =  "DROP TABLE IF EXISTS " +TableName;
    static String CHECK_TABLE = "SELECT name FROM sqlite_master WHERE type='table' AND name ="+TableName;
    static int sensorCount = 0;
    static boolean isDBupdated = false;
    static String Activity_type = "Running";
    static String FileName = " Sensor_data";
    static ArrayList<DataValues> valueHolder = new ArrayList<DataValues>();
    static ArrayList<DataValues> valueHolderClassify = new ArrayList<DataValues>();
}
