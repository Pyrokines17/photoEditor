package ru.nsu;

import ru.nsu.ui.FrameWork;

public class Main {
    public static void main(String[] args) {
        FrameWork frameWork = FrameWork.getInstance();
        frameWork.repaint();
    }
}