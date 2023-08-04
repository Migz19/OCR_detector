package com.example.orcdetect;

import static com.example.orcdetect.utils.Constants.INTENT_EMAIL;
import static com.example.orcdetect.utils.Constants.INTENT_NAME;
import static com.example.orcdetect.utils.Constants.INTENT_PHONE_NUMBER;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orcdetect.controllers.CardsAdapter;
import com.example.orcdetect.controllers.OnItemClick;
import com.example.orcdetect.data.CardDB;
import com.example.orcdetect.data.CardDao;
import com.example.orcdetect.data.CardsRepo;
import com.example.orcdetect.databinding.ActivityMainBinding;
import com.example.orcdetect.model.CardModel;
import com.example.orcdetect.utils.CardDetails;
import com.example.orcdetect.utils.ContactsActivity;
import com.example.orcdetect.utils.OCRScanner;
import com.example.orcdetect.utils.QRScanner;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements OnItemClick {

    private ActivityMainBinding binding;
    private static int REQUEST_CAMERA_CODE = 100;
    private CardsRepo cardsRepo;
    private CardDao cardDao;
    private RecyclerView recyclerView;
    private CardsAdapter adapter;
    private NfcAdapter nfcAdapter;
    private CardModel nfcCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cardDao = CardDB.getDatabase(this).cardsDao();
        cardsRepo = new CardsRepo(cardDao);

        // Initialize NFC adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter != null) {
            try {
                if (nfcAdapter.isEnabled()) {
                    Toast.makeText(this, "NFC is available", Toast.LENGTH_SHORT).show();
                    binding.nfcLogo.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(this, "NFC is not enabled", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error accessing NFC", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "NFC is not supported on this device", Toast.LENGTH_SHORT).show();
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, REQUEST_CAMERA_CODE);
        }

        binding.qrBtn.setOnClickListener(view -> {

            startActivity(new Intent(this, QRScanner.class));
        });
        binding.ocrBtn.setOnClickListener(view -> {
            startActivity(new Intent(this, OCRScanner.class));
        });
        // Initialize the RecyclerView
        recyclerView = binding.cardRV;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create an instance of your custom adapter and pass any required parameters
        adapter = new CardsAdapter(this);
        adapter.setCardsList(cardsRepo.getCards());
        // Set the adapter for the RecyclerView
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            enableNfcForegroundDispatch();
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    private void enableNfcForegroundDispatch() {

        Intent intent = new Intent(this, getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        nfcAdapter.enableForegroundDispatch(
                this, PendingIntent.getActivity(this, 0, intent, 0), null,
                null);


    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            disableNfcForegroundDispatch();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void disableNfcForegroundDispatch() {
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        try {
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                if (tag != null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    Ndef ndef = Ndef.get(tag);
                    if (ndef == null) {
                        Toast.makeText(this, "NDEF not supported by this Tag", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    NdefMessage ndefMessage = ndef.getCachedNdefMessage();
                    NdefRecord[] records = ndefMessage.getRecords();
                    for (NdefRecord record : records) {
                        String payload = new String(record.getPayload(), StandardCharsets.UTF_8);
                        stringBuilder.append(payload);
                        stringBuilder.append("\n");
                        // Extract the necessary information from the payload
                        // Modify this code to extract name, email, and phone number
                        Log.d("NFC Payload", payload);
                    }
                    nfcCard = extractCardInfo(stringBuilder.toString());
                    saveToContacts(nfcCard);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private CardModel extractCardInfo(String cardInfo) {
        CardModel card;
        String[] lines = cardInfo.split(Objects.requireNonNull(System.getProperty("line.separator")));
        char separator = '#';
        StringBuilder emailBuilder = new StringBuilder();
        StringBuilder numBuilder = new StringBuilder();
        int firstcount =0,secondcount=0;
        String email = "";
        String number = "";
        String username = "";
        String addtionalInfo = "";
        for (String line : lines) {
            Log.d("121544151", Arrays.toString(lines));
            if (line.isEmpty()) {
                Log.d("12343423432434232","empty");
                continue;
            }

            if (Patterns.EMAIL_ADDRESS.matcher(line).matches()) {
                if (firstcount>0)
                    emailBuilder.append(separator).append(line);
                else
                    emailBuilder.append(line);
                firstcount++;
                Log.d("123434232",line);
                continue;
            }

            if (line.matches("\\d+")) {
                String tempNumber = line.replaceAll("\\s+", "");
                if (secondcount>0)
                    numBuilder.append(separator).append(tempNumber);
                else
                    numBuilder.append(tempNumber);
                secondcount++;
                continue;
            }

            if (username.isEmpty()){
                username = line;
            }else
            {
                addtionalInfo=line;
            }
        }
        number = numBuilder.toString();
        email = emailBuilder.toString();
        card = new CardModel(username, number, email);
        card.setAdditionalInfo(addtionalInfo);
        return card;
    }

    public void saveToContacts(CardModel card) {
        Intent intent = new Intent(this, ContactsActivity.class);
        intent.putExtra(INTENT_PHONE_NUMBER, card.getNumber());
        intent.putExtra(INTENT_NAME, card.getName());
        intent.putExtra(INTENT_EMAIL, card.getEmail());
        startActivity(intent);
    }


    @Override
    public void removeCard(CardModel card) {
        cardsRepo.deleteItem(card);

    }

    @Override
    public void view(CardModel card) {
        Intent intent = new Intent(MainActivity.this, CardDetails.class);
        //Bundle bundle = new Bundle();
        intent.putExtra("ID",card.getId());

        startActivity(intent);
    }
}
