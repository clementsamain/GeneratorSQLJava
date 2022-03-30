# Generator SQL Java (Intro)
*With this package you can create the database structure based on the Java classes, generate the SQL queries, execute them and finally, get the results.*\
It's useful to communicate with the database without the need to write SQL queries.
For the moment, the package is not complete, but it's working.\
Some features are not implemented yet and some bugs are maybe not fixed.\
If you have any suggestion, bugs or problems, please, contact me.
# Installation
TODO
# Usage
TODO
# Todo list
Already implemented
- [X] Create a database structure based on the Java classes
- [X] Generate the SQL queries for get data from the database
- [X] Generate the SQL queries for insert data into the database


Not implemented yet
- [ ] Generate the SQL queries for update data into the database
- [ ] Generate the SQL queries for delete data from the database
- [ ] Specify primary keys and unique keys
- [ ] Generate reports with structure of the database
- [ ] Generate more complex queries (like 'where date is between two dates', >, <, >=, <=, =, !=, etc.)
- [ ] Add more type of data (like 'geometry', 'point', 'polygon', etc.)
- [ ] *Another TODO why not?*


# Contributing
- *Clément Samain*


# Examples
You can find all examples in the package Examples.
In all examples bellow, the database is composed of one table named "Security" with the following fields:
- securityId: string (primary key)
- synonym: string (unique), representing a list of synonyms separated by `;`
- faceValue: numeric
- faceValueCurrency: string
- typeOfBooking: string
- countryOfIssue: string
- legalTypeOfSecurity: string
- status: string
- cdcDepositDate: date
- revisionDate: date
- codeNature: string

if you want to test the package, you can use this SQL script to create the example database:
```sql 
CREATE TABLE `Security` (
    `faceValue` double NOT NULL,
    `faceValueCurrency` text NOT NULL,
    `typeOfBooking` text NOT NULL,
    `countryOfIssue` text NOT NULL,
    `legalTypeOfSecurity` text NOT NULL,
    `status` text NOT NULL,
    `cdcDepositDate` text NOT NULL,
    `revisionDate` text NOT NULL,
    `codeNature` text NOT NULL,
    `securityID` text NOT NULL,
    `synonym` text NOT NULL
);
```
```sql 
INSERT INTO `Security` (`faceValue`, `faceValueCurrency`, `typeOfBooking`, `countryOfIssue`, `legalTypeOfSecurity`, `status`, `cdcDepositDate`, `revisionDate`, `codeNature`, `securityID`, `synonym`) VALUES
(0.7, 'EUR', 'Nominal', 'France', 'Equity', 'Current', '2016', '2016', '1', '02e757a3-66bd-459d-8f17-c6868375a63f', '[type1:1;type2:2,type3:3]'),
(0.7, 'EUR', 'Nominal', 'France', 'Equity', 'Current', '2016', '2016', '1', '59430271-5535-41b4-b79a-73afb43666b7', '[type1:1;type2:2]');
```
Connecting to the database
```java
DataBaseConnexion db = new DataBaseConnexion("jdbc:mysql://localhost:3306/test", "user", "password");
```
If you have "SSLHandShakeException No Appropriate Protocol" error during the connection (often the case when your database is hosted online), you can try to use the following command:
```java
DataBaseConnexion db = new DataBaseConnexion("jdbc:mysql://localhost:3306/test" + "?enabledTLSProtocols=TLSv1.2", "user", "password");
```
Creating the database structure

- First, we need to create tables

