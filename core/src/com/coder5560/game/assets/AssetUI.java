package com.coder5560.game.assets;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AssetUI {
	public TextureRegion	reg_ninepatch;
	public TextureRegion	reg_ninepatch1;
	public TextureRegion	reg_ninepatch2;
	public TextureRegion	reg_ninepatch3;
	public TextureRegion	reg_ninepatch4;

	public AssetUI(TextureAtlas textureAtlas) {
		reg_ninepatch = textureAtlas.findRegion("ninepatch_none");
		reg_ninepatch1 = textureAtlas.findRegion("ninepatch_rounded");
		reg_ninepatch2 = textureAtlas.findRegion("ninepatch_outline");
		reg_ninepatch3 = textureAtlas.findRegion("ninepatch_stock");
		reg_ninepatch4 = textureAtlas.findRegion("ninepatch_shadow_bottom");
	}

}