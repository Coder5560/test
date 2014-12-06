package com.coder5560.game.views;

import utils.networks.FacebookConnector;
import utils.screen.GameCore;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Array;
import com.coder5560.game.enums.AnimationType;
import com.coder5560.game.enums.ViewState;

public class ViewController implements IViewController {
	public Stage				stage;
	public Array<IViews>		views;

	public IViews				currentView;
	public FacebookConnector	facebookConnector;
	private GameCore			_gameParent;

	public ViewController(GameCore _gameParent) {
		super();
		this._gameParent = _gameParent;
	}

	@Override
	public void update(float delta) {
		for (IViews view : views) {
			view.update();
			if (view.getViewState() == ViewState.DISPOSE) {
				removeView(view.getName());
			}
		}
	}

	public void build(Stage stage) {
		this.stage = stage;
		views = new Array<IViews>();
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
	public void focusView(String name) {
		if (!isContainView(name)) {
			return;
		}

		IViews view = getView(name);
		if (view != null) {
			if (currentView != null) {
				this.currentView.hide();
				((Actor) currentView).setTouchable(Touchable.disabled);
			}
			view.show();
			((Actor) view).setTouchable(Touchable.childrenOnly);
			((Actor) view).toFront();
			this.currentView = view;
		}
	}

	@Override
	public void focusView(String name, AnimationType animationType) {

		if (!isContainView(name)) {
			return;
		}
		IViews view = getView(name);
		if (view != null) {
			if (currentView != null) {
				this.currentView.hide(AnimationType.NONE, null);
				((Actor) currentView).setTouchable(Touchable.disabled);
			}
			view.show(animationType, null);
			((Actor) view).setTouchable(Touchable.childrenOnly);
			((Actor) view).toFront();
			this.currentView = view;
		}
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

	@Override
	public void hideView(String name) {
		if (!avaiable())
			return;
		IViews view = getView(name);
		if (view != null)
			view.hide(AnimationType.NONE, null);
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

}
