function IFrameForm(url)
{
    var object = this;
    object.time = new Date().getTime();
    object.form = $('<form action="'+url+'" method="post" style="display:none;" id="form'+object.time+'"></form>');

    object.addParameter = function(parameter,value)
    {
        $('<input type="hidden" />')
            .attr('name', parameter)
            .attr('value', value)
            .appendTo(object.form);
    }

    object.send = function()
    {
        var iframe = $('<iframe id="iframe'+object.time+'" name="iframe'+object.time+'" data-time="'+object.time+'" style="display:none;"></iframe>');
        $('body').append(iframe);
        object.form.attr('target', 'iframe' + object.time);
        $('body').append(object.form);
        object.form.submit();
        iframe.load(function(){  $('#form'+$(this).data('time')).remove();  $(this).remove();   });
    }
}

function isChromium() {
    var isChromium = window.chrome,
        vendorName = window.navigator.vendor;
    return (isChromium !== null && isChromium !== undefined && vendorName === "Google Inc.");
}

function isFirefox() {
    return typeof InstallTrigger !== 'undefined';
}

function isMobile() {
    return (/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini|Mobile|mobile/i.test(navigator.userAgent));
}

var requestGeolocationModal;
function requestGeolocation(onGeoSuccessCallback, onGeoErrorCallback) {
    if (isChromium() && !isMobile() && navigator.geolocation) {
        requestGeolocationModal = new jBox('Modal', {
            // title: 'Geolocation settings',
            content: '<center><img src="' + CONTEXT_PATH + '/img/chrome-location.png" style="max-width: 168px" /></center>' +
            '<div style="max-width: 168px; padding-top: 12px">Please allow us to detect your location.</span></div>'
        });

        function chromeLocationCallback() {
            requestGeolocationModal.close();
            geolocator.locate(onGeoSuccessCallback, onGeoErrorCallback, true, {enableHighAccuracy: true, timeout: 6000, maximumAge: 0});
        }

        var isNeedToShowLocationPrompt = true;
        setTimeout(function() {
            if (isNeedToShowLocationPrompt) {
                requestGeolocationModal.open();
            }
        }, 2000);

        navigator.geolocation.getCurrentPosition(
            function() {
                isNeedToShowLocationPrompt = false;
                chromeLocationCallback();
            },
            function(error) {
                if (error.code != 2) {
                    isNeedToShowLocationPrompt = false;
                }
                chromeLocationCallback();
            },
            {timeout: 2000}
        );
    } else if (isFirefox()) {
        var isTimeOut = false;
        var isGeoSuccess = false;
        setTimeout(function() {
            isTimeOut = true;
            if (!isGeoSuccess) {
                geolocator.locateByIP(onGeoSuccessCallback, onGeoErrorCallback);
            }
        }, 5000);

        geolocator.locate(function(location) {
            if (!isTimeOut) {
                isGeoSuccess = true;
                onGeoSuccessCallback(location);
            }
        }, onGeoErrorCallback, true, {enableHighAccuracy: true, timeout: 6000, maximumAge: 0});
    } else {
        geolocator.locate(onGeoSuccessCallback, onGeoErrorCallback, true, {enableHighAccuracy: true, timeout: 6000, maximumAge: 0});
    }
}

$.widget("custom.cityautocomplete", $.ui.autocomplete, {
    options: {
        source: function (request, response) {
            autoCompleteService.getPlacePredictions({ input: request.term, types: ['(cities)'] }, function (predictions, status) {
                if (status != google.maps.places.PlacesServiceStatus.OK) {
                    return;
                }
                response($.map(predictions, function (prediction, i) {
                    prediction.value = prediction.terms[0].value;
                    return prediction;
                }));
            });
        }
    },
    _renderMenu: function(ul, items) {
        ul.addClass('pac-container');

        var self = this;
        $.each(items, function(index, item) {
            self._renderItem( ul, item );
        });
    },
    _renderItem: function (ul, item) {
        var query = item.terms[0].value;
        var content = '<span class="pac-icon pac-icon-marker"></span><span class="pac-item-query">' + query.substr(0, item.matched_substrings[0].offset) + '<span class="pac-matched">' + query.substr(item.matched_substrings[0].offset, item.matched_substrings[0].length) + '</span>' + query.substr(item.matched_substrings[0].offset + item.matched_substrings[0].length) + '</span>';

        for (var i = 1; i < item.terms.length; i++) {
            content += ' ' + item.terms[i].value;
        };

        return $('<li></li>')
            .addClass('pac-item')
            .css('margin', '0px')
            .append(content)
            .data("ui-autocomplete-item", item)
            .appendTo(ul);
    }
});

$(function () {
    $('.appstore').on('click', function(){
        ga('send', 'event', 'button', 'click', 'App Store Button');
    });

    $('.googleplay').on('click', function(){
        ga('send', 'event', 'button', 'click', 'Google Play Button');
    });
});

$(function() {
    $('.ytp-thumbnail')
        .click(
        function() {
            var video = '<iframe src="'+ $(this).attr('data-video') +'" width="' + $(this).width() + '" height="' + $(this).height() + '" frameborder="0" allowfullscreen="allowfullscreen"></iframe>';
            $(this).replaceWith(video);
        }
    )
        .hover(
        function() {
            $(this).children('.ytp-large-play-button').children('svg').children('path').attr('fill', '#cc181e').attr('fill-opacity', '1.0');
        },
        function() {
            $(this).children('.ytp-large-play-button').children('svg').children('path').attr('fill', '#1f1f1f').attr('fill-opacity', '0.9');
        }
    );
});