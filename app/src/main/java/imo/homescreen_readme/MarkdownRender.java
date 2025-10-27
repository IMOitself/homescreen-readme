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
	public static Bitmap renderMarkdownToBitmap(Context ctx, String md, int width, int maxHeight) {
		TextView tv = new TextView(ctx);
		tv.setTextColor(0xFF000000);
		tv.setTextSize(14);
		tv.setPadding(10, 10, 10, 10);
		setMarkdown(tv, md);

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
	
	public static void setMarkdown(TextView tv, String md) {
		if (md == null) md = "";

		SpannableStringBuilder sb = new SpannableStringBuilder(md);

		applyHeaders(sb);
		applyBold(sb);
		applyItalic(sb);
		applyInlineCode(sb);
		applyLinks(sb);
		applyBullets(sb);
		
		tv.setTextColor(0xFFFFFFFF);
		tv.setText(sb);
		tv.setMovementMethod(LinkMovementMethod.getInstance());
		tv.setHorizontallyScrolling(false);
		tv.setLineSpacing(0.0f, 1.1f);
	}

	private static void applyHeaders(SpannableStringBuilder sb) {
		Pattern p = Pattern.compile("(?m)^(#{1,6})\\s*(.+)$");
		Matcher m = p.matcher(sb);
		while (m.find()) {
			String hashes = m.group(1);
			String text = m.group(2);
			int start = m.start();
			int end = m.end();

			sb.replace(start, end, text);
			int newStart = start;
			int newEnd = start + text.length();

			sb.setSpan(new StyleSpan(Typeface.BOLD), newStart, newEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			float size = (hashes.length() == 1) ? 1.8f :
				(hashes.length() == 2) ? 1.5f :
				(hashes.length() == 3) ? 1.3f : 1.1f;
			sb.setSpan(new RelativeSizeSpan(size), newStart, newEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			m = p.matcher(sb);
		}
	}

	private static void applyBold(SpannableStringBuilder sb) {
		Pattern p = Pattern.compile("\\*\\*(.+?)\\*\\*");
		Matcher m = p.matcher(sb);
		while (m.find()) {
			String c = m.group(1);
			int s = m.start();
			int e = m.end();
			sb.replace(s, e, c);
			int ns = s, ne = s + c.length();
			sb.setSpan(new StyleSpan(Typeface.BOLD), ns, ne, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			m = p.matcher(sb);
		}
	}

	private static void applyItalic(SpannableStringBuilder sb) {
		Pattern p = Pattern.compile("(?<!\\*)\\*(?!\\s)(.+?)(?<!\\s)\\*(?!\\*)");
		Matcher m = p.matcher(sb);
		while (m.find()) {
			String c = m.group(1);
			int s = m.start();
			int e = m.end();
			sb.replace(s, e, c);
			int ns = s, ne = s + c.length();
			sb.setSpan(new StyleSpan(Typeface.ITALIC), ns, ne, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			m = p.matcher(sb);
		}
	}

	private static void applyInlineCode(SpannableStringBuilder sb) {
		Pattern p = Pattern.compile("`([^`]+)`");
		Matcher m = p.matcher(sb);
		while (m.find()) {
			String c = m.group(1);
			int s = m.start();
			int e = m.end();
			sb.replace(s, e, c);
			int ns = s, ne = s + c.length();
			sb.setSpan(new TypefaceSpan("monospace"), ns, ne, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			m = p.matcher(sb);
		}
	}

	private static void applyLinks(SpannableStringBuilder sb) {
		Pattern p = Pattern.compile("\\[([^\\]]+)\\]\\(([^)]+)\\)");
		Matcher m = p.matcher(sb);
		while (m.find()) {
			String label = m.group(1);
			String url = m.group(2);
			int s = m.start();
			int e = m.end();
			sb.replace(s, e, label);
			int ns = s, ne = s + label.length();
			sb.setSpan(new URLSpan(url), ns, ne, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			m = p.matcher(sb);
		}
	}

	private static void applyBullets(SpannableStringBuilder sb) {
		Pattern p = Pattern.compile("(?m)^\\s*[-*]\\s+(.+)$");
		Matcher m = p.matcher(sb);
		while (m.find()) {
			String c = m.group(1);
			int s = m.start();
			int e = m.end();
			sb.replace(s, e, c + "\n");
			int ns = s, ne = s + c.length();
			sb.setSpan(new BulletSpan(20), ns, ne, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			m = p.matcher(sb);
		}
		if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '\n') sb.delete(sb.length() - 1, sb.length());
	}
}


