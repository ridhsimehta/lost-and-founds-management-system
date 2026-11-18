package lostfound.db;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lostfound.core.*;

/** All JDBC access: PreparedStatement for every query, ResultSet mapped to objects. */
public class Repository {
    private final Connection con;

    public Repository(String url) throws SQLException {
        con = DriverManager.getConnection(url);
        try (Statement st = con.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "name TEXT NOT NULL, contact TEXT UNIQUE NOT NULL)");
            st.execute("CREATE TABLE IF NOT EXISTS items(id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "type TEXT, name TEXT, category TEXT, description TEXT, location TEXT, "
                    + "date TEXT, user_id INTEGER, status TEXT DEFAULT 'OPEN')");
            st.execute("CREATE TABLE IF NOT EXISTS claims(id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "lost_id INTEGER, found_id INTEGER, score INTEGER, proof TEXT, "
                    + "status TEXT DEFAULT 'SUGGESTED')");
        }
    }

    public synchronized int saveUser(User u) throws SQLException {
        try (PreparedStatement q = con.prepareStatement("SELECT id FROM users WHERE contact=?")) {
            q.setString(1, u.getContact());
            try (ResultSet rs = q.executeQuery()) { if (rs.next()) return rs.getInt(1); }
        }
        try (PreparedStatement p = con.prepareStatement("INSERT INTO users(name,contact) VALUES(?,?)")) {
            p.setString(1, u.getName());
            p.setString(2, u.getContact());
            p.executeUpdate();
            try (ResultSet k = p.getGeneratedKeys()) { k.next(); return k.getInt(1); }
        }
    }

    public synchronized User getUser(int id) throws SQLException {
        try (PreparedStatement p = con.prepareStatement("SELECT * FROM users WHERE id=?")) {
            p.setInt(1, id);
            try (ResultSet rs = p.executeQuery()) {
                return rs.next() ? new User(id, rs.getString("name"), rs.getString("contact")) : null;
            }
        }
    }

    public synchronized void addItem(Item it) throws SQLException {
        try (PreparedStatement p = con.prepareStatement(
                "INSERT INTO items(type,name,category,description,location,date,user_id,status) VALUES(?,?,?,?,?,?,?,?)")) {
            p.setString(1, it.getType());
            p.setString(2, it.getName());
            p.setString(3, it.getCategory());
            p.setString(4, it.getDescription());
            p.setString(5, it.getLocation());
            p.setString(6, it.getDate().toString());
            p.setInt(7, it.getUserId());
            p.setString(8, it.getStatus());
            p.executeUpdate();
            try (ResultSet k = p.getGeneratedKeys()) { if (k.next()) it.setId(k.getInt(1)); }
        }
    }

    private Item map(ResultSet rs) throws SQLException {
        int id = rs.getInt("id"), uid = rs.getInt("user_id");
        String n = rs.getString("name"), c = rs.getString("category"), d = rs.getString("description");
        String l = rs.getString("location"), s = rs.getString("status");
        LocalDate date = LocalDate.parse(rs.getString("date"));
        return "LOST".equals(rs.getString("type"))
                ? new LostItem(id, n, c, d, l, date, uid, s)
                : new FoundItem(id, n, c, d, l, date, uid, s);
    }

    public synchronized List<Item> items(String type, String status) throws SQLException {
        List<Item> out = new ArrayList<>();
        try (PreparedStatement p = con.prepareStatement(
                "SELECT * FROM items WHERE type=? AND status=? ORDER BY id")) {
            p.setString(1, type);
            p.setString(2, status);
            try (ResultSet rs = p.executeQuery()) { while (rs.next()) out.add(map(rs)); }
        }
        return out;
    }

    public synchronized Item getItem(int id) throws SQLException, ItemNotFoundException {
        try (PreparedStatement p = con.prepareStatement("SELECT * FROM items WHERE id=?")) {
            p.setInt(1, id);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        throw new ItemNotFoundException("Item #" + id + " was not found");
    }

    public synchronized void setItemStatus(int id, String status) throws SQLException {
        try (PreparedStatement p = con.prepareStatement("UPDATE items SET status=? WHERE id=?")) {
            p.setString(1, status);
            p.setInt(2, id);
            p.executeUpdate();
        }
    }

    public synchronized boolean claimExists(int lostId, int foundId) throws SQLException {
        try (PreparedStatement p = con.prepareStatement(
                "SELECT 1 FROM claims WHERE lost_id=? AND found_id=?")) {
            p.setInt(1, lostId);
            p.setInt(2, foundId);
            try (ResultSet rs = p.executeQuery()) { return rs.next(); }
        }
    }

    public synchronized void addClaim(int lostId, int foundId, int score) throws SQLException {
        try (PreparedStatement p = con.prepareStatement(
                "INSERT INTO claims(lost_id,found_id,score,proof,status) VALUES(?,?,?,'','SUGGESTED')")) {
            p.setInt(1, lostId);
            p.setInt(2, foundId);
            p.setInt(3, score);
            p.executeUpdate();
        }
    }

    public synchronized List<Claim> claims(String status) throws SQLException {
        List<Claim> out = new ArrayList<>();
        try (PreparedStatement p = con.prepareStatement("SELECT * FROM claims WHERE status=? ORDER BY score DESC")) {
            p.setString(1, status);
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next())
                    out.add(new Claim(rs.getInt("id"), rs.getInt("lost_id"), rs.getInt("found_id"),
                            rs.getInt("score"), rs.getString("proof"), rs.getString("status")));
            }
        }
        return out;
    }

    public synchronized void updateClaim(int id, String proof, String status) throws SQLException {
        try (PreparedStatement p = con.prepareStatement("UPDATE claims SET proof=?, status=? WHERE id=?")) {
            p.setString(1, proof);
            p.setString(2, status);
            p.setInt(3, id);
            p.executeUpdate();
        }
    }
}
