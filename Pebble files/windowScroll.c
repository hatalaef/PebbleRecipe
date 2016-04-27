#include <windowScroll.h>

#include <windowScroll.h>


static Window *wdwScroll;
static TextLayer *txtLayer;
static ScrollLayer *scrollLayer;
static GFont font;

static void wdwScroll_load(Window *window) {
	char *text = "Here is the text.\nIt's long\nOr not\nBlah blah blah\n\
	Blah blah blah\nBlah blah blah\nBlah blah blah\nBlah blah blah\n\
	Blah blah blah\nBlah blah blah\nBlah blah blah\nBlah blah blah\n\
	Blah blah blah\nBlah blah blah\nBlah blah blah\nBlah blah blah\n";
	wdwScrollSetText(window, text);
}

static void wdwScroll_unload(Window *window) {
	scroll_layer_destroy(scrollLayer);
	text_layer_destroy(txtLayer);
}

void wdwScrollPush() {
	wdwScroll = window_create();
	window_set_window_handlers(wdwScroll, (WindowHandlers) {
		.load = wdwScroll_load,
		.unload = wdwScroll_unload,
	});
	//window_set_click_config_provider(wdwMain, click_config_provider);
	window_stack_push(wdwScroll, true);
}


void wdwScrollSetText(Window *window, char *text) {
	font = fonts_get_system_font(FONT_KEY_GOTHIC_18_BOLD);
	Layer *lyrWindow = window_get_root_layer(window);
	GRect bounds = layer_get_bounds(lyrWindow);

	GRect rect = GRect(0, 0, bounds.size.w, 2000);
	GSize textSize = graphics_text_layout_get_content_size(
		text, font, rect, 
		GTextOverflowModeWordWrap, GTextAlignmentLeft);
  GRect textBounds = bounds;
	textBounds.size.h = textSize.h;
	
	txtLayer = text_layer_create(textBounds);
	text_layer_set_overflow_mode(txtLayer, GTextOverflowModeWordWrap);
	text_layer_set_font(txtLayer, font);
	text_layer_set_text(txtLayer, text);
	
	scrollLayer = scroll_layer_create(bounds);
	scroll_layer_set_content_size(scrollLayer, textSize);
	scroll_layer_set_click_config_onto_window(scrollLayer, window);
	scroll_layer_add_child(scrollLayer, text_layer_get_layer(txtLayer));
	layer_add_child(lyrWindow, scroll_layer_get_layer(scrollLayer));
}

void wdwScrollDestroy() {
	window_destroy(wdwScroll);
}