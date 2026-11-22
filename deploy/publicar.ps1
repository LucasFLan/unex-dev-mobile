<#
.SYNOPSIS
  Gera e publica no Docker Hub as imagens do back e do front com uma versão (tag) real.

.EXAMPLE
  .\deploy\publicar.ps1 -Versao 1.0.1

  Publica cesinha012642/chamaai-backend:1.0.1 e cesinha012642/chamaai-frontend:1.0.1 e move a tag
  "latest" para essa versão. Depois, no Portainer, ajuste TAG=1.0.1 na stack e faça "Update the stack".

  Use versionamento semântico: MAIOR.MENOR.CORREÇÃO (ex.: 1.0.1 correção, 1.1.0 funcionalidade nova).
  Uma versão já publicada nunca é sobrescrita: para mudar algo, publique a próxima.
#>
param(
  [Parameter(Mandatory = $true)]
  [ValidatePattern('^\d+\.\d+\.\d+$')]
  [string]$Versao,

  [string]$Usuario = "cesinha012642",

  # Não move a tag "latest" (ex.: para publicar uma versão de teste).
  [switch]$SemLatest
)

# O docker escreve progresso no stderr: os erros são conferidos pelo $LASTEXITCODE de cada comando.
$ErrorActionPreference = "Continue"
$raiz = Split-Path -Parent $PSScriptRoot
$revisao = (git -C $raiz rev-parse --short HEAD).Trim()
$alteracoes = git -C $raiz status --porcelain
if ($alteracoes) {
  Write-Warning "Há alterações não commitadas: a imagem $Versao vai conter código que não está em nenhum commit."
}

$apps = @(
  @{ Nome = "chamaai-backend";  Pasta = "apps/backend/chamaAi" },
  @{ Nome = "chamaai-frontend"; Pasta = "apps/frontend" }
)

foreach ($app in $apps) {
  $imagem = "$Usuario/$($app.Nome)"
  docker manifest inspect "${imagem}:$Versao" *> $null
  if ($LASTEXITCODE -eq 0) {
    throw "${imagem}:$Versao já existe no Docker Hub. Use a próxima versão."
  }
}

foreach ($app in $apps) {
  $imagem = "$Usuario/$($app.Nome)"
  Write-Host "==> Build ${imagem}:$Versao" -ForegroundColor Cyan
  docker build `
    --label "org.opencontainers.image.version=$Versao" `
    --label "org.opencontainers.image.revision=$revisao" `
    -t "${imagem}:$Versao" (Join-Path $raiz $app.Pasta)
  if ($LASTEXITCODE -ne 0) { throw "Falha no build de $imagem." }
}

foreach ($app in $apps) {
  $imagem = "$Usuario/$($app.Nome)"
  Write-Host "==> Push ${imagem}:$Versao" -ForegroundColor Cyan
  docker push "${imagem}:$Versao"
  if ($LASTEXITCODE -ne 0) { throw "Falha no push de ${imagem}:$Versao." }
  if (-not $SemLatest) {
    docker tag "${imagem}:$Versao" "${imagem}:latest"
    docker push "${imagem}:latest"
    if ($LASTEXITCODE -ne 0) { throw "Falha no push de ${imagem}:latest." }
  }
}

Write-Host ""
Write-Host "Publicado: $Usuario/chamaai-backend:$Versao e $Usuario/chamaai-frontend:$Versao (commit $revisao)." -ForegroundColor Green
Write-Host "No Portainer: TAG=$Versao na stack e 'Update the stack' com 'Re-pull image'."
