package meet.lunch.lunchmeet.ui.utils;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import meet.lunch.lunchmeet.ui.widgets.MaterialSheetFab;

/**
 * Created by Aaron on 7/29/2015.
 *
 * Listener for recycler view touches to handle fab sheet
 */
public class RecyclerViewTouchListener implements RecyclerView.OnTouchListener {

    public MaterialSheetFab materialSheetFab;

    public RecyclerViewTouchListener(MaterialSheetFab materialSheetFab) {
        this.materialSheetFab = materialSheetFab;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (materialSheetFab.isSheetVisible()) {
            materialSheetFab.hideSheet();
        }
        return false;
    }
}
