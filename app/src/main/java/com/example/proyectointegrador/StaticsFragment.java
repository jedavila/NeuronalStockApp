package com.example.proyectointegrador;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class StaticsFragment extends Fragment {

    private Modelo modelo = Modelo.getInstance();
    private Acciones accion;
    private TextView txtNomEmpresa;
    private TextView txtAcronimo;
    private ImageView imgArrowAction;
    private ImageView imgLogo;
    private TextView txtPrecioAccion;
    private TextView txtPorcentaje;
    private TextView txtFechaSel;
    private TextView txtPrecio;
    private LineChart chrGrafico;
    private LineChart chrFuturo;
    private ToggleButton favoriteButton;
    private RadioGroup contenedorFechas;
    private RadioGroup contenedorPrediccion;
    private RadioGroup contenedorFechasFuturo;
    private List<String> fechasFuturo;
    private int accionId;
    private Bundle b;
    private TextView txtMedia;
    private TextView txtStd;
    private TextView txtVar;
    private TextView txtPinicial;
    private TextView txtBpa;
    private TextView txtPE;
    private TextView txtPactual;
    private TextView txtRanual;
    private TextView txtRdiario;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_estadistica, container, false);
        fechasFuturo = new ArrayList<>();
        if (modelo.isLandscape()){
            b = getArguments();
        }else{
            b = getActivity().getIntent().getExtras();
        }
        txtFechaSel = (TextView) view.findViewById(R.id.txtFechaSel);
        txtPrecio = (TextView) view.findViewById(R.id.txtPrecio);
        setToggleButtons(view);

        if(b != null){
            accionId = b.getInt("Accion");
            accion = modelo.getAccionesList().get(accionId);
            setCardview(accion, view);
            generateFechasFuturo();
            setGraphFutureConfig(view);
            setGraphConfi(view);
        }
        setFavoriteButton(view);
        setMetricsText(view);
        
        return view;
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void setMetricsText(View view) {
        //ids
        txtMedia = (TextView) view.findViewById(R.id.txtMedia);
        txtStd = (TextView) view.findViewById(R.id.txtStd);
        txtVar = (TextView) view.findViewById(R.id.txtVar);
        txtPinicial = (TextView) view.findViewById(R.id.txtPinicial);
        txtBpa = (TextView) view.findViewById(R.id.txtBpa);
        txtPE = (TextView) view.findViewById(R.id.txtPE);
        txtPactual = (TextView) view.findViewById(R.id.txtPactual);
        txtRanual = (TextView) view.findViewById(R.id.txtRanual);
        txtRdiario = (TextView) view.findViewById(R.id.txtRdiario);
        //setTexts
        txtMedia.setText(String.format("%.02f",accion.getMedia()));
        txtStd.setText(String.format("%.03f",accion.getStd()));
        txtVar.setText(String.format("%.02f",accion.getVar()));
        txtPinicial.setText("$"+String.format("%.02f",accion.getpInicial()));
        txtBpa.setText("$"+String.format("%.02f",accion.getBpa()));
        txtPE.setText(String.format("%.02f",accion.getPeRatio())+"%");
        txtPactual.setText("$"+String.format("%.02f",accion.getpFinal()));
        txtRanual.setText(String.format("%.02f",accion.getrAnual())+"%");
        txtRdiario.setText(String.format("%.02f",accion.getrDiario())+"%");
    }

    private void setToggleButtons(View view){
        contenedorFechas = (RadioGroup)view.findViewById(R.id.rdgFechas);
        ((RadioButton)contenedorFechas.getChildAt(5)).setChecked(true);
        contenedorPrediccion = (RadioGroup)view.findViewById(R.id.togglePasFut);
        ((RadioButton)contenedorPrediccion.getChildAt(0)).setChecked(true);
        contenedorFechasFuturo = (RadioGroup) view.findViewById(R.id.rdgFechasFuturo);
        ((RadioButton)contenedorFechasFuturo.getChildAt(2)).setChecked(true);
        contenedorPrediccion.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.btnPasado:
                        txtFechaSel.setText(R.string.defaultFecha);
                        txtPrecio.setText(R.string.defaultPrecio);
                        contenedorFechasFuturo.setVisibility(View.INVISIBLE);
                        contenedorFechas.setVisibility(View.VISIBLE);
                        chrFuturo.setVisibility(View.INVISIBLE);
                        chrGrafico.setVisibility(View.VISIBLE);
                        break;
                    case R.id.btnFuturo:
                        txtFechaSel.setText(R.string.defaultFecha);
                        txtPrecio.setText(R.string.defaultPrecio);
                        contenedorFechas.setVisibility(View.INVISIBLE);
                        contenedorFechasFuturo.setVisibility(View.VISIBLE);
                        chrGrafico.setVisibility(View.INVISIBLE);
                        chrFuturo.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

    }

    private void setFavoriteButton(View view) {
        favoriteButton = (ToggleButton) view.findViewById(R.id.btnFavorite);
        if(modelo.getFavoritasList().contains(accion)){
            favoriteButton.setChecked(true);
        }
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(favoriteButton.isChecked()){
                    modelo.getFavoritasList().add(accion);

                }else{
                    modelo.getFavoritasList().remove(accion);
                }
            }
        });

    }

    private void setCardview(Acciones accion, View view){
        txtNomEmpresa = (TextView) view.findViewById(R.id.txtNomEmpresa);
        txtAcronimo = (TextView) view.findViewById(R.id.txtAcronimo);
        txtPrecioAccion = (TextView) view.findViewById(R.id.txtPrecioAccion);
        txtPorcentaje = (TextView) view.findViewById(R.id.txtPorcentaje);
        imgArrowAction = (ImageView) view.findViewById(R.id.imgArrowAccion);
        imgLogo = (ImageView) view.findViewById(R.id.imgLogo);
        txtNomEmpresa.setText(accion.getNombre());
        txtAcronimo.setText(accion.getTicker());
        if(accion.isAltbaj()){
            imgArrowAction.setImageResource(R.drawable.ic_alta_accion);
        }else{
            imgArrowAction.setImageResource(R.drawable.ic_baja_accion);
        }
        txtPrecioAccion.setText(accion.getValorMercado());
        txtPorcentaje.setText(accion.getPorcentajePeriodo());
        imgLogo.setImageResource(accion.getLogo());

    }

    @SuppressLint("NonConstantResourceId")
    private void setGraphConfi(View view){
        int colorGreen = ContextCompat.getColor(Objects.requireNonNull(getActivity()), R.color.green);
        int colorRed = ContextCompat.getColor(getActivity(), R.color.red);
        int colorWhite = ContextCompat.getColor(getActivity(), R.color.white);
        List<LocalDate> fechas = accion.getFechas();
        List<Float> precios = accion.getPrecios();
        chrGrafico = (LineChart) view.findViewById(R.id.chrGrafico);
        chrGrafico.setDragEnabled(true);
        chrGrafico.setScaleEnabled(true);
        chrGrafico.getLegend().setEnabled(false);
        chrGrafico.getDescription().setEnabled(false);
        chrGrafico.getXAxis().setTextColor(colorWhite);
        chrGrafico.getAxisLeft().setEnabled(false);
        chrGrafico.getAxisRight().setTextColor(colorWhite);
        chrGrafico.setScaleMinima(1f, 1f);
        chrGrafico.moveViewToX(precios.size());

        ArrayList<Entry> yValues = new ArrayList<>();
        for (int i = 0; i < precios.size(); i++) {
            yValues.add(new Entry(i, precios.get(i)));
        }

        LineDataSet set1 = new LineDataSet(yValues, "Data Set 1");
        set1.setFillAlpha(0);
        set1.setCircleRadius(4);
        if(accion.isAltbaj()){
            set1.setColor(colorGreen);
            set1.setCircleColor(colorGreen);
        }
        else{
            set1.setColor(colorRed);
            set1.setCircleColor(colorRed);
        }

        set1.setDrawCircles(false);
        set1.setLineWidth(1);
        set1.setHighLightColor(ContextCompat.getColor(getActivity(), R.color.grey));
        set1.setHighlightLineWidth(2);
        set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setDrawValues(false);
        contenedorFechas.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId){
                case R.id.btnDia:
                    chrGrafico.fitScreen();
                    chrGrafico.setScaleMinima(365f, 1f);
                    chrGrafico.moveViewToX(precios.size());
                    break;
                case R.id.btnSemana:
                    chrGrafico.fitScreen();
                    chrGrafico.setScaleMinima(52f, 1f);
                    chrGrafico.moveViewToX(precios.size());
                    break;
                case R.id.btnMes:
                    chrGrafico.fitScreen();
                    chrGrafico.setScaleMinima(12f, 1f);
                    chrGrafico.moveViewToX(precios.size());
                    break;
                case R.id.btnTrimestre:
                    chrGrafico.fitScreen();
                    chrGrafico.setScaleMinima(4f, 1f);
                    chrGrafico.getXAxis().setLabelCount(3);
                    chrGrafico.moveViewToX(precios.size());
                    break;
                case R.id.btnSemestre:
                    chrGrafico.fitScreen();
                    chrGrafico.setScaleMinima(2f, 1f);
                    chrGrafico.moveViewToX(precios.size());
                    break;
                case R.id.btnAno:
                    chrGrafico.fitScreen();
                    chrGrafico.setScaleMinima(1f, 1f);
                    chrGrafico.moveViewToX(precios.size());
                    break;
            }
        });

        chrGrafico.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                txtFechaSel.setText(fechas.get((int)e.getX()).toString());
                txtPrecio.setText(Float.toString(e.getY()));
            }

            @Override
            public void onNothingSelected() {

            }
        });
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        LineData data = new LineData(dataSets);

        //xAxis

        chrGrafico.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return fechas.get((int) value).toString();
            }
        });
        chrGrafico.getXAxis().setGranularityEnabled(true);
        chrGrafico.getXAxis().setGranularity(1f);
        chrGrafico.getXAxis().setLabelCount(5);
        chrGrafico.setData(data);
    }
    protected void generateFechasFuturo(){
        for (int i = 1; i <= 30; i++) {
            LocalDate futureDate = LocalDate.now().plusDays(i);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String dateStr = futureDate.format(formatter);
            fechasFuturo.add(dateStr);
        }
    }

    @SuppressLint("NonConstantResourceId")
    private void setGraphFutureConfig(View view){
        int colorGreen = ContextCompat.getColor(Objects.requireNonNull(getActivity()), R.color.green);
        int colorRed = ContextCompat.getColor(getActivity(), R.color.red);
        int colorWhite = ContextCompat.getColor(getActivity(), R.color.white);
        List<Float> precios = accion.getPreciosfuturo();
        chrFuturo = (LineChart) view.findViewById(R.id.chrFuturo);
        chrFuturo.setDragEnabled(true);
        chrFuturo.setScaleEnabled(true);
        chrFuturo.getLegend().setEnabled(false);
        chrFuturo.getDescription().setEnabled(false);
        chrFuturo.getXAxis().setTextColor(colorWhite);
        chrFuturo.getAxisLeft().setEnabled(false);
        chrFuturo.getAxisRight().setTextColor(colorWhite);
        chrFuturo.setScaleMinima(1f, 1f);
        chrFuturo.moveViewToX(precios.size());
        contenedorFechasFuturo.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId){
                case R.id.btnDiaFuturo:
                    chrFuturo.fitScreen();
                    chrFuturo.setScaleMinima(30f, 1f);
                    chrFuturo.moveViewToX(precios.size());
                    break;
                case R.id.btnSemanaFuturo:
                    chrFuturo.fitScreen();
                    chrFuturo.setScaleMinima(4f, 1f);
                    chrFuturo.getXAxis().setLabelCount(3);
                    chrFuturo.moveViewToX(precios.size());
                    break;
                case R.id.btnMesFuturo:
                    chrFuturo.fitScreen();
                    chrFuturo.setScaleMinima(1f, 1f);
                    chrFuturo.moveViewToX(precios.size());
                    break;
            }
        });

        ArrayList<Entry> yValues = new ArrayList<>();
        for (int i = 0; i < precios.size(); i++) {
            yValues.add(new Entry(i, precios.get(i)));
        }

        LineDataSet set1 = new LineDataSet(yValues, "Data Set 1");
        set1.setFillAlpha(0);
        if(precios.get(0)<=precios.get(precios.size()-1)){
            set1.setColor(colorGreen);
            set1.setCircleColor(colorGreen);
        }
        else{
            set1.setColor(colorRed);
            set1.setCircleColor(colorRed);
        }


        set1.setDrawCircles(true);
        set1.setLineWidth(1);
        set1.setHighLightColor(ContextCompat.getColor(getActivity(), R.color.grey));
        set1.setHighlightLineWidth(2);
        set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setDrawValues(false);

        chrFuturo.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                txtFechaSel.setText(fechasFuturo.get((int)e.getX()).toString());
                txtPrecio.setText(Float.toString(e.getY()));
            }

            @Override
            public void onNothingSelected() {

            }
        });
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        LineData data = new LineData(dataSets);

        //xAxis

        chrFuturo.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return fechasFuturo.get((int) value);
            }
        });
        chrFuturo.getXAxis().setGranularityEnabled(true);
        chrFuturo.getXAxis().setGranularity(1f);
        chrFuturo.getXAxis().setLabelCount(5);
        chrFuturo.setData(data);
    }
}















