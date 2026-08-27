package GameTrial;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Panel extends JPanel implements Runnable {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    private Thread gameThread;
    private boolean running = false;
    private final int FPS = 60;

    private GameMap gameMap;
    private Player player;

    public Panel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        requestFocus();

        gameMap = new GameMap();
        player = new Player();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                player.keyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                player.keyReleased(e);
            }
        });
    }

    public synchronized void startGame() {
        if (running) return;
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (running) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    private void update() {
        player.update(gameMap);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int rows = gameMap.getRows();
        int cols = gameMap.getCols();
        int tileSize = GameMap.TILE_SIZE;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (gameMap.getTile(c, r) == 1) {
                    g2.setColor(Color.PINK);
                    g2.fillRect(c * tileSize, r * tileSize, tileSize, tileSize);
                    g2.setColor(Color.BLACK);
                    g2.drawRect(c * tileSize, r * tileSize, tileSize, tileSize);
                } else {
                    g2.setColor(Color.LIGHT_GRAY);
                    g2.fillRect(c * tileSize, r * tileSize, tileSize, tileSize);
                }
            }
        }

        player.draw(g2);
        drawDebugRay(g2);
    }

    private void drawDebugRay(Graphics2D g2) {
        double rayDirX = player.dirX;
        double rayDirY = player.dirY;

        int mapX = (int) (player.x / GameMap.TILE_SIZE);
        int mapY = (int) (player.y / GameMap.TILE_SIZE);

        double deltaDistX = (rayDirX == 0) ? 1e30 : Math.abs(GameMap.TILE_SIZE / rayDirX);
        double deltaDistY = (rayDirY == 0) ? 1e30 : Math.abs(GameMap.TILE_SIZE / rayDirY);

        int stepX, stepY;
        double sideDistX, sideDistY;

        if (rayDirX < 0) {
            stepX = -1;
            sideDistX = (player.x - mapX * GameMap.TILE_SIZE) / Math.abs(rayDirX);
        } else {
            stepX = 1;
            sideDistX = ((mapX + 1) * GameMap.TILE_SIZE - player.x) / Math.abs(rayDirX);
        }

        if (rayDirY < 0) {
            stepY = -1;
            sideDistY = (player.y - mapY * GameMap.TILE_SIZE) / Math.abs(rayDirY);
        } else {
            stepY = 1;
            sideDistY = ((mapY + 1) * GameMap.TILE_SIZE - player.y) / Math.abs(rayDirY);
        }

        boolean hit = false;
        int side = 0; // 0 = hit a vertical grid line, 1 = hit a horizontal grid line

        while (!hit) {
            if (sideDistX < sideDistY) {
                sideDistX += deltaDistX;
                mapX += stepX;
                side = 0;
            } else {
                sideDistY += deltaDistY;
                mapY += stepY;
                side = 1;
            }
            if (gameMap.getTile(mapX, mapY) == 1) hit = true;
        }

        double wallDist = (side == 0) ? (sideDistX - deltaDistX) : (sideDistY - deltaDistY);
        double hitX = player.x + rayDirX * wallDist;
        double hitY = player.y + rayDirY * wallDist;

        g2.setColor(Color.GREEN);
        g2.drawLine((int) player.x, (int) player.y, (int) hitX, (int) hitY);

        g2.setColor(Color.CYAN);
        g2.drawRect(mapX * GameMap.TILE_SIZE, mapY * GameMap.TILE_SIZE,
                GameMap.TILE_SIZE, GameMap.TILE_SIZE);
    }


}
