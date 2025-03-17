import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private static final int SCREEN_WIDTH = 600;
    private static final int SCREEN_HEIGHT = 600;
    private static final int UNIT_SIZE = 25;
    private static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    private static final int DELAY = 100;
    
    private static final int MENU = 0;
    private static final int PLAYING = 1;
    private static final int GAME_OVER = 2;
    private int gameState = MENU;
    
    private int applesEaten = 0;
    private int appleX;
    private int appleY;
    private char direction = 'R'; // R = right, L = left, U = up, D = down
    private boolean running = false;
    private Timer timer;
    private Random random;
    
    private CustomSnake snake;
    
    //Buttons
    private Rectangle startButton = new Rectangle(SCREEN_WIDTH/2 - 100, SCREEN_HEIGHT/2 - 30, 200, 60);
    private Rectangle replayButton = new Rectangle(SCREEN_WIDTH/2 - 100, SCREEN_HEIGHT/2 + 50, 200, 60);
    
    public SnakeGame() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(new Color(135, 245, 255)); // Light cyan background
        this.setFocusable(true);
        this.addKeyListener(this);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }
        });
        
        //Initialize but don't start the game as of yet
        snake = new CustomSnake(6); // Start with 6 body parts
        newApple();
        timer = new Timer(DELAY, this);
    }
    
    private void handleMouseClick(int x, int y) {
        if (gameState == MENU && startButton.contains(x, y)) {
            startGame();
        } else if (gameState == GAME_OVER && replayButton.contains(x, y)) {
            resetGame();
        }
    }
    
    public void startGame() {
        gameState = PLAYING;
        running = true;
        timer.start();
    }
    
    public void resetGame() {
        // Reset game variables
        applesEaten = 0;
        direction = 'R';
        snake = new CustomSnake(6);
        newApple();
        gameState = PLAYING;
        running = true;
        timer.start();
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (gameState == MENU) {
            drawMenu(g);
        } else if (gameState == PLAYING) {
            drawGame(g);
        } else if (gameState == GAME_OVER) {
            drawGameOver(g);
        }
    }
    
    public void drawMenu(Graphics g) {
        //Title
        g.setColor(new Color(0, 102, 204));
        g.setFont(new Font("Arial", Font.BOLD, 70));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Snake Game", (SCREEN_WIDTH - metrics.stringWidth("Snake Game")) / 2, SCREEN_HEIGHT / 4);
        
        //Start button
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(60, 220, 78));
        g2d.fill(startButton);
        g2d.setColor(new Color(0, 102, 204));
        g2d.setStroke(new BasicStroke(3));
        g2d.draw(startButton);
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        metrics = getFontMetrics(g.getFont());
        g.drawString("START", startButton.x + (startButton.width - metrics.stringWidth("START")) / 2, 
                startButton.y + ((startButton.height + metrics.getHeight()) / 2) - 6);
        
        // cuteu snakey
        int snakeX = SCREEN_WIDTH / 2;
        int snakeY = SCREEN_HEIGHT / 2 + 100;
        drawMenuSnake(g2d, snakeX, snakeY);
    }
    
    private void drawMenuSnake(Graphics2D g, int x, int y) {
        //Head
        g.setColor(new Color(60, 220, 78)); 
        g.fillOval(x - 50, y, 100, 80);
        
        //Yellow belly 
        g.setColor(new Color(255, 230, 90)); 
        g.fillOval(x - 40, y + 40, 80, 40);
        
        //Eyes
        g.setColor(new Color(0, 51, 153)); 
        g.fillOval(x - 20, y + 20, 25, 25);
        g.fillOval(x + 20, y + 15, 25, 25);
        
        //Eye highlights
        g.setColor(Color.WHITE);
        g.fillOval(x - 15, y + 25, 10, 10);
        g.fillOval(x + 25, y + 20, 10, 10);
        
        //Tongue
        g.setColor(new Color(255, 51, 102)); 
        g.fillRect(x - 5, y + 60, 10, 20);
        g.fillOval(x - 10, y + 75, 10, 10);
        g.fillOval(x, y + 75, 10, 10);
        
        //Outline
        g.setColor(new Color(0, 0, 102)); 
        g.setStroke(new BasicStroke(3));
        g.drawOval(x - 50, y, 100, 80);
        
        //Speech bubble
        int bubbleX = x + 80;
        int bubbleY = y - 20;
        g.setColor(Color.WHITE);
        g.fillOval(bubbleX, bubbleY, 100, 60);
        
        //Bubble pointer
        int[] xPoints = {bubbleX + 10, bubbleX - 10, bubbleX + 20};
        int[] yPoints = {bubbleY + 50, bubbleY + 70, bubbleY + 40};
        g.fillPolygon(xPoints, yPoints, 3);
        
        //Bubble outline
        g.setColor(new Color(0, 0, 102));
        g.drawOval(bubbleX, bubbleY, 100, 60);
        g.drawPolygon(xPoints, yPoints, 3);
        
        //Text in da bubble
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Let's play!", bubbleX + 15, bubbleY + 35);
    }
    
    public void drawGame(Graphics g) {
        //Draw an apple
        g.setColor(Color.RED);
        g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
        
        //Draw snakeyyy
        snake.draw(g);
        
        //Draw the score
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
    }
    
    public void drawGameOver(Graphics g) {
        drawGame(g);
        
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        
        //Uh ohhh 
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 3);
        
        //Display score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        metrics = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, SCREEN_HEIGHT / 2);
        
        //Replay button
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(60, 220, 78));
        g2d.fill(replayButton);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(3));
        g2d.draw(replayButton);
        
        g.setFont(new Font("Arial", Font.BOLD, 30));
        metrics = getFontMetrics(g.getFont());
        g.drawString("REPLAY", replayButton.x + (replayButton.width - metrics.stringWidth("REPLAY")) / 2, 
                replayButton.y + ((replayButton.height + metrics.getHeight()) / 2) - 6);
    }
    
    public void newApple() {
        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }
    
    public void move() {
        snake.move(direction);
    }
    
    public void checkApple() {
        if ((snake.getHeadX() == appleX) && (snake.getHeadY() == appleY)) {
            snake.grow();
            applesEaten++;
            newApple();
        }
    }
    
    public void checkCollisions() {
        //Check if head collides with body
        if (snake.checkSelfCollision()) {
            endGame();
        }
        
        //Check if head touches boundaries
        if (snake.getHeadX() < 0 || snake.getHeadX() >= SCREEN_WIDTH || 
            snake.getHeadY() < 0 || snake.getHeadY() >= SCREEN_HEIGHT) {
            endGame();
        }
    }
    
    private void endGame() {
        running = false;
        timer.stop();
        gameState = GAME_OVER;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (direction != 'R') {
                    direction = 'L';
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (direction != 'L') {
                    direction = 'R';
                }
                break;
            case KeyEvent.VK_UP:
                if (direction != 'D') {
                    direction = 'U';
                }
                break;
            case KeyEvent.VK_DOWN:
                if (direction != 'U') {
                    direction = 'D';
                }
                break;
            case KeyEvent.VK_ENTER:
                if (gameState == MENU) {
                    startGame();
                } else if (gameState == GAME_OVER) {
                    resetGame();
                }
                break;
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        // Not used
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Cute Snake Game");
        SnakeGame game = new SnakeGame();
        
        frame.add(game);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

//Custom snake class to handle the appearance and behavior of the snake
class CustomSnake {
    private static final int UNIT_SIZE = 25;
    private ArrayList<Integer> x = new ArrayList<>();
    private ArrayList<Integer> y = new ArrayList<>();
    private int bodyParts;
    
    public CustomSnake(int initialSize) {
        bodyParts = initialSize;
        
        // Initialize snake position (center of screen)
        for (int i = 0; i < bodyParts; i++) {
            x.add(300 - i * UNIT_SIZE);
            y.add(300);
        }
    }
    
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        drawSnakeHead(g2d, x.get(0), y.get(0));
        
        //Body segments
        for (int i = 1; i < bodyParts; i++) {
            drawSnakeSegment(g2d, x.get(i), y.get(i), i);
        }
    }
    
    private void drawSnakeHead(Graphics2D g, int x, int y) {
        //Base head
        g.setColor(new Color(60, 220, 78)); // Bright green
        g.fillOval(x - 5, y - 5, UNIT_SIZE + 10, UNIT_SIZE + 10);
        
        g.setColor(new Color(255, 230, 90)); // Yellow
        g.fillOval(x, y + UNIT_SIZE/2, UNIT_SIZE, UNIT_SIZE/2);
        
        // Eye
        g.setColor(new Color(0, 51, 153)); // Dark blue
        g.fillOval(x + UNIT_SIZE/2, y + UNIT_SIZE/4, UNIT_SIZE/3, UNIT_SIZE/3);
        
        g.setColor(Color.WHITE);
        g.fillOval(x + UNIT_SIZE/2 + 2, y + UNIT_SIZE/4 + 2, UNIT_SIZE/6, UNIT_SIZE/6);
        
        g.setColor(new Color(255, 51, 102)); // Pink
        g.fillRect(x + UNIT_SIZE - 5, y + UNIT_SIZE/2, UNIT_SIZE/3, UNIT_SIZE/6);
        
        g.setColor(new Color(0, 0, 102)); // Dark blue outline
        g.setStroke(new BasicStroke(2));
        g.drawOval(x - 5, y - 5, UNIT_SIZE + 10, UNIT_SIZE + 10);
    }
    
    private void drawSnakeSegment(Graphics2D g, int x, int y, int index) {
        float sizeMultiplier = 1.0f - (0.015f * index);
        if (sizeMultiplier < 0.7f) sizeMultiplier = 0.7f;
        
        int segmentSize = (int)(UNIT_SIZE * sizeMultiplier);
        int offset = (UNIT_SIZE - segmentSize) / 2;
        
        g.setColor(new Color(60, 220, 78)); // Bright green
        g.fillOval(x + offset, y + offset, segmentSize, segmentSize);
        
        if (index % 2 == 0) {
            g.setColor(new Color(255, 230, 90)); // Yellow
            g.fillOval(x + offset, y + UNIT_SIZE/2, segmentSize, segmentSize/2);
        }
        
        //Outline
        g.setColor(new Color(0, 0, 102)); // Dark blue outline
        g.setStroke(new BasicStroke(2));
        g.drawOval(x + offset, y + offset, segmentSize, segmentSize);
    }
    
    public void move(char direction) {
        //Move the body
        for (int i = bodyParts - 1; i > 0; i--) {
            x.set(i, x.get(i - 1));
            y.set(i, y.get(i - 1));
        }
        
        //Move the head
        switch (direction) {
            case 'U':
                y.set(0, y.get(0) - UNIT_SIZE);
                break;
            case 'D':
                y.set(0, y.get(0) + UNIT_SIZE);
                break;
            case 'L':
                x.set(0, x.get(0) - UNIT_SIZE);
                break;
            case 'R':
                x.set(0, x.get(0) + UNIT_SIZE);
                break;
        }
    }
    
    public void grow() {
        x.add(x.get(bodyParts - 1));
        y.add(y.get(bodyParts - 1));
        bodyParts++;
    }
    
    public boolean checkSelfCollision() {
        for (int i = 4; i < bodyParts; i++) {
            if ((x.get(0).equals(x.get(i))) && (y.get(0).equals(y.get(i)))) {
                return true;
            }
        }
        return false;
    }
    
    public int getHeadX() {
        return x.get(0);
    }
    
    public int getHeadY() {
        return y.get(0);
    }
}