package ru.nsu.filters;

import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Parameters {
    private final BufferedImage image;
    private final HashMap<String, String> types;

    private final HashMap<String, Integer> intParams;
    private final HashMap<String, Double> doubleParams;

    public Parameters(BufferedImage image, HashMap<String, String> types) {
        this.image = image;
        this.types = types;

        intParams = new HashMap<>();
        doubleParams = new HashMap<>();
    }

    public BufferedImage getImage() {
        return image;
    }

    public HashMap<String, String> getTypes() {
        return types;
    }

    public boolean setIntParam(String name, int value) {
        if (checkParam(name, "int")) {
            return false;
        }

        intParams.put(name, value);
        return true;
    }

    public boolean setDoubleParam(String name, double value) {
        if (checkParam(name, "double")) {
            return false;
        }

        doubleParams.put(name, value);
        return true;
    }

    private boolean checkParam(String name, String type) {
        return (types == null) || !types.containsKey(name) || !types.get(name).equals(type);
    }

    public int getIntParam(String name) {
        return intParams.get(name);
    }

    public double getDoubleParam(String name) {
        return doubleParams.get(name);
    }
}
