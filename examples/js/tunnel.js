dojo.require("dojo.string");

var tunnel = {

  // name of this object
  _name: "tunnel",

  publish: function(/*String*/event){
    var _exception, _message;
    switch(event){
      case "Init":
        this.onInit();
        break;
      default:
        _exception = arguments[1] || "";
        _message = arguments[2] || "";
        this.onError(_exception, _message);
        break;
    }
  },

  onInit: function(){},

  onError: function(exception, message){},
  
  create: function(args){
    var t = [
'<object type="application/x-java-applet" id="tunnel" width="1" height="1" >',
  '<param name="mayscript" value="true">',
  '<param name="archive" value="/applet/evd.jar">',
  '<param name="code" value="Tunnel">',      
  '<param name="host" value="${host}" >',
  '<param name="remoteip" value="${remoteip}" >',
  '<param name="localport" value="${localport}" >',
  '<param name="remoteport" value="${remoteport}" >',
  '<param name="username" value="${username}" >',
  '<param name="callback" value="tunnel.publish" >',      
'</object>'].join('\n');
    args.name = this._name;
    dojo.place(dojo.string.substitute(t, args), args.appendto);
  }
};