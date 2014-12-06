package utils.factory;

import java.io.InputStream;

import com.badlogic.gdx.graphics.Pixmap;

public interface PlatformResolver {

	public Pixmap formatBitmap(InputStream in);

	public String getDeviceName();

	public String getDeviceID();
}
