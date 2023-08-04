package com.example.orcdetect.utils;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.orcdetect.data.CardDB;
import com.example.orcdetect.data.CardDao;
import com.example.orcdetect.data.CardsRepo;
import com.example.orcdetect.databinding.ActivityCardDetailsBinding;
import com.example.orcdetect.model.CardModel;

public class CardDetails extends AppCompatActivity {
ActivityCardDetailsBinding binding;
CardDao cardDao;
CardsRepo cardsRepo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCardDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int id=getIntent().getIntExtra("ID",0);
//
//        Bundle bundle = getIntent().getExtras();
//        if (bundle != null) {
//            phone = bundle.getString(INTENT_PHONE_NUMBER);
//            email = bundle.getString(INTENT_EMAIL);
//            name = bundle.getString(INTENT_NAME);
//            info = bundle.getString("10022");
//        }
        cardDao = CardDB.getDatabase(this).cardsDao();
        cardsRepo=new CardsRepo(cardDao);
        CardModel card= cardsRepo.getItemById(id);

        if (card.getEmail() != null) {
            binding.emailText.setText(card.getEmail());
        }

        if (card.getName() != null) {
            binding.nameText.setText(card.getName());
        }

        if (card.getAdditionalInfo() != null) {
            binding.infoTv.setText(card.getAdditionalInfo());
        }

        if (card.getNumber() != null) {
            binding.phoneText.setText(card.getNumber());
        }

        binding.backBtn.setOnClickListener(view -> finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding=null;
    }
}