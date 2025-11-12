package com.example.servletdemo.servlet;

import com.example.servletdemo.model.OrderEntity;
import com.example.servletdemo.repository.OrderRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = "/place-order")
public class PlaceOrderServlet extends HttpServlet {
   

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("menu", OrderRepository.getMenuMap());
        } catch (SQLException e) {
            getServletContext().log("Failed to load menu", e);
            req.setAttribute("menu", new java.util.LinkedHashMap<String, Double>());
        }
      
        List<OrderEntity> cart = (List<OrderEntity>) req.getSession().getAttribute("cart");
        if (cart == null) cart = new ArrayList<>();
        double total = cart.stream().mapToDouble(OrderEntity::getAmount).sum();
        req.setAttribute("cart", cart);
        req.setAttribute("total", total);
        req.getRequestDispatcher("/WEB-INF/jsp/place_order.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "add";

        if (action.equals("add")) {
            String item = req.getParameter("item");
            if (item != null) {
                try {
                    Double price = OrderRepository.getMenuPrice(item);
                    if (price != null) {
                        List<OrderEntity> cart = (List<OrderEntity>) req.getSession().getAttribute("cart");
                        if (cart == null) { cart = new ArrayList<>(); req.getSession().setAttribute("cart", cart); }
                        cart.add(new OrderEntity(item, price));
                        req.getSession().setAttribute("flash", "Added to cart: " + item);
                        req.getSession().setAttribute("flashType", "success");
                    } else {
                        req.getSession().setAttribute("flash", "Unknown menu item.");
                        req.getSession().setAttribute("flashType", "danger");
                    }
                } catch (SQLException e) {
                    getServletContext().log("Failed to lookup menu price", e);
                    req.getSession().setAttribute("flash", "Database error: " + e.getMessage());
                    req.getSession().setAttribute("flashType", "danger");
                }
            } else {
                req.getSession().setAttribute("flash", "No item specified.");
                req.getSession().setAttribute("flashType", "danger");
            }
            resp.sendRedirect(req.getContextPath() + "/place-order");
            return;
        }

        if (action.equals("checkout")) {
            
            String[] items = req.getParameterValues("cart_item[]");
            String[] qtys = req.getParameterValues("cart_qty[]");
            List<OrderEntity> cart = new ArrayList<>();
            if (items != null && qtys != null && items.length == qtys.length) {
                for (int i = 0; i < items.length; i++) {
                    try {
                        int q = Integer.parseInt(qtys[i]);
                        if (q <= 0) continue;
                        String name = items[i];
                            Double price = null;
                            try { price = OrderRepository.getMenuPrice(name); } catch (SQLException ex) { /* ignore */ }
                        if (price == null) continue;
                        for (int k = 0; k < q; k++) cart.add(new OrderEntity(name, price));
                    } catch (NumberFormatException ignore) {
                    }
                }
            }

            
            if (cart.isEmpty()) {
                List<OrderEntity> sessionCart = (List<OrderEntity>) req.getSession().getAttribute("cart");
                if (sessionCart != null) cart.addAll(sessionCart);
            }

            if (cart.isEmpty()) {
                req.getSession().setAttribute("flash", "Cart is empty.");
                req.getSession().setAttribute("flashType", "danger");
                resp.sendRedirect(req.getContextPath() + "/place-order");
                return;
            }

           
            StringBuilder names = new StringBuilder();
            double total = 0.0;
            for (int i = 0; i < cart.size(); i++) {
                if (i > 0) names.append(", ");
                names.append(cart.get(i).getName());
                total += cart.get(i).getAmount();
            }

            OrderEntity order = new OrderEntity(names.toString(), total);
            try {
                OrderRepository.save(order);
                req.getSession().setAttribute("flash", "Order placed (#" + order.getId() + "): " + order.getName());
                req.getSession().setAttribute("flashType", "success");
                
                req.getSession().removeAttribute("cart");
            } catch (SQLException e) {
                req.getSession().setAttribute("flash", "Database error: " + e.getMessage());
                req.getSession().setAttribute("flashType", "danger");
            }
            resp.sendRedirect(req.getContextPath() + "/orders");
            return;
        }

  
        req.getSession().setAttribute("flash", "Unknown action");
        req.getSession().setAttribute("flashType", "danger");
        resp.sendRedirect(req.getContextPath() + "/place-order");
    }
}
