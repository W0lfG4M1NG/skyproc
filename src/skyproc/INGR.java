/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LChannel;
import lev.LExporter;
import lev.LFlags;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * Ingredient Records
 *
 * @author Justin Swanson
 */
public class INGR extends MagicItem {

    // Static prototypes and definitions
    static final SubPrototype INGRproto = new SubPrototype(MagicItem.magicItemProto) {

	@Override
	protected void addRecords() {
	    after(new ScriptPackage(), "EDID");
	    remove("DESC");
	    add(SubString.getNew("MODL", true));
	    add(new SubData("MODT"));
	    add(new AltTextures("MODS"));
	    add(new SubForm("YNAM"));
	    add(new SubForm("ZNAM"));
	    add(new DATA());
	    add(new ENIT());
	    reposition("EFID");
	    add(SubString.getNew("ICON", true));
	    add(SubString.getNew("MICO", true));
	    add(new SubForm("ETYP"));
	}
    };

    static class DATA extends SubRecord {

	int value = 0;
	float weight = 0;

	DATA() {
	    super();
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(value);
	    out.write(weight);
	}

	@Override
	void parseData(LChannel in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in, srcMod);
	    value = in.extractInt(4);
	    weight = in.extractFloat();
	    if (SPGlobal.logging()) {
		logSync("", "Setting DATA:    Weight: " + weight);
	    }
	}

	@Override
	SubRecord getNew(String type) {
	    return new DATA();
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 8;
	}

	@Override
	ArrayList<String> getTypes() {
	    return Record.getTypeList("DATA");
	}
    }

    static class ENIT extends SubRecord {

	int baseCost = 0;
	LFlags flags = new LFlags(4);

	ENIT() {
	    super();
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(baseCost);
	    out.write(flags.export(), 4);
	}

	@Override
	void parseData(LChannel in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in, srcMod);
	    baseCost = in.extractInt(4);
	    flags.set(in.extract(4));
	    if (SPGlobal.logging()) {
		logSync("", "Base cost: " + baseCost + ", flags: " + flags);
	    }
	}

	@Override
	SubRecord getNew(String type) {
	    return new ENIT();
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 8;
	}

	@Override
	ArrayList<String> getTypes() {
	    return Record.getTypeList("ENIT");
	}
    }

    // Enums
    /**
     *
     */
    public enum INGRFlag {

	/**
	 *
	 */
	ManualCalc(0),
	/**
	 *
	 */
	Food(1),
	/**
	 *
	 */
	ReferencesPersist(8);
	int value;

	INGRFlag(int value) {
	    this.value = value;
	}
    }

    // Common Functions
    INGR() {
	super();
	subRecords.setPrototype(INGRproto);
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("INGR");
    }

    @Override
    Record getNew() {
	return new INGR();
    }

    // Get/set
    /**
     *
     * @return
     */
    public ScriptPackage getScriptPackage() {
	return subRecords.getScripts();
    }

    /**
     *
     * @param groundModel
     */
    public void setModel(String groundModel) {
	subRecords.setSubString("MODL", groundModel);
    }

    /**
     *
     * @return
     */
    public String getModel() {
	return subRecords.getSubString("MODL").print();
    }

    /**
     *
     * @param pickupSound
     */
    public void setPickupSound(FormID pickupSound) {
	subRecords.setSubForm("YNAM", pickupSound);
    }

    /**
     *
     * @return
     */
    public FormID getPickupSound() {
	return subRecords.getSubForm("YNAM").getForm();
    }

    /**
     *
     * @param dropSound
     */
    public void setDropSound(FormID dropSound) {
	subRecords.setSubForm("ZNAM", dropSound);
    }

    /**
     *
     * @return
     */
    public FormID getDropSound() {
	return subRecords.getSubForm("ZNAM").getForm();
    }

    DATA getDATA() {
	return (DATA) subRecords.get("DATA");
    }

    /**
     *
     * @param weight
     */
    public void setWeight(float weight) {
	getDATA().weight = weight;
    }

    /**
     *
     * @return
     */
    public float getWeight() {
	return getDATA().weight;
    }

    /**
     *
     * @return
     */
    public int getValue() {
	return getDATA().value;
    }

    /**
     *
     * @param value
     */
    public void setValue(int value) {
	getDATA().value = value;
    }

    ENIT getENIT() {
	return (ENIT) subRecords.get("ENIT");
    }

    /**
     *
     * @param baseCost
     */
    public void setBaseCost(int baseCost) {
	getENIT().baseCost = baseCost;
    }

    /**
     *
     * @return
     */
    public int getBaseCost() {
	return getENIT().baseCost;
    }

    /**
     *
     * @param flag
     * @param on
     */
    public void set(INGRFlag flag, boolean on) {
	getENIT().flags.set(flag.value, on);
    }

    /**
     *
     * @param flag
     * @return
     */
    public boolean get(INGRFlag flag) {
	return getENIT().flags.get(flag.value);
    }

    /**
     *
     * @param filename
     */
    public void setInventoryIcon(String filename) {
	subRecords.setSubString("ICON", filename);
    }

    /**
     *
     * @return
     */
    public String getInventoryIcon() {
	return subRecords.getSubString("ICON").print();
    }

    /**
     *
     * @param filename
     */
    public void setMessageIcon(String filename) {
	subRecords.setSubString("MICO", filename);
    }

    /**
     *
     * @return
     */
    public String getMessageIcon() {
	return subRecords.getSubString("MICO").print();
    }

    /**
     *
     * @param equipType
     */
    public void setEquipType(FormID equipType) {
	subRecords.setSubForm("ETYP", equipType);
    }

    /**
     *
     * @return
     */
    public FormID getEquipType() {
	return subRecords.getSubForm("ETYP").getForm();
    }
}
