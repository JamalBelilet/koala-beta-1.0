package koala.gestionClient;

import koala.db.ConnectionManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by Bureau on 26/05/2017.
 */
public class ClientManager {
    public static Connection connection = ConnectionManager.getInstance().getConnection();

    public static ArrayList<Client> TableToList(){
            ArrayList<Client> clients=new ArrayList<>();

        try (
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("select * from client")
        ) {
            do {
                clients.add(CurrentRowToObject(rs));
            } while(rs.next());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }
    public static Client CurrentRowToObject(ResultSet rs) throws SQLException {
        Client client = new Client();

        rs.next();
        client.setIdClient(rs.getInt("idClient"));
        client.setNom(rs.getString("nom"));
        client.setEmail(rs.getString("email"));
        /*plat.setIdCategorie(rs.getInt("idCategorie"));
        plat.setNom(rs.getString("nom"));
        plat.setPrix(rs.getDouble("prix"));
        plat.setDateIntronisation(rs.getDate("dateIntronisation"));
        plat.setDescription(rs.getString("description"));
        plat.setImage(rs.getBlob("image"));
        plat.setTempsPreparation(rs.getInt("tempsPreparation"));
        plat.setDisponible(rs.getBoolean("disponible"));*/

        return client;
    }
}


