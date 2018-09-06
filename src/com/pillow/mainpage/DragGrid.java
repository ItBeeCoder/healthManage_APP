package com.pillow.mainpage;

import com.pillow.R;
import com.pillow.ui.MainActivity;

import android.content.Context; 
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode.Callback;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author kris lau 
 * @Time 201603023
 * @description ģ��֧�������϶��Ź�����
 * @blog http://blog.sina.com.cn/u/1964256004
 */

public class DragGrid extends GridView {
	
	public int downX;
	
	public int downY;

	public int windowX;
	/**  */
	public int windowY;
	/**  */
	private int win_view_x;
	/**  */
	private int win_view_y;

	int dragOffsetX;

	int dragOffsetY;
	
	public int dragPosition;
	
	private int dropPosition;
	
	private int startPosition;
	
	private int itemHeight;
	
	private int itemWidth;
	
	private View dragImageView = null;
	
	private ViewGroup dragItemView = null;
	
	private WindowManager windowManager = null;
	/** */
	private WindowManager.LayoutParams windowParams = null;
	
	private int itemTotalCount;
	
	private int nColumns = 3;
	
	private int nRows;

	private int Remainder;
	
	private boolean isMoving = false;
	/** */
	private int holdPosition;
	
	private double dragScale = 1.2D;
	
	private Vibrator mVibrator;
	
	private int mHorizontalSpacing = 15;
	
	private int mVerticalSpacing = 15;
	
	private String LastAnimationID;
	private Context ctx;
	private boolean isReturnOnClickItem = false;

	public DragGrid(Context context) {
		super(context);
		init(context);
		this.ctx = context;
	}

	public DragGrid(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
		this.ctx = context;
	}

	public DragGrid(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		this.ctx = context;
	}

