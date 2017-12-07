package site.lankui.impaler.constant;

import io.netty.util.AttributeKey;
import site.lankui.impaler.client.bean.Session;

public class AttributeMapConstant {
	public static final AttributeKey<Session> KEY_CLIENT = AttributeKey.valueOf("session");
}
