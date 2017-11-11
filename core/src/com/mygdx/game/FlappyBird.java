package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {


	SpriteBatch batch;
	ShapeRenderer shapeRenderer;
	Texture background;
	Texture bottomTube;
	BitmapFont bitmapFont, bitmapFont1;
	Texture topTube;
	int score = 0;
	Texture[] birds;
	Circle birdCircle;
	int flapState = 0;
	float birdY = 0;
	float velocity = 0;
	int gamestate = 0;
	int gravity = 2;
	float gap = 400;
	float maxTubeOffset = 0;
	Random randomGenerator;
	int scoringTube = 0;
	float tubevelocity = 4;
	float tubeX[] = new float[4];
	float tubeOffset[] = new float[4];
	Rectangle[] topTubeRectangles;
	Rectangle[] bottomTubeRectangles;
	int numberOfTubes = 4;
	float distance;
	Texture gameover;
	Preferences prefrences;
	int highscore = 0;
	Sound birdflysound, scoresound, diesound;


	@Override
	public void create () {

		batch = new SpriteBatch();
		birdCircle = new Circle();
		//shapeRenderer=new ShapeRenderer();
		gameover = new Texture("flappybird_fulmer.png");
		background = new Texture("bg.png");
		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");
		bottomTube = new Texture("bottomtube.png");
		topTube = new Texture("toptube.png");

		maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
		randomGenerator = new Random();
		topTubeRectangles = new Rectangle[numberOfTubes];
		bitmapFont = new BitmapFont();
		bitmapFont1 = new BitmapFont();
		bitmapFont1.setColor(Color.RED);
		bitmapFont1.getData().scale(3);
		bitmapFont.setColor(Color.WHITE);
		bitmapFont.getData().scale(5);
		bottomTubeRectangles = new Rectangle[numberOfTubes];
		distance = Gdx.graphics.getWidth() * 3 / 4;
		birdflysound = Gdx.audio.newSound(Gdx.files.internal("data/sfx_wing.wav"));
		scoresound = Gdx.audio.newSound(Gdx.files.internal("data/sfx_point.wav"));
		diesound = Gdx.audio.newSound(Gdx.files.internal("data/sfx_hit.wav"));
		startGame();
	}
	public void startGame() {

		birdY = Gdx.graphics.getHeight() / 2 - birds[flapState].getHeight() / 2;
		for (int i = 0; i < numberOfTubes; i++) {
			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
			tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getHeight() + i * distance;
			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();
		}
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		if (gamestate == 1) {
			if (tubeX[scoringTube] < Gdx.graphics.getWidth() / 2) {
				score++;
				Gdx.app.log("Score", String.valueOf(score));
				scoresound.play();
				if (scoringTube < numberOfTubes - 1) {
					scoringTube++;
				} else {
					scoringTube = 0;
				}
			}
			if (Gdx.input.justTouched()) {
				birdflysound.play();
				velocity = -25;
			}
			for (int i = 0; i < numberOfTubes; i++) {
				if (tubeX[i] < -topTube.getWidth()) {
					tubeX[i] = tubeX[i] + numberOfTubes * distance;
					tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
				} else {
					tubeX[i] = tubeX[i] - tubevelocity;
				}
				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);
				topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
				bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());
			}
			if (birdY > 0) {
				velocity = velocity + gravity;
				birdY = birdY - velocity;
			} else {
				gamestate = 2;
			}
		} else if (gamestate == 0) {
			if (Gdx.input.justTouched()) {
				birdflysound.play();
				Gdx.app.log("touched:", "Yep");
				gamestate = 1;
			}
		} else if (gamestate == 2) {

			batch.draw(gameover, Gdx.graphics.getWidth() / 2 - gameover.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gameover.getHeight() / 2);
			prefrences = Gdx.app.getPreferences("game");
			if (prefrences.contains("highscore")) {
				bitmapFont.draw(batch, String.valueOf(prefrences.getString("highscore")), Gdx.graphics.getWidth() - 100, 100);
			} else {
				bitmapFont.draw(batch, String.valueOf(0), Gdx.graphics.getWidth() - 100, 100);
			}
			if (score > highscore) {
				highscore = score;
				prefrences = Gdx.app.getPreferences("game");
				prefrences.putString("highscore", String.valueOf(highscore));
				prefrences.flush();
				Gdx.app.log("Highscore", String.valueOf(highscore));
			}
			if (Gdx.input.justTouched()) {
				Gdx.app.log("touched:", "Yep");
				gamestate = 1;
				startGame();
				score = 0;
				scoringTube = 0;
				velocity = 0;
			}
		}
		if (flapState == 0)
			flapState = 1;
		else
			flapState = 0;
		batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);
		bitmapFont1.draw(batch, "HIGH-SCORE", Gdx.graphics.getWidth() - 400, 180);
		bitmapFont.draw(batch, String.valueOf(score), 50, 100);
		//   bitmapFont.draw(batch,Gdx.app.getPreferences("game").getString("highscore"),Gdx.graphics.getWidth()-100,100);
		birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flapState].getHeight() / 2, birds[flapState].getWidth() / 2);
		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.setColor(Color.RED);
		//shapeRenderer.circle(birdCircle.x,birdCircle.y,birdCircle.radius);
		for (int i = 0; i < numberOfTubes; i++) {
			//shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i],topTube.getWidth(),topTube.getHeight());
			//shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i],bottomTube.getWidth(),bottomTube.getHeight());
			if (Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {
				Gdx.app.log("Collision:", "Yes!");
				gamestate = 2;
			}
		}
		//shapeRenderer.end();
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
		scoresound.dispose();
		birdflysound.dispose();
	}
}
