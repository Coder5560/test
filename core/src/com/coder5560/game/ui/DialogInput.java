package com.coder5560.game.ui;

import java.util.HashMap;

import utils.keyboard.VirtualKeyboard.OnDoneListener;
import utils.keyboard.VirtualKeyboard.OnHideListener;
import utils.screen.AbstractGameScreen;
import utils.screen.Toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.coder5560.game.assets.Assets;
import com.coder5560.game.listener.OnClickListener;
import com.coder5560.game.listener.OnResponseListener;
import com.coder5560.game.views.View;

public class DialogInput extends View {
	Table						root;
	public CustomTextField		tfName, tfQuality;
	public OnResponseListener	onResponseListener;
	HashMap<String, String>		fomula;
	CustomTextButton			btnOk, btnCancel;
	Label						lbTitle;

	public void buildComponent() {
		setName(name);
		Image transparent = new Image(Assets.instance.ui.reg_ninepatch);
		transparent.setBounds(0, 0, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		transparent.setColor(new Color(0 / 255f, 0 / 255f, 0 / 255f, 0.4f));
		transparent.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (AbstractGameScreen.keyboard.isShowing())
					AbstractGameScreen.keyboard.hide();
				hide();
				super.clicked(event, x, y);
			}
		});
		this.addActor(transparent);

		root = new Table();
		root.setTouchable(Touchable.enabled);
		root.setSize(6 * getBound().width / 9, getBound().height / 3);
		root.top();
		root.defaults().expand().fillX().expandY().center();
		root.setBackground(new NinePatchDrawable(new NinePatch(
				Assets.instance.ui.reg_ninepatch, new Color(240 / 255f,
						240 / 255f, 240 / 255f, 1f))));

		tfName = new CustomTextField("",
				UIUtils.getTextFieldStyle(new NinePatch(
						Assets.instance.ui.reg_ninepatch2, 10, 10, 10, 10)));
		tfName.setMessageText("Dữ liệu thứ nhất");

		tfQuality = new CustomTextField("",
				UIUtils.getTextFieldStyle(new NinePatch(
						Assets.instance.ui.reg_ninepatch2, 10, 10, 10, 10)));
		tfQuality.setMessageText("Dữ liệu thứ hai");

		BitmapFont font_normal = Assets.instance.fontFactory.getLight20();
		LabelStyle style_normal = new LabelStyle(font_normal, new Color(
				0 / 255f, 0 / 255f, 0 / 255f, 1));

		lbTitle = new Label("Nhập Dữ Liệu", style_normal);

		NinePatch bg = new NinePatch(Assets.instance.ui.reg_ninepatch4, 6, 6,
				6, 6);

		btnOk = UIUtils.getTextButton("OK", bg,
				Assets.instance.fontFactory.getLight20(), Color.WHITE,
				Color.BLACK, onAddListener);
		btnCancel = UIUtils.getTextButton("CANCEL", bg,
				Assets.instance.fontFactory.getLight20(), Color.WHITE,
				Color.BLACK, onCancelListener);

		Image image = new Image(Assets.instance.ui.reg_ninepatch);
		image.setColor(Color.BLUE);
		root.add(lbTitle).height(60).colspan(2).padLeft(20);
		root.row();
		root.add(image).height(4).colspan(2).padBottom(20);
		root.row();
		root.add(tfName).padLeft(20).padRight(20).padBottom(20).height(60)
				.colspan(2);
		root.row();
		root.add(tfQuality).padLeft(20).padRight(20).padBottom(30).height(60)
				.colspan(2);
		root.row();

		root.add(btnOk).height(60).padBottom(10);
		root.add(btnCancel).height(60).padBottom(10);
		root.setPosition(getX() + getWidth() / 2 - root.getWidth() / 2, getY()
				+ getHeight() / 2 - root.getHeight() / 2);
		this.addActor(root);

		buildTextFieldListener();

	}

	@Override
	public void show() {
		_viewController.addView(this);
		super.show();
		setVisible(true);
	}

	@Override
	public void hide() {
		super.hide();
		setVisible(false);
		this.clear();
		_viewController.removeView(getName());
	}

	@Override
	public void update() {
		super.update();
	}

	@Override
	public void destroyComponent() {
		super.destroyComponent();
	}

	@Override
	public void back() {
		super.back();
	}

	public void setOnResponseListener(HashMap<String, String> fomula,
			OnResponseListener listener) {
		this.fomula = fomula;
		this.onResponseListener = listener;
	}

	public void buildTextFieldListener() {

		tfName.addListener(new InputListener() {

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				tfName.setOnscreenKeyboard(AbstractGameScreen.keyboard);
				tfName.toFront();
				UIUtils.registerKeyBoard(tfName, _onDoneListener,
						_onHideListener);
				root.addAction(Actions.moveTo(root.getX(), 400, .2f,
						Interpolation.exp10Out));
				return false;
			}
		});

		tfQuality.addListener(new InputListener() {

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				tfQuality.setOnscreenKeyboard(AbstractGameScreen.keyboard);
				tfQuality.toFront();
				UIUtils.registerKeyBoard(tfQuality, _onDoneListener,
						_onHideListener);
				root.addAction(Actions.moveTo(root.getX(), 400, .2f,
						Interpolation.exp10Out));
				return false;
			}
		});

	}

	public void setTitle(String text) {
		if (lbTitle != null)
			lbTitle.setText(text);
	}

	public void setFomulaName(String fomulaName) {
		if (tfName != null)
			tfName.setText(fomulaName);
	}

	public void setQuality(String quality) {
		if (tfQuality != null)
			tfQuality.setText(quality);
	}

	public void setNegativeText(String negative) {
		if (btnCancel != null)
			btnCancel.setText(negative);
	}

	public void setPositiveText(String positive) {
		if (btnOk != null)
			btnOk.setText(positive);
	}

	final OnDoneListener	_onDoneListener		= new OnDoneListener() {
													@Override
													public void done() {
														AbstractGameScreen.keyboard
																.reset();
														getStage()
																.setKeyboardFocus(
																		null);
													}
												};
	final OnHideListener	_onHideListener		= new OnHideListener() {
													@Override
													public void hide() {
														AbstractGameScreen.keyboard
																.reset();
														getStage()
																.setKeyboardFocus(
																		null);
														root.addAction(Actions.moveTo(
																root.getX(),
																getY()
																		+ getHeight()
																		/ 2
																		- root.getWidth()
																		/ 4,
																.2f,
																Interpolation.exp10Out));
													}
												};

	final OnClickListener	onAddListener		= new OnClickListener() {

													@Override
													public void onClick(
															float x, float y) {
														if (AbstractGameScreen.keyboard
																.isShowing())
															AbstractGameScreen.keyboard
																	.hide();

														if (tfName
																.getText()
																.equalsIgnoreCase(
																		"")) {
															Toast.makeText(
																	getStage(),
																	"Bạn chưa nhập đủ thông tin !",
																	Toast.LENGTH_SHORT);
															return;
														}
														if (tfQuality
																.getText()
																.equalsIgnoreCase(
																		"")) {
															Toast.makeText(
																	getStage(),
																	"Bạn chưa nhập đủ thông tin !",
																	Toast.LENGTH_SHORT);
															return;
														}
														if (onResponseListener != null) {
															String fName = tfName
																	.getText();
															String fQuality = tfQuality
																	.getText();

															if (fomula
																	.containsKey(fName)) {
																String value = fomula
																		.get(fName);
																if (value
																		.equalsIgnoreCase(fQuality)) {
																	Toast.makeText(
																			getStage(),
																			"Nguyên liệu đã tồn tại !",
																			Toast.LENGTH_SHORT);
																	return;
																}
															}

															onResponseListener
																	.onOk(tfName
																			.getText(),
																			tfQuality
																					.getText());
															hide();
														}

													}
												};

	final OnClickListener	onCancelListener	= new OnClickListener() {

													@Override
													public void onClick(
															float x, float y) {
														if (AbstractGameScreen.keyboard
																.isShowing())
															AbstractGameScreen.keyboard
																	.hide();
														hide();
													}
												};
}
