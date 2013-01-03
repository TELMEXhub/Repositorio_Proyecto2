#include "C:\Android\Thub\Proyecto2\PicMatrix\Matrix.h"
#include <string.h>
#byte TRISA = 85  // direccion del registro trisA
#byte TRISB = 86 //direcion del registro trisB
#byte puerto_a = 05   // direccion del puerto A
#byte puerto_b = 06  //direccion del puerto B

BYTE salidaA1=0xff;  //Variables donde se almacenara el 
BYTE salidaA2=0xff;  //estado de los leds dependiendo
BYTE salidaA3=0xff;  //la columna a la que correspondan
BYTE salidaA4=0xff;
BYTE salidaA5=0xff;


#int_RDA
void  RDA_isr(void) {
   //Interrupcion generada por un dato entrante en el puerto USART
 char entrada[3]="\0";
   fgets(entrada,BLUE); //GUARDAMOS EN ENTRADA LO QUE HAY EN LA USART
   //leemos lo que hay a la entrada
   char led1[2]="1",led2[2]="2",led3[2]="3",led4[2]="4",led5[2]="5",
   led6[2]="6",led7[2]="7",led8[2]="8",led9[2]="9",led10[3]="10",
   led11[3]="11",led12[3]="12",led13[3]="13",led14[3]="14",led15[3]="15",
   led16[3]="16",led17[3]="17",led18[3]="18",led19[3]="19",led20[3]="20",
   led21[3]="21",led22[3]="22",led23[3]="23",led24[3]="24",led25[3]="25",
   led26[3]="26",led27[3]="27",led28[3]="28",led29[3]="29",led30[3]="30",
   led31[3]="31",led32[3]="32",led33[3]="33",led34[3]="34",led35[3]="35";
   //Y comparamos para encender la fila correspondiente a la columna especificada
   if(strcmp(entrada,led1)==0)salidaA1^=0x02;
   else if(strcmp(entrada,led2)==0)salidaA2^=0x02;
   else if(strcmp(entrada,led3)==0)salidaA3^=0x02;
   else if(strcmp(entrada,led4)==0)salidaA4^=0x02;
   else if(strcmp(entrada,led5)==0)salidaA5^=0x02;
   
      else if(strcmp(entrada,led6)==0)salidaA1^=0x04;
   else if(strcmp(entrada,led7)==0)salidaA2^=0x04;
   else if(strcmp(entrada,led8)==0)salidaA3^=0x04;
   else if(strcmp(entrada,led9)==0)salidaA4^=0x04;
   else if(strcmp(entrada,led10)==0)salidaA5^=0x04;
   
      else if(strcmp(entrada,led11)==0)salidaA1^=0x08;
   else if(strcmp(entrada,led12)==0)salidaA2^=0x08;
   else if(strcmp(entrada,led13)==0)salidaA3^=0x08;
   else if(strcmp(entrada,led14)==0)salidaA4^=0x08;
   else if(strcmp(entrada,led15)==0)salidaA5^=0x08;
   
      else if(strcmp(entrada,led16)==0)salidaA1^=0x10;
   else if(strcmp(entrada,led17)==0)salidaA2^=0x10;
   else if(strcmp(entrada,led18)==0)salidaA3^=0x10;
   else if(strcmp(entrada,led19)==0)salidaA4^=0x10;
   else if(strcmp(entrada,led20)==0)salidaA5^=0x10;
   
      else if(strcmp(entrada,led21)==0)salidaA1^=0x01;
   else if(strcmp(entrada,led22)==0)salidaA2^=0x01;
   else if(strcmp(entrada,led23)==0)salidaA3^=0x01;
   else if(strcmp(entrada,led24)==0)salidaA4^=0x01;
   else if(strcmp(entrada,led25)==0)salidaA5^=0x01;
   
      else if(strcmp(entrada,led26)==0)salidaA1^=0x40;
   else if(strcmp(entrada,led27)==0)salidaA2^=0x40;
   else if(strcmp(entrada,led28)==0)salidaA3^=0x40;
   else if(strcmp(entrada,led29)==0)salidaA4^=0x40;
   else if(strcmp(entrada,led30)==0)salidaA5^=0x40;
   
      else if(strcmp(entrada,led31)==0)salidaA1^=0x80;
   else if(strcmp(entrada,led32)==0)salidaA2^=0x80;
   else if(strcmp(entrada,led33)==0)salidaA3^=0x80;
   else if(strcmp(entrada,led34)==0)salidaA4^=0x80;
   else if(strcmp(entrada,led35)==0)salidaA5^=0x80;   
}//fin de metodo interrupcion rda


void main() {  
   setup_comparator(NC_NC_NC_NC);//DESACTIVAMOS COMPARADORES
   setup_vref(FALSE);
   enable_interrupts(INT_RDA); //POR PUERTO DE COMUNICACIONES 
   enable_interrupts(GLOBAL);  //E INTERRUPCIONES GLOBALES

   set_tris_a( 0x20); //puerto A como  0000 0001nput >> cambia por 00x0 0000 = 0x20
   set_tris_b(0x03); //puerto B como 00000(columnas)/0(tx)1(rx)1(rb0)   
   
   puerto_a = 0xfe; //limpiamos puerto, encendemos filas con ceros
   puerto_b = 0x00; // limpiamos puerto, excitamos transistores con 1

   putc('Z'); //usaos por primera vez el puerto de comunicaciones

   while(true){     //Ciclo infinito en espera de interrupciones 

  puerto_a=salidaA1;puerto_b=0x08;delay_ms(2);  //activamos cada columna por un intervalo de 2ms
   puerto_a=salidaA2;puerto_b=0x10;delay_ms(2); //y cambiamos a la siguiente, para de esta forma
    puerto_a=salidaA3;puerto_b=0x20;delay_ms(2); //generar un efecto de POV en la matriz de leds.
     puerto_a=salidaA4;puerto_b=0x40;delay_ms(2);
      puerto_a=salidaA5;puerto_b=0x80;delay_ms(2);
            }//FIN 

              }                                
 
