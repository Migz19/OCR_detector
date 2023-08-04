package com.example.orcdetect.controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orcdetect.R;
import com.example.orcdetect.model.CardModel;

import java.util.ArrayList;

public class CardsAdapter extends ListAdapter<CardModel, CardsAdapter.CardsViewHolder> {

    private OnItemClick onItemClick;

    public CardsAdapter(OnItemClick onitemclick) {
        super(new CardModelDiffCallback());
        this.onItemClick = onitemclick;
    }

    @NonNull
    @Override
    public CardsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview, parent, false);
        return new CardsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardsViewHolder holder, int position) {
        CardModel card = getItem(position);
        holder.emailTv.setText(card.getEmail());
        holder.usernameTv.setText(card.getName());
        holder.numberTv.setText(card.getNumber());
        holder.viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemClick != null) {
                    CardModel card = getItem(position);
                    onItemClick.view(card);
                }
            }
        });
        holder.viewBtn.setOnClickListener(view -> onItemClick.view(card));
        holder.removeBtn.setOnClickListener(view -> {
            onItemClick.removeCard(card);
            removeItem(position);
            notifyItemRemoved(position);
        });
    }

    public void setCardsList(ArrayList<CardModel> cardsList) {
        submitList(cardsList);
    }

    public CardModel getCardAt(int position) {
        return getItem(position);
    }

    @Override
    public int getItemCount() {
        return getCurrentList().size();
    }

    public static class CardsViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTv;
        TextView emailTv;
        TextView numberTv;
        TextView infoTv;
        Button removeBtn;
        Button viewBtn;

        public CardsViewHolder(@NonNull View itemView) {
            super(itemView);
            numberTv = itemView.findViewById(R.id.phoneText);
            usernameTv = itemView.findViewById(R.id.nameText);
            emailTv = itemView.findViewById(R.id.emailText);
            infoTv = itemView.findViewById(R.id.infoTv);
            removeBtn = itemView.findViewById(R.id.removeBtn);
            viewBtn = itemView.findViewById(R.id.viewBtn);
        }
    }



    private static class CardModelDiffCallback extends DiffUtil.ItemCallback<CardModel> {
        @Override
        public boolean areItemsTheSame(@NonNull CardModel oldItem, @NonNull CardModel newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull CardModel oldItem, @NonNull CardModel newItem) {
            return oldItem.equals(newItem);
        }
    }
    public void removeItem(int position) {
        if (position >= 0 && position < getItemCount()) {
            ArrayList<CardModel> newList = new ArrayList<>(getCurrentList());
            newList.remove(position);
            submitList(newList);
        }
    }
}
