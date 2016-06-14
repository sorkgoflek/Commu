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
		System.out.println("여기는 FragmentMainActivity");
     
	} 

	@SuppressLint("SdCardPath")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data != null){
			super.onActivityResult(requestCode, resultCode, data);
			
				System.out.println("클릭 포지션 : " + CustomAdapter.positionMyHope);
				System.out.println("클릭 번호 : " + CustomAdapter.uriPhoneNumber);
				
				//String test = data.getDataString();
				//System.out.println("갤러리 데이터String값 : "+test);
				if(resultCode != RESULT_OK){
					return;
				}
				
				switch(requestCode){
				
					case 0 : 
					{
						System.out.println("이미지 받아옴");
						imgBitmap = null;
						imageUri = data.getData();//이미지 받아옴
						
						Intent intent = new Intent("com.android.camera.action.CROP");
				        intent.setDataAndType(imageUri, "image/*");
						
				        intent.putExtra("outputX", 90);
				        intent.putExtra("outputY", 90);
				        intent.putExtra("aspectX", 1);
				        intent.putExtra("aspectY", 1);
				        intent.putExtra("scale", true);
				        intent.putExtra("return-data", true);
				        startActivityForResult(intent, 1); //사진크기조정쪽으로 이동
				        System.out.println("사진크기 조정으로 이동");
				        break;
					}
					
					case 1 :
					{
							// 크롭이 된 이후의 이미지를 넘겨 받습니다.
					        // 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에
					        // 임시 파일을 삭제합니다.
							System.out.println("여기는 사진크기조정부분");
							final Bundle extras = data.getExtras();
							
							if(extras != null)
								imgBitmap = extras.getParcelable("data");
					        
							/*//이미지 비트맵으로 변환
							try {
								System.out.println("비트맵 작업하기 직전");
								imgBitmap = Media.getBitmap(getContentResolver(), imageUri);
							} catch (FileNotFoundException e) {
								e.printStackTrace();
								System.out.println("파일 못찾았대");
							} catch (IOException e) {
								e.printStackTrace();
								System.out.println("io문제");
							}*/
							
							//해당 position 아이콘에 이미지파일을 넣는것까지는 맞다
							CustomAdapter.friend_icon.get(CustomAdapter.positionMyHope).setImageBitmap(imgBitmap);
							//View 영역에 공백이 있으면 채워서 보여줌(비율유지)
							CustomAdapter.friend_icon.get(CustomAdapter.positionMyHope).setScaleType(ImageView.ScaleType.CENTER_CROP);
							
						//이미지uri 저장하는데 
						//해당 uri key값과 value 값에 내가 선택한 사람의 핸드폰번호를 넣는다 
						//그래서 그 핸드폰 번호를 통해 연결되게 하여 새로운 사람이 삽입 삭제 되는 경우에도 제위치를 찾을 수 있다 
						try{
							//이미지 포맷
							System.out.println("이미지 포맷시작 직전");
							imgBitmap.compress(CompressFormat.JPEG, 100, openFileOutput("Bitmap"+CustomAdapter.uriPhoneNumber+".jpg", MODE_PRIVATE));
							//uri에 position 넣어서 각각 저장되게 설정 
							saveText(CustomAdapter.uriPhoneNumber+"uri","/data/data/com.lance.commu.intro/files/Bitmap"+CustomAdapter.uriPhoneNumber+".jpg");//save
										
						}catch(Exception e){}
								
						// 임시 파일 삭제
				        File f = new File(imageUri.getPath());
				        if(f.exists())
				          f.delete();
				        
				    	System.out.println("최종작업완료");
				    	break;
					}
				}//switch end
	
				}
			}

	public void saveText(String key,String text) {
    	System.out.println("saveText 시작");

    	System.out.println("key : "+key);
    	System.out.println("text : "+text);
    	Editor editor = friend_image.edit();
        editor.putString(key, text);
        editor.commit();
        System.out.println("saveText 끝");
    }
	
} //class end