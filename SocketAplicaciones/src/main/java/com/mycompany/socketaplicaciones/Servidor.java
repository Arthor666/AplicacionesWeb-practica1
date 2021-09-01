/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.socketaplicaciones;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import com.mycompany.socketaplicaciones.Constantes;
import java.io.File;
import java.io.FileOutputStream;

/**
 *
 * @author Arturo
 */
public class Servidor {
    public static void main(String args[]) throws IOException, ClassNotFoundException{
        ServerSocket serverSocket = new ServerSocket(Constantes.PTO);
        while(true){
            Socket cs ;
            System.out.println("Esperando...");
            cs = serverSocket.accept();
            System.out.println("Cliente en línea");
            boolean repeat = true;
            DataOutputStream salidaCliente = new DataOutputStream(cs.getOutputStream());
            ObjectInputStream dataInputStream = new ObjectInputStream(cs.getInputStream()); 
                Object object = dataInputStream.readObject();
                File userFolder; 
                if(object instanceof String){        
                    String instruccion = (String) object;
                    File thisFolder;
                    switch(instruccion) {
                    case Constantes.MKDIR:
                    	String folderName = (String) dataInputStream.readObject();
                        System.out.println("Creando carpeta: "+ folderName);
                        thisFolder = new File("");
                        userFolder = new File(thisFolder.getAbsolutePath()+cs.getInetAddress());
                        if(!userFolder.exists()){
                            userFolder.mkdir();
                        }
                        File newFolder = new File(userFolder.getAbsolutePath()+"\\"+folderName);
                        newFolder.mkdir();                                                
                        System.out.println("Carpeta creada :"+folderName);
                    break;
                    case Constantes.EXIT:
                    	repeat = false;
                    break;
                    case Constantes.MKFILE:
                    	String targetPath = (String) dataInputStream.readObject();
                    	thisFolder = new File("");
                    	userFolder = new File(thisFolder.getAbsolutePath()+cs.getInetAddress());
                        if(!userFolder.exists()){
                            userFolder.mkdir();
                        }
                    	String fileName = (String) dataInputStream.readObject();
                    	String userIp = cs.getInetAddress().toString().replace('/','\\')+"\\";
                    	File newAuxFile = new File(thisFolder.getAbsolutePath()+userIp+targetPath+fileName);
                    	newAuxFile.createNewFile();
                    	DataOutputStream dos = new DataOutputStream(new FileOutputStream(thisFolder.getAbsolutePath()+userIp+targetPath+fileName));                    	
                    	Long tamanio =  dataInputStream.readLong();
                    	long recibidos =0;
                    	int aux = 0,porcentaje =0;
                    	while(recibidos<tamanio) {
                    		byte [] buffer = new byte[1500];
                    		aux = dataInputStream.read(buffer);
                    		dos.write(buffer,0,aux);
                    		dos.flush();
                    		recibidos += aux;
                    		porcentaje = (int)(recibidos*100/tamanio);
                    		System.out.println(fileName+" recibed:"+porcentaje+"%");
                    	}
                    	System.out.println("Archivo cargado"+fileName);
                    	dos.close();                    	
                    	
                    break;
                    }
                }
  
            System.out.println("Fin de la conexión");
            cs.close();
            dataInputStream.close();
            salidaCliente.close();
        }
        
    }
}
