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
	static final int MAX_LINES = 35;

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

        static float startLine = 0;
        static float endLine = MAX_LINES - 1;
        static int currentEditTextLine = 0;


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
			editText.setBackgroundColor(0x7061AFEF);
			editText.setPadding(0, 0, 0, 0);
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
                float previousY = initialY;
                final int NO_SWIPE_RANGE = 15;
                final int SCROLL_STRENGTH = 1;

                @Override
                public boolean onTouch (View v, MotionEvent motion) {
                    int action = motion.getAction();
                    if (MotionEvent.ACTION_DOWN == action) {
                        initialY = motion.getY();
                        previousY = initialY;
                        return true;
                    }

                    float currentY = motion.getY();
                    float distToInitialY = currentY - initialY;
                    boolean canSwipe = 
                        distToInitialY < -NO_SWIPE_RANGE || 
                        distToInitialY > NO_SWIPE_RANGE;

                    if (MotionEvent.ACTION_MOVE == action) {
                        if (!canSwipe) return true;
                        boolean swipeUp = currentY < previousY;
                        boolean swipeDown = currentY > previousY;
                        previousY = currentY;
                        if (swipeUp) {
                            if (endLine >= content.length - 1) return true;
                            startLine++;
                            endLine++;
                            previousY -= SCROLL_STRENGTH;
                        }
                        if (swipeDown) {
                            if (startLine <= 0) return true;
                            startLine--;
                            endLine--;
                            previousY += SCROLL_STRENGTH;
                        }
                        mContext.setTitle("init: " + (int)initialY + "\tcurrent: " + (int)currentY + "\tprev: " + (int)previousY);
                        positionEditText(currentEditTextLine);
                    }
                    if (MotionEvent.ACTION_UP == action) {
                        if (canSwipe) return true;
                        onClick(currentY);
                    }
                    return true;
                }

                void onClick (float y) {
                    int touchedSection = (int) (y / linesHeight);
                    boolean lastSection = touchedSection == MAX_LINES;
                    if (lastSection) return;
                    mContext.setTitle(touchedSection + "");
                    positionEditText(touchedSection);
                }
            };
        }

        static void setTexts (int editTextPosition) {
            int startLineInt = (int) startLine;
            int editTextLine = startLineInt + editTextPosition;
            StringBuilder stringAbove = new StringBuilder();
            StringBuilder stringBelow = new StringBuilder();
            String stringEditText = "";

            for (int i = 0; i < MAX_LINES; i++) {
                int currentLine = startLineInt + i;
                String currentString = content[currentLine];
                if (currentLine < editTextLine) stringAbove.append(currentString + "\n");
                if (currentLine == editTextLine) stringEditText = currentString;
                if (currentLine > editTextLine) stringBelow.append(currentString + "\n");
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
            currentEditTextLine = position;
		}

		static String generateContent () {
            String crazy = mContext.getResources().getString(R.string.crazy);
            for (int i = 0; i <= 100; i++) {
                crazy += mContext.getResources().getString(R.string.crazy);
			}
            if (true) return crazy;

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i <= 60; i++) {
				sb.append(i).append("\n");
			}
			return sb.toString();
		}
	}
}
