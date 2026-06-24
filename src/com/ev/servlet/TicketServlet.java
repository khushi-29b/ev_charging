package com.ev.servlet;

import com.ev.dao.TicketDAO;
import com.ev.model.MaintenanceTicket;
import com.google.gson.Gson;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/tickets/*")
public class TicketServlet extends HttpServlet {
    private final TicketDAO dao  = new TicketDAO();
    private final Gson      gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        try {
            List<MaintenanceTicket> list = dao.getAllTickets();
            res.getWriter().write(gson.toJson(list));
        } catch (SQLException e) {
            res.setStatus(500);
            res.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // POST /api/tickets          → open ticket   {stationId, issueDesc}
    // POST /api/tickets/close    → close ticket  {ticketId}
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        try {
            String path = req.getPathInfo();
            if ("/close".equals(path)) {
                MaintenanceTicket t = gson.fromJson(req.getReader(), MaintenanceTicket.class);
                dao.closeTicket(t.getTicketId());
                res.getWriter().write("{\"message\":\"Ticket closed\"}");
            } else {
                MaintenanceTicket t = gson.fromJson(req.getReader(), MaintenanceTicket.class);
                dao.openTicket(t);
                res.setStatus(201);
                res.getWriter().write("{\"message\":\"Ticket opened\"}");
            }
        } catch (SQLException e) {
            res.setStatus(500);
            res.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}