package imo.frog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity
{
	static Context mContext;
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

			positionEditText(0);

			codeLayout.addView(textviewAbove);
			codeLayout.addView(editText);
			codeLayout.addView(textviewBelow);
            codeLayout.setOnTouchListener(touchLogic());

		}

        static View.OnTouchListener touchLogic () {
            return new View.OnTouchListener(){
                @Override
                public boolean onTouch (View view, MotionEvent event) {
                    int action = event.getAction();
                    if (action == MotionEvent.ACTION_DOWN) {
                        //down
                    } else if (action == MotionEvent.ACTION_UP) {
                        //up
                    } else if (action == MotionEvent.ACTION_MOVE) {
                        //move
                    }
                    return true;
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
			textviewAbove.setLayoutParams(new ViewGroup.LayoutParams(parentWidth, editTextHeight * (position)));
			editText.setLayoutParams(new ViewGroup.LayoutParams(parentWidth, editTextHeight));
			textviewBelow.setLayoutParams(new ViewGroup.LayoutParams(parentWidth, editTextHeight * (MAX_LINES - position)));

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
