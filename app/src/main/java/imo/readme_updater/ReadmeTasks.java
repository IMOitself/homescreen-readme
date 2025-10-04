package imo.readme_updater;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;

public class ReadmeTasks {

	public static void getReadmeContent(final Context context, final String repoUrl, final OnAfterFetch onAfterFetch){
		Executors.newSingleThreadExecutor().execute(new Runnable() {
				@Override
				public void run() {
					final String finalOutput = getReadmeContentMain(context, repoUrl);
					new Handler(Looper.getMainLooper()).post(new Runnable() {
							@Override
							public void run() {
								onAfterFetch.run(finalOutput);
							}
						});
				}
			});
	}
	
    private static String getReadmeContentMain(final Context context, String repoUrl) {
		try {
			String repoUrlPrefix = getRepoUrlPrefix(repoUrl);
			String fileName = "README.md";
            String defaultBranch = getDefaultBranch(repoUrlPrefix);

            URL rawContentUrl = new URL("https://raw.githubusercontent.com/"+repoUrlPrefix+"/" + defaultBranch + "/" + fileName);
            HttpURLConnection contentConnection = (HttpURLConnection) rawContentUrl.openConnection();
            contentConnection.setRequestMethod("GET");

            BufferedReader contentReader = new BufferedReader(new InputStreamReader(contentConnection.getInputStream()));
            StringBuilder content = new StringBuilder();
            String contentLine;
            while ((contentLine = contentReader.readLine()) != null) {
                content.append(contentLine).append("\n");
            }
            contentReader.close();
            contentConnection.disconnect();
			return content.toString();

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
            return sw.toString();
        }
    }
	
	interface OnAfterFetch {
		public void run(String output);
	}
	
	private static String getDefaultBranch(String repoUrlPrefix) throws Exception {
		URL apiUrl = new URL("https://api.github.com/repos/"+repoUrlPrefix);
		HttpURLConnection apiConnection = (HttpURLConnection) apiUrl.openConnection();
		apiConnection.setRequestMethod("GET");

		BufferedReader apiReader = new BufferedReader(new InputStreamReader(apiConnection.getInputStream()));
		StringBuilder apiResponse = new StringBuilder();
		String apiLine;
		while ((apiLine = apiReader.readLine()) != null) {
			apiResponse.append(apiLine);
		}
		apiReader.close();
		apiConnection.disconnect();

		JSONObject jsonObject = new JSONObject(apiResponse.toString());
		return jsonObject.getString("default_branch");
	}
	
	private static String getRepoUrlPrefix(String repoUrl) throws Exception{
		String regex = "https://github\\.com/([^/]+/[^/]+)";
        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(repoUrl);
		if (matcher.find()) return matcher.group(1);
		else throw new Exception("url is not properly defined");
	}
}
