import java.applet.Applet;

import net.propero.rdp.Rdesktop;
import net.propero.rdp.RdesktopException;

import com.tigervnc.vncviewer.VncViewer;

public class Display extends Applet{

	public void init(){
		System.out.println("version 1");
		String ip = getParameter("ip");
		String port = getParameter("port");
		String displaytype = getParameter("displaytype");
		System.out.println("Starting " + displaytype + " display on " + ip + ":" + port);
		startDislay(ip, port, displaytype);
	}
	
	public void startVNC(String ip, String port){
		System.out.println("Starting vnc ...");
		VncViewer.main(new String[]{"HOST", ip,"PORT", port});
	}
	
	public void startRDP(String ip, String port){
		System.out.println("Starting rdp ...");
		try{
			String screensize = getParameter("screensize");
			String windowtitle = getParameter("windowtitle");
			String lang = getParameter("lang");
			Rdesktop.main(new String[]{"-g",screensize,"-m", lang, ip + ":" + port, "-T", windowtitle});
		}
		catch(RdesktopException e){
			e.printStackTrace();
		}
	}
	
	public void startDislay(String ip, String port, String type){
		if(type.equalsIgnoreCase("vnc")){
			startVNC(ip, port);
		}
		else if(type.equalsIgnoreCase("rdp")){
			startRDP(ip, port);
		}
	}
}
