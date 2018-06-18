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
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Subscription;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import java.util.List;
import toong.vn.androidgooglefit.util.Constant;

public class RecordingActivity extends AppCompatActivity {
    int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        findViewById(R.id.button_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions();
            }
        });

        findViewById(R.id.button_list_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listRecord();
            }
        });
    }

    private void accessGoogleFit() {
        record();
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

    private void record() {
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .subscribe(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(Constant.TAG, "Successfully subscribed!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(Constant.TAG, "There was a problem subscribing.");
                    }
                });
    }

    private void listRecord() {
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .listSubscriptions(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(new OnSuccessListener<List<Subscription>>() {
                    @Override
                    public void onSuccess(List<Subscription> subscriptions) {
                        Log.i(Constant.TAG, "List subscription size: " + subscriptions.size());
                        for (Subscription sc : subscriptions) {
                            DataType dt = sc.getDataType();
                            Log.i(Constant.TAG, "Active subscription for data type: " + dt.getName());
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
                accessGoogleFit();
            } else {

            }
        }
    }
}
