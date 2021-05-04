package server;

import java.io.InputStream;
import java.net.Socket;
import java.util.Map;

public class RequestProcessor extends Thread {

    private Socket socket;
    private Map<String, Map<String, HttpServlet>> content2Wrapper;
    private String appBase;

    public RequestProcessor(Socket socket, Map<String, Map<String, HttpServlet>> content2Wrapper,String appBase) {
        this.socket = socket;
        this.content2Wrapper = content2Wrapper;
        this.appBase = appBase;
    }

    @Override
    public void run() {
        try{
            InputStream inputStream = socket.getInputStream();

            // 封装Request对象和Response对象
            Request request = new Request(inputStream);
            Response response = new Response(socket.getOutputStream());
            //解析url
            String url = request.getUrl();
            System.out.println("url=="+url);
            //项目名
            String webPath = url.split("/")[1];
            Map<String,HttpServlet> servletMap = content2Wrapper.get(webPath);
            if(servletMap == null){
                response.outputHtml("404 Not Found");
                return;
            }
            HttpServlet httpServlet = servletMap.get(url.replace("/"+url.split("/")[1],""));
            // 静态资源处理
            if(httpServlet == null) {
                response.outputHtml(appBase+request.getUrl());
            }else{
                // 动态资源servlet请求
                httpServlet.service(request,response);
            }

            socket.close();

        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}
