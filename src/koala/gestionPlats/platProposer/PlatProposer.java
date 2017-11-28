package koala.gestionPlats.platProposer;


import java.sql.Blob;
import java.sql.Date;


public class PlatProposer {
    private int idPlatProposer;
    private int idCompte;
    private String nom;
    private String description;
    private Blob image;
    private Date date;

    @Override
    public String toString() {
        return nom;
    }

    public PlatProposer() {
    }

    public PlatProposer(int idCompte, String nom, String description, Blob image, Date date) {
        this.idCompte = idCompte;
        this.nom = nom;
        this.description = description;
        this.image = image;
        this.date = date;
    }


    public int getIdPlatProposer() {
        return idPlatProposer;
    }

    public void setIdPlatProposer(int idPlatProposer) {
        this.idPlatProposer = idPlatProposer;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
