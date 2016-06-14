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
		
		RelayServer.log(myPhoneNumber+" ����Ʈ���� ����");
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

			myIP=socket.getInetAddress().toString();// �����̵� ������ ����� (/192.168.0.9)
			myIP=myIP.substring(1);// �����̵� ����
			
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
							
							//�ߺ��� ������ �ִ��� Ȯ��
							for(User other: userList){
								if(myPhoneNumber.matches(other.myPhoneNumber) && createdTime-other.createdTime>200){
									RelayServer.log("PhoneNumber: " + myPhoneNumber + " �������� �ߺ��Ǿ� ����");
									
									other.remove();//���� ������ ��ü ����
									
									break;
								}
							}
							
							//������ user��� ��ġ�Ǵ°� �ִ��� Ȯ��
							for(User other: userList){
								RelayServer.log("��ġ");

								if(other.myPhoneNumber.matches(partnerPhoneNumber) && other.partnerPhoneNumber.matches(myPhoneNumber)){//���� ���߿� ������ ������
										
									if(permit==true && other.permit==true){//���� ��� ��� ������ ���ϴ� ���
										other.out.writeUTF(""+DATA_TYPE_MODE+SERVER_MODE);//���� ���� ����
										other.out.flush();
											
										out.writeUTF(""+DATA_TYPE_MODE+CLIENT_MODE);//Ŭ���̾�Ʈ ���� ����
										out.flush();
										out.writeUTF(""+DATA_TYPE_IP+other.myIP);//���� ������ ������ �����Ǹ� ����
										out.flush();
										
										RelayServer.log(other.myPhoneNumber+"���� ���� ���� ����");
										RelayServer.log(myPhoneNumber+"���� Ŭ���̾�Ʈ ���� ����");
									}
									else if(permit==false && other.permit==true){//���� ������ ������ �ʴ� ���
										out.writeUTF(""+DATA_TYPE_MODE+CANCEL_MODE);
										out.flush();
										
										other.out.writeUTF(""+DATA_TYPE_MODE+CANCEL_MODE);
										other.out.flush();
										
										RelayServer.log(myPhoneNumber+"�� "+other.myPhoneNumber+"���� VT�� ������ ����");
									}
									else{//��밡 ������ ������ ���� ���
										out.writeUTF(""+DATA_TYPE_MODE+CANCEL_MODE);
										out.flush();
										
										RelayServer.log(other.myPhoneNumber+"�� "+myPhoneNumber+"���� VT�� ������ ����");
									}
									
									other.isMatch=true;
									isMatch=true;
									
									removeWithPartner(other);
	
									terminated=true;
									break;
								}
							}
								
							if(isMatch==false){//���� ���� ������ �������
								if(permit==true){//���� ������ ���ϸ�
									out.writeUTF(""+DATA_TYPE_MODE+WATING_MODE);//����� �ǻ縦 Ȯ���� ������ ���
									out.flush();
										
									RelayServer.log(myPhoneNumber+"�� ������ VT�� ���ϸ� ����� �ǻ���� �����");
								}
								else{//������ ������ ������ �ʴ´ٸ�
									out.writeUTF(""+DATA_TYPE_MODE+CANCEL_MODE);
									out.flush();
										
									RelayServer.log(myPhoneNumber+"�� VT ������ ������ ����");
								}
							}
								
							terminated=true;
						}
						else{
							RelayServer.log("�����κ��� �߸��� Ÿ���� �����Ͱ� ���۵�");
						}
					}
					else{
						sleep(300);
					}
				}
			}catch(EOFException e){
				//���������� ������� ���� ���¿��� ���� ����� �߻�
				e.printStackTrace();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}