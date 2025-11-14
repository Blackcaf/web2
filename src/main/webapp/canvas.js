let canvas, ctx;
const CONFIG = {
    size: 600,
    center: 300,
    scale: 45
};

const WIND_DIRECTIONS = {
    'С': { dx: 0, dy: 1 },      // Север
    'Ю': { dx: 0, dy: -1 },     // Юг
    'В': { dx: 1, dy: 0 },      // Восток
    'З': { dx: -1, dy: 0 },     // Запад
    'С-В': { dx: 0.707, dy: 0.707 },   // Северо-Восток
    'С-З': { dx: -0.707, dy: 0.707 },  // Северо-Запад
    'Ю-В': { dx: 0.707, dy: -0.707 },  // Юго-Восток
    'Ю-З': { dx: -0.707, dy: -0.707 }  // Юго-Запад
};

let cachedWindData = null;

document.addEventListener('DOMContentLoaded', () => {
    setTimeout(initializeCanvas, 200);
    updateWindData();
    setInterval(updateWindData, 5000);
});

async function updateWindData() {
    try {
        const response = await fetch('/web2-1.0-SNAPSHOT/weather');
        const data = await response.json();

        if (data.success) {
            cachedWindData = {
                speed: data.speed,
                direction: data.direction
            };
            displayWindInfo(data.speed, data.direction);
        } else {
            console.error('Ошибка получения данных о погоде:', data.error);
            displayWindError();
        }
    } catch (error) {
        console.error('Ошибка при запросе погоды:', error);
        displayWindError();
    }
}

function displayWindError() {
    let windInfoElement = document.getElementById('windInfo');

    if (!windInfoElement) {
        windInfoElement = document.createElement('div');
        windInfoElement.id = 'windInfo';
        windInfoElement.style.cssText = `
            position: fixed;
            top: 80px;
            right: 20px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 15px 20px;
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            font-family: Arial, sans-serif;
            z-index: 1000;
            min-width: 200px;
        `;
        document.body.appendChild(windInfoElement);
    }

    windInfoElement.innerHTML = `
        <div style="font-size: 16px; font-weight: bold; margin-bottom: 8px;">
            Текущий ветер
        </div>
        <div style="font-size: 14px; color: #ffcccb;">
            Не удалось получить данные
        </div>
    `;
}

function initializeCanvas() {
    canvas = document.getElementById('coordinatePlane');
    if (!canvas || !(ctx = canvas.getContext('2d'))) {
        console.error('Canvas initialization failed');
        return;
    }
    canvas.addEventListener('click', handleCanvasClick);
    drawCoordinatePlane();
}

function drawCoordinatePlane() {
    if (!ctx) return;

    ctx.clearRect(0, 0, CONFIG.size, CONFIG.size);
    ctx.fillStyle = 'white';
    ctx.fillRect(0, 0, CONFIG.size, CONFIG.size);

    const r = window.currentR?.() || 1;

    if (r > 0) drawAreas(r);

    drawAxes();

    drawScale();

    drawAllPoints();
}

function drawAxes() {
    ctx.save();
    ctx.strokeStyle = '#333';
    ctx.lineWidth = 2;

    ctx.beginPath();
    ctx.moveTo(20, CONFIG.center);
    ctx.lineTo(CONFIG.size - 20, CONFIG.center);
    ctx.stroke();

    ctx.beginPath();
    ctx.moveTo(CONFIG.center, 20);
    ctx.lineTo(CONFIG.center, CONFIG.size - 20);
    ctx.stroke();

    ctx.fillStyle = '#333';

    ctx.beginPath();
    ctx.moveTo(CONFIG.size - 20, CONFIG.center);
    ctx.lineTo(CONFIG.size - 30, CONFIG.center - 5);
    ctx.lineTo(CONFIG.size - 30, CONFIG.center + 5);
    ctx.closePath();
    ctx.fill();

    ctx.beginPath();
    ctx.moveTo(CONFIG.center, 20);
    ctx.lineTo(CONFIG.center - 5, 30);
    ctx.lineTo(CONFIG.center + 5, 30);
    ctx.closePath();
    ctx.fill();

    ctx.restore();
}

function drawAreas(r) {
    const rPx = r * CONFIG.scale;  // R в пикселях
    const halfR = rPx / 2;

    ctx.save();
    ctx.globalAlpha = 0.5;

    ctx.fillStyle = '#2196F3';
    ctx.fillRect(
        CONFIG.center - halfR,
        CONFIG.center - rPx,
        halfR,
        rPx
    );

    ctx.fillStyle = '#4CAF50';
    ctx.beginPath();
    ctx.moveTo(CONFIG.center, CONFIG.center);
    ctx.lineTo(CONFIG.center + halfR, CONFIG.center);
    ctx.lineTo(CONFIG.center, CONFIG.center - halfR);
    ctx.closePath();
    ctx.fill();

    ctx.fillStyle = '#FF9800';
    ctx.beginPath();
    ctx.arc(CONFIG.center, CONFIG.center, rPx, 0, Math.PI / 2);
    ctx.lineTo(CONFIG.center, CONFIG.center);
    ctx.closePath();
    ctx.fill();

    ctx.restore();
}

