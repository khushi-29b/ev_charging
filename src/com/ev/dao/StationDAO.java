package com.ev.dao;

import com.ev.model.ChargingStation;
import com.ev.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StationDAO {

    // 🔹 GET ALL STATIONS
    public List<ChargingStation> getAllStations() throws SQLException {
        List<ChargingStation> list = new ArrayList<>();

        String sql = "SELECT cs.*, d.discom_name " +
                     "FROM charging_station cs " +
                     "JOIN discom d ON cs.discom_id = d.discom_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }

        return list;
    }

    // 🔹 GET BY ID
    public ChargingStation getById(int id) throws SQLException {
        String sql = "SELECT cs.*, d.discom_name " +
                     "FROM charging_station cs " +
                     "JOIN discom d ON cs.discom_id = d.discom_id " +
                     "WHERE cs.station_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }

        return null;
    }

    // 🔹 ADD STATION
    public void addStation(ChargingStation s) throws SQLException {
        String sql = "INSERT INTO charging_station " +
                     "(operator_name, latitude, longitude, discom_id, transformer_id) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, s.getOperatorName());
            ps.setDouble(2, s.getLatitude());
            ps.setDouble(3, s.getLongitude());
            ps.setInt(4, s.getDiscomId());
            ps.setInt(5, s.getTransformerId());

            ps.executeUpdate();
        }
    }

    // 🔹 DELETE STATION
    public void deleteStation(int id) throws SQLException {
        String sql = "DELETE FROM charging_station WHERE station_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // 🔹 MAP RESULTSET → OBJECT
    private ChargingStation mapRow(ResultSet rs) throws SQLException {
        ChargingStation s = new ChargingStation();

        s.setStationId(rs.getInt("station_id"));
        s.setOperatorName(rs.getString("operator_name"));
        s.setLatitude(rs.getDouble("latitude"));
        s.setLongitude(rs.getDouble("longitude"));
        s.setDiscomId(rs.getInt("discom_id"));
        s.setTransformerId(rs.getInt("transformer_id"));
        s.setDiscomName(rs.getString("discom_name"));

        return s;
    }
}