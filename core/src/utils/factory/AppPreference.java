package utils.factory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class AppPreference {
	public static final String	TAG				= AppPreference.class.getName();
	public static AppPreference	instance		= new AppPreference();

	private Preferences			preferences;


	private AppPreference() {
		preferences = Gdx.app.getPreferences(TAG);
		load();
	}

	public void load() {

	}

	public Preferences getPreferences() {
		return preferences;
	}
}
