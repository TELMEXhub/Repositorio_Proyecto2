package com.matrixled;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;


@SuppressLint("HandlerLeak")
public class MainActivity extends Activity implements View.OnClickListener{
	//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
	// Debugging
    public static final String TAG = "LEDv0";
    public static final boolean D = true;
    // Tipos de mensaje enviados y recibidos desde el Handler de ConexionBT
    public static final int Mensaje_Estado_Cambiado = 1;
    public static final int Mensaje_Leido = 2;
    public static final int Mensaje_Escrito = 3;
    public static final int Mensaje_Nombre_Dispositivo = 4;
    public static final int Mensaje_TOAST = 5;	        
    public static final int MESSAGE_Desconectado = 6;
    public static final int REQUEST_ENABLE_BT = 7;    
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    //Nombre del dispositivo conectado
    private String mConnectedDeviceName = null;
    // Adaptador local Bluetooth 
    private BluetoothAdapter AdaptadorBT = null;    
    //Objeto del servicio de ConexionBT 
    private ConexionBT Servicio_BT = null;	      
    //variables para el Menu de conexión
    private boolean seleccionador=false;
    public int Opcion=R.menu.conecta; 
    //Matriz de estados
    public boolean estados[]= new boolean[36];
    //botones
    public RadioButton botones[] = new RadioButton[36];
    //Handler usado para la muestra de anuncio	
	private Handler anuncio = new Handler();
	int contador=1, corrimiento=0;       
    //\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
   /*
    * Metodo iniciado al arrancar la aplicacion, aqui definiremos los botones que se agregaron
    * en el archivo activity_main.xml asignandole a cada uno un Listener del tipo OnClick
    */
        for (int i=0; i<35; i++){ 
        	botones[i+1]= (RadioButton)findViewById(R.id.radioButton1+i);
        	botones[i+1].setOnClickListener(this);	
        						}                    
    }

    public  void onStart() {
    super.onStart();
    ConfigBT();	//LLamamos al metodo ConfigBT donde solicitaremos la conexión BT
    }
    
    @Override
    public void onDestroy(){
    	 super.onDestroy();    
    	 borrando();   //Evitamos que la matriz de led quede encendida	 
    	 if (Servicio_BT != null) Servicio_BT.stop();//Detenemos servicio
    }
      
    public void ConfigBT(){
    	// Obtenemos el adaptador de bluetooth
        AdaptadorBT = BluetoothAdapter.getDefaultAdapter();
        if (AdaptadorBT.isEnabled()) {//Si el BT esta encendido,
       	 if (Servicio_BT == null) {//y el Servicio_BT es nulo, invocamos el Servicio_BT    		 
       		 Servicio_BT = new ConexionBT(this, mHandler);
       	 }      
        }
   	 else{ //De lo contrario haremos la peticion para que el BT sea encendido
     Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
     startActivityForResult(enableBluetooth, REQUEST_ENABLE_BT); //y esperamos por respuesta
   	 }
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
  	      switch (requestCode) {
  	               
  	            case REQUEST_ENABLE_BT://Respuesta de intento de encendido de BT
  	                if (resultCode == Activity.RESULT_OK) {//BT esta activado,iniciamos servicio
  	                	 ConfigBT();
  	                } else {//No se activo BT, salimos de la app                 
  	                    finish();}

  	                }//fin de switch case
  	            }//fin de onActivityResult     
  	          	      
        
    @Override
    public boolean onPrepareOptionsMenu(Menu menux){
    //cada vez que se presiona la tecla menu  este metodo es llamado
    menux.clear();//limpiamos menu actual    
       	if (seleccionador==false)Opcion=R.menu.conecta;//dependiendo las necesidades
       	if (seleccionador==true)Opcion=R.menu.desconecta;  // crearemos un menu diferente
     	getMenuInflater().inflate(Opcion, menux); // y lo "inflamos"
    return super.onPrepareOptionsMenu(menux);
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.Conexion:      	    	 
	                 String address = "00:06:66:42:A7:C5";//Direccion Mac de mi rn42 "CAMBIAR"                    
	                 BluetoothDevice device = AdaptadorBT.getRemoteDevice(address);
	                 Servicio_BT.connect(device);         		 	        		              	
            return true;	    
            
        case R.id.desconexion:
        	 borrando(); //Evitamos que la matriz de led quede encendida con algun dato
        	 if (Servicio_BT != null) Servicio_BT.stop();//Detenemos servicio
        	 return true;
        	 
        case R.id.borrar:
        	borrando();                	
        	return true;	
        	
        case R.id.mover:
        	anuncio.postDelayed(temporiza, 500); //Mostramos un anuncio (Telmexhub//)
        	return true;
        	 
        }//fin de swtich de opciones
        return false;
    }//fin de metodo onOptionsItemSelected
   

    public  void sendMessage(String message) {
    	//Metodo para el envio de "mensajes" hacia nuestro circuito electronico
        if (Servicio_BT.getState() == ConexionBT.STATE_CONNECTED) {//checa si estamos conectados a BT   
        if (message.length() > 0) {   // checa si hay algo que enviar
            byte[] send = message.getBytes();//Obtenemos bytes del mensaje        
                 Servicio_BT.write(send);     //Mandamos a escribir el mensaje     
        		}
        	} else Toast.makeText(this, "No conectado", Toast.LENGTH_SHORT).show();
    		}//fin de sendMessage
    
    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
	            	
	                switch (msg.what) {
	     //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>   
	                case Mensaje_Escrito:
	                    break;
	      //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>                                
	                case Mensaje_Leido:             	                 
	                    break;
	     //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>	           
	                case Mensaje_Nombre_Dispositivo:
	                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME); //Guardamos nombre del dispositivo
	     Toast.makeText(getApplicationContext(), "Conectado con "+ mConnectedDeviceName, Toast.LENGTH_SHORT).show();
	     seleccionador=true;
	                    break;
	    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>                  
	                case Mensaje_TOAST://mostramos por mensaje de toast una notificacion
	                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
	                    Toast.LENGTH_SHORT).show();
	                    break;
	     //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 
	                case MESSAGE_Desconectado:	
	                  	 seleccionador=false;             	
	          break;
	    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  
	                }//FIN DE SWITCH CASE PRIMARIO DEL HANDLER
	            }//FIN DE METODO INTERNO handleMessage
	        };//Fin de Handler

	        
	@Override
	public void onClick(View v) {				
		/*Metodo implementado por la interfaz View.OnClickListener
		 * cuando un boton es presionado, obtenmos su id lo restamos con el numero id del radioboton1 (+1) para asi obtener
		 * el numero del 1 al 35 que debemos encender y para ello el mensaje que debemos enviar al rn42. Y en caso de que
		 * el boton ya fuera presionado, al volver a presionarlo cambiara a no presionado con .setChecker(false)
		 */
 if (estados[ v.getId()-(R.id.radioButton1)+1 ]==true)botones[v.getId()-(R.id.radioButton1)+1].setChecked(false);
 	estados[v.getId()-(R.id.radioButton1)+1]=!estados[v.getId()-(R.id.radioButton1)+1];  
 	sendMessage((v.getId()-(R.id.radioButton1)+1)+"\r");				
	
	}
    
	
	
	public void borrando(){
		for(int i=1; i<=35; i++){//Nos encargamos de verificar en el vector de estados 
    		if(estados[i]==true){ //quien esta en estado alto para asi enviar mensaje al rn42
    			sendMessage(i+"\r"); // y que este led sea apagado, de igual forma hacemos un "reset"
    			botones[i].setChecked(false); //en su estado grafico y vectorial
    			estados[i]=false;
    			}	}
	}
	
	
	
	//<<>><<>><<>><<>><<>><<>><<ANUNCIO>><<>><<>><<>><<>><<>><<>><<>>//
	   
	   private Runnable temporiza = new Runnable() {
		     public void run() { 
		     	/*Este metodo es usado para que en combinacion del handler anuncio
		     	 * sea ejecutado cada 350 milisegundos, donde se encargara de borrar
		     	 * y "dibujar" cada segmento de las letras que componen a TELMEXhub//
		     	 * añadiendo un corrimiento para dar un efecto de movimiento.
		     	 */
		    	 
		    	 borrando(); //primero limpiamos la matriz de led apagandola totalmente
		  		     
		     	switch (contador){
		     	//dependiendo donde vaya el corrimiento sera la letra que vaya encendiendo
		     	case 1:	letraT();		     	
		     	if(corrimiento>=20)letraE();
		     	if(corrimiento>=40)letraL();
		     	if(corrimiento>=60)letraM();
		     	if(corrimiento>=90)letraE2();
		     	if(corrimiento>=110)letraX();
		     	if(corrimiento>=140)letrah();
		     	if(corrimiento>=160)letrau();
		     	if(corrimiento>=180)letrab();
		     	if(corrimiento>=200)letrafinal();
		     	
		     	corrimiento+=5; //y aumentamos el corrimiento para el efecto de movimiento
		     	
		     	if(corrimiento==265){contador=2;corrimiento=0;};//cuando el corrimiento haya permitido
		     	//a todas las letras mostrarse, aumentara el contador para no volver a ejecutar el runnable
		     		break;
		     		
		     	case 2:
		     		break;
		     	
		     	}
		     			     	
		    	anuncio.removeCallbacks(temporiza);
		     	if(contador<2){anuncio.postDelayed(this, 350);}
		     	else {contador=1;corrimiento=0;}
		     }
		   };
			
		   
	   public void letraT(){//"coordenadas" de cada letra. Cada fila es activada o desactivada 		
		if(corrimiento>0 && corrimiento<40){ envio(0,4); }//dependiendo su posicion en la matriz
		if(corrimiento>5 && corrimiento<45){for(int i=1; i<6; i++)envio(i,10); }//de leds del circuito
		if(corrimiento>10 && corrimiento<50){envio(1,15);}		   
	   }
	   	   
	   public void letraE(){	   		   
	if(corrimiento>20 && corrimiento<60){for(int i=1; i<6; i++)envio(i,25); }
	if(corrimiento>25 && corrimiento<65){for(int i=1; i<7; i+=2)envio(i,30); }
	if(corrimiento>30 && corrimiento<70){for(int i=1; i<6; i+=4)envio(i,35); }	   
	   }
	   	   
	   public void letraL(){	   		   
	if(corrimiento>40 && corrimiento<80){for(int i=1; i<6; i++)envio(i,45); }
	if(corrimiento>45 && corrimiento<85){envio(5,50);}
	if(corrimiento>50 && corrimiento<90){envio(5,55);}	   
	   }
	   
	   public void letraM(){	   		   
	if(corrimiento>60 && corrimiento<100){for(int i=1; i<6; i++)envio(i,65); }
	if(corrimiento>65 && corrimiento<105){envio(2,70);} 
	if(corrimiento>70 && corrimiento<110){envio(3,75);} 	  
	if(corrimiento>75 && corrimiento<115){envio(2,80);} 
	if(corrimiento>80 && corrimiento<120){for(int i=1; i<6; i++)envio(i,85); }
	   }
	   
	   public void letraE2(){	   		   
	if(corrimiento>90 && corrimiento<130){for(int i=1; i<6; i++)envio(i,95); }
	if(corrimiento>95 && corrimiento<135){for(int i=1; i<7; i+=2)envio(i,100); }
	if(corrimiento>100 && corrimiento<140){for(int i=1; i<6; i+=4)envio(i,105);}	   
	   }
	   
	   public void letraX(){	   		   
	if(corrimiento>110 && corrimiento<150){for(int i=1; i<6; i+=4)envio(i,115); }
	if(corrimiento>115 && corrimiento<155){for(int i=2; i<6; i+=2)envio(i,120); }
	if(corrimiento>120 && corrimiento<160){envio(3,125);}   
	if(corrimiento>125 && corrimiento<165){for(int i=2; i<6; i+=2)envio(i,130); }
	if(corrimiento>130 && corrimiento<170){for(int i=1; i<6; i+=4)envio(i,135); }	 
	   }
	   
	   public void letrah(){	   		   
	if(corrimiento>140 && corrimiento<180){for(int i=1; i<6; i++)envio(i,145); }
	if(corrimiento>145 && corrimiento<185){envio(3,150);} 
	if(corrimiento>150 && corrimiento<190){for(int i=3; i<6; i++)envio(i,155); }  
	   }
	   
	   public void letrau(){	   		   
	if(corrimiento>160 && corrimiento<200){for(int i=3; i<6; i++)envio(i,165); }
	if(corrimiento>165 && corrimiento<205){envio(5,170);} 
	if(corrimiento>170 && corrimiento<210){for(int i=3; i<6; i++)envio(i,175); }  
	   }
	   
	   public void letrab(){	   		   
	if(corrimiento>180 && corrimiento<220){for(int i=1; i<6; i++)envio(i,185); }
	if(corrimiento>185 && corrimiento<225){for(int i=3; i<6; i+=2)envio(i,190); }
	if(corrimiento>190 && corrimiento<230){for(int i=3; i<6; i++)envio(i,195); }  
	   }
	   
	   public void letrafinal(){	   		   
	if(corrimiento>200 && corrimiento<240){envio(5,205);} 
	if(corrimiento>205 && corrimiento<245){envio(3,210);} 
	if(corrimiento>210 && corrimiento<250){for(int i=1; i<6; i+=4)envio(i,215);} 
	if(corrimiento>215 && corrimiento<255){envio(3,220);} 
	if(corrimiento>220 && corrimiento<260){envio(1,225);} 
	   }	   
	
	
	public void envio(int i,int resta){	//cada letra debera enviar su "mensaje" al modulo rn42 para activar
		//el led correspondiente en la matriz de leds, asi como cambiar su estado (alto/bajo) y su estado grafico
		sendMessage((i+corrimiento-resta)+"\r");
		estados[i+corrimiento-resta]=true;
		botones[i+corrimiento-resta].toggle();
	}
	
	
	
	
	
}//Fin MainActivity

