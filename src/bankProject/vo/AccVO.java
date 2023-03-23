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
public class AccVO {
	private int acc_id;
	private int acc_balance;
	private Date acc_start;
	private String acc_card;
	private String mem_id;
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("계좌번호:");
		builder.append(acc_id);
		builder.append("\t잔고:₩");
		builder.append(acc_balance);
		builder.append("\t\t개설일:");
		builder.append(acc_start);
		builder.append("\t\t카드:");
		builder.append(acc_card.equals("y")? "발급":"미발급");
		return builder.toString();
	}
}
