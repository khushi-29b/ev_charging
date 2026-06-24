package com.ev.servlet;

import com.ev.dao.VehicleDAO;
import com.ev.model.Vehicle;
import com.google.gson.Gson;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/vehicles/*")
public class VehicleServlet extends HttpServlet {
    private final VehicleDAO dao  = new VehicleDAO();
    private final Gson       gson = new Gson();

    // GET /api/vehicles          → all
    // GET /api/vehicles?user=1   → by user
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        try {
            String userParam = req.getParameter("user");
            List<Vehicle> list;
            if (userParam != null) {
                list = dao.getByUser(Integer.parseInt(userParam));
            } else {
                list = dao.getAllVehicles();
            }
            res.getWriter().write(gson.toJson(list));
        } catch (SQLException e) {
            res.setStatus(500);
            res.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        try {
            Vehicle v = gson.fromJson(req.getReader(), Vehicle.class);
            dao.addVehicle(v);
            res.setStatus(201);
            res.getWriter().write("{\"message\":\"Vehicle registered\"}");
        } catch (SQLException e) {
            res.setStatus(500);
            res.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}