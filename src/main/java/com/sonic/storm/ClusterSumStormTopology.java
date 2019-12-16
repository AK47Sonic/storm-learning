package com.sonic.storm;

import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * ClusterSumStormTopology
 *
 * @author Sonic
 * @since 2019/12/15
 */
public class ClusterSumStormTopology {

    private static Logger logger = LoggerFactory.getLogger(ClusterSumStormTopology.class);

    public static class DataSourceSpout extends BaseRichSpout {

        private SpoutOutputCollector collector;

        @Override
        public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
            this.collector = collector;
        }

        int number = 0;

        @Override
        public void nextTuple() {
            this.collector.emit(new Values(++number));
            logger.info("Spout: {}", number);

            Utils.sleep(1000);
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("num"));
        }
    }

    public static class SumBolt extends BaseRichBolt {

        @Override
        public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {

        }

        int sum = 0;

        // 收到就执行
        @Override
        public void execute(Tuple input) {
            Integer value = input.getIntegerByField("num");
            sum += value;
            logger.info("sum = [{}]", sum);
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {

        }
    }

    public static void main(String[] args) throws InterruptedException, InvalidTopologyException, AuthorizationException, AlreadyAliveException {

        TopologyBuilder topologyBuilder = new TopologyBuilder();

        topologyBuilder.setSpout("DataSourceSpout", new LocalSumStormTopology.DataSourceSpout());
        topologyBuilder.setBolt("SumBolt", new LocalSumStormTopology.SumBolt()).shuffleGrouping("DataSourceSpout");

        StormSubmitter.submitTopology(ClusterSumStormTopology.class.getSimpleName(), new Config(), topologyBuilder.createTopology());

    }

}
