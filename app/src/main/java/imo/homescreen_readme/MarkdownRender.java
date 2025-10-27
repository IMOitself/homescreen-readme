package imo.homescreen_readme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.BulletSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownRender {
	public static Bitmap renderMarkdownToBitmap(Context context, String mdString, int width, int maxHeight) {
		TextView tv = new TextView(context);
		tv.setTextColor(0xFF000000);
		tv.setTextSize(14);
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

		renderHeaders(sb);
		renderBold(sb);
		renderItalic(sb);
		renderInlineCode(sb);
		renderLinks(sb);
		renderBullets(sb);
		renderTables(sb);
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
		//TODO: render inline code
	}

	private static void renderLineSpacing(SpannableStringBuilder sb) {
		//TODO: render line spacing
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


