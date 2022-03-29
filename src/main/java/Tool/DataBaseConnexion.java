package Tool;

import java.sql.*;

/**
 * class who able to connect to the database and execute queries
 */
public class DataBaseConnexion
{
    private Statement stmt; // statement of the database

    /**
     * constructor
     * @param url url of the database
     * @param user user of the database
     * @param password password of the user of the database
     */
    public DataBaseConnexion(String url, String user, String password)
    {
        try
        {
            try{
                Class cls = Class.forName("Driver");
            } catch(Exception e){
                Class cls = Class.forName("com.mysql.cj.jdbc.Driver");
            }
            if(stmt == null) {
                if (!connexionToDB(url, user, password))
                {
                    throw new SQLException();
                }
            }
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Impossible to find com.mysql.cj.jdbc.Driver\n");
            e.printStackTrace();
        }
        catch (SQLException e)
        {
            System.out.println("ERROR WITH DB CONNEXION");
            System.exit(0);
        }
    }

    /**
     * method that establish connection with the database
     * @return true if the connection is established, false otherwise
     */
    private boolean connexionToDB(String url, String user, String password)
    {
        try {
            Connection con = DriverManager.getConnection(url, user, password);
            stmt=con.createStatement();
            return true;
        }
        catch (SQLException e)
        {
            System.out.println("Impossible to connect with sql\n");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * method that execute a request and return the result
     * @param requestSQL the request to execute
     * @return the result of the request
     */
    public ResultSet sendRequest(String requestSQL)
    {
        try {
            ResultSet rs = stmt.executeQuery(requestSQL);
            return rs;
        }
        catch (SQLException e)
        {
            System.out.println("Impossible to connect with sql\n");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * method that execute a request that change data in the database
     * @param requestSQL the request to execute
     * @return true if the request is executed, false otherwise
     */
    public boolean sendModifyRequest(String requestSQL)
    {
        try {
            int rs = stmt.executeUpdate(requestSQL);
            if(rs == 1)
                return true;
            else
                return false;
        }
        catch (SQLException e)
        {
            System.out.println("Impossible to connect with sql\n");
            e.printStackTrace();
            return false;
        }
    }

}
