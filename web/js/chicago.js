var CHICAGO_LAT_LNG = new google.maps.LatLng(41.883799, -87.631636);
var CHICAGO_BOUNDS = new google.maps.LatLngBounds(new google.maps.LatLng(41.640078, -87.947145), new google.maps.LatLng(42.024814, -87.526917));
var map;
var userLocationMarker;
var userLocationInfobox;
var pacChicago;
var gridCrimeReportList = Array();

function initializeMap() {
    google.maps.Map.prototype.markers = new Array();
    google.maps.Map.prototype.getMarkers = function() {
        return this.markers
    };
    google.maps.Map.prototype.clearMarkers = function() {
        for(var i=0; i<this.markers.length; i++){
            this.markers[i].setMap(null);
        }
        this.markers = new Array();
    };
    google.maps.Marker.prototype._setMap = google.maps.Marker.prototype.setMap;
    google.maps.Marker.prototype.setMap = function(map) {
        if (map) {
            map.markers[map.markers.length] = this;
        }
        this._setMap(map);
    }

    google.maps.Map.prototype.rectangles = new Array();
    google.maps.Map.prototype.getRectangles = function() {
        return this.rectangles
    };
    google.maps.Map.prototype.clearRectangles = function() {
        for(var i=0; i<this.rectangles.length; i++){
            this.rectangles[i].setMap(null);
        }
        this.rectangles = new Array();
    };
    google.maps.Rectangle.prototype._setMap = google.maps.Rectangle.prototype.setMap;
    google.maps.Rectangle.prototype.setMap = function(map) {
        if (map) {
            map.rectangles[map.rectangles.length] = this;
        }
        this._setMap(map);
    }


    var mapProp = {
        center: CHICAGO_LAT_LNG,
        zoom: 17,
        scrollwheel: false,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    map = new google.maps.Map(document.getElementById('map-canvas'), mapProp);

    google.maps.event.addListener(
        map,
        'click',
        enableScrollWheelOnMap
    );

    google.maps.event.addListener(
        map,
        'dragend',
        enableScrollWheelOnMap
    );

    google.maps.event.addListener(
        map,
        'drag',
        function(event) {
            if (typeof userLocationInfobox !== 'undefined') {
                userLocationInfobox.close(map, userLocationMarker);
                userLocationMarker.setMap(null);

                delete userLocationInfobox;
                delete userLocationMarker;
            }
            setToUpdatingStatus();
        }
    );

    google.maps.event.addListener(map, 'dragend', updateTopCrimes);
    google.maps.event.addListener(map, 'zoom_changed', updateTopCrimes);

    requestGeolocation(onGeoSuccess_Chicago, onGeoError_Chicago);
}

function initializeMarker() {
    $('<div/>').addClass('centered-marker').appendTo(map.getDiv());
}

function initializePlaceAutocomplete() {
    // <div id="map-search-container">
    //     <div class="input-group">
    //        <input type="text" id="map-search-textbox" class="form-control" placeholder="Enter a location" />
    //        <span class="input-group-btn">
    //           <button id="map-search-button" class="btn btn-primary" type="button"><i class="fa fa-search"></i></button>
    //        </span>
    //     </div>
    // </div>

    var searchDiv = document.createElement('div');
    searchDiv.id = 'map-search-container';
    searchDiv.className = 'input-group';

    var searchTextbox = document.createElement('input');
    searchTextbox.id = 'map-search-textbox';
    searchTextbox.type = 'text';
    searchTextbox.className = 'form-control';
    searchTextbox.placeholder = 'Enter a location';
    searchDiv.appendChild(searchTextbox);

    var searchButtonContainer = document.createElement('span');
    searchButtonContainer.className = 'input-group-btn';
    searchDiv.appendChild(searchButtonContainer);

    var searchButton = document.createElement('button');
    searchButton.id = 'map-search-button';
    searchButton.className = 'btn btn-primary';
    searchButton.innerHTML = '<i class="fa fa-search"></i>';
    searchButtonContainer.appendChild(searchButton);

    map.controls[google.maps.ControlPosition.BOTTOM_CENTER].push(searchDiv);

    pacChicago = new google.maps.places.Autocomplete(
        searchTextbox,
        {
            bounds: CHICAGO_BOUNDS,
            componentRestrictions: {country: 'US'}
        });

    google.maps.event.addListener(pacChicago, 'place_changed', function() {
        var place = pacChicago.getPlace();
        if (!place.geometry) {
            return;
        }

        map.panTo(place.geometry.location);
        map.setZoom(17);
        setToUpdatingStatus();
        updateTopCrimes();
    });
}

function enableScrollWheelOnMap() {
    this.setOptions({
        scrollwheel: true
    });
}

function onGeoSuccess_Chicago(location) {
    if (location.address.countryCode == 'US' && location.address.city == 'Chicago') {
        var latLng = new google.maps.LatLng(location.coords.latitude, location.coords.longitude);
        focusMapTo(latLng);

        userLocationMarker = new google.maps.Marker({
            position: latLng,
            map: map,
            icon: '../img/icons/marker-red-small.png'
        });

        userLocationInfobox = new InfoBox({
            content: document.getElementById("infobox"),
            disableAutoPan: false,
            maxWidth: 150,
            pixelOffset: new google.maps.Size(-50, 0),
            zIndex: null,
            boxStyle: {
                background: "url('https://google-maps-utility-library-v3.googlecode.com/svn/trunk/infobox/examples/tipbox.gif') no-repeat",
                width: "100px"
            },
            closeBoxMargin: "12px 4px 2px 2px",
            closeBoxURL: "",
            infoBoxClearance: new google.maps.Size(1, 1)
        });
        userLocationInfobox.open(map, userLocationMarker);

    } else {
        focusMapTo(CHICAGO_LAT_LNG);

        var nonChicagoModal = new jBox('Modal', {
            // title: 'Not in Chicago',
            content: 'Safer Streets is available only for Chicago.<br />To see how it works, browse any address or drag the map.'
        })  ;
        nonChicagoModal.open();
    }
}

function onGeoError_Chicago(error) {
    focusMapTo(CHICAGO_LAT_LNG);
}

function focusMapTo(latLng) {
    map.panTo(latLng);
    updateTopCrimes(latLng.lat(), latLng.lng());
}

function setToUpdatingStatus() {
    setSafetyRatingText('(UPDATING...)');
    setCrimeTrendData([]);
}

function setToUnknownStatus() {
    setSafetyRatingText('UNKNOWN (OUT OF COVERAGE AREA)');
    setCrimeTrendData([]);
}


function updateTopCrimes() {
    if (isInChicago(map.getCenter())) {
        var currentGrid = getCurrentGrid(gridCrimeReportList, map.getCenter());
        if (typeof currentGrid !== 'undefined') {
            updateTopCrimesUi(currentGrid);
        } else {
            var lat = map.getCenter().lat();
            var lng = map.getCenter().lng();

            $.ajax({
                url: SS_API_BASE_URL + "/rs/data/saferstreetscrimerating",
                type: 'POST',
                data: JSON.stringify({
                    city: 'Chicago',
                    latitude: lat,
                    longitude: lng
                }),
                contentType: 'application/json; charset=utf-8',
                dataType:'json',
                success: function(data, status) {
                    // DEBUG: For display crime grids in maps
                    // map.clearRectangles();
                    // map.clearMarkers();
                    // drawGridOnMap(data.gridCrimeReportList);

                    gridCrimeReportList = data.gridCrimeReportList;
                    var currentGrid = getCurrentGrid(gridCrimeReportList, map.getCenter());
                    updateTopCrimesUi(currentGrid);
                }
            });
        }
    } else {
        setToUnknownStatus();
    }
}

function isInChicago(latLng) {
    return CHICAGO_BOUNDS.contains(latLng);
}

function drawGridOnMap(gridCrimeReportList) {
    for (var i = 0, len = gridCrimeReportList.length; i < len; i++) {
        var grid = gridCrimeReportList[i];

        var rectangle = new google.maps.Rectangle({
            strokeColor: getSafetyRatingColor(grid.safetyRating),
            strokeOpacity: 0.8,
            strokeWeight: 2,
            fillColor: getSafetyRatingColor(grid.safetyRating),
            fillOpacity: 0.35,
            map: map,
            bounds: new google.maps.LatLngBounds(
                new google.maps.LatLng(grid.bottomLeftLat, grid.bottomLeftLng),
                new google.maps.LatLng(grid.topRightLat, grid.topRightLng))
        });

        var marker = new google.maps.Marker({
            position: new google.maps.LatLng(grid.centerPointLat, grid.centerPointLng),
            map: map,
            title: grid.grid.gridId
        });
    }
}

function getCurrentGrid(gridCrimeReportList, latLng) {
    for (var i = 0, len = gridCrimeReportList.length; i < len; i++) {
        var grid = gridCrimeReportList[i];
        var bounds = new google.maps.LatLngBounds(new google.maps.LatLng(grid.bottomLeftLat, grid.bottomLeftLng), new google.maps.LatLng(grid.topRightLat, grid.topRightLng));

        if (bounds.contains(latLng)) {
            return grid;
        }
    }
}

function updateTopCrimesUi(grid) {
    if (typeof grid !== 'undefined') {
        setSafetyRatingText(grid.safetyRating);
        if (typeof grid.crimeReport !== 'undefined' && typeof grid.crimeReport.crimeTrend !== 'undefined') {
            setCrimeTrendData(grid.crimeReport.crimeTrend);
        }
    } else {
        setToUnknownStatus();
    }
}

function setSafetyRatingText(safetyRating) {
    var spanSafetyRating = $('#safety-rating');
    spanSafetyRating.html(safetyRating);
    spanSafetyRating.css('color', getSafetyRatingColor(safetyRating));
}

function getSafetyRatingColor(safetyRating) {
    if (safetyRating == 'UNKNOWN (OUT OF COVERAGE AREA)' || safetyRating == '(UPDATING...)') {
        return '';
    } else if (safetyRating == 'LOW SAFETY') {
        return '#f95a54';
    } else if (safetyRating == 'MODERATE') {
        return '#f9aa54';
    } else {
        return '#4cd764';
    }
}

function setCrimeTrendData(crimeTrend) {
    var i = 0;
    var len = Math.min(crimeTrend.length, 3);
    for (; i < len; i++) {
        $('#top-crime-count-' + i).text(crimeTrend[i].crimeCount);
        $('#top-crime-type-' + i).text(crimeTrend[i].crimeType);
    }
    for (; i < 3; i++) {
        $('#top-crime-count-' + i).text(' - ');
        $('#top-crime-type-' + i).text(' - ');
    }
}

if (typeof google != 'undefined'){
    google.maps.event.addDomListener(window, 'load', function() {
        initializeMap();
        initializeMarker();
        initializePlaceAutocomplete();
    });
}