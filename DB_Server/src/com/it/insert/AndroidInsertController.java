package com.it.insert;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;

import com.it.member.MemberDao;
import com.it.member.MemberVo;

@Controller
public class AndroidInsertController {
	
	private static MemberDao memberDaoImpl;
	
	public AndroidInsertController(MemberDao memberDaoImpl){
		this.memberDaoImpl = memberDaoImpl;
	}
	
	public static String androidInsertData(String id, String pass, String name, String phone_number, String gender, String dir){
		
		MemberVo vo = new MemberVo();
		vo.setId(id);
		vo.setPass(pass);
		vo.setName(name);
		vo.setPhone_number(phone_number);
		vo.setGender(gender);
		
		String dbId = memberDaoImpl.selectIdCheck(vo.getId()); //���̵� �ߺ� üũ
		//System.out.println("���̵� �ߺ�üũ:"+dbId);
		
		String dbPhoneNumber = memberDaoImpl.selectPhoneNumberCheck(vo.getPhone_number()); //�ڵ��� ��ȣ �ߺ� üũ
		//System.out.println("���ѹ� üũ:"+dbPhoneNumber);
		
		if(dbId == null){ //���� ������ ���̵� �����ϴϱ� ��� ��������
			if(dbPhoneNumber == null){// ���ݹ�ȣ�� �ߺ��� �ƴ϶�� �� = ó�� ���������Ѵٴ� ��
				memberDaoImpl.insertMember(vo);//��� �����ϰ� 
			
				//���⼭ ���κ� ������ ����������� 
				File parent = new File(dir+"/"+id); //���κ��� ������ ����� 
				if(!parent.exists()){ //���丮�� ������ 
					parent.mkdir(); //���丮 ����� 
				}	
				System.out.println("(�ȵ���̵�) ���̵� ��밡��");
				return "possi";
			}
			else{//���̵�� ���ð����ѵ� ��ȣ�� �ߺ��̸� 
				System.out.println("(�ȵ���̵�) ���̵�� ��밡�������� ��ȣ�ߺ�");
				return "duphn";
			}
		}
		else{
			System.out.println("(�ȵ���̵�) ���̵� �ߺ�");
			return "dupli";
		}
	}
	
	public static String androidIdCheck(String id){

		String dbId = memberDaoImpl.selectIdCheck(id);
		
		if(dbId == null){ 
			System.out.println("(�ȵ���̵�) ���̵� ��밡��");
			return "possicheck";
		}
		else{
			System.out.println("(�ȵ���̵�) ���̵� �ߺ�");
			return "duplicheck";
		}
	}
	
}
