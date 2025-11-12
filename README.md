MoodCafe

- A simple servlet using Jakarta Servlet API
- JDBC access to an in-memory H2 database and PostgreSql.
- Java concepts: constructor overloading, generics, collections, exceptions


A compact Java Servlet web application that demonstrates servlets, raw JDBC (H2), JSP rendering, and several core Java concepts (generics, collections, exceptions, streams).

## Features

- Simple servlet controllers: add menu, place orders, view kitchen and orders
- JSP-based UI (server-side rendering)
- Examples of JDBC usage, generics, collections, custom exceptions, and streams

## Prerequisites

- Java 11+ (project uses Maven and targets recent Java versions; confirm in `pom.xml`)
- Apache Maven 3.6+
- Optional: Apache Tomcat (9/10) to deploy the produced WAR, or the Jetty Maven plugin for local runs

## Build

From the repository root run:

```powershell
mvn clean package
```

This produces a WAR under `target/` (example: `target/servlet-demo-0.1.0.war`).

## Run / Deploy

There are two common ways to run the app locally:

- Using the Jetty Maven plugin (if configured in `pom.xml`):

```powershell
mvn -Djetty.port=8081 jetty:run
```

- Deploy the WAR to a servlet container (Apache Tomcat):
  1. Build with `mvn clean package`.
  2. Copy `target/*.war` to `TOMCAT_HOME/webapps/` and start Tomcat.
  3. Open the app at: http://localhost:8080/<context-path>/ (context path depends on the WAR name)

Notes:
- When using the Jetty plugin you can override the port via `-Djetty.port=9000`.
- To use an external database (Postgres, MySQL), set JDBC system properties as described in the Configuration section below.

## Configuration

Configuration properties live in `src/main/resources/application.properties`. Useful properties include JDBC URL, user and password. Example system property overrides when starting via Maven:

```powershell
mvn -Djdbc.url=jdbc:postgresql://host:5432/moodcafe -Djdbc.user=me -Djdbc.pass=secret -Djetty.port=8082 jetty:run
```

By default the app will use an in-memory H2 database (data lasts only while the JVM runs).

## Project structure (key files)

- `src/main/java/com/example/servletdemo/servlet/` — servlet controllers (AddMenuServlet, PlaceOrderServlet, KitchenServlet, HomeServlet)
- `src/main/java/com/example/servletdemo/repository/OrderRepository.java` — simple JDBC data access
- `src/main/java/com/example/servletdemo/model/` — domain model (OrderEntity, OrderLine)
- `src/main/java/com/example/servletdemo/generics/GenericBox.java` — small generics demo
- `src/main/java/com/example/servletdemo/collections/` — examples of collection usage and counting
- `src/main/webapp/WEB-INF/jsp/` — JSP views (orders.jsp, place_order.jsp, kitchen.jsp)
- `src/main/resources/application.properties` — runtime configuration

