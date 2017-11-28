package koala.gestionCommandes.commande;

import koala.db.ConnectionManager;
import koala.gestionCommandes.Etat;
import koala.gestionCommandes.ModePaiement;
import koala.gestionCommandes.commandeUnitaire.CommandeUnitaire;
import koala.gestionCommandes.commandeUnitaire.CommandeUnitaireManager;
import koala.gestionEmployes.Cuisiner;
import koala.gestionEmployes.Employe;
import koala.gestionStock.mini.MiniStock;
import koala.gestionStock.mini.MiniStockManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommandeManager {

    public static Connection connection = ConnectionManager.getInstance().getConnection();


    public static Commande CurrentRowToObject(ResultSet rs) throws Exception{
        Commande commande=new Commande(rs.getInt("Commande.idCommande"),rs.getTimestamp("Commande.dateCommande").toLocalDateTime(),rs.getInt("Commande.idClient"),rs.getBoolean("Commande.payer"),false);
//            commande=new Commande(rs.getInt("Commande.idCommande"),rs.getTimestamp("Commande.dateCommande")!=null ? rs.getTimestamp("Commande.dateCommande").toLocalDateTime():null,rs.getInt("Commande.idClient"),rs.getBoolean("Commande.payer"),rs.getBoolean("Commande.prete"));

        ArrayList<CommandeUnitaire> commandeUnitaires=new ArrayList<CommandeUnitaire>();

         try (
                Statement stmt = connection.createStatement();
                ResultSet rs1 = stmt.executeQuery("select * from commandeUnitaire WHERE idCommande="+commande.getIdCommande())) {

                while(rs1.next()){
                    CommandeUnitaire commandeUnitaire=CommandeUnitaireManager.CurrentRowToObject(rs1);
                    commandeUnitaires.add(commandeUnitaire);
                }

         }catch(SQLException e){
             e.printStackTrace();
         }
        commande.commandeUnitaires=commandeUnitaires;

        return commande;
    }
    public static boolean objectToRow(Commande commande){
        if (commande!=null){


            try (
                    Statement stmt = connection.createStatement();
                    ////??????????????????????????????????????????????????????????????????????????????????????????????????? LocalDateTime --> dateTemps?;
            ) {
                stmt.executeUpdate("INSERT INTO Commande (idCommande, dateCommande, modePaiement, idClient, payer) VALUES ("+commande.getIdCommande()+", "+

                        "CAST('" + Timestamp.valueOf(commande.getDateCommande()) +"'AS DATETIME)"
                        + ", '"+commande.getModePaiement()+"', "+commande.getIdClient()+", "+commande.getPayer()+" )");

                for (int i=0;i<commande.commandeUnitaires.size();i++){
                    if(!commande.commandeUnitaires.get(i).affecterCommande()) {
                        try {
                            stmt.executeUpdate("DELETE FROM Commande WHERE idCommande=" + commande.getIdCommande());
                        } catch (SQLException ex ){
                            ex.printStackTrace();
                        }
                        for (int j=0;j<=i;j++){
                            CommandeUnitaireManager.deleteObjectFromTable(commande.commandeUnitaires.get(j));
                        }
                        return false;
                    } else {
                        if (!commande.commandeUnitaires.get(i).miniStockSuffisantCommande()) {
                            commande.commandeUnitaires.get(i).setEtat(Etat.A);
                            CommandeUnitaireManager.objectToRow(commande.commandeUnitaires.get(i));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return true;
    }
    public static boolean deleteObjectFromTable(Commande commande){
        for(int i=0;i<commande.commandeUnitaires.size();i++){
            if (!CommandeUnitaireManager.deleteObjectFromTable(commande.commandeUnitaires.get(i))) return false;
        }
        try(
                Statement stmt=connection.createStatement();
                ResultSet rs=stmt.executeQuery("DALETE FROM commande WHERE idCommande="+commande.getIdCommande())
        ) {
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static ArrayList<Commande> TableToList() throws Exception{
        ArrayList<Commande> commandes=new ArrayList<Commande>();

        try (
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("select * from commande")
        ) {
            while(rs.next()){
                commandes.add(CommandeManager.CurrentRowToObject(rs));
            }
        }
        return commandes;
    }
    public static ArrayList<Commande> TableToListReverse() throws Exception{
        ArrayList<Commande> commandes=new ArrayList<Commande>();

        try (
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("select * from commande ORDER BY idCommande DESC; ")
        ) {
            while(rs.next()){
                commandes.add(CommandeManager.CurrentRowToObject(rs));
            }
        }
        return commandes;
    }
    public static Commande getCommande(int idCommande){

        Commande commande=null;
        try (
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("select * from commande WHERE idCommande="+ idCommande)
        ) {
            if (rs.next()){
                commande=CommandeManager.CurrentRowToObject(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return commande;
    }

    public static Commande Commander(ArrayList<CommandeUnitaire> commandeUnitaires, int idClient, boolean payee, ModePaiement modePaiment) throws Exception{
        Commande.setMaxId();
        Commande commande=new Commande(Commande.maxId, LocalDateTime.now(), idClient, payee, false, commandeUnitaires, modePaiment);
//        System.err.println(commande.getIdCommande());
        for(int i=0;i<commande.commandeUnitaires.size();i++){
            commande.commandeUnitaires.get(i).setIdCommande(commande.getIdCommande());
        }
        if (CommandeManager.objectToRow(commande)) return commande;
        return null;
    }


    public static boolean stockSuffisant(ArrayList<CommandeUnitaire> commandeUnitaires) throws Exception{
        List<Employe> employess = Cuisiner.loadTableToList();
        ArrayList<Cuisiner> cuisiners = new ArrayList<Cuisiner>();
        for (int i = 0; i < employess.size(); i++)  cuisiners.add((Cuisiner) employess.get(i));//Actif ou non;
        ArrayList<MiniStock> miniStocks=new ArrayList<MiniStock>();
        for (Cuisiner c:
                cuisiners) {
            miniStocks.add(MiniStockManager.getMiniStock(c.getIdCuisiner()));
        }
        for (CommandeUnitaire c:
             commandeUnitaires) {
            if (!c.canBePrepared(cuisiners,miniStocks)) return false;
        }
        return true;
    }

}
