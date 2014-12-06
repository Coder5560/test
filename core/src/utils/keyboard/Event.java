package utils.keyboard;

import com.badlogic.gdx.utils.CharArray;

/**
 * 
 * @author HungHD
 *
 */
public final class Event {
	public Action action;
	public CharArray data;
	public int cursor;
	
	public Event(Action action) {
		this.action = action;
		data = new CharArray();
		cursor = -1;
	}
}
