package com.coder5560.game.ui;

import utils.screen.AbstractGameScreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.coder5560.game.assets.Assets;
import com.coder5560.game.listener.OnClickListener;
import com.coder5560.game.listener.OnCompleteListener;
import com.coder5560.game.listener.OnResponseListener;
import com.coder5560.game.views.View;

public class CustomDialog extends View {
	Table						root;
	Table						content;
	Label						lbContent;
	Label						lbTitle;
	CustomTextButton			btnPositive, btnNegative;
	public OnResponseListener	onResponseListener;

	public void buildComponent(String name, Rectangle bound) {
		setName(name);
		setBounds(bound.x, bound.y, bound.width, bound.height);
		Image transparent = new Image(Assets.instance.ui.reg_ninepatch);
		transparent.setBounds(bound.x, bound.y, bound.width, bound.height);
		transparent.setColor(new Color(0 / 255f, 0 / 255f, 0 / 255f, 0.4f));
		transparent.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (AbstractGameScreen.keyboard.isShowing())
					AbstractGameScreen.keyboard.hide();
				hide(null);
				super.clicked(event, x, y);
			}
		});
		this.addActor(transparent);
		root = new Table();
		root.setTouchable(Touchable.enabled);
		root.setSize(8 * bound.width / 9, bound.height / 3);
		root.top();
		root.defaults().expand().fillX().center();
		root.setBackground(new NinePatchDrawable(new NinePatch(
				Assets.instance.ui.reg_ninepatch, new Color(240 / 255f,
						240 / 255f, 240 / 255f, 1f))));

		BitmapFont font_normal = Assets.instance.fontFactory.getLight20();
		LabelStyle style_normal = new LabelStyle(font_normal, new Color(
				0 / 255f, 0 / 255f, 0 / 255f, 1));

		lbTitle = new Label("Thông Báo", style_normal);

		NinePatch bg = new NinePatch(Assets.instance.ui.reg_ninepatch4, 6, 6,
				6, 6);
		btnPositive = UIUtils.getTextButton("OK", bg,
				Assets.instance.fontFactory.getLight20(), Color.WHITE,
				Color.BLACK, onAddListener);
		btnNegative = UIUtils.getTextButton("Cancel", bg,
				Assets.instance.fontFactory.getLight20(), Color.WHITE,
				Color.BLACK, onCancelListener);

		Image image = new Image(Assets.instance.ui.reg_ninepatch);
		image.setColor(Color.BLUE);
		root.add(lbTitle).height(40).colspan(2).padLeft(20).padRight(20)
				.padTop(10);
		root.row();
		root.add(image).height(4).colspan(2).padTop(10);
		root.row();

		content = new Table();
		// content.setBackground(new NinePatchDrawable(new NinePatch(
		// Assets.instance.uiA.reg_ninepatch, 6, 6, 6, 6)));
		content.setSize(root.getWidth() - 40, 120);
		Table tbScroll = new Table();
		ScrollPane scroll = new ScrollPane(tbScroll);
		scroll.setScrollingDisabled(true, false);

		lbContent = new Label("", style_normal);
		lbContent.setAlignment(Align.center, Align.left);
		lbContent.setWrap(true);
		lbContent.setWidth(300);
		tbScroll.add(lbContent).expand().fill().top();
		content.add(scroll).expand().fill().top();
		root.add(content).padLeft(20).padRight(20).padTop(20).height(120)
				.colspan(2);
		root.row();

		root.add(btnPositive).height(60).padBottom(10).padTop(20);
		root.add(btnNegative).height(60).padBottom(10).padTop(20);
		root.setPosition(getX() + getWidth() / 2 - root.getWidth() / 2, getY()
				+ getHeight() / 2 - root.getHeight() / 2);
		this.addActor(root);
	}

	public void setNegativeText(String negativeText) {
		if (btnNegative != null)
			btnNegative.setText(negativeText);
	}

	public void setPositiveText(String positiveText) {
		if (btnPositive != null)
			btnPositive.setText(positiveText);
	}

	public void setTitle(String title) {
		if (lbTitle != null)
			lbTitle.setText(title);
	}

	public void setContent(String txtContent) {
		if (lbContent != null) {
			lbContent.setText(txtContent);
			// lbContent.setAlignment(Align.center, Align.left);
			// lbContent.setWrap(true);
			// lbContent.setWidth(root.getWidth() - 20);
		}
	}

	@Override
	public void show(OnCompleteListener listener) {
		_viewController.addView(this);
		super.show(listener);
		setVisible(true);
	}

	@Override
	public void hide(OnCompleteListener listener) {
		super.hide(listener);
		setVisible(false);
		this.clear();
		_viewController.removeView(getName());
	}

	@Override
	public void update(float delta) {
		super.update(delta);
	}

	@Override
	public void destroyComponent() {
		super.destroyComponent();
	}

	@Override
	public void back() {
		System.out.println("Back In dialog");
		hide(null);
	}

	public void setOnResponseListener(OnResponseListener listener) {
		this.onResponseListener = listener;
	}

	final OnClickListener	onAddListener		= new OnClickListener() {

													@Override
													public void onClick(
															float x, float y) {
														if (AbstractGameScreen.keyboard
																.isShowing())
															AbstractGameScreen.keyboard
																	.hide();
														onResponseListener
																.onOk();
														hide(null);
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
														hide(null);
													}
												};
}
