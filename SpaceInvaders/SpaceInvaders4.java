import acm.program.*;
import acm.graphics.*;
import acm.util.*;
import stanford.spl.GButton;

import java.awt.Color;
import java.awt.event.*;
import java.util.ArrayList;

public class SpaceInvaders4 extends GraphicsProgram {

	private GLabel score;
	private GLabel lives;
	private int scoreCount = 0;
	private int playerLives = 3;


	private ArrayList<GOval> shotsFiredList = new ArrayList<GOval>(); 		//shots fired by player from paddle at invaders
	private ArrayList<GOval> bossShotsFiredList  = new ArrayList<GOval>(); 	// list of shots fired at the paddle by invaders
	private ArrayList<GImage> invaderList = new ArrayList<GImage>();		//list of created invaders, gImages
	private GImage paddle ;
	public static final int APPLICATION_HEIGHT = 600;
	private static final double BALL_SIZE = 5;
	private static final int PADDLE_WIDTH = 100;
	private static final int PADDLE_HEIGHT = 80;
	private static final int INV_WIDTH = 80;
	private static final int INV_HEIGHT = 60;
	RandomGenerator rgen = new RandomGenerator();
	private int DELAY = 50;
	private int g = -1;
	private int w = 10;


	/**
	 * Number of invaders in each row
	 * 7--> 5
	 * 8--> 7
	 * 5-->2
	 * 4--> FAILLL
	 */
	public static final int NUM_INV_PER_ROW = 6;

