package toong.vn.androidgooglefit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Device;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import toong.vn.androidgooglefit.util.Constant;
import toong.vn.androidgooglefit.util.DateTimeUtils;

/**
 * Line1 <br/>
 * Line2
 */
public class ReadFitHistoryActivity extends AppCompatActivity {
    int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fit_history);

        findViewById(R.id.button_fit_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions();
            }
        });
    }

    private void accessGoogleFit() {
        getHistory();
    }

    private void requestPermissions() {
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .build();
        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this),
                fitnessOptions)) {
            GoogleSignIn.requestPermissions(this, // your activity
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE, GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions);
        } else {
            accessGoogleFit();
        }
    }

    private void getHistory() {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.DATE, -7);
        long startTime = cal.getTimeInMillis();

        Log.i(Constant.TAG, "Range Start: " + DateTimeUtils.format(startTime));
        Log.i(Constant.TAG, "Range End: " + DateTimeUtils.format(endTime));

        DataSource ESTIMATED_STEP_DELTAS =
                new DataSource.Builder().setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                        .setType(DataSource.TYPE_DERIVED)
                        .setStreamName("estimated_steps")
                        .setAppPackageName("com.google.android.gms")
                        .build();

        DataReadRequest readRequest = new DataReadRequest.Builder().read(ESTIMATED_STEP_DELTAS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readData(readRequest)
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse response) {
                        Log.d(Constant.TAG, "onSuccess() " + response.getDataSets().size());
                        for (DataSet dataSet : response.getDataSets()) {
                            dumpDataSet(dataSet);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(Constant.TAG, "onFailure()", e);
                    }
                });
    }

    private void dumpDataSet(DataSet dataSet) {
        if (dataSet.getDataPoints().isEmpty()) {
            return;
        }
        int stepSum = 0;
        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.i(Constant.TAG,
                    "Origin type: " + dp.getOriginalDataSource().getDataType().getName());
            Log.i(Constant.TAG, "Type: " + dp.getOriginalDataSource().getType());
            Log.i(Constant.TAG,
                    "Origin type uid: " + dp.getOriginalDataSource().getDevice().getUid());
            Log.i(Constant.TAG,
                    "Local uid: " + Device.getLocalDevice(getApplicationContext()).getUid());
            Log.i(Constant.TAG, "Type: " + dp.getDataType().getName());
            Log.i(Constant.TAG, "Start: "
                    + DateTimeUtils.format(dp.getStartTime(TimeUnit.MILLISECONDS))
                    + "  End: "
                    + DateTimeUtils.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            for (Field field : dp.getDataType().getFields()) {
                Log.i(Constant.TAG, "Field:" + field.getName() + " Value:" + dp.getValue(field));
                stepSum += dp.getValue(field).asInt();
            }
        }
        Log.i(Constant.TAG, "STEP SUM = " + stepSum);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
                accessGoogleFit();
            }
        }
    }
}
