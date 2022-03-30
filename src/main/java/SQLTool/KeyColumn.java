package SQLTool;

/**
 * this class is used to link a column with a key (like primary key, foreign key, ect)
 */
public class KeyColumn {
    private KeySQL keySQL;
    private String columnName;
    private Integer lengthOfValue;

    /**
     * constructor
     * @param keySQL the keySQL
     * @param lengthOfValue the length of the value (for example for varchar(10) it is 10) or null if there is no length (for primary key it is an obligatory field)
     * @param columnName the columnName
     */
    public KeyColumn(KeySQL keySQL,Integer lengthOfValue , String columnName) {
        this.keySQL = keySQL;
        this.columnName = columnName;
        this.lengthOfValue = lengthOfValue;
    }

    public KeySQL getKeySQL() {
        return keySQL;
    }

    public String getColumnName() {
        return columnName;
    }

    public Integer getLengthOfValue() {
        return lengthOfValue;
    }
}
