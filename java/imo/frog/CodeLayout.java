package imo.frog;

import android.app.Activity;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CodeLayout
{
    static Activity mContext;
    static String[] contentStrings;
    static final float textSizeFactor = 0.61f;
    static float textSize;
    static TextView textview;
    static int parentWidth;
    static int linesHeight;

    static final int MAX_LINES = 35;
    static int startLine = 0;

    static void loadWithDelay (Activity activity, final ViewGroup codeLayout, int millis) {
        mContext = activity;
        Runnable delayedRunnable = new Runnable() {
            @Override
            public void run () {
                CodeLayout.init(codeLayout);
            }
        };
        new Handler().postDelayed(delayedRunnable, millis);
    }

    static void init (ViewGroup codeLayout) {
        contentStrings = generateContent().split("\n");

        parentWidth = codeLayout.getWidth();
        linesHeight = codeLayout.getHeight() / MAX_LINES;
        textSize = linesHeight * textSizeFactor;

        textview = new TextView(mContext);
        textview.setTextSize(textSize);

        refreshTexts();

        codeLayout.addView(textview);
        codeLayout.setOnTouchListener(ScrollAndClickLogic());
    }

    static void setTexts () {
        String allStringsInRange = "";
        for (int i = 0; i < MAX_LINES; i++) {
            int currentLine = startLine + i;
            String currentString = contentStrings[currentLine];
            allStringsInRange += currentString + "\n";
        }
        textview.setText(allStringsInRange);
    }

    static void resizeTexts () {
        textview.setLayoutParams(new LinearLayout.LayoutParams(parentWidth, linesHeight * MAX_LINES));
        textview.invalidate();
    }

    static void refreshTexts () {
        resizeTexts();
        setTexts();
    }

    static String generateContent () {
        String s = "";
        for (int i = 0; i <= 100; i++) {
            s += mContext.getResources().getString(R.string.crazy);
            s += mContext.getResources().getString(R.string.gyattstacy);
            s += mContext.getResources().getString(R.string.last_rizzmas);
        }
        if (true) return s;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= 60; i++) {
            sb.append(i).append("\n");
        }
        return sb.toString();
    }
    
    static View.OnTouchListener ScrollAndClickLogic () {
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
                    onScroll(currentY);
                    
                }
                if (MotionEvent.ACTION_UP == action) {
                    if (canSwipe) return true;
                    onClick(currentY);
                }
                return true;
            }
            
            void onScroll(float currentY){
                boolean swipeUp = currentY < previousY;
                boolean swipeDown = currentY > previousY;
                previousY = currentY;
                if (swipeUp) {
                    if ((startLine + MAX_LINES) >= contentStrings.length) return;
                    startLine++;
                    previousY -= SCROLL_STRENGTH;
                }
                if (swipeDown) {
                    if (startLine <= 0) return;
                    startLine--;
                    previousY += SCROLL_STRENGTH;
                }
                mContext.setTitle("startLine: " + startLine);
                refreshTexts();
            }

            void onClick (float y) {
                int touchedSection = (int) (y / linesHeight);
                boolean lastSection = touchedSection == MAX_LINES;
                if (lastSection) return;
                mContext.setTitle(touchedSection + "");
            }
        };
    }
}