the package contains an abstract class called *ColumnTable*. This class is used to create a column structure.\
You must specify the type of object that will input into the table from your program. (in this example, we suppose that the type of entries from the program is a String)\
You must also the type of the column in the table in the database. (in this example, the type of the column *faceValue* is a Double.)\
You must also specify the name of the column in the table in the database.\
Finally, you must implement the method *verify(T value)*, which is used to verify if the object that will be input into the table is valid(for example, the column *faceValue* can only accept Double values, so you must verify if the value is a Double).\
```java
List<ColumnTable> list = new ArrayList<>();
        list.add(new ColumnTable<String>("faceValue", TypeSQL.NUMBER) {
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
        list.add(new ColumnTable<String>("faceValueCurrency", TypeSQL.STRING) {
            @Override
            public boolean verify(String value) {
                return value.equals("EUR") || value.equals("USD") || value.equals("GBP");
            }
        });
        list.add(new ColumnTable<String>("typeOfBooking", TypeSQL.STRING) {
            @Override
            public boolean verify(String value) {
                return value.equals("Nominal") || value.equals("By Piece");
            }
        });
        list.add(new ColumnTable<String>("countryOfIssue", TypeSQL.STRING) {
            @Override
            public boolean verify(String value) {
                return value.equals("France") || value.equals("Espagne") || value.equals("Angleterre");
            }
        });
        list.add(new ColumnTable<String>("legalTypeOfSecurity", TypeSQL.STRING) {
            @Override
            public boolean verify(String value) {
                return value.equals("Equity") || value.equals("stock") || value.equals("Bound");
            }
        });
        list.add(new ColumnTable<String>("status", TypeSQL.STRING) {
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
        list.add(new ColumnTable<String>("codeNature", TypeSQL.STRING) {
            @Override
            public boolean verify(String value) {
                return true;
            }
        });
        list.add(new ColumnTable<String>("securityID", TypeSQL.STRING) {
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
        list.add(new ColumnTable<String>("synonym", TypeSQL.STRING) {
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
                        if(typeAndValue.length < 2){ // verify if element is a list
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
```
After the creation of columns, we can add columns to the table.\
The class DataBaseTable take a list of columns and the name of the table.
```java
DataBaseTable table = new DataBaseTable(list, "Security");
```
Now you have a connexion to the database and the table (or tables).\
So it's time to add some data to the table or to get some data from the table.
- Add data to the table\
Before building and execute a query, you have to add data to the table structure.\
For this, you have to create a list of ColumnValue.\
Each ColumnValue is a pair of a column name and a value.
    ```java
    List<ColumnValue> listOfColumnValue = new ArrayList<>();
        listOfColumnValue.add(new ColumnValue("securityID", UUID.randomUUID().toString())); // UUID is a class from java.util and it's used to generate a random UUID (Universally Unique Identifier) (https://en.wikipedia.org/wiki/Universally_unique_identifier)
        listOfColumnValue.add(new ColumnValue("faceValue", "0.7"));
        listOfColumnValue.add(new ColumnValue("faceValueCurrency", "EUR"));
        listOfColumnValue.add(new ColumnValue("typeOfBooking", "Nominal"));
        listOfColumnValue.add(new ColumnValue("countryOfIssue", "France"));
        listOfColumnValue.add(new ColumnValue("legalTypeOfSecurity", "Equity"));
        listOfColumnValue.add(new ColumnValue("status", "Current"));
        listOfColumnValue.add(new ColumnValue("cdcDepositDate", "2018-01-01"));
        listOfColumnValue.add(new ColumnValue("revisionDate", "2018-01-01"));
        listOfColumnValue.add(new ColumnValue("codeNature", "1"));
        listOfColumnValue.add(new ColumnValue("synonym", "[type1:1;type2:2,type3:3]"));
    ```
    It's time to build and execute the query.\
    The class DataBaseGenerator have functions to build and execute queries.\
    The function insertElement() take the *DataBaseConnexion* db, the *DataBaseTable* table and the list of *ColumnValue* and return a boolean that indicate if the query is executed correctly.
    ```java
    boolean ifOk = DataBaseGenerator.insertElement(db, table, listOfColumnValue);
    ```
- Get data from the table\
Before building and execute a query, you have to specify the columns and the value of the columns you want to get.\
In this case, we want to get all the data of the table when the country of issue is France.\
    ```java
    List<ColumnValue> listOfColumnValue = new ArrayList<>();
    listOfColumnValue.add(new ColumnValue("countryOfIssue", "France"));
    ```
  Now we have to build and execute the query. 
  The class DataBaseGenerator have functions to build and execute queries.\
  The function getResult() take the *DataBaseConnexion* db, the *DataBaseTable* table and the list of *ColumnValue* and return the result of the query.
    ```java
    ResultSet result = DataBaseGenerator.getElement(db, table, listOfColumnValue);
    ```
  The result of the query is a ResultSet.\
  If you want to get the data in a simple format, you can use the function *parseSQLResult(ResultSet resultSet, DataBaseTable table)* in the class DataBaseGenerator.
    ```java
    String simplyRes = DataBaseGenerator.parseSQLResult(result, table);
    System.out.println(simplyRes);
    ```
- modify data in the table\
  Before building and execute a query, you have to specify the columns and the value of the columns you want to modify.\
  In this case, we want to get all the data of the table that have *France* as *countryOfIssue* column.\
    ```java
    List<ColumnValue> listOfColumnValue = new ArrayList<>();
    listOfColumnValue.add(new ColumnValue("countryOfIssue", "France"));
    ```
  Now we have to specify the columns and the value of the columns for the modification.\
- ```java
  List<ColumnValue> listOfColumnValue2 = new ArrayList<>();
  listOfColumnValue2.add(new ColumnValue("countryOfIssue", "Espagne"));
    ```
  Now we have to build and execute the query.\
  The class DataBaseGenerator have function modifyElement() to build and execute queries.\
  The function modifyElement() take the *DataBaseConnexion* db, the *DataBaseTable* table, the list of *ColumnValue* that specify the data to modify and the list of *ColumnValue* that specify the data to set and return the number of rows modified.
    ```java
    int nbr = DataBaseGenerator.modifyElement(db, table, listOfColumnValue, listOfColumnValue2);
  System.out.println("element(s) modified : " + nbr);
    ```
  