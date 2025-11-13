package com.nlshakal.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;

public class ControllerServlet extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String action = request.getParameter("action");

    if ("clear".equals(action)) {
      request.getRequestDispatcher("/clearHistory").forward(request, response);
      return;
    }

    String xParam = request.getParameter("x");
    String yParam = request.getParameter("y");
    String rParam = request.getParameter("r");

    if (xParam != null && yParam != null && rParam != null) {
      request.getRequestDispatcher("/areaCheck").forward(request, response);
    } else {
      request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    request.getRequestDispatcher("/index.jsp").forward(request, response);
  }

  @Override
  protected void doPut(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Метод PUT не поддерживается");
  }

  @Override
  protected void doDelete(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Метод DELETE не поддерживается");
  }
}
