<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Place Order - MoodCafe</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body class="container mt-4">
<h1>Place Order</h1>

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

<div class="row">
    <div class="col-md-6">
        <div class="card mb-3">
            <div class="card-header">Menu</div>
            <div class="card-body">
                <div class="list-group" id="menuList">
                    <% java.util.Map<String, Double> menu = (java.util.Map<String, Double>) request.getAttribute("menu");
                       if (menu != null) {
                           for (java.util.Map.Entry<String, Double> e : menu.entrySet()) {
                    %>
                    <div class="list-group-item d-flex justify-content-between align-items-center">
                        <div>
                            <strong><%= e.getKey() %></strong>
                            <div class="text-muted small">₹<%= String.format("%.2f", e.getValue()) %></div>
                        </div>
                        <div>
                            <input type="number" min="0" value="0" class="form-control form-control-sm qty-input" data-name="<%= e.getKey() %>" data-price="<%= e.getValue() %>" style="width:80px" />
                        </div>
                    </div>
                    <%   }
                       }
                    %>
                </div>
                <div class="mt-3">
                    <button id="addSelected" class="btn btn-primary">Add Selected to Cart</button>
                </div>
                <hr/>
                <div class="card mt-3">
                    <div class="card-header">Add / Update Menu Item</div>
                    <div class="card-body">
                        <form method="post" action="${pageContext.request.contextPath}/menu/add" class="row g-2">
                            <div class="col-7">
                                <input name="name" class="form-control" placeholder="Item name" required />
                            </div>
                            <div class="col-3">
                                <input name="price" class="form-control" type="number" step="0.01" placeholder="Price" required />
                            </div>
                            <div class="col-2">
                                <button class="btn btn-success w-100" type="submit">Save</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="col-md-6">
        <div class="card mb-3">
            <div class="card-header">Cart</div>
            <div class="card-body">
                <table class="table" id="cartTable">
                    <thead><tr><th>Item</th><th>Qty</th><th>Price</th><th></th></tr></thead>
                    <tbody></tbody>
                </table>
                <p class="fw-bold">Total: ₹<span id="cartTotal">0.00</span></p>

                <form id="checkoutForm" method="post" action="${pageContext.request.contextPath}/place-order">
                    <input type="hidden" name="action" value="checkout" />
                    <!-- JS will populate these arrays -->
                    <div id="cartInputs"></div>
                    <button class="btn btn-success" type="submit">Checkout</button>
                    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/orders">Back to orders</a>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
    // Client-side cart handling
    const cart = new Map(); // item -> {qty, price}
    function renderCart() {
        const tbody = document.querySelector('#cartTable tbody');
        tbody.innerHTML = '';
        let total = 0;
        cart.forEach((v, name) => {
            const tr = document.createElement('tr');
            const inner = '<td>' + name + '</td>' +
                '<td><input type="number" min="1" value="' + v.qty + '" class="form-control form-control-sm cart-qty" data-name="' + name + '" style="width:80px" /></td>' +
                '<td>$' + (v.price * v.qty).toFixed(2) + '</td>' +
                '<td><button class="btn btn-sm btn-danger remove-item" data-name="' + name + '">Remove</button></td>';
            tr.innerHTML = inner;
            tbody.appendChild(tr);
            total += v.price * v.qty;
        });
        document.getElementById('cartTotal').textContent = total.toFixed(2);

        // populate hidden inputs (use cart_item[] and cart_qty[] so servlet can read parameter arrays)
        const inputs = document.getElementById('cartInputs');
        inputs.innerHTML = '';
        cart.forEach((v, name) => {
            const it = document.createElement('input'); it.type='hidden'; it.name='cart_item[]'; it.value = name; inputs.appendChild(it);
            const q = document.createElement('input'); q.type='hidden'; q.name='cart_qty[]'; q.value = v.qty; inputs.appendChild(q);
        });
    }

    document.getElementById('addSelected').addEventListener('click', function(e){
        e.preventDefault();
        document.querySelectorAll('.qty-input').forEach(function(inp){
            const q = parseInt(inp.value || '0');
            if (q > 0) {
                const name = inp.dataset.name;
                const price = parseFloat(inp.dataset.price);
                const existing = cart.get(name) || {qty:0, price:price};
                existing.qty += q;
                cart.set(name, existing);
                inp.value = 0;
            }
        });
        renderCart();
    });

    document.querySelector('#cartTable tbody').addEventListener('click', function(e){
        if (e.target.classList.contains('remove-item')) {
            const name = e.target.dataset.name; cart.delete(name); renderCart();
        }
    });

    document.querySelector('#cartTable tbody').addEventListener('change', function(e){
        if (e.target.classList.contains('cart-qty')) {
            const name = e.target.dataset.name; const q = parseInt(e.target.value || '1');
            const obj = cart.get(name); if (obj) { obj.qty = q; cart.set(name, obj); renderCart(); }
        }
    });

    // initial render (if server provided a cart)
    renderCart();
</script>

</body>
</html>