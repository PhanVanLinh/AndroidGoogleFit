package toong.vn.androidgooglefit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        findViewById(R.id.button_sign_in_sign_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(SignInSignOutActivity.class);
            }
        });

        findViewById(R.id.button_sign_in_sign_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(SignInSignOutActivity.class);
            }
        });

        findViewById(R.id.button_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(RecordingActivity.class);
            }
        });

        findViewById(R.id.button_fit_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ReadFitHistoryActivity.class);
            }
        });

        findViewById(R.id.button_register_sensor_api).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(SensorActivity.class);
            }
        });
    }

    private <Z> void startActivity(Class<Z> classes) {
        startActivity(new Intent(this, classes));
    }
}
