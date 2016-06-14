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

	// actionbarsherlock drawable - hpdi�� �̹���
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		SampleAdapter adapter = new SampleAdapter(getActivity());  

		adapter.add(new SampleItem("Commu", R.drawable.phonebook_lo));
		adapter.add(new SampleItem("ģ�����", R.drawable.abs__ic_people_y));
		adapter.add(new SampleItem("���̵�", R.drawable.abs__ic_help));
		adapter.add(new SampleItem("����", R.drawable.abs__ic_system));
		adapter.add(new SampleItem("����������", R.drawable.informationimage));
		adapter.add(new SampleItem("���̼���", R.drawable.opensourceimage));
		adapter.add(new SampleItem("�α׾ƿ�", R.drawable.abs__ic_logout));
		
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

			//�����׸�Ʈ�� ù��°�κ� view����  
			if (position == 0) {
				icon[position].setBackgroundColor(Color.rgb(25, 25, 25));
				title[position].setBackgroundColor(Color.rgb(25, 25, 25));
				title[position].setTextColor(Color.WHITE);
			}
			
			else if(position == 1){ //ģ������� ���� ó���� �ߴϱ� ������ ������ ó������ ȸ������ ���� 
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

	
	//�޴�Ŭ���ϴºκ�
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		switch (position) {
		case 0:
			//((BaseActivity)getActivity()).fragmentReplace(0);
			// Ŭ���� ����.
			break;
		case 1: //ģ�����
			//�����͵��߿� ȸ�������� �����ϱ� �� ������� �ʱ�ȭ�ϰ� 
			for(int i=2;i<=6;i++){
				icon[i].setBackgroundColor(Color.WHITE);
				title[i].setBackgroundColor(Color.WHITE);
			}
			//�ش�� ȸ��ó��
			icon[1].setBackgroundColor(Color.rgb(225, 225, 225));
			title[1].setBackgroundColor(Color.rgb(225, 225, 225));
			((BaseActivity) getActivity()).fragmentReplace(1);
			
			break;
		case 2: //���̵�
			
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
		case 3: //����
			
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
		case 4: //����������
			
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
		case 5: //���̼���
			
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
		case 6: //�α׾ƿ�
			
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