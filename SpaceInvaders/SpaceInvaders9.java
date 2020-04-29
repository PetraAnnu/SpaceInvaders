import acm.program.*;
import acm.graphics.*;
import acm.util.*;

import java.awt.Color;
import java.awt.event.*;
import java.util.ArrayList;

public class SpaceInvaders9 extends GraphicsProgram {

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

	public static final int NUM_INV_PER_ROW = 6;
	public static final int NUM_INV_ROWS = 3;
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
					System.out.println("tohle se stalo" + lvlNumber);
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
			
			GImage theBoss = createBoss(20, 0);
			GImage theSmallBoss = createBoss(-40, INV_HEIGHT + 20);	
			
			while (true) {
				checkIfPlayerDead();
				animateShots();
				score.setLabel("Score: " + scoreCount);
				lives.setLabel("Lives: " + playerLives);
				
				if (invaderList.size() == 0) { 
					lvlNumber++;
					nextLevelReact();
					break;
				} else {
					moveInvaders(2);
				}
				
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
			GImage theSmallBoss = createBoss(-40, INV_HEIGHT + 20);
			GImage theThirdBoss = createBoss(-50, INV_HEIGHT + 20 + 40);
			
			while (true) {
				checkIfPlayerDead();
				animateShots();
				score.setLabel("Score: " + scoreCount);
				lives.setLabel("Lives: " + playerLives);
				moveInvaders(2);
				if (invaderList.size() == 0) { 
					
					lvlNumber++;
					System.out.println("tohle se stalo" + lvlNumber);
					nextLevelReact();
					break;
				} else {
					moveInvaders(2);
				}

				if (invaderList.contains(theBoss)) {
					moveBoss(theBoss, 15, 20);
					moveBoss(theSmallBoss, -10, -40);
				}
				if (invaderList.contains(theSmallBoss)) {
					moveBoss(theThirdBoss, 20, -50);
					shootingBoss(theSmallBoss);
				}
				animateBOSSShots();
				pause(DELAY);
				if (invaderList.isEmpty()) {
					nextLevelReact();
					lvlNumber++;
				}

			}
			if (lvlNumber == 4) {
				lives.setLabel("Lives: " + playerLives);
				GLabel endGame1 = new GLabel("You finished all the available levels!");
				endGame1.setFont("courier-30");
				endGame1.setColor(Color.WHITE);
				endGame1.setLocation(getWidth() / 2 - endGame1.getWidth() / 2, getHeight() / 2);
				add(endGame1);
				GLabel endGame2 = new GLabel("Well done! :D");
				endGame2.setFont("courier-30");
				endGame2.setColor(Color.WHITE);
				endGame2.setLocation(getWidth() / 2 - endGame2.getWidth() / 2, getHeight() / 2 + 35);
				add(endGame2);
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
	 * keeps the shots moving after being fired
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
		paddle = new GImage("res/SpaceShip.png");
		paddle.setSize(PADDLE_WIDTH, PADDLE_HEIGHT);
		add(paddle, getWidth() / 2 - paddle.getWidth() / 2, getHeight() - paddle.getHeight());
	}

	/*
	 * creates one single invader 
	 * needs more information for which style it should make said invader and where to put it
	 */
	private void createInvader(double x, double y, String numberOfInva) {
		GImage inva = new GImage("res/invader-" + numberOfInva + ".png");
		inva.setSize(INV_WIDTH, INV_HEIGHT);
		add(inva, x, y);
		invaderList.add(inva);
		movementList.add(rgen.nextBoolean());

	}

	/*
	 * creates a several rows of invaders using the createInvader method
	 * there are as many rows as is the number in the variable NUM_INV_ROWS 
	 * and in each row there is NUM_INV_PER_ROW <-- this many invaders
	 */
	private void createLineOfInvaders() {
		for (int i = 0; i < NUM_INV_PER_ROW; i++) {
			String str = " ";
			int f = rgen.nextInt(0, 3);
			if (f == 0) {
				str = "0";
			} else if (f == 1) {
				str = "1";
			} else if (f == 2) {
				str = "2";
			} else if (f == 3) {
				str = "3";
			}

			for (int j = 0; j < NUM_INV_ROWS; j++) {
				int s = rgen.nextInt(0 , ((int)getCanvasWidth() - INV_WIDTH));
				createInvader(s, j * (INV_HEIGHT + INV_SEP), str);
			}
		}
	}

	/*
	 * moves the group of invaders from side to side
	 * int a represents speed
	 */
	private void moveInvaders(int a) {
		for (int i = 0; i < invaderList.size(); i++) {
			if (invaderList.get(i).getX() <= 0) {
				invaderList.get(i).move(a, 0);
				movementList.add(i, true);
			} else if (invaderList.get(i).getRightX() >= getCanvasWidth()) {
				invaderList.get(i).move(-1*a, 0);
				movementList.add(i, false);
			} else if(movementList.get(i) ){
				invaderList.get(i).move(a, 0);
			} else {
				invaderList.get(i).move(-1 * a, 0);
			}
		}
	}

	/*
	 * checks if the boss hit the player, if yes it removes a life from the player
	 * checks if the player hit any of the invaders and if yes, removes them from the game and list they are stored in, as well as their movement
	 * removes the shots that hit something as well
	 */
	private void checkCollision(GOval shot) {
		if (getElementAt(shot.getX(), shot.getY()) != null || getElementAt(shot.getX(), shot.getY() + BALL_SIZE) != null
				|| getElementAt(shot.getX() + BALL_SIZE, shot.getY() + BALL_SIZE) != null
				|| getElementAt(shot.getX() + BALL_SIZE, shot.getY()) != null) { 	//reacts if the shot hit ANYTHING
			if (getElementAt(shot.getX(), shot.getY()) == paddle
					|| getElementAt(shot.getX(), shot.getY() + BALL_SIZE) == paddle
					|| getElementAt(shot.getX() + BALL_SIZE, shot.getY() + BALL_SIZE) == paddle
					|| getElementAt(shot.getX() + BALL_SIZE, shot.getY()) == paddle) {
				
				// if the invaders managed to hit the player (their paddle/rocket) and if yes, removes a life
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
				// something --> therefore it is a shot fired by the player and it hit the invader
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
	 * creates a special invader - The Boss - it shoots back at the player 
	 * int previousBossSize = INV_HEIGHT + bossSize of the boss above them (programmer has to remember)
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
	 * creates the shots fired by the boss invader, at random times, 
	 * the boss is not shooting constantly with same breaks in between
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

	/*
	 * check the number of lives the plyer has, if the player has 0 lives and is therefore dead, 
	 * it reacts accordingly, by saying the payer lost and offers to restart the game
	 */
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

	/*
	 * all the things that need to be redone when a new level starts up
	 */
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
		
		for (int i = 0; i < shotsFiredList.size(); i++) {
			GOval oneShot = shotsFiredList.get(i);
			remove(oneShot.getCenterX(), (oneShot.getCenterY()));
		}
		invaderList.removeAll(invaderList);
		shotsFiredList.removeAll(shotsFiredList);
		createLineOfInvaders();
	}
}
