package com.example.orcdetect.controllers;

import com.example.orcdetect.model.CardModel;

public interface OnItemClick {
    void removeCard(CardModel card);
    void view(CardModel cardModel);
}
