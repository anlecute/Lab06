package com.student.model;

import java.sql.Timestamp;

public class Student {
    private int id;
    private String studentCode;
    private String username;      // thêm
    private String password;      // thêm (hash bằng BCrypt)
    private String fullName;
    private String email;
    private String major;
    private String role;          // thêm: user/admin
    private Timestamp createdAt;
    
    // No-arg constructor (required for JavaBean)
    public Student() {
    }
    
    // Constructor for creating new student (without ID)
    public Student(String studentCode, String username, String fullName, String email, String major, String password, String role) {
        this.studentCode = studentCode;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.major = major;
        this.password = password;
        this.role = role;
    }
    
    // Getters and Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getStudentCode() { return studentCode; }
    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", studentCode='" + studentCode + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", major='" + major + '\'' +
                '}';
    }
}
