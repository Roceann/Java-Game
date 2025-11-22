package io.github.dr4c0nix.survivorgame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.dr4c0nix.survivorgame.entities.enemy.ClassicEnemy;
import io.github.dr4c0nix.survivorgame.entities.player.Player;
import io.github.dr4c0nix.survivorgame.screens.Gameplay;
import java.util.ArrayList;

public class SpawnManager {
    private static final int MAX_SPAWN_ATTEMPTS = 10;

    private final Gameplay gameplay;
    private final EntityFactory entityFactory;
    private final ArrayList<Rectangle> room1Areas = new ArrayList<>();
    private final ArrayList<Rectangle> room2Areas = new ArrayList<>();
    private final ArrayList<Rectangle> corridorAreas = new ArrayList<>();

    private boolean spawningUnlocked = false;
    private float spawnTimer = 0f;
    private float spawnInterval = 2f;

    public SpawnManager(Gameplay gameplay, EntityFactory entityFactory, TiledMap map) {
        this.gameplay = gameplay;
        this.entityFactory = entityFactory;
        parseMobSpawnLayer(map);
    }

    private MapLayer findLayerCaseInsensitive(TiledMap map, String targetName) {
        if (map == null || targetName == null) return null;
        MapLayer direct = map.getLayers().get(targetName);
        if (direct != null) return direct;
        for (MapLayer candidate : map.getLayers()) {
            if (candidate != null && candidate.getName() != null && candidate.getName().equalsIgnoreCase(targetName)) {
                return candidate;
            }
        }
        return null;
    }

    private void parseMobSpawnLayer(TiledMap map) {
        if (map == null) return;
        MapLayer layer = findLayerCaseInsensitive(map, "mobspawn");
        if (layer == null) {
            StringBuilder available = new StringBuilder();
            for (MapLayer candidate : map.getLayers()) {
                if (candidate != null && candidate.getName() != null) {
                    available.append(candidate.getName()).append(", ");
                }
            }
            return;
        }
        for (MapObject obj : layer.getObjects()) {
            if (!(obj instanceof RectangleMapObject)) continue;
            Rectangle rect = new Rectangle(((RectangleMapObject) obj).getRectangle());
            String name = obj.getName() == null ? "" : obj.getName().toLowerCase();
            switch (name) {
                case "room1":
                    room1Areas.add(rect);
                    break;
                case "room2":
                    room2Areas.add(rect);
                    break;
                case "corridor":
                    corridorAreas.add(rect);
                    break;
                default:
                    break;
            }
        }
    }

    public void unlockSpawning() {
        this.spawningUnlocked = true;
    }

    public boolean isSpawningUnlocked() {
        return spawningUnlocked;
    }

    public void setSpawnInterval(float seconds) {
        this.spawnInterval = seconds;
    }

    public void update(float delta, Player player) {
        if (!spawningUnlocked || player == null) return;
        spawnTimer += delta;
        if (spawnTimer < spawnInterval) return;
        spawnTimer -= spawnInterval;
        attemptSpawn(player);
    }

    private void attemptSpawn(Player player) {
        ArrayList<Rectangle> zones = selectZones(player);
        if (zones.isEmpty()) return;

        ArrayList<String> types = entityFactory.getAvailableEnemyTypes();
        if (types.isEmpty()) return;

        int desired = computeSpawnBatch(player);
        int spawned = 0;
        while (spawned < desired) {
            if (!trySpawnOne(zones, types)) break;
            spawned++;
        }
    }

    private ArrayList<Rectangle> selectZones(Player player) {
        ArrayList<Rectangle> result = new ArrayList<>();
        boolean inRoom1 = isPlayerInsideAreas(room1Areas, player);
        boolean inRoom2 = isPlayerInsideAreas(room2Areas, player);
        boolean inCorridor = isPlayerInsideAreas(corridorAreas, player);

        if (inRoom1) {
            result.addAll(room1Areas);
        } else if (inRoom2) {
            result.addAll(room2Areas);
        } else if (inCorridor) {
            result.addAll(room1Areas);
            result.addAll(room2Areas);
        }
        return result;
    }

    private boolean trySpawnOne(ArrayList<Rectangle> candidates, ArrayList<String> types) {
        for (int attempt = 0; attempt < MAX_SPAWN_ATTEMPTS; attempt++) {
            Rectangle zone = candidates.get(MathUtils.random(candidates.size() - 1));
            String type = types.get(MathUtils.random(types.size() - 1));

            Vector2 size = entityFactory.getEnemyHitboxSize(type);

            if (size == null) {
                ClassicEnemy proto = entityFactory.obtainEnemy(type, new Vector2(-10000f, -10000f));
                if (proto != null) {
                    entityFactory.releaseEnemy(proto);
                    size = entityFactory.getEnemyHitboxSize(type);
                }
            }

            // Si toujours null, on saute le type
            if (size == null) continue;
            
            

            float width = size.x;
            float height = size.y;

            if (zone.width < width || zone.height < height) continue;

            float x = MathUtils.random(zone.x, zone.x + zone.width - width);
            float y = MathUtils.random(zone.y, zone.y + zone.height - height);

            Rectangle probe = new Rectangle(x, y, width, height);
            if (gameplay.isColliding(probe)) continue;

            ClassicEnemy enemy = entityFactory.obtainEnemy(type, new Vector2(x, y));
            if (enemy == null) return false;

            if (gameplay.isColliding(enemy.getHitbox())) {
                entityFactory.releaseEnemy(enemy);
                continue;
            }
            return true;
        }
        return false;
    }

    private int computeSpawnBatch(Player player) {
        float elapsedTime = gameplay.getElapsedTime();
        float difficultyFactor = player.getDifficulter();
        
        float baseSpawn = 2f;
        

        float timeFactor = (float)Math.sqrt(elapsedTime / 60f); 
        
        float rawValue = baseSpawn + (timeFactor * difficultyFactor);
        
        return MathUtils.clamp((int)Math.floor(rawValue), 1, 15);
    }

    private boolean isPlayerInsideAreas(ArrayList<Rectangle> areas, Player player) {
        Rectangle playerRect = player.getHitbox();
        for (Rectangle area : areas) {
            if (area.overlaps(playerRect)) return true;
        }
        return false;
    }
}