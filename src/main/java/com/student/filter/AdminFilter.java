package com.student.filter;

import com.student.model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

// Áp dụng cho tất cả URL /student và action admin
@WebFilter(filterName = "AdminFilter", urlPatterns = {"/student", "/student/*"})
public class AdminFilter implements Filter {

    private static final String[] ADMIN_ACTIONS = {"new", "insert", "edit", "update", "delete"};

    @Override
    public void init(FilterConfig filterConfig) {
        System.out.println("AdminFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String action = req.getParameter("action");

        if (action != null && isAdminAction(action)) {
            HttpSession session = req.getSession(false);
            User user = (session != null) ? (User) session.getAttribute("user") : null;

            if (user == null || !user.isAdmin()) {
                res.sendRedirect(req.getContextPath() + "/student?action=list&error=Access denied");
                return;
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        System.out.println("AdminFilter destroyed");
    }

    private boolean isAdminAction(String action) {
        for (String adminAction : ADMIN_ACTIONS) {
            if (adminAction.equals(action)) return true;
        }
        return false;
    }
}
