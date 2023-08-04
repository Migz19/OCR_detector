package com.example.orcdetect.utils;

import static com.example.orcdetect.utils.Constants.INTENT_EMAIL;
import static com.example.orcdetect.utils.Constants.INTENT_NAME;
import static com.example.orcdetect.utils.Constants.INTENT_PHONE_NUMBER;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.orcdetect.R;

public class ContactsActivity extends AppCompatActivity {
    private String name;
    private String email;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        EditText phoneText = findViewById(R.id.phoneText);
        EditText nameText = findViewById(R.id.nameText);
        EditText emailText = findViewById(R.id.emailText);
        Button contactsButton = findViewById(R.id.contactBtn);
       Intent intent= getIntent();
        try {
            phone = intent.getStringExtra(INTENT_PHONE_NUMBER);
            email = intent.getStringExtra(INTENT_EMAIL);
            name = intent.getStringExtra(INTENT_NAME);

        } catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(ContactsActivity.this, "Error", Toast.LENGTH_SHORT).show();
        }

        phoneText.setText(phone);
        nameText.setText(name);
        emailText.setText(email);

        contactsButton.setOnClickListener(v -> startContactsActivityIntent());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * Starts the contacts intent and requests permission to write to contacts if permission doesn't exist
     */
    public void startContactsActivityIntent() {
        String[] permissions = {"android.permission.WRITE_CONTACTS"};
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, 100);
        } else {
            if (intent.resolveActivity(getPackageManager()) != null) {
                intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
                intent.putExtra(ContactsContract.Intents.Insert.EMAIL, email);
                intent.putExtra(ContactsContract.Intents.Insert.PHONE, phone);
                startActivity(intent);
            }
        }
        finish();
    }

}
