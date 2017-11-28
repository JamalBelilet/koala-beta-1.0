package koala.gestionCommandes.commandeUnitaire;

import koala.db.ConnectionManager;
import koala.gestionCommandes.Etat;
import koala.gestionEmployes.Cuisiner;
import koala.gestionEmployes.Employe;
import koala.gestionEmployes.EmployeManager;
import koala.gestionPlats.plat.PlatManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class CommandeUnitaireManager {

    public static Connection connection = ConnectionManager.getInstance().getConnection();


    public static ArrayList<CommandeUnitaire> TableToList() throws Exception {
        ArrayList<CommandeUnitaire> commandeUnitaire = new ArrayList<CommandeUnitaire>();

        try (
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM commandeunitaire");
        ) {
            while (rs.next()) {
                commandeUnitaire.add(CommandeUnitaireManager.CurrentRowToObject(rs));
            }
        }
        return commandeUnitaire;
    }

    public static CommandeUnitaire CurrentRowToObject(ResultSet rs) throws Exception {
        CommandeUnitaire commandeUnitaire = new CommandeUnitaire();
        commandeUnitaire.setQuantite(rs.getInt("commandeunitaire.quantite"));
        if (rs.getTimestamp("CommandeUnitaire.tempsDebut")!=null)commandeUnitaire.setTempsDebut(rs.getTimestamp("CommandeUnitaire.tempsDebut").toLocalDateTime());
        else commandeUnitaire.setTempsDebut(null);
        if (rs.getString("commandeunitaire.etat").equals("A")) {

            commandeUnitaire.setEtat( Etat.A );

        } else if (rs.getString("commandeunitaire.etat").equals("T")) {

            commandeUnitaire.setEtat( Etat.T );

        } else if (rs.getString("commandeunitaire.etat").equals("E")) {

            commandeUnitaire.setEtat( Etat.E );

        } else {
            commandeUnitaire.setEtat( Etat.F );

        }
        commandeUnitaire.setIdCommande(rs.getInt("commandeunitaire.idCommande"));
        commandeUnitaire.setPlat(PlatManager.getPlat(rs.getInt("commandeunitaire.idPlat")));
        commandeUnitaire.setIdCuisiner(rs.getInt("commandeunitaire.idCuisiner"));
        return commandeUnitaire;
    }

    public static boolean objectToRow(CommandeUnitaire commandeUnitaire) {
        if (commandeUnitaire != null) {
            try (
                    Statement stmt = connection.createStatement();

//                            + Timestamp.valueOf(commandeUnitaire.getTempsDebut()) + " )")
                    ////??????????????????????????????????????????????????????????????????????????????????????????????????? LocalDateTime --> dateTemps?;
            ) {
                stmt.executeUpdate("INSERT INTO commandeunitaire (idCommande, idPlat, idCuisiner, quantite, etat) VALUES (" + commandeUnitaire.getIdCommande() + ", "
                        + commandeUnitaire.getPlat().getIdPlat() + ", "
                        + commandeUnitaire.getIdCuisiner() + ", "
                        + commandeUnitaire.getQuantite() + ", '"
                        + commandeUnitaire.getEtat() + "' )");
//                        + "CAST( " + Timestamp.valueOf(commandeUnitaire.getTempsDebut())+ " AS DATETIME)" + " )");

            } catch (SQLException e) {
                try (
                        Statement stmt = connection.createStatement();
                ){
                    stmt.executeUpdate("UPDATE commandeunitaire SET idCuisiner = " + commandeUnitaire.getIdCuisiner() + ", quantite = " +commandeUnitaire.getQuantite() + ", etat = '" + commandeUnitaire.getEtat()   + "' WHERE idCommande = " + commandeUnitaire.getIdCommande() + " AND  idPlat =  "+ commandeUnitaire.getPlat().getIdPlat());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    return false;
                }



            }
            return true;
        }
        return true;
    }

    public static boolean deleteObjectFromTable(CommandeUnitaire commandeUnitaire) {
        try (Statement stmt = connection.createStatement();

        ) {
            stmt.executeUpdate("DELETE FROM commandeunitaire WHERE idCommande=" + commandeUnitaire.getIdCommande() + " AND idPlat=" + commandeUnitaire.getPlat().getIdPlat() + " AND idCuisiner=" + commandeUnitaire.getIdCuisiner());

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static Cuisiner getCuisiner(int idCuisiner) {
        Cuisiner cuisiner = null;
        try (
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("select * from cuisiner WHERE idCuisiner=" + idCuisiner)
        ) {
            if (rs.next()) {
                cuisiner = (Cuisiner) EmployeManager.loadCurrentRowToObject(rs, "cuisiner");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cuisiner;
    }

    public static Cuisiner premierCuisinerLibre() throws Exception {
        List<Employe> employess = Cuisiner.loadTableToList();
        ArrayList<Cuisiner> cuisiners = new ArrayList<Cuisiner>();
        for (int i = 0; i < employess.size(); i++) cuisiners.add((Cuisiner) employess.get(i));//Actif ou non;
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

    public static long tempsRestantCuisiner(Cuisiner cuisiner) {
        long t = 0;
        try {


            try(
                    Statement statement=connection.createStatement();
                    ResultSet rs=statement.executeQuery("SELECT * FROM commandeUnitaire WHERE idCuisiner="+cuisiner.getIdCuisiner())
            ) {
                while(rs.next()){
                    if(rs.getString("commandeunitaire.etat").equals("A")){
                        try {
                            cuisiner.commandeEnAttente.add(CurrentRowToObject(rs));
                        } catch (Exception e) {}
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }


            for (int i = 0; i < cuisiner.commandeEnAttente.size(); i++) {
                t += tempsCommandeUnitaire(cuisiner.commandeEnAttente.get(i));
            }
            long tempsCommandeCourrante=0;
            for(CommandeUnitaire commandeUnitaire:cuisiner.commandeCourante) {
                if (tempsCommandeUnitaire(commandeUnitaire)<(tempsCommandeUnitaire(commandeUnitaire) - ChronoUnit.SECONDS.between(commandeUnitaire.getTempsDebut(), LocalDateTime.now())) ) tempsCommandeCourrante=tempsCommandeUnitaire(commandeUnitaire) - ChronoUnit.SECONDS.between(commandeUnitaire.getTempsDebut(), LocalDateTime.now());
            }
            if (tempsCommandeCourrante > 0) t+= tempsCommandeCourrante;
        } catch (NullPointerException ex) {
            System.out.println("null from 131 commandeunitairemanager");
        }
        return t;
    }

    public static long tempsCommandeUnitaire(CommandeUnitaire commandeUnitaire) {
        try {
            if (commandeUnitaire != null) {
                if (!commandeUnitaire.getEtat().equals(Etat.F)) {
                    int t;
                    if ((commandeUnitaire.getQuantite() % commandeUnitaire.getPlat().getParallele()) == 0) {
                        t = commandeUnitaire.getPlat().getTempsPreparation() * (commandeUnitaire.getQuantite() / commandeUnitaire.getPlat().getParallele());
                        return (long) t;
                    } else {
                        t = commandeUnitaire.getPlat().getTempsPreparation() * (commandeUnitaire.getQuantite() / commandeUnitaire.getPlat().getParallele() + 1);
                        return (long) t;
                    }
                }
            }
        } catch (ArithmeticException e) {
            System.out.println("ArithmeticException");
        }
        return 0;
    }

    public static CommandeUnitaire commander(String platName, int quantite) throws Exception {
        CommandeUnitaire commandeUnitaire = new CommandeUnitaire(-1, PlatManager.getPlat(platName),-1, quantite, null, Etat.T);
        return commandeUnitaire;
    }
}
