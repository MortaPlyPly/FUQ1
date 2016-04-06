// JavaScript Document
function prepareList() {
  "use strict";
  $('#expList').find('li:has(ul)')
  	.click( function(event) {
  		if (this === event.target) {
  			$(this).toggleClass('expanded');
  			$(this).children('ul').toggle('medium');
  		}
  		return false;
  	})
  	.addClass('collapsed')
  	.children('ul').hide();
  }
 
  $(document).ready( function() {
	  "use strict";
      prepareList();
  });
