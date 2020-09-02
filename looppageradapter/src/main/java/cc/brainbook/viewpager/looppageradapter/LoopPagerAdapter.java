package cc.brainbook.viewpager.looppageradapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.util.List;

import static cc.brainbook.viewpager.looppageradapter.BuildConfig.DEBUG;

/**
 * The seamless infinite loop pager adapter class that extends {@link PagerAdapter}.
 *
 * <p>LoopPagerAdapter supports arbitrary data type by using {@link ViewHolder}.</p>
 * <p>Considering efficiency issues, we implement the infinite loop pager adapter with COUNT_FACTOR instead of Integer.MAX_VALUE.</p>
 *
 * @author Robert Han
 * @email brainbook.cc@outlook.com
 * @website www.brainbook.cc
 * @time 2016/4/8 14:07
 */
public class LoopPagerAdapter extends PagerAdapter {
    private static final String TAG = "LoopPagerAdapter";

    /**
     * The {@link List} data used by LoopPagerAdapter.
     */
    private List mData;

    /**
     * The {@link ViewHolder} creator used by LoopPagerAdapter.
     */
    private ViewHolderCreator mViewHolderCreator;

    /**
     * The static final factor multiplied by {@link #getRealCount()}.
     *
     * <p>Note: considering that a view page may show multiple pages, e.g. padding, COUNT_FACTOR must be at least 3.</p>
     */
    private static final int COUNT_FACTOR = 3;

    /**
     * Whether can loop or not.
     */
    private boolean mCanLoop = true;

    /**
     * The count of updating the {@link ViewPager}. It will be 1 when initial show.
     *
     * <p>Note: when flipping pages quickly, it counts only ONCE, although it may turn over many pages!</p>
     */
    private int mUpdateCount;

    /**
     * The position of updating the {@link ViewPager}.
     */
    private int mUpdatePosition = -1;

    /**
     * Listener used to dispatch starting update events.
     */
    public OnStartUpdateListener mOnStartUpdateListener;

    /**
     * Constructor to use when creating a LoopPagerAdapter from code.
     *
     * @param holderCreator The {@link ViewHolder} creator .
     * @param data The {@link List} data.
     */
    public LoopPagerAdapter(ViewHolderCreator holderCreator, List data) {
        this.mData = data;
        this.mViewHolderCreator = holderCreator;
    }

    /**
     * Interface definition for a callback to be invoked when a view pager is starting update.
     */
    public interface OnStartUpdateListener {
        void onStartUpdate(@NonNull ViewGroup container, int updatePosition, int updateCount);
    }

    /**
     * Register a callback to be invoked when this view pager is starting update.
     *
     * @param l     The callback that will run
     */
    public void setOnStartUpdateListener(@Nullable OnStartUpdateListener l) {
        mOnStartUpdateListener = l;
    }

    /**
     * Get whether can loop or not.
     */
    public boolean getCanLoop() {
        return mCanLoop;
    }

    /**
     * Set whether can loop or not.
     */
    public void setCanLoop(boolean canLoop) {
        mCanLoop = canLoop;
    }

    /**
     * Return a big enough number of views available. Because of efficiency issues,
     * instead of using Integer.MAX_VALUE, we use multiply getRealCount() by COUNT_FACTOR.
     *
     * @return Returns a big enough number of views available.
     */
    @Override
    public int getCount() {
        return mCanLoop ? getRealCount() * COUNT_FACTOR : getRealCount();
    }

    /**
     * Return the real number of views available.
     *
     * @return Returns the real number of views available.
     */
    public int getRealCount() {
        return null == mData ? 0 : mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;///https://developer.android.com/reference/android/support/v4/view/PagerAdapter.html
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (DEBUG) Log.i(TAG, "instantiateItem: addView: position: " + position);
        if(0 == getRealCount()) return null;///Note: deal with `divide by 0` error occurs when the number of pages is 0.
        final int realPosition = position % getRealCount();
        if (DEBUG) Log.i(TAG, "instantiateItem: addView: realPosition: " + realPosition);

        final View view = getView(container, realPosition);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (DEBUG) Log.i(TAG, "destroyItem: position: " + position);
        container.removeView((View) object);
    }

    /**
     * Called when a change in the shown pages is going to start being made.
     *
     * @param container The containing View which is displaying this adapter's
     * page views.
     */
    @Override
    public void startUpdate(@NonNull ViewGroup container) {
        final int position = ((ViewPager)container).getCurrentItem();
        if (DEBUG) Log.i(TAG, "startUpdate: position: " + position);

        ///Solve the issue of not being right-slip.
        ///Solve the issue of left-slip out of bounds without using Integer.MAX_VALUE.
        if(mCanLoop && (position < getRealCount() || position >= getCount() - getRealCount())){
            if(0 == getRealCount()) return;///Note: deal with `divide by 0` error occurs when the number of pages is 0.
            final int realPosition = position % getRealCount() + getRealCount();
            if (DEBUG) Log.i(TAG, "startUpdate: realPosition: " + realPosition);

            ///Solved bug: the transformer appear blank page.
            try {
                Field mFirstLayout = ViewPager.class.getDeclaredField("mFirstLayout");
                mFirstLayout.setAccessible(true);
                mFirstLayout.set(container, true);
            }catch(Exception e) {
                e.printStackTrace();
            }

            ((ViewPager)container).setCurrentItem(realPosition, false);

            ///Solved bug: when the number of pictures is 1, there is no call event.
            if(1 == getRealCount() && mUpdatePosition != position) {
                mUpdatePosition = realPosition;
                mUpdateCount++;
                if(null != mOnStartUpdateListener) {
                    mOnStartUpdateListener.onStartUpdate(container, mUpdatePosition, mUpdateCount);
                }
            }
        } else {
            ///When you update the same location multiple times, only count once.
            if(mUpdatePosition != position) {
                mUpdatePosition = position;
                mUpdateCount++;
                if(null != mOnStartUpdateListener) {
                    mOnStartUpdateListener.onStartUpdate(container, mUpdatePosition, mUpdateCount);
                }
            }
        }
    }

    /**
     * Get a view from {@link ViewHolder}.
     *
     * @param container The containing View in which the page will be shown.
     * @param position The page position to be instantiated.
     * @return Returns a View representing the new page.
     */
    private View getView(@NonNull ViewGroup container, int position){
        final ViewHolder holder = (ViewHolder) mViewHolderCreator.createViewHolder();
        if(null == holder){
            throw new RuntimeException("Could not return a null holder");
        }

        final View view = holder.createView(container.getContext());

        if( null != mData && !mData.isEmpty()){
            holder.onBind(container.getContext(), position, mData.get(position));
        }

        return view;
    }

}
