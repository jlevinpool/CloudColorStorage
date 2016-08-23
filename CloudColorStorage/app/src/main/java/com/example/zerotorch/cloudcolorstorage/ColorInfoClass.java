package com.example.zerotorch.cloudcolorstorage;

/**
 * Created by ZeroTorch on 8/11/2016.
 */
public class ColorInfoClass {

    // Class Variables
    private Long key;  // Color NDB Key
    private String title;  // Color Title
    private Integer value;  // Color Value
    private Long pKey;  // Parent's NDB key

    // Accessors Functions
    public Long get_key() { return key; }
    public String get_title() { return title; }
    public Integer get_value() { return value; }
    public Long get_pKey() { return pKey; }

    // Mutator Functions
    public void set_key(Long key) { this.key = key; }
    public void set_title(String title) { this.title = title; }
    public void set_value(Integer value) { this.value = value; }
    public void set_pKey(Long pKey) { this.pKey = pKey; }

    // Color Value as Hex - From: https://github.com/danielnilsson9/color-picker-view
    public String get_value_HexString() {
        return String.format("#%06X", 0xFFFFFFFF & this.value);
    }
}
