import java.io.*;
import java.net.*;
import java.util.LinkedList;

public class User {
	static final char DATA_TYPE_MODE='M';
	static final char DATA_TYPE_IP='I';
	static final char DATA_TYPE_PHONENUMBER_AND_PERMIT='D';
	
	static final int SERVER_MODE=100;
	static final int CLIENT_MODE=200;
	static final int WATING_MODE=300;
	static final int CANCEL_MODE=400;
	
	String myIP;
	String myPhoneNumber;
	String partnerPhoneNumber;
	
	boolean permit;
	boolean isMatch;
	long createdTime;
	
	LinkedList<User> userList;
	
	Socket socket;
	Connection connection;
	DataInputStream in;
	DataOutputStream out;
	
	
	User(Socket s, LinkedList<User> ul){
		socket=s;
		userList=ul;
		connection= new Connection();
		isMatch=false;
		createdTime=System.currentTimeMillis();
		
		try {
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void remove(){
		try {
			in.close();
			out.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		connection.terminate();

		userList.remove(this);
		
		RelayServer.log(myPhoneNumber+" 리스트에서 삭제");
	}
	
	void removeWithPartner(User partner){
		partner.remove();
		remove();
	}
	
	//inner class
	class Connection extends Thread{
		
		boolean terminated;
		
		Connection(){
			terminated=false;
		}
		
		public void terminate(){
			terminated=true;
		}
		
		public void run(){
			String received;

			myIP=socket.getInetAddress().toString();// 슬라이드 포함해 저장됨 (/192.168.0.9)
			myIP=myIP.substring(1);// 슬라이드 제거
			
			try{
				while(!terminated){
					if(in.available()!=0 && (received = in.readUTF()) != null){
						
						if(received.charAt(0)==DATA_TYPE_PHONENUMBER_AND_PERMIT){
							myPhoneNumber=received.substring(1, received.indexOf("/"));
							partnerPhoneNumber=received.substring(received.indexOf("/")+1, received.lastIndexOf("/"));
							permit=Boolean.parseBoolean(received.substring(received.lastIndexOf("/")+1));

							RelayServer.log("myPhoneNumber: " + myPhoneNumber);
							if(myPhoneNumber.charAt(0)=='+'){
								myPhoneNumber='0' + myPhoneNumber.substring(3);
							}
							RelayServer.log("myPhoneNumber: " + myPhoneNumber);
							
							//중복된 유저가 있는지 확인
							for(User other: userList){
								if(myPhoneNumber.matches(other.myPhoneNumber) && createdTime-other.createdTime>200){
									RelayServer.log("PhoneNumber: " + myPhoneNumber + " 서버에서 중복되어 삭제");
									
									other.remove();//먼저 생성된 객체 삭제
									
									break;
								}
							}
							
							//기존의 user들과 매치되는게 있는지 확인
							for(User other: userList){
								RelayServer.log("서치");

								if(other.myPhoneNumber.matches(partnerPhoneNumber) && other.partnerPhoneNumber.matches(myPhoneNumber)){//내가 나중에 접속한 유저면
										
									if(permit==true && other.permit==true){//나와 상대 모두 연결을 원하는 경우
										other.out.writeUTF(""+DATA_TYPE_MODE+SERVER_MODE);//서버 역할 전달
										other.out.flush();
											
										out.writeUTF(""+DATA_TYPE_MODE+CLIENT_MODE);//클라이언트 역할 전달
										out.flush();
										out.writeUTF(""+DATA_TYPE_IP+other.myIP);//서버 역할인 유저의 아이피를 전달
										out.flush();
										
										RelayServer.log(other.myPhoneNumber+"에게 서버 역할 전달");
										RelayServer.log(myPhoneNumber+"에게 클라이언트 역할 전달");
									}
									else if(permit==false && other.permit==true){//나만 연결을 원하지 않는 경우
										out.writeUTF(""+DATA_TYPE_MODE+CANCEL_MODE);
										out.flush();
										
										other.out.writeUTF(""+DATA_TYPE_MODE+CANCEL_MODE);
										other.out.flush();
										
										RelayServer.log(myPhoneNumber+"가 "+other.myPhoneNumber+"와의 VT를 원하지 않음");
									}
									else{//상대가 연결을 원하지 않을 경우
										out.writeUTF(""+DATA_TYPE_MODE+CANCEL_MODE);
										out.flush();
										
										RelayServer.log(other.myPhoneNumber+"가 "+myPhoneNumber+"와의 VT를 원하지 않음");
									}
									
									other.isMatch=true;
									isMatch=true;
									
									removeWithPartner(other);
	
									terminated=true;
									break;
								}
							}
								
							if(isMatch==false){//내가 먼저 접속한 유저라면
								if(permit==true){//상대와 연결을 원하면
									out.writeUTF(""+DATA_TYPE_MODE+WATING_MODE);//상대의 의사를 확인할 때까지 대기
									out.flush();
										
									RelayServer.log(myPhoneNumber+"는 상대와의 VT를 원하며 상대의 의사결정 대기중");
								}
								else{//상대와의 연결을 원하지 않는다면
									out.writeUTF(""+DATA_TYPE_MODE+CANCEL_MODE);
									out.flush();
										
									RelayServer.log(myPhoneNumber+"가 VT 실행을 원하지 않음");
								}
							}
								
							terminated=true;
						}
						else{
							RelayServer.log("유저로부터 잘못된 타입의 데이터가 전송됨");
						}
					}
					else{
						sleep(300);
					}
				}
			}catch(EOFException e){
				//유저끼리의 연결되지 않은 상태에서 어플 종료시 발생
				e.printStackTrace();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}