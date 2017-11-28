package koala.gui.magasinier.controller;

import javafx.beans.property.SimpleStringProperty;

public class IngredientProperty {

    private SimpleStringProperty magasin;
    private SimpleStringProperty id;
    private SimpleStringProperty name;
    private SimpleStringProperty quantitie;
    private SimpleStringProperty prixAchat;
    private SimpleStringProperty datePeremption;
    private SimpleStringProperty description;
    private SimpleStringProperty calorie;
    private SimpleStringProperty proteine;
    private SimpleStringProperty glucide;
    private SimpleStringProperty lipide;
    private SimpleStringProperty gluten;

    public IngredientProperty() {
        this.magasin = new SimpleStringProperty("");
        this.id = new SimpleStringProperty("");
        this.name = new SimpleStringProperty("");
        this.quantitie = new SimpleStringProperty("");
        this.prixAchat = new SimpleStringProperty("");
        this.datePeremption = new SimpleStringProperty("");
        this.description = new SimpleStringProperty("");
        this.calorie = new SimpleStringProperty("");
        this.proteine = new SimpleStringProperty("");
        this.glucide = new SimpleStringProperty("");
        this.lipide = new SimpleStringProperty("");
        this.gluten = new SimpleStringProperty("");

    }

    public IngredientProperty(String magasin, String id, String name, String quantite, String prixAchat, String datePeremption, String description, String calorie, String proteine, String glucide, String lipide, String gluten) {
        this.magasin = new SimpleStringProperty(magasin);
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.quantitie = new SimpleStringProperty(quantite);
        this.prixAchat = new SimpleStringProperty(prixAchat);
        this.datePeremption = new SimpleStringProperty(datePeremption);
        this.description = new SimpleStringProperty(description);
        this.calorie = new SimpleStringProperty(calorie);
        this.proteine = new SimpleStringProperty(proteine);
        this.glucide = new SimpleStringProperty(glucide);
        this.lipide = new SimpleStringProperty(lipide);
        this.gluten = new SimpleStringProperty(gluten);
    }

    public String getMagasin() {
        return magasin.get();
    }

    public void setMagasin(String magasin) {
        this.magasin.set(magasin);
    }

    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getQuantitie() {
        return quantitie.get();
    }

    public void setQuantitie(String quantitie) {
        this.quantitie.set(quantitie);
    }

    public String getPrixAchat() {
        return prixAchat.get();
    }

    public void setPrixAchat(String prixAchat) {
        this.prixAchat.set(prixAchat);
    }

    public String getDatePeremption() {
        return datePeremption.get();
    }

    public void setDatePeremption(String datePeremption) {
        this.datePeremption.set(datePeremption);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getCalorie() {
        return calorie.get();
    }

    public void setCalorie(String calorie) {
        this.calorie.set(calorie);
    }

    public String getProteine() {
        return proteine.get();
    }

    public void setProteine(String proteine) {
        this.proteine.set(proteine);
    }

    public String getGlucide() {
        return glucide.get();
    }

    public void setGlucide(String glucide) {
        this.glucide.set(glucide);
    }

    public String getLipide() {
        return lipide.get();
    }

    public void setLipide(String lipide) {
        this.lipide.set(lipide);
    }

    public String getGluten() {
        return gluten.get();
    }

    public void setGluten(String gluten) {
        this.gluten.set(gluten);
    }


    @Override
    public String toString() {
        return getMagasin() + "::" + getId() +"::"+ getName() +"::"+ getQuantitie() +"::"+ getPrixAchat() +"::"+ getDatePeremption() +"::"+ getDescription() +"::"+ getCalorie() +"::"+ getProteine() +"::"+ getGlucide() +"::"+ getLipide() +"::"+ getGluten();
    }

    @Override
    public boolean equals(Object obj) {
        return getId().equals(((IngredientProperty) obj).getId());
    }
}
