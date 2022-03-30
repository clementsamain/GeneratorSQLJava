package SQLTool;

/**
 * class that represents a value in a column in a table of a database
 */
public class ColumnValue {
    private String columnValue;
    private String columnName;

    /**
     * constructor
     * @param columnName name of the column
     * @param columnValue value of the column
     */
    public ColumnValue(String columnName, String columnValue) {
        this.columnName = columnName;
        this.columnValue = columnValue;
    }

    public String getColumnValue() {
        return columnValue;
    }

    public String getColumnName() {
        return columnName;
    }
}
