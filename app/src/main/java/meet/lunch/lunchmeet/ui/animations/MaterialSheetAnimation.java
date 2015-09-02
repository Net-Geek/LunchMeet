package meet.lunch.lunchmeet.ui.animations;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Interpolator;

import com.gordonwong.materialsheetfab.animations.AnimationListener;

import io.codetail.animation.SupportAnimator;

/**
 * Created by Aaron on 7/29/2015.
 *
 * Animation for fab sheet
 */
public class MaterialSheetAnimation {
    private View sheet;
    private int sheetColor;
    private int fabColor;
    private Interpolator interpolator;

    public MaterialSheetAnimation(View sheet, int sheetColor, int fabColor, Interpolator interpolator) {
        this.sheet = sheet;
        this.sheetColor = sheetColor;
        this.fabColor = fabColor;
        this.interpolator = interpolator;
    }

    public void morphFromFab(View fab, long showSheetDuration, long showSheetColorDuration, AnimationListener listener) {
        this.sheet.setVisibility(View.VISIBLE);
        this.revealSheetWithFab(fab, this.getFabRevealRadius(fab), this.getSheetRevealRadius(), showSheetDuration, this.fabColor, this.sheetColor, showSheetColorDuration, listener);
    }

    public void morphIntoFab(View fab, long hideSheetDuration, long hideSheetColorDuration, AnimationListener listener) {
        this.revealSheetWithFab(fab, this.getSheetRevealRadius(), this.getFabRevealRadius(fab), hideSheetDuration, this.sheetColor, this.fabColor, hideSheetColorDuration, listener);
    }

    protected void revealSheetWithFab(View fab, float startRadius, float endRadius, long sheetDuration, int startColor, int endColor, long sheetColorDuration, AnimationListener listener) {
        if (listener != null) {
            listener.onStart();
        }

        AnimationListener revealListener = sheetDuration >= sheetColorDuration ? listener : null;
        AnimationListener colorListener = sheetColorDuration > sheetDuration ? listener : null;
        this.startCircularRevealAnim(this.sheet, this.getSheetRevealCenterX(), this.getSheetRevealCenterY(fab), startRadius, endRadius, sheetDuration, this.interpolator, revealListener);
        this.startColorAnim(this.sheet, startColor, endColor, sheetColorDuration, this.interpolator, colorListener);
    }

    protected void startCircularRevealAnim(View view, int centerX, int centerY, float startRadius, float endRadius, long duration, Interpolator interpolator, final AnimationListener listener) {
        if (Build.VERSION.SDK_INT >= 21) {
            Animator anim = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius);
            anim.setDuration(duration);
            anim.setInterpolator(interpolator);
            anim.addListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animation) {
                    if (listener != null) {
                        listener.onStart();
                    }

                }

                public void onAnimationEnd(Animator animation) {
                    if (listener != null) {
                        listener.onEnd();
                    }

                }
            });
            anim.start();
        } else {
            centerX = (int) ((float) centerX + view.getX());
            centerY = (int) ((float) centerY + view.getY());
            SupportAnimator anim1 = io.codetail.animation.ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius);
            anim1.setDuration((int) duration);
            anim1.setInterpolator(interpolator);
            anim1.addListener(new SupportAnimator.SimpleAnimatorListener() {
                public void onAnimationStart() {
                    if (listener != null) {
                        listener.onStart();
                    }

                }

                public void onAnimationEnd() {
                    if (listener != null) {
                        listener.onEnd();
                    }

                }
            });
            anim1.start();
        }

    }

    protected void startColorAnim(final View view, int startColor, int endColor, long duration, Interpolator interpolator, final AnimationListener listener) {
        ValueAnimator anim = ValueAnimator.ofObject(new ArgbEvaluator(), new Object[]{Integer.valueOf(startColor), Integer.valueOf(endColor)});
        anim.setDuration(duration);
        anim.setInterpolator(interpolator);
        anim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animation) {
                if (listener != null) {
                    listener.onStart();
                }

            }

            public void onAnimationEnd(Animator animation) {
                if (listener != null) {
                    listener.onEnd();
                }

            }
        });
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animator) {
                Integer color = (Integer) animator.getAnimatedValue();
                view.setBackgroundColor(color.intValue());
            }
        });
        anim.start();
    }

    public void setSheetVisibility(int visibility) {
        this.sheet.setVisibility(visibility);
    }

    public boolean isSheetVisible() {
        return this.sheet.getVisibility() == View.VISIBLE;
    }

    public float getSheetCenterX() {
        return this.sheet.getX() + (float) this.getSheetRevealCenterX();
    }

    public int getRevealTranslationY() {
        return this.sheet.getHeight() / 5;
    }

    protected int getSheetRevealCenterX() {
        return this.sheet.getWidth() / 2;
    }

    protected int getSheetRevealCenterY(View fab) {
        return (int) ((float) (this.sheet.getHeight() * 4 / 5) - fab.getPivotY());
    }

    protected float getSheetRevealRadius() {
        return (float) Math.max(this.sheet.getWidth(), this.sheet.getHeight());
    }

    protected float getFabRevealRadius(View fab) {
        return (float) (Math.max(fab.getWidth(), fab.getHeight()) / 2);
    }
}
