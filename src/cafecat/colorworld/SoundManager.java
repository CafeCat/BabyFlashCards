package cafecat.colorworld;

import android.media.AudioManager;
import android.media.SoundPool;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.media.SoundPool.OnLoadCompleteListener;
import android.util.Log;

public class SoundManager {
	private Context pContext;		// Local copy of app context
	private SoundPool sndPool;		// Our SoundPool instance
	private float rate = 1.0f;		// Playback rate
	private float masterVolume = 1.0f;	// Master vloume level
	private float leftVolume = 1.0f;	// Volume levels for left and right channels
	private float rightVolume = 1.0f;
	private float balance = 0.5f;
	private boolean loaded = false;
	
	public SoundManager(Context appContext)
	{
	  sndPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
	  pContext = appContext;
	  sndPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
          @Override
          public void onLoadComplete(SoundPool soundPool, int sampleId,int status) {
              loaded = true; 
          }
      });
	}
	

	public int load(int sound_id)
	{
		return sndPool.load(pContext, sound_id, 1);
	}
	
	public void play(int sound_id)
	{
		if (loaded) {
            Log.e("Test", "sound is loaded");
        }
		if(loaded)
		{
		sndPool.play(sound_id, leftVolume, rightVolume, 1, 0, rate); 
		}
		
	}
	
	public void setVolume(float vol)
	{
		masterVolume = vol;
 
		if(balance < 1.0f) // Left dominant
		{
			leftVolume = masterVolume;
			rightVolume = masterVolume * balance;
		}
		else  // Right dominant
		{
			rightVolume = masterVolume;
			leftVolume = masterVolume * ( 2.0f - balance );
		}
 
	}
	
	public SoundPool getSoundPool(){
		return sndPool;
	}
	
	public void unloadAll()
	{
		sndPool.release();		
	}
	
}
