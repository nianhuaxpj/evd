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

  _java: null,
  
  publish: function(/*String*/event){
    // some safari bug creates a deadlock if we continue to call
    // detach here
    var self = this;

    setTimeout(
      function(){
        var _exception, _message;
        console.log('event:', event);
        switch(event){
          case "Init":
            self.onInit();
            break;
          case "Connected":
            self.onConnect();
            break;
          case "PasswordError":
            console.log('password error');
            self.askPassword();
            break;
          default:
            _exception = arguments[1] || "";
            _message = arguments[2] || "";
            self.onError(_exception, _message);
            break;
        }
      }, 0);

  },

  // called when tunnel is started
  onConnect: function(){},

  // called when applet is ready
  onInit: function(){},

  onError: function(exception, message){},

  askPassword: function(){
    var content, dialog;
    dialog = this._password_dialog;
    if(!dialog){
      dialog = new dijit.Dialog(
      {
        onCancel: function(){ dialog.hide(); return true;},
        onClose: function(){ dialog.hide(); return true;},
        title: "Enter password",
        style: "width: 300px"
      });
      content = [
      '<div dojoType="dijit.form.Form" onSubmit="return ' + this._name + '.askPasswordSubmit()">',
      '<table>',
      '<tr><th>Password:</th><td><input id="askPasswordInput" type="password" name="password" dojoType="dijit.form.TextBox"></input></td></tr>',
      '</table>',
      '<button type="submit">OK</button>',
      '</div>'].join('');
      dialog.set('content', content);
      console.log(this._name);
      this._password_dialog = dialog;
    }
    dialog.show();
  },

  askPasswordSubmit: function(form){
    this._password = dijit.byId('askPasswordInput').get('value');
    this.create(this._args);
    this._password_dialog.hide();
    return false; // don't submit form
  },

  create: function(args){
    var self = this;
    self._args = args;

    if(self._password === null){
      self.askPassword();
      return;
    }

    var handle = dojo.connect(this, 'onInit', function(){
      self._java = dojo.byId('tunnel');
      self._java.start(args.localport, args.remoteip, args.remoteport, args.host, args.username, self._password);
      dojo.disconnect(handle);
    });

    var t = [
'<object type="application/x-java-applet" id="tunnel" width="1" height="1" >',
  '<param name="mayscript" value="true">',
  '<param name="archive" value="/applet/evd.jar?v=' + version + '">',
  '<param name="code" value="Tunnel">',      
  '<param name="callback" value="tunnel.publish" >',      
'</object>'].join('\n');
    dojo.place(dojo.string.substitute(t, args), args.appendto);
  }
};
