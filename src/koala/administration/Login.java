package koala.administration;


import koala.db.ConnectionManager;

import java.sql.*;
public class Login {
    public static Connection connection = ConnectionManager.getInstance().getConnection();


    private static int connect(String login, String passwd) {
        new Object().hashCode();
        int b=-1;
        try {
            String sql = "SELECT * FROM compte WHERE login ='" + login + "'";
            ResultSet resultSet = connection.createStatement().executeQuery(sql);
//            SELECT * FROM compte WHERE login ='login'
//            hello '' ; drop database koala; SELECT * FROM compte WHERE login =
            if (resultSet.next()) {

                System.out.println();
                System.out.println();
                if (new MD5(passwd).toString().equals(resultSet.getString("password"))) {
                    System.err.println("mot d pass correct");
                    return resultSet.getInt("idCompte");
                }
                return -1;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }
        return b;
    }
    private static int rech(String nomtab, int id ) throws SQLException {
        String sql = "SELECT * FROM "+ nomtab +" WHERE  idCompte=  "+ id;
        ResultSet resultSet = connection.createStatement().executeQuery(sql);
        if (resultSet.next()) {
            return resultSet.getInt("id"+nomtab);
        }
        return -1;

    }

    public static String[] getConnection(String login, String pass ) throws SQLException {
        String[] tab = {"-1", null};
        int id = connect(login, pass);
        if (id > 0){
            tab[0] = Integer.toString(id);
            if (rech("cuisiner", id) >0){
                tab[1]="cuisiner";
                tab[0]=Integer.toString(rech("cuisiner", id));
            } else if (rech("caissier", id) >0) {
                tab[1] = "caissier";
                tab[0]=Integer.toString(rech("caissier", id));

            } else if (rech("magasinier", id) >0) {
                tab[1] = "magasinier";
                tab[0]=Integer.toString(rech("magasinier", id));

            } else if (rech("admin", id) >0) {
                tab[1] = "admin";
                tab[0]=Integer.toString(rech("admin", id));

            }
        }
        return tab;

    }
}
