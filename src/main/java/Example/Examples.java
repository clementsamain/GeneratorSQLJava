package Example;

import SQLTool.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Examples {
    public static void main(String[] args) throws SQLException {
        //EXAMPLE_INSERT();
        //EXAMPLE_GET();
        //EXAMPLE_MODIFY();
        //EXAMPLE_CREATE_TABLE();
        //EXAMPLE_DROP_ELEM();
    }

    /**
     * function that read the DB.txt file and get the information for the connection with the DB
     * @return the information for the connection with the DB in the form of an array of 3 strings (URL, USER, PASSWORD)
     */
    private static String[] openDB() {
        try {
            FileReader fr = new FileReader(System.getProperty("user.dir") + "/DB.option");
            BufferedReader br = new BufferedReader(fr);
            String URL = br.readLine().substring("URL :".length()).replace(" ", "");
            String USER = br.readLine().substring("USER :".length()).replace(" ", "");
            String PASSWORD = br.readLine().substring("PWD :".length()).replace(" ", "");
            br.close();
            fr.close();
            return new String[]{URL, USER, PASSWORD};
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    /**
     * function that show an example of an insert request
     * @throws SQLException if there is an error with the SQL request
     */
    private static void EXAMPLE_INSERT() throws SQLException {
        // creation of the connexion
        String[] info = openDB();
        DataBaseConnexion db = new DataBaseConnexion(info[0], info[1], info[2]);
        // creation of the table
        List<ColumnTable> list = new ArrayList<>();
        list.add(new ColumnTable<String>("faceValue", TypeSQL.DOUBLE) {
            @Override
            public boolean verify(String value) {
                try{
                    Double.parseDouble(value);
                }
                catch (NumberFormatException e){
                    return false;
                }
                return true;
            }
        });
        list.add(new ColumnTable<String>("faceValueCurrency", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                return value.equals("EUR") || value.equals("USD") || value.equals("GBP");
            }
        });
        list.add(new ColumnTable<String>("typeOfBooking", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                return value.equals("Nominal") || value.equals("By Piece");
            }
        });
        list.add(new ColumnTable<String>("countryOfIssue", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                return value.equals("France") || value.equals("Espagne") || value.equals("Angleterre");
            }
        });
        list.add(new ColumnTable<String>("legalTypeOfSecurity", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                return value.equals("Equity") || value.equals("stock") || value.equals("Bound");
            }
        });
        list.add(new ColumnTable<String>("status", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                return value.equals("Current") || value.equals("Inactive") || value.equals("Matured");
            }
        });
        list.add(new ColumnTable<String>("cdcDepositDate", TypeSQL.DATE) {
            @Override
            public boolean verify(String value) {
                try{
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    format.parse ( value );
                }
                catch (Exception e){
                    return false;
                }
                return true;
            }
        });
        list.add(new ColumnTable<String>("revisionDate", TypeSQL.DATE) {
            @Override
            public boolean verify(String value) {
                try{
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    format.parse ( value );
                }
                catch (Exception e){
                    return false;
                }
                return true;
            }
        });
        list.add(new ColumnTable<String>("codeNature", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                return true;
            }
        });
        list.add(new ColumnTable<String>("securityID", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                try{
                    UUID.fromString(value);
                }
                catch (Exception e){
                    return false;
                }
                return true;
            }
        });
        list.add(new ColumnTable<String>("synonym", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                try{
                    if(value.indexOf("[") != 0)
                    {
                        return false;
                    }
                    if(value.lastIndexOf("]") != value.length()-1)
                    {
                        return false;
                    }
                    value = value.substring(1, value.length()-1);
                    String[] listOfElement = value.split(";");
                    for(String element : listOfElement){
                        String[] typeAndValue = element.split(":");
                        if(typeAndValue.length < 2){
                            return false;
                        }
                    }
                }
                catch (Exception e){
                    return false;
                }
                return true;
            }
        });
        DataBaseTable table = new DataBaseTable(list, "Security");

        // creation of the request
        List<ColumnValue> listOfColumnValue = new ArrayList<>();
        listOfColumnValue.add(new ColumnValue("securityID", UUID.randomUUID().toString()));
        listOfColumnValue.add(new ColumnValue("faceValue", "0.7"));
        listOfColumnValue.add(new ColumnValue("faceValueCurrency", "EUR"));
        listOfColumnValue.add(new ColumnValue("typeOfBooking", "Nominal"));
        listOfColumnValue.add(new ColumnValue("countryOfIssue", "France"));
        listOfColumnValue.add(new ColumnValue("legalTypeOfSecurity", "Equity"));
        listOfColumnValue.add(new ColumnValue("status", "Current"));
        listOfColumnValue.add(new ColumnValue("cdcDepositDate", "2018-01-01"));
        listOfColumnValue.add(new ColumnValue("revisionDate", "2018-01-01"));
        listOfColumnValue.add(new ColumnValue("codeNature", "1"));

        // launch the request
        int max = 1;
        boolean ifOk = false;
        do {
            String type = "[";
            for (int i = 0; i < max; i++) {
                type += "type" + i + ":" + i + ";";
            }
            type = type.substring(0, type.length() - 1);
            type += "]";
            if(listOfColumnValue.get(listOfColumnValue.size()-1).getColumnName().equals("synonym")){
                listOfColumnValue.get(listOfColumnValue.size()-1).setColumnValue(type);
            }
            else {
                listOfColumnValue.add(new ColumnValue("synonym", type));
            }
            try {
                ifOk = DataBaseGenerator.insertElement(db, table, listOfColumnValue);
            } catch (SQLException e) {
                if(e.getSQLState().equals("23000")){ // if duplicate entry error
                    max++;
                }
                else{
                    throw e;
                }
            }
        } while (!ifOk);

        // check if the request is ok
        System.out.println("add elements : " + true);
    }

    /**
     * function that show an example of a get request to the database
     * @throws SQLException if there is an error with the SQL request
     */
    private static void EXAMPLE_GET() throws SQLException {
        // creation of the connexion
        String[] info = openDB();
        DataBaseConnexion db = new DataBaseConnexion(info[0], info[1], info[2]);
        // creation of the table
        List<ColumnTable> list = new ArrayList<>();
        list.add(new ColumnTable<String>("faceValue", TypeSQL.DOUBLE) {
            @Override
            public boolean verify(String value) {
                try{
                    Double.parseDouble(value);
                }
                catch (NumberFormatException e){
                    return false;
                }
                return true;
            }
        });
        list.add(new ColumnTable<String>("faceValueCurrency", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                return value.equals("EUR") || value.equals("USD") || value.equals("GBP");
            }
        });
        list.add(new ColumnTable<String>("typeOfBooking", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                return value.equals("Nominal") || value.equals("By Piece");
            }
        });
        list.add(new ColumnTable<String>("countryOfIssue", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                return value.equals("France") || value.equals("Espagne") || value.equals("Angleterre");
            }
        });
        list.add(new ColumnTable<String>("legalTypeOfSecurity", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                return value.equals("Equity") || value.equals("stock") || value.equals("Bound");
            }
        });
        list.add(new ColumnTable<String>("status", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                return value.equals("Current") || value.equals("Inactive") || value.equals("Matured");
            }
        });
        list.add(new ColumnTable<String>("cdcDepositDate", TypeSQL.DATE) {
            @Override
            public boolean verify(String value) {
                try{
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    format.parse ( value );
                }
                catch (Exception e){
                    return false;
                }
                return true;
            }
        });
        list.add(new ColumnTable<String>("revisionDate", TypeSQL.DATE) {
            @Override
            public boolean verify(String value) {
                try{
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    format.parse ( value );
                }
                catch (Exception e){
                    return false;
                }
                return true;
            }
        });
        list.add(new ColumnTable<String>("codeNature", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                return true;
            }
        });
        list.add(new ColumnTable<String>("securityID", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                try{
                    UUID.fromString(value);
                }
                catch (Exception e){
                    return false;
                }
                return true;
            }
        });
        list.add(new ColumnTable<String>("synonym", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                try{
                    if(value.indexOf("[") != 0)
                    {
                        return false;
                    }
                    if(value.lastIndexOf("]") != value.length()-1)
                    {
                        return false;
                    }
                    value = value.substring(1, value.length()-1);
                    String[] listOfElement = value.split(";");
                    for(String element : listOfElement){
                        String[] typeAndValue = element.split(":");
                        if(typeAndValue.length != 2){
                            return false;
                        }
                    }
                }
                catch (Exception e){
                    return false;
                }
                return true;
            }
        });
        DataBaseTable table = new DataBaseTable(list, "Security");

        // creation of the request
        List<ColumnValue> listOfColumnValue = new ArrayList<>();
        listOfColumnValue.add(new ColumnValue("countryOfIssue", "France"));

        // launch the request
        ResultSet result = DataBaseGenerator.getElement(db, table, listOfColumnValue);

        // check if the request is ok
        System.out.println(DataBaseGenerator.parseSQLResult(result, table));
    }

    /**
     * function that show an example of a modify request
     * @throws SQLException if there is an error with the SQL request
     */
    private static void EXAMPLE_MODIFY() throws SQLException {
        // creation of the connexion
        String[] info = openDB();
        DataBaseConnexion db = new DataBaseConnexion(info[0], info[1], info[2]);
        // creation of the table
        List<ColumnTable> list = new ArrayList<>();
        list.add(new ColumnTable<String>("faceValue", TypeSQL.DOUBLE) {
            @Override
            public boolean verify(String value) {
                try{
                    Double.parseDouble(value);
                }
                catch (NumberFormatException e){
                    return false;
                }
                return true;
            }
        });
        list.add(new ColumnTable<String>("faceValueCurrency", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                return value.equals("EUR") || value.equals("USD") || value.equals("GBP");
            }
        });
        list.add(new ColumnTable<String>("typeOfBooking", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                return value.equals("Nominal") || value.equals("By Piece");
            }
        });
        list.add(new ColumnTable<String>("countryOfIssue", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                return value.equals("France") || value.equals("Espagne") || value.equals("Angleterre");
            }
        });
        list.add(new ColumnTable<String>("legalTypeOfSecurity", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                return value.equals("Equity") || value.equals("stock") || value.equals("Bound");
            }
        });
        list.add(new ColumnTable<String>("status", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                return value.equals("Current") || value.equals("Inactive") || value.equals("Matured");
            }
        });
        list.add(new ColumnTable<String>("cdcDepositDate", TypeSQL.DATE) {
            @Override
            public boolean verify(String value) {
                try{
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    format.parse ( value );
                }
                catch (Exception e){
                    return false;
                }
                return true;
            }
        });
        list.add(new ColumnTable<String>("revisionDate", TypeSQL.DATE) {
            @Override
            public boolean verify(String value) {
                try{
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    format.parse ( value );
                }
                catch (Exception e){
                    return false;
                }
                return true;
            }
        });
        list.add(new ColumnTable<String>("codeNature", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                return true;
            }
        });
        list.add(new ColumnTable<String>("securityID", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                try{
                    UUID.fromString(value);
                }
                catch (Exception e){
                    return false;
                }
                return true;
            }
        });
        list.add(new ColumnTable<String>("synonym", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                try{
                    if(value.indexOf("[") != 0)
                    {
                        return false;
                    }
                    if(value.lastIndexOf("]") != value.length()-1)
                    {
                        return false;
                    }
                    value = value.substring(1, value.length()-1);
                    String[] listOfElement = value.split(";");
                    for(String element : listOfElement){
                        String[] typeAndValue = element.split(":");
                        if(typeAndValue.length < 2){
                            return false;
                        }
                    }
                }
                catch (Exception e){
                    return false;
                }
                return true;
            }
        });
        DataBaseTable table = new DataBaseTable(list, "Security");

        // creation of elements that will modify in the table
        List<ColumnValue> listOfColumnValue = new ArrayList<>();
        listOfColumnValue.add(new ColumnValue("countryOfIssue", "France"));

        // creation of the element that will be added in the table
        List<ColumnValue> listOfColumnValue2 = new ArrayList<>();
        listOfColumnValue2.add(new ColumnValue("countryOfIssue", "Espagne"));

        // launch the request
        int nbr = DataBaseGenerator.modifyElement(db, table, listOfColumnValue, listOfColumnValue2);

        // check if the request is ok
        System.out.println("element(s) modified : " + nbr);
    }

    /**
     * function that show an example of a creation table request
     * @throws SQLException if there is an error with the SQL request
     */
    private static void EXAMPLE_CREATE_TABLE() throws SQLException {
        // creation of the connexion
        String[] info = openDB();
        DataBaseConnexion db = new DataBaseConnexion(info[0], info[1], info[2]);
        // creation of the table
        List<ColumnTable> list = new ArrayList<>();
        list.add(new ColumnTable<String>("faceValue", TypeSQL.DOUBLE) {
            @Override
            public boolean verify(String value) {
                try{
                    Double.parseDouble(value);
                }
                catch (NumberFormatException e){
                    return false;
                }
                return true;
            }
        });
        list.add(new ColumnTable<String>("faceValueCurrency", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                return value.equals("EUR") || value.equals("USD") || value.equals("GBP");
            }
        });
        list.add(new ColumnTable<String>("typeOfBooking", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                return value.equals("Nominal") || value.equals("By Piece");
            }
        });
        list.add(new ColumnTable<String>("countryOfIssue", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                return value.equals("France") || value.equals("Espagne") || value.equals("Angleterre");
            }
        });
        list.add(new ColumnTable<String>("legalTypeOfSecurity", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                return value.equals("Equity") || value.equals("stock") || value.equals("Bound");
            }
        });
        list.add(new ColumnTable<String>("status", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                return value.equals("Current") || value.equals("Inactive") || value.equals("Matured");
            }
        });
        list.add(new ColumnTable<String>("cdcDepositDate", TypeSQL.DATE) {
            @Override
            public boolean verify(String value) {
                try{
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    format.parse ( value );
                }
                catch (Exception e){
                    return false;
                }
                return true;
            }
        });
        list.add(new ColumnTable<String>("revisionDate", TypeSQL.DATE) {
            @Override
            public boolean verify(String value) {
                try{
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    format.parse ( value );
                }
                catch (Exception e){
                    return false;
                }
                return true;
            }
        });
        list.add(new ColumnTable<String>("codeNature", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                return true;
            }
        });
        list.add(new ColumnTable<String>("securityID", TypeSQL.VARCHAR) {
            @Override
            public boolean verify(String value) {
                try{
                    UUID.fromString(value);
                }
                catch (Exception e){
                    return false;
                }
                return true;
            }
        });
        list.add(new ColumnTable<String>("synonym", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                try{
                    if(value.indexOf("[") != 0)
                    {
                        return false;
                    }
                    if(value.lastIndexOf("]") != value.length()-1)
                    {
                        return false;
                    }
                    value = value.substring(1, value.length()-1);
                    String[] listOfElement = value.split(";");
                    for(String element : listOfElement){
                        String[] typeAndValue = element.split(":");
                        if(typeAndValue.length < 2){
                            return false;
                        }
                    }
                }
                catch (Exception e){
                    return false;
                }
                return true;
            }
        });
        DataBaseTable table = new DataBaseTable(list, "Security");

        //create key for table
        ArrayList<KeyColumn> listKey = new ArrayList<>();
        listKey.add(new KeyColumn(KeySQL.PRIMARY, 36,"securityID"));
        listKey.add(new KeyColumn(KeySQL.UNIQUE, null, "synonym"));

        //generate and execute the query
        boolean ifOk = DataBaseGenerator.createTableInDataBase(db, table, listKey);

        //check if the table has been created
        System.out.println("Table Security created : " + ifOk);
    }

    /**
     * function that show an example of a drop request
     * @throws SQLException if there is an error with the SQL request
     */
    private static void EXAMPLE_DROP_ELEM() throws SQLException {
        // creation of the connexion
        String[] info = openDB();
        DataBaseConnexion db = new DataBaseConnexion(info[0], info[1], info[2]);
        // creation of the table
        List<ColumnTable> list = new ArrayList<>();
        list.add(new ColumnTable<String>("faceValue", TypeSQL.DOUBLE) {
            @Override
            public boolean verify(String value) {
                try{
                    Double.parseDouble(value);
                }
                catch (NumberFormatException e){
                    return false;
                }
                return true;
            }
        });
        list.add(new ColumnTable<String>("faceValueCurrency", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                return value.equals("EUR") || value.equals("USD") || value.equals("GBP");
            }
        });
        list.add(new ColumnTable<String>("typeOfBooking", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                return value.equals("Nominal") || value.equals("By Piece");
            }
        });
        list.add(new ColumnTable<String>("countryOfIssue", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                return value.equals("France") || value.equals("Espagne") || value.equals("Angleterre");
            }
        });
        list.add(new ColumnTable<String>("legalTypeOfSecurity", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                return value.equals("Equity") || value.equals("stock") || value.equals("Bound");
            }
        });
        list.add(new ColumnTable<String>("status", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                return value.equals("Current") || value.equals("Inactive") || value.equals("Matured");
            }
        });
        list.add(new ColumnTable<String>("cdcDepositDate", TypeSQL.DATE) {
            @Override
            public boolean verify(String value) {
                try{
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    format.parse ( value );
                }
                catch (Exception e){
                    return false;
                }
                return true;
            }
        });
        list.add(new ColumnTable<String>("revisionDate", TypeSQL.DATE) {
            @Override
            public boolean verify(String value) {
                try{
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    format.parse ( value );
                }
                catch (Exception e){
                    return false;
                }
                return true;
            }
        });
        list.add(new ColumnTable<String>("codeNature", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                return true;
            }
        });
        list.add(new ColumnTable<String>("securityID", TypeSQL.VARCHAR) {
            @Override
            public boolean verify(String value) {
                try{
                    UUID.fromString(value);
                }
                catch (Exception e){
                    return false;
                }
                return true;
            }
        });
        list.add(new ColumnTable<String>("synonym", TypeSQL.TEXT) {
            @Override
            public boolean verify(String value) {
                try{
                    if(value.indexOf("[") != 0)
                    {
                        return false;
                    }
                    if(value.lastIndexOf("]") != value.length()-1)
                    {
                        return false;
                    }
                    value = value.substring(1, value.length()-1);
                    String[] listOfElement = value.split(";");
                    for(String element : listOfElement){
                        String[] typeAndValue = element.split(":");
                        if(typeAndValue.length < 2){
                            return false;
                        }
                    }
                }
                catch (Exception e){
                    return false;
                }
                return true;
            }
        });
        DataBaseTable table = new DataBaseTable(list, "Security");

        //creation list of ColumnValue to specify the values of the columns that will be deleted
        List<ColumnValue> listOfColumnValue = new ArrayList<>();
        listOfColumnValue.add(new ColumnValue("countryOfIssue", "France"));

        //delete the row
        int nbrDeleted = DataBaseGenerator.dropElement(db, table, listOfColumnValue);

        //print the number of deleted rows
        System.out.println("Number of deleted rows : " + nbrDeleted);
    }
}
