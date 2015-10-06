import java.util.Random;

//UIUC CS125 FALL 2014 MP. File: RainGame.java, CS125 Project: PairProgramming, Version: 2015-09-28T22:11:14-0500.040275944

/**
 * A game of raining numbers.
 * 
 * @author jmgreen5 magerko2
 */
public class RainGame {
	
	static Random rand = new Random();
	/* constant declarations */
	private static final Position2d DEFAULT_POSITION = new Position2d(0,
			Zen.getZenHeight() / 2);
	private static final Velocity2d DEFAULT_VELOCITY = new Velocity2d(0, 1);

	private static final int BASE_SCORE = 5000;
	private static final int MAX_SCALE_LEVEL = 5;
	private static final int MIN_RANDOMIZE_LEVEL = MAX_SCALE_LEVEL;

	private static final int SLEEP_TIME_IN_MS = 50;
	private static final int CHAR_HEIGHT_PX = 50;
	private static final int CHAR_WIDTH_PX = 36;
	private static final Color TEXT_COLOR = new Color(255, 0, 255);
	private static final Color BACKGROUND_COLOR = new Color(0, 255, 0);
	private static final Position2d LEVEL_POSITION = new Position2d(10, 60);
	private static final Position2d SCORE_POSITION = new Position2d(10, 120);

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

		public void randomize(int minX, int maxX, int minY, int maxY) {
			this.x = minX + (int) (maxX * Math.random());
			this.y = minY + (int) (maxY * Math.random());
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

		public void scale(int scalar) {
			dx *= scalar;
			dy *= scalar;
		}
		
		public void randomize() {
			this.dx = ((Math.random() > .5) ? 1 : -1) * ((Math.random() > .75) ? 0 : 1);
			this.dy = ((Math.random() > .5) ? 1 : -1) * ((Math.random() > .75 && this.dx != 0) ? 0 : 1);
		}

		@Override
		public Velocity2d clone() {
			return new Velocity2d(dx, dy);
		}
	}

	/* helper method declarations */
	private static String generateRandomText() {
		return String.valueOf((int) (Math.random() * 999));
	}

	private static int getHorizontalOffset(String text) {
		return text.length() * CHAR_WIDTH_PX;
	}

	private static int calculateScore(long timeElapsed) {
		if (timeElapsed <= 0) return 0;
		return (int) (BASE_SCORE / timeElapsed);
	}
	
	private static void applyPositionExcitement(Position2d position, String text, int level) {
		// randomly-assigns position
		int textOffset = getHorizontalOffset(text);
		position.randomize(0, Zen.getZenWidth() - textOffset, CHAR_HEIGHT_PX,
				Zen.getZenHeight() - CHAR_HEIGHT_PX);
	}

	private static void applyVelocityExcitement(Velocity2d velocity, int level) {
		if (level >= MIN_RANDOMIZE_LEVEL) {
			// apply the randomization aspect only after a certain point
			velocity.randomize();
		}
		
		// always apply level-based velocity scaling, up to a certain point
		int velocityScalar = level;
		velocityScalar = (level > MAX_SCALE_LEVEL) ? MAX_SCALE_LEVEL : level;
		velocity.scale(velocityScalar);
	}

	private static void handleBoundaryCrossings(Position2d position,
			Velocity2d velocity, String text) {
		int characterOffset = getHorizontalOffset(text);
		int nextX = position.x + velocity.dx + characterOffset;
		int nextY = position.y + velocity.dy - CHAR_HEIGHT_PX;

		if (nextX > Zen.getZenWidth() || nextX - characterOffset < 0) {
			velocity.dx *= -1;
		}
		if (nextY + CHAR_HEIGHT_PX > Zen.getZenHeight() || nextY < 0) {
			velocity.dy *= -1;
		}
	}

	private static void updateZenInterface(String text, Position2d position,
			int level, int score) {
		Zen.setColor(TEXT_COLOR.red, TEXT_COLOR.green, TEXT_COLOR.blue);
		Zen.fillRect(0, 0, Zen.getZenWidth(), Zen.getZenHeight());
		Zen.setColor(BACKGROUND_COLOR.red, BACKGROUND_COLOR.green,
				BACKGROUND_COLOR.blue);
		Zen.drawText(text, position.x, position.y);
		Zen.drawText(String.format("Level: %d", level), LEVEL_POSITION.x,
				LEVEL_POSITION.y);
		Zen.drawText(String.format("Score: %d", score), SCORE_POSITION.x,
				SCORE_POSITION.y);
	}

	/* program entry-point */
	public static void main(String[] args) {

		Zen.setFont("Helvetica-64");

		int score = 0, level = 0;
		String text = "";
		Position2d position = DEFAULT_POSITION.clone();
		Velocity2d velocity = DEFAULT_VELOCITY.clone();

		long startTime = System.currentTimeMillis();
		
		TextIO.putln("Enter 99 to skip to level 6. Enter 1 for Easy Mode (Level 1). Enter 42 for Random Difficulty.");
		int levelSkipCommand = TextIO.getlnInt();
		
		if (levelSkipCommand == 99) {
			level = 5;
			TextIO.putln("Congrats tryhard! Return to game.");
			Zen.isRunning();
		}
		else if (levelSkipCommand == 1){
			level = 0;
			TextIO.putln("Easy does it. Return to game.");
			Zen.isRunning();
		}
		else if (levelSkipCommand == 42) {
			level = rand.nextInt(21);
			TextIO.putln("Haha good luck man. Return to game.");
			Zen.isRunning();
			
		}
		
		while (Zen.isRunning()) {

			// reset condition
			if (text.isEmpty()) {
				// reset the visible text
				text = generateRandomText();

				// calculate the resulting score and reset the timer
				long timeElapsed = System.currentTimeMillis() - startTime;
				startTime = System.currentTimeMillis();
				score += calculateScore(timeElapsed);
				level++;

				position = DEFAULT_POSITION.clone();
				velocity = DEFAULT_VELOCITY.clone();

				applyPositionExcitement(position, text, level);
				applyVelocityExcitement(velocity, level);
			}

			// make sure that the text doesn't disappear forever
			handleBoundaryCrossings(position, velocity, text);

			// perform necessary UI updates
			updateZenInterface(text, position, level, score);

			// increase position by current velocity
			position.x += velocity.dx;
			position.y += velocity.dy;

			// find out what keys the user has been pressing.
			String user = Zen.getEditText();

			// reset the keyboard input to an empty string
			// so next iteration we will only get the most recently pressed
			// keys.
			Zen.setEditText("");

			for (int i = 0; i < user.length() && !text.isEmpty(); i++) {
				char c = user.charAt(i);
				if (c == text.charAt(0))
					text = text.substring(1, text.length());
			}

			// flip the buffer so that there is no 'flicker'
			Zen.flipBuffer();
			Zen.sleep(SLEEP_TIME_IN_MS);

		}
	}

}
