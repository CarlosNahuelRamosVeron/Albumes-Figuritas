#!/usr/bin/env bash
# Seed robusto para poblar la API con usuarios, álbumes y contenidos (composite).
# Requisitos: bash, curl, y opcionalmente jq (fallback a python3 si no hay jq).
# Uso:
#   chmod +x scripts/seed.sh
#   ./scripts/seed.sh
# Variables opcionales:
#   BASE_URL (default http://localhost:8080)
#   ADMIN_USER (default admin)
#   ADMIN_PASS (default admin123)
#   USER_USER  (default user)
#   USER_PASS  (default user123)
#   DEBUG=true para ver los curl ejecutados (sin token real)

set -euo pipefail

# Config
BASE_URL=${BASE_URL:-"http://localhost:8080"}
ADMIN_USER=${ADMIN_USER:-admin}
ADMIN_PASS=${ADMIN_PASS:-admin123}
USER_USER=${USER_USER:-user}
USER_PASS=${USER_PASS:-user123}
DEBUG=${DEBUG:-false}
WAIT_TIMEOUT=${WAIT_TIMEOUT:-40}
WAIT_INTERVAL=${WAIT_INTERVAL:-2}

# Logging
info() { echo -e "[INFO] $*"; }
warn() { echo -e "[WARN] $*" >&2; }
err()  { echo -e "[ERR ] $*"  >&2; }
# Mensaje informativo pero a stderr, para no contaminar salidas JSON capturadas
debug_info() { echo -e "[INFO] $*" >&2; }

# Utilidades JSON (jq o python3)
json_get() {
  local key="$1"
  if command -v jq >/dev/null 2>&1; then
    jq -r ".${key}"
  else
    python3 - "$key" <<'PY'
import sys, json
key = sys.argv[1]
try:
    data = json.load(sys.stdin)
    v = data
    for part in key.split('.'):
        v = v[part]
    print(v)
except Exception:
    sys.exit(1)
PY
  fi
}

# Validación simple de formato JWT (tres segmentos base64url separados por puntos)
validate_jwt_format() {
  local token="$1"
  # Remover comillas accidentales
  token=$(printf %s "$token" | sed -E 's/^"|"$//g')
  if [[ ! "$token" =~ ^[A-Za-z0-9_-]+\.[A-Za-z0-9_-]+\.[A-Za-z0-9_-]+$ ]]; then
    local preview
    preview="$(printf %s "$token" | cut -c1-12) ... $(printf %s "$token" | rev | cut -c1-12 | rev)"
    err "Token con formato inválido. Preview: $preview"
    exit 1
  fi
}

# HTTP helpers
auth_header() {
  local token="$1"
  # Sanitizar por si vinieran caracteres no válidos (solo base64url + '.')
  token=$(printf %s "$token" | tr -d '\r\n' | sed -E 's/[^A-Za-z0-9_.-]//g')
  printf 'Authorization: Bearer %s' "$token"
}

http_request() {
  # Ejecuta curl capturando cuerpo y status code en HTTP_BODY/HTTP_STATUS
  local method="$1" url="$2" body="${3:-}" token="${4:-}"
  local args=( -s -X "$method" "$url" )
  local debug_line="curl -s -X $method '$url'"
  if [[ -n "$token" ]]; then
    local auth; auth=$(auth_header "$token")
    args+=( -H "$auth" )
    debug_line+=" -H 'Authorization: Bearer TOKEN_REDACTED'"
  fi
  # Aceptar JSON siempre
  args+=( -H 'Accept: application/json' )
  if [[ -n "$body" ]]; then
    args+=( -H 'Content-Type: application/json' -d "$body" )
    debug_line+=" -H 'Content-Type: application/json' -d '...json...'"
  fi
  args+=( -w "\n%{http_code}" )
  [[ "$DEBUG" == "true" ]] && debug_info "$debug_line"
  local resp
  resp=$(curl "${args[@]}")
  HTTP_STATUS="${resp##*$'\n'}"
  HTTP_BODY="${resp%$'\n'*}"
}

wait_for_server() {
  info "Esperando a que el servidor responda en ${BASE_URL} (timeout ${WAIT_TIMEOUT}s)"
  local start_ts=$(date +%s)
  while true; do
    local code
    code=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL" || true)
    if [[ "$code" != "000" ]]; then
      info "Servidor respondió (HTTP $code). Continuando"
      break
    fi
    local now=$(date +%s)
    if (( now - start_ts > WAIT_TIMEOUT )); then
      err "Timeout esperando al servidor en ${BASE_URL}"
      exit 1
    fi
    sleep "$WAIT_INTERVAL"
  done
}

create_user() {
  local username="$1" password="$2" role="$3"
  info "Creando usuario ${username} (${role})"
  local payload
  payload="{\"username\":\"${username}\",\"password\":\"${password}\",\"role\":\"${role}\"}"
  http_request POST "${BASE_URL}/usuarios" "$payload"
  if [[ "$HTTP_STATUS" == "201" || "$HTTP_STATUS" == "200" ]]; then
    info "Usuario ${username} creado"
  elif [[ "$HTTP_STATUS" == "400" || "$HTTP_STATUS" == "409" ]]; then
    warn "Usuario ${username} ya existe o datos inválidos (HTTP ${HTTP_STATUS})"
  else
    err "Crear usuario ${username} falló (HTTP ${HTTP_STATUS}). Respuesta: ${HTTP_BODY}"
    exit 1
  fi
}

