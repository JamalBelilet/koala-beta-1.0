package koala.gestionEmployes;

import koala.db.ConnectionManager;
import koala.gestionCommandes.commandeUnitaire.CommandeUnitaire;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



public class Cuisiner extends Employe {
    public static Connection connection = ConnectionManager.getInstance().getConnection();

    private int idCuisiner;
    public ArrayList<CommandeUnitaire> commandeEnAttente;

    public ArrayList<CommandeUnitaire> commandeCourante;




//    {
//        try (ResultSet resultSet = connection.createStatement().executeQuery("select * from commandeunitaire WHERE idCuisiner = 1 AND etat = 'T'")) {
//        while (resultSet.next()) {
//            commandeEnAttente.add(CommandeUnitaireManager.CurrentRowToObject(resultSet));
//            }
//        } catch (SQLException ex) {
//
//        } catch (Exception e) {
//            System.out.println("plaplapla");
//        }
//    }





    public static List<Employe> loadTableToList() throws SQLException {
        return EmployeManager.loadTableToList("cuisiner");
    }

    @Override
    public int getIdSpecial() {
        return idCuisiner;
    }

    @Override
    public void setIdSpecial(int idSpecial) {
        this.idCuisiner = idSpecial;
    }

    public int getIdCuisiner() {
        return idCuisiner;
    }

    public void setIdCuisiner(int idCuisiner) {
        this.idCuisiner = idCuisiner;
    }




//    public ArrayList<CommandeUnitaire>  Tcommande() throws SQLException {
//
//        ArrayList<CommandeUnitaire> CU =new  ArrayList<CommandeUnitaire>();
//        Statement stmt= connection.createStatement();
//        Statement stmtE= connection.createStatement();
//        ResultSet rs=stmt.executeQuery("SELECT * FROM commandeUnitaire Where idCuisiner="+idCuisiner+" and etat='T'");
//        ResultSet rsE=stmtE.executeQuery("SELECT * FROM commandeUnitaire Where idCuisiner="+idCuisiner+" and etat='E'");
//
//        while (rs.next()){
//
//            CommandeUnitaire c=new CommandeUnitaire(rs.getInt("idCommande"), rs.getInt("idPlat"), rs.getInt("idCuisiner"), rs.getInt("quantite"), Etat.T);
//            CU.add(c);
//
//        }
//        while (rsE.next()){
//
//            CommandeUnitaire c=new CommandeUnitaire(rsE.getInt("idCommande"), rsE.getInt("idPlat"), rsE.getInt("idCuisiner"), rsE.getInt("quantite"), Etat.E);
//            CU.add(c);
//
//        }
//        return CU;
//
//    }
}
