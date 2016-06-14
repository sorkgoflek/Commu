package com.lance.commu.fragment;
 

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.slidingsimplesample.R;
import com.lance.commu.guide.BasicGuideActivity;
import com.lance.commu.guide.UniqueBackgroudGuideActivity;
import com.lance.commu.guide.UseGuideActivity;

public class Fragment_Guide extends Fragment {
	ArrayAdapter<String> adapt; //ListView
	ListView menuList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_guide, container, false);

        return v;
    }

    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		String[] lists = {"기본사용법","이렇게 써봐","우리만의 배경을 공유해"};
        //리스트뷰 생성해서 그안에 집어넣어 
       adapt = new ArrayAdapter<String>(getActivity(), R.layout.menu_item_guide, R.id.label2, lists);
       menuList = (ListView)getActivity().findViewById(R.id.listView_guide);
      
       menuList.setOnItemClickListener(new MyOnItemClickListener());    
 	   menuList.setAdapter(adapt);
 	   
 	   MyOnItemClickListener listViewExampleClickListener = new MyOnItemClickListener();
 	   menuList.setOnItemClickListener(listViewExampleClickListener);
	}

	private class MyOnItemClickListener implements AdapterView.OnItemClickListener{
        @Override
		public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id)
        {
        	if(position == 0){
        		startActivity(new Intent(getActivity(),BasicGuideActivity.class));
        		/*Toast toast = Toast.makeText(getActivity() , "기본사용법", Toast.LENGTH_SHORT);
				toast.show();*/			
        	}
        	else if(position == 1){
        		startActivity(new Intent(getActivity(),UseGuideActivity.class));
        		/*Toast toast = Toast.makeText(getActivity() , "이렇게 써봐", Toast.LENGTH_SHORT);
				toast.show();*/
        	}
        	else{
        		startActivity(new Intent(getActivity(),UniqueBackgroudGuideActivity.class));
        		/*Toast toast = Toast.makeText(getActivity() , "우리만의 배경을 공유해", Toast.LENGTH_SHORT);
				toast.show();*/
        	}

        }
    }
    
}