package com.ev.dao;

import com.ev.model.MaintenanceTicket;
import com.ev.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {

    // 🔹 GET ALL TICKETS
    public List<MaintenanceTicket> getAllTickets() throws SQLException {
        List<MaintenanceTicket> list = new ArrayList<>();

        String sql = "SELECT mt.*, cs.operator_name " +
                     "FROM maintenance_ticket mt " +
                     "JOIN charging_station cs ON mt.station_id = cs.station_id " +
                     "ORDER BY mt.opened_time DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }

        return list;
    }

    // 🔹 OPEN TICKET
    public void openTicket(MaintenanceTicket t) throws SQLException {
        String sql = "INSERT INTO maintenance_ticket (station_id, issue_desc, opened_time) VALUES (?, ?, NOW())";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, t.getStationId());
            ps.setString(2, t.getIssueDesc());

            ps.executeUpdate();
        }
    }

    // 🔹 CLOSE TICKET
    public void closeTicket(int ticketId) throws SQLException {
        String sql = "UPDATE maintenance_ticket SET closed_time = NOW() WHERE ticket_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, ticketId);
            ps.executeUpdate();
        }
    }

    // 🔹 MAP RESULTSET → OBJECT
    private MaintenanceTicket mapRow(ResultSet rs) throws SQLException {
        MaintenanceTicket t = new MaintenanceTicket();

        t.setTicketId(rs.getInt("ticket_id"));
        t.setStationId(rs.getInt("station_id"));
        t.setIssueDesc(rs.getString("issue_desc"));
        t.setOpenedTime(rs.getString("opened_time"));
        t.setClosedTime(rs.getString("closed_time"));
        t.setOperatorName(rs.getString("operator_name"));

        return t;
    }
}