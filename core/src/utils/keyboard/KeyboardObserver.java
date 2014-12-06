package utils.keyboard;

/**
 * 
 * @author HungHD
 *
 */
public interface KeyboardObserver {

	public void dispatch(Event event);
	
	// Edit info
	public Event getLatestEvent();
	
	public String getHint();
	
	public int getMode();
	
	public int getType();
	
	public int getMaxLength();
	
	public int getMaxLine();
}
