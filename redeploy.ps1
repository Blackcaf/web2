# Быстрое развертывание для PowerShell (Jakarta EE)
$ErrorActionPreference = "Stop"

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "   Развертывание web2 на WildFly 38" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

Set-Location "C:\Users\NLSHAKAL\Documents\ITMO\web\web2"

Write-Host "[1/3] Сборка проекта..." -ForegroundColor Yellow
& .\gradlew.bat clean war

if ($LASTEXITCODE -ne 0) {
    Write-Host "`n[✗] Ошибка сборки!" -ForegroundColor Red
    Read-Host "Нажмите Enter для выхода"
    exit 1
}

Write-Host "`n[2/3] Копирование в WildFly..." -ForegroundColor Yellow
$warFile = "build\libs\web2-1.0-SNAPSHOT.war"
$deployPath = "C:\Users\NLSHAKAL\Documents\wildfly-38.0.0.Final\standalone\deployments"

# Удаляем старые файлы
Remove-Item "$deployPath\web2-1.0-SNAPSHOT.war*" -Force -ErrorAction SilentlyContinue
Start-Sleep -Milliseconds 500

# Копируем новый WAR
Copy-Item $warFile $deployPath -Force

Write-Host "`n[3/3] Проверка..." -ForegroundColor Yellow
if (Test-Path "$deployPath\web2-1.0-SNAPSHOT.war") {
    Write-Host "[✓] WAR файл успешно скопирован" -ForegroundColor Green
} else {
    Write-Host "[✗] Ошибка копирования!" -ForegroundColor Red
    Read-Host "Нажмите Enter для выхода"
    exit 1
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "[✓] Развертывание завершено!" -ForegroundColor Green
Write-Host "========================================`n" -ForegroundColor Cyan

Write-Host "Теперь запустите WildFly командой:" -ForegroundColor Yellow
Write-Host "  cd C:\Users\NLSHAKAL\Documents\wildfly-38.0.0.Final\bin" -ForegroundColor White
Write-Host '  .\standalone.bat "-Djboss.socket.binding.port-offset=704" "-b=0.0.0.0"' -ForegroundColor White
Write-Host "`nПосле запуска откройте:" -ForegroundColor Yellow
Write-Host "  http://localhost:8784/web2-1.0-SNAPSHOT/" -ForegroundColor Cyan
Write-Host ""

