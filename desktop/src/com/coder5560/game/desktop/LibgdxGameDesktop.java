package com.coder5560.game.desktop;

import utils.screen.GameCore;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.coder5560.game.enums.Constants;
import com.coder5560.game.screens.FlashScreen;

public class LibgdxGameDesktop {
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Constants.WIDTH_SCREEN;
		config.height = Constants.HEIGHT_SCREEN;
		
		GameCore game = new GameCore() {
			@Override
			public void create() {
				super.create();
				setScreen(new FlashScreen(this));
			}
		};
		
		new LwjglApplication(game, config);
	}
}
