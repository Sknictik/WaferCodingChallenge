package sknictik.wafercodingchallenge.presentation.main;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;
import android.view.View;

public class SwipeToDeleteHelperCallback extends ItemTouchHelper.Callback {

    private static final int MAX_ALPHA = 255;
    private static final int ANCHOR_POINT = 300;

    private boolean swipeBack;
    private DeleteButtonsState deleteButtonsState = DeleteButtonsState.GONE;

    private final OnItemDeletedListener onItemDeletedListener;

    private final ColorDrawable purpleBackground = new ColorDrawable(Color.rgb(128, 0, 128));

    //Constructor will still be available in this file
    SwipeToDeleteHelperCallback(final OnItemDeletedListener onItemDeletedListener) {
        this.onItemDeletedListener = onItemDeletedListener;
    }

    @Override
    public void onSelectedChanged(@Nullable final RecyclerView.ViewHolder viewHolder, final int actionState) {
        if (viewHolder != null) {
            final View foregroundView = ((InfoListAdapter.InfoViewHolder) viewHolder).foreground;

            getDefaultUIUtil().onSelected(foregroundView);
        }
    }

    @Override
    public boolean onMove(@NonNull final RecyclerView recyclerView, @NonNull final RecyclerView.ViewHolder viewHolder, @NonNull final RecyclerView.ViewHolder target) {
        //Not implemented
        return false;
    }

    @Override
    public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, final int swipeDir) {
        final int position = viewHolder.getAdapterPosition();
        if (onItemDeletedListener != null) {
            onItemDeletedListener.onItemDeleted(position);
        }
    }

    @Override
    public void onChildDraw(@NonNull final Canvas canvas, @NonNull final RecyclerView recyclerView,
                            @NonNull final RecyclerView.ViewHolder viewHolder, float dX,
                            final float dY, final int actionState, final boolean isCurrentlyActive) {
        if (viewHolder.getAdapterPosition() < 0) {
            return;
        }

        final View view = viewHolder.itemView; //swiped view

        purpleBackground.setAlpha(Math.round(MAX_ALPHA - MAX_ALPHA * (Math.abs(dX) / recyclerView.getWidth())));

        purpleBackground.setBounds(view.getRight() + Math.round(dX), view.getTop(), view.getRight(), view.getBottom());
        purpleBackground.draw(canvas);

        final View foregroundView = ((InfoListAdapter.InfoViewHolder) viewHolder).foreground;

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (deleteButtonsState != DeleteButtonsState.GONE) {
                super.onChildDraw(canvas, recyclerView, viewHolder, Math.min(dX, -ANCHOR_POINT), dY, actionState, isCurrentlyActive);
            } else {
                //getDefaultUIUtil().onDraw(canvas, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
                setTouchListener(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }
        if (deleteButtonsState == DeleteButtonsState.GONE) {
            super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
        //drawButtons(canvas, viewHolder);
    }

    @Override
    public void onChildDrawOver(@NonNull final Canvas canvas, @NonNull final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
        final View foregroundView = ((InfoListAdapter.InfoViewHolder) viewHolder).foreground;

        getDefaultUIUtil().onDrawOver(canvas, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void clearView(@NonNull final RecyclerView recyclerView, @NonNull final RecyclerView.ViewHolder viewHolder) {
        final View foregroundView = ((InfoListAdapter.InfoViewHolder) viewHolder).foreground;

        //foregroundView.setTranslationX(50);

        getDefaultUIUtil().clearView(foregroundView);
    }

    @Override
    public int getMovementFlags(@NonNull final RecyclerView recyclerView, @NonNull final RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.LEFT);
    }

    @Override
    public int convertToAbsoluteDirection(final int flags, final int layoutDirection) {
        if (swipeBack) {
            swipeBack = deleteButtonsState != DeleteButtonsState.GONE;
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    private void setTouchListener(final Canvas canvas,
                                  final RecyclerView recyclerView,
                                  final RecyclerView.ViewHolder viewHolder,
                                  final float dX, final float dY,
                                  final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                //TODO rework condition for swipeback?
                swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
                if (swipeBack) {
                    if (dX < -ANCHOR_POINT) {
                        deleteButtonsState = DeleteButtonsState.VISIBLE;
                    }
                    if (deleteButtonsState != DeleteButtonsState.GONE) {
                        setTouchDownListener(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        setItemsClickable(recyclerView, false);
                    }
                }
                return false;
            }
        });
    }

    private void setTouchDownListener(final Canvas c,
                                      final RecyclerView recyclerView,
                                      final RecyclerView.ViewHolder viewHolder,
                                      final float dX, final float dY,
                                      final int actionState, final boolean isCurrentlyActive) {

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
                return false;
            }
        });
    }

    private void setTouchUpListener(final Canvas c,
                                    final RecyclerView recyclerView,
                                    final RecyclerView.ViewHolder viewHolder,
                                    final float dX, final float dY,
                                    final int actionState, final boolean isCurrentlyActive) {

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    SwipeToDeleteHelperCallback.super.onChildDraw(c, recyclerView, viewHolder, 0F, dY, actionState, isCurrentlyActive);
                    recyclerView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return false;
                        }
                    });
                    setItemsClickable(recyclerView, true);
                    swipeBack = false;

                    deleteButtonsState = DeleteButtonsState.GONE;
                }
                return false;
            }
        });
    }

    private void setItemsClickable(final RecyclerView recyclerView,
                                   final boolean isClickable) {
        for (int i = 0; i < recyclerView.getChildCount(); ++i) {
            recyclerView.getChildAt(i).setClickable(isClickable);
        }
    }

    private void drawButtons(final Canvas canvas, final RecyclerView.ViewHolder viewHolder) {
        //final View view = viewHolder.itemView;

        /*final Drawable bombMark = ContextCompat.getDrawable(view.getContext(), R.drawable.ic_bomb);
        final int bombMarkMargin = view.getContext().getResources().getDimensionPixelSize(R.dimen.bomb_icon_margin);

        final int iconTop = view.getTop() + (view.getBottom() - view.getTop() - bombMark.getIntrinsicHeight()) / 2;
        bombMark.setBounds(view.getRight() - bombMarkMargin - bombMark.getIntrinsicWidth(),
                iconTop,
                view.getRight() - bombMarkMargin,
                iconTop + bombMark.getIntrinsicHeight());
        bombMark.draw(canvas);

        view.invalidate();*/

        //buttonInstance = bombMark;
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        return super.getSwipeEscapeVelocity(defaultValue);
    }

    public interface OnItemDeletedListener {
        void onItemDeleted(int positionOfDeletedItem);
    }

    private enum DeleteButtonsState {
        GONE,
        VISIBLE
    }

}
