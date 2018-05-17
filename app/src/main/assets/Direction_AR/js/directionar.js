var newLat = 12.32;
var newLng = 122.53;
var map;
var userPos;
var propertyPos;
var rotation;
var startIcon;
var startMarker;
var stepMarkerAltitude;

var stepMarkerLat = [];
var stepMarkerLng = [];
var stepMarkerSrc = [];
var NearMarker = 0;
var isMarkerNearby = false;
var markerAlt;
var globalCount;
var propName;
var directionsDisplay;

var drawing;

$('document').ready(function(){
    
    var arrowHolderClasses = document.getElementById("arrowHolder").classList;
    var mapHolderClasses = document.getElementById("mapHolder").classList;
    var mapClasses = document.getElementById("map").classList;
    var backClasses = document.getElementById("back").classList;
    
    document.getElementById("mapHolder").style.WebkitTransition = "all 0.5s"; // Code for Safari 3.1 to 6.0
    document.getElementById("mapHolder").style.transition = "all 0.5s";       // Standard syntax
   
    arrowHolderClasses.add('arrowHolderCircle');
    mapHolderClasses.add('mapHolderCircle');
    mapClasses.add('mapCircle');
    backClasses.add('backCircle');
    
    $('#back').click(function() {
        arrowHolderClasses.add('arrowHolderCircle');
        mapHolderClasses.add('mapHolderCircle');
        mapClasses.add('mapCircle');
        backClasses.add('backCircle');
        
        arrowHolderClasses.remove('arrowHolderFull');
        mapHolderClasses.remove('mapHolderFull');
        mapClasses.remove('mapFull');
        backClasses.remove('backFull');
        
    });

    
    $('#mapHolder').click(function() {
        arrowHolderClasses.remove('arrowHolderCircle');
        mapHolderClasses.remove('mapHolderCircle');
        mapClasses.remove('mapCircle');
        backClasses.remove('backCircle');
        
        arrowHolderClasses.add('arrowHolderFull');
        mapHolderClasses.add('mapHolderFull');
        mapClasses.add('mapFull');
        backClasses.add('backFull');
        
    });
        
});

var north = {
    lat: 90,
    lng: 0
}

var initBearing;
var finBearing;

function loadRotationFromJava(rotation, hasSensor){
    if(hasSensor == true){
        var markIndex;
        if(stepMarkerLat.length > 0){
            var temp = stepMarkerLat.length - 1;
            if(NearMarker == temp){
                markIndex = NearMarker;
            }
            else{
                markIndex = NearMarker + 1;
            }
            
           initBearing = bearingInitial(userPos.lat, userPos.lng, stepMarkerLat[markIndex], stepMarkerLng[markIndex]);
            finBearing = bearingFinal(userPos.lat, userPos.lng, stepMarkerLat[markIndex], stepMarkerLng[markIndex]);
            
            var arrowRot = finBearing + rotation;
        }
        $("#greenArrow").rotate(arrowRot);
    }
    if(hasSensor == false){
        alert("Compass will not work because required sensors not found on device.");
    }

}


function bearingInitial (lat1, long1, lat2, long2)
{
    return (bearingDegrees(lat1, long1, lat2, long2) + 360) % 360;
}

function bearingFinal(lat1, long1, lat2, long2) {
    return (bearingDegrees(lat2, long2, lat1, long1) + 180) % 360;
}

function bearingDegrees (lat1, long1, lat2, long2)
{
    var degToRad= Math.PI/180.0;

    var phi1= lat1 * degToRad;
    var phi2= lat2 * degToRad;
    var lam1= long1 * degToRad;
    var lam2= long2 * degToRad;

    return Math.atan2(Math.sin(lam2-lam1) * Math.cos(phi2),
                      Math.cos(phi1)*Math.sin(phi2) - Math.sin(phi1)*Math.cos(phi2)*Math.cos(lam2-lam1)
                     ) * 180/Math.PI;
}


function loadValuesFromJava(altitude,latitude,longitude,propertyLatitude,propertyLongitude,count){
    globalCount = count;
    markerAlt = altitude;
   
    userPos = {
        lat: latitude,
        lng: longitude
    }
    
    propertyPos = {
        lat: propertyLatitude,
        lng: propertyLongitude
    }
    
    if(count == 1){
        
        map = new google.maps.Map(document.getElementById('map'), {
            zoom: 16,    
            center: userPos
        });
        
        startIcon = {
                path: google.maps.SymbolPath.FORWARD_CLOSED_ARROW,
                fillColor: '#e73f3e',
                fillOpacity: 1,
                scale: 7,
                strokeColor: '#ca2f2d',
                strokeWeight: 1
            };
            
        startMarker = new google.maps.Marker({
            position: userPos,
            map: map,
            icon: startIcon
        });
            
        var endMarker = new google.maps.Marker({
            position: propertyPos,
            map: map
        });
        
        getDirection();
        
    }
    else if(count > 1){
        updateUI();
    }
}

