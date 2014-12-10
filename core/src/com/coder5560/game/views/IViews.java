package com.coder5560.game.views;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.coder5560.game.enums.ViewState;
import com.coder5560.game.listener.OnCompleteListener;

public interface IViews {
	/*
	 * this method will be called to initial a view and adding it into a stage.
	 * in the constructor of a view, we don't need to initial it
	 */
	public void build(Stage stage, IViewController viewController,
			String viewName, Rectangle viewBound);

	/* this method is call when we need to update all actor in this view */
	public void update(float delta);

	public void show(OnCompleteListener listener);

	/*
	 * this method will be called by controller. on listener will must set the
	 * view state for this one. or it will be set "HIDE" automatically
	 */
	
	public void hide(OnCompleteListener listener);
	
	public void setViewState(ViewState state);

	public ViewState getViewState();

	public void setName(String name);

	public String getName();

	public void setSize(float width, float height);

	public void setPosition(float x, float y);

	public Rectangle getBound();

	/*
	 * return true : means this view catch the leftSizeEvent and prevent view
	 * controller call the left side by automatically
	 * 
	 * return false : mean the left side will be called by view controller by
	 * automatically
	 */
	public boolean onLeftSide();

	/*
	 * return true : means this view catch the leftSizeEvent and prevent view
	 * controller call the right side by automatically
	 * 
	 * return false : mean the right side will be called by view controller by
	 * automatically
	 */
	public boolean onRightSide();

	/*
	 * this method will be called if we don't need to use it anymore but we
	 * still want to hold it on our stage. all the component including in this
	 * view will be remove
	 */
	public void destroyComponent();

	/* return the Controller of all of views that we're handle */
	public IViewController getViewController();

	/*
	 * this method is call after viewController catch the method named back. by
	 * default we call the last view in the trace system.
	 */
	public void back();

}
