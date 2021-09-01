/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.socketaplicaciones;

import java.io.DataInputStream;
/**
 *
 * @author Arturo
 */
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.JFileChooser;
import org.apache.commons.io.FilenameUtils;


public class Client {
   public static void main(String args[]) throws IOException{   
       JFileChooser jfileChooser = new JFileChooser();
       jfileChooser.setMultiSelectionEnabled(true);
       jfileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
       int selected = jfileChooser.showOpenDialog(null);      
       if(selected == JFileChooser.APPROVE_OPTION){
          File [] files = jfileChooser.getSelectedFiles();
          for(File folder :files){
              sendFolder(folder,"");
          }
       }
   }
   
   public static void sendFolder(File folder,String serverLocation) throws IOException{
       String extension = FilenameUtils.getExtension(folder.getName());
       if(!extension.isBlank()){
           sendFile(folder,serverLocation);
       }else{
    	   serverLocation += folder.getName()+"\\";
    	   createFolderInServer(folder, serverLocation); 
           for(File internal :folder.listFiles()){
        	   sendFolder(internal,serverLocation );
           }
       }
   }
   
   public static void createFolderInServer(File folder,String serverLocation) throws IOException{
	   Socket socket = new Socket(Constantes.IP,Constantes.PTO);
       ObjectOutputStream d = new ObjectOutputStream(socket.getOutputStream());
       d.writeObject(Constantes.MKDIR);
       d.flush();
       d.writeObject(serverLocation);
       d.flush();
       d.close();
   }
   
   public static void sendFile(File file,String serverLocation) throws IOException{
	   Socket socket = new Socket(Constantes.IP,Constantes.PTO);
	   ObjectOutputStream d = new ObjectOutputStream(socket.getOutputStream());
	   DataInputStream dis = new DataInputStream(new FileInputStream(file));
	   Long tam = file.length();
	   d.writeObject(Constantes.MKFILE);
	   d.flush();
	   d.writeObject(serverLocation);
	   d.flush();
	   d.writeObject(file.getName());
	   d.flush();
	   d.writeLong(tam);
	   d.flush();
	   long enviados = 0;
	   int l=0,porcentaje =0;	   
	   while(enviados<tam) {
		  byte [] buffer = new byte[1500];
		  l = dis.read(buffer);
		  d.write(buffer,0,l);
		  d.flush();
		  enviados += l;
		  porcentaje = (int) ((enviados*100)/tam);
		  System.out.println(file.getName()+" enviado: %"+porcentaje);
	  }
	   d.close();
	   dis.close();
       
   }
   
   public static void pack(String sourceDirPath, String zipFilePath) throws IOException {
    Path p = Files.createFile(Paths.get(zipFilePath));
    try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {
        Path pp = Paths.get(sourceDirPath);
        Files.walk(pp)
          .filter(path -> !Files.isDirectory(path))
          .forEach(path -> {
              ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
              try {
                  zs.putNextEntry(zipEntry);
                  Files.copy(path, zs);
                  zs.closeEntry();
            } catch (IOException e) {
                System.err.println(e);
            }
          });
    }
}
}
