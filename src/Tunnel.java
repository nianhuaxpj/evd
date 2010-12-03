import java.applet.Applet;
import java.security.AccessController;
import java.security.PrivilegedAction;

import netscape.javascript.JSObject;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class Tunnel extends Applet{

	private String callback;
	private JSObject js;

	private static boolean is_wrong_password;
		
	// note: static variables are shared accross applets loaded with the same uri -
	// they have the same class loader 
	private static Session session;
	
	@Override
	public void destroy() {
		System.out.println("destroying tunnel ...");
		session.disconnect();
		super.destroy();
	}

	@Override
	public void init() {
		js = JSObject.getWindow(this);			
		callback = getParameter("callback");
		publishEvent("Init");
	}
	
	private void publishEvent(String event, String ... messages){
		String args = "\"" + event + "\"";
		for(String s : messages){
			args += ", \"" + s + "\""; 
		}		
		System.out.println(callback + "(" + args + ")");
		if(js != null){
			js.eval(callback + "(" + args + ")");
		}
	}
	
	private void publishEvent(String event){
		publishEvent(event, "");
	}
	
	@SuppressWarnings("unchecked")
	private void elevatePrivsAndStartTunnel(final int localport, final String remoteip, final int remoteport, final String host, final String username, final String password){
		
		AccessController.doPrivileged(new PrivilegedAction() {

			@Override
			public Object run() {
				startTunnel(localport, remoteip, remoteport, host, username, password);
				return null;
			}	
		});
	}
	
	public void log(String ... args){
		StringBuilder sb = new StringBuilder();
		for(String s : args){
			sb.append(",");
			sb.append(s);
		}
		System.out.println(sb.toString());
	}
	
	public void start(String localport, String remoteip, String remoteport, String host, String username, String password){
		log("start(", localport, remoteip, remoteport, host, username, password);
		int _localport = Integer.parseInt(localport, 10);
		int _remoteport = Integer.parseInt(remoteport, 10);
		elevatePrivsAndStartTunnel(_localport, remoteip, _remoteport, host, username, password);
	}
	
	private void startTunnel(int localport, String remoteip, int remoteport, String host, String username, String password){
		System.out.println("ssh -L " + localport + ":" + remoteip + ":" + remoteport + " " + username + "@" + host + " pwd:" + password);
        try{
			if(session != null){
				System.out.println("destryoing existing tunnel ...");
				session.disconnect();
			}
			session = new JSch().getSession(username, host, 22);
			session.setPassword(password);
			session.setUserInfo(new SimpleUser());
			session.connect();
			session.setPortForwardingL(localport, remoteip, remoteport);
			publishEvent("Connected");
		}
		catch(JSchException e){
			e.printStackTrace();							
			if(is_wrong_password){
				publishEvent("PasswordError");
				is_wrong_password = false;
			}
			else{
				Throwable cause = e.getCause();
				publishEvent("Error", cause.getMessage());
			}
		}
	}
	
	class SimpleUser implements UserInfo{
		public String getPassword(){ 
			// returning null will cause us to go into the JSchException handler in startTunnel
			// better suggestions for figuring out if the password is wrong?
			is_wrong_password = true;
			return null; 
		}
		public boolean promptYesNo(String str){return true;}
		public String getPassphrase(){ return null; }
		public boolean promptPassphrase(String message){return true; }
		public boolean promptPassword(String message){return true;}
		public void showMessage(String message){System.out.println(message);}
	}   
}
