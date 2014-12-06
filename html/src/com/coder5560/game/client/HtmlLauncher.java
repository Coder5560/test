package com.coder5560.game.client;

import utils.screen.GameCore;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.coder5560.game.screens.FlashScreen;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(480, 320);
        }

        @Override
        public ApplicationListener getApplicationListener () {
        	GameCore game = new GameCore() {
    			@Override
    			public void create() {
    				super.create();
    				setScreen(new FlashScreen(this));
    			}
    		}; 
        	return game;
        }
}