package com.coder5560.game.screens;

import imp.view.TestView;
import utils.factory.StringSystem;
import utils.screen.AbstractGameScreen;
import utils.screen.GameCore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.coder5560.game.enums.Constants;
import com.coder5560.game.views.TraceView;
import com.coder5560.game.views.ViewController;

public class GameScreen extends AbstractGameScreen {
	ViewController	controller;
	Image			flash;

	public GameScreen(GameCore game) {
		super(game);
	}

	@Override
	public void show() {
		super.show();
		controller = new ViewController(parent);
		controller.build(stage);
		controller.setFacebookConnector(parent.facebookConnector);
		Gdx.input.setCatchBackKey(true);
		System.gc();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void update(float delta) {
		controller.update(delta);
		if (isExit) {
			timeExit += delta;
			if (timeExit >= 2) {
				timeExit = 0;
				isExit = false;
			}
		}

	}

	@Override
	public void drawBatch(SpriteBatch batch) {

	}

	@Override
	public void drawShapeLine(ShapeRenderer shapeRenderer) {
	}

	@Override
	public void drawShapeFill(ShapeRenderer shapeRenderer) {

	}

	boolean	isExit		= false;
	float	timeExit	= 0;

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.BACK || keycode == Keys.ESCAPE) {
			if (AbstractGameScreen.keyboard.isShowing()) {
				AbstractGameScreen.keyboard.hide();
				AbstractGameScreen.keyboard.clear();
			} else {
				TraceView.instance.debug();
				if (controller.getView(TraceView.instance.getLastView()) != null)
					controller.getView(TraceView.instance.getLastView()).back();
			}
		}
		if (keycode == Keys.NUM_1) {
			if (!TraceView.instance.containView(StringSystem.VIEW_TEST)) {
				TestView view = new TestView();
				view.build(stage, controller, StringSystem.VIEW_TEST,
						new Rectangle(0, 0, Constants.WIDTH_SCREEN,
								Constants.HEIGHT_SCREEN));
			}
		}
		if (keycode == Keys.NUM_2) {
		}
		if (keycode == Keys.NUM_3) {
		}
		if (keycode == Keys.NUM_4) {
		}
		return false;
	}

	boolean	connect	= false;
	float	offset	= 8;

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		viewport.unproject(touchPoint.set(screenX, screenY));
		return false;
	}

	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (touchPoint.isZero())
			return false;
		Vector2 touchUp = new Vector2();
		viewport.unproject(touchUp.set(screenX, screenY));
		return false;
	};

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (touchPoint.isZero())
			return false;
		return false;
	}

}
