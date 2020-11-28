package com.example.camera1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
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