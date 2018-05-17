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
var markerAlt = 5;
var globalCount = 0;
var propName;
var directionsDisplay;
var nextMarker = 0;

var latTemp = 0;
var lngTemp = 0;
var angTemp = 0;

var drawing;

var meter = 3;

var label;

var initCount = 0;

$('document').ready(function(){
        
    $('#addObject').click(function() {
        label = document.getElementById("textLabel").value;
        addNewMarker();
    });
    
});

function retrieveObjects(object){
//function retrieveObjects(passLabel,passDistance,passLatitude,passLongitude){
    if(initCount == 0){
         for(var x=0; x<object.length; x++){
            label = object[x].label;
            meter = object[x].distance;
            augmentedStepMarkers.init(object[x].latitude, object[x].longitude);
        }
        alert("Existing Objects Already Retrieved");
    }
    initCount += 1;
}

function saveObjectData(){
    alert("Passing Json Object To Activity");
    AR.platform.sendJSONObject({ 
        label: label,
        distance: meter,
        latitude: latTemp,
        longitude: lngTemp
    });
}

function addNewMarker(){
    if(globalCount > 0){
        augmentedStepMarkers.create(latTemp, lngTemp);
    }
}

function loadRotationFromJava(rotation, hasSensor){
    if(hasSensor == true){
        rotation -= 3;
//        document.getElementById("angVal").innerHTML = rotation;
        
        lngTemp = meter*Math.sin(rotation * (Math.PI/180));
//        document.getElementById("latVal").innerHTML = lngTemp;
        latTemp = meter*Math.cos(rotation * (Math.PI/180));
//        document.getElementById("lngVal").innerHTML = latTemp;
    }
    if(hasSensor == false){
        alert("Adding Indoor Tour will not work because required sensors not found on device.");
    }

}

function loadValuesFromJava(altitude,latitude,longitude,propertyLatitude,propertyLongitude,count){
    
//    alert("Property Latitude: " + propertyLatitude + "Property Longitude" + propertyLongitude);
    
    globalCount = count;
    
    propertyPos = {
        lat: propertyLatitude,
        lng: propertyLongitude
    }
    
    userPos = {
        alt: altitude,
        lat: latitude,
        lng: longitude
    }
    
    if(count == 1){
        alert("Augmented Reality Initially Loaded");
    }
}

var augmentedStepMarkers = {
    init: function(latitude, longitude) {
        var estLat = Math.round(latitude * 100) / 100;
        var estLng = Math.round(longitude * 100) / 100;
        
//        var gLocation = new AR.GeoLocation(propertyPos.lat, propertyPos.lng, userPos.alt);
        var stepMarkerLocation = new AR.RelativeLocation(null, estLat, estLng, -2);
        augmentedStepMarkers.markerDrawable_idle = new AR.ImageResource("assets/background.png");

        var markerImageDrawable_idle = new AR.ImageDrawable(augmentedStepMarkers.markerDrawable_idle, 1.5, {
            zOrder: 0,
            opacity: 1.0
        });
        
        var titleLabel = new AR.Label(label, 1, {
            zOrder: 1,
            style: {
                textColor: '#FFFFFF',
                fontStyle: AR.CONST.FONT_STYLE.BOLD
            }
        });

        // create GeoObject
        var stepMarkerObject = new AR.GeoObject(stepMarkerLocation, {
            drawables: {
                cam: [markerImageDrawable_idle, titleLabel]
            }
        });
    }, 
    
    create: function(latitude, longitude) {
        var estLat = Math.round(latitude * 100) / 100;
        var estLng = Math.round(longitude * 100) / 100;
        
//        var gLocation = new AR.GeoLocation(propertyPos.lat, propertyPos.lng, userPos.alt);
        var stepMarkerLocation = new AR.RelativeLocation(null, estLat, estLng, -2);
        augmentedStepMarkers.markerDrawable_idle = new AR.ImageResource("assets/background.png");

        var markerImageDrawable_idle = new AR.ImageDrawable(augmentedStepMarkers.markerDrawable_idle, 1.5, {
            zOrder: 0,
            opacity: 1.0
        });
        
        var titleLabel = new AR.Label(label, 1, {
            zOrder: 1,
            style: {
                textColor: '#FFFFFF',
                fontStyle: AR.CONST.FONT_STYLE.BOLD
            }
        });

        // create GeoObject
        var stepMarkerObject = new AR.GeoObject(stepMarkerLocation, {
            drawables: {
                cam: [markerImageDrawable_idle, titleLabel]
            }
        });
        
        saveObjectData();
               
        alert("Added New Marker: " + label);
    }   
};