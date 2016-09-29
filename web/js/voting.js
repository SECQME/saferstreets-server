$(document).ready(function () {
    $('#voting-modal').one('shown.bs.modal', initVotingModal);
});

function initVotingModal() {
    // var content =
    //     '<div class="row">' +
    //     '    <button type="button" class="close pull-right" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>' +
    //     '    <div class="col-sm-6 col-xs-12">' +
    //     '        <div class="row modal-intro">' +
    //     '            <h2>Reclaim your streets &amp; your city today</h2>' +
    //     '            <p>The Safer Streets technology is now live in Chicago, but we\'re looking to bring Safer Streets to 50 cities by 2017... and your city could be next. <b>Vote for your city</b> so we can add it to our Safer Streets pipeline.</p>' +
    //     '        </div>' +

    //     '        <div class="row vote-form">' +
    //     '            <h4>I vote for <img src="/img/icons/loading-img.gif" class="loading-img" /><input id="vote-locality" name="vote-locality" class="input-location" type="text" placeholder="" value=" " autofocus="autofocus" style="display: none" /></h4>' +
    //     '            <div id="voteForm">' +
    //     '                <input type="hidden" id="vote-state" name="vote-state" />' +
    //     '                <input type="hidden" id="vote-country" name="vote-country" />' +
    //     '                <input type="hidden" id="vote-postcode" name="vote-postcode" />' +
    //     '                <label for="vote-name">' +
    //     '                    <span class="text">Name</span>' +
    //     '                    <input id="vote-name" name="vote-name" type="text" value="" autocomplete="off" autofocus="" placeholder="Your full name" required="true" />' +
    //     '                </label>' +
    //     '                <label for="vote-email">' +
    //     '                    <span class="text">Email</span>' +
    //     '                    <input id="vote-email" name="vote-email" type="email" value="" autocomplete="off" autofocus="" placeholder="Your email address" required="true" />' +
    //     '                </label>' +
    //     '                <button class="btn btn-success btn-vote" onclick="checkAndPostSaferStreetsRequest();">VOTE</button>' +
    //     '                <p class="or">- or -</p>' +
    //     '                <button class="btn btn-fb" onclick="loginAndVoteViaFacebook();">Vote with Facebook</button>' +
    //     '            </div>' +
    //     '        </div>' +
    //     '    </div>' +
    //     '</div>'
    // var modal = new jBox('Modal', {
    //     content: content
    // });
    // modal.open();

    initVotingPlaceAutoComplete();
    requestGeolocation(onGeoSuccess_Voting, onGeoError_Voting);
}

function initVotingPlaceAutoComplete() {
    autoCompleteService = new google.maps.places.AutocompleteService();
    placeService = new google.maps.places.PlacesService(document.createElement('div'));

    $("#vote-locality").cityautocomplete(
        {
            select: function (event, ui) {
                placeService.getDetails(
                    {placeId: ui.item.place_id},
                    function (details, status) {
                        var city = '', state = '', country = '', postcode = '';

                        for (var i = 0; i < details.address_components.length; i++) {
                            if (details.address_components[i].types[0] == 'administrative_area_level_1') {
                                state = details.address_components[i].long_name;
                            } else if (details.address_components[i].types[0] == 'country') {
                                country = details.address_components[i].short_name;
                            } else if (details.address_components[i].types[0] == 'postal_code') {
                                postcode = details.address_components[i].long_name;
                            } else if (details.address_components[i].types[0] == 'locality') {
                                city = details.address_components[i].long_name;
                            } else if (city == '') {
                                city = details.address_components[i].long_name;
                            }
                        }

                        // console.log('Selected: ' + city + ', ' + state + ', ' + country + ' ' + postcode);
                        $('#vote-locality').val(city);
                        $('#vote-state').val(state);
                        $('#vote-country').val(country);
                        $('#vote-postcode').val(postcode);
                    }
                );
            }
        });
}

