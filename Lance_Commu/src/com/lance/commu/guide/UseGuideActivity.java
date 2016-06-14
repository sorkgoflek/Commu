package com.lance.commu.guide;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.lance.commu.extra.ViewPagerAdapter;
import com.lance.commu.intro.R;

public class UseGuideActivity extends Activity {
	private int imageArra[] = { R.drawable.map_guide, R.drawable.problem_guide,R.drawable.game_guide};//��밡�̵� ȭ�� ä���� �̹�������
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.useguide);
		
		//ȭ�� ����
		ViewPagerAdapter adapter = new ViewPagerAdapter(this, imageArra);
		ViewPager myPager = (ViewPager) findViewById(R.id.myuseguidepager);
		myPager.setAdapter(adapter);
		myPager.setCurrentItem(0);
	}
}
