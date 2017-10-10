pn = require('pubnub');
var lat = 38.896520;
var lng = -77.009087;
var watson = 0;

step = 0;
p = new pn({
  publishKey: "pub-c-06ffc4af-724e-4f80-a295-e3224d244e32",
  subscribeKey: "sub-c-76162f2a-ab2b-11e7-b4e4-2675c721e615"
});
var x = setInterval(function() {
  
  if (step < 9){
  	lat = lat + 0.00005;
  }
  if (step == 8){
    watson = 999;
  }
  if (step > 12){
    watson = 69;
  	lat = lat + 0.0001;
  }
  

  step = step + 1;
  return p.publish({
    channel: "maps-channel",
    message: {
      lat: lat.toString(),
      lng: lng.toString(),
      watson: watson.toString()
    }
  }, function() {

  if (step == 21){
  	// clearInterval(x);
  	step = 0;
  	lat = 38.896520;
    watson = 0;
  }
    return console.log(arguments);
  });
}, 650);