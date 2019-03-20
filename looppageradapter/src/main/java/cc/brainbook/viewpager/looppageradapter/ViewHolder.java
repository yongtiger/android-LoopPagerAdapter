package cc.brainbook.viewpager.looppageradapter;

import android.content.Context;
import android.view.View;

/**
 * Interface definition for creating a {@link View} and a callback to be invoked when data binding.
 *
 * @author Robert Han
 * @email brainbook.cc@outlook.com
 * @website www.brainbook.cc
 * @time 2016/4/8 23:33
 */
public interface ViewHolder<T> {
    View createView(Context context);
    void onBind(Context context, int position, T data);
}
