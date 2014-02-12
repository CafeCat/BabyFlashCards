package cafecat.colorworld;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class viewAllCards extends View {
	private final static String Tag_ViewAllCards = "viewAllCards";
	private Bitmap aFitableVersionCard;
	private Bitmap aPreFitableVersionCard;
	private Bitmap cellBlock;
	private Bitmap storeCellBlock;
	private Context context;
	private Resources res;
	private TypedArray coloredCards;
	private TypedArray cardsBgcolor;
	private SliceImage mySlices;
	private SliceImage myPreSlices;
	private int thisViewWidth;
	private int thisViewHeight;
	
	private int crtAnimatingCard = 0;
	private int crtAnimatingTargetCell = 0;
	private int drawAtX = 0;
	private int drawAtY = 0;
	private int setAnimatingColumns = 1;
	private int setAnimatingRows = 1;
	private Matrix scaleEffects = new Matrix();
	private float scaleSwitch = 0;
	private int animatedTimes = 0;
	private Boolean cellAnimcationDone = false;
    private Paint paint = new Paint();
	
	public viewAllCards(Context context) {
		super(context);
		this.context = context;
		
		/*res = context.getResources();
		this.coloredCards = res.obtainTypedArray(R.array.all_cards);
		this.cardsBgcolor = res.obtainTypedArray(R.array.all_cards_bgcolor);
		this.coloredCards.recycle();
		this.cardsBgcolor.recycle();	
		
        Log.e(Tag_ViewAllCards,"viewAllCards class, constructed");
        Log.e(Tag_ViewAllCards,"cards lenth"+coloredCards.length());*/
	}
	public viewAllCards(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		/*res = context.getResources();
		this.coloredCards = res.obtainTypedArray(R.array.all_cards);
		this.cardsBgcolor = res.obtainTypedArray(R.array.all_cards_bgcolor);
		this.coloredCards.recycle();
		this.cardsBgcolor.recycle();*/
	}
	public viewAllCards(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	    this.context = context;
	    /*res = context.getResources();
		this.coloredCards = res.obtainTypedArray(R.array.all_cards);
		this.cardsBgcolor = res.obtainTypedArray(R.array.all_cards_bgcolor);
		this.coloredCards.recycle();
		this.cardsBgcolor.recycle();*/
	}
	
	/**
	 * This method must be called after an instance is created.
	 * @param _viewWidth
	 * @param _viewHeight
	 */
	public void prepare(int _viewWidth,int _viewHeight,int _sliceCols, int _sliceRows){
		this.thisViewWidth = _viewWidth;
		this.thisViewHeight = _viewHeight;
		storeCellBlock = Bitmap.createBitmap(_viewWidth, _viewHeight, Bitmap.Config.ARGB_8888);
		this.res = this.context.getResources();
		this.coloredCards = this.res.obtainTypedArray(R.array.all_cards);
		this.cardsBgcolor = this.res.obtainTypedArray(R.array.all_cards_bgcolor);
		this.coloredCards.recycle();
		this.cardsBgcolor.recycle();
		if(_sliceCols <=0){_sliceCols=1;}
		if(_sliceRows <=0){_sliceRows=1;}
		this.setAnimatingColumns = _sliceCols;
		this.setAnimatingRows = _sliceRows;
		this.aFitableVersionCard = prepareACardForSlices(((BitmapDrawable)coloredCards.getDrawable(0)).getBitmap(), getCardBgColor(0), _viewWidth, _viewHeight,_sliceCols,_sliceRows);
		mySlices = new SliceImage(this.aFitableVersionCard, this.setAnimatingColumns, this.setAnimatingRows);
		paint.setColor(Color.BLACK);
	}
	private int getCardBgColor(int cardIndex){
		int bgColor;
		if(cardsBgcolor != null && cardsBgcolor.length()>0){
			bgColor = cardsBgcolor.getColor(cardIndex, 0);
		}else{
			bgColor = Color.WHITE;
		}
		return bgColor;
	}
	
	/**
	 * Scales a cell bitmap and updates the offsetX and offsetY for onDraw.
	 * @return
	 */
	public Boolean updateCellAnimationFrame(){	
		cellAnimcationDone = false;
		if(this.crtAnimatingTargetCell == this.setAnimatingColumns*this.setAnimatingRows){
			this.crtAnimatingTargetCell = 0;
			if(animatedTimes == 1)
			{
				this.crtAnimatingCard = crtAnimatingCard + 1;
				if(this.crtAnimatingCard == this.coloredCards.length()){
					this.crtAnimatingCard = 0;
				}
				this.aFitableVersionCard = prepareACardForSlices(((BitmapDrawable)coloredCards.getDrawable(crtAnimatingCard)).getBitmap(), getCardBgColor(crtAnimatingCard), this.thisViewWidth,this.thisViewHeight,this.setAnimatingColumns,this.setAnimatingRows);
				mySlices = new SliceImage(this.aFitableVersionCard, this.setAnimatingColumns, this.setAnimatingRows);
				animatedTimes = 0;
			}else{animatedTimes = animatedTimes + 1;}
		}
			
		Bitmap bm = mySlices.getSlice(crtAnimatingTargetCell);
		if(scaleSwitch<1){
			scaleSwitch = scaleSwitch + 0.04f;
		}else if(scaleSwitch > 1){
			scaleSwitch = 1;
		}
		Log.e(Tag_ViewAllCards,"updateCellAnimationFrame(), scaleSwitch "+scaleSwitch);
		scaleEffects.reset();
		if(animatedTimes == 0)
		{
			scaleEffects.postScale(scaleSwitch, 1f, (float)bm.getWidth(), 0);
		}else if(animatedTimes == 1){
			float ns = 1f-scaleSwitch;
			if(ns<=0){
				ns = 0.06f;
			}
			scaleEffects.postScale(ns, 1f, (float)bm.getWidth(), 0);
		}
		
		cellBlock = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), scaleEffects,false);
		int cx = mySlices.getCellOffsetX(crtAnimatingTargetCell);
		int cy = mySlices.getCellOffsetY(crtAnimatingTargetCell);
		int cw = mySlices.getCellWidth(crtAnimatingTargetCell);
		int ch = mySlices.getCellHeight(crtAnimatingTargetCell);
		
		drawAtX = mySlices.getCellOffsetX(crtAnimatingTargetCell)+ (int)Math.round((float)(bm.getWidth()-cellBlock.getWidth())/2f);
		drawAtY = mySlices.getCellOffsetY(crtAnimatingTargetCell);
		if(animatedTimes >0){
			Canvas canvas = new Canvas(storeCellBlock);
			canvas.drawRect(cx, cy, cx+cw, cy+ch, paint);
		}
		
		if(scaleSwitch >= 1){
			cellAnimcationDone = true;
			Canvas canvas = new Canvas(storeCellBlock);
			if(animatedTimes == 0){
				canvas.drawBitmap(bm, mySlices.getCellOffsetX(crtAnimatingTargetCell),mySlices.getCellOffsetY(crtAnimatingTargetCell),null);
			}else{
				canvas.drawRect(cx, cy, cx+cw, cy+ch, paint);
			}
			crtAnimatingTargetCell = crtAnimatingTargetCell + 1;
			scaleSwitch = 0;
		}
		Log.e(Tag_ViewAllCards,"updateCellAnimationFrame(), return "+cellAnimcationDone.toString());
        invalidate();
        return cellAnimcationDone; 
	}
	 
	/*private void prepareACardForSlices(int cardIndex,int _thisViewWidth, int _thisViewHeight, int _columns, int _rows){
		//merge cards and colors
		this.aFitableVersionCard = Bitmap.createBitmap(_thisViewWidth, _thisViewHeight, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(this.aFitableVersionCard);
		if(cardsBgcolor != null && cardsBgcolor.length()>0){
			canvas.drawColor(cardsBgcolor.getColor(crtAnimatingCard, 0));
		}else{
			canvas.drawColor(Color.WHITE);
		}
		Bitmap src = ((BitmapDrawable)coloredCards.getDrawable(crtAnimatingCard)).getBitmap();
		Bitmap fitable = myImageFactory.getFitableBitmap(src, _thisViewWidth, _thisViewHeight, src.getWidth(), src.getHeight());
		
		int offsetX = (int)Math.round((float)(_thisViewWidth-fitable.getWidth())/(float)2);
    	int offsetY = (int)Math.round((float)(_thisViewHeight-fitable.getHeight())/(float)2);
    	canvas.drawBitmap(fitable, offsetX, offsetY, null);
    	src.recycle();
    	fitable.recycle();
	}*/
	private Bitmap prepareACardForSlices(Bitmap src,int bgClor,int _thisViewWidth, int _thisViewHeight, int _columns, int _rows){
		//merge cards and colors
		Bitmap destBm = Bitmap.createBitmap(_thisViewWidth, _thisViewHeight, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(destBm);
		
	    canvas.drawColor(bgClor);
		Bitmap fitable = myImageFactory.getFitableBitmap(src, _thisViewWidth, _thisViewHeight, src.getWidth(), src.getHeight());
		
		int offsetX = (int)Math.round((float)(_thisViewWidth-fitable.getWidth())/(float)2);
    	int offsetY = (int)Math.round((float)(_thisViewHeight-fitable.getHeight())/(float)2);
    	canvas.drawBitmap(fitable, offsetX, offsetY, null);
    	src.recycle();
    	fitable.recycle();
    	return destBm;
	}
	
	
	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		thisViewWidth = canvas.getWidth();
		thisViewHeight = canvas.getHeight();
		
		if(storeCellBlock != null){
			canvas.drawBitmap(storeCellBlock, 0, 0, null);
		}
        canvas.drawBitmap(cellBlock,drawAtX,drawAtY, null);
        
        Log.e(Tag_ViewAllCards,"onDraw(), atCell"+crtAnimatingTargetCell);
        
        
        
	}
    
	private class SliceImage{
		private final Bitmap bmSrc;
		private int[][] offsetAndSize;
		public SliceImage(final Bitmap bm, int columns, int rows){
			bmSrc = bm;
			offsetAndSize = myImageFactory.calculateSliceOffsetAndSize(columns, rows, bm.getWidth(), bm.getHeight());
		}
		public Bitmap getSlice(int cellIndex){
			return Bitmap.createBitmap(bmSrc,offsetAndSize[cellIndex][myImageFactory.CroppedItem.OFFSET_X.toInteger()],offsetAndSize[cellIndex][myImageFactory.CroppedItem.OFFSET_Y.toInteger()],offsetAndSize[cellIndex][myImageFactory.CroppedItem.WIDTH.toInteger()],offsetAndSize[cellIndex][myImageFactory.CroppedItem.HEIGHT.toInteger()]);
		}
		public int getCellOffsetX(int cellIndex){
			int rst = 0;
				if(offsetAndSize != null){
					rst = offsetAndSize[cellIndex][myImageFactory.CroppedItem.OFFSET_X.toInteger()];
				}
			return rst;
		}
		public int getCellOffsetY(int cellIndex){
			int rst = 0;
			if(offsetAndSize != null){
				rst = offsetAndSize[cellIndex][myImageFactory.CroppedItem.OFFSET_Y.toInteger()];
			}
			return rst;
		}
		public int getCellWidth(int cellIndex){
			int rst=0;
			if(offsetAndSize != null){
				rst = offsetAndSize[cellIndex][myImageFactory.CroppedItem.WIDTH.toInteger()];
			}
			return rst;
		}
		public int getCellHeight(int cellIndex){
			int rst=0;
			if(offsetAndSize != null){
				rst = offsetAndSize[cellIndex][myImageFactory.CroppedItem.HEIGHT.toInteger()];
			}
			return rst;
		}
	}
}
