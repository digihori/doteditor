package tk.horiuchi.doteditor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import static tk.horiuchi.doteditor.MainActivity.digit;
import static tk.horiuchi.doteditor.MainActivity.scale;

/**
 * Created by yoshimine on 2018/10/13.
 */

public class MyEditView extends View implements View.OnTouchListener {
    //private float scale = 2;
    private final Paint paint = new Paint();

    public MyEditView(Context context, AttributeSet attrs) {
        super(context, attrs);

        findViewById(R.id.myEditView).setOnTouchListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.scale(scale, scale);

        int stp_x, stp_y, d_row, d_col;
        int x, y;
        int i, j;

        stp_x = 26;
        stp_y = 26;
        d_row=7;
        d_col=5;
        canvas.drawColor(0xFFEEFFFF);

        paint.setStyle(Paint.Style.FILL);

        for (j = 0, x = 0; j < d_col; j++, x += stp_x) {
            for (i = 0, y = 0; i < d_row; i++, y += stp_y) {
                if ((digit[j] & 0x01 << i) != 0) {
                    paint.setColor(Color.DKGRAY);
                } else {
                    paint.setColor(0xffdcdcdc);
                }
                canvas.drawRect(x+1, y+1, x + stp_x, y + stp_y, paint);
            }
        }

        ((TextView)(getRootView().findViewById(R.id.text1))).setText(String.format("%02X %02X %02X %02X %02X", digit[0], digit[1], digit[2], digit[3], digit[4]));

    }

    public boolean onTouch(View v, MotionEvent event) {
        //int id = v.getId();

        int eventAction = event.getActionMasked();
        int x = (int)(event.getX()/(26*scale));
        int y = (int)(event.getY()/(26*scale));

        switch (eventAction) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                digit[x] ^= (1 << y);
                Log.w("onTouch", String.format("%02x %02x %02x %02x %02x", digit[0], digit[1], digit[2], digit[3], digit[4]));
                //((TextView)(getRootView().findViewById(R.id.text1))).setText(String.format("%02X %02X %02X %02X %02X", digit[0], digit[1], digit[2], digit[3], digit[4]));
                invalidate();
                Log.w("ACTION", String.format("(%d,%d)", x, y));
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_MOVE:
                Log.w("ACTION", String.format("(%d,%d)", x, y));
                break;
        }
        return false;
    }
}
