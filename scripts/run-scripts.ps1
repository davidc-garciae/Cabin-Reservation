# Script de PowerShell para ejecutar los scripts de base de datos
# Configuración de conexión a la base de datos

param(
    [Parameter(Mandatory = $true)]
    [ValidateSet("cleanup", "insert", "configs", "migrate", "test-documents", "all")]
    [string]$Action,
    
    [string]$Host = "localhost",
    [string]$Port = "5433", 
    [string]$Database = "cabin-reservation",
    [string]$Username = "postgres",
    [string]$Password = "root"
)

# Función para ejecutar script SQL
function Invoke-SqlScript {
    param(
        [string]$ScriptPath,
        [string]$Description
    )
    
    Write-Host "🔄 Ejecutando: $Description" -ForegroundColor Yellow
    
    try {
        # Construir comando psql
        $env:PGPASSWORD = $Password
        $psqlCmd = "psql -h $Host -p $Port -U $Username -d $Database -f `"$ScriptPath`""
        
        # Ejecutar comando
        Invoke-Expression $psqlCmd
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✅ $Description completado exitosamente" -ForegroundColor Green
        }
        else {
            Write-Host "❌ Error ejecutando $Description" -ForegroundColor Red
            return $false
        }
    }
    catch {
        Write-Host "❌ Error: $($_.Exception.Message)" -ForegroundColor Red
        return $false
    }
    finally {
        Remove-Item Env:PGPASSWORD -ErrorAction SilentlyContinue
    }
    
    return $true
}

# Verificar que psql esté disponible
try {
    $null = Get-Command psql -ErrorAction Stop
}
catch {
    Write-Host "❌ Error: psql no está disponible. Asegúrate de tener PostgreSQL instalado y en el PATH." -ForegroundColor Red
    exit 1
}

# Obtener directorio del script
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path

Write-Host "🚀 Iniciando gestión de base de datos..." -ForegroundColor Cyan
Write-Host "📊 Base de datos: $Database en $Host:$Port" -ForegroundColor Gray
Write-Host "👤 Usuario: $Username" -ForegroundColor Gray

switch ($Action) {
    "cleanup" {
        Write-Host "`n⚠️  ADVERTENCIA: Esto eliminará TODOS los datos de la base de datos!" -ForegroundColor Red
        $confirm = Read-Host "¿Estás seguro? (escribe 'SI' para continuar)"
        
        if ($confirm -eq "SI") {
            $scriptPath = Join-Path $ScriptDir "cleanup-database.sql"
            Invoke-SqlScript -ScriptPath $scriptPath -Description "Limpieza de base de datos"
        }
        else {
            Write-Host "❌ Operación cancelada por el usuario" -ForegroundColor Yellow
        }
    }
    
    "insert" {
        $scriptPath = Join-Path $ScriptDir "insert-test-data.sql"
        Invoke-SqlScript -ScriptPath $scriptPath -Description "Inserción de datos de prueba"
    }
    
    "configs" {
        $scriptPath = Join-Path $ScriptDir "insert-system-configs-only.sql"
        Invoke-SqlScript -ScriptPath $scriptPath -Description "Inserción de configuraciones del sistema"
    }
    
    "migrate" {
        $scriptPath = Join-Path $ScriptDir "migration_add_checkin_checkout_times.sql"
        Invoke-SqlScript -ScriptPath $scriptPath -Description "Migración de horarios de check-in/check-out"
    }
    
    "test-documents" {
        $scriptPath = Join-Path $ScriptDir "insert-test-documents.sql"
        Invoke-SqlScript -ScriptPath $scriptPath -Description "Inserción de documentos de prueba"
    }
    
    # Limpiar
    $cleanupScript = Join-Path $ScriptDir "cleanup-database.sql"
    if (Invoke-SqlScript -ScriptPath $cleanupScript -Description "Limpieza de base de datos") {
        # Insertar datos
        $insertScript = Join-Path $ScriptDir "insert-test-data.sql"
        Invoke-SqlScript -ScriptPath $insertScript -Description "Inserción de datos de prueba"
    }
}
else {
    Write-Host "❌ Operación cancelada por el usuario" -ForegroundColor Yellow
}
}
}

Write-Host "`n🏁 Proceso completado!" -ForegroundColor Green
        }
    }
}

Write-Host "`n🏁 Proceso completado!" -ForegroundColor Green
