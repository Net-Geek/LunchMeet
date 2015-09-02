package meet.lunch.lunchmeet.ui.widgets;

import android.os.Build.VERSION;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import com.gordonwong.materialsheetfab.AnimatedFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;
import com.gordonwong.materialsheetfab.animations.AnimationListener;
import com.gordonwong.materialsheetfab.animations.FabAnimation;
import com.gordonwong.materialsheetfab.animations.OverlayAnimation;

import meet.lunch.lunchmeet.R;
import meet.lunch.lunchmeet.ui.animations.MaterialSheetAnimation;

/**
 * Created by Aaron on 7/29/2015.
 *
 * Material sheet for floating action button
 */

public class MaterialSheetFab<FAB extends View & AnimatedFab> extends RecyclerView.OnScrollListener {
    private static final boolean IS_LOLLIPOP;
    private static final int SHEET_ANIM_DURATION;
    private static final int SHOW_SHEET_COLOR_ANIM_DURATION;
    private static final int HIDE_SHEET_COLOR_ANIM_DURATION;
    private static final int SHOW_OVERLAY_ANIM_DURATION;
    private static final int HIDE_OVERLAY_ANIM_DURATION;
    private static final int MOVE_FAB_ANIM_DELAY;
    protected FAB fab;
    protected FabAnimation fabAnimation;
    protected MaterialSheetAnimation sheetAnimation;
    protected OverlayAnimation overlayAnimation;
    protected float anchorX;
    protected float anchorY;
    private boolean isAnimating;
    private boolean isFabLaidOut;
    private MaterialSheetFabEventListener eventListener;

    public MaterialSheetFab(FAB fab, View sheet, View overlay, int sheetColor, int fabColor) {
        Interpolator interpolator = AnimationUtils.loadInterpolator(sheet.getContext(), R.interpolator.msf_interpolator);
        this.fab = fab;
        this.fabAnimation = new FabAnimation(fab, interpolator);
        this.sheetAnimation = new MaterialSheetAnimation(sheet, sheetColor, fabColor, interpolator);
        this.overlayAnimation = new OverlayAnimation(overlay, interpolator);
        sheet.setVisibility(View.INVISIBLE);
        overlay.setVisibility(View.GONE);
        fab.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MaterialSheetFab.this.showSheet();
            }
        });

        fab.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                if (!MaterialSheetFab.this.isFabLaidOut) {
                    MaterialSheetFab.this.updateFabAnchor();
                    MaterialSheetFab.this.isFabLaidOut = true;
                }

            }
        });
    }

    public void showFab() {
        this.showFab(0.0F, 0.0F);
    }

    public void showFab(float translationX, float translationY) {
        this.setFabAnchor(translationX, translationY);
        if(!this.isSheetVisible()) {
            ((AnimatedFab)this.fab).show(translationX, translationY);
        }

    }

    public void showSheet() {
        if(!this.isAnimating()) {
            this.setIsAnimating(true);
            this.overlayAnimation.show((long)SHOW_OVERLAY_ANIM_DURATION, (AnimationListener)null);
            this.morphIntoSheet(new AnimationListener() {
                public void onEnd() {
                    MaterialSheetFab.this.setIsAnimating(false);
                }
            });
            if(this.eventListener != null) {
                this.eventListener.onShowSheet();
            }

        }
    }

    public void hideSheet() {
        this.hideSheet((AnimationListener)null);
    }

    protected void hideSheet(final AnimationListener endListener) {
        if(!this.isAnimating()) {
            this.setIsAnimating(true);
            this.overlayAnimation.hide((long)HIDE_OVERLAY_ANIM_DURATION, (AnimationListener)null);
            this.morphFromSheet(new AnimationListener() {
                public void onEnd() {
                    if(endListener != null) {
                        endListener.onEnd();
                    }

                    MaterialSheetFab.this.setIsAnimating(false);
                }
            });
            if(this.eventListener != null) {
                this.eventListener.onHideSheet();
            }

        }
    }

    public void hideSheetThenFab() {
        AnimationListener listener = new AnimationListener() {
            public void onEnd() {
                ((AnimatedFab)MaterialSheetFab.this.fab).hide();
            }
        };
        if(this.isSheetVisible()) {
            this.hideSheet(listener);
        } else {
            listener.onEnd();
        }

    }

    protected void morphIntoSheet(final AnimationListener endListener) {
        this.updateFabAnchor();
        float endY = this.anchorY - (float)this.sheetAnimation.getRevealTranslationY();
        this.fabAnimation.morphIntoSheet(this.sheetAnimation.getSheetCenterX(), endY, 0, 0.6F, 300L, (AnimationListener)null);
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                MaterialSheetFab.this.fab.setVisibility(View.INVISIBLE);
                MaterialSheetFab.this.sheetAnimation.morphFromFab(MaterialSheetFab.this.fab, (long)MaterialSheetFab.SHEET_ANIM_DURATION, (long)MaterialSheetFab.SHOW_SHEET_COLOR_ANIM_DURATION, endListener);
            }
        }, 150L);
    }

    protected void morphFromSheet(final AnimationListener endListener) {
        this.sheetAnimation.morphIntoFab(this.fab, (long)SHEET_ANIM_DURATION, (long)HIDE_SHEET_COLOR_ANIM_DURATION, (AnimationListener)null);
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                MaterialSheetFab.this.sheetAnimation.setSheetVisibility(4);
                MaterialSheetFab.this.fabAnimation.morphFromSheet(MaterialSheetFab.this.anchorX, MaterialSheetFab.this.anchorY, 0, -0.6F, 300L, endListener);
            }
        }, (long)MOVE_FAB_ANIM_DELAY);
    }

    protected void updateFabAnchor() {
        this.setFabAnchor(this.fab.getTranslationX(), this.fab.getTranslationY());
    }

    protected void setFabAnchor(float translationX, float translationY) {
        this.anchorX = this.fab.getX() + this.fab.getPivotX() + (translationX - this.fab.getTranslationX());
        this.anchorY = this.fab.getY() + this.fab.getPivotY() + (translationY - this.fab.getTranslationY());
    }

    private synchronized boolean isAnimating() {
        return this.isAnimating;
    }

    private synchronized void setIsAnimating(boolean isAnimating) {
        this.isAnimating = isAnimating;
    }

    public boolean isSheetVisible() {
        return this.sheetAnimation.isSheetVisible();
    }

    public void setEventListener(MaterialSheetFabEventListener eventListener) {
        this.eventListener = eventListener;
    }

    static {
        IS_LOLLIPOP = VERSION.SDK_INT >= 21;
        SHEET_ANIM_DURATION = (IS_LOLLIPOP?600:300) * 1;
        SHOW_SHEET_COLOR_ANIM_DURATION = (int)((double)SHEET_ANIM_DURATION * 0.75D);
        HIDE_SHEET_COLOR_ANIM_DURATION = IS_LOLLIPOP?(int)((double)SHEET_ANIM_DURATION * 1.5D):SHEET_ANIM_DURATION * 2;
        SHOW_OVERLAY_ANIM_DURATION = 150 + SHEET_ANIM_DURATION;
        HIDE_OVERLAY_ANIM_DURATION = SHEET_ANIM_DURATION;
        MOVE_FAB_ANIM_DELAY = IS_LOLLIPOP?(int)((double)SHEET_ANIM_DURATION * 0.3D):(int)((double)SHEET_ANIM_DURATION * 0.6D);
    }
}
