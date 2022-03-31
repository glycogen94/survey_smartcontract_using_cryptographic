$('.value').each(function() {
	var text = $(this).text();
    $(this).parent('div.block').css('width', text);
});