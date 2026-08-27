package GameTrial;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Player {
    public double x = 64 + 32;
    public double y = 64 + 32;
    public double dirX = 1, dirY = 0;
    public double planeX = 0, planeY = 0.66;

    private final int radius = 15;
    private final double moveSpeed = 4.0;
    private final double rotSpeed = 0.05;

    private boolean forward, backward, turnLeft, turnRight;

    public void update(GameMap gameMap) {
        if (turnLeft) rotate(-rotSpeed);
        if (turnRight) rotate(rotSpeed);

        double moveX = 0, moveY = 0;
        if (forward)  { moveX += dirX * moveSpeed; moveY += dirY * moveSpeed; }
        if (backward) { moveX -= dirX * moveSpeed; moveY -= dirY * moveSpeed; }

        double nextX = x + moveX;
        if (!hasCollision(nextX, y, gameMap)) x = nextX;

        double nextY = y + moveY;
        if (!hasCollision(x, nextY, gameMap)) y = nextY;
    }

    private void rotate(double angle) {
        double oldDirX = dirX;
        dirX = dirX * Math.cos(angle) - dirY * Math.sin(angle);
        dirY = oldDirX * Math.sin(angle) + dirY * Math.cos(angle);

        double oldPlaneX = planeX;
        planeX = planeX * Math.cos(angle) - planeY * Math.sin(angle);
        planeY = oldPlaneX * Math.sin(angle) + planeY * Math.cos(angle);
    }

    private boolean hasCollision(double targetX, double targetY, GameMap gameMap) {
        return gameMap.isWall(targetX - radius, targetY - radius) ||
                gameMap.isWall(targetX + radius, targetY - radius) ||
                gameMap.isWall(targetX - radius, targetY + radius) ||
                gameMap.isWall(targetX + radius, targetY + radius);
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.RED);
        g2.fillOval((int) (x - radius), (int) (y - radius), radius * 2, radius * 2);

        g2.setColor(Color.YELLOW);
        int lineLength = 30;
        g2.drawLine((int) x, (int) y,
                (int) (x + dirX * lineLength), (int) (y + dirY * lineLength));
    }

    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) forward = true;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) backward = true;
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) turnLeft = true;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) turnRight = true;
    }

    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) forward = false;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) backward = false;
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) turnLeft = false;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) turnRight = false;
    }
}
