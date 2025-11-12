package com.example.servletdemo.servlet;

import com.example.servletdemo.collections.orderCount;
import com.example.servletdemo.exception.CustomMyException;
import com.example.servletdemo.exception.ExceptionEmpty;
import com.example.servletdemo.generics.GenericBox;
import com.example.servletdemo.model.OrderEntity;
import com.example.servletdemo.repository.OrderRepository;
import com.example.servletdemo.utils.DemoUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = "/orders")
public class HomeServlet extends HttpServlet {
    @Override
    public void init() throws ServletException {
        super.init();
        try {
            OrderRepository.initDatabase();
            getServletContext().setAttribute("dbInitError", null);
        } catch (SQLException e) {
           
            String msg = "Failed to initialize DB: " + e.getMessage();
            getServletContext().log(msg, e);
            getServletContext().setAttribute("dbInitError", msg);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       
        List<OrderEntity> orders = new java.util.ArrayList<>();
        Map<String, Integer> counts = java.util.Collections.emptyMap();
        String formatted = "";
        try {
            orders = OrderRepository.findAll();
      
            java.util.Comparator<OrderEntity> orderCmp = (a, b) -> Integer.compare(statusRank(a.getStatus()), statusRank(b.getStatus()));
            orders.sort(orderCmp);
        
            GenericBox<OrderEntity> box = new GenericBox<>();
            for (OrderEntity o : orders) box.add(o);

            
            counts = orderCount.countByName(orders);

           
            formatted = DemoUtils.format(orders);

      
            try {
                ExceptionEmpty.validateNotEmpty(orders);
                req.setAttribute("exceptionMessage", null);
            } catch (CustomMyException ex) {
                req.setAttribute("exceptionMessage", ex.getMessage());
            }
        } catch (SQLException e) {
            String msg = "Database error: " + e.getMessage();
            getServletContext().log(msg, e);
         
            getServletContext().setAttribute("dbInitError", msg);
        }

        req.setAttribute("orders", orders);
        req.setAttribute("counts", counts);
        req.setAttribute("formatted", formatted);
        req.getRequestDispatcher("/WEB-INF/jsp/orders.jsp").forward(req, resp);
    }

    private static int statusRank(String status) {
        if (status == null) return 2;
        switch (status.toUpperCase()) {
            case "COMPLETED": return 0;
            case "IN_PROGRESS": return 1;
            case "PENDING": return 2;
            default: return 3;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String amountStr = req.getParameter("amount");
        double amount = 0.0;
        String flashMessage = null;
        String flashType = "success";

        if (name == null || name.trim().isEmpty()) {
            flashMessage = "Name is required.";
            flashType = "danger";
        } else {
            if (amountStr == null || amountStr.trim().isEmpty()) {
                flashMessage = "Amount is required.";
                flashType = "danger";
            } else {
                try {
                    amount = Double.parseDouble(amountStr);
                    if (amount < 0) {
                        flashMessage = "Amount must be zero or positive.";
                        flashType = "danger";
                    }
                } catch (NumberFormatException ex) {
                    flashMessage = "Amount must be a number.";
                    flashType = "danger";
                }
            }
        }

        if (flashMessage == null) {
            OrderEntity o = new OrderEntity(name.trim(), amount);
            try {
                OrderRepository.save(o);
                flashMessage = "Order added: " + o.getName() + " (" + String.format("%.2f", o.getAmount()) + ")";
                flashType = "success";
            } catch (SQLException e) {
                String msg = "Database error while saving order: " + e.getMessage();
                getServletContext().log(msg, e);
                
                flashMessage = "Database error: " + e.getMessage();
                flashType = "danger";
            }
        }

        req.getSession().setAttribute("flash", flashMessage);
        req.getSession().setAttribute("flashType", flashType);

        resp.sendRedirect(req.getContextPath() + "/orders");
    }
}
