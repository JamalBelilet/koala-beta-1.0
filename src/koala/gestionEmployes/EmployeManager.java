package koala.gestionEmployes;

import koala.db.ConnectionManager;
import koala.gestionCommandes.Etat;
import koala.gestionCommandes.commandeUnitaire.CommandeUnitaire;
import koala.gestionCommandes.commandeUnitaire.CommandeUnitaireManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class EmployeManager {

    public static Connection connection = ConnectionManager.getInstance().getConnection();

    public static List<Employe> loadTableToList() throws SQLException {
        List<Employe> list = new ArrayList<>();
        list = EmployeManager.loadTableToList("admin");
        list.addAll(EmployeManager.loadTableToList("caissier"));
        list.addAll(EmployeManager.loadTableToList("cuisiner"));
        list.addAll(EmployeManager.loadTableToList("magasinier"));

        return list;

    }

    protected static List<Employe> loadTableToList(String s) throws SQLException {
        List<Employe> list = new ArrayList<>();

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM " + s);

        while (rs.next()) {
            list.add(loadCurrentRowToObject(rs, s));
        }

        return list;
    }

    public static Employe loadCurrentRowToObject(ResultSet rs, String s) throws SQLException {
        Employe emp = null;

        switch (s) {
            case "admin":
                emp = new Admin();
                ((Admin) emp).setIdAdmin(rs.getInt("idAdmin"));
                break;
            case "caissier":
                emp = new Caissier();
                ((Caissier) emp).setIdCaissier(rs.getInt("idCaissier"));
                break;
            case "cuisiner":
                emp = new Cuisiner();
                try{
                    ((Cuisiner) emp).setIdCuisiner(rs.getInt("idCuisiner"));
                    ((Cuisiner) emp).commandeEnAttente=new ArrayList<CommandeUnitaire>();//MEKKI
                    ((Cuisiner) emp).commandeCourante=new ArrayList<CommandeUnitaire>();//MEKKI
                } catch (Exception e){
                    System.err.println("id mmj");
                }
                /////Mekki
                try (
                        Statement statement = connection.createStatement();
                        ResultSet rs1 = statement.executeQuery("SELECT * FROM commandeunitaire WHERE idCuisiner="+ ((Cuisiner) emp).getIdCuisiner());

                ){
//                    System.out.println(rs.getInt("idCuisiner") + "-------------------------------96941561984165168995164598");
                    while(rs1.next()){
                        CommandeUnitaire commandeUnitaire=CommandeUnitaireManager.CurrentRowToObject(rs1);

                        if (commandeUnitaire!=null) {
//                            System.err.println(commandeUnitaire.getEtat() + "-------------------");
                            if (commandeUnitaire.getEtat() == Etat.T || commandeUnitaire.getEtat() == Etat.A) {

                                ((Cuisiner) emp).commandeEnAttente.add(commandeUnitaire);
                            }
                            if (commandeUnitaire.getEtat().equals(Etat.E)) {

                                ((Cuisiner) emp).commandeCourante.add(commandeUnitaire);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /////Mekki
                break;
             case "magasinier":
               emp = new Magasinier();
             ((Magasinier) emp).setIdMagasinier(rs.getInt("idMagasinier"));
            break;
        }

        if (emp == null)
            return null;

        emp.setNom(rs.getString("nom"));
        emp.setPrenom(rs.getString("prenom"));
        emp.setDateNaissance(rs.getDate("dateNaissance"));
        emp.setDateEmbauche(rs.getDate("dateEmbauche"));
        emp.setSalaire(rs.getDouble("Salaire"));
        emp.setSalaireVerse(rs.getBoolean("salaireVerse"));
        emp.setSexe(rs.getString("sexe"));
        emp.setImage(rs.getBlob("image"));
        emp.setEmail(rs.getString("email"));
        emp.setAdresse(rs.getString("adresse"));
        emp.setTelephone(rs.getString("telephone"));
        emp.setDerniereEvaluation(rs.getDouble("derniereEvaluation"));
        emp.setSalaireAnnuel(rs.getDouble("salaireAnnuel"));
        emp.setNumeroSecuriteSociale(rs.getString("numeroSecuriteSociale"));
        emp.setIdMagasin(rs.getInt("idMagasin"));
        emp.setIdCompte(rs.getInt("idCompte"));

        return emp;
    }
    public static Employe getEmloye(int idEmploye, String type) throws SQLException {
        ResultSet res = connection.createStatement().executeQuery("SELECT * FROM " + type+" WHERE id"+ type +"= " + idEmploye);
        res.next();
        return loadCurrentRowToObject(res, type);
    }

    public static boolean insert(Employe employe) throws SQLException {
        String table = "";

        if (employe instanceof Admin)
            table = "admin";
        else if (employe instanceof Caissier)
            table = "caissier";
        else if (employe instanceof Cuisiner)
            table = "cuisiner";
        else if (employe instanceof Magasinier)
            table = "magasinier";
        else
            return false;

        String sql = "INSERT INTO " + table +
                "(`idCompte`, `idMagasin`, `nom`, `prenom`, `sexe`, `dateNaissance`, " +
                "`email`, `telephone`, `adresse`, `numeroSecuriteSociale`, `dateEmbauche`, `image`, " +
                "`derniereEvaluation`, `salaire`, `salaireAnnuel`, `salaireVerse`) " +
                "VALUES " +
                "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        ResultSet keys = null;
        try (
                PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ){
            stmt.setInt(1, employe.getIdCompte());
            stmt.setInt(2, employe.getIdMagasin());
            stmt.setString(3, employe.getNom());
            stmt.setString(4, employe.getPrenom());
            stmt.setString(5, employe.getSexe());
            stmt.setDate(6, employe.getDateNaissance());
            stmt.setString(7, employe.getEmail());
            stmt.setString(8, employe.getTelephone());
            stmt.setString(9, employe.getAdresse());
            stmt.setString(10, employe.getNumeroSecuriteSociale());
            stmt.setDate(11, employe.getDateEmbauche());
            stmt.setBlob(12, employe.getImage());
            stmt.setDouble(13, employe.getDerniereEvaluation());
            stmt.setDouble(14, employe.getSalaire());
            stmt.setDouble(15, employe.getSalaireAnnuel());
            stmt.setBoolean(16, employe.isSalaireVerse());

//            System.err.println(stmt.toString());
            int affected = stmt.executeUpdate();

            if (affected == 1) {
                keys = stmt.getGeneratedKeys();
                keys.next();
                int newKey = keys.getInt(1);

                if (employe instanceof Admin)
                    ((Admin) employe).setIdAdmin(newKey);
                else if (employe instanceof Caissier)
                    ((Caissier) employe).setIdCaissier(newKey);
                else if (employe instanceof Cuisiner)
                    ((Cuisiner) employe).setIdCuisiner(995);
                else if (employe instanceof Magasinier)
                    ((Magasinier) employe).setIdMagasinier(newKey);
                else
                    return false;

            } else {
                System.err.println("No rows affected");
                return false;
            }
        } catch (SQLException e) {
            System.err.println(e);
            return false;
        } finally{
            if (keys != null) keys.close();
        }
        return true;
    }

    public static boolean update(Employe employe) throws Exception {
        String table = "";

        if (employe instanceof Admin)
            table = "admin";
        else if (employe instanceof Caissier)
            table = "caissier";
        else if (employe instanceof Cuisiner)
            table = "cuisiner";
        else if (employe instanceof Magasinier)
            table = "magasinier";
        else
            return false;

        String sql =
            "UPDATE " + table + " SET " +
                    "`idCompte` = ?,`idMagasin` = ?,`nom` = ?,`prenom` = ?,`sexe` = ?,`dateNaissance`= ?," +
                    "`email` = ?,`telephone` = ?,`adresse` = ?,`numeroSecuriteSociale` = ?," +
                    "`dateEmbauche` = ?,`image` = ?,`derniereEvaluation` = ?,`salaire` = ?," +
                    "`salaireAnnuel` = ?,`salaireVerse` = ? " +
                    "WHERE id" + table + " = ?";

        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ){
            stmt.setInt(1, employe.getIdCompte());
            stmt.setInt(2, employe.getIdMagasin());
            stmt.setString(3, employe.getNom());
            stmt.setString(4, employe.getPrenom());
            stmt.setString(5, employe.getSexe());
            stmt.setDate(6, employe.getDateNaissance());
            stmt.setString(7, employe.getEmail());
            stmt.setString(8, employe.getTelephone());
            stmt.setString(9, employe.getAdresse());
            stmt.setString(10, employe.getNumeroSecuriteSociale());
            stmt.setDate(11, employe.getDateEmbauche());
            stmt.setBlob(12, employe.getImage());
            stmt.setDouble(13, employe.getDerniereEvaluation());
            stmt.setDouble(14, employe.getSalaire());
            stmt.setDouble(15, employe.getSalaireAnnuel());
            stmt.setBoolean(16, employe.isSalaireVerse());

            if (employe instanceof Admin)
                stmt.setInt(17, ((Admin) employe).getIdAdmin());
            else if (employe instanceof Caissier)
                stmt.setInt(17, ((Caissier) employe).getIdCaissier());
            else if (employe instanceof Cuisiner)
                stmt.setInt(17, ((Cuisiner) employe).getIdCuisiner());
            else if (employe instanceof Magasinier)
                stmt.setInt(17, ((Magasinier) employe).getIdMagasinier());
            else
                return false;

            int affected = stmt.executeUpdate();
            if (affected == 1) {
                return true;
            } else {
                return false;
            }
        }
        catch(SQLException e) {
            System.err.println(e);
            return false;
        }
    }

    public static boolean delete(Employe employe) throws Exception {
        String table = "";

        if (employe instanceof Admin) {
            table = "admin";
        } else if (employe instanceof Caissier) {
            table = "caissier";
        } else if (employe instanceof Cuisiner) {
            table = "cuisiner";
            ArrayList<CommandeUnitaire> commandeUnitaires = CommandeUnitaireManager.TableToList();
            for (CommandeUnitaire cu : commandeUnitaires) {
                if ((cu.getIdCuisiner() == ((Cuisiner) employe).getIdCuisiner()) && !(cu.getEtat().equals(Etat.F))) {
                    System.err.println("le cuisinier a des commandes unitaires a preparer");
                    return false;
                }
            }
            for(CommandeUnitaire cu:commandeUnitaires)
                if(cu.getIdCuisiner()==((Cuisiner) employe).getIdCuisiner())
                    CommandeUnitaireManager.deleteObjectFromTable(cu);

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("DELETE FROM magasiniernotification WHERE idCuisiner = " + ((Cuisiner) employe).getIdCuisiner());
                statement.executeUpdate("DELETE FROM ministock WHERE idCuisiner = " + ((Cuisiner) employe).getIdCuisiner());
            }catch (Exception e){
                System.err.println(e);
            }
        } else if (employe instanceof Magasinier) {
            table = "magasinier";
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("DELETE FROM magasiniernotification WHERE idMagasinier = " + ((Magasinier) employe).getIdMagasinier());

            }catch (Exception e){
                System.err.println(e);
            }
        }
        else
            return false;

        String sql = "DELETE FROM " + table + " WHERE id" + table + " = ?";

        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
                Statement statement=connection.createStatement();
        ) {
            stmt.setInt(1, employe.getIdSpecial());

            int affected = stmt.executeUpdate();

            statement.executeUpdate("DELETE FROM platproposer WHERE idCompte = " + employe.getIdCompte());
            statement.executeUpdate("DELETE FROM compte WHERE idCompte != 0 AND idCompte = " + employe.getIdCompte());
            return (affected == 1);

        } catch (SQLException e) {
            System.err.println(e);
            return false;
        }
    }

    public static  ArrayList<Employe> employesNonCompte() throws SQLException {

        ArrayList<Employe> list=new ArrayList<>();
        for (Employe e:loadTableToList()){
            if (e.getIdCompte()==0) list.add(e);
        }
        if (list.size()==0) return null;
        return list;
    }

    public static void updateIdCompte(Employe e){
        String function;
        if (e instanceof Cuisiner) {
            function="cuisiner";
            try(Statement stmt=connection.createStatement();
            ) {
                stmt.executeUpdate("UPDATE "+function+" SET idCompte="+e.getIdCompte()+" WHERE id" + function + "="+((Cuisiner) e).getIdCuisiner());

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        if (e instanceof Admin) {
            function="admin";
            try(            Statement stmt=connection.createStatement();
            ) {
                stmt.executeUpdate("UPDATE "+function+" SET idCompte="+e.getIdCompte()+" WHERE id" + function + "="+((Admin) e).getIdAdmin() );
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        if (e instanceof Caissier) {
            function="caissier";
            try(            Statement stmt=connection.createStatement();
            ) {
                stmt.executeUpdate("UPDATE "+function+" SET idCompte="+e.getIdCompte()+" WHERE id" + function + "="+((Caissier) e).getIdCaissier() );

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        if (e instanceof Magasinier) {
            function="magasinier";
            try(            Statement stmt=connection.createStatement();
            ) {
                stmt.executeUpdate("UPDATE "+function+" SET idCompte="+e.getIdCompte()+" WHERE id" + function + "="+((Magasinier) e).getIdMagasinier() );
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

    }

}
