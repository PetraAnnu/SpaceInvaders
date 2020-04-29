import acm.program.*;
import acm.graphics.*;
import acm.util.*;

import java.awt.Color;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.plaf.synth.SynthStyleFactory;


public class SpaceInvaders7 extends GraphicsProgram {

	private GLabel score;
	private GLabel lives;
	private int scoreCount = 0;
	private int playerLives = 3;

	private ArrayList<GOval> shotsFiredList = new ArrayList<GOval>(); // shots fired by player from paddle at invaders
	private ArrayList<GOval> bossShotsFiredList = new ArrayList<GOval>(); // list of shots fired at the paddle by
																			// invaders
	private ArrayList<GImage> invaderList = new ArrayList<GImage>(); // list of created invaders, gImages
	private ArrayList<Boolean> movementList = new ArrayList<Boolean>(); 
	private GImage paddle;
	public static final int APPLICATION_HEIGHT = 600;
	private static final double BALL_SIZE = 5;
	private static final int PADDLE_WIDTH = 100;
	private static final int PADDLE_HEIGHT = 80;
	private static final int INV_WIDTH = 80;
	private static final int INV_HEIGHT = 60;
	RandomGenerator rgen = new RandomGenerator();
	private int DELAY = 50;
	private int g = -1;
	private int w = 1;

	/**
	 * Number of invaders in each row 7--> 5 8--> 7 5-->2 4--> FAILLL
	 */
	public static final int NUM_INV_PER_ROW = 6;

	/**
	 * Number of rows of invaders Number of invader rows in reality it shows this
	 * number + 1 invader rows
	 */
	public static final int NUM_INV_ROWS = 3;

	/**
	 * Separation between neighboring invaders, in pixels
	 */

	public static final int INV_SEP = 6;
	private GImage background = new GImage("res/Background.gif");

	public void run() {
		super.setSize(APPLICATION_HEIGHT, APPLICATION_HEIGHT);
		background.setSize(getWidth(), getHeight());

		add(background);
		score = new GLabel("Score: " + scoreCount);
		score.setFont("courier-20");
		score.setColor(Color.WHITE);
		score.setLocation(0, getHeight());
		add(score);

		lives = new GLabel("Lives: " + playerLives);
		lives.setFont("courier-20");
		lives.setColor(Color.WHITE);
		lives.setLocation(getWidth() - lives.getWidth() - 20, getHeight());
		add(lives);

		shotsFiredList = new ArrayList<GOval>();
		createPaddle();
		addMouseListeners();
		

		GLabel clickStart = new GLabel("CLICK TO START!");
		clickStart.setFont("courier-50");
		clickStart.setColor(Color.WHITE);
		clickStart.setLocation(getWidth() / 2 - clickStart.getWidth() / 2, getHeight() / 2);
		add(clickStart);
		waitForClick();
		remove(clickStart);

		int lvlNumber = 1;

		if (lvlNumber == 1) {
			GImage theBoss = createBoss(20, 0);
			createLineOfInvaders();
			while (true) {
				
				checkIfPlayerDead();
				
				if (invaderList.size() == 0) { 
					
					lvlNumber++;
					nextLevelReact();
					break;
				} else {
					moveInvaders(2);
				}
				animateShots();
				score.setLabel("Score: " + scoreCount);
				lives.setLabel("Lives: " + playerLives);
				
				if (invaderList.contains(theBoss)) {
					
					moveBoss(theBoss, 10, 20);
					shootingBoss(theBoss);
				}
				animateBOSSShots();
				pause(DELAY);
				
			}
		}
		if (lvlNumber == 2) {
			
			createLineOfInvaders();
			GImage theBoss = createBoss(20, 0);
			GImage theSmallBoss = createBoss(-40, INV_HEIGHT + 20);
			while (true) {
				checkIfPlayerDead();
				animateShots();
				score.setLabel("Score: " + scoreCount);
				lives.setLabel("Lives: " + playerLives);
				
				moveInvaders(2);
				if (invaderList.contains(theBoss)) {
					moveBoss(theBoss, 15, 20);
				}
				if (invaderList.contains(theSmallBoss)) {
					moveBoss(theSmallBoss, -10, -40);
					shootingBoss(theSmallBoss);
				}
				animateBOSSShots();
				pause(DELAY);
				if (invaderList.isEmpty()) {
					nextLevelReact();
					lvlNumber++;
				}

			}
		}

		if (lvlNumber == 3) {
			createLineOfInvaders();
			GImage theBoss = createBoss(20, 0);
			while (true) {
				checkIfPlayerDead();
				animateShots();
				score.setLabel("Score: " + scoreCount);
				lives.setLabel("Lives: " + playerLives);
				moveInvaders(2);
				if (invaderList.contains(theBoss)) {
					moveBoss(theBoss, 10, 20);
					shootingBoss(theBoss);
				}
				animateBOSSShots();
				pause(DELAY);
				if (invaderList.isEmpty()) {
					nextLevelReact();
					lvlNumber++;
				}

			}

		}
	}

