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
	
    private static String getReadmeContentMain(final Context context, final String repoUrl) {
		try {
			StringBuilder content = new StringBuilder();
            URL url = new URL("https://raw.githubusercontent.com/IMOitself/IMOitself/master/README.md");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();
            connection.disconnect();
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
}
