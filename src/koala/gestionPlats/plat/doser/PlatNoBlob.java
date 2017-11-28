package koala.gestionPlats.plat.doser;

import koala.gestionPlats.plat.Plat;

import java.sql.Blob;

/**
 * Created by bjama on 5/10/2017.
 */
public class PlatNoBlob extends Plat{
    @Override
    public void setImage(Blob image) {
        super.setImage(null);
    }
}
