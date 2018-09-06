/**
 *日期：2017年7月26日下午1:30:01
pillow
TODO
author：shaozq
 */
package com.pillow.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * @author shaozq
 *2017年7月26日下午1:30:01
 */
public class MyPagerAdapter extends PagerAdapter {

	/* (non-Javadoc)
	 * @see android.support.v4.view.PagerAdapter#getCount()
	author:shaozq
	2017年7月26日下午1:30:01
	
	 */
	List<ImageView> list;

    public MyPagerAdapter(List<ImageView> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView=list.get(position);
        container.addView(imageView);
        return list.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(list.get(position));
    }

}
