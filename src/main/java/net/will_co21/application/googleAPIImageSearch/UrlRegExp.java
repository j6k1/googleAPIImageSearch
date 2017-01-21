package net.will_co21.application.googleAPIImageSearch;

import java.util.regex.Pattern;

public class UrlRegExp {
	public static final Pattern pattern = Pattern.compile("^(https?://([\\-._~@:a-zA-Z0-9!\\$()*+,;=]|(%[0-9A-F]{2}))+([\\/?#]([\\-._~@:a-zA-Z0-9!\\$&()*+,;=\\/?\\[\\]#\\|])*)?)$");
}
