package com.ev.servlet;

import com.ev.dao.PaymentDAO;
import com.ev.model.Payment;
import com.google.gson.Gson;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/payments/*")
public class PaymentServlet extends HttpServlet {
    private final PaymentDAO dao  = new PaymentDAO();
    private final Gson       gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        try {
            List<Payment> list = dao.getAllPayments();
            res.getWriter().write(gson.toJson(list));
        } catch (SQLException e) {
            res.setStatus(500);
            res.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // POST /api/payments  → {sessionId, paymentMethod, amount}
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        try {
            Payment p = gson.fromJson(req.getReader(), Payment.class);
            dao.addPayment(p);
            res.setStatus(201);
            res.getWriter().write("{\"message\":\"Payment recorded\"}");
        } catch (SQLException e) {
            res.setStatus(500);
            res.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}