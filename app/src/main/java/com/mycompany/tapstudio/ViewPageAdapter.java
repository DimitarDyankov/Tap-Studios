/**************************************************************
 | Author: Dimitar Dyankov  								   | 
 | View page adapter to handle tabls and views on the editor   |
 **************************************************************/
package com.mycompany.tapstudio;

import java.util.*;
import android.support.v4.app.*;
import android.view.*;
import android.support.v4.view.*;
import android.util.*;

class ViewPagerAdapter extends FragmentStatePagerAdapter
{
	private ArrayList<PagerItem> mPagerItems = new ArrayList();
	private FragmentManager Fm;
	
    public ViewPagerAdapter(FragmentManager fm) {
		super(fm);
		Fm=fm;
    }

    public ViewPagerAdapter(FragmentManager fm, ArrayList<PagerItem> al) {
		super(fm);
		Fm=fm;
		mPagerItems = al;
    }
    public void addFragment(Fragment fragment, String title) {
		mPagerItems.add(new PagerItem(title, fragment));
    }

	public void removeFragment(int pos){
			mPagerItems.remove(pos);
	}

    @Override
    public CharSequence getPageTitle(int position) {
        return mPagerItems.get(position).getTitle();
    }
	@Override
    public int getItemPosition(Object object) {
        // refresh all fragments when data set changed
        return POSITION_NONE;
    }

	@Override
	public Fragment getItem(int p1)
	{
		return mPagerItems.get(p1).getFragment();
	}
	
    @Override
    public int getCount() {
        return mPagerItems.size();
    }
}
