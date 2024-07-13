package tk.horiuchi.doteditor;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static int[] digit = {0x42,0x6f,0x7f,0x7f,0x72};
    public static float scale = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics metrics = new DisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();
        display.getMetrics(metrics);
        scale = metrics.scaledDensity;
        Log.w("Main", String.format("scaledDensity=%f\n", scale));

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean init = prefs.getBoolean("init", false);
        if (!init) {
            digit[0] = 0x42;
            digit[1] = 0x6f;
            digit[2] = 0x7f;
            digit[3] = 0x7f;
            digit[4] = 0x72;
        } else {
            for (int i = 0; i < digit.length; i++) {
                digit[i] = prefs.getInt("digit" + i, 0);
            }
        }
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
        findViewById(R.id.btn3).setOnClickListener(this);
        findViewById(R.id.btn4).setOnClickListener(this);
        findViewById(R.id.btnCopy).setOnClickListener(this);

        ((TextView)findViewById(R.id.text1)).setText(String.format("%02X %02X %02X %02X %02X", digit[0], digit[1], digit[2], digit[3], digit[4]));
    }

    @Override
    protected void onPause() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        for (int i = 0; i < digit.length; i++) {
            prefs.edit().putInt("digit"+i, digit[i]).apply();
        }
        prefs.edit().putBoolean("init", true).apply();
        Log.w("Main", "onPause!!!");
        super.onPause();
    }

    public void onClick(View v) {
        int id = v.getId();
        int temp;

        if (id == R.id.btn1) {  // 左右
            temp = digit[0];
            digit[0] = digit[4];
            digit[4] = temp;
            temp = digit[1];
            digit[1] = digit[3];
            digit[3] = temp;
            findViewById(R.id.myEditView).invalidate();
        } else if (id == R.id.btn2) {   // 上下
            for (int i = 0; i < 5; i++) {
                digit[i] = swapBit(digit[i]);
            }
            findViewById(R.id.myEditView).invalidate();
        } else if (id == R.id.btn3) {   // 回転
            temp = digit[0];
            digit[0] = digit[4];
            digit[4] = temp;
            temp = digit[1];
            digit[1] = digit[3];
            digit[3] = temp;
            for (int i = 0; i < 5; i++) {
                digit[i] = swapBit(digit[i]);
            }
            findViewById(R.id.myEditView).invalidate();
        } else if (id == R.id.btn4) {   // 消去
            for (int i = 0; i < 5; i++) {
                digit[i] = 0;
            }
            findViewById(R.id.myEditView).invalidate();
        } else if (id == R.id.btnCopy) {    // クリップボードコピー
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                //str += String.format("&%02X,", digit[i]);
                str.append(String.format("&%02X,", digit[i]));
            }
            ClipboardManager clipboardManager =
                    (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
            if (null == clipboardManager) {
                return;
            }
            clipboardManager.setPrimaryClip(ClipData.newPlainText("", str.toString()));
            Log.w("btnCopy", String.format("copy -> '%s'", str));
        } else {
            ;   // 何もしない
        }

    }

    private int swapBit(int x) {
        int ans = 0;
        ans |= (x & 1) << 6;
        ans |= (x & 2) << 4;
        ans |= (x & 4) << 2;
        ans |= x & 8;
        ans |= (x & 0x10) >> 2;
        ans |= (x & 0x20) >> 4;
        ans |= (x & 0x40) >> 6;
        return ans;
    }
}
