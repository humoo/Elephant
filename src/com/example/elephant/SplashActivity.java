package com.example.elephant;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//ȫ����ʾ
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//ȥ����Ϣ��
		setContentView(R.layout.activity_splash);
		
		new Thread(new loadMainUIActivity()).start();
		
		
	}
	
	class loadMainUIActivity implements Runnable{
		@Override
		public void run() {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			Intent intent=new Intent(SplashActivity.this,TabMainActivity.class);
			startActivity(intent);
			finish();
		}
	}
}
