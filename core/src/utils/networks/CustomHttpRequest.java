package utils.networks;


public class CustomHttpRequest {

	public String cmd;
	public String params;
	public boolean sendPost;
	
	public CustomHttpRequest() {
		
	}
	
	public CustomHttpRequest setCmd(String value) {
		cmd = value;
		return this;
	}
	
	public CustomHttpRequest setParam(String value) {
		params = value;
		return this;
	}
	
	public CustomHttpRequest setSendPost(boolean sendPost) {
		this.sendPost = sendPost;
		return this;
	}
	
}
