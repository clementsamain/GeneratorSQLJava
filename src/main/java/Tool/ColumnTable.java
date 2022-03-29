package Tool;

/**
 * represents a column in a table in a database
 */
abstract class ColumnTable {
    private String columnName;

    /**
     * verify if the value is valid for the column
     * @param value the value to be inserted
     * @return true if the value is valid, false otherwise
     */
    abstract public boolean verify(Object value);

    /**
     * constructor
     * @param columnName the name of the column
     */
    public ColumnTable(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }
}
