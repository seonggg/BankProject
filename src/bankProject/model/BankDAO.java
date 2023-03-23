package bankProject.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import bankProject.OracleUtil;
import bankProject.controller.BankController;
import bankProject.vo.AccVO;
import bankProject.vo.CardVO;
import bankProject.vo.HistoryVO;
import bankProject.vo.MemVO;
import lombok.extern.java.Log;

public class BankDAO {
	Connection conn;
	Statement st;
	PreparedStatement pst;
	ResultSet rs;
	int resultCount;
	CallableStatement cst;
	
	//멤버 명단 조회
	public List<MemVO> selectMember() {
		String sql="""
				select MEM_ID,
						NAME,
						PHONE,
						ADDRESS,
						EMAIL
				from members
				order by 1
				""";
		List<MemVO> memlist = new ArrayList<>();
		conn = OracleUtil.getConnection();
		try {
			st=conn.createStatement();
			rs=st.executeQuery(sql);
			
			while(rs.next()) {
				MemVO mem = makeMem(rs);
				memlist.add(mem);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			OracleUtil.dbDisconnect(rs, st, conn);
		}
		return memlist;
	}
	
	//회원가입-멤버 추가
	public int insertMember(MemVO mem) {
		String sql="insert into members values(?, ?, ?, ?, ?)";
		conn=OracleUtil.getConnection();
		try {
			pst = conn.prepareStatement(sql);
			pst.setString(1, mem.getMem_id());
			pst.setString(2, mem.getName());
			pst.setString(3, mem.getPhone());
			pst.setString(4, mem.getAddress());
			pst.setString(5, mem.getEmail());
			resultCount = pst.executeUpdate();
			
		} catch (SQLException e) {
			resultCount=-1;
			e.printStackTrace();
		} finally {
			OracleUtil.dbDisconnect(null, pst, conn);
		}
		return resultCount;
	}
	
	//멤버 테이블에 해당 주민번호 있는지 조회
	public int selectMemId(String memId) {
		String sql="select * from members where mem_id=?";
		conn=OracleUtil.getConnection();
		try {
			pst = conn.prepareStatement(sql);
			pst.setString(1, memId);
			resultCount = pst.executeUpdate();
			
		} catch (SQLException e) {
			resultCount=-1;
			e.printStackTrace();
		} finally {
			OracleUtil.dbDisconnect(null, pst, conn);
		}
		return resultCount;
	}
	
	//로그인
	public int login(String id, String phone) {
		String sql="select phone from members where mem_id=?";
		conn=OracleUtil.getConnection();
		try {
			pst = conn.prepareStatement(sql);
			pst.setString(1, id);
			rs=pst.executeQuery();
			while(rs.next()) {
				if(rs.getString("phone").equals(phone)) {
					resultCount=1;
				} else {
					resultCount=-1;
				}
			}
			
		} catch (SQLException e) {
			resultCount=-1;
			e.printStackTrace();
		} finally {
			OracleUtil.dbDisconnect(null, pst, conn);
		}
		return resultCount;
	}

	//계좌 개설
	public int insertAccount(int balance) {
		String sql="""
				insert into accounts values(acc_seq.nextval, ?, sysdate, 'n', ?)
				""";
		conn=OracleUtil.getConnection();
		try {
			pst = conn.prepareStatement(sql);
			pst.setInt(1, balance);
			pst.setString(2, BankController.memid);
			resultCount = pst.executeUpdate();
			
		} catch (SQLException e) {
			resultCount=-1;
			e.printStackTrace();
		} finally {
			OracleUtil.dbDisconnect(null, pst, conn);
		}
		return resultCount;
	}

	//멤버별 계좌 조회
	public List<AccVO> selectAccount() {
		String sql="select ACC_ID, ACC_BALANCE, ACC_START, ACC_CARD, MEM_ID from accounts where mem_id=?";
		List<AccVO> acclist = new ArrayList<>();
		conn = OracleUtil.getConnection();
		try {
			pst = conn.prepareStatement(sql);
			pst.setString(1, BankController.memid);
			rs=pst.executeQuery();
			
			while(rs.next()) {
				AccVO acc = makeAcc(rs);
				acclist.add(acc);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			OracleUtil.dbDisconnect(rs, st, conn);
		}
		return acclist;
	}
	
	//계좌 이체
	public int transferAcc(int fromId, int toId, int price) {
		if(getBal(fromId)-price<0) return -10000;
		resultCount=-1;
		if(receiveMoney(toId, price)>0) {
			if(giveMoney(fromId,price)>0) {
				if(insertHistory(fromId, 0, price, getBal(fromId))*
					insertHistory(toId, 1, price, getBal(toId))>0) {
					resultCount=1;
				}
			}
		}
		return resultCount;
	}
	
	//돈 보내기
	private int giveMoney(int fromId, int price) {
		String sql="""
				update accounts 
				set acc_balance=acc_balance-?
				where acc_id=?
				""";
		conn=OracleUtil.getConnection();
		try {
			//if(resultCount==-10000)
			pst = conn.prepareStatement(sql);
			pst.setInt(1, price);
			pst.setInt(2, fromId);
			resultCount = pst.executeUpdate();
			
		} catch (SQLException e) {
			resultCount=-1;
			e.printStackTrace();
		} finally {
			OracleUtil.dbDisconnect(null, pst, conn);
		}
		return resultCount;
	}
	
	//돈 받기
	private int receiveMoney(int toId, int price) {
		String sql="""
				update accounts 
				set acc_balance=acc_balance+?
				where acc_id=?
				""";
		conn=OracleUtil.getConnection();
		try {
			pst = conn.prepareStatement(sql);
			pst.setInt(1, price);
			pst.setInt(2, toId);
			resultCount = pst.executeUpdate();
			
		} catch (SQLException e) {
			resultCount=-1;
			e.printStackTrace();
		} finally {
			OracleUtil.dbDisconnect(null, pst, conn);
		}
		return resultCount;
	}
	
	//계좌 해지
	public int deleteAccount(int accId) {
		String sql="""
				delete from accounts where acc_id=?
				""";
		conn=OracleUtil.getConnection();
		try {
			pst = conn.prepareStatement(sql);
			pst.setInt(1, accId);
			resultCount = pst.executeUpdate();
			
		} catch (SQLException e) {
			resultCount=-1;
			e.printStackTrace();
		} finally {
			OracleUtil.dbDisconnect(null, pst, conn);
		}
		return resultCount;
	}
	
	//카드 발급
	public int insertCard(int acc_id) {
		if(chkCard(acc_id)<=0) {
			resultCount=chkCard(acc_id);
			return resultCount;
		}
		String sql="""
				insert into cards values(card_seq.nextval, sysdate, ?)
				""";
		conn=OracleUtil.getConnection();
		try {
			pst = conn.prepareStatement(sql);
			pst.setInt(1, acc_id);
			resultCount = pst.executeUpdate();
			
		} catch (SQLException e) {
			resultCount=-1;
			e.printStackTrace();
		} finally {
			OracleUtil.dbDisconnect(null, pst, conn);
		}
		return resultCount;
	}
	
	//계좌의 카드 발급 여부 확인
	private int chkCard(int acc_id) {
		String sql="""
				select ACC_CARD
				from accounts
				where acc_id=?
				""";
		conn=OracleUtil.getConnection();
		try {
			pst = conn.prepareStatement(sql);
			pst.setInt(1, acc_id);
			rs=pst.executeQuery();
			while(rs.next()) {
				if(rs.getString("acc_card").equals("y")) resultCount= -100;
			}
		} catch (SQLException e) {
			resultCount=-1;
			e.printStackTrace();
		} finally {
			OracleUtil.dbDisconnect(null, pst, conn);
		}
		return resultCount;
	}
	
	//멤버별 카드 조회
	public List<CardVO> selectCard() {
		String sql="""
				select CARD_ID, CARD_START, ACC_ID
				from cards join accounts using(acc_id)
				where mem_id=?
				""";
		
		List<CardVO> cardlist = new ArrayList<>();
		conn = OracleUtil.getConnection();
		try {
			pst = conn.prepareStatement(sql);
			pst.setString(1, BankController.memid);
			rs=pst.executeQuery();
			
			while(rs.next()) {
				CardVO card = makeCard(rs);
				cardlist.add(card);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			OracleUtil.dbDisconnect(rs, st, conn);
		}
		return cardlist;
	}
	
	//카드 결제
	public int useCard(int cardNum, int price) {
		if(getBal(getAcc(cardNum))-price<0) return -10000;
		String sql="""
				update accounts set acc_balance=acc_balance-? 
				where acc_id=(
					select acc_id 
					from cards 
					where card_id=?)
				""";
		conn=OracleUtil.getConnection();
		try {
			pst = conn.prepareStatement(sql);
			pst.setInt(1, price);
			pst.setInt(2, cardNum);
			resultCount = pst.executeUpdate();
			
		} catch (SQLException e) {
			resultCount=-1;
			e.printStackTrace();
		} finally {
			OracleUtil.dbDisconnect(null, pst, conn);
		}
		insertHistory(getAcc(cardNum), 0, price, getBal(getAcc(cardNum)));
		return resultCount;
	}
	
	//카드 해지
	public int deleteCard(int cardId) {
		String sql="""
				delete from cards where card_id=?
				""";
		conn=OracleUtil.getConnection();
		try {
			pst = conn.prepareStatement(sql);
			pst.setInt(1, cardId);
			resultCount = pst.executeUpdate();
			
		} catch (SQLException e) {
			resultCount=-1;
			e.printStackTrace();
		} finally {
			OracleUtil.dbDisconnect(null, pst, conn);
		}
		return resultCount;
	}
	
	//거래 내역 조회
	public List<HistoryVO> selectHistory(int id) {
		String sql="""
				select ACC_ID,
					HISTORY_DATE,
					HISTORY_ID,
					KIND,
					PRICE,
					HISTORY_BALANCE
				from history
				where acc_id = ? 
				order by HISTORY_DATE desc, HISTORY_ID desc
				""";
		List<HistoryVO> hislist = new ArrayList<>();
		conn = OracleUtil.getConnection();
		try {
			pst = conn.prepareStatement(sql);
			pst.setInt(1, id);
			rs=pst.executeQuery();
			
			while(rs.next()) {
				HistoryVO his = makeHis(rs);
				hislist.add(his);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			OracleUtil.dbDisconnect(rs, st, conn);
		}
		return hislist;
	}
	
	//거래 내역 추가
	private int insertHistory(int acc_id, int kind, int price, int balance) {
		String sql="""
				insert into history 
				values(?, sysdate, hist_seq.nextval, ?, ?, ?)
				""";
		conn=OracleUtil.getConnection();
		try {
			pst = conn.prepareStatement(sql);
			pst.setInt(1, acc_id);
			pst.setInt(2, kind);
			pst.setInt(3, price);
			pst.setInt(4, balance);
			resultCount = pst.executeUpdate();
			
		} catch (SQLException e) {
			resultCount=-1;
			e.printStackTrace();
		} finally {
			OracleUtil.dbDisconnect(null, pst, conn);
		}
		return resultCount;
	}
	
	//카드번호로 계좌번호 알아내기
	private int getAcc(int cardNum) {
		String sql="""
					select ACC_ID
							from cards where card_id=?
						 """;
		conn=OracleUtil.getConnection();
		int account = 0;
		try {
			pst = conn.prepareStatement(sql);
			pst.setInt(1, cardNum);
			rs=pst.executeQuery();
			
			while(rs.next()) {
				account=rs.getInt("acc_id");
			}
			
		} catch (SQLException e) {
			resultCount=-1;
			e.printStackTrace();
		} finally {
			OracleUtil.dbDisconnect(null, pst, conn);
		}
		return account;
	}
	
	//계좌번호로 계좌 잔고 알아내기
	public int getBal(int acc_id) {
		String sql="select ACC_BALANCE from accounts where acc_id=?";
		conn=OracleUtil.getConnection();
		int balance = 0;
		try {
			pst = conn.prepareStatement(sql);
			pst.setInt(1, acc_id);
			rs=pst.executeQuery();
			
			while(rs.next()) {
				balance=rs.getInt("acc_balance");
			}
			
		} catch (SQLException e) {
			resultCount=-1;
			e.printStackTrace();
		} finally {
			OracleUtil.dbDisconnect(null, pst, conn);
		}
		return balance;
	}
	
	//내 계좌인지 확인
	public int grantAcc(int accid) {
		resultCount=-1;
		String sql="""
				select MEM_ID
				from accounts
				where acc_id=?
				""";
		conn=OracleUtil.getConnection();
		try {
			pst = conn.prepareStatement(sql);
			pst.setInt(1, accid);
			rs=pst.executeQuery();
			while(rs.next()) {
				if(rs.getString("mem_id").equals(BankController.memid)) {
					resultCount= 1;
				}
			}
			
		} catch (SQLException e) {
			//resultCount=-1;
			e.printStackTrace();
		} finally {
			OracleUtil.dbDisconnect(null, pst, conn);
		}
		return resultCount;
	}
	
	//내 카드인지 확인
	public int grantCard(int cardid) {
		resultCount=-1;
		String sql="""
				select MEM_ID
				from accounts
				where acc_id=(
					select acc_id
					from cards
					where card_id=?)
				""";
		conn=OracleUtil.getConnection();
		try {
			pst = conn.prepareStatement(sql);
			pst.setInt(1, cardid);
			rs=pst.executeQuery();
			while(rs.next()) {
				if(rs.getString("mem_id").equals(BankController.memid)) {
					resultCount= 1;
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			OracleUtil.dbDisconnect(null, pst, conn);
		}
		return resultCount;
	}
	
	private HistoryVO makeHis(ResultSet rs) throws SQLException {
		HistoryVO hist = new HistoryVO();
		hist.setAcc_id(rs.getInt("acc_id"));
		hist.setHistory_date(rs.getDate("history_date"));
		hist.setHistory_id(rs.getInt("history_id"));
		hist.setKind(rs.getInt("kind"));
		hist.setPrice(rs.getInt("price"));
		hist.setHistory_balance(rs.getInt("history_balance"));
		return hist;
	}
		
	private CardVO makeCard(ResultSet rs) throws SQLException {
		CardVO card = new CardVO();
		card.setCard_id(rs.getInt("card_id"));
		card.setCard_start(rs.getDate("card_start"));
		card.setAcc_id(rs.getInt("acc_id"));
		return card;
	}
	
	private AccVO makeAcc(ResultSet rs) throws SQLException {
		AccVO acc = new AccVO();
		acc.setAcc_id(rs.getInt("acc_id"));
		acc.setAcc_balance(rs.getInt("acc_balance"));
		acc.setAcc_start(rs.getDate("acc_start"));
		acc.setAcc_card(rs.getString("acc_card"));
		acc.setMem_id(rs.getString("mem_id"));
		return acc;
	}
	
	private MemVO makeMem(ResultSet rs) throws SQLException {
		MemVO mem=new MemVO();
		mem.setMem_id(rs.getString("mem_id"));
		mem.setName(rs.getString("name"));
		mem.setPhone(rs.getString("phone"));
		mem.setAddress(rs.getString("address"));
		mem.setEmail(rs.getString("email"));
		return mem;
	}
}
