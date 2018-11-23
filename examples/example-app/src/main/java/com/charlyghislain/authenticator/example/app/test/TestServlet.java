package com.charlyghislain.authenticator.example.app.test;

import org.checkerframework.checker.nullness.qual.NonNull;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "test", urlPatterns = "/test/*")
public class TestServlet extends HttpServlet {


    @Override
    protected void doGet(@NonNull HttpServletRequest req, @NonNull HttpServletResponse resp) throws ServletException, IOException {
        String requestedResource = req.getRequestURI().replaceAll("/test/", "");
        if (requestedResource.isEmpty()) {
            resp.sendRedirect("index.html");
            return;
        }
        RequestDispatcher defaultDispatcher = getServletContext().getNamedDispatcher("default");
        defaultDispatcher.forward(req, resp);
    }
}
