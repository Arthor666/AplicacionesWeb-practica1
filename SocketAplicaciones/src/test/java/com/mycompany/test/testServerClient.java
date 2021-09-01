package com.mycompany.test;

import java.io.IOException;

import org.junit.Test;

import com.mycompany.socketaplicaciones.Client;
import com.mycompany.socketaplicaciones.Servidor;

public class testServerClient extends Thread {
	
	
	@Test
	public void runClient() throws ClassNotFoundException, IOException {
		Client.main(null);
	}
	
	@Test
	public void runService() throws ClassNotFoundException, IOException {
		Servidor.main(null);
	}
	

}
