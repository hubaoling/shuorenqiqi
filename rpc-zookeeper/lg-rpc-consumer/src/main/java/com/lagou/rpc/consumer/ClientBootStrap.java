package com.lagou.rpc.consumer;

import com.lagou.rpc.api.IUserService;
import com.lagou.rpc.common.RpcRequest;
import com.lagou.rpc.consumer.proxy.RpcClientProxy;
import com.lagou.rpc.pojo.User;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 启动服务，从zk注册中心上获取服务端注册的ip列表，通过动态代理获取IUserService接口信息
 */
public class ClientBootStrap {
    public static final Map<String, ServerMapper> serviceMap = new ConcurrentHashMap<String, ServerMapper>();

    public static void main(String[] args) {
        ZkClient zkClient = new ZkClient("127.0.0.1:2181");
        System.out.println("会话被创建了..");
        String rootNodePath = "/lg-server";
        //获取rootNodePath节点下的子节点
        List<String> children = zkClient.getChildren(rootNodePath);
       // Map<String, IUserService> serviceMap = new HashMap<>();
        for (String ipPort : children) {
            String[] ip_port = ipPort.split("_");
            String ip = ip_port[0];
            int port = Integer.parseInt(ip_port[1]);
            IUserService userService = (IUserService) RpcClientProxy.createProxy(IUserService.class, ip, port);
            ServerMapper serverMapper = new ServerMapper();
            serverMapper.setLastTime(zkClient.readData(rootNodePath+"/"+ipPort));
            serverMapper.setUserService(userService);
            serviceMap.put(ipPort, serverMapper);
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        //循环调用服务器
                        System.out.println("循环调用服务器");
                        runServer(zkClient);
                        Thread.sleep(3000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();

        //监听服务器子节点变更信息
        zkClient.subscribeChildChanges(rootNodePath, new IZkChildListener() {
            public void handleChildChange(String parentPath, List<String> list) throws Exception {
                System.out.println(parentPath + "的子节点列表发生了变化,变化后的子节点列表为"+ list);
                if(list.isEmpty()){
                    System.out.println("所有节点都挂掉了。。。。");
                    serviceMap.clear();
                }

                for (String updateChild : list) {
                    if(!serviceMap.containsKey(updateChild)){
                        //说明新增服务器节点
                        String[] ip_port = updateChild.split("_");
                        String ip = ip_port[0];
                        int port = Integer.parseInt(ip_port[1]);
                        IUserService userService = (IUserService) RpcClientProxy.createProxy(IUserService.class, ip, port);
                        ServerMapper serverMapper = new ServerMapper();
                        serverMapper.setLastTime(zkClient.readData(rootNodePath+"/"+updateChild));
                        serverMapper.setUserService(userService);
                        serviceMap.put(updateChild, serverMapper);
                        System.out.println("新增服务器节点，"+updateChild);
                    }
                }
                //如果原有节点不在当前子节点列表了，说明已经下线了，需要从serviceMap移除
                for(String key : serviceMap.keySet()){
                    if(!list.contains(key)){
                        System.out.println("删除服务器节点，"+key);
                        serviceMap.remove(key);
                    }
                }
            }
        });
    }

    //根据负载均衡策略：选择最后一次响应时间短的服务端进行服务调用
    private static void runServer(ZkClient zkClient) {
        AtomicReference<IUserService> iUserService = new AtomicReference<>();
        final long[] time = {0L};
        final String[] ip_port = {""};//访问的IP和port
        //获取响应时间短的服务端
        serviceMap.forEach((k, v) -> {
            long nodeTime = zkClient.readData("/lg-server/"+k);
            System.out.println(k+"=="+nodeTime);
            if (System.currentTimeMillis() - nodeTime > 5000){
                System.out.println("超出5s"+"==="+k);
                return;
            }
            if (nodeTime > time[0]){
                iUserService.set(v.getUserService());
                time[0] = v.getLastTime();
                ip_port[0] = k;
            }
        });
        if (iUserService.get() != null){
            System.out.println("调用服务器查询用户接口"+ip_port[0]);
            //调用服务器查询用户接口
            User user = iUserService.get().getById(1);
            System.out.println(user);
        }

    }
}
