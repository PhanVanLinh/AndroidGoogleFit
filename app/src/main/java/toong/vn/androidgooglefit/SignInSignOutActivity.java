package toong.vn.androidgooglefit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import toong.vn.androidgooglefit.util.Constant;

/**
 * Document: https://developers.google.com/identity/sign-in/android/sign-in
 */
public class SignInSignOutActivity extends AppCompatActivity {
    private int RC_SIGN_IN = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign_in);

        findViewById(R.id.button_sign_in_sign_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        findViewById(R.id.button_sign_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    private GoogleSignInOptions getSignInOptions() {
        return new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
                .build();
    }

    private void signIn() {
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this, getSignInOptions());
        Intent signInIntent = signInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this, getSignInOptions());
        signInClient.signOut();
        Log.i(Constant.TAG, "signOut ");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.i(Constant.TAG, "signInResult: email: "+account.getEmail());
            Log.i(Constant.TAG, "signInResult: email: "+account.getEmail());
        } catch (ApiException e) {
            // GooglePlayService Disable => error code 12500 (GoogleSignInStatusCodes.html#SIGN_IN_FAILED)
            // GooglePlayService OutUpdate => error code 12500 (GoogleSignInStatusCodes.html#SIGN_IN_FAILED)
            // No Internet connection => still able to sign in (no error throw)

            // Service Missing: error 8 (CommonStatusCodes#INTERNAL_ERROR)
            // API unavailable => look like when don't need to  handle (consider API not connect)
            // Start resolution => look like when don't need to  handle
            //
            Toast.makeText(SignInSignOutActivity.this,
                    "signInResult:failed code = " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
        }
    }

    // onConnectionFailed
}
