package com.coder5560.game.screens;

import utils.screen.AbstractGameScreen;
import utils.screen.GameCore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.coder5560.game.assets.Assets;
import com.coder5560.game.enums.Constants;

public class FlashScreen extends AbstractGameScreen {
	Image	splash;
	Sprite	sprite;

	boolean	loaded		= false;
	boolean	showMessage	= false;

	public FlashScreen(GameCore game) {
		super(game);
		sprite = new Sprite(new Texture(Gdx.files.internal("Img/splash.jpeg")));
		sprite.setSize(Constants.WIDTH_SCREEN, Constants.HEIGHT_SCREEN);
	}

	@Override
	public void show() {
		super.show();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
	}

	float	time	= 0;

	@Override
	public void update(float delta) {
		if (time <= 0.1f)
			time += delta;
		if (!loaded && time > 0.1f) {
			if (Assets.instance.assetManager.update()) {
				Assets.instance.init();
				buildComponent(stage);
				loaded = true;
			}
		}

		if (loaded && !showMessage) {
			showMessage = true;
		}
	}

	boolean	switchScreen	= false;

	void switchScreen() {
		if (!switchScreen) {
			parent.setScreen(new GameScreen(parent));
			switchScreen = true;
		}
	}

	@Override
	public void render(float delta) {
		if (showMessage && splash.getActions().size == 0) {
			switchScreen();
		}
		super.render(delta);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		if (!loaded)
			sprite.draw(batch);
		batch.end();
	}

	void buildComponent(Stage stage) {
		splash = new Image(new Texture(Gdx.files.internal("Img/splash.jpeg")));
		splash.setSize(Constants.WIDTH_SCREEN, Constants.HEIGHT_SCREEN);
		Action act0 = Actions.alpha(0f, 1f);
		splash.addAction(Actions.sequence(act0));
		stage.addActor(splash);
	}
}
