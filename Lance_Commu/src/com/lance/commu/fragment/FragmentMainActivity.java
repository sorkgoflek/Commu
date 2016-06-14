package com.lance.commu.fragment;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.widget.ImageView;

import com.example.slidingsimplesample.R;

public class FragmentMainActivity extends BaseActivity {
	Uri imageUri;
	SharedPreferences friend_image;
	public static int uriKey;
	Bitmap imgBitmap;
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.phonebook_lo);
		actionBar.setTitle("Commu");
		setContentView(R.layout.fragment_main);
		fragmentReplace(0);
		
		friend_image = getSharedPreferences("uri", MODE_PRIVATE);
		System.out.println("����� FragmentMainActivity");
     
	} 

	@SuppressLint("SdCardPath")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data != null){
			super.onActivityResult(requestCode, resultCode, data);
			
				System.out.println("Ŭ�� ������ : " + CustomAdapter.positionMyHope);
				System.out.println("Ŭ�� ��ȣ : " + CustomAdapter.uriPhoneNumber);
				
				//String test = data.getDataString();
				//System.out.println("������ ������String�� : "+test);
				if(resultCode != RESULT_OK){
					return;
				}
				
				switch(requestCode){
				
					case 0 : 
					{
						System.out.println("�̹��� �޾ƿ�");
						imgBitmap = null;
						imageUri = data.getData();//�̹��� �޾ƿ�
						
						Intent intent = new Intent("com.android.camera.action.CROP");
				        intent.setDataAndType(imageUri, "image/*");
						
				        intent.putExtra("outputX", 90);
				        intent.putExtra("outputY", 90);
				        intent.putExtra("aspectX", 1);
				        intent.putExtra("aspectY", 1);
				        intent.putExtra("scale", true);
				        intent.putExtra("return-data", true);
				        startActivityForResult(intent, 1); //����ũ������������ �̵�
				        System.out.println("����ũ�� �������� �̵�");
				        break;
					}
					
					case 1 :
					{
							// ũ���� �� ������ �̹����� �Ѱ� �޽��ϴ�.
					        // �̹����信 �̹����� �����شٰų� �ΰ����� �۾� ���Ŀ�
					        // �ӽ� ������ �����մϴ�.
							System.out.println("����� ����ũ�������κ�");
							final Bundle extras = data.getExtras();
							
							if(extras != null)
								imgBitmap = extras.getParcelable("data");
					        
							/*//�̹��� ��Ʈ������ ��ȯ
							try {
								System.out.println("��Ʈ�� �۾��ϱ� ����");
								imgBitmap = Media.getBitmap(getContentResolver(), imageUri);
							} catch (FileNotFoundException e) {
								e.printStackTrace();
								System.out.println("���� ��ã�Ҵ�");
							} catch (IOException e) {
								e.printStackTrace();
								System.out.println("io����");
							}*/
							
							//�ش� position �����ܿ� �̹��������� �ִ°ͱ����� �´�
							CustomAdapter.friend_icon.get(CustomAdapter.positionMyHope).setImageBitmap(imgBitmap);
							//View ������ ������ ������ ä���� ������(��������)
							CustomAdapter.friend_icon.get(CustomAdapter.positionMyHope).setScaleType(ImageView.ScaleType.CENTER_CROP);
							
						//�̹���uri �����ϴµ� 
						//�ش� uri key���� value ���� ���� ������ ����� �ڵ�����ȣ�� �ִ´� 
						//�׷��� �� �ڵ��� ��ȣ�� ���� ����ǰ� �Ͽ� ���ο� ����� ���� ���� �Ǵ� ��쿡�� ����ġ�� ã�� �� �ִ� 
						try{
							//�̹��� ����
							System.out.println("�̹��� ���˽��� ����");
							imgBitmap.compress(CompressFormat.JPEG, 100, openFileOutput("Bitmap"+CustomAdapter.uriPhoneNumber+".jpg", MODE_PRIVATE));
							//uri�� position �־ ���� ����ǰ� ���� 
							saveText(CustomAdapter.uriPhoneNumber+"uri","/data/data/com.lance.commu.intro/files/Bitmap"+CustomAdapter.uriPhoneNumber+".jpg");//save
										
						}catch(Exception e){}
								
						// �ӽ� ���� ����
				        File f = new File(imageUri.getPath());
				        if(f.exists())
				          f.delete();
				        
				    	System.out.println("�����۾��Ϸ�");
				    	break;
					}
				}//switch end
	
				}
			}

	public void saveText(String key,String text) {
    	System.out.println("saveText ����");

    	System.out.println("key : "+key);
    	System.out.println("text : "+text);
    	Editor editor = friend_image.edit();
        editor.putString(key, text);
        editor.commit();
        System.out.println("saveText ��");
    }
	
} //class end