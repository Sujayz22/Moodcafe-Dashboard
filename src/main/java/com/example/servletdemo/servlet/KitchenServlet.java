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
import java.util.List;

@WebServlet(urlPatterns = "/kitchen")
public class KitchenServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<OrderEntity> orders = OrderRepository.findAll();
          
            java.util.List<OrderEntity> filtered = new java.util.ArrayList<>();
            for (OrderEntity o : orders) {
                String s = o.getStatus();
                if (s == null) s = "PENDING";
                if (!"COMPLETED".equalsIgnoreCase(s)) filtered.add(o);
            }
          
            java.util.Comparator<OrderEntity> cmp = (a, b) -> {
                int rankA = statusRank(a.getStatus());
                int rankB = statusRank(b.getStatus());
                return Integer.compare(rankA, rankB);
            };
            filtered.sort(cmp);
            req.setAttribute("orders", filtered);
        } catch (SQLException e) {
            getServletContext().log("Failed to load orders for kitchen", e);
            req.setAttribute("dbInitError", "Database error: " + e.getMessage());
        }
        req.getRequestDispatcher("/WEB-INF/jsp/kitchen.jsp").forward(req, resp);
    }

    private static int statusRank(String status) {
        if (status == null) return 0;
        switch (status.toUpperCase()) {
            case "PENDING": return 0;
            case "IN_PROGRESS": return 1;
            default: return 2;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        String newStatus = req.getParameter("status");
        if (idStr != null && newStatus != null) {
            try {
                int id = Integer.parseInt(idStr);
                OrderRepository.updateStatus(id, newStatus);
                req.getSession().setAttribute("flash", "Order #" + id + " updated to " + newStatus);
                req.getSession().setAttribute("flashType", "success");
            } catch (NumberFormatException | SQLException e) {
                getServletContext().log("Failed to update order status", e);
                req.getSession().setAttribute("flash", "Failed to update order: " + e.getMessage());
                req.getSession().setAttribute("flashType", "danger");
            }
        }
        resp.sendRedirect(req.getContextPath() + "/kitchen");
    }
}
