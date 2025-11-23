package com.student.controller;

import com.student.dao.StudentDAO;
import com.student.model.Student;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/student")
public class StudentController extends HttpServlet {

    private StudentDAO studentDAO;

    @Override
    public void init() {
        // Khởi tạo DAO để thao tác với cơ sở dữ liệu
        studentDAO = new StudentDAO();
    }

    @Override // GET requests: show page
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) {
            action = "list"; // mặc định hiển thị danh sách
        }

        try {
            switch (action) {
                case "new":
                    showNewForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "delete":
                    deleteStudent(request, response);
                    break;
                case "search":
                    searchStudents(request, response);
                    break;
                case "sort":  // action mới: sắp xếp sinh viên
                    sortStudents(request, response);
                    break;
                case "filter": // action mới: lọc sinh viên theo ngành
                    filterStudents(request, response);
                    break;
                default:
                    listStudents(request, response);
                    break;
            }
        } catch (Exception e) {
            throw new ServletException("Error processing request", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("insert".equals(action)) {
            insertStudent(request, response);
        } else if ("update".equals(action)) {
            updateStudent(request, response);
        }
    }

    // ====================== ACTION METHODS ======================

    // Hiển thị danh sách sinh viên
    private void listStudents(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Student> students = studentDAO.getAllStudents();
        request.setAttribute("students", students);
        request.setAttribute("keyword", ""); // để search form trống
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
        dispatcher.forward(request, response);
    }

    // Tìm kiếm sinh viên theo từ khóa
    private void searchStudents(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        List<Student> students = studentDAO.searchStudents(keyword);

        request.setAttribute("students", students);
        request.setAttribute("keyword", keyword != null ? keyword : "");

        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
        dispatcher.forward(request, response);
    }

    // Hiển thị form thêm mới
    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("formAction", "insert"); // phân biệt Add
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
        dispatcher.forward(request, response);
    }

    // Hiển thị form chỉnh sửa
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Student existingStudent = studentDAO.getStudentById(id);
        request.setAttribute("student", existingStudent);
        request.setAttribute("formAction", "update"); // phân biệt Edit
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
        dispatcher.forward(request, response);
    }

    // Thêm mới sinh viên
    private void insertStudent(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String studentCode = request.getParameter("studentCode");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String major = request.getParameter("major");

        Student newStudent = new Student(studentCode, fullName, email, major);

        // Validate student
        if (!validateStudent(newStudent, request)) {
            request.setAttribute("student", newStudent);
            request.setAttribute("formAction", "insert"); // giữ Add form
            RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
            dispatcher.forward(request, response);
            return;
        }

        boolean success = studentDAO.addStudent(newStudent);

        if (success) {
            response.sendRedirect("student?action=list&message=Student+added+successfully");
        } else {
            request.setAttribute("student", newStudent);
            request.setAttribute("formAction", "insert");
            request.setAttribute("error", "Failed to add student (duplicate code?)");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
            dispatcher.forward(request, response);
        }
    }

    // Cập nhật thông tin sinh viên
    private void updateStudent(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));
        String studentCode = request.getParameter("studentCode");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String major = request.getParameter("major");

        Student student = new Student(studentCode, fullName, email, major);
        student.setId(id);

        // Validate student
        if (!validateStudent(student, request)) {
            request.setAttribute("student", student);
            request.setAttribute("formAction", "update"); // giữ Edit form
            RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
            dispatcher.forward(request, response);
            return;
        }

        boolean success = studentDAO.updateStudent(student);

        if (success) {
            response.sendRedirect("student?action=list&message=Student+updated+successfully");
        } else {
            request.setAttribute("student", student);
            request.setAttribute("formAction", "update");
            request.setAttribute("error", "Failed to update student");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
            dispatcher.forward(request, response);
        }
    }

    // Xóa sinh viên
    private void deleteStudent(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        boolean success = studentDAO.deleteStudent(id);

        if (success) {
            response.sendRedirect("student?action=list&message=Student+deleted+successfully");
        } else {
            response.sendRedirect("student?action=list&error=Failed+to+delete+student");
        }
    }

    // ====================== VALIDATION METHOD ======================

    private boolean validateStudent(Student student, HttpServletRequest request) {
        boolean isValid = true;

        // Validate Student Code
        String studentCode = student.getStudentCode();
        String codePattern = "[A-Z]{2}[0-9]{3,}";
        if (studentCode == null || studentCode.trim().isEmpty()) {
            request.setAttribute("errorCode", "Student code is required");
            isValid = false;
        } else if (!studentCode.matches(codePattern)) {
            request.setAttribute("errorCode", "Invalid format. Use 2 letters + 3+ digits (e.g., SV001)");
            isValid = false;
        }

        // Validate Full Name
        String fullName = student.getFullName();
        if (fullName == null || fullName.trim().isEmpty()) {
            request.setAttribute("errorName", "Full name is required");
            isValid = false;
        } else if (fullName.trim().length() < 2) {
            request.setAttribute("errorName", "Full name must be at least 2 characters");
            isValid = false;
        }

        // Validate Email (optional)
        String email = student.getEmail();
        if (email != null && !email.trim().isEmpty()) {
            String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";
            if (!email.matches(emailPattern)) {
                request.setAttribute("errorEmail", "Invalid email format");
                isValid = false;
            }
        }

        // Validate Major
        String major = student.getMajor();
        if (major == null || major.trim().isEmpty()) {
            request.setAttribute("errorMajor", "Major is required");
            isValid = false;
        }

        return isValid;
    }

    // ====================== SORT & FILTER ======================

    // Sắp xếp sinh viên theo cột và thứ tự
    private void sortStudents(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Lấy tham số từ request
        String sortBy = request.getParameter("sortBy"); // ví dụ: "id", "fullName"
        String order = request.getParameter("order");   // "asc" hoặc "desc"

        // Gọi DAO để lấy danh sách đã sắp xếp
        List<Student> students = studentDAO.getStudentsSorted(sortBy, order);

        // Gán attribute để view hiển thị
        request.setAttribute("students", students);
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("order", order);

        // Forward về student-list.jsp
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
        dispatcher.forward(request, response);
    }

    // Lọc sinh viên theo ngành
    private void filterStudents(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String major = request.getParameter("major");
        List<Student> students;

        if (major == null || major.isEmpty()) {
            // Nếu chọn "All Majors", trả về tất cả sinh viên
            students = studentDAO.getAllStudents();
        } else {
            // Nếu chọn một ngành cụ thể
            students = studentDAO.getStudentsByMajor(major);
        }

        // Gán attribute để view hiển thị
        request.setAttribute("students", students);
        request.setAttribute("major", major);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
        dispatcher.forward(request, response);
    }
}
