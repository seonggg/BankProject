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
public class CardVO {
	private int card_id;
	private Date card_start;
	private int acc_id;
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("카드번호:");
		builder.append(card_id);
		builder.append("\t발급일:");
		builder.append(card_start);
		builder.append("\t\t연결계좌번호:");
		builder.append(acc_id);
		return builder.toString();
	}
}
