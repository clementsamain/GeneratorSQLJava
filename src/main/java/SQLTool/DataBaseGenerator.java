package SQLTool;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * class that generate the sql query, execute it and return the result
 */
public class DataBaseGenerator {

    /**
     * function that generate sql query to get element in the database, execute the request and return the result
     * @param connexion the connexion to the database
     * @param table the table of the database
     * @param columnValues the list of the column and the value that used to get the element in the database
     * @return the result of the request
     */
    public static ResultSet getElement(DataBaseConnexion connexion, DataBaseTable table, List<ColumnValue> columnValues) {
        String request = "SELECT * FROM " + table.getTableName() + " WHERE ";
        for (ColumnValue columnValue : columnValues) {
            if (table.getColumnList().containsKey(columnValue.getColumnName())) // if the column is in the table
            {
                ColumnTable columnTable = table.getColumnList().get(columnValue.getColumnName());
                if (columnTable.verify(columnValue.getColumnValue())) // if the value is valid
                {
                    request += columnValue.getColumnName() + " = " + getGoodFormat(columnTable.getTypeSQL(), columnValue.getColumnValue()) + " AND ";
                } else {
                    throw new IllegalArgumentException("The value " + columnValue.getColumnValue() + " is not valid for the column " + columnValue.getColumnName());
                }
            } else {
                throw new IllegalArgumentException("The column " + columnValue.getColumnName() + " is not in the table " + table.getTableName());
            }
        }
        request = request.substring(0, request.length() - 5) + ";";
        return connexion.sendRequest(request);
    }

    /**
     * function that generate sql query to add element in the database, execute the request and return if the request is done correctly or not
     * @param connexion the connexion to the database
     * @param table the table of the database
     * @param columnValues the list of the column and the value to insert
     * @return true if the request is done correctly, false if not
     */
    public static boolean insertElement(DataBaseConnexion connexion, DataBaseTable table, List<ColumnValue> columnValues) {
        String requestPart1 = "INSERT INTO " + table.getTableName() + " (";
        String requestPart2 = " VALUES (";
        HashMap<String, ColumnTable> map = table.getColumnList();
        HashMap<String, Boolean> verify = new HashMap<>();
        for (String key : map.keySet()) {
            verify.put(key, false);
        }
        for (ColumnValue columnValue : columnValues) {
            if (map.containsKey(columnValue.getColumnName())) // if the column is in the table
            {
                ColumnTable columnTable = map.get(columnValue.getColumnName());
                if (columnTable.verify(columnValue.getColumnValue())) // if the value is valid
                {
                    //change the value for the key in the verify hashmap
                    verify.put(columnValue.getColumnName(), true);
                    requestPart1 += columnValue.getColumnName() + ", ";
                    requestPart2 += getGoodFormat(columnTable.getTypeSQL(), columnValue.getColumnValue()) + ", ";
                } else {
                    throw new IllegalArgumentException("The value " + columnValue.getColumnValue() + " is not valid for the column " + columnValue.getColumnName());
                }
            } else {
                throw new IllegalArgumentException("The column " + columnValue.getColumnName() + " is not in the table " + table.getTableName());
            }
        }
        // verify if all the column are in the request
        for (String key : verify.keySet()) {
            if (!verify.get(key)) {
                throw new IllegalArgumentException("The column " + key + " is not in the request");
            }
        }
        requestPart1 = requestPart1.substring(0, requestPart1.length() - 2) + ")";
        requestPart2 = requestPart2.substring(0, requestPart2.length() - 2) + ");";
        String request = requestPart1 + requestPart2;
        return connexion.sendModifyRequest(request) == 1;
    }