	/** 
	 * Number of rows of invaders
	 * Number of invader rows
	 * in reality it shows this number + 1 invader rows
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
		createLineOfInvaders();
		GImage theBoss = createBoss();
		
		int numOfInvaders = invaderList.size();
		System.out.println(numOfInvaders);
		
		GLabel clickStart = new GLabel("CLICK TO START!");
		clickStart.setFont("courier-50");
		clickStart.setColor(Color.WHITE);
		clickStart.setLocation(getWidth()/2 - clickStart.getWidth()/2, getHeight()/2);
		add(clickStart);
		waitForClick();
		remove(clickStart);
		
		while(true) {

			if(playerLives == 0) {
				lives.setLabel("Lives: " + playerLives);
				GLabel loser = new GLabel("You died!"+ " Your score is " + scoreCount + ".");
				loser.setFont("courier-30");
				loser.setColor(Color.WHITE);
				loser.setLocation(getWidth()/2 - loser.getWidth()/2, getHeight()/2);
				add(loser);
				
				GLabel restart = new GLabel("Click to restart.");
				restart.setFont("courier-30");
				restart.setColor(Color.WHITE);
				restart.setLocation(getWidth()/2 - restart.getWidth()/2, getHeight()/2 + 35);
				add(restart);
				
				waitForClick();
				remove(restart);
				remove(loser);
				playerLives = 3;
				scoreCount = 0;
				invaderList.removeAll(invaderList);
				shotsFiredList.removeAll(shotsFiredList);
				run();
				
			//}else if(invaderList.isEmpty() != false) { 
			//}else if(getElementCount() <= 4) {
			//}else if(invaderList.size() == 0 ) { 	
			//}else if(scoreCount == 19) { 	//numOfInvaders
				
			}else if(invaderList.isEmpty() == true) {	
				animateShots();
				score.setLabel("Score: " + scoreCount);
				lives.setLabel("Lives: " + playerLives);
				
				
				
				GLabel winner = new GLabel("You win!");
				winner.setFont("courier-30");
				winner.setColor(Color.WHITE);
				winner.setLocation(getWidth()/2 - winner.getWidth()/2, getHeight()/2);
				add(winner);
				break;
				/*	
				GLabel nextLVL = new GLabel("Click to continue to next level.");
				nextLVL.setFont("courier-30");
				nextLVL.setColor(Color.WHITE);
				nextLVL.setLocation(getWidth()/2 - nextLVL.getWidth()/2, getHeight()/2 + 35);
				add(nextLVL);
				
				waitForClick();
				remove(nextLVL);
				remove(winner);
				playerLives = 3;
				scoreCount = 0;
				invaderList.removeAll(invaderList);
				shotsFiredList.removeAll(shotsFiredList);
				run();
				makeNextLevel();
					*/
			}else {
				animateShots();
				score.setLabel("Score: " + scoreCount);
				lives.setLabel("Lives: " + playerLives);
				
				moveInvaders();
				if(invaderList.contains(theBoss)) {
					moveBoss(theBoss); 		
					shootingBoss(theBoss);
				}
				animateBOSSShots();	
				
				pause(DELAY);
			}
		}
	}
	
	
	
	

	/*
	 * reacts when mouse moves and shifts the paddle aka our ship along with it
	 */
	public void mouseMoved(MouseEvent e){
		double x = e.getX() - paddle.getWidth()/2 + BALL_SIZE/2;
		paddle.setLocation(x, getHeight() - paddle.getHeight());
	}

	/*
	 * reacts when mouse pressed and creates a shot
	 */
	public void mousePressed(MouseEvent e) {
		double x = e.getX();
		double y = APPLICATION_HEIGHT - paddle.getHeight() -25;
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
		for(int i = shotsFiredList.size() - 1; i >= 0; i--) {
			GOval shot = shotsFiredList.get(i);
			shot.move(0, -5);
			checkCollision(shot);
			if(shot.getY() < 0) {
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
		add(paddle, getWidth()/2 - paddle.getWidth()/2, getHeight() - paddle.getHeight());
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

	}

	/*
	 * creates a table of invaders using the createInvared method
	 */
	private void createLineOfInvaders() {
		for (int i = 0; i < NUM_INV_PER_ROW; i++) {
			String str = " ";
			int f = rgen.nextInt(0, 2);
			if(f == 0) {
				str = "0";
			}else if(f == 1) {
				str = "1";
			}else if(f == 2) {
				str = "2";
			}

			for (int j = 0; j < NUM_INV_ROWS; j++) {

				createInvader(i * (INV_WIDTH + INV_SEP) + getWidth()/2 - (NUM_INV_PER_ROW*(INV_WIDTH + INV_SEP))/2, j * (INV_HEIGHT + INV_SEP), str);
			}
		}
	}


	private void moveInvaders() {
		if(invaderList.get(0).getX() <= 0) {
			g= g*(-1);
		}else if(invaderList.get(0).getX() +  NUM_INV_PER_ROW*(INV_WIDTH + INV_SEP) >= getWidth()) {
			g= g*(-1);
		}
		for (int i = 0; i < invaderList.size(); i++) {
			invaderList.get(i).move( g, 0);

		}
	}

	/*
	 * checks if the boss hit the player, if yes it removes a life
	 * check if the player hit any of the invaders and if yes, removes them from the game and list they are stored in
	 */
	private void checkCollision(GOval shot) {
		if(getElementAt(	shot.getX(), shot.getY()	) != null || getElementAt(	shot.getX(), shot.getY()+ BALL_SIZE	) != null || getElementAt(	shot.getX()+ BALL_SIZE, shot.getY()+ BALL_SIZE	) != null  || getElementAt(	shot.getX()+ BALL_SIZE, shot.getY()	) != null ) {
			if(getElementAt(	shot.getX(), shot.getY()	) == paddle || getElementAt(	shot.getX(), shot.getY()+ BALL_SIZE	) == paddle || getElementAt(	shot.getX()+ BALL_SIZE, shot.getY()+ BALL_SIZE	) == paddle  || getElementAt(	shot.getX()+ BALL_SIZE, shot.getY()	) == paddle ) {
				//if the invaders managed to hit the player (their paddle/rocket)
				//i will have to check the collision when the invaders start shooting and if they hit i need to lower the lives
				if(bossShotsFiredList.contains(shot)) {
					bossShotsFiredList.remove(shot);
					remove(shot);
					playerLives--;
				}
				
			//when the shot reaches the end of the screen
			}else if(shot.getY() >= getHeight()) {
				if(bossShotsFiredList.contains(shot)) {
					bossShotsFiredList.remove(shot);
					remove(shot);
				}
			
			//if the shot hits a label or when it reacts to traveling over the background
			}else if(getElementAt(	shot.getX(), shot.getY()	) == background || getElementAt(	shot.getX(), shot.getY()+ BALL_SIZE	) == background || getElementAt(	shot.getX()+ BALL_SIZE, shot.getY()+ BALL_SIZE	) == background  || getElementAt(	shot.getX()+ BALL_SIZE, shot.getY()	) == background ) {

			}else if(getElementAt(	shot.getX(), shot.getY()	) == score || getElementAt(	shot.getX(), shot.getY()+ BALL_SIZE	) == score || getElementAt(	shot.getX()+ BALL_SIZE, shot.getY()+ BALL_SIZE	) == score  || getElementAt(	shot.getX()+ BALL_SIZE, shot.getY()	) == score ) {

			}else if(getElementAt(	shot.getX(), shot.getY()	) == lives || getElementAt(	shot.getX(), shot.getY()+ BALL_SIZE	) == lives || getElementAt(	shot.getX()+ BALL_SIZE, shot.getY()+ BALL_SIZE	) == lives  || getElementAt(	shot.getX()+ BALL_SIZE, shot.getY()	) == lives ) {

			//the shot hits something else than paddle, label, background - but it does hit something --> therefore it is a shot fired by the player and it hit the invader
			}else {
					//if the top left corner hits something
				if(getElementAt(	shot.getX(), shot.getY()	) != null) {
					
					shotsFiredList.remove(shot);
					invaderList.remove(getElementAt(	shot.getX(), shot.getY()	) );
					//if the lower left corner hits something
					remove(getElementAt(	shot.getX(), shot.getY()	) );
				}else if(getElementAt(	shot.getX(), shot.getY()+ BALL_SIZE	) != null ) {
					
					shotsFiredList.remove(shot);
					invaderList.remove(getElementAt(	shot.getX(), shot.getY()+ BALL_SIZE	) );
					//if the top right corner hits something
					remove(getElementAt(	shot.getX(), shot.getY()+ BALL_SIZE	) );
				}else if(getElementAt(	shot.getX()+ BALL_SIZE, shot.getY()	) != null ) {
					
					shotsFiredList.remove(shot);
					invaderList.remove(getElementAt(	shot.getX()+ BALL_SIZE, shot.getY()	));
					//if the lower right corner hits something
					remove(getElementAt(	shot.getX()+ BALL_SIZE, shot.getY()	));
				}else if(getElementAt(	shot.getX()+ BALL_SIZE, shot.getY()	+ BALL_SIZE ) != null ) {
					
					shotsFiredList.remove(shot);
					invaderList.remove(getElementAt(	shot.getX()+ BALL_SIZE, shot.getY()	+ BALL_SIZE ) );
					//if the top side of the square hits something
					remove(getElementAt(	shot.getX()+ BALL_SIZE, shot.getY()	+ BALL_SIZE ) );
				}else if(getElementAt(	shot.getX(), shot.getY()	) != null || getElementAt(shot.getX()+ BALL_SIZE, shot.getY()	) != null) {				 
					
					shotsFiredList.remove(shot);
					invaderList.remove(getElementAt(	shot.getX(), shot.getY()));
					invaderList.remove(getElementAt(shot.getX()+ BALL_SIZE, shot.getY()	));
					//if the left side of the square hits something
					remove(getElementAt(	shot.getX(), shot.getY()));
					remove(getElementAt(shot.getX()+ BALL_SIZE, shot.getY()	));
				}else if(getElementAt(	shot.getX(), shot.getY()+ BALL_SIZE	) != null ||  getElementAt(	shot.getX(), shot.getY()	) != null) {
					
					shotsFiredList.remove(shot);
					invaderList.remove(getElementAt(	shot.getX(), shot.getY()+ BALL_SIZE	));
					invaderList.remove(getElementAt(	shot.getX(), shot.getY()	) );
					//if the right side of the square hits something
					remove(getElementAt(	shot.getX(), shot.getY()+ BALL_SIZE	));
					remove(getElementAt(	shot.getX(), shot.getY()	) );
				}else if(getElementAt(	shot.getX()+ BALL_SIZE, shot.getY()	+ BALL_SIZE ) != null || getElementAt(shot.getX()+ BALL_SIZE, shot.getY()	) != null) {
					
					shotsFiredList.remove(shot);
					invaderList.remove(getElementAt(	shot.getX()+ BALL_SIZE, shot.getY()	+ BALL_SIZE ) );
					invaderList.remove(getElementAt(shot.getX()+ BALL_SIZE, shot.getY()	));
					//if the under side of the square hits something
					remove(getElementAt(	shot.getX()+ BALL_SIZE, shot.getY()	+ BALL_SIZE ) );
					remove(getElementAt(shot.getX()+ BALL_SIZE, shot.getY()	));
				}else if(getElementAt(	shot.getX()+ BALL_SIZE, shot.getY()	+ BALL_SIZE ) != null || getElementAt(	shot.getX(), shot.getY()+ BALL_SIZE	) != null) {
					
					shotsFiredList.remove(shot);
					invaderList.remove(getElementAt(	shot.getX()+ BALL_SIZE, shot.getY()	+ BALL_SIZE ) );
					invaderList.remove(getElementAt(	shot.getX(), shot.getY()+ BALL_SIZE	));
					remove(getElementAt(	shot.getX()+ BALL_SIZE, shot.getY()	+ BALL_SIZE ) );
					remove(getElementAt(	shot.getX(), shot.getY()+ BALL_SIZE	));
				}
				
				scoreCount++;
				remove(shot);
				
			}
		}
	}


	/*
	 * creates a special invader - The Boss - it shoots back at the player
	 */
	private GImage createBoss() {
		GImage boss = new GImage("res/boss.png");
		boss.setSize(INV_WIDTH + 20, INV_HEIGHT + 20);
		add(boss, getWidth()/2 - (INV_WIDTH + 20)/2, NUM_INV_ROWS*(INV_SEP + INV_HEIGHT));
		invaderList.add(boss);
		return boss;
	}

	/*
	 * moves the boss invader, it moves differently than the main group of invaders, int w represents speed
	 */
	private void moveBoss(GImage boss) {
		if(boss.getX() <= 0) {
			w= w*(-1);
		}else if((boss.getX() +  (INV_WIDTH + 20)) >= getWidth()) {
			w= w*(-1);
		}
		boss.move( w, 0);
	}

	/*
	 * creates the shots fired by the boss invader, at random times, the boss is not shooting constantly with same breaks in between
	 */
	private void shootingBoss(GImage boss) {
		if (rgen.nextBoolean(0.05)) {
			GOval bossAttacks = new GOval(boss.getX() + INV_WIDTH/2, boss.getY() + INV_HEIGHT + 25, BALL_SIZE, BALL_SIZE);
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
			for(int i = bossShotsFiredList.size() - 1; i >= 0; i--) {
				GOval shot = bossShotsFiredList.get(i);
				shot.move(0, 5);
				checkCollision(shot);
				if(shot.getY() < 0) {
					remove(shotsFiredList.get(i));
					shotsFiredList.remove(i);
				}	
			}
	}

	private void makeNextLevel() {
		createLineOfInvaders();
		
		GImage theSmallBoss = new GImage("res/boss2.png");
		theSmallBoss.setSize(INV_WIDTH - 40, INV_HEIGHT - 40);
		add(theSmallBoss, getWidth()/2 - (INV_WIDTH - 40)/2, NUM_INV_ROWS*(INV_SEP + INV_HEIGHT) + INV_HEIGHT + 20);
		invaderList.add(theSmallBoss);
		
		if(invaderList.contains(theSmallBoss)) {
			moveBoss(theSmallBoss);
			shootingBoss(theSmallBoss);
		}
		animateBOSSShots();	
		
	}

	
	
	
	
	

}
