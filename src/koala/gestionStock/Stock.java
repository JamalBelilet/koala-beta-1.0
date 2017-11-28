package koala.gestionStock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bjama on 3/24/2017.
 */
public class Stock {



    private int idMagasin;
    public List<IngredientPropritiete> ingredientPropreties = new ArrayList<>();

    public Stock() {
    }

    public Stock(int idMagasin) {
        this.idMagasin = idMagasin;
    }

    public int getIdMagasin() {
        return idMagasin;
    }

    public void setIdMagasin(int idMagasin) {
        this.idMagasin = idMagasin;
    }
}
