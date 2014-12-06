package utils.networks;

import com.badlogic.gdx.utils.JsonValue;

public class CustomHttpResponse {

	public String cmd;
	public JsonValue content;
	
	public CustomHttpResponse() {
		
	}
	
	public CustomHttpResponse setCmd(String value) {
		cmd = value;
		return this;
	}
	
	public CustomHttpResponse setContent(JsonValue value) {
		content = value;
		return this;
	}

}
