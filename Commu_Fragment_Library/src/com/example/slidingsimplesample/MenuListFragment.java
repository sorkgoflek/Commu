package com.example.slidingsimplesample;



import android.content.Context;
import android.content.Intent;
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

public class MenuListFragment extends ListFragment {

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
		adapter.add(new SampleItem("���̵�", R.drawable.abs__ic_help));
		adapter.add(new SampleItem("ģ�����", R.drawable.abs__ic_people_y));
		adapter.add(new SampleItem("����", R.drawable.abs__ic_system));
		adapter.add(new SampleItem("���̼���", R.drawable.abs__ic_android));
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

		public SampleAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.row, null);
			}
			ImageView icon = (ImageView) convertView
					.findViewById(R.id.row_icon);
			icon.setImageResource(getItem(position).iconRes);
			TextView title = (TextView) convertView
					.findViewById(R.id.row_title);
			title.setText(getItem(position).tag);

			if (position == 0) {
				icon.setBackgroundColor(Color.YELLOW);
				title.setBackgroundColor(Color.YELLOW);
				
			}

			return convertView;
		}
	}

	
//�޴�Ŭ���ϴºκ�
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// 2���߰�
		switch (position) {
		case 0:
			//((BaseActivity)getActivity()).fragmentReplace(0);
			// commu Ŭ���� ����.
			break;
		case 1:
			((BaseActivity) getActivity()).fragmentReplace(1);
			break;

		case 2:
			((BaseActivity) getActivity()).fragmentReplace(2);
			break;
		case 3:
			((BaseActivity) getActivity()).fragmentReplace(3);
			break;
		case 4:
			((BaseActivity) getActivity()).fragmentReplace(4);
			break;
		case 5:
			((BaseActivity) getActivity()).fragmentReplace(5);
			break;
		}
		super.onListItemClick(l, v, position, id);
	}
}