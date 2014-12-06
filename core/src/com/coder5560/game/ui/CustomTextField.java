package com.coder5560.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class CustomTextField extends TextField {

	public CustomTextField(String text, Skin skin, String styleName) {
		super(text, skin, styleName);
	}

	public CustomTextField(String text, Skin skin) {
		super(text, skin);
	}

	public CustomTextField(String text, TextFieldStyle style) {
		super(text, style);
	}
}
