package com.company.controller;

import com.company.dao.inter.UserDAOInter;
import com.company.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@WebServlet(name = "UserDetailController", value = "/userdetail")
public class UserDetailController extends HttpServlet {
    @Autowired
    private UserDAOInter userDao;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String userIdStr = request.getParameter("id");
            if (userIdStr == null || userIdStr.trim().isEmpty()) {
                throw new IllegalArgumentException("id teyin edilmeyib");
            }
            Integer userId = Integer.parseInt(userIdStr);
            System.out.println(userId);
            User u = userDao.getById(userId);
            if (u == null) {
                throw new IllegalArgumentException("bu id ile user tapilmadi");
            }
            request.setAttribute("user", u);
            request.getRequestDispatcher("userdetail.jsp").forward(request, response);
        } catch (Exception ex) {
            ex.printStackTrace();
            response.sendRedirect("error?msg=" + ex.getMessage());
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.valueOf(request.getParameter("id"));
        String action = request.getParameter("action");
        if (action.equals("update")) {
            String name = request.getParameter("name");
            String surname = request.getParameter("surname");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String address = request.getParameter("address");
            String profileDesc = request.getParameter("profile_description");
            String birthdate = request.getParameter("birthdate");

            User user = userDao.getById(id);
            user.setName(name);
            user.setSurname(surname);
            user.setEmail(email);
            user.setPhone(phone);
            user.setAddress(address);
            user.setProfileDescription(profileDesc);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date bd = null;
            try {
                bd = sdf.parse(birthdate);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            java.sql.Date hbd = new java.sql.Date(bd.getTime());
            user.setBirthdate(hbd);

            userDao.updateUser(user);
        } else if (action.equals("delete")) {
            userDao.removeUser(id);
        }
        response.sendRedirect("users");
    }
}
