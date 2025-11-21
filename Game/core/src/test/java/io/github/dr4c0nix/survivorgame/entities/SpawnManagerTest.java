package io.github.dr4c0nix.survivorgame.entities;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.dr4c0nix.survivorgame.entities.enemy.ClassicEnemy;
import io.github.dr4c0nix.survivorgame.entities.player.Player;
import io.github.dr4c0nix.survivorgame.screens.Gameplay;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class SpawnManagerTest {

    @Mock
    private Gameplay mockGameplay;

    @Mock
    private EntityFactory mockEntityFactory;

    @Mock
    private TiledMap mockMap;

    @Mock
    private Player mockPlayer;

    @Mock
    private ClassicEnemy mockEnemy;

    private SpawnManager spawnManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Mocks pour la carte TiledMap
        MapLayers mockLayers = new MapLayers();
        MapLayer mockSpawnLayer = new MapLayer();
        mockSpawnLayer.setName("mobspawn");
        MapObjects mockObjects = mockSpawnLayer.getObjects();

        // Création de fausses zones de spawn
        RectangleMapObject room1Obj = new RectangleMapObject(10, 10, 100, 100);
        room1Obj.setName("room1");
        mockObjects.add(room1Obj);

        RectangleMapObject room2Obj = new RectangleMapObject(200, 200, 100, 100);
        room2Obj.setName("room2");
        mockObjects.add(room2Obj);

        mockLayers.add(mockSpawnLayer);
        when(mockMap.getLayers()).thenReturn(mockLayers);

        // Mocks pour le joueur et l'ennemi
        when(mockPlayer.getHitbox()).thenReturn(new Rectangle(0, 0, 32, 32));
        when(mockPlayer.getDifficulter()).thenReturn(1f);
        when(mockEnemy.getHitbox()).thenReturn(new Rectangle(0, 0, 20, 20));

        // Mocks pour la factory
        when(mockEntityFactory.getAvailableEnemyTypes()).thenReturn(new ArrayList<>(Collections.singletonList("Orc")));
        when(mockEntityFactory.getEnemyHitboxSize("Orc")).thenReturn(new Vector2(20, 20));
        when(mockEntityFactory.obtainEnemy(anyString(), any(Vector2.class))).thenReturn(mockEnemy);

        // Initialisation du SpawnManager
        spawnManager = new SpawnManager(mockGameplay, mockEntityFactory, mockMap);
    }

    @Test
    public void testUpdate_DoesNotSpawnWhenLocked() {
        spawnManager.update(5f, mockPlayer);
        verify(mockEntityFactory, never()).obtainEnemy(anyString(), any(Vector2.class));
    }

    @Test
    public void testUpdate_DoesNotSpawnBeforeInterval() {
        spawnManager.unlockSpawning();
        spawnManager.setSpawnInterval(5f);
        spawnManager.update(4f, mockPlayer);
        verify(mockEntityFactory, never()).obtainEnemy(anyString(), any(Vector2.class));
    }

    @Test
    public void testUpdate_SpawnsAfterInterval() {
        // Le joueur est dans la "room1"
        when(mockPlayer.getHitbox()).thenReturn(new Rectangle(50, 50, 32, 32));
        // Pas de collision pour le spawn
        when(mockGameplay.isColliding(any(Rectangle.class))).thenReturn(false);

        spawnManager.unlockSpawning();
        spawnManager.setSpawnInterval(2f);
        spawnManager.update(3f, mockPlayer);

        verify(mockEntityFactory, atLeastOnce()).obtainEnemy(eq("Orc"), any(Vector2.class));
    }

    @Test
    public void testAttemptSpawn_DoesNotSpawnIfColliding() {
        // Le joueur est dans la "room1"
        when(mockPlayer.getHitbox()).thenReturn(new Rectangle(50, 50, 32, 32));
        // Toujours en collision, le spawn devrait échouer
        when(mockGameplay.isColliding(any(Rectangle.class))).thenReturn(true);

        spawnManager.unlockSpawning();
        spawnManager.setSpawnInterval(1f);
        spawnManager.update(2f, mockPlayer);

        // obtain peut être appelé, mais release doit l'être aussi si la collision est détectée après création
        verify(mockEntityFactory, atMost(10)).obtainEnemy(anyString(), any(Vector2.class));
        verify(mockEntityFactory, atMost(10)).releaseEnemy(any(ClassicEnemy.class));
    }

    @Test
    public void testSpawningUnlocked() {
        assertFalse(spawnManager.isSpawningUnlocked());
        spawnManager.unlockSpawning();
        assertTrue(spawnManager.isSpawningUnlocked());
    }
}