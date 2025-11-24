package com.student.dao;

import com.student.model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

public class StudentDAO {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/student_management_lab5";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "thuyan123457@";

    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found", e);
        }
    }

    // ====================== CRUD ======================

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY id DESC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                students.add(buildStudentFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public Student getStudentById(int id) {
        String sql = "SELECT * FROM students WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return buildStudentFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addStudent(Student student) {
        String sql = "INSERT INTO students (student_code, username, full_name, email, major, password, role) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, student.getStudentCode());
            pstmt.setString(2, student.getUsername());
            pstmt.setString(3, student.getFullName());
            pstmt.setString(4, student.getEmail());
            pstmt.setString(5, student.getMajor());

            // Hash password
            String hashedPassword = BCrypt.hashpw(student.getPassword(), BCrypt.gensalt());
            pstmt.setString(6, hashedPassword);

            pstmt.setString(7, student.getRole());

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStudent(Student student) {
        String sql = "UPDATE students SET student_code = ?, username = ?, full_name = ?, email = ?, major = ?, password = ?, role = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, student.getStudentCode());
            pstmt.setString(2, student.getUsername());
            pstmt.setString(3, student.getFullName());
            pstmt.setString(4, student.getEmail());
            pstmt.setString(5, student.getMajor());

            // Hash password
            String hashedPassword = BCrypt.hashpw(student.getPassword(), BCrypt.gensalt());
            pstmt.setString(6, hashedPassword);

            pstmt.setString(7, student.getRole());
            pstmt.setInt(8, student.getId());

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteStudent(int id) {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ====================== SEARCH ======================

    public List<Student> searchStudents(String keyword) {
        List<Student> students = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllStudents();
        }

        String sql = "SELECT * FROM students WHERE student_code LIKE ? OR username LIKE ? OR full_name LIKE ? OR email LIKE ? ORDER BY id DESC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String pattern = "%" + keyword.trim() + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            pstmt.setString(3, pattern);
            pstmt.setString(4, pattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    students.add(buildStudentFromResultSet(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    // ====================== SORT & FILTER ======================

    public List<Student> getStudentsSorted(String sortBy, String order) {
        List<Student> students = new ArrayList<>();
        sortBy = validateSortBy(sortBy);
        order = validateOrder(order);

        String sql = "SELECT * FROM students ORDER BY " + sortBy + " " + order;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                students.add(buildStudentFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    public List<Student> getStudentsByMajor(String major) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE major = ? ORDER BY id DESC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, major);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    students.add(buildStudentFromResultSet(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    public List<Student> getStudentsFiltered(String major, String sortBy, String order) {
        List<Student> students = new ArrayList<>();
        sortBy = validateSortBy(sortBy);
        order = validateOrder(order);

        String sql = "SELECT * FROM students WHERE 1=1";
        if (major != null && !major.trim().isEmpty()) {
            sql += " AND major = ?";
        }
        sql += " ORDER BY " + sortBy + " " + order;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (major != null && !major.trim().isEmpty()) {
                pstmt.setString(1, major);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    students.add(buildStudentFromResultSet(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    // ====================== LOGIN CHECK ======================

    public boolean checkLogin(String username, String password) {
        String sql = "SELECT * FROM students WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String hashed = rs.getString("password");
                    return BCrypt.checkpw(password, hashed);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // ====================== HELPERS ======================

    private String validateSortBy(String sortBy) {
        String[] valid = {"id", "student_code", "username", "full_name", "email", "major"};
        for (String col : valid) {
            if (col.equalsIgnoreCase(sortBy)) return col;
        }
        return "id";
    }

    private String validateOrder(String order) {
        if ("desc".equalsIgnoreCase(order)) return "DESC";
        return "ASC";
    }

    private Student buildStudentFromResultSet(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        student.setStudentCode(rs.getString("student_code"));
        student.setUsername(rs.getString("username"));
        student.setFullName(rs.getString("full_name"));
        student.setEmail(rs.getString("email"));
        student.setMajor(rs.getString("major"));
        student.setPassword(rs.getString("password")); // hashed
        student.setRole(rs.getString("role"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            student.setCreatedAt(createdAt);
        }

        return student;
    }

    public int getTotalStudents() {
        String sql = "SELECT COUNT(*) AS total FROM students";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) return rs.getInt("total");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
