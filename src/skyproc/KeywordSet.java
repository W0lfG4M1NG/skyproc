/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LExporter;
import lev.LShrinkArray;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * A set of keywords associated with a major record.
 * @author Justin Swanson
 */
public class KeywordSet extends SubRecord {

    private static final Type[] types = {Type.KSIZ, Type.KWDA};
    SubData counter = new SubData(Type.KSIZ);
    SubFormArray keywords = new SubFormArray(Type.KWDA, 0);

    KeywordSet() {
	super(types);
    }

    @Override
    SubRecord getNew(Type type) {
	return new KeywordSet();
    }

    @Override
    public void clear() {
	counter.clear();
	keywords.clear();
    }

    @Override
    Boolean isValid() {
	return counter.isValid()
		&& keywords.isValid();
    }

    @Override
    int getHeaderLength() {
	return 0;
    }

    @Override
    int getContentLength(Mod srcMod) {
	return counter.getTotalLength(srcMod)
		+ keywords.getTotalLength(srcMod);
    }

    @Override
    void export(LExporter out, Mod srcMod) throws IOException {
	if (isValid()) {
	    counter.export(out, srcMod);
	    keywords.export(out, srcMod);
	}
    }

    @Override
    void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	switch (getNextType(in)) {
	    case KSIZ:
		counter.parseData(in);
		keywords = new SubFormArray(Type.KWDA, counter.toInt());
		break;
	    case KWDA:
		keywords.parseData(in);
		break;
	}
    }

    @Override
    void standardizeMasters(Mod srcMod) {
	super.standardizeMasters(srcMod);
	keywords.standardizeMasters(srcMod);
    }

    /**
     * Returns a COPY of the list of FormIDs associated with this keyword set.
     * @return
     */
    public ArrayList<FormID> getKeywordRefs() {
	ArrayList<FormID> out = new ArrayList<FormID>(keywords.size());
	for (FormID id : keywords) {
	    out.add(id);
	}
	return out;
    }

    /**
     * Adds a keyword to the list
     * @param keywordRef A KYWD formID
     */
    public void addKeywordRef(FormID keywordRef) {
	keywords.add(keywordRef);
	counter.modValue(1);
    }

    /**
     * Removes a keyword to the list
     * @param keywordRef A KYWD formID
     */
    public void removeKeywordRef(FormID keywordRef) {
	if (keywords.remove(keywordRef)) {
	    counter.modValue(-1);
	}
    }
}