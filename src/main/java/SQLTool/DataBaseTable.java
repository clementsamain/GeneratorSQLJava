package SQLTool;

import java.util.HashMap;
import java.util.List;

/**
 * represents a table in a database
 */
public class DataBaseTable {
    private HashMap<String, ColumnTable> columnList;
    private String tableName;

    /**
     * constructor
     * @param columnList the list of columns in the table
     * @param tableName the name of the table
     */
    public DataBaseTable(List<ColumnTable> columnList, String tableName) {
        this.columnList = new HashMap<>();
        for(ColumnTable column : columnList) {
            this.columnList.put(column.getColumnName(), column);
        }
        this.tableName = tableName;
    }

    public HashMap<String, ColumnTable> getColumnList() {
        return columnList;
    }

    public String getTableName() {
        return tableName;
    }
}