function drawScale() {
    ctx.save();
    ctx.fillStyle = '#333';
    ctx.font = '14px Arial';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';

    for (let i = -5; i <= 5; i += 1) {
        if (i === 0) continue;

        const px = i * CONFIG.scale;

        const x = CONFIG.center + px;
        if (x >= 50 && x <= CONFIG.size - 50) {
            ctx.fillText(i.toString(), x, CONFIG.center + 25);
            ctx.strokeStyle = '#666';
            ctx.lineWidth = 1;
            ctx.beginPath();
            ctx.moveTo(x, CONFIG.center - 5);
            ctx.lineTo(x, CONFIG.center + 5);
            ctx.stroke();
        }

        const y = CONFIG.center - px;
        if (y >= 50 && y <= CONFIG.size - 50) {
            ctx.fillText(i.toString(), CONFIG.center - 35, y);
            ctx.strokeStyle = '#666';
            ctx.lineWidth = 1;
            ctx.beginPath();
            ctx.moveTo(CONFIG.center - 5, y);
            ctx.lineTo(CONFIG.center + 5, y);
            ctx.stroke();
        }
    }

    ctx.font = 'bold 16px Arial';
    ctx.fillText('X', CONFIG.size - 30, CONFIG.center - 20);
    ctx.fillText('Y', CONFIG.center + 20, 30);
    ctx.fillText('0', CONFIG.center - 20, CONFIG.center + 20);

    ctx.restore();
}

function drawAllPoints() {
    const results = window.applicationResults || [];
    const currentR = window.currentR?.();

    results.forEach(result => {
        const x = parseFloat(result.x);
        const y = parseFloat(result.y);

        if (typeof x === 'number' && typeof y === 'number') {
            const hit = currentR ? checkHit(x, y, currentR) : result.hit;
            drawPoint(x, y, hit);
        }
    });
}

function checkHit(x, y, r) {
    if (x >= -r/2 && x <= 0 && y >= 0 && y <= r) {
        return true;
    }
    if (x >= 0 && y >= 0 && y <= -x + r/2) {
        return true;
    }
    if (x >= 0 && y <= 0 && (x*x + y*y) <= r*r) {
        return true;
    }
    return false;
}

function drawPoint(x, y, hit) {
    const pixelX = CONFIG.center + x * CONFIG.scale;
    const pixelY = CONFIG.center - y * CONFIG.scale;

    ctx.save();
    ctx.fillStyle = hit ? '#4CAF50' : '#f44336';
    ctx.beginPath();
    ctx.arc(pixelX, pixelY, 5, 0, 2 * Math.PI);
    ctx.fill();
    ctx.strokeStyle = '#fff';
    ctx.lineWidth = 2;
    ctx.stroke();
    ctx.restore();
}

function handleCanvasClick(event) {
    if (!canvas) return;

    const r = window.currentR?.();
    if (!r) {
        window.showModal?.('Ошибка', 'Сначала выберите значение R');
        return;
    }

    const rect = canvas.getBoundingClientRect();
    const scaleX = canvas.width / rect.width;
    const scaleY = canvas.height / rect.height;

    const canvasX = (event.clientX - rect.left) * scaleX;
    const canvasY = (event.clientY - rect.top) * scaleY;

    const mathX = (canvasX - CONFIG.center) / CONFIG.scale;
    const mathY = -(canvasY - CONFIG.center) / CONFIG.scale;

    if (mathX < -5 || mathX > 3) {
        window.showModal?.('Ошибка', 'X должен быть в диапазоне [-5; 3]');
        return;
    }

    if (mathY <= -3 || mathY >= 3) {
        window.showModal?.('Ошибка', 'Y должен быть в интервале (-3; 3)');
        return;
    }

    applyWindOffset(mathX, mathY, r);
}

async function applyWindOffset(x, y, r) {
    if (cachedWindData && cachedWindData.speed && cachedWindData.direction) {
        const speed = cachedWindData.speed;
        const direction = cachedWindData.direction;

        const windVector = WIND_DIRECTIONS[direction];

        if (windVector) {
            const offsetPixelsX = windVector.dx * speed * 10;
            const offsetPixelsY = windVector.dy * speed * 10;

            const offsetX = offsetPixelsX / CONFIG.scale;
            const offsetY = offsetPixelsY / CONFIG.scale;

            const newX = x + offsetX;
            const newY = y + offsetY;

            console.log(`Ветер: ${speed} м/с, направление: ${direction}`);
            console.log(`Исходная точка: (${x.toFixed(2)}, ${y.toFixed(2)})`);
            console.log(`Смещенная точка: (${newX.toFixed(2)}, ${newY.toFixed(2)})`);
            console.log(`Смещение: (${offsetX.toFixed(2)}, ${offsetY.toFixed(2)})`);

            visualizeWindShift(x, y, newX, newY);

            submitPoint(newX, newY, r);
        } else {
            console.warn('Неизвестное направление ветра:', direction);
            submitPoint(x, y, r);
        }
    } else {
        console.warn('Данные о ветре недоступны, отправка без смещения');
        submitPoint(x, y, r);
    }
}

