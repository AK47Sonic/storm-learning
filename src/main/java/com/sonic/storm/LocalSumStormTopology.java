package com.sonic.storm;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * LocalSumStormTopology
 *
 * @author Sonic
 * @since 2019/12/11
 */
public class LocalSumStormTopology {

    private static Logger logger = LoggerFactory.getLogger(LocalSumStormTopology.class);

    public static class DataSourceSpout extends BaseRichSpout {

        private SpoutOutputCollector collector;

        @Override
        public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
            this.collector = collector;
        }

        int number = 0;

        @Override
        public void nextTuple() {
            this.collector.emit(new Values(number++));
            logger.info("Spout: {}", number);

            Utils.sleep(1000);
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("num"));
        }
    }

}