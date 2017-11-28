package koala.gestionClient;

import koala.db.ConnectionManager;
import koala.gestionCommandes.commande.Commande;
import koala.gestionCommandes.commande.CommandeManager;
import koala.gestionCommandes.commandeUnitaire.CommandeUnitaire;
import koala.gestionPlats.plat.Plat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Bureau on 26/05/2017.
 */
public class Client {

    public static Connection connection = ConnectionManager.getInstance().getConnection();


    private int idClient;
    private String nom;
    private String email;

    public Client() {
    }

    public Client(int idClient, String nom, String email) {

        this.idClient = idClient;
        this.nom = nom;
        this.email = email;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Commande derniereCommande(){

        ArrayList<Commande> commandes=new ArrayList<>();
        Commande commande=null;
        try (
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("select * from commande WHERE idClient="+ this.idClient)
        ) {
            while(rs.next()){
                commandes.add(CommandeManager.CurrentRowToObject(rs));
            }
            if (commandes.size()!=0){
                commande=commandes.get(0);
                for(Commande c: commandes){
                    if (c.getDateCommande().isAfter(commande.getDateCommande())) commande=c;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return commande;
    }


    public ArrayList<Commande> allCommandes(){
        ArrayList<Commande> commandes=new ArrayList<Commande>();

        try (
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("select * from commande")
        ) {
            while(rs.next()){
                commandes.add(CommandeManager.CurrentRowToObject(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return commandes;
    }

    public HashSet<Plat> platsCommande(Commande c){
        HashSet<Plat> plats=new HashSet<>();
        if (c==null) return null;
        for (CommandeUnitaire cu:c.commandeUnitaires){
            plats.add(cu.getPlat());
        }
        return plats;
    }

    public HashSet<Plat> allPlat(){
        HashSet<Plat> plats=new HashSet<>();
        for (Commande c:allCommandes()){
            plats.addAll(platsCommande(c));
        }
        return plats;
    }

    public HashSet<Plat> platsAProposer(){
        HashSet<Plat> dernierPlats=platsCommande(derniereCommande());
        HashSet<Plat> plats=dernierPlats;

        for (Client c:ClientManager.TableToList()){
            if (c.allPlat().containsAll(dernierPlats)) plats.addAll(c.allPlat());
        }
        return plats;
    }








}
