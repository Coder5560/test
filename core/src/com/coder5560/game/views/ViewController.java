package com.coder5560.game.views;

import imp.view.HomeView;
import imp.view.MainMenu;
import imp.view.TopBarView;
import utils.factory.StringSystem;
import utils.networks.FacebookConnector;
import utils.screen.GameCore;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.coder5560.game.enums.Constants;
import com.coder5560.game.enums.ViewState;
import com.coder5560.game.screens.GameScreen;

public class ViewController implements IViewController {
	public Stage				stage;
	public Array<IViews>		views;

	public IViews				currentView;
	public FacebookConnector	facebookConnector;
	private GameCore			_gameParent;
	private GameScreen			_gameScreen;

	public ViewController(GameCore _gameParent, GameScreen gameScreen) {
		super();
		this._gameParent = _gameParent;
		this._gameScreen = gameScreen;
	}

	public void build(Stage stage) {
		this.stage = stage;
		views = new Array<IViews>();
		HomeView homeView = new HomeView();
		homeView.build(getStage(), this, StringSystem.VIEW_HOME, new Rectangle(
				0, 0, Constants.WIDTH_SCREEN, Constants.HEIGHT_SCREEN));
		homeView.buildComponent().show(null);

		TopBarView topBarView = new TopBarView();
		topBarView.build(getStage(), this, StringSystem.VIEW_ACTION_BAR,
				new Rectangle(0, Constants.HEIGHT_SCREEN
						- Constants.HEIGHT_ACTIONBAR, Constants.WIDTH_SCREEN,
						Constants.HEIGHT_ACTIONBAR));
		topBarView.buildComponent().show(null);

		MainMenu mainMenu = new MainMenu();
		mainMenu.build(getStage(), this, StringSystem.VIEW_MAIN_MENU,
				new Rectangle(0, 0, Constants.WIDTH_SCREEN,
						Constants.HEIGHT_SCREEN));
		mainMenu.buildComponent().show(null);
	}

	@Override
	public void update(float delta) {
		for (IViews view : views) {
			view.update(delta);
			if (view.getViewState() == ViewState.DISPOSE) {
				removeView(view.getName());
			}
		}
	}

	@Override
	public boolean isContainView(String name) {
		if (avaiable()) {
			for (IViews view : views) {
				if (view.getName().equalsIgnoreCase(name))
					return true;
			}
		}
		return false;
	}

	@Override
	public void addView(IViews view) {
		if (!avaiable())
			return;
		views.add(view);
	}

	@Override
	public void removeView(String name) {
		if (!avaiable())
			return;
		IViews view = getView(name);
		if (view == null)
			return;
		view.destroyComponent();
		views.removeValue(view, false);
		stage.getActors().removeValue((Actor) view, true);
	}

	// return the first view has name equal "name" in this container of views
	@Override
	public IViews getView(String name) {
		for (IViews view : views) {
			if (view.getName().equalsIgnoreCase(name)) {
				return view;
			}
		}
		return null;
	}

	@Override
	public Array<IViews> getViews() {
		if (avaiable())
			return views;
		return null;
	}

	public boolean avaiable() {
		return views != null && stage != null;
	}

	@Override
	public void backView() {
	}

	@Override
	public Stage getStage() {
		return stage;
	}

	public void setFacebookConnector(FacebookConnector facebookConnector) {
		this.facebookConnector = facebookConnector;
	}

	public FacebookConnector getFacebookConnector() {
		return facebookConnector;
	}

	@Override
	public void setGameParent(GameCore gameParent) {
		this._gameParent = gameParent;
	}

	@Override
	public GameCore getGameParent() {
		return _gameParent;
	}

	// this method will sort that all of our view from a container of view.
	@Override
	public void sortView() {
	}

	@Override
	public IViews getCurrentView() {
		return currentView;
	}

	@Override
	public void setCurrentView(IViews view) {
		this.currentView = view;
		TraceView.instance.addViewToTrace(view.getName());
	}

	@Override
	public GameScreen getGameScreen() {
		return _gameScreen;
	}

}
