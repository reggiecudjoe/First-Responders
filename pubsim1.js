pn = require('pubnub');
var truckLat = 38.897327;
var truckLng = -77.011432;
var lat = 38.896660;
var lng = -77.009087;
var watson = false;

step = 0;
p = new pn({
  publishKey: "pub-c-06ffc4af-724e-4f80-a295-e3224d244e32",
  subscribeKey: "sub-c-76162f2a-ab2b-11e7-b4e4-2675c721e615"
});
var x = setInterval(function() {
  if (step < 11){
  	truckLng = truckLng + 0.0002;
  } else {
  	truckLat = truckLat + 0.0002;
  }

  if (step < 6){
  	lat = lat + 0.00005;
  }
  if (step > 13){
  	lat = lat + 0.0001;
  }
  // truckLat = truckLat + .00005;

  step = step + 1;
  return p.publish({
    channel: "maps-channel",
    message: {
      truckLat: truckLat.toString(),
      truckLng: truckLng.toString(),
      lat: lat.toString(),
      lng: lng.toString(),
      watson: watson.toString()
    }
  }, function() {

  if (step == 21){
  	// clearInterval(x);
  	step = 0;
  	truckLng = -77.011232;
  	truckLat = 38.897327;
  	lat = 38.896666;

  }
    return console.log(arguments);
  });
}, 500);