package koala.gestionCommandes.commandeUnitaire;

import koala.db.ConnectionManager;
import koala.gestionCommandes.Etat;
import koala.gestionCommandes.commande.Commande;
import koala.gestionCommandes.commande.CommandeManager;
import koala.gestionEmployes.Cuisiner;
import koala.gestionEmployes.Employe;
import koala.gestionPlats.plat.Plat;
import koala.gestionStock.IngredientPropritiete;
import koala.gestionStock.Stock;
import koala.gestionStock.StockManager;
import koala.gestionStock.mini.IngredientAndQuantite;
import koala.gestionStock.mini.MiniStock;
import koala.gestionStock.mini.MiniStockManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static koala.gestionCommandes.commandeUnitaire.CommandeUnitaireManager.tempsRestantCuisiner;

public class CommandeUnitaire implements Comparable<CommandeUnitaire>{

    //modification dans la base de donnes (add tempsDebut);
    //utilisez la methode CommandeUnitaire commander(...) pour creer une ArrayList<CommandeUnitaire>;

    private int idCommande;
    private Plat plat;
    private int idCuisiner;
    private int quantite;
    private LocalDateTime tempsDebut;
    private Etat etat;

    public static Connection conn = ConnectionManager.getInstance().getConnection();

    public void setIdCommande(int idCommande){
        this.idCommande=idCommande;
    }
    public int getIdCommande(){
        return this.idCommande;
    }
    public void setPlat(Plat plat){
        this.plat=plat;
    }
    public Plat getPlat(){
        return this.plat;
    }
    public void setIdCuisiner(int idCuisiner){
        this.idCuisiner=idCuisiner;
    }
    public int getIdCuisiner(){
        return this.idCuisiner;
    }
    public void setQuantite(int quantite){
        this.quantite=quantite;
    }
    public int getQuantite(){
        return this.quantite;
    }
    public void setTempsDebut(LocalDateTime tempsDebut){
        this.tempsDebut=tempsDebut;
    }
    public LocalDateTime getTempsDebut(){
        return this.tempsDebut;
    }
    public void setEtat(Etat etat){
        this.etat=etat;
    }
    public Etat getEtat(){
        return this.etat;
    }



    public CommandeUnitaire(){
    }
    public CommandeUnitaire(int idCommande, Plat plat, int quantite){
        this.setIdCommande(idCommande);
        this.setPlat(plat);
        this.setQuantite(quantite);
        this.setIdCuisiner(-1);
        this.setEtat(Etat.T);
        this.setTempsDebut(null);
    }
    public CommandeUnitaire(int idCommande, Plat plat, int idCuisiner, int quantite, LocalDateTime tempsDebut, Etat etat){
        this(idCommande, plat, quantite);
        this.setIdCuisiner(idCuisiner);
        this.setTempsDebut(tempsDebut);
        this.setEtat(etat);
    }



    public boolean miniStockSuffisantCommande(){
        MiniStock miniStock=MiniStockManager.getMiniStock(this.getIdCuisiner());
        for (IngredientAndQuantite iq :
                this.getPlat().ingredientAndQuantites) {
            int quantite=0;
            for (IngredientAndQuantite iqAtMini :
                    miniStock.ingredientAndQuantites) {
                if (iq.getIdIngredient() == iqAtMini.getIdIngredient()) {
                    quantite=iqAtMini.getQuantite();
                    break;
                }
            }
            if (quantite<(this.getQuantite()*iq.getQuantite())) return false;
        }
        return true;
    }


    public boolean stockSuffisantCommande(Cuisiner cuisiner){
        MiniStock miniStock=MiniStockManager.getMiniStock(cuisiner.getIdCuisiner());
        Stock stock= StockManager.getStock(cuisiner.getIdMagasin());
        for (IngredientAndQuantite iq :
                this.getPlat().ingredientAndQuantites) {
            int quantite=0;
            for (IngredientAndQuantite iqAtMini :
                    miniStock.ingredientAndQuantites) {
                if (iq.getIdIngredient() == iqAtMini.getIdIngredient()) {
                    quantite+=iqAtMini.getQuantite();
                    break;
                }
            }
            for (IngredientPropritiete iqAtStock :
                    stock.ingredientPropreties){
                if (iq.getIdIngredient() == iqAtStock.getIdIngredient()) {
                    quantite+=iqAtStock.getQuantitie();
                    break;
                }
            }
            if (quantite<this.getQuantite()*iq.getQuantite()) return false;
        }
        return true;
    }


    public Cuisiner cuisinerStockSuffisant() throws Exception {
        List<Employe> employess = Cuisiner.loadTableToList();
        ArrayList<Cuisiner> cuisiners = new ArrayList<Cuisiner>();
        for (int i = 0; i < employess.size(); i++) if (this.stockSuffisantCommande((Cuisiner)(employess.get(i)))) cuisiners.add((Cuisiner) employess.get(i));//Actif ou non;
        if (cuisiners.size() != 0) {
            Cuisiner cuisiner = cuisiners.get(0);
            for (Cuisiner c:cuisiners) {
                if (tempsRestantCuisiner(c) < tempsRestantCuisiner(cuisiner))
                    cuisiner = c;
            }
            return cuisiner;
        }
        return null;
    }

