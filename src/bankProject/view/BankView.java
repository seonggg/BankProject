package bankProject.view;

import java.util.List;

import bankProject.vo.MemVO;

public class BankView {
	
	public static <T> void print(List<T> list) {
		System.out.println(
				"-----------------------------------조회-----------------------------------");
		if(list.size()==0) {
			System.out.println("존재하지 않습니다.");
		}
		for(T obj:list) {
			System.out.println(obj.toString());
		}
		System.out.println();
	}
	
	public static void print(String msg) {
		System.out.println("|알림|"+msg);
		System.out.println();
	}
}
