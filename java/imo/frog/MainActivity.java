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
		static int linesHeight;

        static int startLine = 0;
        static int endLine = MAX_LINES - 1;


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
			linesHeight = codeLayout.getHeight() / MAX_LINES;
			textSize = linesHeight * textSizeFactor;

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

                    if (MotionEvent.ACTION_UP == action) {
                        if (!swipeUp && !swipeDown) {
                            onClick(finalY);
                            return true;
                        } 

                        if (swipeUp) {
                            if (endLine >= content.length - 1) return true;
                            startLine++;
                            endLine++;  
                        }
                        if (swipeDown) {
                            if (startLine <= 0) return true;
                            startLine--;
                            endLine--; 
                        }
                        mContext.setTitle("start: " + startLine + "\tend: " + endLine);
                        positionEditText(15);

                    }
                    return true;
                }

                void onClick (float y) {
                    int touchedSection = (int) (y / linesHeight);
                    boolean lastSection = touchedSection == MAX_LINES;
                    if (lastSection) return;
                    mContext.setTitle(touchedSection + "");
                }
            };
        }

        static void setTexts (int editTextPosition) {
            int editTextLine = startLine + editTextPosition;
            StringBuilder stringAbove = new StringBuilder();
            StringBuilder stringBelow = new StringBuilder();
            String stringEditText = "";

            for (int i = 0; i < MAX_LINES; i++) {
                int currentLine = startLine + i;
                if (currentLine < editTextLine) stringAbove.append(content[currentLine] + "\n");
                if (currentLine == editTextLine) stringEditText = content[currentLine];
                if (currentLine > editTextLine) stringBelow.append(content[currentLine] + "\n");
            }
            textviewAbove.setText(stringAbove.toString());
            textviewBelow.setText(stringBelow.toString());
            editText.setText(stringEditText);
        }


		public static void positionEditText (int position) {
			textviewAbove.setLayoutParams(new LinearLayout.LayoutParams(parentWidth, linesHeight * (position)));
			editText.setLayoutParams(new LinearLayout.LayoutParams(parentWidth, linesHeight));
			textviewBelow.setLayoutParams(new LinearLayout.LayoutParams(parentWidth, linesHeight * (MAX_LINES - position)));

			setTexts(position);
			textviewAbove.invalidate();
			editText.invalidate();
			textviewBelow.invalidate();
		}

		static String generateContent () {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i <= 60; i++) {
				sb.append(i).append("\n");
			}
			return sb.toString();
		}
	}
}
