<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if ("GET".equalsIgnoreCase(request.getMethod()) && request.getAttribute("errorCode") == null) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Прямой доступ запрещен");
        return;
    }
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ошибка</title>
    <link rel="stylesheet" href="styles.css">
    <style>
        .error-container {
            max-width: 600px;
            margin: 50px auto;
            padding: 30px;
            background: white;
            border-radius: 10px;
            box-shadow: 0 0 20px rgba(0,0,0,0.1);
            text-align: center;
        }
        .error-icon {
            font-size: 60px;
            color: #e74c3c;
            margin-bottom: 20px;
        }
        .error-title {
            font-size: 24px;
            color: #e74c3c;
            margin-bottom: 15px;
        }
        .error-message {
            font-size: 16px;
            color: #555;
            margin-bottom: 30px;
            line-height: 1.6;
        }
        .error-code {
            font-size: 48px;
            font-weight: bold;
            color: #e74c3c;
            margin-bottom: 10px;
        }
        .back-button {
            display: inline-block;
            padding: 12px 30px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            text-decoration: none;
            border-radius: 25px;
            font-weight: 500;
            transition: transform 0.2s;
        }
        .back-button:hover {
            transform: translateY(-2px);
        }
    </style>
</head>
<body>
    <div class="error-container">
        <div class="error-icon">⚠️</div>
        <div class="error-code">${errorCode != null ? errorCode : '400'}</div>
        <div class="error-title">${errorTitle != null ? errorTitle : 'Произошла ошибка'}</div>
        <div class="error-message">${errorMessage != null ? errorMessage : 'Неизвестная ошибка'}</div>
        <a href="${pageContext.request.contextPath}/index.jsp" class="back-button">Вернуться на главную</a>
    </div>
</body>
</html>
