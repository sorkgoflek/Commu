package com.lance.commu.guide;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.lance.commu.extra.ViewPagerAdapter;
import com.lance.commu.intro.R;

public class UniqueBackgroudGuideActivity extends Activity {
	private int imageArra[] = { R.drawable.specialbackground_guide};//우리만의 배경가이드 화면 채워질 이미지파일
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.uniquebackgroundguide);
		
		//화면 설정
		ViewPagerAdapter adapter = new ViewPagerAdapter(this, imageArra);
		ViewPager myPager = (ViewPager) findViewById(R.id.myuniquebackgroundguidepager);
		myPager.setAdapter(adapter);
		myPager.setCurrentItem(0);
	}


}
