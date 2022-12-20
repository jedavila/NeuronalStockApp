package com.example.proyectointegrador;

import java.util.ArrayList;
import java.util.List;

public class Modelo {
    private List<Acciones> accionesList;
    private List<Acciones> favoritasList;
    private boolean landscape = false;
    private static Modelo ourInstance = new Modelo();
    public static Modelo getInstance() {
        return ourInstance;
    }

    public Modelo() {
        this.accionesList = new ArrayList<>();
        this.favoritasList = new ArrayList<>();
    }

    public boolean isLandscape() {
        return landscape;
    }

    public void setLandscape(boolean landscape) {
        this.landscape = landscape;
    }

    public List<Acciones> getAccionesList() {
        return accionesList;
    }

    public void setAccionesList(List<Acciones> accionesList) {
        this.accionesList = accionesList;
    }

    public List<Acciones> getFavoritasList() {
        return favoritasList;
    }

    public void setFavoritasList(List<Acciones> favoritasList) {
        this.favoritasList = favoritasList;
    }
}
