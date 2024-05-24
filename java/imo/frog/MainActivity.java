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

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mContext = this;
        final ViewGroup codeLayout = findViewById(R.id.code_layout);
        CodeLayout.loadWithDelay(mContext, codeLayout, 500);
    }
}
