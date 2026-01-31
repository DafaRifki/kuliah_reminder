package com.example.kuliahreminder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kuliahreminder.R;
import com.example.kuliahreminder.model.Schedule;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {
    private Context context;
    private List<Schedule> scheduleList;
    private OnScheduleListener listener;

    public interface OnScheduleListener {
        void onScheduleClick(int position);
        void onScheduleDelete(int position);
    }

    public ScheduleAdapter(Context context, List<Schedule> scheduleList, OnScheduleListener listener) {
        this.context = context;
        this.scheduleList = scheduleList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        Schedule schedule = scheduleList.get(position);

        holder.tvNamaMatkul.setText(schedule.getNamaMatkul());
        holder.tvWaktu.setText(schedule.getFormattedTime());
        holder.tvHari.setText(schedule.getHari());
        holder.tvRuangan.setText(schedule.getRuangan() != null && !schedule.getRuangan().isEmpty()
                ? schedule.getRuangan() : "-");
        holder.tvJenis.setText(schedule.getJenis());

        // Set color based on type
        int color = schedule.getColorByType();
        holder.viewIndicator.setBackgroundColor(color);
        holder.tvJenis.setTextColor(color);

        // Click listeners
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onScheduleClick(holder.getAdapterPosition());
                }
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onScheduleDelete(holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    public void updateData(List<Schedule> newList) {
        this.scheduleList = newList;
        notifyDataSetChanged();
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        View viewIndicator;
        TextView tvNamaMatkul, tvWaktu, tvHari, tvRuangan, tvJenis;
        ImageButton btnDelete;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            viewIndicator = itemView.findViewById(R.id.view_indicator);
            tvNamaMatkul = itemView.findViewById(R.id.tv_nama_matkul);
            tvWaktu = itemView.findViewById(R.id.tv_waktu);
            tvHari = itemView.findViewById(R.id.tv_hari);
            tvRuangan = itemView.findViewById(R.id.tv_ruangan);
            tvJenis = itemView.findViewById(R.id.tv_jenis);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
