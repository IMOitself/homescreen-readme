package imo.homescreen_readme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
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

        // Inline elements
        renderBold(sb);
        renderItalic(sb);
        renderInlineCode(sb);
        renderLinks(sb);
		renderSubscript(sb);
		renderSuperscript(sb);

        // Line spacing last
        renderLineBreaks(sb);

		tv.setTextColor(0xFFFFFFFF);
		tv.setText(sb);
	}

	private static void renderHeaders(SpannableStringBuilder sb) {
		String[] headerArray = {"#", "##", "###", "####", "#####", "######"};

		for (final String header : headerArray) {
			setSpanByStartCharToLineEnd(sb, header, new SpanStyler() {
				@Override
				public void style(SpannableStringBuilder sb, int start, int end) {
					sb.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					float size = (header.length() == 1) ? 1.2f :
						(header.length() == 2) ? 1.15f :
						(header.length() == 3) ? 1.1f : 1.05f;
					sb.setSpan(new RelativeSizeSpan(size), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			});
		}
	}

	private static void renderTables(SpannableStringBuilder sb) {
		//TODO: stroke color #3D444D for tables 
	}


	private static void renderBold(SpannableStringBuilder sb) {
		setSpanByWrapChar(sb, "**", new SpanStyler() {
			@Override
			public void style(SpannableStringBuilder sb, int start, int end) {
				sb.setSpan(new StyleSpan(Typeface.BOLD),
            start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		});
	}

	private static void renderItalic(SpannableStringBuilder sb) {
		setSpanByWrapChar(sb, "*", new SpanStyler() {
			@Override
			public void style(SpannableStringBuilder sb, int start, int end) {
				sb.setSpan(new StyleSpan(Typeface.BOLD),
            start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		});
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

	private static void renderSubscript(SpannableStringBuilder sb) {
		setSpanByStartCharToLineEnd(sb, "<sub>", new SpanStyler() {
				@Override
				public void style(SpannableStringBuilder sb, int start, int end) {
					sb.setSpan(new RelativeSizeSpan(0.8f), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			});
	}

	private static void renderSuperscript(SpannableStringBuilder sb) {
		setSpanByStartCharToLineEnd(sb, "<sup>", new SpanStyler() {
				@Override
				public void style(SpannableStringBuilder sb, int start, int end) {
					sb.setSpan(new RelativeSizeSpan(0.8f), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			});
	}
	

	private static void renderLineBreaks(SpannableStringBuilder sb) {
		int index;
		while ((index = sb.toString().indexOf("<br>")) != -1) 
			sb.replace(index, index + 4, "\n");
	}

	public interface SpanStyler {
		void style(SpannableStringBuilder sb, int start, int contentLength);
	}

	public static void setSpanByWrapChar(SpannableStringBuilder sb, String wrapChar, SpanStyler styler) {
		String pWrapChar = "\\Q" + wrapChar + "\\E";
		String pString = "CHAR(.*?)CHAR";
		pString = pString.replace("CHAR", pWrapChar);
		
		Pattern p = Pattern.compile(pString);
    	Matcher m = p.matcher(sb);
    	while (m.find()) {
        	int start = m.start();
        	int end = m.end();
        	styler.style(sb, start, end);
			sb.setSpan(new RelativeSizeSpan(0f),
    			start, start + wrapChar.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			sb.setSpan(new RelativeSizeSpan(0f),
    			end - wrapChar.length(), end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}

	public static void setSpanByStartCharToLineEnd(SpannableStringBuilder sb, String startChar, SpanStyler styler) {
		String pWrapChar = "\\Q" + startChar + "\\E";
		String pString = "CHAR.*";
		pString = pString.replace("CHAR", pWrapChar);
		
		Pattern p = Pattern.compile(pString);
    	Matcher m = p.matcher(sb);
    	while (m.find()) {
        	int start = m.start();
        	int end = m.end();
        	styler.style(sb, start, end);
			sb.setSpan(new RelativeSizeSpan(0f),
    			start, start + startChar.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}

}


