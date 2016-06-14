package com.lance.commu.visualtalk;

import com.lance.commu.intro.R;
import com.lance.commu.network.NetworkManager;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;


	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public class MyView extends View {

		NetworkManager nm;
		
		static Canvas mCanvas;
		static Bitmap mBitmap;
		private Path mPath;
		private Paint mBitmapPaint;

		// ��� ��Ʈ��
		static Bitmap back_img1;
		static Bitmap back_img2;
		static Bitmap back_img3;
		static Bitmap back_img4;
		static Bitmap back_img5;
		static Bitmap back_img6;
		
		static Drawable d;
		
		public MyView(Context context, AttributeSet attrs) {
			super(context,attrs);

			//back = BitmapFactory.decodeResource(getResources(), R.drawable.back);
			mBitmap = Bitmap.createBitmap(VTActivity.mScreenSize.x, VTActivity.mScreenSize.y, Bitmap.Config.ARGB_8888);//���⼭ ��������
			mCanvas = new Canvas(mBitmap);
			mPath = new Path();
			mBitmapPaint = new Paint(Paint.DITHER_FLAG);
			
			back_img1 = BitmapFactory.decodeResource(getResources(), R.drawable.noteboook2);
			back_img2 = BitmapFactory.decodeResource(getResources(), R.drawable.post_it);
			back_img3 = BitmapFactory.decodeResource(getResources(), R.drawable.love);
			back_img4 = BitmapFactory.decodeResource(getResources(), R.drawable.board);
			back_img5 = BitmapFactory.decodeResource(getResources(), R.drawable.black5);
			back_img6 = BitmapFactory.decodeResource(getResources(), R.drawable.white);
			
			back_img1 = imgResize(back_img1);
			back_img2 = imgResize(back_img2);
			back_img3 = imgResize(back_img3);
			back_img4 = imgResize(back_img4);
			back_img5 = imgResize(back_img5);
			back_img6 = imgResize(back_img6);
			
			nm=NetworkManager.getInstance();
		}

		public static Bitmap imgResize(Bitmap bitmap)
		{
			//����
			double x = VTActivity.mScreenSize.x;
			double y = VTActivity.mScreenSize.y-VTActivity.mScreenSize.y*0.15; // �ٲ� �̹��� ������
			
			Bitmap output = Bitmap.createBitmap((int)x, (int)y, Config.ARGB_8888);

			Canvas canvas = new Canvas(output);
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			
			Rect src;
			if(w>h){//���� ���� ũ�⿡ ���� �̹����� ���� ����
				bitmap = imgRotate(bitmap);
				src = new Rect(0, 0, h, w);
			}
			else{
				src = new Rect(0, 0, w, h);
			}
			
			Rect dst = new Rect(0, 0,(int) x, (int)y);// �� ũ��� �����
			canvas.drawBitmap(bitmap, src, dst, null);
			
			return output;
		}
		
		public static Bitmap imgRotate(Bitmap bitmap)
		{
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			
			Matrix matrix= new Matrix();
			matrix.postRotate(90);
			
			Bitmap output = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
			
			return output;
			
		}
		
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);
		}

		protected void onDraw(Canvas canvas) {
			canvas.drawColor(Color.TRANSPARENT); // ��� ����
			
			switch(BackgroundDialog.ImgNumber){
				case 1:
					canvas.drawBitmap(back_img1, 0, 0, null);
					break;
				case 2:
					canvas.drawBitmap(back_img2, 0, 0, null);
					break;
				case 3:
					canvas.drawBitmap(back_img3, 0, 0, null);
					break;
				case 4:
					canvas.drawBitmap(back_img4, 0, 0, null);
					break;
				case 5:
					canvas.drawBitmap(back_img5, 0, 0, null);
					break;
				case 6:
					canvas.drawBitmap(back_img6, 0, 0, null);
					break;
				default:
					break;
			}
			
			if(VTActivity.gal_img == true){
				change_back();
			}

			canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
			canvas.drawPath(mPath, VTActivity.mPaint);
		}

		@SuppressWarnings("deprecation")
		@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
		@SuppressLint("NewApi")
		public static void change_back(){
			BitmapFactory.Options option = new BitmapFactory.Options();
			//��Ʈ�� ����
			//OutOfMemorry�� �߻��ϸ� samplesize�� ��� �������Ѽ� �̹��� �뷮�� ����.
			try{
				option.inDither = true;	// �ε巴���ϱ�.
				option.inSampleSize = 1;
				
				Bitmap _bit = BitmapFactory.decodeFile(VTActivity.path,option); // ���ϰ�ο� �ִ� �̹����� ������.
				int sample = calculateInSampleSize(option,_bit.getWidth(),_bit.getHeight());
				option.inSampleSize = sample;  //������������ ȭ�� ����
				
				_bit = 	imgResize(_bit); // �̹����� ����̽�ũ�⿡ �°� �ٽø���.
				d = new BitmapDrawable(_bit);
				
				if(android.os.Build.VERSION.SDK_INT < 16){
					VTActivity.layout.setBackgroundDrawable(d);
				}
				else{
					VTActivity.layout.setBackground(d);
				}
				
				_bit=null;
			}catch(OutOfMemoryError e){
				try{
					option.inSampleSize = 2;
					Bitmap _bit = BitmapFactory.decodeFile(VTActivity.path,option); // ���ϰ�ο� �ִ� �̹����� ������.
					int sample = calculateInSampleSize(option,_bit.getWidth(),_bit.getHeight());
					option.inSampleSize = sample;  //������������ ȭ�� ����
					
					_bit = 	imgResize(_bit); // �̹����� ����̽�ũ�⿡ �°� �ٽø���.
					d = new BitmapDrawable(_bit);
					
					if(android.os.Build.VERSION.SDK_INT < 16){
						VTActivity.layout.setBackgroundDrawable(d);
					}
					else{
						VTActivity.layout.setBackground(d);
					}
					
					_bit=null;
				}catch(OutOfMemoryError e2){
					try{
						option.inSampleSize = 4;
						Bitmap _bit = BitmapFactory.decodeFile(VTActivity.path,option); // ���ϰ�ο� �ִ� �̹����� ������.
						int sample = calculateInSampleSize(option,_bit.getWidth(),_bit.getHeight());
						option.inSampleSize = sample;  //������������ ȭ�� ����
						
						_bit = 	imgResize(_bit); // �̹����� ����̽�ũ�⿡ �°� �ٽø���.
						d = new BitmapDrawable(_bit);
						
						if(android.os.Build.VERSION.SDK_INT < 16){
							VTActivity.layout.setBackgroundDrawable(d);
						}
						else{
							VTActivity.layout.setBackground(d);
						}
						
						_bit=null;
					}catch(OutOfMemoryError e3){
						try{
							option.inSampleSize = 8;
							Bitmap _bit = BitmapFactory.decodeFile(VTActivity.path,option); // ���ϰ�ο� �ִ� �̹����� ������.
							int sample = calculateInSampleSize(option,_bit.getWidth(),_bit.getHeight());
							option.inSampleSize = sample;  //������������ ȭ�� ����
							
							_bit = 	imgResize(_bit); // �̹����� ����̽�ũ�⿡ �°� �ٽø���.
							d = new BitmapDrawable(_bit);
							
							if(android.os.Build.VERSION.SDK_INT < 16){
								VTActivity.layout.setBackgroundDrawable(d);
							}
							else{
								VTActivity.layout.setBackground(d);
							}
							
							_bit=null;
						}catch(OutOfMemoryError e4){
							e.getMessage();
						}
					}
				}
			}

			Log.i("NetworkWithVT", "option.inSampleSize: " + option.inSampleSize);
			
			VTActivity.gal_img = false;
		}
		
		@SuppressWarnings("deprecation")
		public static boolean change_back(byte[] img){
			if(img==null){
				Log.e("NetworkWithVT", "change_back: img==null");
				return false;
			}
			BitmapFactory.Options option = new BitmapFactory.Options();
			//��Ʈ�� ����
			//OutOfMemorry�� �߻��ϸ� samplesize�� ��� ������Ŵ.
			try{
				option.inDither = true;	// �ε巴���ϱ�.
				option.inSampleSize = 1;
				
				Bitmap _bit = BitmapFactory.decodeByteArray(img, 0, img.length, option); // ���ϰ�ο� �ִ� �̹����� ������.
				
				if(_bit==null){
					Log.e("NetworkWithVT", "change_back: _bit==null");
					return false;
				}
				
				int sample = calculateInSampleSize(option, _bit.getWidth(), _bit.getHeight());
				option.inSampleSize = sample;  //������������ ȭ�� ����
				
				_bit = 	imgResize(_bit); // �̹����� ����̽�ũ�⿡ �°� �ٽø���.
				d = new BitmapDrawable(_bit);
				setBackgroundHandler.sendEmptyMessage(0);
				
				_bit=null;
			}catch(OutOfMemoryError e){
				try{
					option.inSampleSize = 2;
					Bitmap _bit = BitmapFactory.decodeByteArray(img, 0, img.length, option); // ���ϰ�ο� �ִ� �̹����� ������.
					int sample = calculateInSampleSize(option,_bit.getWidth(),_bit.getHeight());
					option.inSampleSize = sample;  //������������ ȭ�� ����
					
					_bit = 	imgResize(_bit); // �̹����� ����̽�ũ�⿡ �°� �ٽø���.
					d = new BitmapDrawable(_bit);
					setBackgroundHandler.sendEmptyMessage(0);
					
					_bit=null;
				}catch(OutOfMemoryError e2){
					try{
						option.inSampleSize = 4;
						Bitmap _bit = BitmapFactory.decodeByteArray(img, 0, img.length, option); // ���ϰ�ο� �ִ� �̹����� ������.
						int sample = calculateInSampleSize(option,_bit.getWidth(),_bit.getHeight());
						option.inSampleSize = sample;  //������������ ȭ�� ����
						
						_bit = 	imgResize(_bit); // �̹����� ����̽�ũ�⿡ �°� �ٽø���.
						d = new BitmapDrawable(_bit);
						setBackgroundHandler.sendEmptyMessage(0);
						
						_bit=null;
					}catch(OutOfMemoryError e3){
						try{
							option.inSampleSize = 8;
							Bitmap _bit = BitmapFactory.decodeByteArray(img, 0, img.length, option); // ���ϰ�ο� �ִ� �̹����� ������.
							int sample = calculateInSampleSize(option,_bit.getWidth(),_bit.getHeight());
							option.inSampleSize = sample;  //������������ ȭ�� ����
							
							_bit = 	imgResize(_bit); // �̹����� ����̽�ũ�⿡ �°� �ٽø���.
							d = new BitmapDrawable(_bit);
							setBackgroundHandler.sendEmptyMessage(0);
							
							_bit=null;
						}catch(OutOfMemoryError e4){
							e.getMessage();
						}
					}
				}
			}

			Log.i("NetworkWithVT", "option.inSampleSize: " + option.inSampleSize);
			return true;
		}
		
		public static int calculateInSampleSize(
	            BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;

	    if (height > reqHeight || width > reqWidth) {

	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;

	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) > reqHeight
	                && (halfWidth / inSampleSize) > reqWidth) {
	            inSampleSize *= 2;
	        }
	    }

	    return inSampleSize;
		}
		
		private float mX, mY; // ������ǥ
		private static final float TOUCH_TOLERANCE = 4;
		
		//��ġ�� ���۵Ǹ� path�� �ʱ�ȭ�ϰ� ��ġ�� �������� �������� ����.
		private void touch_start(float x, float y) {
			mPath.reset();
			mPath.moveTo(x, y);
			mX = x;
			mY = y;
		}
		
		//��ġ�� �����̸� ������ ����(touch_start �κа� ��������� ��ġ�� ����)
		private void touch_move(float x, float y) {
			// ������ ��ǥ���� ������ǥ�� ���밪 = ����
			float dx = Math.abs(x - mX);
			float dy = Math.abs(y - mY);
			
			//�̺κ��� ��� �׸��ºκ��ε� �������� �κ��̶� ���� ���ش� �ȵ�. ����ȯ ��ġ�� ��ǥ�� �̿��ؼ� ��� �׸�.
			if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
				mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
				mX = x;
				mY = y;
			}
		}
		
		private void touch_up() {
			mPath.lineTo(mX, mY);
			mCanvas.drawPath(mPath, VTActivity.mPaint);
			mPath.reset();
		}

		@SuppressLint("ClickableViewAccessibility")
		public boolean onTouchEvent(MotionEvent event) {
			float x = event.getX();
			float y = event.getY();

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touch_start(x, y);
				invalidate();
				nm.sendData(PartnerScreenShower.DATA_TYPE_COORDINATE_START, x+"/"+y);
				
				Log.i("NetworkWithVT", "��ǥ_start: "+x+", "+y);
				
				break;
				
			case MotionEvent.ACTION_MOVE:
				touch_move(x, y);
				invalidate();
				nm.sendData(PartnerScreenShower.DATA_TYPE_COORDINATE_ING, x+"/"+y);
				
				Log.i("NetworkWithVT", "��ǥ_ing: "+x+", "+y);
				
				break;
				
			case MotionEvent.ACTION_UP:
				touch_up();
				invalidate();
				nm.sendData(PartnerScreenShower.DATA_TYPE_COORDINATE_END, x+"/"+y);
				
				Log.i("NetworkWithVT", "��ǥ_end: "+x+", "+y);
				
				break;
			}

			return true;
		}
		
		static void freeImages(){
			back_img1 = null;
			back_img2 = null;
			back_img3 = null;
			back_img4 = null;
			back_img5 = null;
			back_img6 = null;
		}
		
		static Handler setBackgroundHandler = new Handler() {
	    	@SuppressWarnings("deprecation")
			public void handleMessage(Message msg)  {
	    		if(android.os.Build.VERSION.SDK_INT < 16){
					VTActivity.layout.setBackgroundDrawable(d);
				}
				else{
					VTActivity.layout.setBackground(d);
				}
	    	}
	    };
	}
