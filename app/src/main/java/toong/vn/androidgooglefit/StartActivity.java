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

        findViewById(R.id.button_sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(GoogleSignInActivity.class);
            }
        });

        findViewById(R.id.button_fit_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(FitHistoryActivity.class);
            }
        });

        findViewById(R.id.button_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(RecordingActivity.class);
            }
        });
    }

    private <Z> void startActivity(Class<Z> classes) {
        startActivity(new Intent(this, classes));
    }
}
