package koala.gestionCommandes.commande;

import koala.db.ConnectionManager;
import koala.gestionCommandes.Etat;
import koala.gestionCommandes.ModePaiement;
import koala.gestionCommandes.commandeUnitaire.CommandeUnitaire;
import koala.gestionPlats.plat.PlatManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Commande {

    public static Connection connection = ConnectionManager.getInstance().getConnection();


    static int maxId;

    public static void setMaxId() throws Exception {
        int id=0;
        try(
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT idCommande From commande")
        ) {
            while (rs.next()){
                if (rs.getInt("idCommande")>id) id=rs.getInt("idCommande");
            }
        }
        maxId=++id;
    }

    private int idCommande;
    private LocalDateTime dateCommande;
    private int idClient;
    private boolean payer;
    private boolean prete=false;
    private ModePaiement modePaiement;
    public ArrayList<CommandeUnitaire> commandeUnitaires=new ArrayList<CommandeUnitaire>();

    public void setIdCommande(int idComamnde){
        this.idCommande=idComamnde;
    }
    public int getIdCommande(){
        return this.idCommande;
    }
    public void setDateCommande(LocalDateTime dateCommande){
        this.dateCommande=dateCommande;
    }
    public LocalDateTime getDateCommande(){
        return this.dateCommande;
    }
    public void setIdClient(int idClient){
        this.idClient=idClient;
    }
    public int getIdClient(){
        return this.idClient;
    }
    public void setPayer(boolean payer){
        this.payer=payer;
    }
    public boolean getPayer(){
        return this.payer;
    }
    public void setPrete(boolean prete){
        this.prete=prete;
    }
    public boolean getPrete(){
        return this.prete;
    }
    public void setModePaiement(ModePaiement modePaiement){
        this.modePaiement=modePaiement;
    }
    public ModePaiement getModePaiement(){
        return this.modePaiement;
    }

    public Commande() {
        this.setPayer(false);
        this.setPrete(false);
        double time= System.currentTimeMillis();
        this.setDateCommande(LocalDateTime.now());
        this.setIdClient(1);
    }
    public Commande(int idCommande, LocalDateTime dateCommande, boolean payer){
        this.setIdCommande(idCommande);
        this.setDateCommande(dateCommande);
        this.setIdClient(1);
        this.setPayer(payer);
    }
    public Commande(int idCommande, LocalDateTime dateCommande, int idClient, boolean prete){
        this.setIdCommande(idCommande);
        this.setDateCommande(dateCommande);
        this.setIdClient(idClient);
        this.setPrete(prete);
    }
    public Commande(int idCommande, LocalDateTime dateCommande, int idClient, boolean payer, boolean prete ){
        this.setIdCommande(idCommande);
        this.setDateCommande(dateCommande);
        this.setIdClient(idClient);
        this.setPayer(payer);
        this.setPrete(prete);
    }
    public Commande(int idCommande, LocalDateTime dateCommande, int idClient, boolean payer, boolean prete, ArrayList<CommandeUnitaire> commandeUnitaires){
        this(idCommande,dateCommande,idClient,payer,prete);
        this.commandeUnitaires=commandeUnitaires;
    }
    public Commande(int idCommande, LocalDateTime dateCommande, int idClient, boolean payer, boolean prete, ArrayList<CommandeUnitaire> commandeUnitaires, ModePaiement modePaiement){
        this(idCommande, dateCommande, idClient, payer, prete, commandeUnitaires);
        this.setModePaiement(modePaiement);
    }

    public boolean estPrete(){
        this.prete=true;
        for (CommandeUnitaire commandeUnitaire:this.commandeUnitaires){
            if ((commandeUnitaire.getEtat().equals(Etat.E))||(commandeUnitaire.getEtat().equals(Etat.T) )) this.prete=false;

        }
        return prete;
    }

    public ArrayList<CommandeUnitaire> commandeUnitaire() throws Exception{
        ArrayList<CommandeUnitaire> commandeUnitaires=new ArrayList<CommandeUnitaire>();

        try (
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * From commandeUnitaire WHERE idCommande="+this.idCommande)
        ){
            while (rs.next()){
                CommandeUnitaire commandeUnitaire=new CommandeUnitaire();
                commandeUnitaire.setQuantite(rs.getInt("CommandeUnitaire.quantite"));
                commandeUnitaire.setTempsDebut(rs.getTimestamp("CommandeUnitaire.tempsDebut").toLocalDateTime());
                commandeUnitaire.setEtat(rs.getString("CommandeUnitaire.etat").toUpperCase().charAt(0) == 'T' ? rs.getString("CommandeUnitaire.etat").toUpperCase().charAt(0) == 'E' ? Etat.T : Etat.E : Etat.F);
                commandeUnitaire.setIdCommande(this.getIdCommande());
                commandeUnitaire.setPlat(PlatManager.getPlat(rs.getInt("CommandeUnitaire.idPlat")));
                commandeUnitaire.setIdCommande(rs.getInt("CommandeUnitaire.idCuisiner"));
                commandeUnitaires.add(commandeUnitaire);
            }
        }
        this.commandeUnitaires=commandeUnitaires;
        return commandeUnitaires;
    }

    public long tempsRestant(){
        long t=0;
        for (int i=0;i<this.commandeUnitaires.size();i++){
            if (this.commandeUnitaires.get(i).tempsRestant()>t) t=this.commandeUnitaires.get(i).tempsRestant();
        }
        return t;
    }

    public LocalDateTime tempsPreparation(){
        return LocalDateTime.now().plusSeconds(this.tempsRestant());
    }













//
//
//
//
//    public static ArrayList<ArrayList<CommandeUnitaire>> mStockSuffisant(ArrayList<CommandeUnitaire> commandeUnitaires) throws Exception{
//        System.out.println("helloooooooooooooooo");
//
//        ArrayList<ArrayList<CommandeUnitaire>> listCommandeUnitaires=stockSuffisant(commandeUnitaires);
//        listCommandeUnitaires = supprimerdoublon(listCommandeUnitaires);
//
//
//        if (listCommandeUnitaires.size() == 1 && listCommandeUnitaires.get(0) == null)
//            throw new Exception("cantCommande");
//
//        if (listCommandeUnitaires.get(0).size() == commandeUnitaires.size())
//            return null;
//        return listCommandeUnitaires;
//    }
//
//    public static ArrayList<ArrayList<CommandeUnitaire>> supprimerdoublon(ArrayList<ArrayList<CommandeUnitaire>> listCommandeUnitaires){
//        boolean egaux;
//        for(int i=0;i<listCommandeUnitaires.size();i++){
//            for(int j=i+1;j<listCommandeUnitaires.size();j++){
//                egaux=true;
//                for (CommandeUnitaire c:
//                     listCommandeUnitaires.get(j)) {
//                    if (!listCommandeUnitaires.get(i).contains(c)) egaux=false;
//                }
//                if(egaux || (listCommandeUnitaires.get(j)==null)) {
//                    listCommandeUnitaires.remove(j);
//                    j--;
//                    i--;
//                }
//            }
//        }
//        return listCommandeUnitaires;
//    }
//
//    public static ArrayList<ArrayList<CommandeUnitaire>> stockSuffisant(ArrayList<CommandeUnitaire> commandeUnitaires) throws Exception{
//        if (commandeUnitaires.size()==0) return null;
//        List<Employe> employess = Cuisiner.loadTableToList();
//        ArrayList<Cuisiner> cuisiners = new ArrayList<Cuisiner>();
//        for (int i = 0; i < employess.size(); i++)  cuisiners.add((Cuisiner) employess.get(i));//Actif ou non;
//        ArrayList<MiniStock> miniStocks=new ArrayList<MiniStock>();
//        for (Cuisiner c:
//                cuisiners) {
//            miniStocks.add(MiniStockManager.getMiniStock(c.getIdCuisiner()));
//        }
//        boolean canBeprepared=true;
//        for (CommandeUnitaire c:
//             commandeUnitaires) {
//            if (!c.canBePrepared(cuisiners,miniStocks)) {
//                canBeprepared=false;
//                break;
//            }
//        }
//        ArrayList<ArrayList<CommandeUnitaire>> listCommandeUnitaires=new ArrayList<ArrayList<CommandeUnitaire>>();
//        listCommandeUnitaires.add(commandeUnitaires);
//        if (canBeprepared) return listCommandeUnitaires;
//        listCommandeUnitaires.clear();
//        for (int i=0;i<commandeUnitaires.size();i++){
//            ArrayList<CommandeUnitaire> cu=new ArrayList<CommandeUnitaire>();
//            for (CommandeUnitaire c:
//                 commandeUnitaires) {
//
//                cu.add(c);
//            }
//            cu.remove(i);
//
//            if (stockSuffisant(cu) != null)
//                listCommandeUnitaires.addAll(stockSuffisant(cu));
//        }
//        return listCommandeUnitaires;
//
//    }




}
