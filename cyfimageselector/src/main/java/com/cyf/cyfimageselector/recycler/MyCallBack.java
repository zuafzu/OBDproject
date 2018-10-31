package com.cyf.cyfimageselector.recycler;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.cyf.cyfimageselector.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by caoyingfu on 2018/2/26.
 */

public class MyCallBack extends ItemTouchHelper.Callback {

    private final int padding = 8;

    private View tagetView;
    private int dragFlags;
    private int swipeFlags;
    private PostArticleImgAdapter adapter;
    private List<String> originImages;//图片没有经过处理，这里传这个进来是为了使原图片的顺序与拖拽顺序保持一致
    private List<View> viewList = new ArrayList<>();
    private boolean up;//手指抬起标记位
    private RecyclerView recyclerView;
    private boolean isUp = false;//拖拽后是否抬手

    private boolean isCanDelete = false;

    /**
     * 状态栏高度
     */
    private int mStatusHeight = 0;

    public void setCanDelete(boolean canDelete) {
        isCanDelete = canDelete;
    }

    public MyCallBack(PostArticleImgAdapter adapter, List<String> originImages, RecyclerView recyclerView) {
        this.adapter = adapter;
        this.originImages = originImages;
        this.recyclerView = recyclerView;
        mStatusHeight = getStatusHeight(recyclerView.getContext());
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (windowParams != null) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_MOVE:
                            isUp = false;
                            int x = (int) motionEvent.getRawX();
                            int y = (int) motionEvent.getRawY();
                            windowParams.x = x - (windowParams.width / 2);
                            windowParams.y = y - (windowParams.height / 2);
                            if (windowParams.y < mStatusHeight) {
                                windowParams.y = mStatusHeight;
                            }
                            windowManager.updateViewLayout(virtualImage, windowParams);
                            break;
                        case MotionEvent.ACTION_UP:
                            isUp = true;
                            break;
                    }
                }
                return false;
            }
        });
        windowManager = (WindowManager) adapter.getmContext().getSystemService(Context.WINDOW_SERVICE);
    }

    /**
     * 设置item是否处理拖拽事件和滑动事件，以及拖拽和滑动操作的方向
     *
     * @param recyclerView
     * @param viewHolder
     * @return
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //判断 recyclerView的布局管理器数据
        if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {//设置能拖拽的方向
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            swipeFlags = 0;//0则不响应事件
        }
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {//设置能拖拽的方向
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            swipeFlags = 0;//0则不响应事件
        }
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    /**
     * 当用户从item原来的位置拖动可以拖动的item到新位置的过程中调用
     *
     * @param recyclerView
     * @param viewHolder
     * @param target
     * @return
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition();//得到item原来的position
        int toPosition = target.getAdapterPosition();//得到目标position
//        if (toPosition == originImages.size() - 1 || originImages.size() - 1 == fromPosition) {
//            return true;
//        }
        originImages = ((CyfRecyclerView) recyclerView).getSelectList2();
        if (originImages.get(originImages.size() - 1).equals("add")) {
            if (toPosition == originImages.size() - 1) {
                return true;
            }
        }
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(originImages, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(originImages, i, i - 1);
            }
        }
        adapter.notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    /**
     * 设置是否支持长按拖拽
     *
     * @return
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    /**
     * @param viewHolder
     * @param direction
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

    /**
     * 当用户与item的交互结束并且item也完成了动画时调用
     *
     * @param recyclerView
     * @param viewHolder
     */
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE &&
                !recyclerView.isComputingLayout()) {
            viewHolder.itemView.setAlpha(1f);
            adapter.setClick(true);
            adapter.setmList();
            adapter.notifyDataSetChanged();
            initData();
            if (dragListener != null) {
                dragListener.clearView();
            }
            /**
             * 有镜像时将其移除
             */
            if (windowManager != null) {
                for (int i = 0; i < viewList.size(); i++) {
                    try {
                        windowManager.removeView(viewList.get(i));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                viewList.clear();
                windowParams = null;
                tagetView.setAlpha(1.0f);
            }
        }
    }

    /**
     * 重置
     */
    private void initData() {
        if (dragListener != null) {
            dragListener.deleteState(false);
            dragListener.dragState(false);
        }
        up = false;
    }

    /**
     * 自定义拖动与滑动交互
     *
     * @param c
     * @param recyclerView
     * @param viewHolder
     * @param dX
     * @param dY
     * @param actionState
     * @param isCurrentlyActive
     */
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (null == dragListener) {
            return;
        }
        if (isCanDelete) {
            int[] location = new int[2];
            viewHolder.itemView.getLocationOnScreen(location);
            int[] location2 = new int[2];
            tv_delete.getLocationOnScreen(location2);
            if (location[1] + padding / 2 > location2[1] - viewHolder.itemView.getHeight() / 2 - recyclerView.getContext().getResources().getDimension(R.dimen.delete_height) / 2) {
//            if (dY >= (recyclerView.getHeight()
//                    - viewHolder.itemView.getBottom()//item底部距离recyclerView顶部高度
//                    - recyclerView.getContext().getResources().getDimension(R.dimen.delete_height))) {
                //拖到删除处
                dragListener.deleteState(true);
                if (up) {//在删除处放手，则删除item
                    windowParams.alpha = 0.0f;
                    viewHolder.itemView.setVisibility(View.INVISIBLE);//先设置不可见，如果不设置的话，会看到viewHolder返回到原位置时才消失，因为remove会在viewHolder动画执行完成后才将viewHolder删除
                    originImages.remove(viewHolder.getAdapterPosition());
                    adapter.setmList();
                    adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                    initData();
                    return;
                }
            } else {
                // 没有到删除处
                if (View.INVISIBLE == viewHolder.itemView.getVisibility()) {//如果viewHolder不可见，则表示用户放手，重置删除区域状态
                    dragListener.dragState(false);
                }
                dragListener.deleteState(false);
                // 回弹动画
                if (isUp) {
                    if (windowParams != null) {
                        int[] location3 = new int[2];
                        viewHolder.itemView.getLocationOnScreen(location3);
                        int x = location3[0];
                        int y = location3[1];
                        windowParams.x = x - padding / 2;
                        windowParams.y = y - padding / 2;
                        windowManager.updateViewLayout(virtualImage, windowParams);
                    }
                }
            }
        } else {
            if (View.INVISIBLE == viewHolder.itemView.getVisibility()) {//如果viewHolder不可见，则表示用户放手，重置删除区域状态
                dragListener.dragState(false);
            }
            dragListener.deleteState(false);
            // 回弹动画
            if (isUp) {
                if (windowParams != null) {
                    int[] location3 = new int[2];
                    viewHolder.itemView.getLocationOnScreen(location3);
                    int x = location3[0];
                    int y = location3[1];
                    windowParams.x = x - padding / 2;
                    windowParams.y = y - padding / 2;
                    windowManager.updateViewLayout(virtualImage, windowParams);
                }
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    /**
     * 当长按选中item的时候（拖拽开始的时候）调用
     *
     * @param viewHolder
     * @param actionState
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (ItemTouchHelper.ACTION_STATE_DRAG == actionState && dragListener != null) {
            dragListener.dragState(true);
            tagetView = viewHolder.itemView;
            viewHolder.itemView.setAlpha(0.0f);
            //显示镜像view
            int[] location = new int[2];
            viewHolder.itemView.getLocationOnScreen(location);
            int x = location[0];
            int y = location[1];
            virtualImage = showVirtualView(getBitmap(viewHolder.itemView), x, y);
            viewList.add(virtualImage);
            adapter.setClick(false);
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    /**
     * 设置手指离开后ViewHolder的动画时间，在用户手指离开后调用
     *
     * @param recyclerView
     * @param animationType
     * @param animateDx
     * @param animateDy
     * @return
     */
    @Override
    public long getAnimationDuration(RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
        //手指放开
        up = true;
        return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy);
    }

    public interface DragListener {
        /**
         * 用户是否将 item拖动到删除处，根据状态改变颜色
         *
         * @param delete
         */
        void deleteState(boolean delete);

        /**
         * 是否于拖拽状态
         *
         * @param start
         */
        void dragState(boolean start);

        /**
         * 当用户与item的交互结束并且item也完成了动画时调用
         */
        void clearView();
    }

    private DragListener dragListener;
    private TextView tv_delete;

    public void setDragListener(DragListener dragListener, TextView tv_delete) {
        this.dragListener = dragListener;
        if (tv_delete != null) {
            this.tv_delete = tv_delete;
        }
    }

    public void setDragListener(DragListener dragListener) {
        this.dragListener = dragListener;
    }

    /**
     * windowManager全局变量，用于拖动时显示item的镜像
     */
    private WindowManager windowManager;

    /**
     * 拖动时的item镜像
     */
    private ImageView virtualImage;

    /**
     * item镜像的参数
     */
    private WindowManager.LayoutParams windowParams;

    /**
     * 将镜像bitmap显示在屏幕上，返回显示的imageView
     *
     * @param virtualView 镜像bitmap
     * @param x           显示在屏幕上的x值
     * @param y           显示在屏幕上的y值
     * @return 返回imageVIew
     */
    private ImageView showVirtualView(Bitmap virtualView, float x, float y) {
        windowParams = new WindowManager.LayoutParams();
        windowParams.gravity = Gravity.START | Gravity.TOP;
        windowParams.x = (int) x - padding / 2;
        windowParams.y = (int) y - padding / 2;
        windowParams.alpha = 0.8f;
        windowParams.width = (int) (virtualView.getWidth() * 1f) + padding;
        windowParams.height = (int) (virtualView.getHeight() * 1f) + padding;
        windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        windowParams.format = PixelFormat.TRANSLUCENT;
        windowParams.windowAnimations = 0;
        ImageView imageView = new ImageView(adapter.getmContext());
        imageView.setImageBitmap(virtualView);
        windowManager.addView(imageView, windowParams);
        return imageView;

    }

    /**
     * 通过item position位置获得bitmap
     *
     * @param view gridView的view
     * @return
     */
    private Bitmap getBitmap(View view) {
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        return Bitmap.createBitmap(view.getDrawingCache());
    }

    /**
     * 获取状态栏的高度
     *
     * @param context
     * @return
     */
    private static int getStatusHeight(Context context) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int height = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = context.getResources().getDimensionPixelSize(height);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

}