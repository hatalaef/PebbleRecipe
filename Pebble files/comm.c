#include <comm.h>

#define KEY_BUTTON_UP 0
#define KEY_BUTTON_DOWN 1

#define SEND_DONE 2
#define SEND_NEXT 3

#define TYPE_TITLE 4
#define TYPE_INGREDIENT 5
#define TYPE_STEP 6

#define RESULT 7
#define RESULT_DONE 8
#define RESULT_SENDING 9
#define INDEX 10

#define SHOW_TYPE 11
#define SHOW_ING 12
#define SHOW_STEP 13

#define MAX_LENGTH 100

#define NUM_ITEMS 20

#define DELIMITER "|"

char dataItems[NUM_ITEMS][MAX_LENGTH] = {{'\0', '\0'}};
//char message[NUM_ITEMS*MAX_LENGTH] = {'\0'};
char ingredients[NUM_ITEMS*MAX_LENGTH] = {'\0'};

int dataItemsIndex = 0;
//char *dataItems[NUM_ITEMS];
static int indexCur = 0;

bool isDone = false;

static void send(int key, int value) {
  DictionaryIterator *iter;
  app_message_outbox_begin(&iter);

  dict_write_int(iter, key, &value, sizeof(int), true);

  app_message_outbox_send();
}

/*static void up_click_handler(ClickRecognizerRef recognizer, void *context) {
  send(KEY_BUTTON_UP, 0);
}

static void down_click_handler(ClickRecognizerRef recognizer, void *context) {
  send(KEY_BUTTON_DOWN, 0);
}

static void click_config_provider(void *context) {
  window_single_click_subscribe(BUTTON_ID_UP, up_click_handler);
  window_single_click_subscribe(BUTTON_ID_DOWN, down_click_handler);
}
*/

static void inbox_received_handler(DictionaryIterator *iter, void *context) {
	Tuple *result = dict_find(iter, RESULT);
	Tuple *showType = dict_find(iter, SHOW_TYPE);
	if(result) {
		APP_LOG(APP_LOG_LEVEL_INFO, "Phone sent: %d", (int)result->value->int32);
		if((int)result->value->int32 == RESULT_DONE) {
			isDone = true;
			APP_LOG(APP_LOG_LEVEL_INFO, "Phone sent done");
			if(indexCur < NUM_ITEMS) {
					APP_LOG(APP_LOG_LEVEL_INFO, "All messages sent!");
					send(SEND_DONE, 0);
				if (showType) {
					int type = (int)showType->value->int32;
					if (type == SHOW_ING) {
						APP_LOG(APP_LOG_LEVEL_INFO, "message: %s", ingredients);
						wdwScrollSetText(window_stack_get_top_window(), ingredients);
					}
					if (type == SHOW_STEP) {
						//char *message[NUM_ITEMS * MAX_LENGTH];
						for(int i = 0; i < NUM_ITEMS; i++) {
							if (dataItems[i][0] != '\0') {
									wdwScrollSetText(window_stack_get_top_window(), dataItems[i]);
									APP_LOG(APP_LOG_LEVEL_INFO, "dataitem[%d]: %s", i, dataItems[i]);
							}
						}
					}
				}
			}
			indexCur = 0;
		}
		else if((int)result->value->int32 == RESULT_SENDING) {
			isDone = false;
			int index = 0;

				Tuple *item = dict_find(iter, INDEX);
				Tuple *ingredient = dict_find(iter, TYPE_INGREDIENT);
				Tuple *step = dict_find(iter, TYPE_STEP);
				if(item) {
					index = (int)item->value->int32;
					APP_LOG(APP_LOG_LEVEL_INFO, "Found item %d", index);
				}
				if (item || ingredient || step) {
					Tuple *title = dict_find(iter, TYPE_TITLE);
					static char buffer[NUM_ITEMS*MAX_LENGTH];
					if (title && item) {
						snprintf(buffer, sizeof(buffer), "#%d: %s", index, title->value->cstring);
						strcpy(dataItems[index], buffer);
						APP_LOG(APP_LOG_LEVEL_INFO, "%s", buffer);
					}
					if (ingredient) {
						//snprintf(buffer, sizeof(buffer), "#%d: %s", index, ingredient->value->cstring);
						//strcpy(dataItems[index], buffer);
						memset(&ingredients[0], 0, sizeof(ingredients));
						snprintf(buffer, sizeof(buffer), "%s", ingredient->value->cstring);
						strcpy(ingredients, buffer);
						APP_LOG(APP_LOG_LEVEL_INFO, "%s", buffer);
					}
					if (step) {
						//memset(&ingredients[0], 0, sizeof(ingredients));
						memset(&dataItems[0], 0, sizeof(dataItems));
						snprintf(buffer, sizeof(buffer), "%s", step->value->cstring);
						
						char copyBuffer[NUM_ITEMS*MAX_LENGTH];
						strcpy(copyBuffer, buffer);
						dataItemsIndex = -1;
						char *split = strtokC(copyBuffer, DELIMITER);
						APP_LOG(APP_LOG_LEVEL_INFO, "split %s", split);
						while (split != NULL && dataItemsIndex < NUM_ITEMS) {						
							dataItemsIndex++;
							strcpy(dataItems[dataItemsIndex], split);
							APP_LOG(APP_LOG_LEVEL_INFO, "copied %s", dataItems[dataItemsIndex]);
							split = strtokC (NULL, DELIMITER);
						}
						APP_LOG(APP_LOG_LEVEL_INFO, "%s", buffer);
					}
					
					indexCur = index + 1;
					APP_LOG(APP_LOG_LEVEL_INFO, "set indexCurr to %d", index);
					
					if (indexCur < NUM_ITEMS && !isDone) {
						send(SEND_NEXT, indexCur);
						APP_LOG(APP_LOG_LEVEL_INFO, "Send next message: %d", indexCur);
					}
			}
		}
	}
}

static void inbox_dropped_handler(AppMessageResult reason, void *context) {
  APP_LOG(APP_LOG_LEVEL_ERROR, "Message dropped. Reason: %d", (int)reason);
	if (indexCur < NUM_ITEMS && !isDone) {
		send(SEND_NEXT, indexCur);
		APP_LOG(APP_LOG_LEVEL_INFO, "Send next message: %d", indexCur);
	}
}

static void outbox_sent_handler(DictionaryIterator *iter, void *context) {
	
}

static void outbox_failed_handler(DictionaryIterator *iter, AppMessageResult reason, void *context) {
  APP_LOG(APP_LOG_LEVEL_ERROR, "Fail reason: %d", (int)reason);
}

void comm_init() {
	app_message_register_inbox_received(inbox_received_handler);
	app_message_register_inbox_dropped(inbox_dropped_handler);
	app_message_register_outbox_sent(outbox_sent_handler);
	app_message_register_outbox_failed(outbox_failed_handler);
	APP_LOG(APP_LOG_LEVEL_INFO, "inbox max: %d", (int)app_message_inbox_size_maximum());
	
	const int inboxSize = NUM_ITEMS*MAX_LENGTH*2;
	const int outboxSize = 128;
	app_message_open(inboxSize, outboxSize);
}
