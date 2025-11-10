# Déploiement sur Render

Ce guide explique comment déployer ce microservice Spring Boot sur Render avec Swagger accessible.

## Pré-requis
- Un compte Render (https://render.com)
- Une base MySQL accessible depuis Render (Render PostgreSQL géré, pour MySQL utilisez un service externe)
- Le repo hébergé (GitHub/GitLab) et connecté à Render

## Fichiers fournis
- `Dockerfile`: image JRE 17, lancement de l’application.
- `render.yaml`: configuration de service Render (runtime docker, health check, env vars).
- `src/main/resources/application.properties`: `server.port=${PORT:8086}` pour Render.
- `SwaggerConfig`: supporte `SWAGGER_SERVER_URL` pour fixer l’URL publique.

## Variables d’environnement à configurer sur Render
- `SPRING_DATASOURCE_URL` (ex: `jdbc:mysql://host:3306/authSprinf?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true`)
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SECRET_KEY` (JWT)
- `EXPIRATION_TIME` (ms)
- `SWAGGER_SERVER_URL` (ex: `https://users-microservice.onrender.com`)

## Étapes de déploiement
1. Poussez le code sur un repo Git.
2. Sur Render, créez un nouveau service Web "From a Git repository".
3. Sélectionnez ce repo et validez la détection du `render.yaml`.
4. Configurez les variables d’environnement listées ci-dessus.
5. Déployez.

## Accès Swagger
- UI: `https://<votre-domaine-render>/swagger-ui.html`
- Docs: `https://<votre-domaine-render>/v3/api-docs`

## Build local (optionnel)
```bash
./mvnw -DskipTests package
docker build -t users-microservice:latest .
docker run -p 8086:8086 -e PORT=8086 users-microservice:latest
```

## Notes
- Render fixe la variable `PORT`; l’app Spring l’utilise via `server.port=${PORT:8086}`.
- Si le health check échoue, vérifiez la base MySQL (réseau, credentials) et les logs Render.

