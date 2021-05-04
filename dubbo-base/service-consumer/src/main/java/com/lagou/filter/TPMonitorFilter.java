package com.lagou.filter;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.rpc.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Activate(group = {Constants.CONSUMER})
public class TPMonitorFilter implements Filter {

    static Map<String, List<MethodInvokeInfo>> map;//保存每个方法得请求时间Map

    static {
        map = new ConcurrentHashMap<>();
        map.put("A",new ArrayList<MethodInvokeInfo>());
        map.put("B",new ArrayList<MethodInvokeInfo>());
        map.put("C",new ArrayList<MethodInvokeInfo>());

        //启动定时任务
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(2);
        scheduledThreadPool.scheduleAtFixedRate(new CountThread(), 5000,5000, TimeUnit.MILLISECONDS);
        System.out.println("定时任务初始化完成...");
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        //在这统计每一次完成一次调用代理用的时间
        long start = System.currentTimeMillis();
        Result result = invoker.invoke(invocation);
        //执行到这说明代理执行完并返回了结果
        long end = System.currentTimeMillis();
        //将，每个方法的每次请求开始时间和结束时间及请求耗时存进map
        MethodInvokeInfo methodInvokeInfo = new MethodInvokeInfo(start, end);
        if(invocation.getMethodName().indexOf("A") != -1){
            map.get("A").add(methodInvokeInfo);
        }else if(invocation.getMethodName().indexOf("B") != -1){
            map.get("B").add(methodInvokeInfo);
        }else{
            map.get("C").add(methodInvokeInfo);
        }
        return result;
    }

    class MethodInvokeInfo implements Comparable<MethodInvokeInfo>{

        private long startTime;//请求开始时间
        private long endTime;//请求结束时间
        private long cost;//请求耗时

        public MethodInvokeInfo(long startTime, long endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.cost = endTime- startTime;
        }

        /**
         * 按照cost从小到大排序
         * @param o
         * @return
         */
        @Override
        public int compareTo(MethodInvokeInfo o) {
            return (int)(this.cost-o.cost);
        }
    }

    /**
     * 获取TP90/TP99中的最大耗时所在下标
     * @param percent 百分比，TP90=0.9，TP99=0.99
     */
    static int getMaxCostIndex(List<MethodInvokeInfo> list, double percent){
        //向上舍入，获取到TPxx所在的list中的下标
        int index = (int) Math.floor(list.size()*percent);
        return index;
    }

    /**
     * 清理数据，将list中1分钟之前的数据全部清除
     */
    static void cleanData(){
        long now = System.currentTimeMillis();
        for (Map.Entry<String, List<MethodInvokeInfo>> stringListEntry : map.entrySet()) {
            List<MethodInvokeInfo> list = stringListEntry.getValue();
            if((now-list.get(0).startTime) > 60000){
                //说明已经开始有过期的数据了
                for (int i = 0; i < list.size(); i++) {
                    /*
                     *list中的数据是按startTime从小到大排序，所以当遇到小于60000时，
                     *说明是从当前下标开始懂事1分钟之内的了，所以则当前的下标是保存的初始下标
                     */
                    if((now-list.get(i).startTime) < 60000){
                        list = list.subList(i, list.size());
                        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>集合"+stringListEntry.getKey()+"清理了"+i+"条数据！");
                        break;
                    }
                }
            }

        }

    }

    static class CountThread implements Runnable{
        @Override
        public void run() {
            //先将距离现在1分钟之前的数据全部清掉
            cleanData();
            //在这里进行数据的打印
            for (Map.Entry<String, List<MethodInvokeInfo>> stringListEntry : map.entrySet()) {
                //将list排序，按照用时从小到大排序
                List<MethodInvokeInfo> temp = new ArrayList(stringListEntry.getValue().size());
                temp.addAll(stringListEntry.getValue());
                System.out.println("------------temp.size()="+temp.size());
                Collections.sort(temp);

                //获取TP90取值在temp的索引号
                int maxIndexTP90 = getMaxCostIndex(temp, 0.90);
                //获取TP99取值在temp的索引号
                int maxIndexTP99 = getMaxCostIndex(temp, 0.99);
                long costTP90 = temp.get(maxIndexTP90).cost;
                long costTP99 = temp.get(maxIndexTP99).cost;
                System.out.println(">>>>>>>>>>>>method"+stringListEntry.getKey()+"的TP90="+costTP90+"，TP99="+costTP99);
            }

        }
    }
}
