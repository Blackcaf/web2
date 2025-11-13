<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Результат проверки</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
<header class="header">
    <div class="header-content">
        <div class="header-left">
            <h1 class="main-title">Результат проверки точки</h1>
        </div>
    </div>
</header>

<main class="main-container">
    <section class="ios-card">
        <h2 class="section-title">Полученные параметры</h2>
        <table class="results-table">
            <thead>
            <tr>
                <th>Параметр</th>
                <th>Значение</th>
            </tr>
            </thead>
            <tbody>
            <tr>
                <td>X</td>
                <td>${result.x}</td>
            </tr>
            <tr>
                <td>Y</td>
                <td>${result.y}</td>
            </tr>
            <tr>
                <td>R</td>
                <td>${result.r}</td>
            </tr>
            <tr>
                <td>Результат</td>
                <td class="result-${result.hit ? 'hit' : 'miss'}">
                    <strong>${result.hit ? '✓ Точка попадает в область' : '✗ Точка НЕ попадает в область'}</strong>
                </td>
            </tr>
            <tr>
                <td>Время выполнения</td>
                <td>${result.executionTime} мс</td>
            </tr>
            </tbody>
        </table>

        <div style="margin-top: 30px; text-align: center;">
            <a href="${pageContext.request.contextPath}/controller" class="submit-btn" style="display: inline-block; text-decoration: none;">
                ← Вернуться к форме
            </a>
        </div>
    </section>
</main>
</body>
</html>

