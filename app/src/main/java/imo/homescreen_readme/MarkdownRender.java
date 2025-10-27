package imo.homescreen_readme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.LeadingMarginSpan;
import android.view.View;
import android.widget.TextView;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownRender {
	public static Bitmap renderMarkdownToBitmap(Context context, String mdString, int width, int maxHeight) {
		TextView tv = new TextView(context);
		tv.setTextColor(0xFF000000);
		tv.setTextSize(16);
		tv.setPadding(10, 10, 10, 10);
		renderMarkdown(tv, mdString);

		int specW = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
		int specH = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		tv.measure(specW, specH);
		tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());

		int w = tv.getMeasuredWidth();
		int h = Math.min(tv.getMeasuredHeight(), maxHeight);

		Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		canvas.save();
		canvas.clipRect(0, 0, w, h);
		tv.draw(canvas);
		canvas.restore();

		return bmp;
	}

	public static void renderMarkdown(TextView tv, String mdString) {
		if (mdString == null) mdString = "";

		SpannableStringBuilder sb = new SpannableStringBuilder(mdString);

        // Block elements first
		renderHeaders(sb);
        renderTables(sb);
        renderBullets(sb);

        // Inline elements
        renderBold(sb);
        renderItalic(sb);
        renderInlineCode(sb);
        renderLinks(sb);

        // Line spacing last
        renderLineSpacing(sb);

		tv.setTextColor(0xFFFFFFFF);
		tv.setText(sb);
	}

	private static void renderHeaders(SpannableStringBuilder sb) {
		//TODO: render headers
	}

	private static void renderBold(SpannableStringBuilder sb) {
		//TODO: render bold
	}

	private static void renderItalic(SpannableStringBuilder sb) {
		//TODO: render italic
	}

	private static void renderInlineCode(SpannableStringBuilder sb) {
		Pattern p = Pattern.compile("`([^`]+)`");
    	Matcher m = p.matcher(sb);
    	while (m.find()) {
        	int start = m.start();
        	int end = m.end();
        	sb.setSpan(new BackgroundColorSpan(Color.parseColor("#1E242A")),
                start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			sb.replace(start, start + 1, " ");
			sb.replace(end - 1, end, " ");
    	}
	}

	private static void renderLinks(SpannableStringBuilder sb) {
		//TODO: text color #4493F8 for links
	}

	private static void renderBullets(SpannableStringBuilder sb) {
		//TODO: render bullets
	}

	private static void renderTables(SpannableStringBuilder sb) {
		//TODO: stroke color #3D444D for tables 
	}

	private static void renderLineSpacing(SpannableStringBuilder sb) {
		//TODO: render <br> as line spacing
	}
}


