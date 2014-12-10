package imp.view;

import utils.factory.StringSystem;
import utils.listener.CustomListener;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.coder5560.game.assets.Assets;
import com.coder5560.game.enums.Constants;
import com.coder5560.game.listener.OnCompleteListener;
import com.coder5560.game.ui.UIUtils;
import com.coder5560.game.views.TraceView;
import com.coder5560.game.views.View;

public class MainMenu extends View {
	private Image	tranBg;
	Table			content;

	public MainMenu buildComponent() {
		getViewController().getGameScreen().setGestureDetector(
				new GestureDetector(customListener));
		tranBg = new Image(new NinePatch(Assets.instance.ui.reg_ninepatch,
				new Color(00, 00, 00, .4f)));
		// tranBg.setSize(Constants.WIDTH_SCREEN, Constants.HEIGHT_SCREEN
		// - Constants.HEIGHT_ACTIONBAR);
		tranBg.setSize(Constants.WIDTH_SCREEN, Constants.HEIGHT_SCREEN);
		tranBg.setVisible(false);
		tranBg.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				hide(null);
			}
		});

		content = new Table();
		content.setSize(Constants.WIDTH_MAINMENU, Constants.HEIGHT_SCREEN);

		// content.setSize(Constants.WIDTH_MAINMENU, Constants.HEIGHT_SCREEN
		// - Constants.HEIGHT_ACTIONBAR);
		content.setPosition(-content.getWidth(), 0);
		content.setBackground(new NinePatchDrawable(new NinePatch(
				Assets.instance.ui.reg_ninepatch, Color.BLACK)));

		Label lb = UIUtils.getLabel("Menu View", Color.BLACK);
		lb.setAlignment(Align.center);
		content.add(lb).expand().fill().center();

		addActor(tranBg);
		addActor(content);
		return this;

	}

	@Override
	public void show(OnCompleteListener listener) {
		super.show(listener);
		tranBg.setVisible(true);
		content.addAction(Actions.sequence(Actions.moveTo(0, 0, 0.5f,
				Interpolation.pow5Out)));

		// Actor actor = (Actor) (getViewController()
		// .getView(StringSystem.VIEW_HOME));
		// Actor actor2 = (Actor) (getViewController()
		// .getView(StringSystem.VIEW_ACTION_BAR));
		// if (actor != null)
		// actor.addAction(Actions.sequence(Actions.moveBy(
		// Constants.WIDTH_MAINMENU, 0, 0.5f, Interpolation.pow5Out)));
		// if (actor2 != null)
		// actor2.addAction(Actions.sequence(Actions.moveBy(
		// Constants.WIDTH_MAINMENU, 0, 0.5f, Interpolation.pow5Out)));
	}

	@Override
	public void hide(OnCompleteListener listener) {
		super.hide(listener);
		tranBg.setVisible(false);
		content.addAction(Actions.moveTo(-content.getWidth(), 0, 0.5f,
				Interpolation.pow5Out));
		// Actor actor = (Actor) (getViewController()
		// .getView(StringSystem.VIEW_HOME));
		// Actor actor2 = (Actor) (getViewController()
		// .getView(StringSystem.VIEW_ACTION_BAR));
		// if (actor != null)
		// actor.addAction(Actions.sequence(Actions.moveBy(
		// -Constants.WIDTH_MAINMENU, 0, 0.5f, Interpolation.pow5Out)));
		// if (actor2 != null)
		// actor2.addAction(Actions.sequence(Actions.moveBy(
		// -Constants.WIDTH_MAINMENU, 0, 0.5f, Interpolation.pow5Out)));
		TraceView.instance.removeView(this.getName());
	}

	Actor	bar, home;

	@Override
	public void update(float delta) {
		if (tranBg.isVisible()) {
			float alpha = (content.getWidth() - content.getX())
					/ content.getWidth();
			if (content.getX() == 0)
				alpha = 1;
			tranBg.setColor(tranBg.getColor().r, tranBg.getColor().g,
					tranBg.getColor().b, alpha);
		}

		if (getViewController() != null) {
			if (bar == null) {
				bar = (Actor) (getViewController()
						.getView(StringSystem.VIEW_ACTION_BAR));
			}
			if (home == null) {
				home = (Actor) (getViewController()
						.getView(StringSystem.VIEW_HOME));
			}

			if (bar != null)
				bar.setPosition(content.getX() + content.getWidth(), bar.getY());
			if (home != null)
				home.setPosition(content.getX() + content.getWidth(),
						home.getY());
		}
	}

	@Override
	public void destroyComponent() {
	}

	@Override
	public void back() {
		hide(null);
	}

	boolean			canPan			= false;
	CustomListener	customListener	= new CustomListener() {
										public boolean touchDown(float x,
												float y, int pointer, int button) {
											if (content.getX() == -content.getWidth()
													&& x < 10 && !canPan) {
												canPan = true;
											}
											if (content.getX() == 0) {
												canPan = true;
											}

											return false;
										};

										public boolean pan(float x, float y,
												float deltaX, float deltaY) {
											if (canPan) {
												tranBg.setVisible(true);
												content.setPosition(
														MathUtils.clamp(
																content.getX()
																		+ deltaX,
																-content.getWidth(),
																0), content
																.getY());
												float alpha = (content
														.getWidth() - content
														.getX())
														/ content.getWidth();
												if (content.getX() == 0)
													alpha = 1;
												tranBg.setColor(
														tranBg.getColor().r,
														tranBg.getColor().g,
														tranBg.getColor().b,
														alpha);
												return true;
											}
											return false;
										};

										public boolean panStop(float x,
												float y, int pointer, int button) {
											if (canPan) {
												float position = content.getX()
														+ content.getWidth();
												if (position < content
														.getWidth() / 2)
													hide(null);
												if (position > content
														.getWidth() / 2)
													show(null);
												canPan = false;
											}
											return false;
										};

									};

}
