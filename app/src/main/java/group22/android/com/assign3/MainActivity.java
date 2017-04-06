package group22.android.com.assign3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static group22.android.com.assign3.GlobalConstants.Activity_type;
import static group22.android.com.assign3.GlobalConstants.isDBupdated;
import static group22.android.com.assign3.GlobalConstants.sensorCount;
import static group22.android.com.assign3.GlobalConstants.valueHolder;
import static group22.android.com.assign3.GlobalConstants.valueHolderClassify;

public class MainActivity extends AppCompatActivity {
    private static String TAG = MainActivity.class.getName().toString();
    Intent serviceIntent;

    TextView walkingtv;
    TextView runningtv;
    TextView eatingtv;
    TextView accuracytv;
    TextView classifytv;
    int accuracy_per;
    String activity;
    int count_walking = 0;
    int count_running = 0;
    int count_eating = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("SAGAR  :" + TAG, "  onCreate()");
        setContentView(R.layout.activity_main);
        serviceIntent = new Intent(this, SensorDataService.class);

        walkingtv = (TextView) findViewById(R.id.walking_count);
        runningtv = (TextView) findViewById(R.id.running_count);
        eatingtv = (TextView) findViewById(R.id.eating_count);
        classifytv = (TextView) findViewById(R.id.classifytv);
        accuracytv = (TextView) findViewById(R.id.accuracytv);

    }

    public class LoadData extends AsyncTask<Void, Integer, Void> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            Log.d("SAGAR  :" + TAG, "  onPreExecute()");

            progressDialog = ProgressDialog.show(MainActivity.this, Activity_type, "Collecting Sensor Data", true);
            progressDialog.setMax(2);
            progressDialog.show();
            sensorCount = 0;
            valueHolder = new ArrayList<DataValues>();
            valueHolderClassify = new ArrayList<DataValues>();
            startService(serviceIntent);
            isDBupdated = false;
        }

        ;

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("SAGAR  :" + TAG, "  doInBackground()");
            for (int i = 0; i < 2; i++) {
                publishProgress(1);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.incrementProgressBy(values[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d(TAG, "  onPostExecute()");
            super.onPostExecute(result);
            progressDialog.dismiss();
            isDBupdated = true;
            stopService(serviceIntent);
            if (Activity_type.equals("classify")) {
                AndroidLibSVM androidLibSVM = new AndroidLibSVM();
                activity = androidLibSVM.classify(valueHolderClassify);
                Toast.makeText(getApplicationContext(), activity, Toast.LENGTH_LONG).show();
            }
        }

        ;
    }

    public void onWalkingClick(View view) {
        Log.d(TAG, "  onWalkingClick()");
        Activity_type = "Walking";
        LoadData task = new LoadData();
        task.execute();
        walkingtv.setText(Integer.toString(++count_walking));
    }

    public void onRunningClick(View view) {
        Log.d(TAG, "  onRunningClick()");
        Log.d(TAG, "valueHolder.size()" + valueHolder.size());
        Activity_type = "Running";
        LoadData task = new LoadData();

        runningtv.setText(Integer.toString(++count_running));
        task.execute();
    }

    public void onEatingClick(View view) {
        Log.d(TAG, "  onEatingClick()");
        Log.d(TAG, "valueHolder.size()" + valueHolder.size());
        Activity_type = "Eating";
        LoadData task = new LoadData();

        eatingtv.setText(Integer.toString(++count_eating));
        task.execute();
    }

    public void onTrainingClicked(View v) {
       /* ArrayList<DataValues> activityDataArrayList = generateTrainingSetFile();
        if (activityDataArrayList.size() > 0) {*/
        AndroidLibSVM androidLibSVM = new AndroidLibSVM();
        accuracy_per = androidLibSVM.train();
        accuracytv.setText(Integer.toString(++accuracy_per));
        Log.d(TAG, "accuracy " + accuracy_per);
        /*} else {
            Log.w(this.getClass().getSimpleName(), "Insufficient Data");
        }*/
    }

    public void onClassifyClick(View v) {
        Log.d(TAG, "  onClassifyClick()");
        Activity_type = "classify";
        valueHolderClassify = new ArrayList<DataValues>();
        LoadData task = new LoadData();
        task.execute();
        classifytv.setText(activity);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        count_eating = 0;
        count_walking = 0;
        count_running = 0;
        removeAll();
    }

    public void removeAll()
    {
        // db.delete(String tableName, String whereClause, String[] whereArgs);
        // If whereClause is null, it will delete all rows.
        MyDB myDB = new MyDB(this, "Assign3_Group22_DB", null, 1);
        SQLiteDatabase db = myDB.getWritableDatabase(); // helper is object extends SQLiteOpenHelper
        db.delete(GlobalConstants.TableName, null, null);
    }
}
