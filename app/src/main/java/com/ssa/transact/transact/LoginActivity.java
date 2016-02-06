package com.ssa.transact.transact;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity implements TransactLoginListener {

    private static final String TAG = "Transact.LoginActivity";

    private static String username = null;
    private static String password = null;

    public static void setCredentials (String username, String password) {
        LoginActivity.username = username;
        LoginActivity.password = password;
    }

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "The onCreate() event");
        setContentView(R.layout.activity_login);

        getApplicationContext().bindService(new Intent(this, TransactService.class),
                Singleton.getInstance().getServiceConnection(),
                BIND_AUTO_CREATE);
    }

    /** Called when the activity is about to become visible. */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "The onStart() event");
    }

    /** Called when the activity has become visible. */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "The onResume() event");
    }

    /** Called when another activity is taking focus. */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "The onPause() event");
    }

    /** Called when the activity is no longer visible. */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "The onStop() event");
    }

    /** Called just before the activity is destroyed. */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "The onDestroy() event");

        getApplicationContext().unbindService(Singleton.getInstance().getServiceConnection());
    }

    public void onLoginClicked(View v) {
        final Button loginButton = (Button) v;
        final TextView username = (TextView) findViewById(R.id.username);
        final TextView password = (TextView) findViewById(R.id.password);
        final TextView response = (TextView) findViewById(R.id.loginErrorMsg);
        loginButton.setClickable(false);
        response.setText("Connecting ...");
        Singleton.getInstance().getBinder().doLogin(this,
                username.getText().toString(),
                password.getText().toString());
    }

    @Override
    public void updateLogin(boolean response) {
        Log.d(TAG, "updateLogin with response: " + response);
        final Button loginButton = (Button) findViewById(R.id.login);
        loginButton.setClickable(true);
        final TextView loginResponse = (TextView) findViewById(R.id.loginErrorMsg);
        if (response == false) {
            loginResponse.setText("Invalid username or password");
        } else {
            loginResponse.setText("");
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
    }
}
