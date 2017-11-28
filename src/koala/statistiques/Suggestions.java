package koala.statistiques;


import koala.gestionPlats.categorie.Categorie;
import koala.gestionPlats.categorie.CategorieManager;
import koala.gestionPlats.plat.Plat;
import koala.gestionPlats.plat.PlatManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Suggestions {

    public static String miseEnAvantPlat() throws SQLException {
        List<Plat> liste = null;

        try {
            liste = PlatManager.loadTableToList();
        } catch (SQLException e) {
            return null;
        }
        Collections.sort(liste);

        if (liste == null)
            return null;

        String s = "Le plat \"" + liste.get(0).getNom() + "\" (#" + liste.get(0).getIdPlat() +
                ") est le plat le moins vendu de votre menu. " +
                "Il ne s'est vendu que " + Statistique.platCounterAll(liste.get(0).getIdPlat()) +
                " fois. Peut être voudriez-vous le mettre en avant davantage.";

        if (Statistique.platCounterAll(liste.get(0).getIdPlat()) == 0) {
            s = "Le plat \"" + liste.get(0).getNom() + "\" (#" + liste.get(0).getIdPlat() +
                    ") ne s'est jamais vendu. Peut être voudriez-vous le mettre en avant davantage.";
        }

        return s;
    }

    public static String miseEnAvantCategorie() {
        List<Categorie> liste = null;

        try {
            liste = CategorieManager.loadTableToList();
        } catch (SQLException e) {
            return null;
        }

        Collections.sort(liste);

        if (liste == null)
            return null;

        String s = "La catégorie \"" + liste.get(0).getNom() + "\" (#" + liste.get(0).getIdCategorie() +
                ") semble ne pas être très populaire auprès de vos clients. " +
                " Peut être voudriez-vous la mettre plus en avant ou l'enrichir avec d'autres plats.";

        return s;
    }

    public static List<String> margeVentePlat() throws SQLException {
        List<String> listeString = new ArrayList<>();
        List<Plat> listePlat = PlatManager.loadTableToList();

        double tauxDeMarge;

        for (Plat p: listePlat
             ) {
            tauxDeMarge = ((p.getPrix() - p.getCost()) / p.getCost()) * 100;

            if (tauxDeMarge < 0)
                listeString.add("Attention ! Vous êtes en train de vendre le plat \"" + p.getNom() + "\" " +
                "(#" + p.getIdPlat() + ") à perte. Peut être voudriez-vous revoir son prix à la hausse " +
                        "ou changer sa recette.\n" +
                "Coût brute : " + (int)p.getCost() + "\n" +
                "Prix de vente : " + (int)p.getPrix());

            else if (tauxDeMarge <= 60)
                listeString.add("Le taux de marge du plat \"" + p.getNom() + "\" " +
                        "(#" + p.getIdPlat() + ") à la vente n'est que de " + (int)tauxDeMarge + "%. " +
                        "Peut être voudriez-vous revoir son prix à la hausse.\n" +
                        "Coût brute : " + (int)p.getCost() + "\n" +
                        "Prix de vente : " + (int)p.getPrix());
        }

        return listeString;
    }

    public static List<String> getAll() throws SQLException {
        List<String> list = new ArrayList<>();

        list.addAll(margeVentePlat());
        list.add(miseEnAvantCategorie());
        list.add(miseEnAvantPlat());

        return list;
    }

}
