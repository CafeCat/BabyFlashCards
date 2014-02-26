package cafecat.colorworld;

import java.io.File;
import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.app.Activity;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import java.lang.Runnable;

public class ImageCards extends Activity {
    private TextView debugTxt;
    private static final String Tag = "imageCards";
    private Handler mHandler;
    private Handler mHandlerSwitch;
    
    private int soundID;
    private SoundManager mySoundEffects;
    
    private int thisAppViewWidth;
    private int thisAppViewHeight;
    private viewAllCards myCardsViewer;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_cards);
		
		//Get the app view size, handel animations
		Display currentDisplay = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		currentDisplay.getSize(size);
		thisAppViewWidth = size.x;
		thisAppViewHeight = size.y;
		
		myCardsViewer = new viewAllCards(getApplicationContext());
		myCardsViewer = (viewAllCards)findViewById(R.id.view2);	
		myCardsViewer.prepare(size.x, size.y, 6, 4);
		mHandler = new Handler();
		mHandlerSwitch = new Handler();
        
		//handel sounds
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mySoundEffects = new SoundManager(getApplicationContext());
        soundID = mySoundEffects.load(R.raw.digital_pop1);  
        

        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.chopin_walts_35sec);   
        try {
			mp.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        mp.start();
        mp.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				if(myCardsViewer.getPlayingCardIndex()>=0 && myCardsViewer.getPlayingCardIndex()<=7){
					mp.setd
				}
			}
		});
        
        //Listernes here
        myCardsViewer.setOnTouchListener(new MyImageViewTouchProcessor());
        
       
        //Debugging here
        debugTxt = (TextView)findViewById(R.id.textView1);
		//debugTxt.setText(fitableSrc.getWidth()+","+fitableSrc.getHeight()+"/display w,h ("+size.x+","+size.y+")"+"|"+src.getWidth()+","+src.getHeight());
        //debugTxt.setText(myGridCrop.getOffsetAndSizeStr() + System.getProperty("line.separator") + fitableSrc.getWidth()+","+fitableSrc.getHeight());
        //Thread.currentThread().setUncaughtExceptionHandler(new HeapDumpingUncaughtExceptionHandler(getApplicationInfo().dataDir));
	}
	
	@Override 
	protected void onPause(){
		mHandler.removeCallbacks(runCellChangeAnimation);
		mHandlerSwitch.removeCallbacks(runCellAnimation);
		super.onPause();
	}
	
	@Override
	protected void onResume(){
		mHandler.post(runCellChangeAnimation);
		super.onResume();
	}
	
	private Runnable runCellChangeAnimation = new Runnable() 
    {
        public void run() 
        {
        	mHandlerSwitch.post(runCellAnimation);
        	if(myCardsViewer.isCellOpen()){
        		mySoundEffects.setVolume(0.5f);
        		mySoundEffects.play(soundID);
        	}
        	Log.e(Tag,"Runnable CellChange");
        }        
    };
    
    private Runnable runCellAnimation = new Runnable()
    {
    	public void run()
    	{
    		if(myCardsViewer.updateCellAnimationFrame() == false){
    			mHandlerSwitch.postDelayed(this, 5);
    		}else{
    			mHandler.post(runCellChangeAnimation);
    			
    		}
    	}
    };
	
	private class MyImageViewTouchProcessor implements View.OnTouchListener{
		private double initialX = 0;
		private double initialY = 0;
		double limitArea = 100*100*Math.PI;
		private String debuging = "";

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				initialX = event.getRawX();
				initialY = event.getRawY(); 
				mHandler.removeCallbacks(runCellChangeAnimation);
				mHandlerSwitch.removeCallbacks(runCellAnimation);
				break;
			case MotionEvent.ACTION_MOVE:
				double currentX = event.getRawX();
	        	double currentY = event.getRawY();
	        	double radius = Math.sqrt((currentX-initialX)*(currentX-initialX) + (currentY-initialY)*(currentY-initialY));
	        	double area = radius*radius*Math.PI;
	        	debugTxt.setText("inital ("+ (int)initialX+","+(int)initialY+")| area "+(int)area);
	        	if((int)area > limitArea){
	        		initialX = currentX;
	        		initialY = currentY;
	        	}
				break;
			}
			return true;
		}
		
	}
	
	/*public class HeapDumpingUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
	    private static final String HPROF_DUMP_BASENAME = "LeakingApp.dalvik-hprof";
	    private final String dataDir;
	 
	    public HeapDumpingUncaughtExceptionHandler(String dataDir) {
	        this.dataDir = dataDir;
	    }
	 
	    @Override
	    public void uncaughtException(Thread thread, Throwable ex) {
	        String absPath = new File(dataDir, HPROF_DUMP_BASENAME).getAbsolutePath();
	        if(ex.getClass().equals(OutOfMemoryError.class)) {
	            try {
	                Debug.dumpHprofData(absPath);
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        ex.printStackTrace();
	    }
	}*/
}

