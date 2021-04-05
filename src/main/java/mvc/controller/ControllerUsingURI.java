package mvc.controller;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.command.CommandHandler;
import mvc.command.NullHandler;

public class ControllerUsingURI extends HttpServlet{
	
	//명렁어와 처리 클래스를 쌍으로 저장하기 위해
	private Map<String, CommandHandler>commandHandlerMap = new HashMap<>();
	
	@Override
	public void init() throws ServletException {
		
		//설정파일로부터 매핑 정보를 가져와 properties객체에 저장
		String configFile = getInitParameter("configFile");
		Properties prop = new Properties();
		String configfilepath = getServletContext().getRealPath(configFile);
		
		try(FileReader fis = new FileReader(configfilepath)) {
			prop.load(fis);
		} catch (Exception e) {
			throw new ServletException(e);
		}
		
		//프로퍼티에 있는 key값을 keylter에 저장
		Iterator keylter = prop.keySet().iterator();
		
		while(keylter.hasNext()) {
			//키값을 command에 저장
			String command = (String)keylter.next();
			//value값을 handlerClassName에 저장
			String handlerClassName = prop.getProperty(command);
			
			try {
				Class<?> handlerClass = Class.forName(handlerClassName);
				//해당 클래스의 객체 생성
				CommandHandler handlerInstance = (CommandHandler)handlerClass.newInstance();
				commandHandlerMap.put(command, handlerInstance);
				
			} catch (Exception e) {
				throw new ServletException(e);
			}
		}
 	}
	
	private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException , IOException{
		String command = request.getRequestURI();
		if(command.indexOf(request.getContextPath()) == 0) {
			command = command.substring(request.getContextPath().length());
		}
		
		CommandHandler handler = commandHandlerMap.get(command);
		
		if(handler == null) {
			handler = new NullHandler();
		}
		
		String viewPage = null;
		try {
			viewPage = handler.process(request, response);
		} catch (Exception e) {
			throw new ServletException(e);
		}
		
		if(viewPage != null) {
			RequestDispatcher dispatchar = request.getRequestDispatcher(viewPage);
			dispatchar.forward(request, response);
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req, resp);
	}
}
