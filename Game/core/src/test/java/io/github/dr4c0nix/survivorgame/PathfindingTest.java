package io.github.dr4c0nix.survivorgame;

import com.badlogic.gdx.math.Vector2;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests unitaires pour {@link PathfindingMap}.
 * 
 * Cette classe teste l'algorithme de recherche de chemin utilisé par les ennemis
 * pour se déplacer vers le joueur dans le jeu (inspiré de Vampire Survivors).
 */
public class PathfindingTest {

    // Constantes pour les tests
    private static final int DISTANCE_INFINIE = 9999;
    private static final float PRECISION = 0.001f;

    /**
     * Test : Vérifie l'initialisation de la carte et la validation des coordonnées.
     * 
     * Objectif : S'assurer que la carte se crée correctement et que les coordonnées
     * valides/invalides sont bien détectées.
     */
    @Test
    public void testInitialisationEtValidation() {
        // Arrange : Créer une carte 4x3
        PathfindingMap carte = new PathfindingMap(4, 3);
        
        // Assert : Vérifier les dimensions
        assertEquals("La largeur doit être 4", 4, carte.getWidth());
        assertEquals("La hauteur doit être 3", 3, carte.getHeight());

        // Coordonnées valides (dans les limites)
        assertTrue("(0,0) doit être valide", carte.isValid(0, 0));
        assertTrue("(3,2) doit être valide", carte.isValid(3, 2));

        // Coordonnées invalides (hors limites)
        assertFalse("(-1,0) doit être invalide", carte.isValid(-1, 0));
        assertFalse("(4,0) doit être invalide", carte.isValid(4, 0));
        assertFalse("(0,3) doit être invalide", carte.isValid(0, 3));

        // Les coordonnées invalides retournent une distance infinie
        assertEquals("Distance invalide doit être INFINIE", 
                     DISTANCE_INFINIE, carte.getDistance(-5, 0));
    }

    /**
     * Test : Vérifie le calcul des distances dans une grille vide.
     * 
     * Objectif : Dans une grille sans obstacles, la distance entre deux points
     * doit être la distance de Chebyshev (maximum entre différence X et Y).
     * Par exemple : de (2,2) à (4,3) = max(|4-2|, |3-2|) = max(2,1) = 2
     */
    @Test
    public void testCalculDistanceGrilleVide() {
        // Arrange : Créer une carte 5x5 sans obstacles
        int taille = 5;
        PathfindingMap carte = new PathfindingMap(taille, taille);
        int joueurX = 2, joueurY = 2;
        
        // Act : Calculer les distances depuis la position du joueur
        carte.calculateFlow(joueurX, joueurY);

        // Assert : Vérifier chaque case
        for (int x = 0; x < taille; x++) {
            for (int y = 0; y < taille; y++) {
                int distanceAttendue = Math.max(Math.abs(x - joueurX), Math.abs(y - joueurY));
                assertEquals(
                    String.format("Distance de (%d,%d) au joueur", x, y),
                    distanceAttendue, 
                    carte.getDistance(x, y)
                );
            }
        }
    }

