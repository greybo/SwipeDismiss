package com.swipe.swipedisvissview;

import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class Swipe {

    private static final String TAG = "Swipe_tag";
    private float startTransitionX;
    private float histori;
    private float scale;
    private float width = 2000;
    private float height = 2000;
    private int threshold = 200;
    private SwipeCallback callback;
    private List<View> views;
    private View viewChanges;

    public abstract static class SwipeCallback {

        public abstract void move(View view, float transitionX);

        public abstract void end(View view);

        public abstract void click(View view);
    }

    public Swipe(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = null;
        Point size = new Point();
        if (wm != null) {
            display = wm.getDefaultDisplay();
            display.getSize(size);
        }
        this.width = size.x;
        this.height = size.y;
        threshold = (int) (width / 5);

    }

    private void touchMotionEvent(MotionEvent e) {

        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            histori = e.getX() - startTransitionX;
            isContain(e);
        }
        scale = e.getX() - histori;
        move(scale);
        if (e.getAction() == MotionEvent.ACTION_UP) {
            if (Math.abs(scale) < threshold) {
                if (scale == 0) {
                    click(viewChanges);
                } else {
                    move(0);
                }
                viewChanges = null;
            } else {
                taskTransition();
            }
        }
    }

    private void click(View view) {
        if (callback != null) {
            callback.click(view);
        }
    }

    private void end(View view) {
        if (callback != null) {
            callback.end(view);
        }
        view.setVisibility(View.GONE);
    }

    private void move(float translationX) {
        if (viewChanges != null) {
            if (callback != null) {
                callback.move(viewChanges, translationX);
            }
            viewChanges.setTranslationX(translationX);
        }

    }

    private void taskTransition() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(1);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (scale < width && scale > -width) {
                                if (scale > 0)
                                    scale += 100;
                                else
                                    scale -= 100;
                                move(scale);
                                taskTransition();
                            } else {
                                if (viewChanges != null) {
                                    end(viewChanges);
                                }
                                move(0);
                                histori = 0;
                                viewChanges = null;
                            }
                        }
                    });
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        };
        thread.start();
    }


    private void isContain(MotionEvent ev) {
        for (View view : views) {
            int left = (int) view.getX();
            int top = (int) view.getY();
            int right = (int) view.getX() + view.getWidth();
            int bottom = (int) view.getY() + view.getHeight();

            if (left < ev.getX() && ev.getX() < right &&
                    top < ev.getY() && ev.getY() < bottom) {
                viewChanges = view;
                return;
            }
        }
    }

    public void setCallback(SwipeCallback callback) {
        this.callback = callback;
    }

    public void setStartTransitionX(float transitionX) {
        this.startTransitionX = transitionX;
    }

    public void addView(View view) {
        if (views == null) {
            views = new ArrayList<>();
        }
        this.views.add(view);
    }

    public void setLayout(RelativeLayout layout) {
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                touchMotionEvent(motionEvent);
                return true;
            }
        });
    }
}
