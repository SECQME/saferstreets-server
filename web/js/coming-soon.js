$(function() {
    initSubscriptionControls();
});

function initSubscriptionControls() {
    // Submit form when user press Enter (code:13)
    $(".xfield input").keypress(function(e) {
        if (e.which == 13) {
            e.preventDefault();
            submitNotificationForm();
            return false;
        }
    });

    // Change color to 'active' when user type
    $(".xfield input").on("input", function() {
        if (isValidEmail($(this).val())) {
            $(this).parents(".xfield").addClass("filled")
        } else {
            $(this).parents(".xfield").removeClass("filled").removeClass("error");
            $(".notifyError").remove();
        }
    });

    // Submit form when user press submit-btn
    $(".xfield").on("click", ".submit-btn", submitNotificationForm);
}

function isValidEmail(email) {
    var re = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
}

function submitNotificationForm() {
    var txtEmail = $('#subscription-email');
    var container = $(".emailField");
    var divEmail = $(this).parents(".xfield");
    divEmail.removeClass("error");
    $(".notifyError").remove();

    if (isValidEmail(txtEmail.val())) {
        checkAndPostSubscriptionNotificationRequest();
    } else {
        divEmail.addClass("error");
        container.after('<div class="notifyError">Invalid Email</div>')
    }
}


function checkAndPostSubscriptionNotificationRequest() {
    var email = $('#subscription-email').val(),
        city = $('#subscription-city').val(),
        state = $('#subscription-state').val(),
        country = $('#subscription-country').val();

    var data = {
        email: email,
        city: city,
        state: state,
        country: country
    };

    $.ajax({
        url: SS_API_BASE_URL + "/rs/data/checksubscriptionrequest",
        type: 'POST',
        data: JSON.stringify(data),
        contentType: 'application/json; charset=utf-8',
        dataType:'json',
        success: function(data, status) {
            if (data.userRequested === false) {
                postSubscriptionNotificationRequest();
            } else {
                showSimpleModal("Already registered", "We've already got your email! Stay tuned ;)");
            }
        }
    });
}

function postSubscriptionNotificationRequest() {
    var email = $('#subscription-email').val(),
        city = $('#subscription-city').val(),
        state = $('#subscription-state').val(),
        country = $('#subscription-country').val();

    var data = {
        email: email,
        city: city,
        state: state,
        country: country
    };

    $.ajax({
        url: SS_API_BASE_URL + "/rs/data/subscribenotification",
        type: 'POST',
        data: JSON.stringify(data),
        contentType: 'application/json; charset=utf-8',
        dataType:'json',
        success: function(data, status) {
            if (data.userRequested === true) {
                $(".emailField").fadeOut("fast", function() {
                    $(".successField").removeClass("hidden");
                });
            } else {
                console.log('An error occurred when subscribe notification request.');
                console.log(data);
            }
        }
    });
}


function showSimpleModal(title, text) {
    var modal = new jBox('Modal', {
        // title: title,
        content: text
    })  ;
    modal.open();
}