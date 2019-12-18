package com.sonic.storm;

import org.apache.commons.io.FileUtils;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.shade.com.google.common.base.Charsets;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * LocalWordCountStormTopology
 *
 * @author Sonic
 * @since 2019/12/14
 */
public class LocalWordCountStormTopology {

    private static Logger logger = LoggerFactory.getLogger(LocalWordCountStormTopology.class);

    public static class DataSourceSpout extends BaseRichSpout {

        private SpoutOutputCollector collector = null;

        @Override
        public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
            this.collector = collector;
        }

        // 不断重复调用
        @Override
        public void nextTuple() {
            Collection<File> files = FileUtils.listFiles(new File("F:\\BigData\\SpringBootLearning\\projects\\storm-learning\\files"), new String[]{"txt"}, true);
            for (File file : files) {
                try {
                    List<String> lines = FileUtils.readLines(file, Charsets.UTF_8);
                    for (String line : lines) {
                        this.collector.emit(new Values(line));
                    }
                    FileUtils.moveFile(file, new File(file.getAbsolutePath() + System.currentTimeMillis()));
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("line"));
        }
    }

    public static class SplitBolt extends BaseRichBolt {

        private OutputCollector collector;

        @Override
        public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
            this.collector = collector;
        }

        @Override
        public void execute(Tuple input) {
            String line = input.getStringByField("line");
            String[] words = line.split(",");
            for (String word : words) {
                this.collector.emit(new Values(word));
            }

        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("word"));

        }
    }

    public static class CountBolt extends BaseRichBolt {

        @Override
        public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {

        }

        private Map<String, Integer> map = new HashMap<>();

        @Override
        public void execute(Tuple input) {
            String word = input.getStringByField("word");
            Integer count = map.get(word);
            if (count == null) {
                count = 1;
            } else {
                count++;
            }
            map.put(word, count);
            logger.info("~~~~~~~~~~~~~~~~~~~~~~~");
            Set<Map.Entry<String, Integer>> entries = map.entrySet();
            for (Map.Entry<String, Integer> entry : entries) {
                logger.info(entry.toString());
            }
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {

        }
    }

    public static void main(String[] args) throws InterruptedException {
        TopologyBuilder topologyBuilder = new TopologyBuilder();
        topologyBuilder.setSpout("DataSourceSpout", new DataSourceSpout());
        topologyBuilder.setBolt("SplitBolt", new SplitBolt()).shuffleGrouping("DataSourceSpout");
        topologyBuilder.setBolt("CountBolt", new CountBolt()).fieldsGrouping("SplitBolt", new Fields("word"));

        LocalCluster localCluster = new LocalCluster();
        Config config = new Config();
        config.setNumWorkers(2);
        config.setNumAckers(0);
        localCluster.submitTopology("LocalWordCountStormTopology", config, topologyBuilder.createTopology());

        Thread.sleep(20000);

        localCluster.shutdown();
    }

}
