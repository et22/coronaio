package com.example.myapplication;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.farbod.labelledspinner.LabelledSpinner;


/* RegisterActivity manages the registration and edit actions for corona.io.
 * It takes user input - name, password, email, and cell type preference
 * to construct a profile.
 * The output of edit profile and registration are saved via SharedPreferences.
 */

public class RegisterActivity extends AppCompatActivity {

        private EditText mNameView, mEmailView, mPasswordView;
        private Spinner mCellType;
        private Button mRegisterButton;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register);

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
                    finish();
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