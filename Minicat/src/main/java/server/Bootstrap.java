package server;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Minicat的主类
 */
public class Bootstrap {

    /**定义socket监听的端口号*/
    private int port = 8080;

    /**项目部署路径*/
    private String appBase;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAppBase() {
        return appBase;
    }

    public void setAppBase(String appBase) {
        this.appBase = appBase;
    }

    /**
     * Minicat启动需要初始化展开的一些操作
     */
    public void start() throws Exception {
        //加载port和appBase
        loadPortAndAppBase();

        // 加载解析相关的配置，web.xml
        //loadServlet();


        // 定义一个线程池
        int corePoolSize = 10;
        int maximumPoolSize =50;
        long keepAliveTime = 100L;
        TimeUnit unit = TimeUnit.SECONDS;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(50);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();


        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue,
                threadFactory,
                handler
        );

        /*
            完成Minicat 1.0版本
            需求：浏览器请求http://localhost:8080,返回一个固定的字符串到页面"Hello Minicat!"
         */
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("=====>>>Minicat start on port：" + port);

        /*while(true) {
            Socket socket = serverSocket.accept();
            // 有了socket，接收到请求，获取输出流
            OutputStream outputStream = socket.getOutputStream();
            String data = "Hello Minicat!";
            String responseText = HttpProtocolUtil.getHttpHeader200(data.getBytes().length) + data;
            outputStream.write(responseText.getBytes());
            socket.close();
        }*/


        /**
         * 完成Minicat 2.0版本
         * 需求：封装Request和Response对象，返回html静态资源文件
         */
        /*while(true) {
            Socket socket = serverSocket.accept();
            InputStream inputStream = socket.getInputStream();

            // 封装Request对象和Response对象
            Request request = new Request(inputStream);
            Response response = new Response(socket.getOutputStream());

            response.outputHtml(request.getUrl());
            socket.close();

        }*/


        /**
         * 完成Minicat 3.0版本
         * 需求：可以请求动态资源（Servlet）
         */
        /*while(true) {
            Socket socket = serverSocket.accept();
            InputStream inputStream = socket.getInputStream();

            // 封装Request对象和Response对象
            Request request = new Request(inputStream);
            Response response = new Response(socket.getOutputStream());

            // 静态资源处理
            if(servletMap.get(request.getUrl()) == null) {
                response.outputHtml(request.getUrl());
            }else{
                // 动态资源servlet请求
                HttpServlet httpServlet = servletMap.get(request.getUrl());
                httpServlet.service(request,response);
            }

            socket.close();

        }
*/

        /*
            多线程改造（不使用线程池）
         */
        /*while(true) {
            Socket socket = serverSocket.accept();
            RequestProcessor requestProcessor = new RequestProcessor(socket,servletMap);
            requestProcessor.start();
        }*/



        System.out.println("=========>>>>>>使用线程池进行多线程改造");
        /*
            多线程改造（使用线程池）
         */
       /* while(true) {

            Socket socket = serverSocket.accept();
            RequestProcessor requestProcessor = new RequestProcessor(socket,servletMap);
            //requestProcessor.start();
            threadPoolExecutor.execute(requestProcessor);
        }*/

