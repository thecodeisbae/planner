# Système de Gestion des Missions

Application Java EE pour la gestion des missions des conducteurs de véhicules administratifs

## Table des matières

- [Présentation](#présentation)
- [Fonctionnalités](#fonctionnalités)
- [Prérequis techniques](#prérequis-techniques)
- [Installation](#installation)
- [Configuration](#configuration)
- [Structure du projet](#structure-du-projet)
- [API](#api)
- [Tests](#tests)
- [Déploiement](#déploiement)
- [Contribution](#contribution)
- [Licence](#licence)

## Présentation

Le Système de Gestion des Missions est une application Java EE conçue pour gérer efficacement les affectations des conducteurs aux missions de véhicules administratifs. L'application permet d'éviter les chevauchements de missions pour un même conducteur et offre des fonctionnalités de reporting pour visualiser l'état des conducteurs en mission sur une période donnée.

### Objectifs principaux

- Gérer le planning des conducteurs de véhicules administratifs
- Prévenir les chevauchements de missions
- Produire des états et listes des conducteurs en mission

## Fonctionnalités

### Gestion des conducteurs
- Création et modification des profils de conducteurs
- Suivi des disponibilités et qualifications
- Historique des missions par conducteur

### Gestion des véhicules
- Inventaire de la flotte de véhicules
- Association des véhicules aux types de missions
- Suivi de l'état et de la maintenance

### Planification des missions
- Création de nouvelles missions avec assignation de conducteur et véhicule
- Vérification automatique des disponibilités des conducteurs
- Alerte en cas de tentative de planification avec chevauchement
- Visualisation calendaire des missions

### Reporting
- Liste des conducteurs en mission pour une période spécifiée
- Statistiques d'utilisation des véhicules et d'occupation des conducteurs
- Export des rapports au format PDF et Excel

## Prérequis techniques

- Java Development Kit (JDK) 21
- Base de données MySQL 8.0 ou supérieur (une base H2 est utilisée par défaut en développement)
- Maven 3.6 ou supérieur pour la gestion des dépendances
- Git pour le versionnement du code
- Navigateur web moderne pour l'interface utilisateur (avec support de JavaScript et CSS modernes)

## Installation

### Cloner le dépôt

```bash
git clone https://github.com/votre-organisation/planner.git
cd planner
```

### Compilation et exécution avec Maven Wrapper

L'application utilise Maven Wrapper, donc vous n'avez pas besoin d'installer Maven séparément.

#### Sur Linux/MacOS

```bash
./mvnw clean install
./mvnw spring-boot:run
```

#### Sur Windows

```bash
mvnw.cmd clean install
mvnw.cmd spring-boot:run
```

### Génération du fichier JAR

```bash
./mvnw package
```

Le fichier JAR exécutable sera généré dans le répertoire `target/`.

## Configuration

### Mode de développement avec H2 (par défaut)

Par défaut, l'application utilise une base de données H2 en mémoire, configurée dans le fichier `application-dev.properties`. Aucune configuration supplémentaire n'est nécessaire pour commencer à développer.

### Configuration pour la production avec MySQL

1. Créer une base de données MySQL :

```sql
CREATE DATABASE missionsdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'mission_user'@'localhost' IDENTIFIED BY 'votre_mot_de_passe';
GRANT ALL PRIVILEGES ON missionsdb.* TO 'mission_user'@'localhost';
FLUSH PRIVILEGES;
```

2. Configuration de la connexion à la base de données :

Modifier le fichier `src/main/resources/application-prod.properties` avec les paramètres de connexion appropriés :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/missionsdb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=mission_user
spring.datasource.password=votre_mot_de_passe
```

3. Activer le profil de production :

```properties
# Dans le fichier application.properties
spring.profiles.active=prod
```

Ou au démarrage de l'application :

```bash
./mvnw spring-boot:run -Dspring.profiles.active=prod
```

### Utilisateurs par défaut

L'application crée automatiquement deux utilisateurs par défaut au démarrage :

1. Administrateur
   - Nom d'utilisateur : `admin`
   - Mot de passe : `admin123`
   - Rôles : ADMIN, USER

2. Utilisateur standard
   - Nom d'utilisateur : `user`
   - Mot de passe : `user123`
   - Rôle : USER

## Structure du projet

```
planner/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── eneam/
│   │   │       └── tp/
│   │   │           └── planner/
│   │   │               ├── config/            # Configuration Spring
│   │   │               ├── controllers/       # Contrôleurs Spring MVC et REST
│   │   │               ├── dto/               # Objets de transfert de données
│   │   │               ├── exceptions/        # Exceptions personnalisées
│   │   │               ├── models/            # Entités JPA
│   │   │               ├── repositories/      # Repositories Spring Data
│   │   │               └── services/          # Services métier Spring
│   │   ├── resources/
│   │   │   ├── application.properties        # Configuration Spring Boot
│   │   │   ├── application-dev.properties    # Configuration de développement
│   │   │   ├── application-prod.properties   # Configuration de production
│   │   │   └── templates/                    # Templates Thymeleaf
│   │   │       ├── auth/                     # Pages d'authentification
│   │   │       ├── conducteurs/              # Pages de gestion des conducteurs
│   │   │       ├── dashboard/                # Pages du tableau de bord
│   │   │       ├── layout/                   # Layouts réutilisables
│   │   │       ├── missions/                 # Pages de gestion des missions
│   │   │       ├── rapport/                  # Pages de rapport
│   │   │       └── vehicules/                # Pages de gestion des véhicules
│   └── test/
│       └── java/                            # Tests unitaires et d'intégration
├── .gitignore
├── mvnw                                     # Script Maven Wrapper pour Unix
├── mvnw.cmd                                 # Script Maven Wrapper pour Windows
├── pom.xml                                  # Configuration Maven
└── README.md
```

## Modèle de données

### Principales entités

#### Conducteur
```java
@Entity
public class Conducteur {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String prenom;
    private String matricule;
    // Autres attributs et méthodes
}
```

#### Vehicule
```java
@Entity
public class Vehicule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String immatriculation;
    private String modele;
    private String type;
    // Autres attributs et méthodes
}
```

#### Mission
```java
@Entity
public class Mission {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    
    @ManyToOne
    private Conducteur conducteur;
    
    @ManyToOne
    private Vehicule vehicule;
    // Autres attributs et méthodes
}
```

## API

### REST API

L'application expose une API REST pour l'intégration avec d'autres systèmes.

#### Points d'entrée principaux

- `GET /api/conducteurs` : Liste tous les conducteurs
- `GET /api/conducteurs/{id}` : Détails d'un conducteur spécifique
- `GET /api/conducteurs/matricule/{matricule}` : Récupère un conducteur par matricule
- `GET /api/conducteurs/{id}/missions` : Missions d'un conducteur
- `POST /api/conducteurs` : Crée un nouveau conducteur
- `PUT /api/conducteurs/{id}` : Met à jour un conducteur
- `DELETE /api/conducteurs/{id}` : Supprime un conducteur

- `GET /api/vehicules` : Liste tous les véhicules
- `GET /api/vehicules/{id}` : Détails d'un véhicule spécifique
- `GET /api/vehicules/immatriculation/{immatriculation}` : Récupère un véhicule par immatriculation
- `GET /api/vehicules/{id}/missions` : Missions d'un véhicule
- `POST /api/vehicules` : Crée un nouveau véhicule
- `PUT /api/vehicules/{id}` : Met à jour un véhicule
- `DELETE /api/vehicules/{id}` : Supprime un véhicule

- `GET /api/missions` : Liste toutes les missions
- `GET /api/missions/{id}` : Détails d'une mission spécifique
- `GET /api/missions/periode?debut={date}&fin={date}` : Missions sur une période
- `GET /api/missions/rapport/conducteurs` : Rapport des conducteurs en mission
- `GET /api/missions/conducteurs-en-mission` : Liste des conducteurs en mission
- `POST /api/missions` : Crée une nouvelle mission
- `PUT /api/missions/{id}` : Met à jour une mission
- `PUT /api/missions/{id}/annuler` : Annule une mission

### Exemple d'utilisation de l'API

```bash
# Récupérer les conducteurs en mission entre le 01/05/2025 et le 07/05/2025
curl -X GET "http://localhost:8080/api/missions/conducteurs-en-mission?debut=2025-05-01%2000:00&fin=2025-05-07%2023:59&statut=EN_COURS" -H "accept: application/json"

# Récupérer toutes les missions d'un conducteur
curl -X GET "http://localhost:8080/api/conducteurs/1/missions" -H "accept: application/json"
```

## Tests

### Exécuter les tests unitaires

```bash
mvn test
```

### Exécuter les tests d'intégration

```bash
mvn verify
```

## Déploiement

### Sur WildFly

1. Démarrer le serveur WildFly :
```bash
./standalone.sh
```

2. Déployer l'application :
```bash
mvn wildfly:deploy
```

Ou manuellement via l'interface d'administration à http://localhost:9990/console/

### Sur GlassFish

1. Démarrer le serveur GlassFish :
```bash
./asadmin start-domain
```

2. Déployer l'application :
```bash
./asadmin deploy target/gestion-missions.war
```

## Algorithme de vérification des chevauchements

Le cœur du système est l'algorithme qui vérifie les disponibilités des conducteurs :

```java
public boolean verifierDisponibilite(Conducteur conducteur, LocalDateTime debut, LocalDateTime fin) {
    TypedQuery<Mission> query = em.createQuery(
        "SELECT m FROM Mission m WHERE m.conducteur = :conducteur " +
        "AND ((m.dateDebut <= :fin AND m.dateFin >= :debut) " +
        "OR (m.dateDebut >= :debut AND m.dateDebut <= :fin) " +
        "OR (m.dateFin >= :debut AND m.dateFin <= :fin))",
        Mission.class);
    
    query.setParameter("conducteur", conducteur);
    query.setParameter("debut", debut);
    query.setParameter("fin", fin);
    
    List<Mission> missionsEnConflit = query.getResultList();
    return missionsEnConflit.isEmpty();
}
```

## Contribution

### Guide de contribution

1. Forker le dépôt
2. Créer une branche pour votre fonctionnalité (`git checkout -b feature/ma-fonctionnalite`)
3. Commiter vos changements (`git commit -am 'Ajout de ma fonctionnalité'`)
4. Pousser vers la branche (`git push origin feature/ma-fonctionnalite`)
5. Créer une Pull Request

### Conventions de code

- Suivre les conventions Oracle pour Java
- JavaDoc pour toutes les méthodes publiques
- Tests unitaires pour les fonctionnalités critiques

## Licence

Ce projet est sous licence [MIT](LICENSE).

## Contact

Pour toute question ou suggestion, veuillez contacter l'équipe de développement à l'adresse : equipe@gestion-missions.com