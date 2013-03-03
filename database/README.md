Database implementation
=======================

This Android app uses a MySQL database to store the debts.   
Put the file [sql.php](https://github.com/wpinnoo/DebtApp/blob/master/database/sql.php) on your server and fill in your username and password of the MySQL database.   
The Android application can access the database by sending a POST request to `sql.php` with two parameters: 
   
           password:<password check in the php file>   
           stmt:<MySQL-statement>   

`sql.php` will return a JSON file which contains the response of the MySQL database.   

As you can see in [database_implementation.sql](database_implementation.sql), the database consists of two tables:
* User(name:varchar, id:integer)
* Debts(debtid:integer, amount:double, description:varchar, creditorid:integer, debtorid:integer)
