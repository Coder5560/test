package com.coder5560.game.views;

import utils.factory.StringSystem;

import com.badlogic.gdx.utils.Array;

public class TraceView {
	public Array<String>	traceView;
	public static TraceView	instance	= new TraceView();

	public TraceView() {
		super();
		traceView = new Array<String>();
	}

	public boolean containView(String name) {
		for (String view : traceView) {
			if (view.equalsIgnoreCase(name))
				return true;
		}
		return false;
	}

	public void addViewToTrace(String name) {
		if (name.equalsIgnoreCase(StringSystem.VIEW_HOME)) {
			traceView.clear();
		}
		traceView.add(name);

	}

	public String getLastView() {
		if (traceView.size > 1) {
			return traceView.get(traceView.size - 1);
		}
		return StringSystem.VIEW_HOME;
	}

	public void removeView(String name) {
		if (traceView.size > 1)
			if (traceView.get(traceView.size - 1).equalsIgnoreCase(name))
				traceView.removeIndex(traceView.size - 1);
	}

	public void reset() {
		if (traceView.size > 1) {
			traceView.clear();
			traceView.add(StringSystem.VIEW_HOME);
		}
	}

	public void debug() {
		System.out.println("Trace View Size : " + traceView.size);
		for (int i = 0; i < traceView.size; i++) {
			System.out.println("view " + i + " : " + traceView.get(i));
		}

	}

}
