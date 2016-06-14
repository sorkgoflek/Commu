package com.lance.commu.visualtalk;

import com.lance.commu.intro.R;
import com.lance.commu.network.NetworkManager;

import android.app.Dialog;
import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ColorPickerDialog extends Dialog {

	public interface OnColorChangedListener {
		void colorChanged(int color);
	}

	private NetworkManager nm;
	
	private OnColorChangedListener mListener;
	private int mInitialColor;

	private static class ColorPickerView extends View {
		private Paint mPaint;
		private OnColorChangedListener mListener;

		float x, y;
		static float getX = 20;
		static int penSize = 5;
		static int penSize2; // 드래그선 이외에 영역을 클릭했을 때 예시로 보여지는 선을 제어하기 위한 변수.
		//int control=0; // penSize배열 제어변수.
		float S_penSize = VTActivity.mPaint.getStrokeWidth();

		/* 화면에 표기할 이미지버튼 */
		Bitmap white;
		Bitmap yellow;
		Bitmap orange;
		Bitmap red;
		Bitmap pink;
		Bitmap pupple;
		Bitmap sky;
		Bitmap blue;
		Bitmap blue2;

		Bitmap green1;
		Bitmap green2;
		Bitmap green3;
		Bitmap black1;
		Bitmap black2;
		Bitmap black3;
		Bitmap black4;
		Bitmap black5;
		Bitmap black6;

		int originalBackX, originalBackY; // 원본 이미지 크기

		static int color = Color.BLACK;

		ColorPickerView(Context c, OnColorChangedListener l, int color) {
			super(c);

			mListener = l;

			/*Paint 기본 설정.  */
			mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mPaint.setStyle(Paint.Style.STROKE);

			
			/* canvas 위에 버튼표시를 이미지로 하기위한 리소스 받아오기. */
			white = BitmapFactory.decodeResource(getResources(), R.drawable.white);
			yellow = BitmapFactory.decodeResource(getResources(), R.drawable.yellow);
			orange = BitmapFactory.decodeResource(getResources(), R.drawable.orange);
			red = BitmapFactory.decodeResource(getResources(), R.drawable.red);
			pink = BitmapFactory.decodeResource(getResources(), R.drawable.pink);
			pupple = BitmapFactory.decodeResource(getResources(), R.drawable.pupple);
			sky = BitmapFactory.decodeResource(getResources(), R.drawable.sky);
			blue = BitmapFactory.decodeResource(getResources(), R.drawable.blue);
			blue2 = BitmapFactory.decodeResource(getResources(), R.drawable.blue2);

			green1 = BitmapFactory.decodeResource(getResources(), R.drawable.green1);
			green2 = BitmapFactory.decodeResource(getResources(), R.drawable.green2);
			green3 = BitmapFactory.decodeResource(getResources(), R.drawable.green3);
			black1 = BitmapFactory.decodeResource(getResources(), R.drawable.black1);
			black2 = BitmapFactory.decodeResource(getResources(), R.drawable.black2);
			black3 = BitmapFactory.decodeResource(getResources(), R.drawable.black3);
			black4 = BitmapFactory.decodeResource(getResources(), R.drawable.black4);
			black5 = BitmapFactory.decodeResource(getResources(), R.drawable.black5);
			black6 = BitmapFactory.decodeResource(getResources(), R.drawable.black6);

			// 원본이미지의 가로,세로 길이 , 모든 이미지 들이 같음.
			originalBackX = white.getWidth();
			originalBackY = white.getHeight();
		}

		@Override
		protected void onDraw(Canvas canvas) {
			/*canvas위에 모든 거슬 그려야함. 비트맵을 특정 좌표에다가 그림.  */
			canvas.drawColor(Color.DKGRAY);
			canvas.drawBitmap(white, 0, 0, null);
			canvas.drawBitmap(orange, originalBackX + 10, 0, null);
			canvas.drawBitmap(yellow, originalBackX * 2 + 20, 0, null);
			canvas.drawBitmap(red, originalBackX * 3 + 30, 0, null);
			canvas.drawBitmap(pink, originalBackX * 4 + 40, 0, null);
			canvas.drawBitmap(pupple, originalBackX * 5 + 50, 0, null);
			canvas.drawBitmap(sky, originalBackX * 6 + 60, 0, null);
			canvas.drawBitmap(blue, originalBackX * 7 + 70, 0, null);
			canvas.drawBitmap(blue2, originalBackX * 8 + 80, 0, null);

			canvas.drawBitmap(green1, 0, originalBackY + 20, null);
			canvas.drawBitmap(green2, originalBackX + 10, originalBackY + 20, null);
			canvas.drawBitmap(green3, originalBackX * 2 + 20, originalBackY + 20, null);
			canvas.drawBitmap(black1, originalBackX * 3 + 30, originalBackY + 20, null);
			canvas.drawBitmap(black2, originalBackX * 4 + 40, originalBackY + 20, null);
			canvas.drawBitmap(black3, originalBackX * 5 + 50, originalBackY + 20, null);
			canvas.drawBitmap(black4, originalBackX * 6 + 60, originalBackY + 20, null);
			canvas.drawBitmap(black5, originalBackX * 7 + 70, originalBackY + 20, null);
			canvas.drawBitmap(black6, originalBackX * 8 + 80, originalBackY + 20, null);

			/*손터치 슬라이드 식으로 그리는 부분.  */
			mPaint.setColor(Color.WHITE);
			mPaint.setStrokeWidth(5);
			canvas.drawLine(0, originalBackY * 3, 1000, originalBackY * 3, mPaint);
			
			if (y > originalBackY * 3 - 30 && y < originalBackY * 3 + 30) {
				getX = x;
				canvas.drawCircle(x, originalBackY * 3, 20, mPaint);
			} else {
				canvas.drawCircle(getX, originalBackY * 3, 20, mPaint);
			}
			penSize = (int) (x / 10);	//슬라이드가 우측으로 갈수록 펜의 굵기를 증가시킴.

			/* 변화한 색상과 변화하는 굵기를 보여주기 위한 예시 line ( path를 이용하여 표시) */
			Path path = new Path();
			//터치한 좌표가 0이라면 (처음으로 다이얼로그를 열었을 경우) 그전까지 설정했던 모습을 그대로 유지한 예시line을 보여주기 위한 코드(색상,굵기)
			if (x == 0) {
				VTActivity.mPaint.setStrokeWidth(S_penSize);
				mPaint.setStrokeWidth(S_penSize);
			} else{	//터치하는 좌표가 변화되었다면 (다이얼로그에서 터치를 사용했다면) 변화하는 굵기 색상등을 바로바로 그려줌.
				if (y > originalBackY * 3 - 30 && y < originalBackY * 3 + 30) {
					if(penSize < 1){
						penSize=1;
					}
						
					VTActivity.mPaint.setStrokeWidth(penSize);
					mPaint.setStrokeWidth(penSize);
					penSize2 = penSize;
				}
				else{
					if(penSize2 < 1){
						penSize2=1;
					}
					
					VTActivity.mPaint.setStrokeWidth(penSize2);
					mPaint.setStrokeWidth(penSize2);
				}
			}
			mPaint.setColor(color);

			path.moveTo(10, originalBackY * 4 + 20);
			path.lineTo(originalBackX * 3 + 30, originalBackY * 4 + 40);
			path.lineTo(originalBackX * 5 + 50, originalBackY * 4 + 20);
			path.lineTo(originalBackX * 7 + 70, originalBackY * 4 + 40);
			path.lineTo(originalBackX * 9 + 90, originalBackY * 4 + 20);

			canvas.drawPath(path, mPaint);	//예시line 을 그림.
		}

		/*
		 * @Override protected void onSizeChanged(int w,int h,int oldw,int
		 * oldh){ width = w; height = h;
		 * 
		 * scaleRatioX = (int) (w/(originalBackX * 1.0f)); scaleRatioY = (int)
		 * (h/(originalBackY * 1.0f));
		 * 
		 * //화면의 크기에 맞게 이미지의 크기 수정 white =
		 * Bitmap.createScaledBitmap(white,w,h,false); }
		 */

		// 색상 다이얼로그 크기 조절
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			setMeasuredDimension(1000, CENTER_Y * 4);
		}

		private static final int CENTER_X = 100;
		private static final int CENTER_Y = 100;

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			x = event.getX();
			y = event.getY();
			
			//이 부분은 그냥 canvas위에 그려진 비트맵이미지들 좌표를 터치하는 부분.보기만 복잡함.
			//특정 영역이 터치되면 터치된 색상을 받아서 설정함.
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if ((event.getX() >= 0 && event.getX() < originalBackX)
						&& (event.getY() >= 0 && event.getY() < originalBackY)) {
							mListener.colorChanged(Color.parseColor("#ffffff"));
							color = Color.parseColor("#ffffff");
				} else if ((event.getX() >= originalBackX + 10 && event.getX() < originalBackX * 2 + 10)
						&& (event.getY() >= 0 && event.getY() < originalBackY)) {
							mListener.colorChanged(Color.parseColor("#ffa500"));
							color = Color.parseColor("#ffa500");
				} else if ((event.getX() >= originalBackX * 2 + 20 && event.getX() < originalBackX * 3 + 20)
						&& (event.getY() >= 0 && event.getY() < originalBackY)) {
							mListener.colorChanged(Color.parseColor("#ffff00"));
							color = Color.parseColor("#ffff00");
				} else if ((event.getX() >= originalBackX * 3 + 30 && event.getX() < originalBackX * 4 + 30)
						&& (event.getY() >= 0 && event.getY() < originalBackY)) {
							mListener.colorChanged(Color.parseColor("#ff0000"));
							color = Color.parseColor("#ff0000");
				} else if ((event.getX() >= originalBackX * 4 + 40 && event.getX() < originalBackX * 5 + 40)
						&& (event.getY() >= 0 && event.getY() < originalBackY)) {
							mListener.colorChanged(Color.parseColor("#ff69b4"));
							color = Color.parseColor("#ff69b4");
				} else if ((event.getX() >= originalBackX * 5 + 50 && event.getX() < originalBackX * 6 + 50)
						&& (event.getY() >= 0 && event.getY() < originalBackY)) {
							mListener.colorChanged(Color.parseColor("#800080"));
							color = Color.parseColor("#800080");
				} else if ((event.getX() >= originalBackX * 6 + 60 && event.getX() < originalBackX * 7 + 60)
						&& (event.getY() >= 0 && event.getY() < originalBackY)) {
							mListener.colorChanged(Color.parseColor("#87ceeb"));
							color = Color.parseColor("#87ceeb");
				} else if ((event.getX() >= originalBackX * 7 + 70 && event.getX() < originalBackX * 8 + 70)
						&& (event.getY() >= 0 && event.getY() < originalBackY)) {
							mListener.colorChanged(Color.parseColor("#4682b4"));
							color = Color.parseColor("#4682b4");
				} else if ((event.getX() >= originalBackX * 8 + 80 && event.getX() < originalBackX * 9 + 80)
						&& (event.getY() >= 0 && event.getY() < originalBackY)) {
							mListener.colorChanged(Color.parseColor("#0000ff"));
							color = Color.parseColor("#0000ff");
				}

				//
				else if ((event.getX() >= 0 && event.getX() < originalBackX)
						&& (event.getY() >= originalBackY + 20 && event.getY() < originalBackY * 2 + 20)) {
							mListener.colorChanged(Color.parseColor("#7fff00"));
							color = Color.parseColor("#7fff00");
				} else if ((event.getX() >= originalBackX + 10 && event.getX() < originalBackX * 2 + 10)
						&& (event.getY() >= originalBackY + 20 && event.getY() < originalBackY * 2 + 20)) {
							mListener.colorChanged(Color.parseColor("#008000"));
							color = Color.parseColor("#008000");
				} else if ((event.getX() >= originalBackX * 2 + 20 && event.getX() < originalBackX * 3 + 20)
						&& (event.getY() >= originalBackY + 20 && event.getY() < originalBackY * 2 + 20)) {
							mListener.colorChanged(Color.parseColor("#006400"));
							color = Color.parseColor("#006400");
				} else if ((event.getX() >= originalBackX * 3 + 30 && event.getX() < originalBackX * 4 + 30)
						&& (event.getY() >= originalBackY + 20 && event.getY() < originalBackY * 2 + 20)) {
							mListener.colorChanged(Color.parseColor("#fffafa"));
							color = Color.parseColor("#fffafa");
				} else if ((event.getX() >= originalBackX * 4 + 40 && event.getX() < originalBackX * 5 + 40)
						&& (event.getY() >= originalBackY + 20 && event.getY() < originalBackY * 2 + 20)) {
							mListener.colorChanged(Color.parseColor("#d3d3d3"));
							color = Color.parseColor("#d3d3d3");
				} else if ((event.getX() >= originalBackX * 5 + 50 && event.getX() < originalBackX * 6 + 50)
						&& (event.getY() >= originalBackY + 20 && event.getY() < originalBackY * 2 + 20)) {
							mListener.colorChanged(Color.parseColor("#808080"));
							color = Color.parseColor("#808080");
				} else if ((event.getX() >= originalBackX * 6 + 60 && event.getX() < originalBackX * 7 + 60)
						&& (event.getY() >= originalBackY + 20 && event.getY() < originalBackY * 2 + 20)) {
							mListener.colorChanged(Color.parseColor("#696969"));
							color = Color.parseColor("#696969");
				} else if ((event.getX() >= originalBackX * 7 + 70 && event.getX() < originalBackX * 8 + 70)
						&& (event.getY() >= originalBackY + 20 && event.getY() < originalBackY * 2 + 20)) {
							mListener.colorChanged(Color.parseColor("#2f4f4f"));
							color = Color.parseColor("#2f4f4f");
				} else if ((event.getX() >= originalBackX * 8 + 80 && event.getX() < originalBackX * 9 + 80)
						&& (event.getY() >= originalBackY + 20 && event.getY() < originalBackY * 2 + 20)) {
							mListener.colorChanged(Color.parseColor("#000000"));
							color = Color.parseColor("#000000");
				}

				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				invalidate();
				break;
			}
			return true;
		}
	}

	public ColorPickerDialog(Context context, OnColorChangedListener listener, int initialColor) {
		super(context);
		mListener = listener;
		mInitialColor = initialColor;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		nm=NetworkManager.getInstance();
		
		OnColorChangedListener l = new OnColorChangedListener() {
			public void colorChanged(int color) {
				mListener.colorChanged(color);
			}
		};

		setContentView(new ColorPickerView(getContext(), l, mInitialColor));
		setTitle("펜 설정하기");
	}
	
	public void dismiss(){
		super.dismiss();
		float width=VTActivity.mPaint.getStrokeWidth();
		nm.sendData(PartnerScreenShower.DATA_TYPE_PEN_WIDTH, ""+width);
		
		Log.i("NetworkWithVT", "굵기 설정: "+width);
		
	}
}