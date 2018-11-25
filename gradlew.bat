package com.hva.m2mobi.m2hva_reservationsystem.adapters;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hva.m2mobi.m2hva_reservationsystem.R;

import java.util.ArrayList;
import java.util.List;


public class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayViewHolder> {
    public List<Day> mListDays;
    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public DayAdapter(List<Day> mListDays) {
        this.mListDays = mListDays;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_cell_day, parent, false);
        return new DayViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        Day day = mListDays.get(position);

        holder.bookings.setText(Integer.toString(day.getNumberOfBookings()));
        holder.number.setText(Integer.toString(day.getNumberInMonth()));
        holder.day.setText(day.getDayInWeek());

        holder.itemView.

    }

    @Override
    public int getItemCount() {
        return mListDays.size();
    }

    public static class DayViewHolder extends RecyclerView.ViewHolder {
        private TextView day;
        private TextView number;
        private TextView bookings;
        private View itemView;

        public DayViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            this.itemView = itemView;
            day = itemView.findViewById(R.id.dayTextView);
            number = itemView.findViewById(R.id.numberTextView);
            bookings = itemView.findViewById(R.id.bookingsTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
      