login_and_get_token() {
  local username="$1" password="$2"
  info "Login de ${username}"
  local payload="{\"username\":\"${username}\",\"password\":\"${password}\"}"
  http_request POST "${BASE_URL}/auth/login" "$payload"
  if [[ "$HTTP_STATUS" != 2* && "$HTTP_STATUS" != "200" ]]; then
    err "Login falló (HTTP ${HTTP_STATUS}). Respuesta: ${HTTP_BODY}"
    exit 1
  fi
  local token
  token=$(echo "$HTTP_BODY" | json_get token || true)
  token=$(printf %s "$token" | tr -d '\r\n' | sed 's/[[:space:]]//g')
  if [[ -z "${token:-}" || "$token" == "null" ]]; then
    err "No se obtuvo token del login. Cuerpo: ${HTTP_BODY}"
    exit 1
  fi
  validate_jwt_format "$token"
  echo "$token"
}

create_album() {
  local token="$1" titulo="$2" descripcion="$3" categoria="$4"
  info "Creando álbum: ${titulo}"
  local payload
  payload="{\"titulo\":\"${titulo}\",\"descripcion\":\"${descripcion}\",\"categoria\":\"${categoria}\"}"
  http_request POST "${BASE_URL}/albums" "$payload" "$token"
  if [[ "$HTTP_STATUS" != 2* && "$HTTP_STATUS" != "201" && "$HTTP_STATUS" != "200" ]]; then
    err "Crear álbum falló (HTTP ${HTTP_STATUS}). Respuesta: ${HTTP_BODY}"
    exit 1
  fi
  local id
  id=$(echo "$HTTP_BODY" | json_get id || true)
  if [[ -z "${id:-}" || "$id" == "null" ]]; then
    err "No se obtuvo id del álbum. Cuerpo: ${HTTP_BODY}"
    exit 1
  fi
  echo "$id"
}

publish_album() {
  local token="$1" album_id="$2"
  info "Publicando álbum ${album_id}"
  http_request POST "${BASE_URL}/albums/${album_id}/publicar" "" "$token"
  if [[ "$HTTP_STATUS" != 2* && "$HTTP_STATUS" != "200" ]]; then
    err "Publicar álbum falló (HTTP ${HTTP_STATUS}). Respuesta: ${HTTP_BODY}"
    exit 1
  fi
}

seed_album_contenidos() {
  local token="$1" album_id="$2" modo="${3:-AUTOMATICO}"
  info "Cargando contenidos en álbum ${album_id} (modo=${modo})"
  local body
  body='[
    {
      "tipo": "SECCION",
      "nombre": "Sección A",
      "descripcion": "Sección introductoria",
      "contenidos": [
        { "tipo": "FIGURITA", "nombre": "Jugador 1", "numero": 1, "imagenBase64": null },
        { "tipo": "FIGURITA", "nombre": "Jugador 2", "numero": 2, "imagenBase64": null },
        {
          "tipo": "SECCION",
          "nombre": "Subsección A1",
          "descripcion": "Parte interna",
          "contenidos": [
            { "tipo": "FIGURITA", "nombre": "DT", "numero": 10, "imagenBase64": null }
          ]
        }
      ]
    },
    { "tipo": "FIGURITA", "nombre": "Escudo", "numero": 99, "imagenBase64": null }
  ]'
  http_request POST "${BASE_URL}/contenidos/albums/${album_id}?modo=${modo}" "$body" "$token"
  if [[ "$HTTP_STATUS" != 2* && "$HTTP_STATUS" != "200" && "$HTTP_STATUS" != "201" ]]; then
    err "Cargar contenidos falló (HTTP ${HTTP_STATUS}). Respuesta: ${HTTP_BODY}"
    exit 1
  fi
}

main() {
  info "Usando BASE_URL=${BASE_URL}"
  wait_for_server

  # 1) Crear usuarios públicos
  create_user "$ADMIN_USER" "$ADMIN_PASS" "ADMIN"
  create_user "$USER_USER"  "$USER_PASS"  "USER"

  # 2) Login admin
  ADMIN_TOKEN=$(login_and_get_token "$ADMIN_USER" "$ADMIN_PASS")
  info "Token ADMIN obtenido (longitud: ${#ADMIN_TOKEN})"

  # 3) Crear álbumes
  ALBUM1_ID=$(create_album "$ADMIN_TOKEN" "Álbum Deportes" "Colección de deportes" "DEPORTES")
  ALBUM2_ID=$(create_album "$ADMIN_TOKEN" "Álbum Música"   "Colección musical"      "MUSICA")
  info "Álbumes creados: ${ALBUM1_ID}, ${ALBUM2_ID}"

  # 4) Cargar contenidos composite
  seed_album_contenidos "$ADMIN_TOKEN" "$ALBUM1_ID" "AUTOMATICO"
  seed_album_contenidos "$ADMIN_TOKEN" "$ALBUM2_ID" "UNIFORME"

  # 5) Publicar un álbum de ejemplo
  publish_album "$ADMIN_TOKEN" "$ALBUM1_ID"

  info "Seed finalizado con éxito"
  info "Usuarios: ${ADMIN_USER} (ADMIN), ${USER_USER} (USER)"
  info "Álbumes: ${ALBUM1_ID} y ${ALBUM2_ID} (con contenidos)"
}

main "$@"
