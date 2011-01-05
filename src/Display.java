import java.applet.Applet;

import net.propero.rdp.Rdesktop;
import net.propero.rdp.RdesktopException;
import netscape.javascript.JSObject;

import com.tigervnc.vncviewer.VncViewer;

public class Display extends Applet{

	private String window_title;
	private String port;
	private String host;
	private String display_type;
	private String callback;
	private JSObject js;
	
	public void init(){
		js = JSObject.getWindow(this);
		port = getRequiredParameter("port");
		host = getRequiredParameter("host");
		display_type = getRequiredParameter("display_type");
		window_title = getRequiredParameter("window_title");
		callback = getRequiredParameter("callback");
		System.out.println("Starting " + display_type + " display on " + host + ":" + port);
		startDislay(display_type);
		publishEvent("Init");
	}
	
	@Override
	public void destroy() {
		System.out.println("destroying display ...");
		publishEvent("Destroyed");
		super.destroy();
	}

	public void startVNC(){
		System.out.println("Starting vnc ...");
		VncViewer.main(new String[]{
				"host", host, 
				"port", port, 
				"window_title", window_title, 
				"show_controls", "no", 
				"new_window", "yes"
				});
		
		// the only reason to do so is that system.exit shuts down
		// FF and Safari on the Mac.
		VncViewer.inAnApplet = true;
	}
	
	public void startRDP(){
		System.out.println("Starting rdp ...");
		try{
			String screen_size = getRequiredParameter("screen_size");
			String lang = getRequiredParameter("lang");
			
			// so the JVM is not shutdown on exit
			// which causes Firefox mac to shutdown
			net.propero.rdp.Common.underApplet = true;
			Rdesktop.main(new String[]{"-g",screen_size,"-m", lang, host + ":" + port, "-T", window_title});
		}
		catch(RdesktopException e){
			e.printStackTrace();
		}
	}
	
	public void startDislay(String type){
		if(type.equalsIgnoreCase("vnc")){
			startVNC();
		}
		else if(type.equalsIgnoreCase("rdp")){
			startRDP();
		}
	}
	
	private String getRequiredParameter(String name){
		String value = getParameter(name);
		if(value == null){
			throw new RuntimeException("Missing required parameter: " + name);
		}
		return value;
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
}
