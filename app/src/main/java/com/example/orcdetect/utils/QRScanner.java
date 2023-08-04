package com.example.orcdetect.utils;


import static com.example.orcdetect.utils.Constants.INTENT_EMAIL;
import static com.example.orcdetect.utils.Constants.INTENT_NAME;
import static com.example.orcdetect.utils.Constants.INTENT_PHONE_NUMBER;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.orcdetect.data.CardDB;
import com.example.orcdetect.data.CardDao;
import com.example.orcdetect.data.CardsRepo;
import com.example.orcdetect.model.CardModel;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Arrays;

public class QRScanner extends AppCompatActivity {
    private String errorMsg;
    private CardModel card;
    String cardText=" ";
    private CardDao cardDao;
    private CardsRepo cardRepo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cardDao= CardDB.getDatabase(this).cardsDao();
        cardRepo= new CardsRepo(cardDao);
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setPrompt("Scan QR Code or OCR Code");
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);

        intentIntegrator.setCameraId(0);
        intentIntegrator.setCaptureActivity(QRPortrait.class);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                // if the intentResult is not null we'll set
                // the content and format of scan message
                extractCardInfo(intentResult.getContents());
                saveCard();
                saveToContacts(card);
                Toast.makeText(this, intentResult.getContents(), Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    private void extractCardInfo(String cardInfo) {
        String[] lines = cardInfo.split("\n");
        char separator = '#';
        int firstcount =0,secondcount=0;
        StringBuilder emailBuilder = new StringBuilder();
        StringBuilder numBuilder = new StringBuilder();

        String email="";
        String number="";
        String username = "";
        String addtionalInfo="";
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
        number=numBuilder.toString();
        email=emailBuilder.toString();
        card = new CardModel(username, number, email);
        card.setAdditionalInfo(addtionalInfo);

    }
    public void saveCard(){
        cardRepo.insert(card);
    }
    public void saveToContacts(CardModel card){
        Intent intent = new Intent(QRScanner.this, ContactsActivity.class);
        intent.putExtra(INTENT_PHONE_NUMBER, card.getNumber());
        intent.putExtra(INTENT_NAME, card.getName());
        intent.putExtra(INTENT_EMAIL, card.getEmail());
        intent.putExtra("INFO", card.getAdditionalInfo());
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}



