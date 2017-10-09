package toong.vn.androidgooglefit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private GoogleApiClient mClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        buildFitnessClient();
    }

    private void buildFitnessClient() {
        if (mClient == null) {
            mClient = new GoogleApiClient.Builder(this).addApi(Fitness.SENSORS_API)
                    .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {
                            Log.i(TAG, "Connected!!!");
                            // Now you can make calls to the Fitness APIs.
                            findFitnessDataSources();
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            // If your connection to the sensor gets lost at some point,
                            // you'll be able to determine the reason and react to it here.
                            if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                            } else if (i
                                    == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
                            }
                        }
                    })
                    .enableAutoManage(this, 0, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult result) {
                            Log.i(TAG, "Google Play services connection failed. Cause: "
                                    + result.toString());
                            Toast.makeText(MainActivity.this,
                                    "Exception while connecting to Google Play services: ",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .build();
        }
    }

    private void findFitnessDataSources() {
        DataSourcesRequest dataSourceRequest =
                new DataSourcesRequest.Builder().setDataTypes(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                        .setDataSourceTypes(DataSource.TYPE_RAW)
                        .build();
        ResultCallback<DataSourcesResult> dataSourcesResultCallback =
                new ResultCallback<DataSourcesResult>() {
                    @Override
                    public void onResult(DataSourcesResult dataSourcesResult) {
                        for (DataSource dataSource : dataSourcesResult.getDataSources()) {
                            if (DataType.TYPE_STEP_COUNT_CUMULATIVE.equals(
                                    dataSource.getDataType())) {
                                Log.i(TAG, "registerFitnessDataListener");
                                registerFitnessDataListener(dataSource,
                                        DataType.TYPE_STEP_COUNT_CUMULATIVE);
                            }
                        }
                    }
                };
        Fitness.SensorsApi.findDataSources(mClient, dataSourceRequest)
                .setResultCallback(dataSourcesResultCallback);
    }

    private void registerFitnessDataListener(DataSource dataSource, DataType dataType) {
        SensorRequest request = new SensorRequest.Builder().setDataSource(dataSource)
                .setDataType(dataType)
                .setSamplingRate(5, TimeUnit.SECONDS)
                .build();

        Fitness.SensorsApi.add(mClient, request, new OnDataPointListener() {
            @Override
            public void onDataPoint(DataPoint dataPoint) {
                for (final Field field : dataPoint.getDataType().getFields()) {
                    final Value value = dataPoint.getValue(field);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Field: " + field.getName() + " Value: " + value,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    Log.e("GoogleFit", "SensorApi successfully added");
                }
            }
        });
    }
}
