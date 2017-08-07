package com.dzdy.cryptopia;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

class Adapter extends RecyclerView.Adapter<Adapter.PairViewHolder> {

    private List<Pair> pairs;

    Adapter(List<Pair> pairs) {
        this.pairs = pairs;
    }

    @Override
    public PairViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        return new PairViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PairViewHolder holder, int position) {
        holder.pairName.setText(pairs.get(position).getName());
        holder.pairAskPrice.setText(pairs.get(position).getAskPrice().toString());
    }

    @Override
    public int getItemCount() {
        return pairs.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    static class PairViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView pairName;
        TextView pairAskPrice;

        PairViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            pairName = itemView.findViewById(R.id.pair_name);
            pairAskPrice = itemView.findViewById(R.id.ask_price);
        }
    }

    void updatePairPrice(int position, Pair newPair) {
        pairs.set(position, newPair);
    }

}