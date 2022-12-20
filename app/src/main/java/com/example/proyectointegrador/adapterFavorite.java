package com.example.proyectointegrador;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class adapterFavorite extends RecyclerView.Adapter<adapterFavorite.ViewHolder>{
    private Modelo modelo = Modelo.getInstance();
    private Context context;
    private RecyclerView recyclerView;

    public adapterFavorite(Context context, RecyclerView recyclerView){
        this.context = context;
        this.recyclerView = recyclerView;

    }

    @NonNull
    @Override
    public adapterFavorite.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_card, parent, false);
        return new adapterFavorite.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adapterFavorite.ViewHolder holder, int position) {
        Acciones accion = modelo.getFavoritasList().get(position);
        if(position%2==0){
            holder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.favorite_gradient_background_orange));

        } else {
            holder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.favorite_gradient_background_purple));
        }
        holder.txtNomAccion.setText(accion.getNombre());
        holder.txtFavAcronimo.setText(accion.getTicker());
        holder.txtPorAccion.setText(accion.getPorcentajePeriodo());
        holder.txtValorAccion.setText(accion.getValorMercado());
        holder.imgLogoAccion.setImageResource(accion.getLogo());
        setGraphConfi(holder, accion);
    }

    private void setGraphConfi(ViewHolder holder, Acciones accion) {
        int colorWhite = ContextCompat.getColor(context, R.color.white);
        holder.chrGrafico.setDragEnabled(true);
        holder.chrGrafico.setScaleEnabled(true);
        holder.chrGrafico.getLegend().setEnabled(false);
        holder.chrGrafico.getDescription().setEnabled(false);
        holder.chrGrafico.getXAxis().setEnabled(false);
        holder.chrGrafico.getAxisLeft().setEnabled(false);
        holder.chrGrafico.getAxisRight().setEnabled(false);
        List<LocalDate> fechas = accion.getFechas();
        List<Float> precios = accion.getPrecios();

        ArrayList<Entry> yValues = new ArrayList<>();
        for (int i =  precios.size()-61; i < precios.size(); i++) {
            yValues.add(new Entry(i, precios.get(i)));
        }

        LineDataSet set1 = new LineDataSet(yValues, "Data Set 1");
        set1.setFillAlpha(0);
        set1.setColor(colorWhite);
        set1.setDrawCircles(false);
        set1.setLineWidth(1);
        set1.setHighlightEnabled(false);
        set1.setDrawValues(false);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        LineData data = new LineData(dataSets);

        holder.chrGrafico.setData(data);
    }

    @Override
    public int getItemCount() {
        return modelo.getFavoritasList().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtNomAccion;
        TextView txtFavAcronimo;
        TextView txtPorAccion;
        ImageView imgLogoAccion;
        TextView txtValorAccion;
        LineChart chrGrafico;

        public ViewHolder(@NonNull View view) {
            super(view);
            txtNomAccion = (TextView) view.findViewById(R.id.txtNomAccion);
            txtFavAcronimo = (TextView) view.findViewById(R.id.txtFavAcronimo);
            txtPorAccion = (TextView) view.findViewById(R.id.txtPorAccion);
            txtValorAccion = (TextView) view.findViewById(R.id.txtValorAccion);
            imgLogoAccion = (ImageView) view.findViewById(R.id.imgLogoAccion);
            chrGrafico = (LineChart) view.findViewById(R.id.chrGrafico);
            view.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Context context = view.getContext();
            Bundle b = new Bundle();
            b.putInt("Accion", position);
            Intent intent = new Intent(context, StaticsActivity.class);
            intent.putExtras(b);
            context.startActivity(intent);
        }


    }
}
