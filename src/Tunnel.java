import java.applet.Applet;
import java.security.AccessController;
import java.security.PrivilegedAction;

import netscape.javascript.JSObject;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class Tunnel extends Applet{

	private String callback;
	private JSObject js;
	
	// shared accross applets loaded with the same uri (same class loader) 
	private static Session session;
	
	@Override
	public void destroy() {
		System.out.println("destroying tunnel ...");
		session.disconnect();
		super.destroy();
	}

	@Override
	public void init() {
		String username = getParameter("username");
		String host = getParameter("host");
		int localport = Integer.parseInt(getParameter("localport"), 10);
		int remoteport = Integer.parseInt(getParameter("remoteport"), 10);
		String remoteip = getParameter("remoteip");
		js = JSObject.getWindow(this);
		if(js == null){
			throw new RuntimeException("WTF? Stupid browser");
		}
		callback = getParameter("callback");
		boolean success = elevatePrivsAndStartTunnel(localport, remoteip, remoteport, host, username);
		if(success){
			publishEvent("Init");
		}
	}
	
	private void publishEvent(String event, String ... messages){
		String args = "\"" + event + "\"";
		for(String s : messages){
			args += ", \"" + s + "\""; 
		}		
		System.out.println(callback + "(" + args + ")");
		js.eval(callback + "(" + args + ")");
	}
	
	private void publishEvent(String event){
		publishEvent(event, "");
	}
	
	@SuppressWarnings("unchecked")
	private boolean elevatePrivsAndStartTunnel(final int localport, final String remoteip, final int remoteport, final String host, final String username){
		
		return (Boolean)AccessController.doPrivileged(new PrivilegedAction() {

			@Override
			public Object run() {
				return startTunnel(localport, remoteip, remoteport, host, username);
			}	
		});
	}
	
	public boolean startTunnel(int localport, String remoteip, int remoteport, String host, String username){
		System.out.println("ssh -L " + localport + ":" + remoteip + ":" + remoteport + " " + username + "@" + host);
		try{
			if(session != null){
				System.out.println("destryoing existing tunnel ...");
				session.disconnect();
			}
			session = new JSch().getSession(username, host, 22);			
			session.setUserInfo(new User());
			session.connect();
			session.setPortForwardingL(localport, remoteip, remoteport);
		}
		catch(JSchException e){
			Throwable cause = e.getCause();
			publishEvent("Error", cause.getClass().getSimpleName(), cause.getMessage());
			e.printStackTrace();				
			return false;
		}
		return true;
	}
	
	public static void main(String[] args){
		Tunnel t = new Tunnel();
		t.startTunnel(5900, "10.0.0.112", 6107, "valve001.irigo.com", "jakob");
	}
}
