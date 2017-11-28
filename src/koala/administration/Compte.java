package koala.administration;

import koala.db.ConnectionManager;

import java.sql.*;

public class Compte {

    public static Connection connection = ConnectionManager.getInstance().getConnection();
    
    Statement statement = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    private static int idCompte;
    private String login;
    private String password;
    private String pin;
    private Boolean pinEnabled;

    public Compte(int idCompte, String login, String password, String pin, Boolean pinEnabled) {
        this.idCompte = idCompte;
        this.login = login;
        this.password = password;
        this.pin = pin;
        this.pinEnabled = pinEnabled;
    }
    public Compte(int idCompte, String login) {
        this.idCompte = idCompte;
        this.login = login;
    }
    public Compte(int idCompte, String login, String password) {
        this.idCompte = idCompte;
        this.password = password;
        this.login = login;
    }
    public Compte(int idCompte) {
        this.idCompte = idCompte;
    }
    public Boolean getPinEnabled() {
        return pinEnabled;
    }

    public static int getIdCompte() {
        return idCompte;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getPin() {
        return pin;
    }

    public static void setIdCompte(int idComptes) {
        idCompte = idComptes;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public void setPinEnabled(Boolean pinEnabled) {
        this.pinEnabled = pinEnabled;
    }
//
//    public int ajouter_compte() {
//        try {
//            statement = connection.createStatement();
////            connection.prepareStatement("insert into compte values(" + getIdCompte()+","+getLogin()+","+getPassword()+","+getPin()+","+getPinEnabled()+ ")").executeUpdate();
//            PreparedStatement stmt = connection.prepareStatement("insert into compte values("+ getIdCompte()+"," + "\""+ getLogin()+"\",\"" + getPassword()+" \",\""+ getPin()+"\","+ getPinEnabled()+")");
//            int affected = stmt.executeUpdate();
//
//            ResultSet keys;
//            int newKey = -1;
//            if (affected == 1) {
//                keys = stmt.getGeneratedKeys();
//                keys.next();
//                newKey = keys.getInt(1);
//            }
//            return newKey;
//
//            } catch (Exception ex) {
//            ex.printStackTrace();
//            return -1;
//        }
//    }
    public int ajouter_compte() {
        try {
            statement = connection.createStatement();
//            connection.prepareStatement("insert into compte values(" + getIdCompte()+","+getLogin()+","+getPassword()+","+getPin()+","+getPinEnabled()+ ")").executeUpdate();
            connection.prepareStatement("insert into compte (login, password, pin, pinEnabled) values("+ "\""+ getLogin()+"\",\"" + getPassword()+" \",\""+ getPin()+"\","+ getPinEnabled()+")").executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }

        Statement stmt = null;
        ResultSet rs=null;
        try {
            stmt=connection.createStatement();
            rs=stmt.executeQuery("SELECT * FROM compte WHERE login='"+login + "'");
            if (rs.next()) return rs.getInt("idCompte");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (stmt!=null) try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (rs!=null) try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return -1;

    }


    public void supp_compte(Compte c2) {
        try {
            statement = connection.createStatement();
            connection.prepareStatement("delete from compte where idCompte="+c2.getIdCompte()+"").executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static boolean loadObjectToRow(int initId, int idCompte, String nom, String login, String password, int pin, boolean pinEnabled) {

        try (
                Statement stmt = connection.createStatement()
        ){
            if (idCompte!=-1)
                stmt.executeUpdate("update compte set idCompte=" +idCompte+" WHERE idCompte = " + initId);
//            if (nom!=null)
//                stmt.executeUpdate("update compte set nom='" +nom+"' WHERE idIngredient = " + initId);
            if (login!=null)
                stmt.executeUpdate("update compte set login='" +login +"' WHERE idCompte = " + initId);
            if (password!=null)
                stmt.executeUpdate("update compte set password='" +password +"' WHERE idCompte = " + initId);
            if (pin!=-1)
                stmt.executeUpdate("update compte set pin=" +pin+" WHERE idCompte = " + initId);

            stmt.executeUpdate("update compte set pinEnabled=" +pinEnabled+" WHERE idCompte = " + initId);


            return true;
        } catch(Exception ex){
            ex.printStackTrace();
        }return false;
    }
}

