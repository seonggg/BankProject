package bankProject.model;

import java.util.List;

import bankProject.vo.AccVO;
import bankProject.vo.CardVO;
import bankProject.vo.HistoryVO;
import bankProject.vo.MemVO;

public class BankService {
	BankDAO bankDao = new BankDAO();

	//멤버 전체 조회
	public List<MemVO> selectMember() {
		return bankDao.selectMember();
	}

	//멤버 추가
	public String insertMember(MemVO mem) {
		int result = bankDao.insertMember(mem);
		return result > 0 ? "가입 성공" : "가입 실패";
	}
	
	////멤버 테이블에 해당 주민번호 있는지 조회
	public int selectMemId(String memId) {
		int result = bankDao.selectMemId(memId);
		return  result;
	}
	
	//계좌 추가
	public String insertAccount( int bal) {
		int result=bankDao.insertAccount(bal);
		return  result > 0 ? "계좌 개설 성공" : "계좌 개설 실패";
	}

	//계좌 전체 조회
	public List<AccVO> selectAccount() {
		return bankDao.selectAccount();
	}

	//계좌 이체
	public String transferAcc(int fromId, int toId, int price) {
		int result=bankDao.transferAcc(fromId, toId, price);
		if(result==-10000) return "잔액이 모자랍니다.";
		return  result > 0 ? "이체 성공" : "이체 실패";
	}

	//카드 추가
	public String insertCard(int id) {
		int result = bankDao.insertCard(id);
		if(result==-100) return "이미 카드가 발급되어있습니다.";
		return result > 0 ? "카드 발급 성공" : "카드 발급 실패";
	}

	//카드 전체 조회
	public List<CardVO> selectCard() {
		return bankDao.selectCard();
	}

	//카드 결제
	public String useCard(int cardNum, int price) {
		int result=bankDao.useCard(cardNum, price);
		if(result==-50000) return "발급받은 카드가 존재하지 않습니다.";
		else if(result==-10000) return "잔액이 모자랍니다.";
		return  result > 0 ? "결제 성공" : "결제 실패";
	}

	//거래내역 전체 조회
	public List<HistoryVO> selectHistory(int id) {
		return bankDao.selectHistory(id);
	}

	//로그인
	public int login(String id, String phone) {
		int result=bankDao.login(id, phone);
		return  result;
	}

	//계좌 해지
	public String deleteAccount(int accId) {
		int result=bankDao.deleteAccount(accId);
		return  result > 0 ? "해지 성공" : "해지 실패";
	}
	
	//카드 해지
	public String deleteCard(int cardId) {
		int result=bankDao.deleteCard(cardId);
		return  result > 0 ? "해지 성공" : "해지 실패";
	}
	
	//계좌번호로 잔고 조회
	public int getBal(int acc_id) {
		return bankDao.getBal(acc_id);
	}
	
	//계좌번호 권한 확인
	public int grantAcc(int accid) {
		return bankDao.grantAcc(accid);
	}
	
	//카드 권한 확인
	public int grantCard(int cardid) {
		return bankDao.grantCard(cardid);
	}
	
}
