package imo.readme_updater;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class MainActivity extends Activity {
    final static String REPO_URL_KEY = "REPO_URL_KEY";
    final static String WIDGET_STRING_KEY = "WIDGET_STRING_KEY";

    EditText repoLinkEdittext;

	LinearLayout outputActionsLayout;
	CheckBox isLineWrapCheckbox;
	Button editButton;
	Button dailyQuoteButton;

    EditText readmeEdittext;
    Button exitButton;

	boolean buttonOnClickCloseApp = false;
	boolean isInFetchMode = true;
	boolean isInEditMode = false;
	String repositoryURL;
    String readmeContent;
	File readmeFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        repoLinkEdittext = findViewById(R.id.repo_link_edittext);

		outputActionsLayout = findViewById(R.id.output_actions_layout);
		isLineWrapCheckbox = findViewById(R.id.is_linewrap_checkbox);
		editButton = findViewById(R.id.edit_button);
		dailyQuoteButton = findViewById(R.id.daily_quote_button);

        readmeEdittext = findViewById(R.id.command_output_edittext);
		exitButton = findViewById(R.id.exit_button);
        
		outputActionsLayout.setVisibility(View.GONE);
		dailyQuoteButton.setEnabled(false);
		
		readmeEdittext.setFocusable(false);
		readmeEdittext.setFocusableInTouchMode(false);
        
        SharedPreferences sp = getSharedPreferences("hehe", Context.MODE_PRIVATE);
		repositoryURL = sp.getString(REPO_URL_KEY, "");
        repoLinkEdittext.setText(repositoryURL);
		
		readmeEdittext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!isInFetchMode) return;
					startFetchReadme();
					isInFetchMode = false;
                }
            });
		
		isLineWrapCheckbox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton v, boolean isChecked){
				readmeEdittext.setHorizontallyScrolling(isChecked);
				readmeEdittext.setMovementMethod(new ScrollingMovementMethod());
			}
		});
		
		exitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

		editButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// SWITCH TO EDIT MODE
				if(! isInEditMode){
					isInEditMode = !isInEditMode;
					readmeEdittext.setFocusable(true);
					readmeEdittext.setFocusableInTouchMode(true);
					readmeEdittext.requestFocus();
					dailyQuoteButton.setEnabled(true);
					editButton.setText("SAVE README.MD");
					return;
				}

				// SWITCH TO SAVE MODE
				if(readmeContent.equals(readmeEdittext.getText().toString())){
					showToast("No changes detected.");
					return;
				}
				
				isInEditMode = !isInEditMode;
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle("Save README.md");
				builder.setMessage("Are you sure you want to save the README.md?");
				builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						readmeEdittext.setFocusable(false);
						readmeEdittext.setFocusableInTouchMode(false);
						saveModifiedReadmeToFile(readmeEdittext.getText().toString());
						// TODO: commit README.md
						// TODO: push to repository link
						dailyQuoteButton.setEnabled(false);
						editButton.setText("EDIT README.MD");
					}
				});
				builder.setNegativeButton("No", null);
				builder.show();
			}
		});
    }
    
    void startFetchReadme(){
        repositoryURL = repoLinkEdittext.getText().toString().trim();
		if (! repositoryURL.startsWith("https://")) return;
		
		readmeEdittext.setText("Please Wait...");
        fetchReadmeAsync(this, repositoryURL);
    }
    
	public void fetchReadmeAsync(final Context context, final String repoUrl) {
		Executors.newSingleThreadExecutor().execute(new Runnable() {
				@Override
				public void run() {
					fetchReadme(context, repoUrl);
				}
			});
	}
	
	private void fetchReadme(final Context context, final String repoUrl){
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
						onAfterFetchReadme(finalOutput);
					}
				});
		}
	}
	
	void onAfterFetchReadme(String output){
		readmeContent = output;
		readmeEdittext.setText(output);
		outputActionsLayout.setVisibility(View.VISIBLE);
		
		final SharedPreferences.Editor spEditor = getSharedPreferences("hehe", Context.MODE_PRIVATE).edit();
		spEditor.putString(WIDGET_STRING_KEY, output);
		spEditor.putString(REPO_URL_KEY, repositoryURL);
		spEditor.apply();

		updateWidget();

		if(buttonOnClickCloseApp) finish();
	}
	
	void updateWidget(){
        Intent intent = new Intent(MainActivity.this, Widget.class);
        intent.setAction(Widget.ACTION_WIDGET_UPDATE);
        sendBroadcast(intent);
		showToast("widget updated:D");
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

	private static void deleteRecursively(File fileOrDir) {
		if (fileOrDir.isDirectory()) {
			for (File child : fileOrDir.listFiles()) {
				deleteRecursively(child);
			}
		}
		fileOrDir.delete();
	}
	
	void saveModifiedReadmeToFile(String modifiedReadme){
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(readmeFile));
            writer.print(modifiedReadme);
            showToast("successfully saved to readme file");
        } catch (IOException e) {
            StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
            showToast(sw.toString());
        }
	}
	
	void showToast(String string){
		Toast toast = Toast.makeText(MainActivity.this, string, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP, 0, getActionBar().getHeight());
		toast.show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		File clonedRepoFolder = readmeFile.getParentFile();
		if (clonedRepoFolder.exists()) deleteRecursively(clonedRepoFolder);
		
	}
}
