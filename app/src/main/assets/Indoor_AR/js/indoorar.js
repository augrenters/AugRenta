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

var meter = 1;

$('document').ready(function(){
        
    $('#addObject').click(function() {
        addNewMarker();
    });
    
});

function addNewMarker(){
    
}

function loadRotationFromJava(rotation, hasSensor){
    if(hasSensor == true){
        document.getElementById("angVal").innerHTML = rotation;
        
        latTemp = meter*Math.sin(rotation);
        document.getElementById("latVal").innerHTML = latTemp;
        lngTemp = meter*Math.cos(rotation);
        document.getElementById("lngVal").innerHTML = lngTemp;
    }
    if(hasSensor == false){
        alert("Adding Indoor Tour will not work because required sensors not found on device.");
    }

}

function loadValuesFromJava(altitude,latitude,longitude,propertyLatitude,propertyLongitude,count){
    
    alert("Property Latitude: " + propertyLatitude + "Property Longitude" + propertyLongitude);
    
    
    propertyPos = {
        lat: propertyLatitude,
        lng: propertyLongitude
    }
    
    if(count == 1){
        alert("Initially Loaded");
    } else{
        alert("Updating UI");
    }
    
    augmentedStepMarkers.create();
}

var augmentedStepMarkers = {
    create: function() {
        
        alert("Creating Objects");
        var stepMarkerLocation = new AR.GeoLocation(propertyPos.lat, propertyPos.lng, 3);
        augmentedStepMarkers.markerDrawable_idle = new AR.ImageResource("assets/pin_green.png");

        var markerImageDrawable_idle = new AR.ImageDrawable(augmentedStepMarkers.markerDrawable_idle, 2.5, {
            zOrder: 0,
            opacity: 1.0
        });

        // create GeoObject
        var stepMarkerObject = new AR.GeoObject(stepMarkerLocation, {
            drawables: {
                cam: [markerImageDrawable_idle]
            }
        });
    }   
};