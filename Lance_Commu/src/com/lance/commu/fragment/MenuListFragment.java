package com.lance.commu.fragment;


import com.example.slidingsimplesample.R;

import android.annotation.SuppressLint;

import android.content.Context;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("NewApi")
public class MenuListFragment extends ListFragment {

	ImageView[] icon = new ImageView[7];
	TextView[] title = new TextView[7];
	public MenuListFragment() {
	}      
		
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_list, null);
	}

	// actionbarsherlock drawable - hpdi에 이미지
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		SampleAdapter adapter = new SampleAdapter(getActivity());  

		adapter.add(new SampleItem("Commu", R.drawable.phonebook_lo));
		adapter.add(new SampleItem("친구목록", R.drawable.abs__ic_people_y));
		adapter.add(new SampleItem("가이드", R.drawable.abs__ic_help));
		adapter.add(new SampleItem("설정", R.drawable.abs__ic_system));
		adapter.add(new SampleItem("개발자정보", R.drawable.informationimage));
		adapter.add(new SampleItem("라이센스", R.drawable.opensourceimage));
		adapter.add(new SampleItem("로그아웃", R.drawable.abs__ic_logout));
		
		setListAdapter(adapter); 
	}

	private class SampleItem {
		public String tag;
		public int iconRes;

		public SampleItem(String tag, int iconRes) {
			this.tag = tag;
			this.iconRes = iconRes;
		}
	}

	public class SampleAdapter extends ArrayAdapter<SampleItem> {

		public SampleAdapter(Context context){
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.row, null);
			}
			icon[position] = (ImageView) convertView.findViewById(R.id.row_icon);  
			icon[position].setImageResource(getItem(position).iconRes);
			
			title[position] = (TextView) convertView.findViewById(R.id.row_title);
			title[position].setText(getItem(position).tag);

			//프래그먼트의 첫번째부분 view설정  
			if (position == 0) {
				icon[position].setBackgroundColor(Color.rgb(25, 25, 25));
				title[position].setBackgroundColor(Color.rgb(25, 25, 25));
				title[position].setTextColor(Color.WHITE);
			}
			
			else if(position == 1){ //친구목록이 제일 처음에 뜨니까 아이템 색깔을 처음부터 회색으로 고정 
				icon[position].setBackgroundColor(Color.rgb(225, 225, 225));
				title[position].setBackgroundColor(Color.rgb(225, 225, 225));
			}
			
			
			else{
				icon[position].setBackgroundColor(Color.WHITE);
				title[position].setBackgroundColor(Color.WHITE);
			}
			
			return convertView;
		}
	}

	
	//메뉴클릭하는부분
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		switch (position) {
		case 0:
			//((BaseActivity)getActivity()).fragmentReplace(0);
			// 클릭을 막음.
			break;
		case 1: //친구목록
			//기존것들중에 회색있을수 있으니까 다 흰색으로 초기화하고 
			for(int i=2;i<=6;i++){
				icon[i].setBackgroundColor(Color.WHITE);
				title[i].setBackgroundColor(Color.WHITE);
			}
			//해당거 회색처리
			icon[1].setBackgroundColor(Color.rgb(225, 225, 225));
			title[1].setBackgroundColor(Color.rgb(225, 225, 225));
			((BaseActivity) getActivity()).fragmentReplace(1);
			
			break;
		case 2: //가이드
			
			icon[1].setBackgroundColor(Color.WHITE);
			title[1].setBackgroundColor(Color.WHITE);
			
			icon[2].setBackgroundColor(Color.rgb(225, 225, 225));
			title[2].setBackgroundColor(Color.rgb(225, 225, 225));
			
			for(int i=3;i<=6;i++){
				icon[i].setBackgroundColor(Color.WHITE);
				title[i].setBackgroundColor(Color.WHITE);
			}
			((BaseActivity) getActivity()).fragmentReplace(2);
			break;
		case 3: //설정
			
			icon[1].setBackgroundColor(Color.WHITE);
			title[1].setBackgroundColor(Color.WHITE);
			icon[2].setBackgroundColor(Color.WHITE);
			title[2].setBackgroundColor(Color.WHITE);
			for(int i=4;i<=6;i++){
				icon[i].setBackgroundColor(Color.WHITE);
				title[i].setBackgroundColor(Color.WHITE);
			}
			
			icon[3].setBackgroundColor(Color.rgb(225, 225, 225));
			title[3].setBackgroundColor(Color.rgb(225, 225, 225));
			((BaseActivity) getActivity()).fragmentReplace(3);
			break;
		case 4: //개발자정보
			
			for(int i=1;i<=3;i++){
				icon[i].setBackgroundColor(Color.WHITE);
				title[i].setBackgroundColor(Color.WHITE);
			}
			icon[5].setBackgroundColor(Color.WHITE);
			title[5].setBackgroundColor(Color.WHITE);
			icon[6].setBackgroundColor(Color.WHITE);
			title[6].setBackgroundColor(Color.WHITE);
			
			icon[4].setBackgroundColor(Color.rgb(225, 225, 225));
			title[4].setBackgroundColor(Color.rgb(225, 225, 225));
			((BaseActivity) getActivity()).fragmentReplace(4);
			break;	
		case 5: //라이센스
			
			for(int i=1;i<=4;i++){
				icon[i].setBackgroundColor(Color.WHITE);
				title[i].setBackgroundColor(Color.WHITE);
			}
			icon[6].setBackgroundColor(Color.WHITE);
			title[6].setBackgroundColor(Color.WHITE);
			
			icon[5].setBackgroundColor(Color.rgb(225, 225, 225));
			title[5].setBackgroundColor(Color.rgb(225, 225, 225));
			((BaseActivity) getActivity()).fragmentReplace(5);
			break;
		case 6: //로그아웃
			
			for(int i=1;i<=5;i++){
				icon[i].setBackgroundColor(Color.WHITE);
				title[i].setBackgroundColor(Color.WHITE);
			}
			
			icon[6].setBackgroundColor(Color.rgb(225, 225, 225));
			title[6].setBackgroundColor(Color.rgb(225, 225, 225));
			((BaseActivity) getActivity()).fragmentReplace(6);
			break;
		}
		super.onListItemClick(l, v, position, id);
	}
}