package com.nlshakal.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AccessControlFilter implements Filter {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    String uri = httpRequest.getRequestURI();
    String contextPath = httpRequest.getContextPath();
    String path = uri.substring(contextPath.length());

    if (path.startsWith("/styles.css") ||
        path.startsWith("/script.js") ||
        path.startsWith("/canvas.js") ||
        path.equals("/") ||
        path.equals("/index.jsp") ||
        path.equals("/controller") ||
        path.equals("/clearHistory") ||
        path.equals("/weather")) {
      chain.doFilter(request, response);
      return;
    }

    sendError(httpRequest, httpResponse, 404, "Страница не найдена",
             "Запрашиваемый ресурс не найден");
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
