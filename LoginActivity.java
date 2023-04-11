package com.zybooks.inventoryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private String username;
    private String password;

    private TextView usernameList;
    private TextView passwordList;
    private EditText txtUsername;
    private EditText txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUsername = findViewById(R.id.username);
        txtPassword = findViewById(R.id.password);

        usernameList = findViewById(R.id.usernameview);
        passwordList = findViewById(R.id.passwordview);

        Button clearBtn = findViewById(R.id.clear_table);
        clearBtn.setOnClickListener(l -> {
            UserDatabase.getInstance(this).clearTable();
        });



        Button loginBtn = findViewById(R.id.login);
        loginBtn.setOnClickListener(this::login);

        Button createUserBtn = findViewById(R.id.create_user);
        createUserBtn.setOnClickListener(this::createUser);

        Button showUserTable = findViewById(R.id.user_table);
        showUserTable.setOnClickListener(this::showUserTable);
    }

    private void showUserTable(View view) {
        List<UserCredentials> userList = UserDatabase.getInstance(this).getUsers();
        String users = "";
        String passwords = "";

        for (UserCredentials user: userList) {
            users += user.getUsername() + "\n";
            passwords += user.getPassword() + "\n";
        }
        usernameList.setText(users);
        passwordList.setText(passwords);
    }

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
                Toast.makeText(this, "That password is incorrect",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "That username does not exist",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void createUser(View view) {
        UserDatabase db = UserDatabase.getInstance(this);
        password = String.valueOf(txtPassword.getText());
        username = String.valueOf(txtUsername.getText());
        if (db.checkUserName(username)) {
            Toast.makeText(this, "That username already exists", Toast.LENGTH_LONG).show();
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

    private void startMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}