<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  // Avoid JSTL to keep dependencies minimal. Use simple scriptlets for iteration.
  java.util.List orders = (java.util.List) request.getAttribute("orders");
  java.util.Map counts = (java.util.Map) request.getAttribute("counts");
  String flash = (String) session.getAttribute("flash");
  String flashType = (String) session.getAttribute("flashType");
  if (flash != null) {
    // consume flash
    session.removeAttribute("flash");
    session.removeAttribute("flashType");
  }
%>
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>MoodCafe - Orders</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/style.css" />
  <style>body{padding-top:4.5rem}</style>
</head>
<body>
<nav class="navbar navbar-expand-md navbar-dark bg-dark fixed-top">
  <div class="container-fluid">
    <a class="navbar-brand" href="${pageContext.request.contextPath}">MoodCafe</a>
    <div class="collapse navbar-collapse">
      <ul class="navbar-nav me-auto mb-2 mb-md-0">
        <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/orders">Orders</a></li>
        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/place-order">Place Order</a></li>
        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/kitchen">Kitchen</a></li>
      </ul>
    </div>
  </div>
</nav>

<main class="container">
  <div class="py-5">
    <h2>Orders</h2>

    <% if (flash != null) { %>
      <div class="alert alert-<%= (flashType!=null?flashType:"info") %> alert-dismissible fade show" role="alert">
        <%= flash %>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
      </div>
    <% } %>

    <% String dbInitError = (String) application.getAttribute("dbInitError"); %>
    <% if (dbInitError != null) { %>
      <div class="alert alert-warning" role="alert">
        Database not available: <%= dbInitError %>
      </div>
    <% } %>

    <div class="row">
      <div class="col-md-8">
        <table class="table table-striped table-hover">
          <thead>
            <tr><th>ID</th><th>Name</th><th>Amount</th><th>Status</th></tr>
          </thead>
          <tbody>
            <% if (orders != null) {
                 for (Object _o : orders) {
                     com.example.servletdemo.model.OrderEntity o = (com.example.servletdemo.model.OrderEntity) _o;
            %>
              <tr>
                <td><%= o.getId() %></td>
                <td>
                  <% String fullname = o.getName();
                     // split by comma and aggregate counts
                     String[] parts = fullname.split(",\\s*");
                     java.util.Map<String,Integer> ag = new java.util.LinkedHashMap<>();
                     for (String p : parts) { ag.put(p, ag.getOrDefault(p,0)+1); }
                     boolean first=true;
                     for (java.util.Map.Entry<String,Integer> en : ag.entrySet()) {
                         if (!first) out.print(", "); first=false;
                         out.print(en.getKey());
                         if (en.getValue() > 1) out.print(" (" + en.getValue() + ")");
                     }
                  %>
                </td>
                <td>₹<%= String.format("%.2f", o.getAmount()) %></td>
                <td><%= o.getStatus() %></td>
              </tr>
            <%  }
               }
            %>
          </tbody>
        </table>
      </div>
      </div>
    </div>

    <hr/>
  </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
