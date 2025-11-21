package io.github.dr4c0nix.survivorgame;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Heatmap / Dijkstra simple sur grille. Recalcule distances depuis le joueur.
 * Les ennemis consultent distances pour sélectionner le voisin qui diminue la distance.
 */
public class PathfindingMap {
    private final int width;
    private final int height;
    private final int[][] terrain; 
    private final int[][] distances;
    private final Queue<GridPoint2> bfsQueue = new LinkedList<>();

    private static final int INF = 9999;
    private static final int[][] NEIGH = {
        {1,0},{-1,0},{0,1},{0,-1},
        {1,1},{1,-1},{-1,1},{-1,-1}
    };

    public PathfindingMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.terrain = new int[width][height];
        this.distances = new int[width][height];
    }

    public void setWall(int x, int y) {
        if (isValid(x, y)) terrain[x][y] = 1;
    }

    public void clearWalls() {
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                terrain[x][y] = 0;
    }

    public void calculateFlow(int playerX, int playerY) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                distances[x][y] = INF;
            }
        }

        bfsQueue.clear();
        if (!isValid(playerX, playerY)) return;
        if (terrain[playerX][playerY] == 1) return;
        distances[playerX][playerY] = 0;
        bfsQueue.add(new GridPoint2(playerX, playerY));

        GridPoint2 cur;
        while ((cur = bfsQueue.poll()) != null) {
            int dist = distances[cur.x][cur.y];
            for (int[] d : NEIGH) {
                int nx = cur.x + d[0], ny = cur.y + d[1];
                if (!isValid(nx, ny)) continue;

                // block diagonal corner-cutting: if moving diagonally and both adjacent orthogonals are walls, skip
                if (Math.abs(d[0]) == 1 && Math.abs(d[1]) == 1) {
                    int ax = cur.x + d[0], ay = cur.y;      // horizontal neighbor
                    int bx = cur.x,       by = cur.y + d[1]; // vertical neighbor
                    boolean aIsWall = isValid(ax, ay) && terrain[ax][ay] == 1;
                    boolean bIsWall = isValid(bx, by) && terrain[bx][by] == 1;
                    if (aIsWall && bIsWall) continue;
                }

                visit(nx, ny, dist + 1);
            }
        }
    }

    private void visit(int x, int y, int newDist) {
        if (!isValid(x, y)) return;
        if (terrain[x][y] == 1) return;
        if (distances[x][y] <= newDist) return;
        distances[x][y] = newDist;
        bfsQueue.add(new GridPoint2(x, y));
    }

    /**
     * Retourne la direction normalisée vers un voisin qui diminue la distance jusqu'au joueur.
     * Préfère orthogonales. Retourne null si la cellule est inaccessible ou déjà au joueur.
     */
    public Vector2 getDirection(int gx, int gy) {
        if (!isValid(gx, gy)) return null;
        int cur = distances[gx][gy];
        if (cur >= INF) return null;

        // si on est sur le joueur, pas de direction
        if (cur == 0) return null;

        int best = cur;
        int bestDx = 0, bestDy = 0;

        for (int[] d : NEIGH) {
            int nx = gx + d[0], ny = gy + d[1];
            if (!isValid(nx, ny)) continue;
            if (terrain[nx][ny] == 1) continue;
            int nd = distances[nx][ny];
            if (nd < best) {
                best = nd;
                bestDx = d[0];
                bestDy = d[1];
            }
        }

        if (best < cur) {
            Vector2 v = new Vector2(bestDx, bestDy);
            return v.nor();
        }

        return null;
    }

    public boolean isValid(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    public int getDistance(int x, int y) {
        if (!isValid(x, y)) return INF;
        return distances[x][y];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}