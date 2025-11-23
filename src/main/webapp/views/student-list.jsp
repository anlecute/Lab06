<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student List - MVC</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            border-radius: 10px;
            padding: 30px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
        }

        h1 { color: #333; margin-bottom: 10px; font-size: 32px; }
        .subtitle { color: #666; margin-bottom: 30px; font-style: italic; }

        .btn {
            display: inline-block;
            padding: 12px 24px;
            border-radius: 5px;
            text-decoration: none;
            font-size: 14px;
            cursor: pointer;
            transition: 0.3s;
            border: none;
        }

        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: #fff;
            margin-bottom: 20px;
        }

        .btn-secondary { background: #6c757d; color: #fff; padding: 8px 16px; }
        .btn-danger { background: #dc3545; color: #fff; padding: 8px 16px; }

        .sortable {
            color: white;
            text-decoration: underline;
            cursor: pointer;
        }

        .sortable:hover {
            opacity: 0.8;
        }

        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        thead { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; }

        th, td { padding: 15px; text-align: left; border-bottom: 1px solid #ddd; }
        tbody tr:hover { background-color: #f8f9fa; }

        .search-box form {
            display: flex;
            gap: 10px;
            align-items: center;
            
        }

        .search-box input[type="text"] {
            flex: 1;
            padding: 14px;
            border-radius: 5px;
            border: 1px solid #ccc;
        }

        .filter-box select {
            padding: 12px;
            border-radius: 5px;
            border: 1px solid #ccc;
            width: 250px;
            appearance: none;
            background: #fff url("data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='20' height='20'><polygon points='0,0 20,0 10,15' fill='%23767a89'/></svg>") no-repeat right 12px center;
            background-size: 14px;
        }

        .filter-box {
            display: flex;
            justify-content: flex-end;
            margin-top: 20px;
            margin-bottom: 10px;
        }
    </style>
</head>
<body>
<div class="container">

    <h1>📚 Student Management System</h1>
    <p class="subtitle">MVC Pattern with Jakarta EE & JSTL</p>

    <!-- Add New Student -->
    <div class="mb-20">
        <a href="student?action=new" class="btn btn-primary">➕ Add New Student</a>
    </div>

    <!-- SEARCH FORM -->
    <div class="search-box">
        <form action="student" method="get">
            <input type="hidden" name="action" value="search">
            <input type="text" name="keyword" placeholder="Search students..." value="${keyword}">
            <button type="submit" class="btn btn-search">🔎 Search</button>

            <c:if test="${not empty keyword}">
                <a href="student?action=list" class="btn btn-secondary">❌ Clear</a>
            </c:if>
        </form>
    </div>

    <!-- FILTER BY MAJOR -->
    <div class="filter-box">
        <form action="student" method="get">
            <input type="hidden" name="action" value="filter">

            <select name="major">
                <option value="">All Majors</option>
                <option value="Computer Science" <c:if test="${major == 'Computer Science'}">selected</c:if>>Computer Science</option>
                <option value="Information Technology" <c:if test="${major == 'Information Technology'}">selected</c:if>>Information Technology</option>
                <option value="Software Engineering" <c:if test="${major == 'Software Engineering'}">selected</c:if>>Software Engineering</option>
                <option value="Business Administration" <c:if test="${major == 'Business Administration'}">selected</c:if>>Business Administration</option>
            </select>

            <button type="submit" class="btn btn-apply">Apply</button>

            <c:if test="${not empty major}">
                <a href="student?action=list" class="btn btn-secondary">❌ Clear</a>
            </c:if>
        </form>
    </div>

    <!-- ✅ STUDENT TABLE -->
    <table>
        <thead>
        <tr>
            <th>
                <a class="sortable"
                   href="student?action=sort&sortBy=id&order=${order == 'asc' ? 'desc' : 'asc'}">
                    ID
                    <c:if test="${sortBy == 'id'}">${order == 'asc' ? '▲' : '▼'}</c:if>
                </a>
            </th>

            <th>
                <a class="sortable"
                   href="student?action=sort&sortBy=studentCode&order=${order == 'asc' ? 'desc' : 'asc'}">
                    Student Code
                    <c:if test="${sortBy == 'studentCode'}">${order == 'asc' ? '▲' : '▼'}</c:if>
                </a>
            </th>

            <th>
                <a class="sortable"
                   href="student?action=sort&sortBy=fullName&order=${order == 'asc' ? 'desc' : 'asc'}">
                    Full Name
                    <c:if test="${sortBy == 'fullName'}">${order == 'asc' ? '▲' : '▼'}</c:if>
                </a>
            </th>

            <th>Email</th>
            <th>Major</th>
            <th>Actions</th>
        </tr>
        </thead>

        <tbody>
        <c:forEach var="student" items="${students}">
            <tr>
                <td>${student.id}</td>
                <td><strong>${student.studentCode}</strong></td>
                <td>${student.fullName}</td>
                <td>${student.email}</td>
                <td>${student.major}</td>
                <td>
                    <a href="student?action=edit&id=${student.id}" class="btn btn-secondary">✏️ Edit</a>
                    <a href="student?action=delete&id=${student.id}" class="btn btn-danger"
                       onclick="return confirm('Are you sure?')">🗑️ Delete</a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

</div>
</body>
</html>