function displayWindInfo(speed, direction) {
    let windInfoElement = document.getElementById('windInfo');

    if (!windInfoElement) {
        windInfoElement = document.createElement('div');
        windInfoElement.id = 'windInfo';
        windInfoElement.style.cssText = `
            position: fixed;
            top: 80px;
            right: 20px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 15px 20px;
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            font-family: Arial, sans-serif;
            z-index: 1000;
            min-width: 200px;
            transition: all 0.3s ease;
        `;
        document.body.appendChild(windInfoElement);
    }

    const directionNames = {
        'С': 'Север',
        'Ю': 'Юг',
        'В': 'Восток',
        'З': 'Запад',
        'С-В': 'Северо-Восток',
        'С-З': 'Северо-Запад',
        'Ю-В': 'Юго-Восток',
        'Ю-З': 'Юго-Запад'
    };

    const directionName = directionNames[direction] || direction;
    const offset = (speed * 10 / CONFIG.scale).toFixed(2);

    const now = new Date();
    const timeStr = now.toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit', second: '2-digit' });

    windInfoElement.innerHTML = `
        <div style="font-size: 16px; font-weight: bold; margin-bottom: 8px;">
            Текущий ветер
        </div>
        <div style="font-size: 14px; margin-bottom: 5px;">
            <strong>Скорость:</strong> ${speed} м/с
        </div>
        <div style="font-size: 14px; margin-bottom: 5px;">
            <strong>Направление:</strong> ${directionName}
        </div>
        <div style="font-size: 14px; margin-top: 10px; padding-top: 10px; border-top: 1px solid rgba(255,255,255,0.3);">
            <strong>Смещение точки:</strong><br>
            ≈ ${offset} единиц на графике
        </div>
        <div style="font-size: 11px; margin-top: 8px; opacity: 0.7;">
            Обновлено: ${timeStr}
        </div>
    `;
}

function visualizeWindShift(x1, y1, x2, y2) {
    const pixelX1 = CONFIG.center + x1 * CONFIG.scale;
    const pixelY1 = CONFIG.center - y1 * CONFIG.scale;
    const pixelX2 = CONFIG.center + x2 * CONFIG.scale;
    const pixelY2 = CONFIG.center - y2 * CONFIG.scale;

    ctx.save();

    ctx.fillStyle = '#2196F3';
    ctx.beginPath();
    ctx.arc(pixelX1, pixelY1, 4, 0, 2 * Math.PI);
    ctx.fill();

    ctx.strokeStyle = '#FF5722';
    ctx.lineWidth = 2;
    ctx.setLineDash([5, 5]);
    ctx.beginPath();
    ctx.moveTo(pixelX1, pixelY1);
    ctx.lineTo(pixelX2, pixelY2);
    ctx.stroke();

    const angle = Math.atan2(pixelY2 - pixelY1, pixelX2 - pixelX1);
    const arrowLength = 10;

    ctx.setLineDash([]);
    ctx.fillStyle = '#FF5722';
    ctx.beginPath();
    ctx.moveTo(pixelX2, pixelY2);
    ctx.lineTo(
        pixelX2 - arrowLength * Math.cos(angle - Math.PI / 6),
        pixelY2 - arrowLength * Math.sin(angle - Math.PI / 6)
    );
    ctx.lineTo(
        pixelX2 - arrowLength * Math.cos(angle + Math.PI / 6),
        pixelY2 - arrowLength * Math.sin(angle + Math.PI / 6)
    );
    ctx.closePath();
    ctx.fill();

    ctx.restore();
}

function submitPoint(x, y, r) {
    const form = document.getElementById('coordinateForm');
    if (!form) return;

    const tempForm = document.createElement('form');
    tempForm.method = 'POST';
    tempForm.action = form.action;

    const xInput = document.createElement('input');
    xInput.type = 'hidden';
    xInput.name = 'x';
    xInput.value = x.toString();

    const yInput = document.createElement('input');
    yInput.type = 'hidden';
    yInput.name = 'y';
    yInput.value = y.toString();

    const rInput = document.createElement('input');
    rInput.type = 'hidden';
    rInput.name = 'r';
    rInput.value = r;

    tempForm.appendChild(xInput);
    tempForm.appendChild(yInput);
    tempForm.appendChild(rInput);
    document.body.appendChild(tempForm);
    tempForm.submit();
}

window.drawCoordinatePlane = drawCoordinatePlane;
