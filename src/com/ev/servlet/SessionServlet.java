package com.ev.servlet;

import com.ev.dao.SessionDAO;
import com.ev.model.ChargingSession;
import com.google.gson.Gson;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/sessions/*")
public class SessionServlet extends HttpServlet {
    private final SessionDAO dao  = new SessionDAO();
    private final Gson       gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        try {
            List<ChargingSession> list = dao.getAllSessions();
            res.getWriter().write(gson.toJson(list));
        } catch (SQLException e) {
            res.setStatus(500);
            res.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // POST /api/sessions        → start session  {connectorId, vehicleId}
    // POST /api/sessions/end    → end session    {sessionId, totalKwh}
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        try {
            String path = req.getPathInfo();
            if ("/end".equals(path)) {
                ChargingSession s = gson.fromJson(req.getReader(), ChargingSession.class);
                dao.endSession(s.getSessionId(), s.getTotalKwh());
                res.getWriter().write("{\"message\":\"Session ended\"}");
            } else {
                ChargingSession s = gson.fromJson(req.getReader(), ChargingSession.class);
                dao.startSession(s);
                res.setStatus(201);
                res.getWriter().write("{\"message\":\"Session started\"}");
            }
        } catch (SQLException e) {
            res.setStatus(500);
            res.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}