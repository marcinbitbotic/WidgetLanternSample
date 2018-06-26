package com.mpogorzelski.my_lantern_widget;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Para poner al maximo el brillo de la pantalla
		WindowManager.LayoutParams pantalla = getWindow().getAttributes();
		
		//Asigno brillo MAXIMO (0=minimo 1=maximo)
		pantalla.screenBrightness = 1;
		getWindow().setAttributes(pantalla);
		
	}
}
