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
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import java.util.List;
import java.util.concurrent.TimeUnit;
import toong.vn.androidgooglefit.util.Constant;

public class SensorActivity extends AppCompatActivity {

    private static final int REQUEST_OAUTH_REQUEST_CODE = 1;
    private OnDataPointListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        findViewById(R.id.button_register_sensor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasOAuthPermission()) {
                    findFitnessDataSources();
                } else {
                    requestOAuthPermission();
                }
            }
        });
    }

    private FitnessOptions getFitnessSignInOptions() {
        return FitnessOptions.builder().addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE).build();
    }

    private boolean hasOAuthPermission() {
        FitnessOptions fitnessOptions = getFitnessSignInOptions();
        return GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this),
                fitnessOptions);
    }

    private void requestOAuthPermission() {
        FitnessOptions fitnessOptions = getFitnessSignInOptions();
        GoogleSignIn.requestPermissions(this, REQUEST_OAUTH_REQUEST_CODE,
                GoogleSignIn.getLastSignedInAccount(this), fitnessOptions);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                findFitnessDataSources();
            } else {

            }
        }
    }

    private void findFitnessDataSources() {
        Fitness.getSensorsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .findDataSources(new DataSourcesRequest.Builder().setDataTypes(
                        DataType.TYPE_STEP_COUNT_CUMULATIVE)
                        .setDataSourceTypes(DataSource.TYPE_RAW)
                        .build())
                .addOnSuccessListener(new OnSuccessListener<List<DataSource>>() {
                    @Override
                    public void onSuccess(List<DataSource> dataSources) {
                        for (DataSource dataSource : dataSources) {
                            Log.i(Constant.TAG, "Data source found: " + dataSource.toString());
                            Log.i(Constant.TAG,
                                    "Data Source type: " + dataSource.getDataType().getName());

                            // Let's register a listener to receive Activity data!
                            if (dataSource.getDataType().equals(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                                    && mListener == null) {
                                Log.i(Constant.TAG, "Data source found!  Registering.");
                                registerFitnessDataListener(dataSource,
                                        DataType.TYPE_STEP_COUNT_CUMULATIVE);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(Constant.TAG, "failed", e);
                    }
                });
    }

    private void registerFitnessDataListener(DataSource dataSource, DataType dataType) {
        mListener = new OnDataPointListener() {
            @Override
            public void onDataPoint(DataPoint dataPoint) {
                for (Field field : dataPoint.getDataType().getFields()) {
                    Value val = dataPoint.getValue(field);
                    Log.i(Constant.TAG, "Detected DataPoint field: " + field.getName());
                    Log.i(Constant.TAG, "Detected DataPoint value: " + val);
                }
            }
        };

        Fitness.getSensorsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .add(new SensorRequest.Builder().setDataSource(
                        dataSource) // Optional but recommended for custom data sets.
                        .setDataType(dataType) // Can't be omitted.
                        .setSamplingRate(1, TimeUnit.SECONDS).build(), mListener)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i(Constant.TAG, "Listener registered!");
                        } else {
                            Log.e(Constant.TAG, "Listener not registered.", task.getException());
                        }
                    }
                });
    }
}
