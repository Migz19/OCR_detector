package com.example.orcdetect.utils;


import static com.example.orcdetect.utils.Constants.INTENT_EMAIL;
import static com.example.orcdetect.utils.Constants.INTENT_NAME;
import static com.example.orcdetect.utils.Constants.INTENT_PHONE_NUMBER;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.orcdetect.controllers.OCRController;
import com.example.orcdetect.data.CardDB;
import com.example.orcdetect.data.CardDao;
import com.example.orcdetect.data.CardsRepo;
import com.example.orcdetect.model.CardModel;
import com.google.android.gms.vision.text.TextRecognizer;


public class OCRScanner extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private TextRecognizer recognizer;
    private OCRController ocrController;
    private CardsRepo cardRepo;
    CardDao cardDao;
    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dispatchTakePictureIntent();
        recognizer = new TextRecognizer.Builder(this).build();
        cardDao = CardDB.getDatabase(this).cardsDao();
        cardRepo = new CardsRepo(cardDao);
    }

    public void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ocrController = new OCRController(recognizer);
            CardModel card = ocrController.executeCardReadingInfo(imageBitmap);
            Toast.makeText(this, ocrController.getCardText(), Toast.LENGTH_LONG).show();
            saveCard();
            saveToContacts(card);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void saveCard() {
        cardRepo.insert(ocrController.getCard());
    }

    public void saveToContacts(CardModel card) {
        Intent intent = new Intent(OCRScanner.this, ContactsActivity.class);
        intent.putExtra(INTENT_PHONE_NUMBER, card.getNumber());
        intent.putExtra(INTENT_NAME, card.getName());
        intent.putExtra(INTENT_EMAIL, card.getEmail());
        startActivity(intent);
    }


}

