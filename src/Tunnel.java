import java.applet.Applet;
import java.security.AccessController;
import java.security.PrivilegedAction;

import netscape.javascript.JSException;
import netscape.javascript.JSObject;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class Tunnel extends Applet{

	// parameters 
	private JSObject js;
	
	@Override
	public void destroy() {
		super.destroy();
	}

	@Override
	public void init() {
		String host;
		String remoteip;
		int localport;
		int remoteport;
		String username;
		host = getParameter("host");
		localport = Integer.parseInt(getParameter("localport"), 10);
		remoteport = Integer.parseInt(getParameter("remoteport"), 10);
		remoteip = getParameter("remoteip");
		username = getParameter("username");
		try{
			js = (JSObject)JSObject.getWindow(this).eval(getParameter("js"));
		}
		catch(JSException e){
			e.printStackTrace();
		}
		boolean success = elevatePrivsAndStartTunnel(localport, remoteip, remoteport, host, username);
		if(success){
			publishEvent("Init");
		}
	}
	
	private void publishEvent(String event, String message){
		if(js != null){
			Object[] args = new String[]{event, message};
			js.call("publish", args);
		}
	}
	
	private void publishEvent(String event){
		publishEvent(event, null);
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

		JSch ssh = new JSch();
		
		try{
			Session session = ssh.getSession(username, host, 22);
			session.setUserInfo(new User());
			session.connect();
			session.setPortForwardingL(localport, remoteip, remoteport);
			System.out.println("connected");
		}
		catch(JSchException e){
			publishEvent("Error", e.getMessage() + "\n" + e.getCause().getMessage());
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
