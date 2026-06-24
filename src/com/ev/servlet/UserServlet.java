package com.ev.servlet;

import com.ev.dao.UserDAO;
import com.ev.model.User;
import com.google.gson.Gson;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/users/*")
public class UserServlet extends HttpServlet {
    private final UserDAO dao  = new UserDAO();
    private final Gson    gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        try {
            List<User> list = dao.getAllUsers();
            res.getWriter().write(gson.toJson(list));
        } catch (SQLException e) {
            res.setStatus(500);
            res.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // POST /api/users  → register user
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        try {
            User u = gson.fromJson(req.getReader(), User.class);
            if (u.getKycStatus() == null) u.setKycStatus("Pending");
            dao.addUser(u);
            res.setStatus(201);
            res.getWriter().write("{\"message\":\"User registered\"}");
        } catch (SQLException e) {
            res.setStatus(500);
            res.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // PUT /api/users/{id}/kyc?status=Verified
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        try {
            String path   = req.getPathInfo(); // /{id}/kyc
            String[] parts = path.split("/");
            int    userId = Integer.parseInt(parts[1]);
            String status = req.getParameter("status");
            dao.updateKyc(userId, status);
            res.getWriter().write("{\"message\":\"KYC updated\"}");
        } catch (SQLException e) {
            res.setStatus(500);
            res.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}