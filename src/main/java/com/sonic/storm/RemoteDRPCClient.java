package com.sonic.storm;

import org.apache.storm.Config;
import org.apache.storm.thrift.TException;
import org.apache.storm.utils.DRPCClient;

/**
 * RemoteDRPCClient
 *
 * @author Sonic
 * @since 2019/12/22
 */
public class RemoteDRPCClient {

    public static void main(String[] args) throws TException {
        Config conf = new Config();
        conf.put("storm.thrift.transport", "org.apache.storm.security.auth.plain.PlainSaslTransportPlugin");
        conf.put(Config.STORM_NIMBUS_RETRY_TIMES, 3);
        conf.put(Config.STORM_NIMBUS_RETRY_INTERVAL, 10);
        conf.put(Config.STORM_NIMBUS_RETRY_INTERVAL_CEILING, 20);
        DRPCClient client = new DRPCClient(conf, "192.168.1.151", 3772); //默认端口3772，在UI可以搜到
        String result = client.execute("addUser", "zhangsan");
        System.out.println("result: " + result);
    }

}
