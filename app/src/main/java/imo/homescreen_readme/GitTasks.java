package imo.homescreen_readme;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.Executors;
import org.eclipse.jgit.api.Git;

public class GitTasks {
    public static File readmeFile;
	
    public static void downloadReadme(final Context context, final String repoUrl, final AfterDownloadReadme afterDownload){
		Executors.newSingleThreadExecutor().execute(new Runnable() {
				@Override
				public void run() {
					downloadReadmeMain(context, repoUrl, afterDownload);
				}
			});
	}
    private static void downloadReadmeMain(final Context context, final String repoUrl, final AfterDownloadReadme afterDownload){
		File clonedRepoFolder = new File(context.getCacheDir(), "jgit-temp-" + System.currentTimeMillis());
		String output = "";
		try {
			Git.cloneRepository()
				.setURI(repoUrl)
				.setDirectory(clonedRepoFolder)
				.call().close();

			readmeFile = new File(clonedRepoFolder, "README.md");
			if (readmeFile.exists()) 
				output = readFileToString(readmeFile);
			else
				output = "README.md not found.";

		} catch (final Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			output = sw.toString();

		} finally {
			final String finalOutput = output;
			new Handler(Looper.getMainLooper()).post(new Runnable() {
					@Override
					public void run() {
						afterDownload.run(finalOutput);
					}
				});
		}
	}
	
	private static String readFileToString(File file) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line).append("\n");
		}
		reader.close();
		return sb.toString();
	}
	
	interface AfterDownloadReadme{
		public void run(String output);
	}
	
	public static void saveModifiedReadmeToFile(final Context context, final String modifiedReadme, final String repoUrl, final AfterDownloadReadme afterDownload){
		if(readmeFile != null){
			saveModifiedReadmeToFileMain(modifiedReadme);
			return;
		}
		downloadReadme(context, repoUrl, new AfterDownloadReadme(){
				@Override
				public void run(String output){
					saveModifiedReadmeToFileMain(modifiedReadme);
					afterDownload.run(output);
				}
			});
	}
	
	private static void saveModifiedReadmeToFileMain(String modifiedReadme){
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(readmeFile));
            writer.print(modifiedReadme);
            MainActivity.mainActivity.showToast("successfully saved to readme file");
        } catch (IOException e) {
            StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
            MainActivity.mainActivity.showToast(sw.toString());
        }
	}
	
	public static void deleteClonedFolder(){
		File clonedRepoFolder = GitTasks.readmeFile.getParentFile();
		if (clonedRepoFolder.exists()) deleteRecursively(clonedRepoFolder);
	}
	
	private static void deleteRecursively(File fileOrDir) {
		if (fileOrDir.isDirectory()) {
			for (File child : fileOrDir.listFiles()) {
				deleteRecursively(child);
			}
		}
		fileOrDir.delete();
	}
}
