import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import com.jcraft.jsch.UserInfo;


public class User implements UserInfo{

	private String password;
	
	@Override
	public String getPassphrase() {
		return null;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public boolean promptPassphrase(String message) {
		return true;
	}

	@Override
	public boolean promptPassword(String message) {
		JPasswordField pwdField = new JPasswordField();
		Object[] obj = {"Password:", pwdField};
		int result = JOptionPane.showOptionDialog(null, obj, "Enter password", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"OK"}, obj);
		if (result == JOptionPane.YES_OPTION){
			password = new String(pwdField.getPassword());
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public boolean promptYesNo(String message) {
		if (message != null && message.startsWith("The authenticity of host")){
			  return true;
		}       
		Object[] options={ "yes", "no" };
		int result = JOptionPane.showOptionDialog(null, message, "Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
		return result == 0;
	}

	@Override
	public void showMessage(String message) {
		JOptionPane.showMessageDialog(null, message);
		
	}

	
}
