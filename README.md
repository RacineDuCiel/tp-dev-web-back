# tp-dev-web-back

API REST pour une application de gestion de bibliotheque, developpee avec Spring Boot. La securite est assuree par Keycloak (OAuth2 Resource Server, validation JWT).

## Stack

- Java 17
- Spring Boot 4 (Web MVC, Data JPA, Validation, OAuth2 Resource Server)
- MySQL 8
- Lombok
- Logback + Logstash (pipeline ELK)

## Modele de donnees

- `Book` : titre, annee de publication, genre
- `Author` : prenom, nom, biographie — relation ManyToMany avec Book
- `Illustration` : URL, legende — relation OneToMany avec Book

## API

Toutes les routes sont prefixees par `/api` et protegees par JWT (Bearer token emis par Keycloak).

| Methode | Route                        | Description                        |
|---------|------------------------------|------------------------------------|
| GET     | /api/books                   | Liste tous les livres               |
| POST    | /api/books                   | Cree un livre                       |
| PUT     | /api/books/{id}              | Met a jour un livre                 |
| DELETE  | /api/books/{id}              | Supprime un livre                   |
| GET     | /api/authors                 | Liste tous les auteurs              |
| POST    | /api/authors                 | Cree un auteur                      |
| PUT     | /api/authors/{id}            | Met a jour un auteur                |
| DELETE  | /api/authors/{id}            | Supprime un auteur                  |
| GET     | /api/illustrations           | Liste toutes les illustrations      |
| POST    | /api/illustrations           | Cree une illustration               |
| DELETE  | /api/illustrations/{id}      | Supprime une illustration           |

## Prerequis

- Java 17+
- Maven
- MySQL accessible sur `localhost:3306` (base `library_db`)
- Keycloak actif sur `http://localhost:8090` avec le realm `library-realm`
- (Optionnel) Stack ELK pour la centralisation des logs

## Lancement

Demarrer d'abord MySQL et Keycloak via `kc-compose`, puis :

```bash
./mvnw spring-boot:run
```

L'API ecoute sur `http://localhost:8082`.

La base `library_db` est creee automatiquement au premier demarrage (`createDatabaseIfNotExist=true`), et le schema est genere par Hibernate (`ddl-auto=update`).

## Logs ELK (optionnel)

Un fichier `docker-compose-elk.yml` est fourni pour lancer Elasticsearch, Logstash et Kibana. Les logs de l'application sont envoyes en JSON vers Logstash sur le port 5044 via Logback.

```bash
docker compose -f docker-compose-elk.yml up -d
```

Kibana est accessible sur `http://localhost:8081`.
