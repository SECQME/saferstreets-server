##JNDI and DBCP Setup

Edit `${CATALINA_BASE}\conf\context.xml`. Add following lines inside `<Context>` tag:

	<Resource type="javax.sql.DataSource"
		name="jdbc/SaferStreetDB"
		factory="org.apache.tomcat.jdbc.pool.DataSourceFactory"
		driverClassName="com.mysql.jdbc.Driver"
		url="jdbc:mysql://localhost:3306/saferstreet"
		username="root"
		password="root"
		initialSize="10"
		maxActive="100"
		maxIdle="50"
		minIdle="10"
		suspectTimeout="60"
		timeBetweenEvictionRunsMillis="30000"
		minEvictableIdleTimeMillis="60000" />

* Add mysql-connector jar file to ${CATALINA_BASE}/tomcat/lib folder

Sources:

1. http://tomcat.apache.org/tomcat-7.0-doc/jndi-datasource-examples-howto.html
2. http://www.tomcatexpert.com/blog/2010/04/01/configuring-jdbc-pool-high-concurrency
