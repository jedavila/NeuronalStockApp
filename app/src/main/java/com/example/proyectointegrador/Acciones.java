package com.example.proyectointegrador;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Acciones implements Serializable {
    private int logo;
    private String nombre;
    private String ticker;
    private String valorMercado; //Valor Mercado Actual
    private String porcentajePeriodo;
    private boolean altbaj;
    private List<Float> precios;
    private List<LocalDate> fechas;
    private float[] x_input;
    private float minFeature;
    private float scale;
    private List<Float> preciosfuturo;
    //Metricas por accion
    private float media;
    private float std;
    private float var;
    private float bpa;
    private float peRatio;
    private float pInicial;
    private float pFinal;
    private float rAnual;
    private float rDiario;

    public Acciones(int logo, String nombre, String ticker, List<Float> precios, List<LocalDate> fechas,float BPA) {
        this.logo = logo;
        this.nombre = nombre;
        this.ticker = ticker;
        this.precios = precios;
        this.valorMercado = Float.toString(this.precios.get(precios.size()-1)); //Precio de la Accion Actual
        this.fechas = fechas;
        this.porcentajePeriodo = this.calcularPorcentaje();
        this.x_input = inicializarDatos();
        this.preciosfuturo = new ArrayList<>();
        this.pInicial = precios.get(0);
        this.pFinal = precios.get(precios.size()-1);
        this.bpa = BPA;
        this.media = calculoMedia();
        this.std = calculoSTD();
        this.var = calculoVariacion();
        this.peRatio = calculoRatio();
        this.rDiario = calculoDiario();
        this.rAnual = calculoAnual();

    }

    private float calculoAnual() {
        return ((this.getpFinal()/this.getpInicial())-1)*100;
    }

    private float calculoDiario() {
        return ((precios.get(precios.size()-1)/precios.get(precios.size()-2))-1)*100;
    }

    protected float calculoMedia(){
        return (float) precios.stream().mapToDouble(p->p).average().orElse(0.0);
    }
    protected float calculoSTD(){
        float s = 0f;
        for (float precio: precios) {
            s+=Math.pow((precio - media),2);
        }
        return s/(precios.size()-1);
    }
    protected float calculoVariacion(){
        return (float) Math.sqrt(this.getStd());

    }
    protected float calculoRatio(){
        return (this.getpFinal()/this.getBpa());
    }
    protected float[] inicializarDatos(){
        List<Float> last_precios = new ArrayList<>();
        List<Float> normalize_precios = new ArrayList<>();
        float[] x = new float[60];
        float max;
        float min;
        int newMax = 1;
        int newMin = 0;
        int i =60;
        //Normalizamos los datos
        max = Collections.max(precios, null);
        min = Collections.min(precios, null);
        for (int j=0;j<precios.size();j++){
            float value = ((precios.get(j)-min)/(max-min));
            normalize_precios.add(value);
        }
        while (i>0){
            last_precios.add(normalize_precios.get(normalize_precios.size()-i));
            i--;
        }
        for(int j =0; j<last_precios.size();j++){
            x[j]=last_precios.get(j);
        }
        this.setScale((float)(newMax-newMin)/(max-min));
        this.setMinFeature((float)newMin-min*this.getScale());
        return x;
    }

    public List<Float> getPreciosfuturo() {
        return preciosfuturo;
    }

    public void setPreciosfuturo(List<Float> preciosfuturo) {
        this.preciosfuturo = preciosfuturo;
    }

    public float getMinFeature() {
        return minFeature;
    }

    public void setMinFeature(float minFeature) {
        this.minFeature = minFeature;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float[] getX_input() {
        return x_input;
    }

    public void setX_input(float[] x_input) {
        this.x_input = x_input;
    }

    public List<Float> getPrecios() {
        return precios;
    }

    public void setPrecios(List<Float> precios) {
        this.precios = precios;
    }

    public List<LocalDate> getFechas() {
        return fechas;
    }

    public void setFechas(List<LocalDate> fechas) {
        this.fechas = fechas;
    }


    private String calcularPorcentaje() {
        LocalDate a = LocalDate.now();
        LocalDate b = a.minusMonths(1);
        LocalDate c = a.minusMonths(2);
        List<LocalDate> fechas2021 = new ArrayList<>();
        for(LocalDate fecha: fechas){
            if(fecha.isBefore(b) && fecha.isAfter(c)){
                fechas2021.add(fecha);
            }
        }
        int indFecha = fechas.indexOf(fechas2021.get(fechas2021.size()-1));
        float indPrecioActual = precios.get(precios.size()-1);
        float indPrecioPasado = precios.get(indFecha);
        float porcentaje = ((indPrecioActual/indPrecioPasado)-1)*100;
        if (porcentaje>0){ this.altbaj = true;}
        String porcentajePeriodo = String.format("%.2f", porcentaje)+"%";
        return porcentajePeriodo;

    }

    public String getPorcentajePeriodo() {
        return porcentajePeriodo;
    }

    public void setPorcentajePeriodo(String porcentajePeriodo) {
        this.porcentajePeriodo = porcentajePeriodo;
    }

    public int getLogo() {
        return logo;
    }

    public void setLogo(int logo) {
        this.logo = logo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getValorMercado() {
        return valorMercado;
    }

    public void setValorMercado(String valorMercado) {
        this.valorMercado = valorMercado;
    }


    public boolean isAltbaj() {
        return altbaj;
    }

    public void setAltbaj(boolean altbaj) {
        this.altbaj = altbaj;
    }

    public float getMedia() {
        return media;
    }

    public void setMedia(float media) {
        this.media = media;
    }

    public float getStd() {
        return std;
    }

    public void setStd(float std) {
        this.std = std;
    }

    public float getVar() {
        return var;
    }

    public void setVar(float var) {
        this.var = var;
    }

    public float getBpa() {
        return bpa;
    }

    public void setBpa(float bpa) {
        this.bpa = bpa;
    }

    public float getpInicial() {
        return pInicial;
    }

    public void setpInicial(float pInicial) {
        this.pInicial = pInicial;
    }

    public float getpFinal() {
        return pFinal;
    }

    public void setpFinal(float pFinal) {
        this.pFinal = pFinal;
    }

    public float getrAnual() {
        return rAnual;
    }

    public void setrAnual(float rAnual) {
        this.rAnual = rAnual;
    }

    public float getrDiario() {
        return rDiario;
    }

    public void setrDiario(float rDiario) {
        this.rDiario = rDiario;
    }

    public float getPeRatio() {
        return peRatio;
    }

    public void setPeRatio(float peRatio) {
        this.peRatio = peRatio;
    }

    @Override
    public String toString() {
        return "Acciones{" +
                "logo=" + logo +
                ", nombre='" + nombre + '\'' +
                ", ticker='" + ticker + '\'' +
                ", valorMercado='" + valorMercado + '\'' +
                ", porcentajePeriodo='" + porcentajePeriodo + '\'' +
                ", altbaj=" + altbaj +
                '}';
    }
}