    /**
     * function that modify an existing value in the table
     * @param db the database connexion where the table is
     * @param table the table where the value is
     * @param columnValueList the list of the value that represent a row in the table
     * @param newColumnValueList the list of the value that represent a row in the table to modify
     * @return int the number of row modified
     */
    public static int modifyElement(DataBaseConnexion db, DataBaseTable table, List<ColumnValue> columnValueList, List<ColumnValue> newColumnValueList) {
        String request = "UPDATE " + table.getTableName() + " SET ";
        HashMap<String, Boolean> verify = new HashMap<>();
        for (ColumnValue columnValue : columnValueList) {
            verify.put(columnValue.getColumnName(), false);
        }
        for (ColumnValue columnValue : newColumnValueList) {
            if (verify.containsKey(columnValue.getColumnName())) {
                verify.put(columnValue.getColumnName(), true);
                request += columnValue.getColumnName() + " = " + getGoodFormat(table.getColumnList().get(columnValue.getColumnName()).getTypeSQL(), columnValue.getColumnValue()) + ", ";
            } else {
                throw new IllegalArgumentException("The column " + columnValue.getColumnName() + " is not in the table " + table.getTableName());
            }
        }
        for (String key : verify.keySet()) {
            if (!verify.get(key)) {
                throw new IllegalArgumentException("The column " + key + " is not in the request");
            }
        }
        request = request.substring(0, request.length() - 2) + " WHERE ";
        for (ColumnValue columnValue : columnValueList) {
            request += columnValue.getColumnName() + " = " + getGoodFormat(table.getColumnList().get(columnValue.getColumnName()).getTypeSQL(), columnValue.getColumnValue()) + " AND ";
        }
        request = request.substring(0, request.length() - 5) + ";";
        return db.sendModifyRequest(request);
    }

    /**
     * function that generate the SQL request to create table and insert the table in the database
     * @param table the table to create in the database
     * @return boolean if the request is done correctly or not
     */
    public static boolean createTableInDataBase(DataBaseConnexion db, DataBaseTable table, List<KeyColumn> keyColumnList) {
        String request = "CREATE TABLE " + table.getTableName() + " (";
        for (ColumnTable columnTable : table.getColumnList().values()) {
            // verify if the column is in the key
            KeyColumn key = null;
            for (KeyColumn keyColumn : keyColumnList) {
                if (keyColumn.getColumnName().equals(columnTable.getColumnName())) {
                    key = keyColumn;
                }
            }
            if (key != null && key.getLengthOfValue() != null) {
                request += "`" + columnTable.getColumnName() + "` " + columnTable.getTypeSQL().name() + "(" + key.getLengthOfValue() + "), ";
            }
            else {
                request += "`" + columnTable.getColumnName() + "` " + columnTable.getTypeSQL().name() + ", ";
            }
        }
        // add key column
        for (KeyColumn keyColumn : keyColumnList) {
            //verify if the column is in the table
            if (table.getColumnList().containsKey(keyColumn.getColumnName())) {
                request += keyColumn.getKeySQL().name() + " KEY (`" + keyColumn.getColumnName() + "`), ";
            } else {
                throw new IllegalArgumentException("The column " + keyColumn.getColumnName() + " is not in the table " + table.getTableName());
            }
        }
        request = request.substring(0, request.length() - 2) + ");";
        db.sendModifyRequest(request);
        return verifyIfTableExist(db, table.getTableName());
    }

    /**
     * function that get the good format of the value to insert in the database by the type of the column
     * @param type the type of the column
     * @param value the value to insert
     * @return the good format of the value to insert in the database
     */
    private static String getGoodFormat(TypeSQL type, String value) {
        if(type == TypeSQL.TEXT) {
            return "'" + value + "'";
        }
        return value;
    }

    /**
     * function that parse the result of the request to get the list of the element in the database
     * @param resultSet the result of the request
     * @param table the table of the database
     * @return the list of the element in the database
     */
    public static String parseSQLResult(ResultSet resultSet, DataBaseTable table) {
        String result = "";
        try {
            while (resultSet.next()) {
                result += "{";
                for (ColumnTable columnTable : table.getColumnList().values()) {
                    result += columnTable.getColumnName() + ": " + resultSet.getString(columnTable.getColumnName()) + ", ";
                }
                result = result.substring(0, result.length() - 2) + "}, ";
            }
            result = result.substring(0, result.length() - 2);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    //function that verify if the table exist in the database use INFORMATION_SCHEMA.TABLES
    public static boolean verifyIfTableExist(DataBaseConnexion db, String tableName) {
        String request = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = '" + tableName + "';";
        ResultSet resultSet = db.sendRequest(request);
        try {
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
