package com.example.proyectointegrador;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class adapterAcciones extends RecyclerView.Adapter<adapterAcciones.ViewHolder>{
    private Modelo modelo = Modelo.getInstance();
    private Context context;
    private RecyclerView recyclerView;

    public adapterAcciones(Context context, RecyclerView recyclerView) {
        this.context = context;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public adapterAcciones.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.action_card, parent, false);
        return new adapterAcciones.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adapterAcciones.ViewHolder holder, int position) {
        Acciones accion = modelo.getAccionesList().get(position);
        holder.txtNomEmpresa.setText(accion.getNombre());
        holder.txtAcronimo.setText(accion.getTicker());
        if(accion.isAltbaj()){
            holder.imgArrowAction.setImageResource(R.drawable.ic_alta_accion);
        }else{
            holder.imgArrowAction.setImageResource(R.drawable.ic_baja_accion);
        }
        holder.txtPrecioAccion.setText(accion.getValorMercado());
        holder.txtPorcentaje.setText(accion.getPorcentajePeriodo());
        holder.imgLogo.setImageResource(accion.getLogo());

    }

    @Override
    public int getItemCount() {
        return modelo.getAccionesList().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView txtNomEmpresa;
        TextView txtAcronimo;
        ImageView imgArrowAction;
        ImageView imgLogo;
        TextView txtPrecioAccion;
        TextView txtPorcentaje;
        MainActivity mainActivity = (MainActivity) context;

        public ViewHolder(@NonNull View view) {
            super(view);
            txtNomEmpresa = (TextView) view.findViewById(R.id.txtNomEmpresa);
            txtAcronimo = (TextView) view.findViewById(R.id.txtAcronimo);
            txtPrecioAccion = (TextView) view.findViewById(R.id.txtPrecioAccion);
            txtPorcentaje = (TextView) view.findViewById(R.id.txtPorcentaje);
            imgArrowAction = (ImageView) view.findViewById(R.id.imgArrowAccion);
            imgLogo = (ImageView) view.findViewById(R.id.imgLogo);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if(modelo.isLandscape()){
                Bundle arguments = new Bundle();
                arguments.putInt("Accion", position);
                StaticsFragment fragmentStatics = new StaticsFragment();
                fragmentStatics.setArguments(arguments);
                mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frgStatics, fragmentStatics)
                        .commit();

            }else{
                Bundle b = new Bundle();
                b.putInt("Accion", position);
                Intent intent = new Intent(context, StaticsActivity.class);
                intent.putExtras(b);
                context.startActivity(intent);
            }


        }
    }
}





















