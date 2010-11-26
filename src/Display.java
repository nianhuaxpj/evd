import java.applet.Applet;

import sun.plugin2.message.GetProxyMessage;

import net.propero.rdp.Rdesktop;
import net.propero.rdp.RdesktopException;
import net.propero.rdp.applet.RdpApplet;

import com.tigervnc.rfb.message.SetPixelFormat;
import com.tigervnc.vncviewer.VncViewer;

public class Display extends Applet{

	public void init(){
		String ip = getParameter("ip");
		String port = getParameter("port");
		String type = getParameter("type");
		startDislay(ip, port, type);
	}
	
	public void startVNC(String ip, String port){
		VncViewer.main(new String[]{"HOST", ip,"PORT", port});
	}
	
	public void startRDP(String ip, String port){
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
