# Script de Création de Données de Test

Ce script Python génère automatiquement des données de test pour l'API POI.

## 📋 Données Créées

Le script crée les éléments suivants :
- ✅ **3 Organisations** (Tourism, Heritage, Business)
- ✅ **3 Utilisateurs** (avec authentification)
- ✅ **3 POIs** (Mont Cameroun, Palais Royal Foumban, Marché Central Douala)
- ✅ **2 Blogs** (articles sur les POIs)
- ✅ **2 Podcasts** (contenus audio)
- ✅ **3 Reviews** (évaluations des POIs)

## 🚀 Utilisation

### Prérequis

1. Python 3.7+ installé
2. Module `requests` installé :
   ```bash
   pip install requests
   ```
3. L'API doit être en cours d'exécution sur `http://localhost:8080`

### Exécution

```bash
# Rendre le script exécutable (Linux/Mac)
chmod +x create_test_data.py

# Exécuter le script
python3 create_test_data.py
```

ou directement :
```bash
./create_test_data.py
```

## 📊 Résultats

Le script affiche la progression en temps réel avec des indicateurs visuels :
- ✅ Succès (vert)
- ❌ Erreur (rouge)
- ℹ️ Information (bleu)

À la fin de l'exécution, un fichier `test_data_results.json` est créé contenant tous les IDs des éléments créés.

### Exemple de sortie

```
============================================================
🚀 SCRIPT DE CRÉATION DE DONNÉES DE TEST
============================================================

ℹ️  Création des organisations...
✅ Organisation créée: Tourism Cameroon (ID: xxx-xxx-xxx)
✅ Organisation créée: Heritage Preservation Society (ID: xxx-xxx-xxx)
✅ Organisation créée: Cameroon Business Network (ID: xxx-xxx-xxx)

ℹ️  Création des utilisateurs...
✅ Utilisateur créé: alice_tourist (ID: xxx-xxx-xxx)
ℹ️  Token d'authentification configuré pour alice_tourist
✅ Utilisateur créé: bob_heritage (ID: xxx-xxx-xxx)
✅ Utilisateur créé: charlie_business (ID: xxx-xxx-xxx)

...

============================================================
📊 RÉSUMÉ DES DONNÉES CRÉÉES
============================================================
✅ Organisations: 3/3
✅ Utilisateurs: 3/3
✅ POIs: 3/3
✅ Blogs: 2/2
✅ Podcasts: 2/2
✅ Reviews: 3/3
============================================================

✅ Résultats sauvegardés dans test_data_results.json
✅ Script terminé avec succès!
```

## 🔧 Configuration

Pour modifier l'URL de l'API, éditez la variable `BASE_URL` dans le script :

```python
BASE_URL = "http://localhost:8080/api"
```

## 📝 Données Créées en Détail

### Organisations
1. **Tourism Cameroon** (TOUR-CM) - Type: TOURISM
2. **Heritage Preservation Society** (HPS-CM) - Type: CULTURAL
3. **Cameroon Business Network** (CBN-CM) - Type: BUSINESS

### Utilisateurs
1. **alice_tourist** - alice@example.com (Tourism Cameroon)
2. **bob_heritage** - bob@example.com (Heritage Society)
3. **charlie_business** - charlie@example.com (Business Network)

Mot de passe pour tous : `SecurePass123!`

### POIs
1. **Mont Cameroun** - Volcan actif, point culminant d'Afrique de l'Ouest
2. **Palais Royal de Foumban** - Site historique et musée Bamoun
3. **Marché Central de Douala** - Grand marché commercial

### Blogs
1. "Mon Ascension du Mont Cameroun" - Récit d'aventure
2. "Découverte du Patrimoine Bamoun" - Visite culturelle

### Podcasts
1. "Les Légendes du Mont Cameroun" - Mythes et légendes (30 min)
2. "Commerce et Traditions au Marché Central" - Exploration sonore (20 min)

### Reviews
- 3 évaluations pour les POIs (Google, TripAdvisor)
- Notes de 4 à 5 étoiles

## 🛠️ Dépannage

### L'API n'est pas accessible
```
❌ Exception lors de la création de l'organisation: Connection refused
```
**Solution** : Vérifiez que l'API est démarrée sur le port 8080

### Erreur d'authentification
```
❌ Erreur création POI: 401 - Unauthorized
```
**Solution** : Le token d'authentification n'a pas été configuré. Vérifiez que le premier utilisateur a été créé avec succès.

### Données déjà existantes
```
❌ Erreur création organisation: 409 - Email already registered
```
**Solution** : Nettoyez la base de données ou modifiez les données dans le script.

## 📄 Fichier de Résultats

Le fichier `test_data_results.json` contient la structure suivante :

```json
{
  "organizations": [...],
  "users": [...],
  "pois": [...],
  "blogs": [...],
  "podcasts": [...],
  "reviews": [...]
}
```

Chaque élément contient l'ID et les détails complets retournés par l'API.

## ⚠️ Notes Importantes

- Le script utilise le premier utilisateur créé pour l'authentification
- Les POIs sont créés avec des coordonnées GPS réelles au Cameroun
- Les images et fichiers audio utilisent des URLs d'exemple
- Le script s'arrête proprement avec Ctrl+C
