package cc.brainbook.viewpager.looppageradapter;

/**
 * Interface definition for creating a {@link ViewHolder}.
 *
 * @author Robert Han
 * @email brainbook.cc@outlook.com
 * @website www.brainbook.cc
 * @time 2016/4/8 23:35
 */
public interface ViewHolderCreator<T> {
     T createViewHolder();
}
