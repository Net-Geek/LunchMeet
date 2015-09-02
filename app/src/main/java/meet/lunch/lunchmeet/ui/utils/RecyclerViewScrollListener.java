package meet.lunch.lunchmeet.ui.utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import meet.lunch.lunchmeet.ui.widgets.ControllableAppBarLayout;

/**
 * Created by aaron on 7/29/2015.
 * <p/>
 * Automatically expands and collapses toolbar
 */
public class RecyclerViewScrollListener extends RecyclerView.OnScrollListener {

    ControllableAppBarLayout controllableAppBarLayout;
    LinearLayoutManager linearLayoutManager;
    boolean scrollingUp;

    public RecyclerViewScrollListener(ControllableAppBarLayout controllableAppBarLayout) {
        this.controllableAppBarLayout = controllableAppBarLayout;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0){
            if (newState == RecyclerView.SCROLL_STATE_IDLE && !scrollingUp){
                controllableAppBarLayout.expandToolbar(true);
            }
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        scrollingUp = dy > 0;
    }
}
