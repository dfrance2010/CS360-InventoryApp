package com.zybooks.inventoryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private String username;
    private String password;
    private EditText txtUsername;
    private EditText txtPassword;
    private TextView errorTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUsername = findViewById(R.id.username);
        txtPassword = findViewById(R.id.password);
        errorTxt = findViewById(R.id.login_error);

        Button loginBtn = findViewById(R.id.login);
        loginBtn.setOnClickListener(this::login);

        Button createUserBtn = findViewById(R.id.create_user);
        createUserBtn.setOnClickListener(this::createUser);

    }

    // Check if username exists, then if password matches username. If both are true, start main
    // activity. If at least one is not correct, give appropriate error message.
    private void login(View view) {
        UserDatabase db = UserDatabase.getInstance(this);

        password = String.valueOf(txtPassword.getText());
        username = String.valueOf(txtUsername.getText());

        UserCredentials user = new UserCredentials(username, password);

        if (db.checkUserName(username)) {
            if (db.checkPassword(user)) {
                AuthenticatedUser newUser = new AuthenticatedUser(username);
                AuthenticatedUserManager.getInstance().setUser(newUser);
                startMain();
            } else {
                errorTxt.setText("That password is incorrect");
            }
        } else {
            errorTxt.setText("That username does not exist");
        }
    }

    // Check if username already exists. If it does, return with error message. If not
    // create new user in UserDatabase.
    private void createUser(View view) {
        UserDatabase db = UserDatabase.getInstance(this);
        password = String.valueOf(txtPassword.getText());
        username = String.valueOf(txtUsername.getText());
        if (db.checkUserName(username)) {
            errorTxt.setText("That username already exists");
            return;
        }

        UserCredentials newUser = new UserCredentials(username, password);
        long newId = db.addUser(newUser);

        if (newId > 0) {
            AuthenticatedUser authUser = new AuthenticatedUser(username);
            AuthenticatedUserManager.getInstance().setUser(authUser);
            startMain();
        } else {
            Toast.makeText(this, "Failed to add user", Toast.LENGTH_SHORT).show();
        }
    }

    // Method for starting main activity on login or create user.
    private void startMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}