package koala.gestionStock;

import java.time.LocalDate;

/**
 * Created by bjama on 4/15/2017.
 */
public class IngredientPropritiete {
    private int idIngredient;
    private int quantitie;
    private double prixAchat;
    private LocalDate datePeremption;

    public IngredientPropritiete(int idIngredient, int quantitie, double prixAchat, LocalDate datePeremption) {
        this.idIngredient = idIngredient;
        this.quantitie = quantitie;
        this.prixAchat = prixAchat;
        this.datePeremption = datePeremption;
    }

    public int getIdIngredient() {
        return idIngredient;
    }

    public void setIdIngredient(int idIngredient) {
        this.idIngredient = idIngredient;
    }

    public int getQuantitie() {
        return quantitie;
    }

    public void setQuantitie(int quantitie) {
        this.quantitie = quantitie;
    }

    public double getPrixAchat() {
        return prixAchat;
    }

    public void setPrixAchat(double prixAchat) {
        this.prixAchat = prixAchat;
    }

    public LocalDate getDatePeremption() {
        return datePeremption;
    }

    public void setDatePeremption(LocalDate datePeremption) {
        this.datePeremption = datePeremption;
    }
}
