dojo.require("dojo.string");
var display = {

  _start_display: function(args){
    args = dojo.mixin({localport:"",displaytype:"", screensize:"",lang:"",windowtitle:""}, args);
    var t = [
'<object type="application/x-java-applet" id="display" width="1" height="1">',
  '<param name="mayscript" value="true">',
  '<param name="archive" value="/applet/evd.jar">',
  '<param name="code" value="Display" >',
  '<param name="ip" value="localhost" >',
  '<param name="port" value="${localport}" >',
  '<param name="host" value="localhost" >',
  '<param name="displaytype" value="${displaytype}" >',
  '<param name="screensize" value="${screensize}" >',
  '<param name="lang" value="${lang}" >',
  '<param name="windowtitle" value="${windowtitle}" >',
'</object>'].join('');
    dojo.place(dojo.string.substitute(t, args), args.appendto);
  },

  create: function(args){

    var handle = dojo.connect(tunnel, "onInit", function(){
      display._start_display(args);
      dojo.disconnect(handle);                          
    });

    dojo.connect(tunnel, "onError", function(message){
        alert(message);
    });

    tunnel.create(args);
  }
};