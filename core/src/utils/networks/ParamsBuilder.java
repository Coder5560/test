package utils.networks;

import java.util.ArrayList;

public class ParamsBuilder {
	
	ArrayList<String> params;
	
	public ParamsBuilder() {
		params = new ArrayList<String>();
	}
	
	public static ParamsBuilder builder() {
		return new ParamsBuilder();
	}
	
	public ParamsBuilder add(String param, String value) {
		params.add(param + "=" + value);
		return this;
	}
	
	public ParamsBuilder add(String param, int value) {
		return add(param, value + "");
	}
	
	public ParamsBuilder parseParams(String paramStr) {
		String[] strs = paramStr.split("&");
		for (int i = 0; i < strs.length; i++) {
			if (strs[i].length() > 0) {
				params.add(strs[i]);
			}
		}
		return this;
	}
	
	public String build() {
		String content = "";
		for (int i = 0; i < params.size(); i++) {
			content += params.get(i);
			if (i != params.size() - 1) {
				content += "&";
			}
		}
		return content;
	}
}

