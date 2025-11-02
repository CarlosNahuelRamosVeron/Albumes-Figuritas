#!/usr/bin/env bash
set -euo pipefail

# Script de seed para datos dummy de la API
# - Crea usuario ADMIN y USER (si no existen)
# - Hace login y guarda el token en variable
# - Crea álbumes de ejemplo
# - Carga contenidos (patrón composite: secciones con figuritas anidadas y figuritas sueltas)
# - Publica un álbum
# - (Opcional) Actualiza datos de un usuario usando PUT /usuarios/{id}
#
# Requisitos: bash, curl, y (opcional) jq o python3 para parsear JSON.
# Uso:
#   chmod +x scripts/seed.sh
#   BASE_URL=http://localhost:8080 ./scripts/seed.sh
#   UPDATE_USER_EXAMPLE=true ./scripts/seed.sh   # para ejecutar el ejemplo de actualización

BASE_URL=${BASE_URL:-"http://localhost:8080"}
ADMIN_USER=${ADMIN_USER:-admin}
ADMIN_PASS=${ADMIN_PASS:-admin123}
USER_USER=${USER_USER:-user}
USER_PASS=${USER_PASS:-user123}
UPDATE_USER_EXAMPLE=${UPDATE_USER_EXAMPLE:-false}

info() { echo -e "[INFO] $*"; }
warn() { echo -e "[WARN] $*" >&2; }
err()  { echo -e "[ERR ] $*"  >&2; }

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

json_find_user_id_by_username() {
  # Lee desde stdin una lista de usuarios [{id,username,role}...] y busca el id por username exacto
  local username="$1"
  if command -v jq >/dev/null 2>&1; then
    jq -r --arg u "$username" '.[] | select(.username == $u) | .id' | head -n1
  else
    python3 - "$username" <<'PY'
import sys, json
u = sys.argv[1]
try:
    data = json.load(sys.stdin)
    for it in data:
        if it.get('username') == u:
            print(it.get('id'))
            break
except Exception:
    pass
PY
  fi
}

create_user_if_needed() {
  local username="$1"; local password="$2"; local role="$3"
  info "Creando usuario ${username} con rol ${role} (si no existe)"
  set +e
  HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" \
    -X POST "${BASE_URL}/usuarios" \
    -H 'Content-Type: application/json' \
    -d "{\"username\":\"${username}\",\"password\":\"${password}\",\"role\":\"${role}\"}")
  set -e
  if [[ "$HTTP_CODE" == "201" || "$HTTP_CODE" == "200" ]]; then
    info "Usuario ${username} creado"
  elif [[ "$HTTP_CODE" == "400" || "$HTTP_CODE" == "409" ]]; then
    warn "Usuario ${username} ya existe o datos inválidos (HTTP ${HTTP_CODE})"
  else
    info "Respuesta al crear usuario ${username}: HTTP ${HTTP_CODE} (continuando)"
  fi
}

login_and_get_token() {
  local username="$1"; local password="$2"
  info "Login de ${username}"
  RESP=$(curl -s -X POST "${BASE_URL}/auth/login" -H 'Content-Type: application/json' \
    -d "{\"username\":\"${username}\",\"password\":\"${password}\"}")
  TOKEN=$(echo "$RESP" | json_get token || true)
  if [[ -z "${TOKEN:-}" || "${TOKEN}" == "null" ]]; then
    err "No se pudo obtener token para ${username}. Respuesta: $RESP"
    exit 1
  fi
  echo "$TOKEN"
}

auth_header() {
  local token="$1"
  echo "Authorization: Bearer ${token}"
}

create_album() {
  local token="$1"; shift
  local titulo="$1"; local descripcion="$2"; local categoria="$3"
  info "Creando álbum: ${titulo}"
  RESP=$(curl -s -X POST "${BASE_URL}/albums" \
    -H "$(auth_header "$token")" \
    -H 'Content-Type: application/json' \
    -d "{\"titulo\":\"${titulo}\",\"descripcion\":\"${descripcion}\",\"categoria\":\"${categoria}\"}")
  ALBUM_ID=$(echo "$RESP" | json_get id || true)
  if [[ -z "${ALBUM_ID:-}" || "$ALBUM_ID" == "null" ]]; then
    err "No se obtuvo id del álbum. Respuesta: $RESP"
    exit 1
  fi
  echo "$ALBUM_ID"
}

publish_album() {
  local token="$1"; local album_id="$2"
  info "Publicando álbum ${album_id}"
  curl -s -X POST "${BASE_URL}/albums/${album_id}/publicar" \
    -H "$(auth_header "$token")" >/dev/null
}

