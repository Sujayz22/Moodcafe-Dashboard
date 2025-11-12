Servlet demo module demonstrating:

- A simple servlet using Jakarta Servlet API
- JDBC access to an in-memory H2 database
- Java concepts: function overloading, generics, collections, exceptions

How to run


1. From the repository root (or from `g:/Projects/moodcafe`), run:

```powershell
mvn -f servlet-demo jetty:run
```

2. By default the app is served at context path `/moodcafe` on port 8081 (changeable). Open:

- http://localhost:8081/moodcafe/ (landing page)
- http://localhost:8081/moodcafe/orders (orders page)

Override the port with a system property when starting Jetty, for example:

```powershell
mvn -Djetty.port=9000 -f servlet-demo jetty:run
```

Notes

- The project uses an in-memory H2 database (URL jdbc:h2:mem:ordersdb;DB_CLOSE_DELAY=-1). Data lasts while JVM runs.
- The servlet is mapped via @WebServlet to `/orders` and demonstrates the requested Java concepts in a small UI.

Java concepts & teaching notes
-----------------------------
This project was built as a focused classroom demo. Below is a compact map from core Java concepts to the files that show them and a short teaching note for each.

- Servlets & web apps
	- Files: `src/main/java/com/example/servletdemo/servlet/DemoServlet.java`, `PlaceOrderServlet.java`, `KitchenServlet.java`, `AddMenuServlet.java`, `src/main/webapp/WEB-INF/web.xml`
	- Notes: shows servlet lifecycle (init/doGet/doPost), request/session attributes, forwarding to JSPs, redirects, and explicit servlet mapping. Good for demonstrating HTTP request handling and MVC separation.

- JDBC (raw)
	- File: `src/main/java/com/example/servletdemo/repository/OrderRepository.java`
	- Notes: demonstrates DriverManager, PreparedStatement, ResultSet, try-with-resources, schema creation (CREATE TABLE IF NOT EXISTS), retrieving generated keys, and Postgres-specific RETURNING/UPSERT patterns.

- Collections
	- Files: `src/main/java/com/example/servletdemo/collections/CollectionDemo.java`, `src/main/java/com/example/servletdemo/repository/OrderRepository.java`, `src/main/webapp/WEB-INF/jsp/*.jsp`
	- Notes: List, Map, LinkedHashMap used for ordered iteration and counting; aggregation examples used to compute counts by item name.

- Generics
	- File: `src/main/java/com/example/servletdemo/generics/GenericBox.java` and usages in `DemoServlet`
	- Notes: simple generic container to show compile-time type-safety and bounded usage.

- Function overloading
	- File: `src/main/java/com/example/servletdemo/utils/DemoUtils.java`
	- Notes: overloaded methods for formatting single orders vs lists; useful to show compile-time dispatch based on parameter types.

- Exceptions
	- Files: `src/main/java/com/example/servletdemo/exception/CustomDemoException.java`, `ExceptionDemo.java`, plus checked SQLException handling in `OrderRepository` and servlets
	- Notes: custom checked exception usage, try/catch, and surfacing friendly error messages to the UI.

- Streams & lambdas
	- Example: `cart.stream().mapToDouble(OrderEntity::getAmount).sum()` in `PlaceOrderServlet`
	- Notes: small, practical use of streams and method references for summing amounts.

- JSP & server-side rendering
	- Files: `src/main/webapp/WEB-INF/jsp/orders.jsp`, `place_order.jsp`, `kitchen.jsp`
	- Notes: simple scriptlet-based rendering (keeps dependencies minimal). Good for teaching how server-side templates receive model attributes.

Quick teaching tips
- Start the app with `mvn -Djetty.port=8082 -f servlet-demo jetty:run` and walk students through adding menu items, placing orders, and viewing kitchen status.
- Switch to Postgres by running the app with `-Djdbc.url=jdbc:postgresql://host:5432/moodcafe -Djdbc.user=... -Djdbc.pass=...` and point out the `ON CONFLICT` upsert and `RETURNING` usage.
- Discuss trade-offs: raw JDBC + scriptlets = excellent for teaching fundamentals, but real apps typically use JPA/Hibernate, connection pools, and templating frameworks (Thymeleaf, React, etc.) for production.

If you'd like, I can add a short dedicated `TEACHING.md` with step-by-step class activities and exercises (30–60 minute lab). Mark if you want that and I will add it.
