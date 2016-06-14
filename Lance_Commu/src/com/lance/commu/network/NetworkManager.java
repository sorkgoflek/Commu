package com.lance.commu.network;


import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.LinkedList;

import android.util.Log;

public class NetworkManager extends Thread{
	public static String RELAY_SERVER_IP=ServerIP.RELAY_SERVER;
	public static int RELAY_SERVER_PORT=ServerIP.RELAY_SERVER_PORT;
	public static int SERVER_USER_PORT=9999;	
	
	public static int CONNECT_SUCCESS=1;
	public static int FAIL_TO_CONNECT_WITH_RELAYSERVER=0;
	public static int FAIL_TO_GET_A_ROLE=-1;
	public static int FAIL_TO_ACCEPT_WITH_TIMEOUT=-2;
	public static int FAIL_TO_GET_PARTNER_IP=-3;
	public static int FAIL_TO_CONNECT_WITH_SERVER_USER=-4;
	public static int FAIL_WITH_CANCEL_MODE=-5;
	public static int FAIL_WITH_ILLIGAL_MODE=-6;
	
	static final char DATA_TYPE_MODE='M';
	static final char DATA_TYPE_PHONENUMBER_AND_PERMIT='D';
	static final char DATA_TYPE_IP='I';
	
	static final char DATA_TYPE_FILE='L';
	static final char DATA_TYPE_FILE_ING='A';
	static final char DATA_TYPE_FILE_END='B';

	public static final int SERVER_MODE=100;
	public static final int CLIENT_MODE=200;
	static final int WATING_MODE=300;
	static final int CANCEL_MODE=400;
	
	static NetworkManager nm=null;
	
	public boolean permit;
	
	ServerSocket serverSocket;
	Socket socket; 
	DataInputStream in;
	DataOutputStream out;
	
	public int myRole;
	String myIP;
	String previous_myIP;
	String partnerIP;
	
	public static String myPhoneNumber;
	public static String partnerPhoneNumber;
	
	public LinkedList<String> dataList;
	
	boolean running;
	
	private NetworkManager(){
		myRole=0;
		
		myIP=getMyIP();
		
		if(myIP==null){
			Log.e("NetworkWithVT", "내 아이피 확인 불가");
			terminate();
			return;
		}
		
		previous_myIP=myIP;
		running=false;
		
		this.dataList=new LinkedList<String>();
		
		Log.i("NetworkWithVT", "내 아이피: "+myIP);
	}

	private NetworkManager(LinkedList<String> dataList){
		myRole=0;
		
		myIP=getMyIP();
		
		if(myIP==null){
			Log.e("NetworkWithVT", "내 아이피 확인 불가");
			terminate();
			return;
		}
		
		previous_myIP=myIP;
		running=false;
		
		this.dataList=dataList;
		
		Log.i("NetworkWithVT", "내 아이피: "+myIP);
	}
	
	public static NetworkManager getInstance(){
		if(nm==null){
			nm = new NetworkManager();
		}
		
		return nm;
	}
	
	public void terminate(){
		running=false;
		
		if(nm!=null){
			nm=null;
		}
	}
	
	public boolean isRunning(){
		return running;
	}
	
