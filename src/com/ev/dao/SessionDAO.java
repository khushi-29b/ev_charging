package com.ev.dao;

import com.ev.model.ChargingSession;
import com.ev.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SessionDAO {

    // 🔹 GET ALL SESSIONS
    public List<ChargingSession> getAllSessions() throws SQLException {
        List<ChargingSession> list = new ArrayList<>();

        String sql = "SELECT cs.*, c.connector_type, v.make AS vehicle_make " +
                     "FROM charging_session cs " +
                     "JOIN connector c ON cs.connector_id = c.connector_id " +
                     "JOIN vehicle v   ON cs.vehicle_id   = v.vehicle_id " +
                     "ORDER BY cs.start_time DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }

        return list;
    }

    // 🔹 START SESSION
    public void startSession(ChargingSession s) throws SQLException {
        String sql = "INSERT INTO charging_session (connector_id, vehicle_id, start_time) VALUES (?, ?, NOW())";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, s.getConnectorId());
            ps.setInt(2, s.getVehicleId());

            ps.executeUpdate();
        }
    }

    // 🔹 END SESSION
    public void endSession(int sessionId, double totalKwh) throws SQLException {
        String sql = "UPDATE charging_session SET end_time = NOW(), total_kwh = ? WHERE session_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, totalKwh);
            ps.setInt(2, sessionId);

            ps.executeUpdate();
        }
    }

    // 🔹 MAP RESULTSET → OBJECT
    private ChargingSession mapRow(ResultSet rs) throws SQLException {
        ChargingSession s = new ChargingSession();

        s.setSessionId(rs.getInt("session_id"));
        s.setConnectorId(rs.getInt("connector_id"));
        s.setVehicleId(rs.getInt("vehicle_id"));
        s.setStartTime(rs.getString("start_time"));
        s.setEndTime(rs.getString("end_time"));
        s.setTotalKwh(rs.getDouble("total_kwh"));
        s.setConnectorType(rs.getString("connector_type"));
        s.setVehicleMake(rs.getString("vehicle_make"));

        return s;
    }
}