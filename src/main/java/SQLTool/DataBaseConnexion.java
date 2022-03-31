package SQLTool;

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
            System.exit(1);
        }
        catch (SQLException e)
        {
            System.out.println("ERROR WITH DB CONNEXION");
            System.exit(1);
        }
    }

    /**
     * method that establish connection with the database
     * @return true if the connection is established, false otherwise
     */
    private boolean connexionToDB(String url, String user, String password) throws SQLException {
        Connection con = DriverManager.getConnection(url, user, password);
        stmt=con.createStatement();
        return true;
    }

    /**
     * method that execute a request and return the result
     * @param requestSQL the request to execute
     * @return the result of the request
     */
    public ResultSet sendRequest(String requestSQL) throws SQLException {
        ResultSet rs = stmt.executeQuery(requestSQL);
        return rs;
    }

    /**
     * method that execute a request that change data in the database
     * @param requestSQL the request to execute
     * @return true if the request is executed, false otherwise
     */
    public int sendModifyRequest(String requestSQL) throws SQLException {
        return stmt.executeUpdate(requestSQL);
    }

}
