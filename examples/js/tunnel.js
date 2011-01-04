dojo.require("dojo.string");
dojo.require("dijit.form.Form");
dojo.require("dijit.form.Button");
dojo.require("dijit.form.TextBox");
dojo.require("dijit.Dialog");

var tunnel = {

  // name of this object
  _name: "tunnel",

  _password_dialog: null,

  _password: null,

  _args: null,

  _applet: null,

  is_connected: false,
  
  publish: function(/*String*/event, /*String*/message){
    // some safari bug creates a deadlock if we continue to call
    // detach here
    var self = this;

    setTimeout(
      function(){
        console.log('event:', event);
        switch(event){
          case "Init":
            self.onInit();
            break;
          case "Connected":
            self.onConnect();
            break;
          case "Destroyed":
            self.onDestroy();
            break;
          case "PasswordError":
            console.log('password error');
            self.askPassword();
            break;
          default:
            self.onError(message);
            break;
        }
      }, 0);

  },

  // called when tunnel is started
  onConnect: function(){
    console.log("connected");
    this.is_connected = true;
  },
  
  // called when applet is destroyed
  onDestroy: function(){
    console.log('tunnel.onDestroy');
    this.is_connected = false;
    this._applet = null;
    dojo.destroy('tunnel');
  },

  onDisconnect: function(){
    console.log('tunnel.onDisconnect');
  },

  // called when applet is ready
  onInit: function(){},

  onError: function(message){},

  askPassword: function(){
    var content, dialog;
    dialog = this._password_dialog;
    if(!dialog){
      dialog = new dijit.Dialog(
      {
        onCancel: function(){ tunnel.onError("Password input cancelled"); dialog.hide(); return true;},
        onClose: function(){ tunnel.onError("Password input cancelled"); dialog.hide(); return true;},
        title: "Enter password",
        style: "width: 300px"
      });
      content = [
      '<div dojoType="dijit.form.Form" onSubmit="return ' + this._name + '.askPasswordSubmit()">',
      '<table>',
      '<tr><th>Password:</th><td><input id="askPasswordInput" type="password" name="password" dojoType="dijit.form.TextBox" /></td></tr>',
      '</table>',
      '<div><button type="button" dojoType="dijit.form.Button">OK</button></div>',
      '</div>'].join('');
      dialog.set('content', content);
      this._password_dialog = dialog;
    }
    dialog.show();
  },

  askPasswordSubmit: function(form){
    this._password = dijit.byId('askPasswordInput').get('value');
    this.connect(this._args);
    this._password_dialog.hide();
    return false; // don't submit form
  },

  _inject_applet: function(args){
    var self = this;

    var handle = dojo.connect(this, 'onInit', function(){
      self._applet = dojo.byId('tunnel');
      self._applet.connect(args.local_port, args.remote_ip, args.remote_port, args.host, args.username, self._password);
      dojo.disconnect(handle);
    });

    var t = [
'<object type="application/x-java-applet" id="tunnel" width="1" height="1" >',
  '<param name="mayscript" value="true">',
  '<param name="archive" value="/applet/evd.jar?v=' + new Date().getTime() + '">',
  '<param name="code" value="Tunnel">',      
  '<param name="callback" value="' + this._name + '.publish" >',      
'</object>'].join('\n');
    dojo.place(t, args.append_to);
  },

  connect: function(args){
    console.log("tunnel connect", "localhost:" + args.local_port, args.remote_ip + ":" + args.remote_port);
    var self = this;
    try{
      if(self.is_connected){
        self.onConnect();
        return;
      }

      if(self._password === null){
        self._args = args;
        self.askPassword();
        return;
      }
      
      if(!self._applet){
        self._inject_applet(args);
        return;
      }
      
      self._applet.connect(args.local_port, args.remote_ip, args.remote_port, args.host, args.username, self._password);
    } catch (e) {
      console && console.log("Exception:", e);
    }
  },
  
  disconnect: function(){
    if(this.is_connected){
      this._applet.disconnect();
      this.is_connected = false;
      this.onDisconnect();
    }
  },

  destroy: function(){
    this.disconnect();    
    if(this._applet){
      this._applet.destroy();      
    }
  }
};