        /**
         * 完成Minicat 4.0版本
         */
        while(true) {

            Socket socket = serverSocket.accept();
            RequestProcessor requestProcessor = new RequestProcessor(socket,content2Wrapper,appBase);
            //requestProcessor.start();
            threadPoolExecutor.execute(requestProcessor);
        }

    }

    //加载解析server.xml得到port和appBase
    private void loadPortAndAppBase(){
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("server.xml");
        SAXReader saxReader = new SAXReader();

        try {
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();
            //获取port
            List<Element> connNodes = rootElement.selectNodes("//Connector");
            Element connElement =  connNodes.get(0);
            port = Integer.parseInt(connElement.attributeValue("port"));
            //获取host
            List<Element> hostNodes = rootElement.selectNodes("//Host");
            Element hostElement =  hostNodes.get(0);
            //获取appBase
            appBase = hostElement.attributeValue("appBase");
            //加载appBase目录下的各个项目的servlet
            loadAppBaseServlet();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    //加载appBase目录下的各个项目的servlet
    private void loadAppBaseServlet(){
        File File = new File(appBase);
        File[] files = File.listFiles();
        for (File file : files) {
            if(!file.isDirectory()){
                continue;
            }
            //解析web.xml并加载servlet
            String webXmlPath = file.getAbsolutePath()+"/"+"web.xml";
            File webXmlFile = new File(webXmlPath);
            if(!webXmlFile.exists()){
                continue;
            }
            try {
                InputStream inputStream = new FileInputStream(webXmlFile);
                SAXReader saxReader = new SAXReader();
                Document document = saxReader.read(inputStream);
                Element rootElement = document.getRootElement();
                //解析web.xml文件的节点元素，将url与servlet的映射关系存储到servletMap
                Map<String,HttpServlet> servletMap = new HashMap<String,HttpServlet>();
                List<Element> selectNodes = rootElement.selectNodes("//servlet");
                for (int i = 0; i < selectNodes.size(); i++) {
                    Element element = selectNodes.get(i);
                    // <servlet-name>lagou</servlet-name>
                    Element servletnameElement = (Element) element.selectSingleNode("servlet-name");
                    String servletName = servletnameElement.getStringValue();
                    // <servlet-class>server.LagouServlet</servlet-class>
                    Element servletclassElement = (Element) element.selectSingleNode("servlet-class");
                    String servletClass = servletclassElement.getStringValue();


                    // 根据servlet-name的值找到url-pattern
                    Element servletMapping = (Element) rootElement.selectSingleNode("/web-app/servlet-mapping[servlet-name='" + servletName + "']");
                    // /lagou
                    String urlPattern = servletMapping.selectSingleNode("url-pattern").getStringValue();
                   // servletMap.put(urlPattern, (HttpServlet) Class.forName(servletClass).newInstance());

                    String pakageDir = file.getAbsolutePath().replaceAll("\\\\","/");
                    MyClassLoader loader = new MyClassLoader(pakageDir+"/");
                    Class<?> clazz  = loader.findClass(servletClass);
                    //存储url与servlet的映射关系
                    servletMap.put(urlPattern, (HttpServlet) clazz.getClassLoader().loadClass(servletClass).newInstance());
                    //存储项目名与servletMap的映射关系
                    System.out.println("file.getName()="+file.getName());
                    content2Wrapper.put(file.getName(),servletMap);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }

    }
    //存储项目名与servletMap的映射关系
    private Map<String, Map<String, HttpServlet>> content2Wrapper = new HashMap<>();

    private Map<String,HttpServlet> servletMap = new HashMap<String,HttpServlet>();

    /**
     * 加载解析web.xml，初始化Servlet
     */
    private void loadServlet() {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("web.xml");
        SAXReader saxReader = new SAXReader();

        try {
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();

            List<Element> selectNodes = rootElement.selectNodes("//servlet");
            for (int i = 0; i < selectNodes.size(); i++) {
                Element element =  selectNodes.get(i);
                // <servlet-name>lagou</servlet-name>
                Element servletnameElement = (Element) element.selectSingleNode("servlet-name");
                String servletName = servletnameElement.getStringValue();
                // <servlet-class>server.LagouServlet</servlet-class>
                Element servletclassElement = (Element) element.selectSingleNode("servlet-class");
                String servletClass = servletclassElement.getStringValue();


                // 根据servlet-name的值找到url-pattern
                Element servletMapping = (Element) rootElement.selectSingleNode("/web-app/servlet-mapping[servlet-name='" + servletName + "']");
                // /lagou
                String urlPattern = servletMapping.selectSingleNode("url-pattern").getStringValue();
                servletMap.put(urlPattern, (HttpServlet) Class.forName(servletClass).newInstance());

            }



        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }


    /**
     * Minicat 的程序启动入口
     * @param args
     */
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        try {
            // 启动Minicat
            bootstrap.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
