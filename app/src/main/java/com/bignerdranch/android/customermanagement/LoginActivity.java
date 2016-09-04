package com.bignerdranch.android.customermanagement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Activity to log in user.
 * Username: jdoe
 * Password: welcome1
 * Case-sensitive
 */

public class LoginActivity extends AppCompatActivity {

    private EditText mUsername;
    private EditText mPassword;
    private Button mLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsername = (EditText) findViewById(R.id.text_username);
        mPassword = (EditText) findViewById(R.id.text_password);

        mLogin = (Button) findViewById(R.id.button_login);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();

                logInCheck(username, password);
            }
        });
    }

    private void logInCheck(String username, String password){
        if(username.trim().equals(getString(R.string.username)) &&
                password.equals(getString(R.string.password))){
            Toast.makeText(LoginActivity.this, R.string.login_success, Toast.LENGTH_SHORT)
                    .show();
            Intent i = new Intent(LoginActivity.this, MenuActivity.class);
            startActivity(i);
        }
        else{
            Toast.makeText(LoginActivity.this, R.string.login_fail, Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
