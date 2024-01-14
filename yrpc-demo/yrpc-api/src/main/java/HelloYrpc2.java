/**
 * @author Hongbin BAO
 * @Date 2024/1/4 21:14
 */
public interface HelloYrpc2 {
    /**
     * 通用接口 server 和client都需要依赖
     * @param msg 发送的具体消息
     * @return 返回的结果
     */
    String sayHi(String msg);
}
