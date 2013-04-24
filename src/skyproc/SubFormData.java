/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import lev.LImport;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
class SubFormData extends SubRecordTyped {

    FormID ID = new FormID();
    byte[] data;

    SubFormData(String type, FormID id, byte[] data) {
	super(type);
	ID = id;
	this.data = data;
    }

    SubFormData(String in) {
	super(in);
    }

    @Override
    void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	super.parseData(in, srcMod);
	ID.setInternal(in.extract(4));
	setData(in.extract(4));
    }

    @Override
    int getContentLength(ModExporter out) {
	return data.length + ID.getContentLength();
    }

    @Override
    SubRecord getNew(String type_) {
	return new SubFormData(type_);
    }

    @Override
    void export(ModExporter out) throws IOException {
	super.export(out);
	ID.export(out);
	out.write(data, 0);
    }

    public byte[] getData() {
	return data;
    }

    public void setData(byte[] in) {
	data = in;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final SubFormData other = (SubFormData) obj;
	if (!ID.equals(other.ID)
		|| !Arrays.equals(this.data, other.data)) {
	    return false;
	}
	return true;
    }

    @Override
    public int hashCode() {
	int hash = super.hashCode();
	hash = 61 * hash + Arrays.hashCode(this.data);
	return hash;
    }
    
    @Override
    ArrayList<FormID> allFormIDs() {
	ArrayList<FormID> out = new ArrayList<>(1);
	out.add(ID);
	return out;
    }

    void setForm(byte[] in) throws BadParameter {
	ID.setInternal(in);
    }

    void copyForm(FormID in) {
	ID = new FormID(in);
    }

    /**
     *
     * @param id FormID to set the record's to.
     */
    public void setForm(FormID id) {
	ID = id;
    }

    byte[] getFormArray(Boolean master) {
	return ID.getInternal(master);
    }

    /**
     *
     * @return The FormID string of the Major Record.
     */
    public String getFormStr() {
	return ID.getArrayStr(true);
    }

    /**
     *
     * @return The name of the mod from which this Major Record originates.
     */
    public ModListing getFormMaster() {
	return ID.getMaster();
    }

    FormID copyOfForm() {
	return new FormID(ID);
    }

    /**
     * Returns the FormID object of the Sub Record. Note that any changes made
     * to this FormID will be reflected in the Sub Record also.
     *
     * @return The FormID object of the Sub Record.
     */
    public FormID getForm() {
	return ID;
    }
}
