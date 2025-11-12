package com.example.servletdemo.servlet;

import com.example.servletdemo.repository.OrderRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

public class AddMenuServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String priceStr = req.getParameter("price");
        if (name == null || name.trim().isEmpty() || priceStr == null) {
            req.getSession().setAttribute("flash", "Name and price are required");
            req.getSession().setAttribute("flashType", "danger");
            resp.sendRedirect(req.getContextPath() + "/place-order");
            return;
        }
        try {
            double price = Double.parseDouble(priceStr);
            OrderRepository.addMenuItem(name.trim(), price);
            req.getSession().setAttribute("flash", "Menu item added/updated: " + name);
            req.getSession().setAttribute("flashType", "success");
        } catch (NumberFormatException | SQLException e) {
            req.getSession().setAttribute("flash", "Failed to add menu item: " + e.getMessage());
            req.getSession().setAttribute("flashType", "danger");
        }
        resp.sendRedirect(req.getContextPath() + "/place-order");
    }
}
