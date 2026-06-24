package com.ev.dao;

import com.ev.model.Payment;
import com.ev.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    // 🔹 GET ALL PAYMENTS
    public List<Payment> getAllPayments() throws SQLException {
        List<Payment> list = new ArrayList<>();

        String sql = "SELECT * FROM payment ORDER BY payment_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }

        return list;
    }

    // 🔹 ADD PAYMENT
    public void addPayment(Payment p) throws SQLException {
        String sql = "INSERT INTO payment (session_id, payment_method, amount) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, p.getSessionId());
            ps.setString(2, p.getPaymentMethod());
            ps.setDouble(3, p.getAmount());

            ps.executeUpdate();
        }
    }

    // 🔹 MAP RESULTSET → OBJECT
    private Payment mapRow(ResultSet rs) throws SQLException {
        Payment p = new Payment();

        p.setPaymentId(rs.getInt("payment_id"));
        p.setSessionId(rs.getInt("session_id"));
        p.setPaymentMethod(rs.getString("payment_method"));
        p.setAmount(rs.getDouble("amount"));

        return p;
    }
}