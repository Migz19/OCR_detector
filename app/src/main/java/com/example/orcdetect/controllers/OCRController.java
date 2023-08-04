package com.example.orcdetect.controllers;


import android.graphics.Bitmap;
import android.util.Log;
import android.util.Patterns;
import android.util.SparseArray;

import com.example.orcdetect.model.CardModel;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.Arrays;

public class OCRController {
    private TextRecognizer recognizer;
    private String errorMsg;
    private CardModel card;


    String cardText=" ";

    public OCRController(TextRecognizer recognizer) {
        this.recognizer = recognizer;
    }

    //Function that is responsible on executing text from Image
    private String getTextFromImage(Bitmap bitmap) {
        StringBuilder stringBuilder = new StringBuilder();

        if (!recognizer.isOperational()) {
            errorMsg = "Text Recognition Failed";
        } else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> blockSparseArray = recognizer.detect(frame);
            for (int index = 0; index < blockSparseArray.size(); index++) {
                TextBlock textBlock = blockSparseArray.valueAt(index);
                stringBuilder.append(textBlock.getValue());
                stringBuilder.append("\n");
            }

        }
        Log.d("123456789",stringBuilder.toString());
        return stringBuilder.toString();
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

    public CardModel executeCardReadingInfo(Bitmap bitmap) {
    cardText = getTextFromImage(bitmap);
        extractCardInfo(cardText);
        return card;
    }
    public String getErrorMsg() {
        return errorMsg;
    }

    public String getCardText() {
        return cardText;
    }

    public CardModel getCard() {
        return card;
    }

}
