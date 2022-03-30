package SQLTool;

/**
 * represents a column in a table in a database
 */
public abstract class ColumnTable<input> {
    private TypeSQL typeSQL;
    private String columnName;

    /**
     * verify if the value is valid for the column (the type of the column is typeSQL)
     * @param value the value to be inserted
     * @return true if the value is valid, false otherwise
     */
    abstract public boolean verify(input value);

    /**
     * constructor
     * @param columnName the name of the column
     * @param typeSQL the type of the column
     */
    public ColumnTable(String columnName, TypeSQL typeSQL) {
        this.columnName = columnName;
        this.typeSQL = typeSQL;
    }

    public String getColumnName() {
        return columnName;
    }

    public TypeSQL getTypeSQL() {
        return typeSQL;
    }
}
