package com.lance.commu.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.slidingsimplesample.R;


public class CustomAdapter extends ArrayAdapter<String>{

	private LayoutInflater inflater=null;
	private ArrayList<String> name =new ArrayList<String>();
	private ArrayList<String> phone_number =new ArrayList<String>();
	public static ArrayList<ImageView> friend_icon =new ArrayList<ImageView>();
	Uri imageUri;
	Uri imageUri2;
	Context context;
	TextView textView;
	Button gotoCall;
	public static int positionMyHope = 0;
	public static String uriPhoneNumber;
	SharedPreferences friend_image; 
	Activity activity;
	ImageView defaultImage;
	//생성자 기본세팅
	public CustomAdapter(Activity activity, Context context,int textViewResourceId, ArrayList<String> name, ArrayList<String> phone_number){
		super(context,textViewResourceId,name);
		System.out.println("커스텀 어댑터 생성자 시작");
		this.activity = activity;
		this.context = context;
		this.name = name;
		this.phone_number = phone_number;
		this.inflater = LayoutInflater.from(context);
		friend_image = context.getSharedPreferences("uri", Activity.MODE_PRIVATE);	
		
		//초기화
		if(friend_icon.size() != 0){
			friend_icon.removeAll(friend_icon);
		}
		System.out.println("커스텀 어댑터  생성자 끝");
	}
	
	@Override
	public int getCount() {
		return super.getCount();
	}

	@Override
	public String getItem(int position) {
		return super.getItem(position);
	}

	@Override
	public long getItemId(int position) {
		return super.getItemId(position);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		//System.out.println("getView 시작");
		View v = convertView;
		v=inflater.inflate(R.layout.menu_item ,null);	
		//나머지 것들도 기본적으로 깔고 
		gotoCall = (Button)v.findViewById(R.id.phone_note);
		String s = name.get(position);
		textView = (TextView)v.findViewById(R.id.label);
		textView.setText(s);
		defaultImage = (ImageView)v.findViewById(R.id.friend_icon);
		friend_icon.add(position,(ImageView)v.findViewById(R.id.friend_icon));
		//기본적인거 다 깔고// 
		//////////////////////
		/*System.out.println("/////////////////////////");
		System.out.println(friend_icon.size());	
		System.out.println("/////////////////////////");*/
		
		if(receiveText(phone_number.get(position)+"uri").equals("no") != true){ //친구아이콘에 해당하는 uri가 존재한다면 
			//해당 uri에 맞게 이미지 세팅 
			imageUri = Uri.parse(receiveText(phone_number.get(position)+"uri"));
			friend_icon.get(position).setImageURI(imageUri);
		}
		
		//친구아이콘 롱클릭
		friend_icon.get(position).setOnLongClickListener(new OnLongClickListener(){
			@Override
			public boolean onLongClick(final View v) {
				System.out.println("사진 롱클릭"+position);
				
				
				if(receiveText(phone_number.get(position)+"uri").equals("no") != true){ //친구아이콘에 해당하는 uri가 존재한다면
					//다이얼로그에 지우기 기능 추가 
					System.out.println("사진있는 다이얼로그");  
					
					AlertDialog.Builder alert_confirm = new AlertDialog.Builder(context);
			        alert_confirm.setTitle("친구목록 사진"); 
			        alert_confirm.setMessage("사진을 선택하시겠어요?").setIcon(R.drawable.friendigalleryicon2).setCancelable(false).setPositiveButton("갤러리 이동",
			        new DialogInterface.OnClickListener() {
						@Override
			            public void onClick(DialogInterface dialog, int which) {
			              		
							Intent intent = new Intent(Intent.ACTION_PICK);
							intent.setType("image/*");
							
							positionMyHope = position;
							uriPhoneNumber = phone_number.get(position);
							
							System.out.println("startActiviyForResult 직전");
							((Activity)context).startActivityForResult(intent, 0);
							System.out.println("startActiviyForResult 직후");
							
			            }
			        }).setNeutralButton("지우기",
				        new DialogInterface.OnClickListener() {
							@Override
				            public void onClick(DialogInterface dialog, int which) {
				              		
								System.out.println("지워");
								//해당 uri 삭제하고 
								//friend_image = context.getSharedPreferences("uri", Activity.MODE_PRIVATE);
								SharedPreferences.Editor editor = friend_image.edit();
								editor.remove(phone_number.get(position)+"uri");
								editor.commit();
								//친구아이콘 이미지를 기본이미지로 다시 변경 
								Bitmap image =BitmapFactory.decodeResource(context.getResources(), R.drawable.friend);
								friend_icon.get(position).setImageBitmap(image);
				            }
				        }).setNegativeButton("취소",
			        new DialogInterface.OnClickListener() {
			            @Override
			            public void onClick(DialogInterface dialog, int which) {
			                // 'No'
			            	dialog.dismiss();
			            return;
			            }
			        });
			        AlertDialog alert = alert_confirm.create();
			        alert.show();
				}
				
				else{//기본 다이얼로그 띄우기 
					System.out.println("사진없는 다이얼로그");
					
					 AlertDialog.Builder alert_confirm = new AlertDialog.Builder(context);
				        alert_confirm.setTitle("친구목록 사진"); 
				        alert_confirm.setMessage("사진을 선택하시겠어요?").setIcon(R.drawable.friendigalleryicon2).setCancelable(false).setPositiveButton("갤러리 이동",
				        new DialogInterface.OnClickListener() {
							@Override
				            public void onClick(DialogInterface dialog, int which) {
				              		
								Intent intent = new Intent(Intent.ACTION_PICK);
								intent.setType("image/*");
								
								positionMyHope = position;
								uriPhoneNumber = phone_number.get(position);
								
								System.out.println("startActiviyForResult 직전");
								((Activity)context).startActivityForResult(intent, 0);
								System.out.println("startActiviyForResult 직후");
								
				            }
				        }).setNegativeButton("취소",
				        new DialogInterface.OnClickListener() {
				            @Override
				            public void onClick(DialogInterface dialog, int which) {
				                // 'No'
				            	dialog.dismiss();
				            return;
				            }
				        });
				        AlertDialog alert = alert_confirm.create();
				        alert.show();
				}// else end
				
				return true;
			}
		});
		
		gotoCall.setOnClickListener(new OnClickListener(){//전화아이콘
			public void onClick(View v) {
	
				getContext().startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phone_number.get(position))));
			}
		});
		return v;
	}
	
	public String receiveText(String input){
		String initText="no";
		//System.out.println("////////////////");
		//System.out.println(friend_image.getAll()); //갖고있는거 모두 추출해서 
		
		if(friend_image.contains(input)){
			initText=friend_image.getString(input, "");
		}
		
		return initText;
	}
	
	
}
