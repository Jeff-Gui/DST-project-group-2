# DST-project-group-2

Run release (current v2.8)
1. clone repository
2. create PostgreSQL database with name 'biomed'.
3. create tables in PostgreSQL database by running codes in /src/main/resources/Table_creation.sql
4. config /src/main/resources/app.properties by filling the username and password of the database you have created.
5. to set up the database, run method initSystem() in src/test/java/basic/UpdateDataTest.java; this should only be operated once.
6. default administrator is username: zju; password: zju.

To run prototype:
1. download Prototype-group2.zip on this page. Please do not clone or download the entire repository, other parts are under construction.
2. create PostgreSQL database with name 'biomed'.
3. create tables by running codes in /src/main/resources/Table_creation.sql; insert table content by either running 'copy' codes or through pgAdmin GUI (recommended) and essential tables are stored in .src/main/resources/tables/ ; modify tables by running codes in the rest of Table_creation.sql.
4. config /src/main/resources/app.properties by filling the username and password of the database you have created.
5. config local tomcat server in IDEA (tomcat version 7+ is required).

We are:
Jeff Gui: Email: Yifan.18@intl.zju.edu.cn

Jixin Wang Email: Jixin.18@intl.zju.edu.cn

Yuxing Zhou Email: Yuxing.18@intl.zju.edu.cn

Caiylyn Jiang Email: Anlan.18@intl.zju.edu.cn

Valerya Wu Email: Xinyu.18@intl.zju.edu.cn
