# Carga variables desde .env y levanta la app con el Maven wrapper.
$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot

if (-not (Test-Path .env)) { throw "Falta .env (copia .env.example a .env y completa los valores)" }

Get-Content .env | Where-Object { $_ -match '^\s*[^#\s][^=]*=' } | ForEach-Object {
    $name, $value = $_ -split '=', 2
    [Environment]::SetEnvironmentVariable($name.Trim(), $value.Trim(), 'Process')
}

foreach ($v in 'DB_HOST', 'DB_NAME', 'DB_USER', 'DB_PASSWORD') {
    if (-not [Environment]::GetEnvironmentVariable($v)) { throw "Variable $v vacia en .env" }
}

.\mvnw.cmd spring-boot:run
