### Storm Learning

1. 核心概念
    - Topologies： 拓扑，将整个流程串起来
    - Streams：数据流
    - Spouts: 产生数据
    - Bolts： 处理数据
    - Tuple： 数据
    - Stream groupings
    - Reliability
    - Tasks
    - Workers：JVM进程
    
2. ISpout
    - 概述  
        - 核心接口，负责将数据发送到topology中去处理
        - Storm会跟踪Spout发出去的Tuple的DAG
        - ack/fail
        - Tuple: message id
        - ack/fail/nextTuple是在同一个线程中执行的，所以不用考虑线程安全问题
    - 核心方法
        - open：初始化操作
        - close： 资源释放操作
        - nextTuple：发送数据
        - ack：Tuple处理成功，storm会反馈给spout一个成功消息
        - fail：Tuple处理失败，storm会发送一个消息给spout，处理失败
       
3. IComponent 
    - declareOutputFields(OutputFieldsDeclarer var1)：声明当前Spout/Bolt发送的tuple的结构 

4. IBolt
    - 接收tuple处理，并进行相应的处理
    - IBolt会在一个运行机器上创建，使用Java序列化它，然后提交到主节点(Nimbus)上去执行, nimbus会启动worker来反序列化，调用prepare方法，然后才开始处理tuple数据
    - 方法
        - prepare：初始化
        - execute：处理一个tuple
        - cleanup：shutdown之前的资源清理操作
        