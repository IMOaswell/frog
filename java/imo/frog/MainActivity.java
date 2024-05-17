package imo.frog;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity
{
	static Activity mContext;
	static final int MAX_LINES = 30;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mContext = this;
		final ViewGroup codeLayout = findViewById(R.id.code_layout);
		CodeLayout.loadWithDelay(codeLayout, 500);
	}

	static class CodeLayout
    {
		static String[] content;
		static final float textSizeFactor = 0.61f;
		static float textSize;
		static TextView textviewAbove;
		static EditText editText;
		static TextView textviewBelow;
		static int parentWidth;
		static int editTextHeight;

        static int startLine = 0;
        static int endLine = MAX_LINES;


        static void loadWithDelay (final ViewGroup codeLayout, int millis) {
            Runnable delayedRunnable = new Runnable() {
                @Override
                public void run () {
                    CodeLayout.init(codeLayout);
                }
            };
            new Handler().postDelayed(delayedRunnable, millis);
        }

		static void init (ViewGroup codeLayout) {
			content = generateContent().split("\n");

			parentWidth = codeLayout.getWidth();
			editTextHeight = codeLayout.getHeight() / MAX_LINES;
			textSize = editTextHeight * textSizeFactor;

			textviewAbove = new TextView(mContext);
			editText = new EditText(mContext);
			textviewBelow = new TextView(mContext);

			textviewAbove.setTextSize(textSize);
			editText.setBackgroundColor(0x8061AFEF);
			editText.setPadding(0, 0, 0, 0);
			editText.setSingleLine(true);
			editText.setTextSize(textSize);
			textviewBelow.setTextSize(textSize);

			positionEditText(15);

			codeLayout.addView(textviewAbove);
			codeLayout.addView(editText);
			codeLayout.addView(textviewBelow);
            codeLayout.setOnTouchListener(touchLogic());

		}

        static View.OnTouchListener touchLogic () {
            return new View.OnTouchListener(){
                float initialY = 0;
                final float NO_SWIPE_RANGE = 15;
                View view;

                @Override
                public boolean onTouch (View v, MotionEvent motion) {
                    int action = motion.getAction();
                    if (MotionEvent.ACTION_DOWN == action) {
                        initialY = motion.getY();
                        view = v;
                        return true;
                    }
                    float finalY = motion.getY();
                    float distance = finalY - initialY;
                    boolean swipeUp = distance < -NO_SWIPE_RANGE;
                    boolean swipeDown = distance > NO_SWIPE_RANGE;
                    
                    if (MotionEvent.ACTION_MOVE == action) {
                        int scrollFactor = (int) distance / 50;
                        scrollFactor = Math.abs(scrollFactor);
                        if (swipeUp) {
                            startLine = startLine + scrollFactor;
                            endLine = endLine + scrollFactor;
                            
                        } else
                        if (swipeDown) {
                            startLine = startLine - scrollFactor;
                            endLine = endLine - scrollFactor;
                        }
                        mContext.setTitle("d: "+distance+"\tf: "+scrollFactor+"\ts: "+startLine+"\te: "+endLine);
                    }
                    if (MotionEvent.ACTION_UP == action){
                        if (swipeUp || swipeDown) return true;
                        onClick(finalY);
                    }
                    return true;
                }

                void onClick (float y) {
                    float sectionHeight = view.getHeight() / MAX_LINES;
                    int touchedSection = (int) (y / sectionHeight);
                    boolean lastSection = touchedSection == MAX_LINES;
                    if (lastSection) return;
                    mContext.setTitle(touchedSection + "");
                }
            };
        }




		public static void positionEditText (int position) {
			StringBuilder textAbove = new StringBuilder();
			for (int i = startLine; i < position; i++) {
				textAbove.append(content[i]).append("\n");
			}
			StringBuilder textBelow = new StringBuilder();
			for (int i = position + 1; i < endLine; i++) {
				textBelow.append(content[i]).append("\n");
			}
			textviewAbove.setLayoutParams(new LinearLayout.LayoutParams(parentWidth, editTextHeight * (position)));
			editText.setLayoutParams(new LinearLayout.LayoutParams(parentWidth, editTextHeight));
			textviewBelow.setLayoutParams(new LinearLayout.LayoutParams(parentWidth, editTextHeight * (MAX_LINES - position)));

			textviewAbove.setText(textAbove.toString());
			editText.setText(content[position]);
			textviewBelow.setText(textBelow.toString());

			textviewAbove.invalidate();
			editText.invalidate();
			textviewBelow.invalidate();
		}

		static String generateContent () {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i <= 100; i++) {
				sb.append(i).append("\n");
			}
			return sb.toString();
		}
	}
}
