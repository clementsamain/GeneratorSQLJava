package Tool;

import java.sql.ResultSet;
import java.util.List;

/**
 * class
 */
public class DataBase {
    public static ResultSet getResult(DataBaseConnexion connexion, DataBaseTable table, List<ColumnValue> columnValues) {
        String request = "SELECT * FROM " + table.getTableName() + " WHERE ";
        for(ColumnValue columnValue : columnValues) {
            if(table.getColumnList().containsKey(columnValue.getColumnName())) // if the column is in the table
            {
                if(table.getColumnList().get(columnValue.getColumnName()).verify(columnValue.getColumnValue())) // if the value is valid
                {
                    request += columnValue.getColumnName() + " = " + columnValue.getColumnValue() + " AND ";
                }
                else
                {
                    throw new IllegalArgumentException("The value " + columnValue.getColumnValue() + " is not valid for the column " + columnValue.getColumnName());
                }
            }
            else
            {
                throw new IllegalArgumentException("The column " + columnValue.getColumnName() + " is not in the table " + table.getTableName());
            }
        }
        request = request.substring(0, request.length() - 5) + ";";
        return connexion.sendRequest(request);
    }

    public static boolean addElement(DataBaseConnexion connexion, DataBaseTable table, List<ColumnValue> columnValues) {
        String requestPart1 = "INSERT INTO " + table.getTableName() + " (";
        String requestPart2 = " VALUES (";
        for(ColumnValue columnValue : columnValues) {
            if(table.getColumnList().containsKey(columnValue.getColumnName())) // if the column is in the table
            {
                if(table.getColumnList().get(columnValue.getColumnName()).verify(columnValue.getColumnValue())) // if the value is valid
                {
                    requestPart1 += columnValue.getColumnName() + ", ";
                    requestPart2 += columnValue.getColumnValue() + ", ";
                }
                else
                {
                    throw new IllegalArgumentException("The value " + columnValue.getColumnValue() + " is not valid for the column " + columnValue.getColumnName());
                }
            }
            else
            {
                throw new IllegalArgumentException("The column " + columnValue.getColumnName() + " is not in the table " + table.getTableName());
            }
        }
        requestPart1 = requestPart1.substring(0, requestPart1.length() - 2) + ")";
        requestPart2 = requestPart2.substring(0, requestPart2.length() - 2) + ");";
        String request = requestPart1 + requestPart2;
        return connexion.sendModifyRequest(request);
    }
}