	/*
	 * reacts when mouse moves and shifts the paddle aka our ship along with it
	 */
	public void mouseMoved(MouseEvent e) {
		double x = e.getX() - paddle.getWidth() / 2 + BALL_SIZE / 2;
		paddle.setLocation(x, getHeight() - paddle.getHeight());
	}

	/*
	 * reacts when mouse pressed and creates a shot
	 */
	public void mousePressed(MouseEvent e) {
		double x = e.getX();
		double y = APPLICATION_HEIGHT - paddle.getHeight() - 25;
		GOval s = new GOval(x, y, BALL_SIZE, BALL_SIZE);
		s.setFilled(true);
		s.setColor(Color.ORANGE);
		add(s);
		shotsFiredList.add(s);
	}

	/*
	 * makes the shots keep moving after being fired
	 */
	private void animateShots() {
		for (int i = shotsFiredList.size() - 1; i >= 0; i--) {
			GOval shot = shotsFiredList.get(i);
			shot.move(0, -5);
			checkCollision(shot);
			if (shot.getY() < 0) {
				remove(shotsFiredList.get(i));
				shotsFiredList.remove(i);
			}
		}
	}

	/*
	 * when called creates a paddle
	 */
	private void createPaddle() {
		paddle = new GImage("res/SpaceShip5.png");
		paddle.setSize(PADDLE_WIDTH, PADDLE_HEIGHT);
		add(paddle, getWidth() / 2 - paddle.getWidth() / 2, getHeight() - paddle.getHeight());
	}

	/*
	 * creates one single invader needs more information for which style it should
	 * make said invader and where to put it
	 */
	private void createInvader(double x, double y, String numberOfInva) {
		GImage inva = new GImage("res/invader-" + numberOfInva + ".png");
		inva.setSize(INV_WIDTH, INV_HEIGHT);
		add(inva, x, y);
		invaderList.add(inva);
		movementList.add(true);

	}

	/*
	 * creates a table of invaders using the createInvared method
	 */
	private void createLineOfInvaders() {
		for (int i = 0; i < NUM_INV_PER_ROW; i++) {
			String str = " ";
			int f = rgen.nextInt(0, 2);
			if (f == 0) {
				str = "0";
			} else if (f == 1) {
				str = "1";
			} else if (f == 2) {
				str = "2";
			}

			for (int j = 0; j < NUM_INV_ROWS; j++) {

				createInvader(
						i * (INV_WIDTH + INV_SEP) + getWidth() / 2 - (NUM_INV_PER_ROW * (INV_WIDTH + INV_SEP)) / 2,
						j * (INV_HEIGHT + INV_SEP), str);
			}
		}
	}

	/*
	 * moves the group of invaders from side to side int g represents which way way
	 * they will go (-1 means right, 1 means left) int a represents speed
	 */
	private void moveInvaders(int a) {
		for (int i = 0; i < invaderList.size(); i++) {
		
			if (invaderList.get(i).getX() <= 0) {
				invaderList.get(i).move(a, 0);
				movementList.add(i, true);
				System.out.println("1");
			} else if (invaderList.get(i).getRightX() >= getCanvasWidth()) {
				invaderList.get(i).move(-1*a, 0);
				movementList.add(i, false);
				System.out.println("");
				System.out.println(getCanvasWidth());
				
				System.out.println(invaderList.get(i).getRightX());
				System.out.println("");
				
				System.out.println("2");
			} else if(movementList.get(i) ){
				System.out.println("3");
				invaderList.get(i).move(a, 0);
			} else {
				System.out.println("4");
				invaderList.get(i).move(-1*a, 0);
			}
		}
		
		
		/*if (invaderList.get(0).getX() <= 0) {
			g = g * (-1);
			System.out.println("prvni");
		} else if (invaderList.get(0).getX() + NUM_INV_PER_ROW * (INV_WIDTH + INV_SEP) >= getWidth()) {
			g = g * (-1);
			System.out.println("druha blah");
		}
		for (int i = 0; i < invaderList.size(); i++) {
			invaderList.get(i).move(g * a, 0);
		}
		*/
	}

