package com.coder5560.game.enums;

public enum ViewState {
	INITIAL(-1), // the view just initial

	LOADING(0), // the view is loading data prepare for showing

	HIDE(3), // this state will be call when a view isn't show in screen but we
				// still need it to handler some process

	PAUSE(4), // this state when we want to pause the current process to do
				// something

	DISPOSE(5), // it is a state that we don't need this view anymore, the view
				// system will remove it already.

	SHOW(6), // this is state that the view is on processing

	PROCESSING(7); // it is the state that in the show we do some thing and
					// waiting for the result like get data from server
	int	value	= 0;

	private ViewState(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

}