	public void init(Context context) {
		mVibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
	
		mHorizontalSpacing = DataTools.dip2px(context, mHorizontalSpacing);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			downX = (int) ev.getX();
			downY = (int) ev.getY();
			windowX = (int) ev.getX();
			windowY = (int) ev.getY();
			setOnItemClickListener(ev);
		}
		return super.onInterceptTouchEvent(ev);
	}

	private int downPos = -1;

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		boolean bool = true;
		int x = (int) ev.getX();
		int y = (int) ev.getY();
		if (dragImageView == null) {
		
			bool = super.onTouchEvent(ev);
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				downX = (int) ev.getX();
				windowX = (int) ev.getX();
				downY = (int) ev.getY();
				windowY = (int) ev.getY();
				
				downPos = pointToPosition(downX, downY);
				if (DragAdapter.selectedPos != -1
						&& downPos == DragAdapter.selectedPos) {

					if (((ImageView) ((ViewGroup) getChildAt(DragAdapter.selectedPos))
							.findViewById(R.id.delete_icon)).getVisibility() == View.VISIBLE) {
						isReturnOnClickItem = true;
					}
					
					((ImageView) ((ViewGroup) getChildAt(DragAdapter.selectedPos))
							.findViewById(R.id.delete_icon))
							.setVisibility(View.GONE);
					DragAdapter.selectedPos = -1;
				} else if (DragAdapter.selectedPos != -1
						&& downPos != DragAdapter.selectedPos) {
					if (((ImageView) ((ViewGroup) getChildAt(DragAdapter.selectedPos))
							.findViewById(R.id.delete_icon)).getVisibility() == View.VISIBLE) {
						isReturnOnClickItem = true;
					}
					
					((ImageView) ((ViewGroup) getChildAt(DragAdapter.selectedPos))
							.findViewById(R.id.delete_icon))
							.setVisibility(View.GONE);
					DragAdapter.selectedPos = -1;
				}
				break;
			case MotionEvent.ACTION_MOVE:
				break;
			case MotionEvent.ACTION_UP:
				requestDisallowInterceptTouchEvent(false);
				break;
			default:
				break;
			}
		}
		if (dragImageView != null
				&& dragPosition != AdapterView.INVALID_POSITION) {
			
			bool = super.onTouchEvent(ev);
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				downX = (int) ev.getX();
				windowX = (int) ev.getX();
				downY = (int) ev.getY();
				windowY = (int) ev.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				onDrag(x, y, (int) ev.getRawX(), (int) ev.getRawY());
				if (!isMoving) {
					OnMove(x, y);
				}
				if (pointToPosition(x, y) != AdapterView.INVALID_POSITION) {
					break;
				}
				break;
			case MotionEvent.ACTION_UP:
				stopDrag();
				onDrop(downPos);
				requestDisallowInterceptTouchEvent(false);
				break;

			default:
				break;
			}
		}
		return super.onTouchEvent(ev);
	}

	
	private void onDrag(int x, int y, int rawx, int rawy) {
		if (dragImageView != null) {
			windowParams.alpha = 0.6f;
			// windowParams.x = rawx - itemWidth / 2;
			// windowParams.y = rawy - itemHeight / 2;
			windowParams.x = rawx - win_view_x;
			windowParams.y = rawy - win_view_y;
			windowManager.updateViewLayout(dragImageView, windowParams);
		}
	}

	/** �������·ŵ���� */
	private void onDrop(int x, int y) {
		//
		int tempPostion = pointToPosition(x, y);
		System.out.println("当前位置" + x + "," + y + "," + tempPostion);
		// if (tempPostion != AdapterView.INVALID_POSITION) {
		dropPosition = tempPostion;
		DragAdapter mDragAdapter = (DragAdapter) getAdapter();
		//
		mDragAdapter.setShowDropItem(true);
		if (dropPosition != -1)
			mDragAdapter.setHoldPosition(dropPosition);
		// 
		mDragAdapter.notifyDataSetChanged();
		// }
	}

	/** �������·ŵ���� */
	private void onDrop(int clickPos) {
//		System.out.println("���ֲ�����" + clickPos);
		// if (tempPostion != AdapterView.INVALID_POSITION) {
		dropPosition = clickPos;
		DragAdapter mDragAdapter = (DragAdapter) getAdapter();
		// ��ʾ���϶���ITEM
		mDragAdapter.setShowDropItem(true);
		//�ж��Ƿ�һ����λ��
		if (dropPosition != -1)
			mDragAdapter.setHoldPosition(dropPosition);
		// ˢ�����������ö�Ӧ��ITEM��ʾ
		mDragAdapter.notifyDataSetChanged();
		if (dropPosition != DragAdapter.selectedPos) {
			mDragAdapter.setReset(true);
		}else{
			mDragAdapter.setReset(false);
		}
		// }
	}

	private long lastTime = 0;

	/**
	 * @param ev
	 */
	public int clickPos = 0;
	public int clickX = 0;
	public int clickY = 0;
	public void setOnItemClickListener(final MotionEvent ev) {
		clickPos = pointToPosition((int) ev.getX(), (int) ev.getY());
		clickX = (int) ev.getX();
		clickY = (int) ev.getY();
		if ((clickPos != -1 && clickPos == DragAdapter.selectedPos)
				|| DragAdapter.selectedPos == -1) {

			setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent,
						View view, int position, long id) {
					int x = (int) ev.getX();// 
					int y = (int) ev.getY();// 

					startPosition = position;//postion
					dragPosition = position;
					if (startPosition == getCount() - 1) {
						return false;
					}
					ViewGroup dragViewGroup = (ViewGroup) getChildAt(dragPosition
							- getFirstVisiblePosition());
					TextView dragTextView = (TextView) dragViewGroup
							.findViewById(R.id.text_item);
					dragTextView.setSelected(true);
					dragTextView.setEnabled(false);
					ImageView dragIcon = (ImageView) dragViewGroup
							.findViewById(R.id.delete_icon);
					dragIcon.setVisibility(View.VISIBLE);
					itemHeight = dragViewGroup.getHeight();
					itemWidth = dragViewGroup.getWidth();
					itemTotalCount = DragGrid.this.getCount();
					int row = itemTotalCount / nColumns;// 
					Remainder = (itemTotalCount % nColumns);// 
					if (Remainder != 0) {
						nRows = row + 1;
					} else {
						nRows = row;
					}
					// 
					if (dragPosition != AdapterView.INVALID_POSITION) {
						// 
						win_view_x = windowX - dragViewGroup.getLeft();// VIEW
						win_view_y = windowY - dragViewGroup.getTop();// VIEW
						dragOffsetX = (int) (ev.getRawX() - x);// 
						dragOffsetY = (int) (ev.getRawY() - y);// 
						dragItemView = dragViewGroup;
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								dragItemView.destroyDrawingCache();
								dragItemView.setDrawingCacheEnabled(true);
								Bitmap dragBitmap = Bitmap
										.createBitmap(dragItemView
												.getDrawingCache());
								mVibrator.vibrate(50);//
								startDrag(dragBitmap, (int) ev.getRawX(),
										(int) ev.getRawY());
								hideDropItem();
								dragItemView.setVisibility(View.INVISIBLE);
								isMoving = false;
								//TODO:
								DragAdapter.selectedPos = dragPosition;
							}
						}, 50);
						requestDisallowInterceptTouchEvent(true);
						return true;
					}
					return false;
				}
			});
		} else {
			this.setOnItemLongClickListener(null);
		}
		
		setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				ViewGroup dragViewGroup = (ViewGroup) getChildAt(clickPos
						- getFirstVisiblePosition());
				ImageView deleteIcon = (ImageView) dragViewGroup
						.findViewById(R.id.delete_icon);
				int dragIconViewX = dragViewGroup.getRight() - deleteIcon.getWidth();
				int dragIconViewY = dragViewGroup.getTop();
				if (dragIconViewX<clickX && clickX<dragViewGroup.getRight() &&
						dragIconViewY < clickY && clickY < (dragIconViewY + deleteIcon.getHeight())) {
					DragAdapter mDragAdapter = (DragAdapter) getAdapter();
					mDragAdapter.remove(clickPos);
					mDragAdapter.setReset(true);
					return;
				}
				
				if (DragAdapter.selectedPos == -1 && isReturnOnClickItem) {
					isReturnOnClickItem = false;
					return;
				}
				
				
			if (System.currentTimeMillis() - lastTime >= 500){
				if(position==0){
					Toast.makeText(ctx,"点击的是第0个",Toast.LENGTH_SHORT).show();
					Intent intent = new Intent();
					intent.setClass(ctx,MainActivity.class);
					startActionMode((Callback) intent);
					
				}else if(position==1){
					Toast.makeText(ctx,"点击的是第1个",Toast.LENGTH_SHORT).show();
				}else if(position==2){
					Toast.makeText(ctx,"点击的是第2个",Toast.LENGTH_SHORT).show();
				}
			}