	/*
	 * checks if the boss hit the player, if yes it removes a life check if the
	 * player hit any of the invaders and if yes, removes them from the game and
	 * list they are stored in
	 */
	private void checkCollision(GOval shot) {
		if (getElementAt(shot.getX(), shot.getY()) != null || getElementAt(shot.getX(), shot.getY() + BALL_SIZE) != null
				|| getElementAt(shot.getX() + BALL_SIZE, shot.getY() + BALL_SIZE) != null
				|| getElementAt(shot.getX() + BALL_SIZE, shot.getY()) != null) {
			if (getElementAt(shot.getX(), shot.getY()) == paddle
					|| getElementAt(shot.getX(), shot.getY() + BALL_SIZE) == paddle
					|| getElementAt(shot.getX() + BALL_SIZE, shot.getY() + BALL_SIZE) == paddle
					|| getElementAt(shot.getX() + BALL_SIZE, shot.getY()) == paddle) {
				// if the invaders managed to hit the player (their paddle/rocket)
				// i will have to check the collision when the invaders start shooting and if
				// they hit i need to lower the lives
				if (bossShotsFiredList.contains(shot)) {
					bossShotsFiredList.remove(shot);
					remove(shot);
					playerLives--;
				}

				// when the shot reaches the end of the screen
			} else if (shot.getY() >= getHeight()) {
				if (bossShotsFiredList.contains(shot)) {
					bossShotsFiredList.remove(shot);
					remove(shot);
				}

				// if the shot hits a label or when it reacts to traveling over the background
			} else if (getElementAt(shot.getX(), shot.getY()) == background
					|| getElementAt(shot.getX(), shot.getY() + BALL_SIZE) == background
					|| getElementAt(shot.getX() + BALL_SIZE, shot.getY() + BALL_SIZE) == background
					|| getElementAt(shot.getX() + BALL_SIZE, shot.getY()) == background) {

			} else if (getElementAt(shot.getX(), shot.getY()) == score
					|| getElementAt(shot.getX(), shot.getY() + BALL_SIZE) == score
					|| getElementAt(shot.getX() + BALL_SIZE, shot.getY() + BALL_SIZE) == score
					|| getElementAt(shot.getX() + BALL_SIZE, shot.getY()) == score) {

			} else if (getElementAt(shot.getX(), shot.getY()) == lives
					|| getElementAt(shot.getX(), shot.getY() + BALL_SIZE) == lives
					|| getElementAt(shot.getX() + BALL_SIZE, shot.getY() + BALL_SIZE) == lives
					|| getElementAt(shot.getX() + BALL_SIZE, shot.getY()) == lives) {

				// the shot hits something else than paddle, label, background - but it does hit
				// something --> therefore it is a shot fired by the player and it hit the
				// invader
			} else {

				if (getElementAt(shot.getX(), shot.getY()) != null) {
					// if the top left corner hits something
					shotsFiredList.remove(shot);
					int indexCol = invaderList.indexOf(getElementAt(shot.getX(), shot.getY()));
					movementList.remove(indexCol);
					invaderList.remove(indexCol);
					remove(getElementAt(shot.getX(), shot.getY()));
					
				} else if (getElementAt(shot.getX(), shot.getY() + BALL_SIZE) != null) {
					// if the lower left corner hits something
					shotsFiredList.remove(shot);
					
					
					int indexCol = invaderList.indexOf(getElementAt(shot.getX(), shot.getY() + BALL_SIZE));
					movementList.remove(indexCol);
					invaderList.remove(indexCol);
					
					
					remove(getElementAt(shot.getX(), shot.getY() + BALL_SIZE));
				} else if (getElementAt(shot.getX() + BALL_SIZE, shot.getY()) != null) {
					// if the top right corner hits something
					shotsFiredList.remove(shot);
					
					
					int indexCol = invaderList.indexOf(getElementAt(shot.getX() + BALL_SIZE, shot.getY()));
					movementList.remove(indexCol);
					invaderList.remove(indexCol);
					
					
					remove(getElementAt(shot.getX() + BALL_SIZE, shot.getY()));
				} else if (getElementAt(shot.getX() + BALL_SIZE, shot.getY() + BALL_SIZE) != null) {
					// if the lower right corner hits something
					shotsFiredList.remove(shot);
					
					
					int indexCol = invaderList.indexOf(getElementAt(shot.getX() + BALL_SIZE, shot.getY() + BALL_SIZE));
					movementList.remove(indexCol);
					invaderList.remove(indexCol);
					
					
					remove(getElementAt(shot.getX() + BALL_SIZE, shot.getY() + BALL_SIZE));
				} else if (getElementAt(shot.getX(), shot.getY()) != null
						|| getElementAt(shot.getX() + BALL_SIZE, shot.getY()) != null) {
					// if the top side of the square hits something
					shotsFiredList.remove(shot);
					
					
					int indexCol = invaderList.indexOf(getElementAt(shot.getX(), shot.getY()));
					movementList.remove(indexCol);
					invaderList.remove(indexCol);
					
					 indexCol = invaderList.indexOf(getElementAt(shot.getX() + BALL_SIZE, shot.getY()));
					movementList.remove(indexCol);
					invaderList.remove(indexCol);
					
					
					
					
					remove(getElementAt(shot.getX(), shot.getY()));
					remove(getElementAt(shot.getX() + BALL_SIZE, shot.getY()));
				} else if (getElementAt(shot.getX(), shot.getY() + BALL_SIZE) != null
						|| getElementAt(shot.getX(), shot.getY()) != null) {
					// if the left side of the square hits something
					shotsFiredList.remove(shot);
					
					
					int indexCol = invaderList.indexOf(getElementAt(shot.getX(), shot.getY() + BALL_SIZE));
					movementList.remove(indexCol);
					invaderList.remove(indexCol);
					
					indexCol = invaderList.indexOf(getElementAt(shot.getX(), shot.getY()));
					movementList.remove(indexCol);
					invaderList.remove(indexCol);
					
					
					remove(getElementAt(shot.getX(), shot.getY() + BALL_SIZE));
					remove(getElementAt(shot.getX(), shot.getY()));
				} else if (getElementAt(shot.getX() + BALL_SIZE, shot.getY() + BALL_SIZE) != null
						|| getElementAt(shot.getX() + BALL_SIZE, shot.getY()) != null) {
					// if the right side of the square hits something
					shotsFiredList.remove(shot);
					
					
					int indexCol = invaderList.indexOf(getElementAt(shot.getX() + BALL_SIZE, shot.getY() + BALL_SIZE));
					movementList.remove(indexCol);
					invaderList.remove(indexCol);
					
					 indexCol = invaderList.indexOf(getElementAt(shot.getX() + BALL_SIZE, shot.getY()));
					movementList.remove(indexCol);
					invaderList.remove(indexCol);
					
					
					
					
					remove(getElementAt(shot.getX() + BALL_SIZE, shot.getY() + BALL_SIZE));
					remove(getElementAt(shot.getX() + BALL_SIZE, shot.getY()));
				} else if (getElementAt(shot.getX() + BALL_SIZE, shot.getY() + BALL_SIZE) != null
						|| getElementAt(shot.getX(), shot.getY() + BALL_SIZE) != null) {
					// if the under side of the square hits something
					shotsFiredList.remove(shot);
					
					
					int indexCol = invaderList.indexOf(getElementAt(shot.getX() + BALL_SIZE, shot.getY() + BALL_SIZE));
					movementList.remove(indexCol);
					invaderList.remove(indexCol);
					
					 indexCol = invaderList.indexOf(getElementAt(shot.getX(), shot.getY() + BALL_SIZE));
					movementList.remove(indexCol);
					invaderList.remove(indexCol);
					
					
					
					remove(getElementAt(shot.getX() + BALL_SIZE, shot.getY() + BALL_SIZE));
					remove(getElementAt(shot.getX(), shot.getY() + BALL_SIZE));
				}

				scoreCount++;
				remove(shot);

			}
		}
	}

