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
        