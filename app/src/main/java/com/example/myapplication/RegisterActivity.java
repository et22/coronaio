package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.database.FirebaseHelper;
import com.farbod.labelledspinner.LabelledSpinner;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/* RegisterActivity manages the registration and edit actions for corona.io.
 * It takes user input - name, password, email, and cell type preference
 * to construct a profile.
 * The output of edit profile and registration are saved via SharedPreferences.
 */

public class RegisterActivity extends AppCompatActivity {


        private EditText mNameView, mEmailView, mPasswordView;
        private Spinner mCellType;
        private Button mRegisterButton;
        private FirebaseHelper firebaseHelper;
        private FirebaseAuth mAuth;
        private FirebaseUser mFirebaseUser;
        private DatabaseReference mDatabase;
        private String mUserId;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register);
            //Initialize Firebase Auth
            //mAuth = FirebaseAuth.getInstance();
            firebaseHelper = new FirebaseHelper();

            //Makes the toolbar visible
            //Toolbar toolbar = findViewById(R.id.app_bar);
            //setSupportActionBar(toolbar);
            //Makes the "up" button visible and changes manifest to make it function
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                this.setTitle("Registration");
            }

            //Wire up the elements on the screen
            mNameView = findViewById(R.id.edit_name);
            mEmailView = findViewById(R.id.edit_email);
            mPasswordView = findViewById(R.id.edit_password);
            mCellType = findViewById(R.id.cell_type);
            mRegisterButton = findViewById(R.id.button_register);

            //adding content to cell type spinner
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.cell_types, R.layout.spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mCellType.setAdapter(adapter);

            //set register button onClick listener;
            onRegisterButtonClick();
        }

    private void onRegisterButtonClick() {
            mRegisterButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    String password = mPasswordView.getText().toString();
                    String email = mEmailView.getText().toString();
                    final String userName = mNameView.getText().toString().trim();

                    password = password.trim();
                    email = email.trim();

                    // If email and password are empty we prompt the user to fill in the fields
                    if (password.isEmpty() || email.isEmpty()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        builder.setMessage(R.string.signup_error_message)
                                .setTitle(R.string.signup_error_title)
                                .setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    // if username is empty we prompt user to fill out the field
                    if(userName.isEmpty()){
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        builder.setMessage(R.string.userName_error_message)
                                .setTitle(R.string.signup_error_title)
                                .setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        String result = firebaseHelper.addNewUser(email, password, userName, mCellType.getSelectedItemPosition(), RegisterActivity.this);
                        // If user is successfully registered, we lead them to loginActivity
                        if(result.equals("success")){
                            Toast.makeText(RegisterActivity.this, "Authentication worked.",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        }else{
                            Toast.makeText(RegisterActivity.this, "Authentication failed: "
                                            + result,
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                }
            });
    }

    //Adds functionality to the options in the menu
        @Override
        public boolean onOptionsItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.home:
                    finish();
                    break;
            }
            return super.onOptionsItemSelected(item);
        }
}