function updateUI(){
    map.setCenter(userPos);
    startMarker.setPosition(userPos);
    
    if(isMarkerNearby == true){
        checkNearMarker();
    }else{
        checkMarkerStatus();
    }
}

function checkNearMarker(){
    
    var rad = function(x) {
        return x * Math.PI / 180;
    };
    
    var getDistance = function() {
        var R = 6378137; // Earth’s mean radius in meter
        var dLat = rad(stepMarkerLat[NearMarker] - userPos.lat);
        var dLong = rad(stepMarkerLng[NearMarker] - userPos.lng);

        var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(rad(userPos.lat)) * Math.cos(rad(stepMarkerLat[NearMarker])) *
                Math.sin(dLong / 2) * Math.sin(dLong / 2);

        var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        var d = R * c;
                    
        return d; // returns the distance in meter
    };
        
    var distance = getDistance();
    
    if(distance > 15){
        AR.context.destroyAll();
        isMarkerNearby = false;
    }
}

function getDirection() {
    var directionsService = new google.maps.DirectionsService;
    directionsDisplay = new google.maps.DirectionsRenderer({suppressMarkers: true});
    var leg;
    
    directionsDisplay.setMap(map);
    
    directionsService.route({
        origin: userPos,
        destination: propertyPos,
        travelMode: 'WALKING',
        }, function(response, status) {
        if (status === 'OK') {
            directionsDisplay.setDirections(response);
            var route = response.routes[0];
            leg = response.routes[0].legs[0];
            
            setAugmentedMarkers(response);
        }
        else {
            window.alert('Directions request failed due to ' + status);
          }
        });
}

function setAugmentedMarkers(directionResult) {
        // For each step, place a marker, and add the text to the marker's infowindow.
        // Also attach the marker to an array so we can keep track of it and remove it
        // when calculating new routes.
    var myRoute = directionResult.routes[0].legs[0];
        
    for (var i = 1; i < myRoute.steps.length; i++) {
        
        var stepMarkerLoc = myRoute.steps[i].start_location;
        
         var objectMarker = new google.maps.Marker({
            position: stepMarkerLoc,
            map: map
        });

        stepMarkerLat[i] = stepMarkerLoc.lat();
        stepMarkerLng[i] = stepMarkerLoc.lng();
        
        var text = $.trim(myRoute.steps[i].instructions);
        
        if(text.indexOf( "left" ) != -1){
            stepMarkerSrc[i] = "assets/turnLeft.png";
        }else if(text.indexOf( "right" ) != -1){
            stepMarkerSrc[i] = "assets/turnRight.png";
        }else {
            stepMarkerSrc[i] = "assets/straight.png";
        }
    }
    
    stepMarkerLat[myRoute.steps.length] = propertyPos.lat;
    stepMarkerLng[myRoute.steps.length] = propertyPos.lng;
    stepMarkerSrc[myRoute.steps.length] = "assets/marker.png";
    
    checkMarkerStatus();
    
}

function checkMarkerStatus() {
     var rad = function(x) {
            return x * Math.PI / 180;
        };
    
    for(var i = 1; i < stepMarkerLat.length; i++){
        
        var getDistance = function() {
            var R = 6378137; // Earth’s mean radius in meter
            var dLat = rad(stepMarkerLat[i] - userPos.lat);
            var dLong = rad(stepMarkerLng[i] - userPos.lng);

            var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.cos(rad(userPos.lat)) * Math.cos(rad(stepMarkerLat[i])) *
                    Math.sin(dLong / 2) * Math.sin(dLong / 2);

            var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            var d = R * c;
                    
            return d; // returns the distance in meter
        };
        
        var distance = getDistance();
        
        if(distance < 15){
            augmentedStepMarkers.create(i);
            NearMarker = i;
            isMarkerNearby = true;
        }
    } 
        
}

var augmentedStepMarkers = {
    create: function(index) {
        var stepMarkerLocation = new AR.GeoLocation(stepMarkerLat[index], stepMarkerLng[index], markerAlt);
        augmentedStepMarkers.markerDrawable_idle = new AR.ImageResource(stepMarkerSrc[index]);

        var markerImageDrawable_idle = new AR.ImageDrawable(augmentedStepMarkers.markerDrawable_idle, 1.5, {
            zOrder: 0,
            opacity: 1.0
        });

        // create GeoObject
        var stepMarkerObject = new AR.GeoObject(stepMarkerLocation, {
            drawables: {
                cam: [markerImageDrawable_idle]
            }
        });
    },
    
    onLocationChanged: function onLocationChangedfn(userLatitude, userlongitude, userAltitude, userAccuracy){
        stepMarkerAltitude = userAltitude;
    }     
    
};

AR.context.onLocationChanged = augmentedStepMarkers.onLocationChanged;