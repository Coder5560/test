package com.coder5560.game.assets;

import utils.factory.FontFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;
import com.coder5560.game.enums.Constants;

public class Assets implements Disposable, AssetErrorListener {
	public static final String	TAG			= Assets.class.getName();
	public static final Assets	instance	= new Assets();

	public AssetManager			assetManager;
	public AssetUI				ui;

	public FontFactory			fontFactory;

	private Assets() {
		assetManager = new AssetManager();
		assetManager.setErrorListener(this);
		load();
		fontFactory = new FontFactory();
		
	}

	public void load() {
		
		assetManager.load(Constants.TEXTURE_ATLAS_UI, TextureAtlas.class);
		assetManager.finishLoading();
	}

	public void init() {
		TextureAtlas atlasUI = assetManager.get(Constants.TEXTURE_ATLAS_UI);
		for (Texture t : atlasUI.getTextures()) {
			t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
		ui = new AssetUI(atlasUI);
	}

	@Override
	public void dispose() {
		assetManager.dispose();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void error(AssetDescriptor asset, Throwable throwable) {
		Gdx.app.error(TAG, "Couldn't load asset '" + asset.fileName + "'", (Exception) throwable);
	}
}
