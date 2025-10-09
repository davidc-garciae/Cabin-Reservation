# Script para generar documentación estática de la API
# Requiere: redoc-cli o swagger-codegen instalado

param(
    [Parameter(Mandatory = $false)]
    [ValidateSet("html", "pdf", "markdown")]
    [string]$Format = "html",
    
    [string]$OutputDir = "docs/generated"
)

# Función para verificar si un comando existe
function Test-Command {
    param([string]$Command)
    try {
        $null = Get-Command $Command -ErrorAction Stop
        return $true
    }
    catch {
        return $false
    }
}

# Crear directorio de salida si no existe
if (!(Test-Path $OutputDir)) {
    New-Item -ItemType Directory -Path $OutputDir -Force
    Write-Host "✅ Directorio de salida creado: $OutputDir" -ForegroundColor Green
}

Write-Host "🚀 Generando documentación de la API..." -ForegroundColor Cyan
Write-Host "📊 Formato: $Format" -ForegroundColor Gray
Write-Host "📁 Directorio de salida: $OutputDir" -ForegroundColor Gray

switch ($Format) {
    "html" {
        if (Test-Command "redoc-cli") {
            Write-Host "🔄 Generando documentación HTML con Redoc..." -ForegroundColor Yellow
            redoc-cli build "docs/openapi.yaml" --output "$OutputDir/index.html" --title "Cabin Reservation API v2.0"
            Write-Host "✅ Documentación HTML generada: $OutputDir/index.html" -ForegroundColor Green
        }
        elseif (Test-Command "swagger-codegen") {
            Write-Host "🔄 Generando documentación HTML con Swagger Codegen..." -ForegroundColor Yellow
            swagger-codegen generate -i "docs/openapi.yaml" -l html2 -o $OutputDir
            Write-Host "✅ Documentación HTML generada en: $OutputDir" -ForegroundColor Green
        }
        else {
            Write-Host "❌ Error: redoc-cli o swagger-codegen no están instalados" -ForegroundColor Red
            Write-Host "💡 Instalar con: npm install -g redoc-cli" -ForegroundColor Yellow
            exit 1
        }
    }
    
    "pdf" {
        if (Test-Command "redoc-cli") {
            Write-Host "🔄 Generando documentación PDF con Redoc..." -ForegroundColor Yellow
            redoc-cli build "docs/openapi.yaml" --output "$OutputDir/api-docs.pdf" --format pdf
            Write-Host "✅ Documentación PDF generada: $OutputDir/api-docs.pdf" -ForegroundColor Green
        }
        else {
            Write-Host "❌ Error: redoc-cli no está instalado" -ForegroundColor Red
            Write-Host "💡 Instalar con: npm install -g redoc-cli" -ForegroundColor Yellow
            exit 1
        }
    }
    
    "markdown" {
        if (Test-Command "swagger-codegen") {
            Write-Host "🔄 Generando documentación Markdown con Swagger Codegen..." -ForegroundColor Yellow
            swagger-codegen generate -i "docs/openapi.yaml" -l markdown -o $OutputDir
            Write-Host "✅ Documentación Markdown generada en: $OutputDir" -ForegroundColor Green
        }
        else {
            Write-Host "❌ Error: swagger-codegen no está instalado" -ForegroundColor Red
            Write-Host "💡 Instalar con: npm install -g @apidevtools/swagger-cli" -ForegroundColor Yellow
            exit 1
        }
    }
}

Write-Host "`n🎉 Documentación generada exitosamente!" -ForegroundColor Green
Write-Host "📖 Para ver la documentación:" -ForegroundColor Cyan

switch ($Format) {
    "html" {
        Write-Host "   Abrir: $OutputDir/index.html en tu navegador" -ForegroundColor White
    }
    "pdf" {
        Write-Host "   Abrir: $OutputDir/api-docs.pdf con tu lector de PDF" -ForegroundColor White
    }
    "markdown" {
        Write-Host "   Ver archivos .md en: $OutputDir" -ForegroundColor White
    }
}

Write-Host "`n💡 Alternativamente, usar Swagger UI en: http://localhost:8080/swagger-ui.html" -ForegroundColor Yellow