	String getMyIP(){
		String IP=null;
		
		//기기의 아이피 주소 확인
		try {
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
			
			while (en.hasMoreElements()) {
				NetworkInterface interf = en.nextElement();
				Enumeration<InetAddress> ips = interf.getInetAddresses();
				
				while (ips.hasMoreElements()) {
					InetAddress inetAddress = ips.nextElement();
			
					if (!inetAddress.isLoopbackAddress()) {
						IP=inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		
		return IP;
	}
	
	boolean connectWithRelayServer(){
		try {
			SocketAddress socketAddress = new InetSocketAddress(RELAY_SERVER_IP, RELAY_SERVER_PORT);
			
			socket=new Socket();     
			socket.connect(socketAddress, 5000); 

			Log.i("NetworkWithVT", "중계서버 연결");
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (ConnectException e1) {
			Log.i("NetworkWithVT", "중계서버 연결실패-NetworkManager");
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		if(socket!=null && socket.isConnected()){
			try {
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return true;
		}
		else{
			Log.e("NetworkWithVT", "중계서버 연결 실패");
			return false;
		}
	}
	
	String getRole(){
		String received=null;
		
		if(myPhoneNumber != null && partnerPhoneNumber != null){
			try {
				out.writeUTF(""+DATA_TYPE_PHONENUMBER_AND_PERMIT+myPhoneNumber+'/'+partnerPhoneNumber+'/'+permit);
				out.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		else{
			Log.i("NetworkWithVT", "myPhoneNumber == null || partnerPhoneNumber == null");
		}
		
		while(received==null){
			Log.i("NetworkWithVT", "역할 대기중");
			
			try {
				received = in.readUTF();//
			} catch (SocketException e) {
				e.printStackTrace();
				
				//closeAll();
				received=null;
				
				//break;
			}catch (IOException e) {
				e.printStackTrace();
			}
			
			Log.i("NetworkWithVT", received+" 역할 수신");
			
			if(received!=null && received.matches(""+DATA_TYPE_MODE+WATING_MODE)){//대기 모드일 경우
				received=null;//다시 역할을 받을 수 있도록 함
			}
			else{
				break;
			}
		}
		
		return received;
	}
	
	public int connectWithPartner(){
		String received=null;
		
		//중계서버에 연결
		if(!connectWithRelayServer()){
			closeAll();
			return FAIL_TO_CONNECT_WITH_RELAYSERVER;
		}
		
		//서버에서 역할을 수신받음
		received=getRole();
		
		if(received == null){
			closeAll();
			return FAIL_TO_GET_A_ROLE;
		}
		else if(/*received!=null &&*/ received.charAt(0)==DATA_TYPE_MODE){
			
			switch(/*myRole=*/Integer.parseInt(received.substring(1))){
				case SERVER_MODE:
					Log.i("NetworkWithVT", "서버 역할");
					
					try {
						socket.close();
						serverSocket = new ServerSocket(SERVER_USER_PORT);
						Log.i("NetworkWithVT", "클라이언트의 접속 대기");
						serverSocket.setSoTimeout(15000);
						socket = serverSocket.accept();
						Log.i("NetworkWithVT", "클라이언트 접속");
					} catch (SocketTimeoutException e) {
						e.printStackTrace();
						closeAll();
						return FAIL_TO_ACCEPT_WITH_TIMEOUT;
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					try {
						in = new DataInputStream(socket.getInputStream());
						out = new DataOutputStream(socket.getOutputStream());
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					myRole=Integer.parseInt(received.substring(1));
					break;
					
				case CLIENT_MODE:
					Log.i("NetworkWithVT", "클라이언트 역할");
					
					//파트너의 아이피 수신 대기
					while(partnerIP==null){
						Log.i("NetworkWithVT", "파트너 아이피 수신 대기중");
						try {
							partnerIP = in.readUTF();
							break;
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					if(partnerIP.charAt(0)==DATA_TYPE_IP){
						partnerIP=partnerIP.substring(1);
						Log.i("NetworkWithVT", "partnerIP: "+partnerIP);
					}
					else{
						Log.e("NetworkWithVT", "파트너 아이피 전달 못받음");
						closeAll();
						return FAIL_TO_GET_PARTNER_IP;
					}
					
					try {
						socket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
					for(long firstConnectionAttemptTime=System.currentTimeMillis();;){//서버 유저와 연결이 될 때까지 반복
						
						if(System.currentTimeMillis()-firstConnectionAttemptTime >= 7*1000){//7초동안 접속 시도해서 접속이 안되면 접속 취소
							Log.e("NetworkWithVT", "서버 유저와 연결중 접속 시도 시간 초과");
							closeAll();
							return FAIL_TO_CONNECT_WITH_SERVER_USER;
						}
						
						try{
							socket=new Socket(partnerIP, SERVER_USER_PORT);
							Log.i("NetworkWithVT", "서버 유저와 연결");
							break;
						} catch (ConnectException e) {//서버 유저의 준비가 안되어있어 연결을 못 할 경우	
							Log.i("NetworkWithVT", "서버 유저와 연결중 ConnectException 발생");
							//e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
					try {
						in = new DataInputStream(socket.getInputStream());
						out = new DataOutputStream(socket.getOutputStream());
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					myRole=Integer.parseInt(received.substring(1));
					break;
					
				case CANCEL_MODE:
					Log.i("NetworkWithVT", "VT 실행 취소");
					terminate();
					
					myRole=Integer.parseInt(received.substring(1));
					return FAIL_WITH_CANCEL_MODE;
					
				default:
					Log.e("NetworkWithVT", "역할 할당 못받음");
					terminate();
					return FAIL_WITH_ILLIGAL_MODE;
			}
		}
		
		return CONNECT_SUCCESS;
	}
	
	public void closeAll(){
		dataList.clear();
		
		myPhoneNumber=null;
		partnerPhoneNumber=null;
		
		if(serverSocket!=null){
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(socket!=null){
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(in!=null){
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}		}
		
		if(out!=null){
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean sendData(char dataType, String data){
		if(socket!=null && socket.isClosed()==false){
			if(myRole==SERVER_MODE || myRole==CLIENT_MODE){

				if(dataType==DATA_TYPE_FILE){
					
					for(int count=0 ;  ; count++){//60000바이트씩 계속 보냄
						if(data.length() > 60000){
							String temp=data.substring(0, 60000);
							
							if(count != 0){
								dataType=DATA_TYPE_FILE_ING;
							}
							
							try {
								out.writeUTF(dataType + temp);
							} catch (IOException e) {
								e.printStackTrace();
							}
							
							Log.i("NetworkWithVT", "sendData: "+dataType +" "+ data.length()+" byte");
							Log.i("NetworkWithVT", "sendData: "+dataType +" "+ temp);
							
							data=data.substring(60000);
						}
						else{//나머지
							dataType=DATA_TYPE_FILE_END;
							
							try {
								out.writeUTF(dataType + data);
								out.flush();
							} catch (IOException e) {
								e.printStackTrace();
							}
							
							Log.i("NetworkWithVT", "sendData: "+dataType +" "+ data.length()+" byte");
							break;
						}	
					}
				}
				else{
					try {
						out.writeUTF(dataType + data);
						out.flush();
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					Log.i("NetworkWithVT", "sendData: "+dataType + data);
				}
				
				return true;
			}
			else{
				Log.e("NetworkWithVT", "상대 유저와 연결되지 않았습니다.");
				return false;
			}
		}
		else{
			Log.e("NetworkWithVT", "sendData: socket이 닫혔습니다.");
			return false;
		}
	}
	
	public void run(){
		running=true;
		
		String data = null;
			
		while(running){
			try {
				if(in.available()!=0){
					Log.i("NetworkWithVT", "in.available(): "+in.available());
					try {
						data=in.readUTF();
					} catch (Exception e){}
				}
				else{
					yield();
				}
			} catch (SocketException e1) {
				e1.printStackTrace();
				terminate();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
				
			if(data!=null){
				synchronized(dataList){
					dataList.add(data);//새로 들어온 데이터 추가
				}
					
				Log.i("NetworkWithVT", data + " data read");
					
				data=null;
			}
		}
		
		//종료시 모든것을 close
		closeAll();
		
		Log.i("NetworkWithVT", "nm의 run 종료");
	}
}