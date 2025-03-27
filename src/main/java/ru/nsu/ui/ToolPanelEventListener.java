package ru.nsu.ui;

public interface ToolPanelEventListener {
    enum EventType {
        FIT_SCREEN_BUTTON_CLICKED,
        REAL_SIZE_BUTTON_CLICKED,
    }

    void onEvent(EventType eventType);

}
