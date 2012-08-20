/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

/**
 *
 * @author Justin Swanson
 */
public class SaveEnum extends Setting<Enum> {

    Enum prototype;

    public SaveEnum(String title_, Enum data_, Boolean patchChanging) {
        super(title_, data_, patchChanging);
	prototype = data_;
    }

    @Override
    public String toString() {
        return data.toString();
    }

    @Override
    public void parse(String in) {
        data = prototype.valueOf(prototype.getClass(), in);
    }

    @Override
    public Setting<Enum> copyOf() {
        lev.gui.SaveEnum out = new lev.gui.SaveEnum(title, data, patchChanging);
        out.tie = tie;
        return out;
    }

}