package com.xenia.android.emulator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import com.xenia.android.R;

/**
 * Full-screen emulator activity that runs an Xbox 360 game.
 *
 * <p>The game path must be supplied as a cvar via the EXTRA_CVARS Bundle
 * with the key {@code "target"} — matching Xenia's
 * {@code DEFINE_transient_path(target, ...)} cvar from xenia_main.cc.</p>
 *
 * <p>Example launch:</p>
 * <pre>
 *   Bundle cvars = new Bundle();
 *   cvars.putString("target", "/sdcard/game.iso");  // or content:// path
 *   cvars.putString("gpu", "vulkan");               // optional overrides
 *   cvars.putString("apu", "nop");
 *   Intent i = new Intent(this, EmulatorActivity.class);
 *   i.putExtra(WindowedAppActivity.EXTRA_CVARS, cvars);
 *   startActivity(i);
 * </pre>
 */
public class EmulatorActivity extends WindowedAppActivity {

    @Override
    protected String getWindowedAppIdentifier() {
        // Must match XE_DEFINE_WINDOWED_APP(xenia, ...) in xenia_main.cc.
        return "xenia";
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emulator);
        setWindowSurfaceView(findViewById(R.id.emulator_surface_view));
        enterFullscreen();
    }

    @Override
    public void onWindowFocusChanged(final boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            enterFullscreen();
        }
    }

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            confirmExit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // -------------------------------------------------------------------------

    private void enterFullscreen() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            final WindowInsetsController c = getWindow().getInsetsController();
            if (c != null) {
                c.hide(WindowInsets.Type.statusBars()
                        | WindowInsets.Type.navigationBars());
                c.setSystemBarsBehavior(
                        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            //noinspection deprecation
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    private void confirmExit() {
        new android.app.AlertDialog.Builder(this)
                .setTitle(R.string.dialog_exit_title)
                .setMessage(R.string.dialog_exit_message)
                .setPositiveButton(R.string.dialog_exit_confirm,
                        (d, w) -> finish())
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}
