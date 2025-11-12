<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Kitchen - MoodCafe</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body class="container mt-4">
<h1>Kitchen Orders</h1>

<%
    String flash = (String) session.getAttribute("flash");
    String flashType = (String) session.getAttribute("flashType");
    if (flash != null) {
%>
    <div class="alert alert-<%= flashType == null ? "info" : flashType %>"><%= flash %></div>
    <%
        session.removeAttribute("flash");
        session.removeAttribute("flashType");
    }
%>

<table class="table table-striped">
    <thead>
    <tr><th>ID</th><th>Items</th><th>Amount</th><th>Status</th><th>Actions</th></tr>
    </thead>
    <tbody>
    <% java.util.List<com.example.servletdemo.model.OrderEntity> orders = (java.util.List<com.example.servletdemo.model.OrderEntity>) request.getAttribute("orders");
       if (orders != null) {
           for (com.example.servletdemo.model.OrderEntity o : orders) {
    %>
    <tr>
        <td><%= o.getId() %></td>
        <td>
            <% String fullname = o.getName();
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
        <td>
            <form method="post" action="${pageContext.request.contextPath}/kitchen" style="display:inline">
                <input type="hidden" name="id" value="<%= o.getId() %>" />
                <input type="hidden" name="status" value="PENDING" />
                <button class="btn btn-sm btn-outline-secondary" type="submit">Pending</button>
            </form>
            <form method="post" action="${pageContext.request.contextPath}/kitchen" style="display:inline">
                <input type="hidden" name="id" value="<%= o.getId() %>" />
                <input type="hidden" name="status" value="IN_PROGRESS" />
                <button class="btn btn-sm btn-outline-primary" type="submit">In Progress</button>
            </form>
            <form method="post" action="${pageContext.request.contextPath}/kitchen" style="display:inline">
                <input type="hidden" name="id" value="<%= o.getId() %>" />
                <input type="hidden" name="status" value="COMPLETED" />
                <button class="btn btn-sm btn-outline-success" type="submit">Completed</button>
            </form>
        </td>
    </tr>
    <%       }
       }
    %>
    </tbody>
</table>

<a class="btn btn-secondary" href="${pageContext.request.contextPath}/orders">Back to orders</a>
</body>
</html>
