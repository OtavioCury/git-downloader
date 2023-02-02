package gaa.util.github;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GitHubUtil {
	
	private static final Logger logger = Logger.getLogger(GitHubUtil.class);
	

	public static final Date getResetTime(String token) throws IOException {
		logger.trace("GitHubUtil.getResetTime(String)");
		
		String url = "https://api.github.com/rate_limit?access_token=" + token;
		URL obj = new URL(url);
		
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		if (con.getResponseCode() == 200) {
			StringWriter writer = new StringWriter();
			InputStream inputStream = con.getInputStream();
			IOUtils.copy(inputStream, writer);
			String theString = writer.toString();
			inputStream.close();
			
			JsonElement json = new JsonParser().parse(theString);
			JsonObject resources = json.getAsJsonObject().getAsJsonObject("resources");
			JsonObject core = resources.getAsJsonObject("core");
			return new Date(core.getAsJsonPrimitive("reset").getAsLong() * 1000);
		}
		
		return null;
	}
	
	
}
