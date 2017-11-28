package koala.gestionStock.mini;

/**
 * Created by bjama on 4/14/2017.
 */
public class IngredientAndQuantite {
    private int idIngredient;
    private int quantite;

    public IngredientAndQuantite(int idIngredient, int quantite) {
        this.idIngredient = idIngredient;
        this.quantite = quantite;
    }

    public int getIdIngredient() {
        return idIngredient;
    }

    public void setIdIngredient(int idIngredient) {
        this.idIngredient = idIngredient;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
}