    /**
     * Test : Vérifie qu'un mur bloque le calcul de chemin.
     * 
     * Objectif : Si le joueur est placé sur un mur, aucun chemin
     * ne peut être calculé (toutes les distances restent infinies).
     */
    @Test
    public void testJoueurSurMur() {
        // Arrange : Créer une carte et placer un mur
        PathfindingMap carte = new PathfindingMap(3, 3);
        int posX = 1, posY = 1;
        carte.setWall(posX, posY);
        
        // Act : Essayer de calculer depuis un mur
        carte.calculateFlow(posX, posY);

        // Assert : Toutes les distances doivent rester infinies
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                assertEquals("Toutes les distances doivent être INFINIES", 
                             DISTANCE_INFINIE, carte.getDistance(x, y));
            }
        }
    }

    /**
     * Test : Vérifie le blocage des mouvements diagonaux.
     * 
     * Objectif : Un ennemi ne peut pas "couper un coin" en diagonale
     * si les deux cases adjacentes sont des murs.
     * 
     * Exemple :
     *   P . .    P = joueur (2,2)
     *   X ? .    X = murs
     *   X E .    E = ennemi (3,3)
     * 
     * L'ennemi ne peut pas aller directement en diagonale vers P
     * car les deux côtés du coin sont bloqués.
     */
    @Test
    public void testBlocageDiagonal() {
        // Arrange : Créer une carte avec des murs qui bloquent la diagonale
        PathfindingMap carte = new PathfindingMap(5, 5);
        int joueurX = 2, joueurY = 2;
        
        // Placer deux murs qui bloquent le passage diagonal
        carte.setWall(3, 2); // Mur à droite
        carte.setWall(2, 3); // Mur en haut
        
        // Act : Calculer les chemins
        carte.calculateFlow(joueurX, joueurY);

        // Assert : La diagonale ne doit pas être accessible directement
        int distance = carte.getDistance(3, 3);
        assertTrue("Le mouvement diagonal doit être bloqué", 
                   distance == DISTANCE_INFINIE || distance > 1);
    }

    /**
     * Test : Vérifie le calcul de direction vers le joueur.
     * 
     * Objectif : La méthode getDirection doit retourner un vecteur
     * pointant vers la case la plus proche du joueur.
     */
    @Test
    public void testDirectionVersJoueur() {
        // Arrange : Créer une carte simple
        PathfindingMap carte = new PathfindingMap(5, 5);
        int joueurX = 2, joueurY = 2;
        carte.calculateFlow(joueurX, joueurY);

        // Act & Assert : Tester une direction orthogonale (droite → gauche)
        Vector2 direction1 = carte.getDirection(3, 2);
        assertNotNull("Une direction doit exister", direction1);
        assertEquals("Direction X doit pointer vers la gauche", -1f, direction1.x, PRECISION);
        assertEquals("Direction Y doit être nulle", 0f, direction1.y, PRECISION);

        // Act & Assert : Tester une direction diagonale
        Vector2 direction2 = carte.getDirection(3, 3);
        assertNotNull("Une direction diagonale doit exister", direction2);
        float normalisee = (float) (1.0 / Math.sqrt(2.0));
        assertEquals("Direction X normalisée", -normalisee, direction2.x, PRECISION);
        assertEquals("Direction Y normalisée", -normalisee, direction2.y, PRECISION);
    }

    /**
     * Test : Vérifie les cas où aucune direction n'existe.
     * 
     * Objectif : getDirection doit retourner null dans ces cas :
     * - Sur la case du joueur (pas besoin de direction)
     * - Sur une coordonnée invalide
     * - Sur une case inaccessible
     */
    @Test
    public void testDirectionNull() {
        // Arrange
        PathfindingMap carte = new PathfindingMap(3, 3);
        int joueurX = 1, joueurY = 1;
        carte.calculateFlow(joueurX, joueurY);

        // Assert : Pas de direction sur la case du joueur
        assertNull("Pas de direction sur la case du joueur", 
                   carte.getDirection(joueurX, joueurY));

        // Assert : Pas de direction pour coordonnées invalides
        assertNull("Coordonnées invalides", carte.getDirection(-1, 0));
        assertNull("Coordonnées invalides", carte.getDirection(3, 0));

        // Assert : Cellule inaccessible (entourée de murs)
        PathfindingMap carteInaccessible = new PathfindingMap(3, 3);
        carteInaccessible.setWall(0, 1); // entourer (0,0)
        carteInaccessible.setWall(1, 0);
        carteInaccessible.calculateFlow(2, 2);
        assertNull("Cellule inaccessible doit retourner null", carteInaccessible.getDirection(0, 0));
    }

    /**
     * Test : Vérifie l'effacement des murs.
     * 
     * Objectif : Après clearWalls(), les chemins précédemment bloqués
     * doivent devenir accessibles.
     */
    @Test
    public void testEffacementMurs() {
        // Arrange : Créer une ligne avec un mur au milieu
        PathfindingMap carte = new PathfindingMap(3, 1);
        carte.setWall(1, 0); // Bloquer le milieu
        
        // Act : Calculer (la case 2 doit être inaccessible)
        carte.calculateFlow(0, 0);
        assertEquals("Case bloquée", DISTANCE_INFINIE, carte.getDistance(2, 0));

        // Act : Effacer les murs et recalculer
        carte.clearWalls();
        carte.calculateFlow(0, 0);

        // Assert : Maintenant la case doit être accessible
        assertEquals("Case accessible après clearWalls", 2, carte.getDistance(2, 0));
    }

    @Test
    public void testRobustesseContreArgumentsHorsLimites() {
        PathfindingMap m = new PathfindingMap(4, 4);
        // Position de joueur négative ou hors limites doit être ignorée (pas d'exception, distances restent INF)
        m.calculateFlow(-1, -1);
        for (int x = 0; x < 4; x++) for (int y = 0; y < 4; y++)
            assertEquals("calculateFlow avec joueur invalide doit laisser distances INF", DISTANCE_INFINIE, m.getDistance(x, y));

        m.calculateFlow(10, 10);
        for (int x = 0; x < 4; x++) for (int y = 0; y < 4; y++)
            assertEquals("calculateFlow avec joueur hors limites doit laisser distances INF", DISTANCE_INFINIE, m.getDistance(x, y));
    }
}
