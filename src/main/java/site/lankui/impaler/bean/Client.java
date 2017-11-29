package site.lankui.impaler.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class Client {

	private int clientId;

	private String name;

	@JsonIgnore
	private Session session;

}
