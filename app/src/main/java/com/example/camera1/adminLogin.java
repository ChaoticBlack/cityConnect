package com.example.camera1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class adminLogin extends AppCompatActivity {
    static EditText Username;
    static EditText Password;
    static Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

    }
    public void control (View view) {
        Intent i1=new Intent(getBaseContext(), admin_control_panel.class);
        startActivity(i1);
    }
}