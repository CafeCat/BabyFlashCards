package cafecat.colorworld;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//startActivity(new Intent(this,ImageCards.class));
		//finish();
		
		ImageButton btnViewCards = (ImageButton)findViewById(R.id.imageButton1);
		btnViewCards.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this,ImageCards.class));
				finish();				
			}
		});
	}
	
	/*@Override
	public void onWindowFocusChanged(boolean hasFocus){
		if(hasFocus){
			hideNavigationBar();
		}
	}*/
	
	private void hideNavigationBar(){
		View decorView = findViewById(R.id.startScreen_content);
		// Hide both the navigation bar and the status bar.
		// SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
		// a general rule, you should design your app to hide the status bar whenever you
		// hide the navigation bar.
		int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
		              | View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
	}

	

}
