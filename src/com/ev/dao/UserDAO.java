package com.ev.dao;

import com.ev.model.User;
import com.ev.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // GET ALL USERS
    public List<User> getAllUsers() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM \"User\"";   // FIXED

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    // GET USER BY ID
    public User getById(int id) throws SQLException {
        String sql = "SELECT * FROM \"User\" WHERE user_id = ?";  // FIXED

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // ADD USER
    public void addUser(User u) throws SQLException {
        String sql = "INSERT INTO \"User\" (kyc_status) VALUES (?)";  // FIXED

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, u.getKycStatus().toLowerCase());
            ps.executeUpdate();
        }
    }

    // UPDATE KYC
    public void updateKyc(int userId, String status) throws SQLException {
        String sql = "UPDATE \"User\" SET kyc_status = ? WHERE user_id = ?";  // FIXED

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    // MAP RESULTSET
    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setKycStatus(rs.getString("kyc_status"));
        return u;
    }
}