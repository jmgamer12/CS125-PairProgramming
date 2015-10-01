//UIUC CS125 FALL 2014 MP. File: RainGame.java, CS125 Project: PairProgramming, Version: 2015-09-28T22:11:14-0500.040275944


/**
 * A game of raining numbers.
 * @author jmgreen5 magerko2
 */
public class RainGame {
	
	/* internal objects */
	private static class Color {
		public int red;
		public int green;
		public int blue;
		
		public Color(int red, int green, int blue) {
			this.red = red;
			this.blue = blue;
			this.green = green;
		}	
	}
	
	private static class Position2d {
		public int x;
		public int y;
		
		public Position2d(int x, int y) {
			this.x = x;
			this.y = y;
		}	
		
		@Override
		public Position2d clone() {
			return new Position2d(x, y);
		}
	}
	
	private static class Velocity2d {
		public int dx;
		public int dy;
		
		public Velocity2d(int dx, int dy) {
			this.dx = dx;
			this.dy = dy;
		}
		
		@Override
		public Velocity2d clone() {
			return new Velocity2d(dx, dy);
		}
	}
	
	/* constant declarations */
	private static final Position2d INITIAL_POSITION = new Position2d(0, Zen.getZenHeight() / 2);
	private static final Velocity2d INITIAL_VELOCITY = new Velocity2d(0, 1);
	
	private static final int BASE_SCORE = 3000;
	
	private static final Color TEXT_COLOR = new Color(255, 0, 255);
	private static final Color BACKGROUND_COLOR = new Color(0, 255, 0);
	private static final Position2d LEVEL_POSITION = new Position2d(10, 60);
	private static final Position2d SCORE_POSITION = new Position2d(10, 120);
	
	/* helper method declarations */
	private static String generateRandomText() {
		return String.valueOf((int) (Math.random() * 999));
	}
	
	private static int calculateScore(long timeElapsed) {
		if (timeElapsed <= 0) return 0;
		return (int) (BASE_SCORE / timeElapsed);
	}
	
	private static void updateZenInterface(String text, Position2d position) {
		Zen.setColor(TEXT_COLOR.red, TEXT_COLOR.green, TEXT_COLOR.blue);
		Zen.fillRect(0, 0, Zen.getZenWidth(), Zen.getZenHeight());
		Zen.setColor(BACKGROUND_COLOR.red, BACKGROUND_COLOR.green, BACKGROUND_COLOR.blue);
		Zen.drawText(text, position.x, position.y);
		Zen.drawText("Level: 0", LEVEL_POSITION.x, LEVEL_POSITION.y);
		Zen.drawText("Score: 0", SCORE_POSITION.x, SCORE_POSITION.y);
	}

	/* program entry-point */
	public static void main(String[] args) {
		
		Zen.setFont("Helvetica-64");

		int score = 0;
		String text = "";
		Position2d position = INITIAL_POSITION.clone();
		Velocity2d velocity = INITIAL_VELOCITY.clone();
		
		long startTime = System.currentTimeMillis();
		
		while (Zen.isRunning()) {
			
			// reset condition
			if (text.isEmpty()) {
				// reset the visible text
				text = generateRandomText();
				
				// reset the text position and rate
				position = INITIAL_POSITION.clone();
				velocity = INITIAL_VELOCITY.clone();
				
				// calculate the resulting score and reset the timer
				long timeElapsed = System.currentTimeMillis() - startTime;
				startTime = System.currentTimeMillis();
				score += calculateScore(timeElapsed);
			}

			
			// perform necessary UI updates
			updateZenInterface(text, position);
			
			// increase position by current velocity
			position.x += velocity.dx;
			position.y += velocity.dy;
			
			// find out what keys the user has been pressing.
			String user = Zen.getEditText();
			
			// reset the keyboard input to an empty string
			// so next iteration we will only get the most recently pressed keys.
			Zen.setEditText("");
			
			for(int i=0;i < user.length(); i++) {
				// check to see if the user entered the correct character
				// if correct, remove that character
				char c = user.charAt(i);
				if(c == text.charAt(0))
					text = text.substring(1,text.length());
			}
			
			// sleep for 90 milliseconds
			Zen.sleep(90);

		}
	}

}
