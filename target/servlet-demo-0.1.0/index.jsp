<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="en">
<head>
        <meta charset="utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <title>MoodCafe</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>
        <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/style.css" />
        <style>
            body { padding-top: 4.5rem; }
        </style>
 </head>
<body>
<nav class="navbar navbar-expand-md navbar-dark bg-dark fixed-top">
    <div class="container-fluid">
        <a class="navbar-brand" href="${pageContext.request.contextPath}">MoodCafe</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarsExampleDefault" aria-controls="navbarsExampleDefault" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarsExampleDefault">
            <ul class="navbar-nav me-auto mb-2 mb-md-0">
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/orders">Orders</a></li>
            </ul>
        </div>
    </div>
</nav>

<main class="container">
    <div class="px-4 py-5 my-5 text-center">
        <h1 class="display-5 fw-bold">Welcome to MoodCafe</h1>
        <div class="col-lg-6 mx-auto">
            <p class="lead mb-4">A tiny servlet demo showcasing JDBC, generics, collections, function overloading and exceptions.</p>
            <div class="d-grid gap-2 d-sm-flex justify-content-sm-center">
                <a href="${pageContext.request.contextPath}/orders" class="btn btn-primary btn-lg px-4 gap-3">View Orders</a>
            </div>
        </div>
    </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
