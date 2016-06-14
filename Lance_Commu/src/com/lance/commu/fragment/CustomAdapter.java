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
	//������ �⺻����
	public CustomAdapter(Activity activity, Context context,int textViewResourceId, ArrayList<String> name, ArrayList<String> phone_number){
		super(context,textViewResourceId,name);
		System.out.println("Ŀ���� ����� ������ ����");
		this.activity = activity;
		this.context = context;
		this.name = name;
		this.phone_number = phone_number;
		this.inflater = LayoutInflater.from(context);
		friend_image = context.getSharedPreferences("uri", Activity.MODE_PRIVATE);	
		
		//�ʱ�ȭ
		if(friend_icon.size() != 0){
			friend_icon.removeAll(friend_icon);
		}
		System.out.println("Ŀ���� �����  ������ ��");
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
		//System.out.println("getView ����");
		View v = convertView;
		v=inflater.inflate(R.layout.menu_item ,null);	
		//������ �͵鵵 �⺻������ ��� 
		gotoCall = (Button)v.findViewById(R.id.phone_note);
		String s = name.get(position);
		textView = (TextView)v.findViewById(R.id.label);
		textView.setText(s);
		defaultImage = (ImageView)v.findViewById(R.id.friend_icon);
		friend_icon.add(position,(ImageView)v.findViewById(R.id.friend_icon));
		//�⺻���ΰ� �� ���// 
		//////////////////////
		/*System.out.println("/////////////////////////");
		System.out.println(friend_icon.size());	
		System.out.println("/////////////////////////");*/
		
		if(receiveText(phone_number.get(position)+"uri").equals("no") != true){ //ģ�������ܿ� �ش��ϴ� uri�� �����Ѵٸ� 
			//�ش� uri�� �°� �̹��� ���� 
			imageUri = Uri.parse(receiveText(phone_number.get(position)+"uri"));
			friend_icon.get(position).setImageURI(imageUri);
		}
		
		//ģ�������� ��Ŭ��
		friend_icon.get(position).setOnLongClickListener(new OnLongClickListener(){
			@Override
			public boolean onLongClick(final View v) {
				System.out.println("���� ��Ŭ��"+position);
				
				
				if(receiveText(phone_number.get(position)+"uri").equals("no") != true){ //ģ�������ܿ� �ش��ϴ� uri�� �����Ѵٸ�
					//���̾�α׿� ����� ��� �߰� 
					System.out.println("�����ִ� ���̾�α�");  
					
					AlertDialog.Builder alert_confirm = new AlertDialog.Builder(context);
			        alert_confirm.setTitle("ģ����� ����"); 
			        alert_confirm.setMessage("������ �����Ͻðھ��?").setIcon(R.drawable.friendigalleryicon2).setCancelable(false).setPositiveButton("������ �̵�",
			        new DialogInterface.OnClickListener() {
						@Override
			            public void onClick(DialogInterface dialog, int which) {
			              		
							Intent intent = new Intent(Intent.ACTION_PICK);
							intent.setType("image/*");
							
							positionMyHope = position;
							uriPhoneNumber = phone_number.get(position);
							
							System.out.println("startActiviyForResult ����");
							((Activity)context).startActivityForResult(intent, 0);
							System.out.println("startActiviyForResult ����");
							
			            }
			        }).setNeutralButton("�����",
				        new DialogInterface.OnClickListener() {
							@Override
				            public void onClick(DialogInterface dialog, int which) {
				              		
								System.out.println("����");
								//�ش� uri �����ϰ� 
								//friend_image = context.getSharedPreferences("uri", Activity.MODE_PRIVATE);
								SharedPreferences.Editor editor = friend_image.edit();
								editor.remove(phone_number.get(position)+"uri");
								editor.commit();
								//ģ�������� �̹����� �⺻�̹����� �ٽ� ���� 
								Bitmap image =BitmapFactory.decodeResource(context.getResources(), R.drawable.friend);
								friend_icon.get(position).setImageBitmap(image);
				            }
				        }).setNegativeButton("���",
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
				
				else{//�⺻ ���̾�α� ���� 
					System.out.println("�������� ���̾�α�");
					
					 AlertDialog.Builder alert_confirm = new AlertDialog.Builder(context);
				        alert_confirm.setTitle("ģ����� ����"); 
				        alert_confirm.setMessage("������ �����Ͻðھ��?").setIcon(R.drawable.friendigalleryicon2).setCancelable(false).setPositiveButton("������ �̵�",
				        new DialogInterface.OnClickListener() {
							@Override
				            public void onClick(DialogInterface dialog, int which) {
				              		
								Intent intent = new Intent(Intent.ACTION_PICK);
								intent.setType("image/*");
								
								positionMyHope = position;
								uriPhoneNumber = phone_number.get(position);
								
								System.out.println("startActiviyForResult ����");
								((Activity)context).startActivityForResult(intent, 0);
								System.out.println("startActiviyForResult ����");
								
				            }
				        }).setNegativeButton("���",
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
		
		gotoCall.setOnClickListener(new OnClickListener(){//��ȭ������
			public void onClick(View v) {
	
				getContext().startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phone_number.get(position))));
			}
		});
		return v;
	}
	
	public String receiveText(String input){
		String initText="no";
		//System.out.println("////////////////");
		//System.out.println(friend_image.getAll()); //�����ִ°� ��� �����ؼ� 
		
		if(friend_image.contains(input)){
			initText=friend_image.getString(input, "");
		}
		
		return initText;
	}
	
	
}
