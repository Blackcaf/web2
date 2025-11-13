package com.nlshakal.servlets;

import com.nlshakal.beans.ResultBean;
import com.nlshakal.beans.ResultsStorage;
import com.nlshakal.utils.HitChecker;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

public class AreaCheckServlet extends HttpServlet {

  private final HitChecker hitChecker = new HitChecker();
  private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

  public AreaCheckServlet() {
    dateFormatter.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    long startTime = System.nanoTime();

    try {
      BigDecimal x = new BigDecimal(request.getParameter("x"));
      BigDecimal y = new BigDecimal(request.getParameter("y"));
      BigDecimal r = new BigDecimal(request.getParameter("r"));

      boolean hit = hitChecker.isHit(x, y, r);

      long executionTime = System.nanoTime() - startTime;
      String executionTimeMs = String.format("%.2f", executionTime / 1000000.0);

      String currentTime = dateFormatter.format(new Date());

      ResultBean result = new ResultBean(
          request.getParameter("x"),
          request.getParameter("y"),
          request.getParameter("r"),
          hit,
          currentTime,
          executionTimeMs
      );

      ServletContext context = getServletContext();
      ResultsStorage storage = (ResultsStorage) context.getAttribute("results");

      if (storage == null) {
        storage = new ResultsStorage();
        context.setAttribute("results", storage);
      }

      storage.addResult(result);

      request.setAttribute("result", result);
      request.getRequestDispatcher("/result.jsp").forward(request, response);

    } catch (Exception e) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      request.setAttribute("errorCode", 500);
      request.setAttribute("errorTitle", "Внутренняя ошибка сервера");
      request.setAttribute("errorMessage", e.getMessage());
      request.getRequestDispatcher("/error.jsp").forward(request, response);
    }
  }
}
