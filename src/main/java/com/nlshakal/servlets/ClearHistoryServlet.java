package com.nlshakal.servlets;

import com.nlshakal.beans.ResultsStorage;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;

public class ClearHistoryServlet extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    HttpSession session = request.getSession();
    ResultsStorage storage = (ResultsStorage) session.getAttribute("results");

    if (storage != null) {
      storage.clear();
    }

    response.sendRedirect(request.getContextPath() + "/index.jsp");
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    request.setAttribute("errorCode", 405);
    request.setAttribute("errorTitle", "Метод не разрешен");
    request.setAttribute("errorMessage", "Разрешен только метод POST для очистки истории.");
    request.getRequestDispatcher("/error.jsp").forward(request, response);
  }
}
