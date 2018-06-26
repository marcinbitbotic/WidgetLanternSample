package com.mpogorzelski.my_lantern_widget;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Created by mpogorzelski on 25/06/2018.
 */

public class lanternWidget extends AppWidgetProvider {
	
	//Source
	//http://www.aprendeandroid.com/l10/widget_fundamentos.htm
	
	public static final int LED_ON = 1;
	public static final int LED_OFF = 0;
	
	//Defino las variables de la app
	static Camera camara;
	Parameters p;
	static int status_led = 0;
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		
		final int N = appWidgetIds.length;
		
		//bucle para actualizar todos los widget (por si se añade mas de uno)
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			
			// Método para actualizar y/o inicializar el widget la primera vez que se ejecuta
			updateWidget(context, appWidgetManager, appWidgetId);
		}
		
	}
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
		
		// acciones a tomar cuando se recibe el "broadcast" del widget
		// compruebo la "accion" del boton pulsado que introducimos en el método "actualizar widget"
		// en la linea 163
		
		if (intent.getAction().equals("iniciar_pantalla")) {
			
			// Inicio la activity con la pantalla blanca xxxxxxxxxxxxxxxxxx
			// xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
			Intent i = new Intent(context, MainActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
			
		} else if (intent.getAction().equals("iniciar_led")) {
			
			// Inicio acciones para encender o apagar el LED xxxxxxxxxxxx
			// xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
			
			// Para comprobar si hay LED en el movil
			PackageManager pm = context.getPackageManager();
			
			// El dispositivo soporta LED en la camara?
			if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
				Toast t_no_camara = Toast.makeText(context,
						"No hay LED detectado!!", Toast.LENGTH_LONG);
				t_no_camara.setGravity(Gravity.TOP, 50, 200);
				t_no_camara.show();
				return;
				
			} else {
				// Si hay LED, enciendo el LED ;P
				
				// primero compruebo si estaba encendido mediante esa variable
				if (status_led == LED_ON) {
					
					// como estaba encendido, lo apagamos y guardo el nuevo estado del LED a OFF
					status_led = LED_OFF;
					
					// El LED estaba ON y lo apago, compruebo primero el objeto camara tiene datos
					if (camara != null) {
						
						camara.stopPreview();
						camara.release();
						camara = null;
						
					}
					
				} else {
					
					// El LED estaba OFF y lo enciendo
					camara = Camera.open();
					if (camara == null) {
						Toast.makeText(context, "Error al encender la Camara!",
								Toast.LENGTH_SHORT).show();
					} else {
						// enciendo el LED
						p = camara.getParameters();
						p.setFlashMode(Parameters.FLASH_MODE_TORCH);
						camara.setParameters(p);
						camara.startPreview();
						
						// guardo estado del LED a encendido
						status_led = LED_ON;
						
					}
					
				}
				
			}
			
		}
		
		//Actualizamos todos los widgets (por si hay mas de 1 widget)
		Bundle extras = intent.getExtras();
		if (extras != null) {
			AppWidgetManager appWidgetManager = AppWidgetManager
					.getInstance(context);
			ComponentName thisAppWidget = new ComponentName(
					context.getPackageName(), lanternWidget.class.getName());
			int[] appWidgetIds = appWidgetManager
					.getAppWidgetIds(thisAppWidget);
			
			for (int appWidgetId : appWidgetIds) {
				// llamo al método para generar los intents y actualizar iconos
				updateWidget(context, appWidgetManager, appWidgetId);
			}
			
		}
	}
	
	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);
		
	}
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onDeleted(context, appWidgetIds);
		
		//guardo el estado led a apagado
		status_led = 0;
		
		//Si elimino widget, apago led (si estubiera encendido)
		if (camara != null) {
			
			camara.stopPreview();
			camara.release();
			camara = null;
		}
	}
	
	public static void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
		
		// Localizamos los controles (botones) del widget
		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.widget_layout);
		
		// Creamos el Intent y lo vinculamos al ImageView btn_screen
		Intent i = new Intent(context, lanternWidget.class);
		i.setAction("iniciar_pantalla");
		i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		PendingIntent pi = PendingIntent.getBroadcast(context, appWidgetId, i,
				0);
		views.setOnClickPendingIntent(R.id.btn_screen, pi);
		
		// Creamos el Intent y lo vinculamos al ImageView btn_led
		Intent i2 = new Intent(context, lanternWidget.class);
		i2.setAction("iniciar_led");
		i2.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		PendingIntent pi2 = PendingIntent.getBroadcast(context, appWidgetId,
				i2, 0);
		views.setOnClickPendingIntent(R.id.btn_led, pi2);
		
		// Cambiamos la imagen dependiendo de si esta activo o no el LED
		if (status_led == LED_OFF) {
			views.setImageViewResource(R.id.btn_led, R.drawable.led_off);
			
		} else if (status_led == LED_ON) {
			views.setImageViewResource(R.id.btn_led, R.drawable.led);
			
		}
		
		// Enviamos datos al sistema para que actualice el widget
		appWidgetManager.updateAppWidget(appWidgetId, views);
	}
}
