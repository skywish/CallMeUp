package com.example.skywish.imtest001.adapter;

import android.util.SparseArray;
import android.view.View;

/**
 * 网上对Viewholder的简化
 * http://www.eoeandroid.com/thread-321547-1-1.html
 */
@SuppressWarnings("unchecked")
public class ViewHolder {
    public static <T extends View> T get(View view, int id) {
        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<View>();
            view.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }
}