	/*
	 * creates a special invader - The Boss - it shoots back at the player int
	 * previousBossSize = INV_HEIGHT + bossSize
	 */
	private GImage createBoss(int bossSize, int previousBossSize) {
		GImage boss = new GImage("res/boss.png");
		boss.setSize(INV_WIDTH + bossSize, INV_HEIGHT + bossSize);
		add(boss, getWidth() / 2 - (INV_WIDTH + bossSize) / 2,
				NUM_INV_ROWS * (INV_SEP + INV_HEIGHT) + previousBossSize);
		invaderList.add(boss);
		movementList.add(true);
		return boss;
	}

	/*
	 * moves the boss invader, it moves differently than the main group of invaders,
	 * int w represents which way way they will go (-1 means right, 1 means left)
	 * int a represents speed
	 */
	private void moveBoss(GImage boss, int a, int bossSize) {
		if (boss.getX() <= 0) {
			w = w * (-1);
		} else if ((boss.getX() + (INV_WIDTH + bossSize)) >= getWidth()) {
			w = w * (-1);
		}
		boss.move(w * a, 0);

	}

	
	
	/*
	 * creates the shots fired by the boss invader, at random times, the boss is not
	 * shooting constantly with same breaks in between
	 */
	private void shootingBoss(GImage boss) {
		if (rgen.nextBoolean(0.05)) {
			GOval bossAttacks = new GOval(boss.getX() + INV_WIDTH / 2, boss.getY() + INV_HEIGHT + 25, BALL_SIZE,
					BALL_SIZE);
			bossAttacks.setFilled(true);
			bossAttacks.setColor(Color.RED);
			add(bossAttacks);
			bossShotsFiredList.add(bossAttacks);
		}
	}

