package com.ev.dao;

import com.ev.model.Vehicle;
import com.ev.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {

    // 🔹 GET VEHICLES BY USER
    public List<Vehicle> getByUser(int userId) throws SQLException {
        List<Vehicle> list = new ArrayList<>();

        String sql = "SELECT * FROM vehicle WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }

        return list;
    }

    // 🔹 GET ALL VEHICLES
    public List<Vehicle> getAllVehicles() throws SQLException {
        List<Vehicle> list = new ArrayList<>();

        String sql = "SELECT * FROM vehicle";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }

        return list;
    }

    // 🔹 ADD VEHICLE
    public void addVehicle(Vehicle v) throws SQLException {
        String sql = "INSERT INTO vehicle (user_id, make, battery_capacity) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, v.getUserId());
            ps.setString(2, v.getMake());
            ps.setDouble(3, v.getBatteryCapacity());

            ps.executeUpdate();
        }
    }

    // 🔹 MAP RESULTSET → OBJECT
    private Vehicle mapRow(ResultSet rs) throws SQLException {
        Vehicle v = new Vehicle();

        v.setVehicleId(rs.getInt("vehicle_id"));
        v.setUserId(rs.getInt("user_id"));
        v.setMake(rs.getString("make"));
        v.setBatteryCapacity(rs.getDouble("battery_capacity"));

        return v;
    }
}