package koala.gestionEmployes;


import koala.db.ConnectionManager;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public abstract class Employe {
    private String nom;
    private String prenom;
    private Date dateNaissance;
    private Date dateEmbauche;
    private double salaire;
    private boolean salaireVerse;
    private String sexe;
    private Blob image;
    private String email;
    private String adresse;
    private String telephone;
    private double derniereEvaluation;
    private double salaireAnnuel;
    private String numeroSecuriteSociale;
    private int idMagasin;
    private int idCompte;

    public static Connection connection = ConnectionManager.getInstance().getConnection();

    protected static List<Employe> loadTableToList() throws SQLException {
        return null;
    }

    @Override
    public String toString() {
        return nom + " " + prenom;
    }

    public abstract int getIdSpecial();

    public abstract void setIdSpecial(int idSpecial);

    public static boolean delete(int id) throws Exception {
        return false;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public int getIdMagasin() {
        return idMagasin;
    }

    public void setIdMagasin(int idMagasin) {
        this.idMagasin = idMagasin;
    }

    public int getIdCompte() {
        return idCompte;
    }

    public void setIdCompte(int idCompte) {
        this.idCompte = idCompte;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Date getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public Date getDateEmbauche() {
        return dateEmbauche;
    }

    public void setDateEmbauche(Date dateEmbauche) {
        this.dateEmbauche = dateEmbauche;
    }

    public double getSalaire() {
        return salaire;
    }

    public void setSalaire(double salaire) {
        this.salaire = salaire;
    }

    public boolean isSalaireVerse() {
        return salaireVerse;
    }

    public void setSalaireVerse(boolean salaireVerse) {
        this.salaireVerse = salaireVerse;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public double getDerniereEvaluation() {
        return derniereEvaluation;
    }

    public void setDerniereEvaluation(double derniereEvaluation) {
        this.derniereEvaluation = derniereEvaluation;
    }

    public double getSalaireAnnuel() {
        return salaireAnnuel;
    }

    public void setSalaireAnnuel(double salaireAnnuel) {
        this.salaireAnnuel = salaireAnnuel;
    }

    public String getNumeroSecuriteSociale() {
        return numeroSecuriteSociale;
    }

    public void setNumeroSecuriteSociale(String numeroSecuriteSociale) {
        this.numeroSecuriteSociale = numeroSecuriteSociale;
    }

}