seed_contenidos_album() {
  local token="$1"; local album_id="$2"; local modo="${3:-AUTOMATICO}"
  info "Cargando contenidos en álbum ${album_id} (modo=${modo})"
  BODY='[
    {
      "tipo": "SECCION",
      "nombre": "Sección A",
      "descripcion": "Sección introductoria",
      "contenidos": [
        { "tipo": "FIGURITA", "nombre": "Jugador 1", "numero": 1, "archivoImagen": null },
        { "tipo": "FIGURITA", "nombre": "Jugador 2", "numero": 2, "archivoImagen": null },
        {
          "tipo": "SECCION",
          "nombre": "Subsección A1",
          "descripcion": "Parte interna",
          "contenidos": [
            { "tipo": "FIGURITA", "nombre": "DT", "numero": 10, "archivoImagen": null }
          ]
        }
      ]
    },
    { "tipo": "FIGURITA", "nombre": "Escudo", "numero": 99, "archivoImagen": null }
  ]'
  curl -s -X POST "${BASE_URL}/contenidos/albums/${album_id}?modo=${modo}" \
    -H "$(auth_header "$token")" \
    -H 'Content-Type: application/json' \
    -d "$BODY" >/dev/null
}

resolve_user_id_by_username() {
  local token="$1"; local username="$2"
  info "Buscando id de usuario por username=${username}"
  RESP=$(curl -s -X GET "${BASE_URL}/usuarios" -H "$(auth_header "$token")")
  local uid
  uid=$(echo "$RESP" | json_find_user_id_by_username "$username" || true)
  if [[ -z "${uid:-}" || "${uid}" == "null" ]]; then
    err "No se encontró id para el usuario ${username}. Respuesta: $RESP"
    exit 1
  fi
  echo "$uid"
}

update_user_by_id() {
  local token="$1"; local user_id="$2"; local new_username="$3"; local new_password="$4"; local new_role="$5"
  info "Actualizando usuario id=${user_id}"
  local payload
  payload='{'
  local first=true
  if [[ -n "$new_username" ]]; then
    payload+="\"username\":\"$new_username\""; first=false
  fi
  if [[ -n "$new_password" ]]; then
    [[ "$first" == false ]] && payload+=","; payload+="\"password\":\"$new_password\""; first=false
  fi
  if [[ -n "$new_role" ]]; then
    [[ "$first" == false ]] && payload+=","; payload+="\"role\":\"$new_role\""; first=false
  fi
  payload+="}"
  curl -s -X PUT "${BASE_URL}/usuarios/${user_id}" \
    -H "$(auth_header "$token")" \
    -H 'Content-Type: application/json' \
    -d "$payload" >/dev/null
}

main() {
  info "Usando BASE_URL=${BASE_URL}"

  # 1) Usuarios
  create_user_if_needed "$ADMIN_USER" "$ADMIN_PASS" "ADMIN"
  create_user_if_needed "$USER_USER"  "$USER_PASS"  "USER"

  # 2) Login como admin
  ADMIN_TOKEN=$(login_and_get_token "$ADMIN_USER" "$ADMIN_PASS")
  info "Token ADMIN obtenido (longitud: ${#ADMIN_TOKEN})"

  # 3) (Opcional) Actualizar usuario de ejemplo
  if [[ "$UPDATE_USER_EXAMPLE" == "true" ]]; then
    USER_ID=$(resolve_user_id_by_username "$ADMIN_TOKEN" "$USER_USER")
    update_user_by_id "$ADMIN_TOKEN" "$USER_ID" "" "${USER_PASS}-upd" ""
    info "Usuario ${USER_USER} actualizado (password)"
  fi

  # 4) Crear álbumes
  ALBUM1_ID=$(create_album "$ADMIN_TOKEN" "Álbum Deportes" "Colección de deportes" "DEPORTES")
  ALBUM2_ID=$(create_album "$ADMIN_TOKEN" "Álbum Música"   "Colección musical"        "MUSICA")
  info "Álbumes creados: ${ALBUM1_ID}, ${ALBUM2_ID}"

  # 5) Cargar contenidos (composite)
  seed_contenidos_album "$ADMIN_TOKEN" "$ALBUM1_ID" "AUTOMATICO"
  seed_contenidos_album "$ADMIN_TOKEN" "$ALBUM2_ID" "UNIFORME"

  # 6) Publicar un álbum de ejemplo
  publish_album "$ADMIN_TOKEN" "$ALBUM1_ID"

  info "Seed finalizado. Datos creados:"
  info "- Usuarios: ${ADMIN_USER} (ADMIN), ${USER_USER} (USER)"
  info "- Álbumes: ${ALBUM1_ID} y ${ALBUM2_ID} (con contenidos)"
  info "Podés listar contenidos con: ${BASE_URL}/contenidos/albums/${ALBUM1_ID}"
}

main "$@"
