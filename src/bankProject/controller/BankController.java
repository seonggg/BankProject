package bankProject.controller;

import java.util.Scanner;
import java.util.regex.Pattern;

import bankProject.model.BankService;
import bankProject.view.BankView;
import bankProject.vo.MemVO;

public class BankController {
	public static String memid = null;
	private static BankService bService = new BankService();
	private static final String PHONEPATTERN="^010-(?:\\d{4})-\\d{4}$";
	private static final String IDPATTERN="\\d{6}\\-[1-4]\\d{6}";
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		while(true) {
			System.out.println("Start=======================");
			System.out.println("1.회원가입 | 2.로그인 | 3.종료");
			System.out.println("============================");
			System.out.print("작업 선택>>");
			String job=sc.next();
			if(job.equals("3")) break;
			switch(job) {
			case "1":{ //회원가입
				join(sc);
				continue;
			}
			case "2": //로그인
				if(login(sc)==0) continue;
				else break;
			default:
				BankView.print("메뉴에 있는 번호를 입력하세요.");
				break;
			}
			while(true) {
				System.out.println("MENU=========================================");
				System.out.println("1.계좌 개설 | 2.계좌 조회 | 3.계좌 이체 | 4.계좌 해지");
				System.out.println("5.카드 발급 | 6.카드 조회 | 7.카드 결제 | 8.카드 해지");
				System.out.println("9.내역 조회 | 0.로그아웃");
				System.out.println("=============================================");
				System.out.print("작업 선택>>");
				job=sc.next();
				if(job.equals("0")) {
					memid=null;
					break;
				}
				switch(job) {
				case "1": //계좌 개설
					System.out.print("입금액>>");
					int bal=sc.nextInt();
					BankView.print(bService.insertAccount(bal));
					break;
				case "2": //계좌 조회
					BankView.print(bService.selectAccount());
					break;
				case "3": {//계좌 이체
					transferMoney(sc);
					break;
				}
				case "4": { //계좌 해지
					deleteAcc(sc);
					break;
				}
				case "5": {//카드 발급
					makeCard(sc);
					break;
				}
				case "6": {//카드 조회
					BankView.print(bService.selectCard());
					break;
				}
				case "7": {//카드 결제
					useCard(sc);
					break;
				}
				case "8": { //카드 해지
					deleteCard(sc);
					break;
				}
				case "9": //계좌 내역 조회
					printHistory(sc);
					break;
				default:
					BankView.print("메뉴에 있는 번호를 입력하세요.");
					break;
				}
			}
		}
	}
	
	//거래내역 조회
	private static void printHistory(Scanner sc) {
		BankView.print(bService.selectAccount());
		System.out.print("조회할 계좌번호>>");
		int accid=sc.nextInt();
		if(bService.grantAcc(accid)<=0) {
			BankView.print("권한이 없는 계좌입니다.");
			return;
		}
		BankView.print(bService.selectHistory(accid));
		return;
	}

	//카드해지
	private static void deleteCard(Scanner sc) {
		BankView.print(bService.selectCard());
		System.out.print("해지할 카드번호>>");
		int cardId=sc.nextInt();
		if(bService.grantCard(cardId)<=0) {
			BankView.print("권한이 없는 카드입니다.");
			return;
		}
		System.out.print("카드를 해지하시겠습니까?.(y/n)>>");
		String answer=sc.next();
		if(!answer.equals("y"))  return;
		BankView.print(bService.deleteCard(cardId));
		return;
	}

	//카드 결제
	private static void useCard(Scanner sc) {
		BankView.print(bService.selectCard());
		System.out.print("결제할 카드 번호>>");
		int cardId=sc.nextInt();
		if(bService.grantCard(cardId)<=0) {
			BankView.print("권한이 없는 카드입니다.");
			return;
		}
		System.out.print("결제할 금액>>");
		int price=sc.nextInt();
		BankView.print(bService.useCard(cardId,price));
		return;
	}

	//카드발급
	private static void makeCard(Scanner sc) {
		BankView.print(bService.selectAccount());
		System.out.print("카드 발급받을 계좌번호>>");
		int accid=sc.nextInt();
		if(bService.grantAcc(accid)<=0) {
			BankView.print("권한이 없는 계좌입니다.");
			return;
		}
		BankView.print(bService.insertCard(accid));
		return;
	}
	//계좌해지
	private static void deleteAcc(Scanner sc) {
		BankView.print(bService.selectAccount());
		System.out.print("해지할 계좌번호>>");
		int accId=sc.nextInt();
		if(bService.grantAcc(accId)<=0) {
			BankView.print("권한이 없는 계좌입니다.");
			return;
		}
		System.out.print("계좌에 잔액이 "+bService.getBal(accId)+
				"원인데 해지하시겠습니까? 잔액은 사라집니다.(y/n)>>");
		String answer=sc.next();
		if(!answer.equals("y"))  {
			BankView.print("계좌 해지 취소");
			return;
		}
		BankView.print(bService.deleteAccount(accId));
		return;
	}
	//계좌이체
	private static void transferMoney(Scanner sc) {
		BankView.print(bService.selectAccount());
		System.out.print("나의 계좌번호>>");
		int fromId=sc.nextInt();
		if(bService.grantAcc(fromId)<=0) {
			BankView.print("권한이 없는 계좌입니다.");
			return;
		}
		System.out.print("보낼 계좌번호>>");
		int toId=sc.nextInt();
		System.out.print("이체할 금액>>");
		int price=sc.nextInt();
		BankView.print(bService.transferAcc(fromId, toId, price));
		return;
	}
	//회원가입
	private static void join(Scanner sc) {
		System.out.print("주민번호 [######-#######] >>");
		memid = sc.next();
		//입력 형태 설정
		if(Pattern.matches(IDPATTERN, memid)==false) {
			BankView.print("주민번호 형식대로 입력해주세요.");
			return;
		}
		//주민번호 중복 확인
		if(bService.selectMemId(memid)>0) {
			BankView.print("이미 가입되어 있습니다. 로그인하세요.");
			return;
		}
		System.out.print("이름>>");
		String name = sc.next();
		System.out.print("전화번호 [010-####-####] >>");
		String phone = sc.next();
		//입력 형태 설정
		if(Pattern.matches(PHONEPATTERN, phone)==false) {
			BankView.print("전화번호 형식대로 입력해주세요.");
			return;
		}
		System.out.print("주소>>");
		String add = sc.next();
		System.out.print("이메일>>");
		String email = sc.next();
		MemVO mem = new MemVO(memid, name, phone, add, email);
		BankView.print(bService.insertMember(mem));
	}
	//로그인
	private static int login(Scanner sc) {
		System.out.print("주민번호 [######-#######] >>");
		memid = sc.next();
		//입력 형태 설정
		if(Pattern.matches(IDPATTERN, memid)==false) {
			BankView.print("주민번호 형식대로 입력해주세요.");
			return 0;
		}
		if(bService.selectMemId(memid)<=0){
			BankView.print("로그인 불가, 회원가입 먼저 하세요.");
			return 0;
		}
		System.out.print("전화번호 [010-####-####] >>");
		String phone = sc.next();
		//입력 형태 설정
		if(Pattern.matches(PHONEPATTERN, phone)==false) {
			BankView.print("전화번호 형식대로 입력해주세요.");
			return 0;
		}
		if(bService.login(memid,phone)>0) {
			BankView.print("로그인 성공");
			return 1;
		}
		BankView.print("로그인 실패");
		return 0;
	}
}
