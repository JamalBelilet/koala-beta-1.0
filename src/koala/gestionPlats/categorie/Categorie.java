package koala.gestionPlats.categorie;


import koala.statistiques.Statistique;

import java.sql.Blob;
import java.sql.SQLException;

public class Categorie implements Comparable<Categorie> {
    private int idCategorie;
    private String nom;
    private Blob image;
    private String description;


    public int getIdCategorie() {
        return idCategorie;
    }

    public void setIdCategorie(int idCategorie) {
        this.idCategorie = idCategorie;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int compareTo(Categorie c) {
        try {
            if (Statistique.categorieCounterAll(this.getIdCategorie()) < Statistique.categorieCounterAll(c.getIdCategorie()))
                return -1;
            else if (Statistique.categorieCounterAll(this.getIdCategorie()) > Statistique.categorieCounterAll(c.getIdCategorie()))
                return 1;
            else if (Statistique.categorieCounterAll(this.getIdCategorie()) == Statistique.categorieCounterAll(c.getIdCategorie()))
                return 0;
            else
                return -1;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public String toString() {
        return  nom + " #" + idCategorie;
    }
}
