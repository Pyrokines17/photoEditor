package ru.nsu.filters;

import java.util.HashMap;

public class Parameters {
    private final HashMap<String, String> types;
    private final HashMap<String, String> borders;

    private final HashMap<String, Integer> intParams;
    private final HashMap<String, Double> doubleParams;

    public Parameters(HashMap<String, String> types, HashMap<String, String> borders) {
        this.types = types;
        this.borders = borders;

        intParams = new HashMap<>();
        doubleParams = new HashMap<>();
    }

    public HashMap<String, String> getTypes() {
        return types;
    }

    public HashMap<String, String> getBorders() {
        return borders;
    }

    public void setIntParam(String name, int value) {
        if (checkParam(name, "int")) {
            return;
        }

        intParams.put(name, value);
    }

    public void setDoubleParam(String name, double value) {
        if (checkParam(name, "double")) {
            return;
        }

        doubleParams.put(name, value);
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
