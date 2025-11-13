<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Лабораторная работа №2</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
<header class="header">
    <div class="header-content">
        <div class="header-left">
            <h1 class="main-title">Лабораторная работа №2</h1>
            <p class="subtitle">Проверка попадания точки в область</p>
        </div>
        <div class="student-info">
            <div class="info-card">
                <p><span class="info-label">ФИО:</span> Мантуш Даниил Валерьевич</p>
                <p><span class="info-label">Группа:</span> P3219</p>
                <p><span class="info-label">Вариант:</span> 434931</p>
            </div>
        </div>
    </div>
</header>

<main class="main-container">
    <section class="input-section ios-card">
        <h2 class="section-title">Введите координаты точки</h2>
        <form id="coordinateForm" action="${pageContext.request.contextPath}/controller"
              method="POST" class="coordinate-form">

            <div class="form-group">
                <label class="form-label">Координата X:</label>
                <div class="r-radios">
                    <label><input type="radio" name="x" value="-5" required> -5</label>
                    <label><input type="radio" name="x" value="-4"> -4</label>
                    <label><input type="radio" name="x" value="-3"> -3</label>
                    <label><input type="radio" name="x" value="-2"> -2</label>
                    <label><input type="radio" name="x" value="-1"> -1</label>
                    <label><input type="radio" name="x" value="0"> 0</label>
                    <label><input type="radio" name="x" value="1"> 1</label>
                    <label><input type="radio" name="x" value="2"> 2</label>
                    <label><input type="radio" name="x" value="3"> 3</label>
                </div>
                <div class="error-message" id="x-error"></div>
            </div>

            <div class="form-group">
                <label for="y-input" class="form-label">Координата Y:</label>
                <div class="input-group">
                    <input type="text" id="y-input" name="y" class="ios-input"
                           placeholder="от -3 до 3" required>
                    <span class="input-hint">(-3 ... 3)</span>
                </div>
                <div class="error-message" id="y-error"></div>
            </div>

            <div class="form-group">
                <label class="form-label">Параметр R:</label>
                <input type="hidden" id="r-hidden" name="r" value="">
                <div class="r-buttons">
                    <button type="button" class="r-btn" data-value="1">1</button>
                    <button type="button" class="r-btn" data-value="2">2</button>
                    <button type="button" class="r-btn" data-value="3">3</button>
                    <button type="button" class="r-btn" data-value="4">4</button>
                    <button type="button" class="r-btn" data-value="5">5</button>
                </div>
                <div class="error-message" id="r-error"></div>
            </div>

            <button type="submit" class="submit-btn">Проверить точку</button>
        </form>
    </section>

    <section class="canvas-section ios-card">
        <h2 class="section-title">Координатная плоскость</h2>
        <div class="canvas-container">
            <div class="canvas-wrapper">
                <canvas id="coordinatePlane" width="600" height="600"></canvas>
            </div>
            <div class="canvas-info">
                <p class="canvas-hint">Кликните на область для добавления точки</p>
                <p class="current-r-display">
                    Текущий R: <span id="current-r" class="r-value">-</span>
                </p>
            </div>
        </div>
    </section>

    <section class="results-section ios-card">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
            <h2 class="section-title" style="margin: 0;">Результаты проверки</h2>
            <form action="${pageContext.request.contextPath}/controller" method="POST" style="margin: 0;">
                <input type="hidden" name="action" value="clear">
                <button type="submit" class="clear-btn" onclick="return confirm('Вы уверены, что хотите очистить всю историю?')">
                    🗑️ Очистить историю
                </button>
            </form>
        </div>
        <div class="table-container">
            <table class="results-table">
                <thead>
                <tr>
                    <th>X</th>
                    <th>Y</th>
                    <th>R</th>
                    <th>Результат</th>
                    <th>Время сервера</th>
                    <th>Время выполнения</th>
                </tr>
                </thead>
                <tbody id="results-body">
                <c:forEach items="${applicationScope.results.results}" var="result">
                    <tr class="${result.hit ? 'hit' : 'miss'}">
                        <td>${result.x}</td>
                        <td title="${result.y}">${result.y.length() > 8 ? result.y.substring(0, 8).concat('...') : result.y}</td>
                        <td>${result.r}</td>
                        <td class="result-${result.hit ? 'hit' : 'miss'}">
                                ${result.hit ? 'Попадание' : 'Промах'}
                        </td>
                        <td class="time-cell">${result.currentTime}</td>
                        <td>${result.executionTime} мс</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <div id="empty-state" class="empty-state" style="display: ${empty applicationScope.results.results ? 'block' : 'none'}">
                <p>Пока нет результатов</p>
                <small>Добавьте точку для начала работы</small>
            </div>
        </div>
    </section>
</main>

<div id="error-modal" class="modal">
    <div class="modal-backdrop"></div>
    <div class="modal-content">
        <div class="modal-header">
            <h3 class="modal-title">Ошибка</h3>
            <button class="close-btn" type="button">×</button>
        </div>
        <div class="modal-body">
            <p id="error-text"></p>
        </div>
    </div>
</div>

<script>
    window.applicationResults = [
        <c:forEach items="${applicationScope.results.results}" var="result" varStatus="status">
        {
            x: "${result.x}",
            y: "${result.y}",
            r: "${result.r}",
            hit: ${result.hit}
        }<c:if test="${!status.last}">,</c:if>
        </c:forEach>
    ];
</script>
<script src="script.js"></script>
<script src="canvas.js"></script>
</body>
</html>
