$(function(){
	$('#twitter').sharrre({
		share: {
			twitter: true
		},
		template: 
		'<a href="#"><div class="share"></div><div class="count" href="#">{total}</div></a>',
		enableHover: false,
		enableTracking: true,
		buttons: { 
			twitter: {via: ''
		}},
		click: function(api, options){
			api.simulateClick();
			api.openPopup('twitter');
		}
	});
	$('#facebook').sharrre({
		share: {
			facebook: true
		},
		template: 
		'<a href="#"><div class="share"></div><div class="count" href="#">{total}</div></a>',
		enableHover: false,
		enableTracking: true,
		click: function(api, options){
			api.simulateClick();
			api.openPopup('facebook');
		}
	});
	$('#linkedin').sharrre({
		share: {
			linkedin: true
		},
		template: 
		'<a href="#"><div class="share"></div><div class="count" href="#">{total}</div></a>',
		enableHover: false,
		enableTracking: true,
		click: function(api, options){
			api.simulateClick();
			api.openPopup('linkedin');
		}
	});

	// join the movement
	$('#twitter1').sharrre({
		share: {
			twitter: true
		},
		template: 
		'<a href="#"><div class="share"></div><div class="count" href="#">{total}</div></a>',
		enableHover: false,
		enableTracking: true,
		buttons: { 
			twitter: {via: ''
		}},
		click: function(api, options){
			api.simulateClick();
			api.openPopup('twitter');
		}
	});
	$('#facebook1').sharrre({
		share: {
			facebook: true
		},
		template: 
		'<a href="#"><div class="share"></div><div class="count" href="#">{total}</div></a>',
		enableHover: false,
		enableTracking: true,
		click: function(api, options){
			api.simulateClick();
			api.openPopup('facebook');
		}
	});
	$('#linkedin1').sharrre({
		share: {
			linkedin: true
		},
		template: 
		'<a href="#"><div class="share"></div><div class="count" href="#">{total}</div></a>',
		enableHover: false,
		enableTracking: true,
		click: function(api, options){
			api.simulateClick();
			api.openPopup('linkedin');
		}
	});

});