    public boolean affecterCommande() throws Exception{
        if (this.getEtat().equals(Etat.T)) {
            Cuisiner cuisiner = CommandeUnitaireManager.premierCuisinerLibre();
            if (cuisiner == null) return false;
            if (!this.stockSuffisantCommande(cuisiner)) cuisiner=cuisinerStockSuffisant();
            if (cuisiner == null)
                return false;
            this.setIdCuisiner(cuisiner.getIdCuisiner());
            //TODO handling all cook are offline exception to re-affect non affected commands
            MiniStockManager.update(this);

            return CommandeUnitaireManager.objectToRow(this);
        }
        return true;
    }


    public long tempsRestant(){ //SECONDES
        if (this.getEtat().equals(Etat.F)) return 0;
        Cuisiner cuisiner=CommandeUnitaireManager.getCuisiner(this.getIdCuisiner());
        long t=0;
        for (int i=0;i<cuisiner.commandeCourante.size();i++) {
            if(CommandeUnitaireManager.tempsCommandeUnitaire(cuisiner.commandeCourante.get(i)) - ChronoUnit.SECONDS.between(cuisiner.commandeCourante.get(i).getTempsDebut(), LocalDateTime.now()) > t)
            t = CommandeUnitaireManager.tempsCommandeUnitaire(cuisiner.commandeCourante.get(i)) - ChronoUnit.SECONDS.between(cuisiner.commandeCourante.get(i).getTempsDebut(), LocalDateTime.now()) > t ? CommandeUnitaireManager.tempsCommandeUnitaire(cuisiner.commandeCourante.get(i)) - ChronoUnit.SECONDS.between(cuisiner.commandeCourante.get(i).getTempsDebut(), LocalDateTime.now()): t;
        }
        if (t<0) t=0;
        if (cuisiner.commandeCourante.contains(this)) return t;
        for (int i=0;i<cuisiner.commandeEnAttente.size();i++){
            t+=cuisiner.commandeEnAttente.get(i).tempsRestant();
            if (cuisiner.commandeEnAttente.get(i).equals(this)) break;
        }
        return t;
    }

    public boolean reaffecterCommande() throws Exception{//en cas de problemes chez le cuisiner;
        if (!this.getEtat().equals(Etat.F)) {
            if (!CommandeUnitaireManager.deleteObjectFromTable(this)) return false;
            this.setEtat(Etat.T);
            this.affecterCommande();
        }
        return true;
    }



    public void valider(){
        try(
                Statement stmt= conn.createStatement();
        ){

            stmt.executeUpdate("UPDATE commandeUnitaire SET etat='"+this.etat.F+"' WHERE idCommande="+idCommande+" AND idPlat="+this.plat.getIdPlat()+" ;");

            etat = Etat.F;

        }catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void prepare(){
        try(
                Statement stmt= conn.createStatement();
        ){
            stmt.executeUpdate("UPDATE commandeUnitaire SET etat='"+this.etat.E+"' WHERE idCommande="+idCommande+" AND idPlat="+this.plat.getIdPlat()+" ;");
            etat = Etat.E;

        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean canBePrepared(ArrayList<Cuisiner> cuisiners,ArrayList<MiniStock> miniStocks){
        for(int i=0;i<cuisiners.size();i++){
            Stock stock=StockManager.getStock(cuisiners.get(i).getIdMagasin());
            for (IngredientAndQuantite iq :
                    this.getPlat().ingredientAndQuantites) {
                boolean peut=true;
                for (IngredientAndQuantite iqAtMini :
                        miniStocks.get(i).ingredientAndQuantites) {
                    int quantite=0;
                    if (iq.getIdIngredient() == iqAtMini.getIdIngredient()) {
                        for(int j=0;j<stock.ingredientPropreties.size();j++) {
                            if (stock.ingredientPropreties.get(j).getIdIngredient()==iqAtMini.getIdIngredient()) quantite=stock.ingredientPropreties.get(j).getQuantitie();
                        }
                        if ((quantite+iqAtMini.getQuantite())<(iq.getQuantite()*this.getQuantite())) peut=false;
                    }
                }
                if(peut) {
                    for (IngredientAndQuantite iqAtMini :
                            miniStocks.get(i).ingredientAndQuantites) {
                        if (iq.getIdIngredient() == iqAtMini.getIdIngredient()) {
                            iqAtMini.setQuantite(iqAtMini.getQuantite() - iq.getQuantite() * this.getQuantite());
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }




    @Override
    public boolean equals(Object o){
        if (!(o instanceof CommandeUnitaire)) return false;
        CommandeUnitaire commandeUnitaire=(CommandeUnitaire) o;
        return ((this.getIdCommande()==commandeUnitaire.getIdCommande())&&(this.getPlat().getIdPlat()==commandeUnitaire.getPlat().getIdPlat()));
    }

    @Override
    public int compareTo(CommandeUnitaire commandeUnitaire){
        Commande commande1= CommandeManager.getCommande(this.getIdCommande());
        Commande commande2= CommandeManager.getCommande(commandeUnitaire.getIdCommande());
        if (commande1.getDateCommande().isAfter(commande2.getDateCommande())) return -1;
        else return 1;
    }

}







