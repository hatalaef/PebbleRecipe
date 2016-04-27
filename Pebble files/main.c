#include <pebble.h>

#include <comm.h>
#include <windowScroll.h>

static void init(void) {
	comm_init();
	wdwScrollPush();
}

static void deinit(void) {
	wdwScrollDestroy();
}

int main(void) {
	init();
	app_event_loop();
	deinit();
}