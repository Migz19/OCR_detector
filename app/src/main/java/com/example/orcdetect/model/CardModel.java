package com.example.orcdetect.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "Cards")
public class CardModel {
   private String name;
    private String number;
   private String email;
   private String additionalInfo;

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    @PrimaryKey(autoGenerate = true)
    private int id=0;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public CardModel(String name, String number, String email) {
        this.name = name;
        this.number = number;
        this.email = email;
    }

    public String getName() {
        return name;
    }



    public String getNumber() {
        return number;
    }


    public String getEmail() {
        return email;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        CardModel otherCard = (CardModel) obj;

        // Compare the relevant fields for equality check
        return id==otherCard.id &&
                name.equals(otherCard.name) &&
                email.equals(otherCard.email) &&
                number.equals(otherCard.number);
    }

    @Override
    public int hashCode() {
        // Generate the hash code based on the relevant fields
        return Objects.hash(id, name, email, number);
    }
}
