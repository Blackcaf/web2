package com.nlshakal.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class ValidationFilter implements Filter {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    String uri = httpRequest.getRequestURI();

    if ("POST".equalsIgnoreCase(httpRequest.getMethod()) &&
        (uri.contains("/controller") || uri.contains("/areaCheck"))) {

      String action = httpRequest.getParameter("action");

      if ("clear".equals(action)) {
        chain.doFilter(request, response);
        return;
      }

      try {
        Map<String, String[]> paramMap = httpRequest.getParameterMap();
        Set<String> allowedParams = new HashSet<>(Arrays.asList("x", "y", "r"));

        for (String paramName : paramMap.keySet()) {
          if (!allowedParams.contains(paramName)) {
            sendError(httpRequest, httpResponse, 400, "Некорректные данные",
                     "Передан неизвестный параметр: " + paramName + ". Допустимы только: x, y, r");
            return;
          }
        }

        for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
          if (entry.getValue().length > 1) {
            sendError(httpRequest, httpResponse, 400, "Некорректные данные",
                     "Параметр " + entry.getKey() + " передан " + entry.getValue().length + " раза. Должен быть передан только один раз");
            return;
          }
        }

        String xParam = httpRequest.getParameter("x");
        String yParam = httpRequest.getParameter("y");
        String rParam = httpRequest.getParameter("r");

        if (xParam == null || yParam == null || rParam == null) {
          List<String> missing = new ArrayList<>();
          if (xParam == null) missing.add("x");
          if (yParam == null) missing.add("y");
          if (rParam == null) missing.add("r");
          sendError(httpRequest, httpResponse, 400, "Некорректные данные",
                   "Отсутствуют обязательные параметры: " + String.join(", ", missing));
          return;
        }

        String xStr = xParam.replace(',', '.').trim();
        String yStr = yParam.replace(',', '.').trim();
        String rStr = rParam.replace(',', '.').trim();

        if (!xStr.matches("^-?\\d*\\.?\\d+$") || xStr.length() > 100) {
          sendError(httpRequest, httpResponse, 400, "Некорректные данные",
                   "X должен быть числом (макс. 100 символов)");
          return;
        }
        if (!yStr.matches("^-?\\d*\\.?\\d+$") || yStr.length() > 100) {
          sendError(httpRequest, httpResponse, 400, "Некорректные данные",
                   "Y должен быть числом (макс. 100 символов)");
          return;
        }
        if (!rStr.matches("^-?\\d*\\.?\\d+$") || rStr.length() > 100) {
          sendError(httpRequest, httpResponse, 400, "Некорректные данные",
                   "R должен быть числом (макс. 100 символов)");
          return;
        }

        BigDecimal x = new BigDecimal(xStr);
        BigDecimal y = new BigDecimal(yStr);
        BigDecimal r = new BigDecimal(rStr);

        if (x.compareTo(new BigDecimal("-5")) < 0 || x.compareTo(new BigDecimal("3")) > 0) {
          sendError(httpRequest, httpResponse, 400, "Некорректные данные",
                   "X должен быть в диапазоне [-5; 3]");
          return;
        }

        if (y.compareTo(new BigDecimal("-3")) <= 0 || y.compareTo(new BigDecimal("3")) >= 0) {
          sendError(httpRequest, httpResponse, 400, "Некорректные данные",
                   "Y должен быть числом в интервале (-3; 3)");
          return;
        }

        if (r.compareTo(new BigDecimal("1")) < 0 || r.compareTo(new BigDecimal("5")) > 0) {
          sendError(httpRequest, httpResponse, 400, "Некорректные данные",
                   "R должен быть в диапазоне [1; 5]");
          return;
        }

      } catch (NumberFormatException e) {
        sendError(httpRequest, httpResponse, 400, "Некорректные данные", "Неверный формат числа");
        return;
      } catch (Exception e) {
        sendError(httpRequest, httpResponse, 500, "Внутренняя ошибка сервера", e.getMessage());
        return;
      }
    }

    chain.doFilter(request, response);
  }

  private void sendError(HttpServletRequest request, HttpServletResponse response,
                        int errorCode, String errorTitle, String errorMessage)
      throws ServletException, IOException {
    response.setStatus(errorCode);
    request.setAttribute("errorCode", errorCode);
    request.setAttribute("errorTitle", errorTitle);
    request.setAttribute("errorMessage", errorMessage);
    request.getRequestDispatcher("/error.jsp").forward(request, response);
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {}

  @Override
  public void destroy() {}
}
