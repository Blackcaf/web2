package com.nlshakal.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class AccessControlFilter implements Filter {

  private Set<String> allowedPaths;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    allowedPaths = new HashSet<>();
    String allowedPathsParam = filterConfig.getInitParameter("allowedPaths");

    if (allowedPathsParam != null && !allowedPathsParam.isEmpty()) {
      String[] paths = allowedPathsParam.split(",");
      for (String path : paths) {
        allowedPaths.add(path.trim());
      }
    }
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    String uri = httpRequest.getRequestURI();
    String contextPath = httpRequest.getContextPath();
    String path = uri.substring(contextPath.length());

    boolean isAllowed = false;
    for (String allowedPath : allowedPaths) {
      if (path.equals(allowedPath) ||
          (allowedPath.endsWith(".css") && path.startsWith(allowedPath.replace(".css", "")) && path.endsWith(".css")) ||
          (allowedPath.endsWith(".js") && path.startsWith(allowedPath.replace(".js", "")) && path.endsWith(".js"))) {
        isAllowed = true;
        break;
      }
    }

    if (isAllowed) {
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
  public void destroy() {}
}
