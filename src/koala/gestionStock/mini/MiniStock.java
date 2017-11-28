package koala.gestionStock.mini;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asus on 4/4/2017.
 */
public class MiniStock {
    private int idCuisinier;
    public List<IngredientAndQuantite> ingredientAndQuantites = new ArrayList<>();


    public int getIdCuisinier() {
        return idCuisinier;
    }

    public void setIdCuisinier(int idCuisinier) {
        this.idCuisinier = idCuisinier;
    }



}
