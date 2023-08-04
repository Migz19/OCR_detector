package com.example.orcdetect.data;

import androidx.annotation.NonNull;

import com.example.orcdetect.model.CardModel;

import java.util.ArrayList;

public class CardsRepo implements CardDao {
private CardDao cardDao;
public CardsRepo( CardDao cardDao){
    this.cardDao=cardDao;
}
    @Override
    public void insert(CardModel card) {
        cardDao.insert(card);
    }

    @NonNull
    @Override
    public ArrayList<CardModel> getCards() {
       return (ArrayList<CardModel>) cardDao.getCards();
    }

    @Override
    public void deleteItem(CardModel card) {
        cardDao.deleteItem(card);
    }

    @Override
    public CardModel getItemById(int itemId) {
        return cardDao.getItemById(itemId);
    }
}