function onGeoSuccess_Voting(location) {
    $('#vote-locality').val(location.address.city).attr('placeholder', location.address.city);
    $('#vote-state').val(location.address.region);
    $('#vote-country').val(location.address.countryCode);
    $('#vote-postcode').val(location.address.postalCode);

    putFocus('#vote-locality');

    $('.loading-img').hide();
    $('#vote-locality').show();
}

function onGeoError_Voting(error) {
    $('.loading-img').hide();
    $('#vote-locality').show();
}

function putFocus(selector) {
    var element = $(selector).get(0);

    if (typeof element.value.length !== 'undefined') {
        var elementLength = element.value.length;
        element.selectionStart = elementLength;
        element.selectionEnd = elementLength;
    }
    element.focus();
}


function checkAndPostSaferStreetsRequest() {
    var email = $('#vote-email').val(),
        name = $('#vote-name').val(),
        city = $('#vote-locality').val(),
        state = $('#vote-state').val(),
        country = $('#vote-country').val();

    $.ajax({
        url: SS_API_BASE_URL + "/rs/data/checksupportrequest",
        type: 'POST',
        data: JSON.stringify({
            email: email,
            name: name,
            city: city,
            state: state,
            country: country
        }),
        contentType: 'application/json; charset=utf-8',
        dataType:'json',
        success: function(data, status) {
            if (data.userRequested === true) {
                showAlreadyVotedModal();
            } else if (data.userRequested === false) {
                postSaferStreetsRequest();
            } else {
                console.log('An error occurred when posting Safer Streets request.');
                console.log(data);
            }
        }
    });
}

function postSaferStreetsRequest() {
    var email = $('#vote-email').val(),
        name = $('#vote-name').val(),
        city = $('#vote-locality').val(),
        state = $('#vote-state').val(),
        country = $('#vote-country').val(),
        postcode = $('#vote-postcode').val();

    var data = {
        email: email,
        name: name,
        city: city,
        state: state,
        country: country
    };

    if (postcode != '') {
        data.postcode = postcode;
    }

    $.ajax({
        url: SS_API_BASE_URL + "/rs/data/saferstreetsrequest",
        type: 'POST',
        data: JSON.stringify(data),
        contentType: 'application/json; charset=utf-8',
        dataType:'json',
        success: function(data, status) {
            if (data.userRequested === true) {
                showVoteSuccessModal();
            } else if (data.userRequested === false) {
                showAlreadyVotedModal();
            } else {
                console.log('An error occurred when posting Safer Streets request.');
                console.log(data);
            }
        }
    });
}

function showVoteSuccessModal() {
    var modal = new jBox('Modal', {
        // title: 'Vote is success',
        content: 'Success! Thank you for voting :)'
    })  ;
    modal.open();
    $('#voting-modal').modal('hide');
}

function showAlreadyVotedModal() {
    var modal = new jBox('Modal', {
        // title: 'Already voted',
        content: 'Sorry, you\'ve already voted for a city.'
    })  ;
    modal.open();
    $('#voting-modal').modal('hide');
}

// Load the Facebook SDK asynchronously
(function(d, s, id) {
    var js, fjs = d.getElementsByTagName(s)[0];
    if (d.getElementById(id)) return;
    js = d.createElement(s); js.id = id;
    js.src = "//connect.facebook.net/en_US/sdk.js";
    fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));

// Initialize the Facebook JavaScript SDK
window.fbAsyncInit = function() {
    FB.init({
        appId      : '868090419896543',
        cookie     : true,
        xfbml      : true,
        version    : 'v2.2'
    });
};

function loginAndVoteViaFacebook() {
    FB.login(function(response) {
        loginStatusChangeCallback(response);
    }, {scope: 'public_profile,email'});
}

function loginStatusChangeCallback(response) {
    if (response.status === 'connected') {
        voteViaFacebook();
    }
}

function voteViaFacebook() {
    FB.api('/me', function(response) {
        $('#vote-name').val(response.name);
        $('#vote-email').val(response.email);
        checkAndPostSaferStreetsRequest();
    });
}