package com.example.mylogin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String API_BASE_URL = "https://android-messaging.branch.co/api/";
    private static final String API_LOGIN_ENDPOINT = "login";
    private static final String SHARED_PREF_NAME = "MyLoginApp";
    private static final String AUTH_TOKEN_KEY = "authToken";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assuming you have a MaterialButton with id loginButton in your XML layout
        MaterialButton loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the entered email and password
                EditText emailEditText = findViewById(R.id.emailEditText);
                EditText passwordEditText = findViewById(R.id.passwordEditText);
                String enteredEmail = emailEditText.getText().toString().trim();
                String enteredPassword = passwordEditText.getText().toString().trim();

                // Validate email format
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(enteredEmail).matches()) {
                    Toast.makeText(MainActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate password
                String reversedEmail = new StringBuilder(enteredEmail).reverse().toString();
                if (!enteredPassword.equals(reversedEmail)) {
                    Toast.makeText(MainActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // If both email and password are valid, proceed with login
                performLogin(enteredEmail, reversedEmail);
            }
        });
    }

    private void performLogin(String username, String password) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("username", username);
            requestBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest loginRequest = new JsonObjectRequest(
                Request.Method.POST,
                API_BASE_URL + API_LOGIN_ENDPOINT,
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Retrieve auth token from the response
                            String authToken = response.getString("auth_token");

                            // Save auth token to SharedPreferences
                            saveAuthToken(authToken);

                            // Print the token on log (for testing)
                            Log.d("AuthToken", authToken);

                            // Move to the MessageThreadsActivity
                            Intent intent = new Intent(MainActivity.this, MessageThreadsActivity.class);
                            intent.putExtra("email", username);
                            startActivity(intent);
                            finish(); // Finish MainActivity to remove it from the back stack
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(loginRequest);
    }

    private void saveAuthToken(String authToken) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AUTH_TOKEN_KEY, authToken);
        editor.apply();
    }
}
