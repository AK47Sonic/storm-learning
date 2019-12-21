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
        
5. 安装环境
    - tar -zxvf jdk.tar.gz -C 安装目录
    - 加环境变量在~/.bash_profile
    - jps -m 路径
    - jps -l 类名
    
6. Storm 架构
    - 主从
    - Nimbus：主， 集群的主节点，负责任务（task）的指派和分发，资源的分配
    - Supervisor：从，可以启动多个worker，无状态，元数据存储在ZK中
    - Worker：运行具体组件逻辑（Spout、Bolt）的进程
    - Task：Worker中每一个Spout和Bolt的线程为一个Task
    - Executor：Spout和Bolt可能会共享一个线程
    - logviewer 日志查看服务，提供给UI使用
    - UI UI界面
    
7. Storm 目录树
    - ${storm.local.dir}/nimbus/inbox存放正在运行的topology jar
    - ${storm.local.dir}/supervisor/stormdist 
    
8. 并行度
    - 一个worker进程执行的是一个topology的子集
    - 一个worker进程会启动1..n个executor线程来执行一个topology的component
    - 一个运行的topology是由集群中多态物理机的多个worker进程组成
    - executor是一个被worker进程启动的单独线程，每个executor会运行1个topology的一个component（可能多个）
    - task是最终运行spout或bolt代码的最小执行单元
    - acker线程是为了保证 at least once
    - 默认：
        - 一个supervisor节点最多启动4个worker进程，storm.yaml配置多个
        - 每个topology默认占用一个worker进程，可以配多个
        - 每个worker进程会启动一个executor，可以配多个
        - 每个executor启动一个task，executor数量<=task数量
        - 每个worker启动一个acker
        
9. Stream Grouping
    - Shuffle grouping 随机发送
    - Fields grouping 同样字段会发送到同一个bolt中
    - Partial Key grouping 同样字段会发送到同一个bolt中，并且会对数据倾斜情况进行优化
    - All grouping 一份数据发送给所有的bolt
    

    
    
    