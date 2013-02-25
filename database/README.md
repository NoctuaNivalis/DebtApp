Database implementation
=======================

This Android app uses a MySQL database to store the debts.   
Make a file in this directory named `database.properties` in which you save your username, password and the link to your database.  
e.g.:   

    url = jdbc:mysql://servername:3306/debtdb?zeroDateTimeBehavior=convertToNull
    
    driver = com.mysql.jdbc.Driver
  
    user = debtdbuser
  
    password = XXXXXXXXX
   
As you can see in [database_implementation.sql](database_implementation.sql), the database consists of two tables:
* User(name:varchar, id:integer)
* Debts(debtid:integer, amount:double, description:varchar, creditorid:integer, debtorid:integer)