	/*
	 * animates the shots fired by the boss invaders, makes them move
	 */
	private void animateBOSSShots() {
		for (int i = bossShotsFiredList.size() - 1; i >= 0; i--) {
			GOval shot = bossShotsFiredList.get(i);
			shot.move(0, 10);
			checkCollision(shot);
			if (shot.getY() < 0) {
				remove(shotsFiredList.get(i));
				shotsFiredList.remove(i);
			}
		}
	}

	private void checkIfPlayerDead() {
		if (playerLives == 0) {
			lives.setLabel("Lives: " + playerLives);
			GLabel loser = new GLabel("You died!" + " Your score is " + scoreCount + ".");
			loser.setFont("courier-30");
			loser.setColor(Color.WHITE);
			loser.setLocation(getWidth() / 2 - loser.getWidth() / 2, getHeight() / 2);
			add(loser);

			GLabel restart = new GLabel("Click to restart.");
			restart.setFont("courier-30");
			restart.setColor(Color.WHITE);
			restart.setLocation(getWidth() / 2 - restart.getWidth() / 2, getHeight() / 2 + 35);
			add(restart);

			waitForClick();
			remove(restart);
			remove(loser);
			playerLives = 3;
			scoreCount = 0;
			invaderList.removeAll(invaderList);
			shotsFiredList.removeAll(shotsFiredList);
			run();
		}
	}

	public void nextLevelReact() {
		lives.setLabel("Lives: " + playerLives);
		GLabel congrats = new GLabel("You finished this level! Congratulations!");
		congrats.setFont("courier-30");
		congrats.setColor(Color.WHITE);
		congrats.setLocation(getWidth() / 2 - congrats.getWidth() / 2, getHeight() / 2);
		add(congrats);

		GLabel nextLvl = new GLabel("Click to start next level.");
		nextLvl.setFont("courier-30");
		nextLvl.setColor(Color.WHITE);
		nextLvl.setLocation(getWidth() / 2 - nextLvl.getWidth() / 2, getHeight() / 2 + 35);
		add(nextLvl);
		
		waitForClick();
		remove(congrats);
		remove(nextLvl);
		playerLives = 3;
		scoreCount = 0;
		
		invaderList.removeAll(invaderList);
		shotsFiredList.removeAll(shotsFiredList);
		for (int i = 0; i <= shotsFiredList.size(); i++) {
			GOval oneShot = shotsFiredList.get(i);
			remove(oneShot.getCenterX(), (oneShot.getCenterY()));
		}
		
		
		
		

	}
}
