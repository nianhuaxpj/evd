dojo.require("dojo.string");
var display = {
  
  _name: "display",

  _applet: null,

  publish: function(/*String*/ event, /*String*/message){
    var self = this;
    setTimeout(function(){
      switch(event){
        case "Init":
          self.onInit();
          break;
        case "Destroyed":
          self.onDestroy();
          break;
        default:
          self.onError();
          break;
      }
    }, 0);
  },

  // FIXME: closing the window on the applet doesn't destroy the applet
  // therefore this is only called when removing applet from the DOM.
  onDestroy: function(){},

  onInit: function(){},
  
  onError: function(){},

  getObjectHTML: function(args){
    args = dojo.mixin({local_port:"",display_type:"", screen_size:"",lang:"",window_title:""}, args);
    var t = [
'<object type="application/x-java-applet" id="display" width="1" height="1">',
  '<param name="mayscript" value="true">',
  '<param name="archive" value="/applet/evd.jar?v=' + new Date().getTime() + '">',
  '<param name="code" value="Display" >',
  '<param name="port" value="${local_port}" >',
  '<param name="host" value="localhost" >',
  '<param name="display_type" value="${display_type}" >',
  '<param name="screen_size" value="${screen_size}" >',
  '<param name="lang" value="${lang}" >',
  '<param name="window_title" value="${window_title}" >',
  '<param name="callback" value="' + this._name + '.publish" >',
'</object>'].join('');
    return dojo.string.substitute(t, args);
  },
  
  start: function(args){
    var self = this;
    display.destroy('display');

    var handle = dojo.connect(tunnel, "onConnect", function(){
      var html = display.getObjectHTML(args);
      dojo.place(html, args.append_to);
      dojo.disconnect(handle);                          
    });

    var handle2 = dojo.connect(tunnel, "onDisconnect", function(){
      self.destroy();                                   
      dojo.disconnect(handle2);                              
    });

    tunnel.connect(args);
  },

  destroy: function(){
    dojo.destroy('display');
  }

};
