package com.sadzakov.sofija;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import helpers.Score;
import scenes.Gameplay;

public class GameMain extends Game {

	SpriteBatch batch;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		Score.getScoreInstance().initializeGameData();
		setScreen(new Gameplay(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}

	public SpriteBatch getBatch() {
		return this.batch;
	}
}
