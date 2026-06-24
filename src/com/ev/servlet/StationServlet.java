package com.ev.servlet;

import com.ev.dao.StationDAO;
import com.ev.model.ChargingStation;
import com.google.gson.Gson;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/stations/*")
public class StationServlet extends HttpServlet {
    private final StationDAO dao  = new StationDAO();
    private final Gson       gson = new Gson();

    // GET /api/stations        → all stations
    // GET /api/stations/{id}   → single station
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        try {
            String path = req.getPathInfo();
            if (path == null || path.equals("/")) {
                List<ChargingStation> list = dao.getAllStations();
                res.getWriter().write(gson.toJson(list));
            } else {
                int id = Integer.parseInt(path.substring(1));
                ChargingStation s = dao.getById(id);
                if (s == null) { res.setStatus(404); res.getWriter().write("{\"error\":\"Not found\"}"); return; }
                res.getWriter().write(gson.toJson(s));
            }
        } catch (SQLException e) {
            res.setStatus(500);
            res.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // POST /api/stations  → add station
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        try {
            ChargingStation s = gson.fromJson(req.getReader(), ChargingStation.class);
            dao.addStation(s);
            res.setStatus(201);
            res.getWriter().write("{\"message\":\"Station added successfully\"}");
        } catch (SQLException e) {
            res.setStatus(500);
            res.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // DELETE /api/stations/{id}
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        try {
            String path = req.getPathInfo();
            int id = Integer.parseInt(path.substring(1));
            dao.deleteStation(id);
            res.getWriter().write("{\"message\":\"Station deleted\"}");
        } catch (SQLException e) {
            res.setStatus(500);
            res.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}