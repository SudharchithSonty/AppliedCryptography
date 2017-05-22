package com;
public class ServerThread extends Thread
{
	Main server;
public ServerThread(Main server){
	this.server=server;
	start();
}
public void run(){
	server.start();
}
}