/**
 * @author Hongbin BAO
 * @Date 2024/1/4 21:24
 */
public class HelloYrpcImpl implements HelloYrpc{
    @Override
    public String sayHi(String msg) {
        return "hi consumer:"+ msg;
    }
}
