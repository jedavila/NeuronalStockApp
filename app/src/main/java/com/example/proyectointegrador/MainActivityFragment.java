package com.example.proyectointegrador;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.proyectointegrador.ml.ModelBG;
import com.example.proyectointegrador.ml.ModelCF;
import com.example.proyectointegrador.ml.ModelCN;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


public class MainActivityFragment extends Fragment {
    private RecyclerView rcvFavorite;
    private RecyclerView rcvAcciones;
    private TextView txtNoFavorites;
    private Modelo modelo = Modelo.getInstance();
    private ProgressBar progressBar;
    private adapterFavorite adapterFavorite;
    private adapterAcciones adapterAcciones;
    private View view;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_main, container, false);
        rcvFavorite = view.findViewById(R.id.rcvFavoritas);
        txtNoFavorites = view.findViewById(R.id.txtNoFavorites);
        rcvFavorite.setHasFixedSize(true);
        rcvFavorite.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapterFavorite = new adapterFavorite(view.getContext(), rcvFavorite);
        rcvFavorite.setAdapter(adapterFavorite);
        rcvAcciones = view.findViewById(R.id.rcvAcciones);
        rcvAcciones.setHasFixedSize(true);
        rcvAcciones.setLayoutManager(new LinearLayoutManager(view.getContext()));
        adapterAcciones = new adapterAcciones(view.getContext(), rcvAcciones);
        rcvAcciones.setHasFixedSize(true);
        rcvAcciones.setAdapter(adapterAcciones);
        progressBar = view.findViewById(R.id.progressBar);
        downloadFileTask downloadFileTask = new downloadFileTask(getActivity());
        downloadFileTask.execute("");
        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        adapterFavorite.notifyDataSetChanged();
        if(adapterFavorite.getItemCount()==0){
            txtNoFavorites.setVisibility(View.VISIBLE);
        }else{
            txtNoFavorites.setVisibility(View.GONE);
        }

    }

    private class downloadFileTask extends AsyncTask<String, Void, Boolean> {
        Activity context;
        public  downloadFileTask(Activity context)
        {
            this.context=context;
        }
        private void doInferenceFavorita(){
            float [] data_predicted = new float[30];
            List<Float> data_predicted_normalized = new ArrayList<>();
            try {
                ModelCF model =ModelCF.newInstance(context);
                TensorBuffer inputFeature0=TensorBuffer.createFixedSize(new int[]{1,60,1}, DataType.FLOAT32);
                inputFeature0.loadArray(modelo.getAccionesList().get(2).getX_input());
                ModelCF.Outputs outputs=model.process(inputFeature0);
                TensorBuffer outputFeature0=outputs.getOutputFeature0AsTensorBuffer();
                data_predicted=outputFeature0.getFloatArray();
                model.close();

            }catch(IOException e){
                Log.d("ERROR: ", String.valueOf(e));
            }

            //Inverse Normalization Data
            float min = modelo.getAccionesList().get(2).getMinFeature();
            float scale = modelo.getAccionesList().get(2).getScale();
            for (int i = 0; i < 30; i++) {
                float value = (data_predicted[i]-min)/scale;
                data_predicted_normalized.add(value);
            }
            modelo.getAccionesList().get(2).setPreciosfuturo(data_predicted_normalized);
            Log.d("Valor En el futuro: ", data_predicted_normalized.toString());
        }
        private void doInferenceBancoGuayaquil(){
            float [] data_predicted = new float[30];
            List<Float> data_predicted_normalized = new ArrayList<>();
            try {
                ModelBG model = ModelBG.newInstance(context);
                TensorBuffer inputFeature0=TensorBuffer.createFixedSize(new int[]{1,60,1},DataType.FLOAT32);
                inputFeature0.loadArray(modelo.getAccionesList().get(1).getX_input());
                ModelBG.Outputs outputs=model.process(inputFeature0);
                TensorBuffer outputFeature0=outputs.getOutputFeature0AsTensorBuffer();
                data_predicted=outputFeature0.getFloatArray();
                model.close();

            }catch(IOException e){
                Log.d("ERROR: ", String.valueOf(e));
            }

            //Inverse Normalization Data
            float min = modelo.getAccionesList().get(1).getMinFeature();
            float scale = modelo.getAccionesList().get(1).getScale();
            for (int i = 0; i < 30; i++) {
                float value = (data_predicted[i]-min)/scale;
                data_predicted_normalized.add(value);
            }
            modelo.getAccionesList().get(1).setPreciosfuturo(data_predicted_normalized);
            Log.d("Valor En el futuro: ", data_predicted_normalized.toString());
        }
        private void doInferenceCerveceriaNacional(){
            float [] data_predicted = new float[30];
            List<Float> data_predicted_normalized = new ArrayList<>();
            try {
                ModelCN model = ModelCN.newInstance(context);
                TensorBuffer inputFeature0=TensorBuffer.createFixedSize(new int[]{1,60,1},DataType.FLOAT32);
                inputFeature0.loadArray(modelo.getAccionesList().get(0).getX_input());
                ModelCN.Outputs outputs=model.process(inputFeature0);
                TensorBuffer outputFeature0=outputs.getOutputFeature0AsTensorBuffer();
                data_predicted=outputFeature0.getFloatArray();
                model.close();

            }catch(IOException e){
                Log.d("ERROR: ", String.valueOf(e));
            }

            //Inverse Normalization Data
            float min = modelo.getAccionesList().get(0).getMinFeature();
            float scale = modelo.getAccionesList().get(0).getScale();
            for (int i = 0; i < 30; i++) {
                float value = (data_predicted[i]-min)/scale;
                data_predicted_normalized.add(value);
            }
            modelo.getAccionesList().get(0).setPreciosfuturo(data_predicted_normalized);
            Log.d("Valor En el futuro: ", data_predicted_normalized.toString());
        }

        private void createModel() throws IOException {
            LocalDate a = LocalDate.now();
            LocalDate pastYear = a.minusYears(1);
            File excel = new File(context.getExternalFilesDir("/accion"),"/acciones.xls");
            Workbook workBook = WorkbookFactory.create(excel);
            int sheetIterator = 0;
            int rowNo;
            AtomicReference<String> ticker = new AtomicReference<>("");
            AtomicInteger logo = new AtomicInteger();
            HashMap<String, List<Float>> precios = new HashMap<>();
            HashMap<String, List<LocalDate>> fechas = new HashMap<>();
            List<Acciones> accionesList = new ArrayList<>();
            AtomicReference<Float> bpa = new AtomicReference<>((float) 0);
            precios.put("CORPORACION FAVORITA C.A.", new ArrayList<>());
            precios.put("BANCO GUAYAQUIL S.A.", new ArrayList<>());
            precios.put("CERVECERIA NACIONAL CN S A", new ArrayList<>());
            fechas.put("CORPORACION FAVORITA C.A.", new ArrayList<>());
            fechas.put("BANCO GUAYAQUIL S.A.", new ArrayList<>());
            fechas.put("CERVECERIA NACIONAL CN S A", new ArrayList<>());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

            while(sheetIterator!=2){
                Sheet sheet = workBook.getSheetAt(sheetIterator);
                rowNo = 10;
                Row myRow = sheet.getRow(rowNo++);
                while (myRow != null) {
                    Cell fecha = myRow.getCell(1);
                    Cell nombre = myRow.getCell(2);
                    Cell precio = myRow.getCell(5);
                    if(precios.containsKey(nombre.toString())){
                        if(LocalDate.parse(fecha.toString(), formatter).isAfter(pastYear)){
                            if(!fechas.get(nombre.toString()).contains(LocalDate.parse(fecha.toString(), formatter))){
                                fechas.get(nombre.toString()).add(LocalDate.parse(fecha.toString(), formatter));
                                precios.get(nombre.toString()).add(Float.parseFloat(precio.toString()));
                            }
                        }
                    }
                    myRow = sheet.getRow(rowNo++);
                }
                sheetIterator++;
            }
            precios.forEach((key, value)->{
                if (key.equals("CORPORACION FAVORITA C.A.")){
                    logo.set(R.drawable.logo_favorita);
                    ticker.set("SLU");
                    bpa.set(0.06f);

                }else if(key.equals("BANCO GUAYAQUIL S.A.")){
                    logo.set(R.drawable.logo_gyq);
                    ticker.set("GYE");
                    bpa.set(0.07f);
                }
                else{
                    logo.set(R.drawable.logo_cer);
                    ticker.set("CCN");
                    bpa.set(2.67f);
                }
                Acciones acc = new Acciones(logo.get(), key, ticker.get(), value, fechas.get(key), bpa.get());
                accionesList.add(acc);
            });
            modelo.setAccionesList(accionesList);
        }

        private boolean isFileExists(String filename){
            File folder1 = new File(context.getExternalFilesDir("/accion"),filename);
            return folder1.exists();
        }

        @SuppressLint("Range")
        @Override
        protected Boolean doInBackground(String... strings) {
            if(isFileExists("/acciones.xls")){
                File folder1 = new File(context.getExternalFilesDir("/accion") + "/acciones.xls");
                folder1.delete();
            }
            String DownloadUrl = "https://www.bolsadequito.com/uploads/estadisticas/boletines/cotizaciones-historicas/acciones.xls";
            DownloadManager.Request request1 = new DownloadManager.Request(Uri.parse(DownloadUrl));
            request1.setDescription("Acciones de la Bolsa");   //appears the same in Notification bar while downloading
            request1.setTitle("acciones.xls");
            request1.setVisibleInDownloadsUi(false);

            request1.allowScanningByMediaScanner();
            request1.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
            request1.setDestinationInExternalFilesDir(context.getApplicationContext(), "/accion", "acciones.xls");

            DownloadManager manager1 = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
            Long downloadID = manager1.enqueue(request1);
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadID);
            Cursor cursor = ((DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE))
                    .query(query);
            int status = 0;
            if (cursor.moveToFirst())
            {
                while(status!=DownloadManager.STATUS_SUCCESSFUL){
                    query.setFilterById(downloadID);
                    cursor = ((DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE))
                            .query(query);
                    cursor.moveToFirst();
                    status=cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));

                }

            }
            return true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void onPostExecute(Boolean unused) {
            super.onPostExecute(unused);
            progressBar.setVisibility(View.GONE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out));
            if(isFileExists("/acciones.xls")){
                try {
                    createModel();
                    adapterAcciones.notifyDataSetChanged();
                    doInferenceFavorita();
                    doInferenceBancoGuayaquil();
                    doInferenceCerveceriaNacional();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}









