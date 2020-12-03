package com.example.camera1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class adminLogin extends AppCompatActivity {
    static EditText Username;
    static EditText Password;
    static Button btn;
    static TextView Attempts;
    private String trueLogin = "Shivam";
    private String truePassword = "12345678";
    String userName = "";
    String userPassword = "";
    boolean crosscheck = false;
    private int counter = 5;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent in;
            switch (item.getItemId()) {
                case R.id.navigation_home: {
                    in=new Intent(getBaseContext(),MainActivity.class);
                    startActivity(in);
                    return true;
                }
                case R.id.map_dashboard: {

                    in=new Intent(getBaseContext(),maps.class);
                    startActivity(in);
                    return true;
                }
                case R.id.rate_us:
                    Toast.makeText(getApplicationContext(),"Review",Toast.LENGTH_LONG).show();
                    return true;
            }
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Username = findViewById(R.id.username);
        Password = findViewById(R.id.password);
        Attempts = findViewById(R.id.attemptinfo);

    }

    public void control (View view) {
//        Intent i1=new Intent(getBaseContext(), admin_control_panel.class);
//        startActivity(i1);
        userName = Username.getText().toString();
        userPassword = Password.getText().toString();
        if(userName.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(this, "Please enter name and password!", Toast.LENGTH_LONG).show();
        }
        else{
            crosscheck = validate(userName,userPassword);
            if(!crosscheck)
            {
                counter--;
                Attempts.setText("Attempts remaining: " + counter);
                if(counter==0)
                {
                    btn.setEnabled(false);
                    Toast.makeText(this, "Number of attempts exceeded, contact Developers", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(this, "Incorrect credentials, try again", Toast.LENGTH_LONG).show();

                }

            }
            else{
                Intent i1=new Intent(getBaseContext(), admin_control_panel.class);
                startActivity(i1);
            }
        }
    }
    private boolean validate(String userName, String userPassword)
    {

        if(userName.equals(trueLogin) && userPassword.equals(truePassword))
        {
            return true;
        }

        return false;
    }
}