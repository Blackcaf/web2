package com.nlshakal.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MethodFilter implements Filter {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    String method = httpRequest.getMethod();
    String uri = httpRequest.getRequestURI();

    if (uri.contains("/areaCheck") || uri.contains("/clearHistory")) {
      if (!"POST".equalsIgnoreCase(method)) {
        sendError(httpRequest, httpResponse, 405, "Метод не разрешен",
                 "Разрешен только метод POST. " + method + " запросы не поддерживаются.");
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
