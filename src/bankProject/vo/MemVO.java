package bankProject.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @ToString
public class MemVO {
	private String mem_id;
	private String name;
	private String phone;
	private String address;
	private String email;
}
