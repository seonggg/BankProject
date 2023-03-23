package bankProject.vo;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class HistoryVO {
	private int acc_id;
	private Date history_date;
	private int history_id;
	private int kind;
	private int price;
	private int history_balance;
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("계좌번호:");
		builder.append(acc_id);
		builder.append("\t날짜:");
		builder.append(history_date);
		builder.append("\t금액:");
		builder.append(kind==0? "-":"+");
		builder.append(price);
		builder.append("\t잔고:₩");
		builder.append(history_balance);
		return builder.toString();
	}
}
