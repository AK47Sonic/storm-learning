package com.sonic.storm;

import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.drpc.LinearDRPCTopologyBuilder;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * RemoteDRPCTopology
 *
 * @author Sonic
 * @since 2019/12/22
 */
public class RemoteDRPCTopology {

    private static Logger logger = LoggerFactory.getLogger(LocalDRPCTopology.class);

    public static class MyBolt extends BaseRichBolt {

        private OutputCollector collector;

        @Override
        public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
            this.collector = collector;
        }

        @Override
        public void execute(Tuple input) {
            Object requestId = input.getValue(0); // 请求id
            String name = input.getString(1);

            String result = "add user: " + name;
            logger.info("MyBolt requestId: {}", requestId);
            this.collector.emit(new Values(requestId, result));
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("id", "result"));
        }
    }

    /**
     * drpc：
     * 修改Storm.yaml drpc.servers
     * 启动drpc storm drpc
     * 在服务器jps，发现drpc进程
     */
    public static void main(String[] args) throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {
        LinearDRPCTopologyBuilder builder = new LinearDRPCTopologyBuilder("addUser");
        builder.addBolt(new LocalDRPCTopology.MyBolt(), 1);

        StormSubmitter.submitTopology("remote-drpc", new Config(), builder.createRemoteTopology());

    }
}