//				Toast.makeText(ctx,
//					((DragAdapter) getAdapter()).channelList.get(
//					position).getName()
//					+ "," + position + "������",Toast.LENGTH_SHORT).show();
				
				lastTime = System.currentTimeMillis();
			}
		});
	}

	public void startDrag(Bitmap dragBitmap, int x, int y) {
		stopDrag();
		windowParams = new WindowManager.LayoutParams();// ��ȡWINDOW�����
		// Gravity.TOP|Gravity.LEFT;��������
		windowParams.gravity = Gravity.TOP | Gravity.LEFT;
		// windowParams.x = x - (int)((itemWidth / 2) * dragScale);
		// windowParams.y = y - (int) ((itemHeight / 2) * dragScale);
		// �õ�preview���Ͻ��������Ļ�����
		windowParams.x = x - win_view_x;
		windowParams.y = y - win_view_y;
		// this.windowParams.x = (x - this.win_view_x + this.viewX);//λ�õ�xֵ
		// this.windowParams.y = (y - this.win_view_y + this.viewY);//λ�õ�yֵ
		// ������קitem�Ŀ�͸�
		windowParams.width = (int) (dragScale * dragBitmap.getWidth());// �Ŵ�dragScale�������������϶���ı���
		windowParams.height = (int) (dragScale * dragBitmap.getHeight());// �Ŵ�dragScale�������������϶���ı���
		this.windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		this.windowParams.format = PixelFormat.TRANSLUCENT;
		this.windowParams.windowAnimations = 0;
		ImageView iv = new ImageView(getContext());
		iv.setImageBitmap(dragBitmap);
		windowManager = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);// "window"
		windowManager.addView(iv, windowParams);
		dragImageView = iv;
	}

	/** ֹͣ�϶� ���ͷŲ���ʼ�� */
	private void stopDrag() {
		if (dragImageView != null) {
			windowManager.removeView(dragImageView);
			dragImageView = null;
		}
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

	/** ���� ���� ��ITEM */
	private void hideDropItem() {
		((DragAdapter) getAdapter()).setShowDropItem(false);
	}

	/** ��ȡ�ƶ����� */
	public Animation getMoveAnimation(float toXValue, float toYValue) {
		TranslateAnimation mTranslateAnimation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.0F, Animation.RELATIVE_TO_SELF,
				toXValue, Animation.RELATIVE_TO_SELF, 0.0F,
				Animation.RELATIVE_TO_SELF, toYValue);// ��ǰλ���ƶ���ָ��λ��
		mTranslateAnimation.setFillAfter(true);// ����һ������Ч��ִ����Ϻ�View����������ֹ��λ�á�
		mTranslateAnimation.setDuration(300L);
		return mTranslateAnimation;
	}

	/** �ƶ���ʱ�򴥷� */
	public void OnMove(int x, int y) {
		//�϶���VIEW�·���POSTION
		int dPosition = pointToPosition(x, y);
		// �ж��·���POSTION�Ƿ����ʼ2�������϶���
		if (dPosition < getCount() - 1) {
			if ((dPosition == -1) || (dPosition == dragPosition)) {
				return;
			}
			dropPosition = dPosition;
			if (dragPosition != startPosition) {
				dragPosition = startPosition;
			}
			int movecount;
			// �϶���=��ʼ�ϵģ����� �϶��� �����ڷ��µ�
			if ((dragPosition == startPosition)
					|| (dragPosition != dropPosition)) {
				// ����Ҫ�ƶ��Ķ�ITEM����
				movecount = dropPosition - dragPosition;
			} else {
				// ����Ҫ�ƶ��Ķ�ITEM����Ϊ0
				movecount = 0;
			}
			if (movecount == 0) {
				return;
			}

			int movecount_abs = Math.abs(movecount);

			if (dPosition != dragPosition) {
				// dragGroup����Ϊ���ɼ�
				ViewGroup dragGroup = (ViewGroup) getChildAt(dragPosition);
				dragGroup.setVisibility(View.INVISIBLE);
				float to_x = 1;// ��ǰ�·�positon
				float to_y;// ��ǰ�·��ұ�positon
				// x_vlaue�ƶ��ľ���ٷֱȣ�������Լ����ȵİٷֱȣ�
				float x_vlaue = ((float) mHorizontalSpacing / (float) itemWidth) + 1.0f;
				// y_vlaue�ƶ��ľ���ٷֱȣ�������Լ���ȵİٷֱȣ�
				float y_vlaue = ((float) mVerticalSpacing / (float) itemHeight) + 1.0f;
				Log.d("x_vlaue", "x_vlaue = " + x_vlaue);
				for (int i = 0; i < movecount_abs; i++) {
					to_x = x_vlaue;
					to_y = y_vlaue;
					// ����
					if (movecount > 0) {
						// �ж��ǲ���ͬһ�е�
						holdPosition = dragPosition + i + 1;
						if (dragPosition / nColumns == holdPosition / nColumns) {
							to_x = -x_vlaue;
							to_y = 0;
						} else if (holdPosition % 4 == 0) {
							to_x = 3 * x_vlaue;
							to_y = -y_vlaue;
						} else {
							to_x = -x_vlaue;
							to_y = 0;
						}
					} else {
						// ����,���Ƶ��ϣ����Ƶ���
						holdPosition = dragPosition - i - 1;
						if (dragPosition / nColumns == holdPosition / nColumns) {
							to_x = x_vlaue;
							to_y = 0;
						} else if ((holdPosition + 1) % 4 == 0) {
							to_x = -3 * x_vlaue;
							to_y = y_vlaue;
						} else {
							to_x = x_vlaue;
							to_y = 0;
						}
					}
					ViewGroup moveViewGroup = (ViewGroup) getChildAt(holdPosition);
					Animation moveAnimation = getMoveAnimation(to_x, to_y);
					moveViewGroup.startAnimation(moveAnimation);
					// ��������һ���ƶ��ģ���ô���������������IDΪLastAnimationID
					if (holdPosition == dropPosition) {
						LastAnimationID = moveAnimation.toString();
					}
					moveAnimation.setAnimationListener(new AnimationListener() {

						@Override
						public void onAnimationStart(Animation animation) {
							// TODO Auto-generated method stub
							isMoving = true;
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationEnd(Animation animation) {
							// TODO Auto-generated method stub
							// ���Ϊ��������������ִ������ķ���
							if (animation.toString().equalsIgnoreCase(
									LastAnimationID)) {
								DragAdapter mDragAdapter = (DragAdapter) getAdapter();
								mDragAdapter.exchange(startPosition,
										dropPosition);
								downPos = dropPosition;
								startPosition = dropPosition;
								dragPosition = dropPosition;
								isMoving = false;
							}
						}
					});
				}
			}
		}
	}
}