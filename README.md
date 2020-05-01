# DST-project-group-2
Project under construction...

To run prototype:
1. download Prototype-group2.zip on this page. Please do not clone or download the entire repository, other parts are under construction.
2. create PostgreSQL database with name 'biomed'.
3. create tables by running codes in /src/main/resources/Table_creation.sql; insert table content by either running 'copy' codes or through pgAdmin GUI (recommended) and essential tables are stored in .src/main/resources/tables/ ; modify tables by running codes in the rest of Table_creation.sql.
4. config /src/main/resources/app.properties by filling the username and password of the database you have created.
5. config local tomcat server in IDEA.