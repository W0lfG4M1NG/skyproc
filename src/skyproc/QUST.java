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
 *
 * @author Justin Swanson
 */
public class QUST extends MajorRecordNamed {

    // Static prototypes and definitions
    static SubPrototype ALSTALLSproto = new SubPrototype() {
	@Override
	protected void addRecords() {
	    add(SubString.getNew("ALID", true));
	    add(new SubFlag("FNAM", 4));
	    add(new SubForm("ALUA"));
	    add(new SubForm("ALCO"));
	    add(new SubList<>(new SubForm("ALEQ")));
	    add(SubString.getNew("ALFE", false));
	    add(new SubForm("ALFL"));
	    add(new SubForm("ALFR"));
	    add(new SubForm("ALRT"));
	    add(new SubInt("ALFD"));
	    add(new SubList<>(new Condition()));
	    add(new SubInt("ALCA"));
	    add(new SubInt("ALCL"));
	    add(new SubInt("ALEA"));
	    add(new SubInt("ALFA"));
	    add(new SubInt("ALNA"));
	    add(new SubInt("ALNT"));
	    add(new SubForm("VTCK"));
	    forceExport("VTCK");
	    add(new SubData("ALED"));
	    add(new SubForm("ALDN"));
	    add(new SubList<>(new SubForm("ALFC")));
	    add(new SubList<>(new SubInt("ALFI")));
	    add(new SubList<>(new SubForm("ALPC")));
	    add(new SubList<>(new SubForm("ALSP")));
	    add(new SubListCounted<>("COCT", 4, new SubFormInt("CNTO")));
	    add(new SubForm("SPOR"));
	    add(new SubForm("ECOR"));
	    add(new SubForm("KNAM"));
	    add(new KeywordSet());
	    add(new SubInt("NAM0"));
	    add(new SubInt("QTGL"));
	}
    };
    static final SubPrototype aliasLocationProto = new SubPrototype() {
	@Override
	protected void addRecords() {
	    add(new SubInt("ALLS"));
	    mergeIn(ALSTALLSproto);
	}
    };
    static final SubPrototype aliasReferenceProto = new SubPrototype() {
	@Override
	protected void addRecords() {
	    add(new SubInt("ALST"));
	    mergeIn(ALSTALLSproto);
	}
    };
    static final SubPrototype questLogEntryProto = new SubPrototype() {
	@Override
	protected void addRecords() {
	    add(new SubFlag("QSDT", 1));
	    add(new SubForm("NAM0"));
	    add(SubString.getNew("CNAM", true));
	    add(new SubData("SCHR"));
	    add(new SubForm("QNAM"));
	    add(SubString.getNew("SCTX", false));
	    add(new SubList<>(new Condition()));
	}
    };
    static final SubPrototype questStageProto = new SubPrototype() {
	@Override
	protected void addRecords() {
	    add(new INDX());
	    add(new SubList<>(new QuestLogEntry()));
	}
    };
    static final SubPrototype questTargetProto = new SubPrototype() {
	@Override
	protected void addRecords() {
	    add(new QuestTargetData());
	    add(new SubList<>(new Condition()));
	}
    };
    static final SubPrototype questObjectiveProto = new SubPrototype() {
	@Override
	protected void addRecords() {
	    add(new SubInt("QOBJ", 2));
	    add(new SubData("FNAM"));
	    add(new SubStringPointer("NNAM", SubStringPointer.Files.DLSTRINGS));
	    add(new SubList<>(new QuestTarget()));
	}
    };
    static final SubPrototype QUSTproto = new SubPrototype(MajorRecordNamed.namedProto) {
	@Override
	protected void addRecords() {
	    after(new ScriptPackage(), "EDID");
	    reposition("FULL");
	    add(new DNAM());
	    add(SubString.getNew("ENAM", false));
	    add(new SubForm("QTGL"));
	    add(SubString.getNew("FLTR", true));
	    add(new SubList<>(new Condition()));
	    add(new SubData("NEXT"));
	    forceExport("NEXT");
	    add(new SubList<>(new QuestStage()));
	    add(new SubInt("ANAM"));
	    add(new SubList<>(new QuestObjective()));
	    add(new SubList<>(new AliasLocation()));
	    add(new SubList<>(new AliasReference()));
	}
    };

    abstract static class Alias extends SubShellBulkType {

	Alias(SubPrototype proto) {
	    super(proto, false);
	}

	public void setName(String name) {
	    subRecords.setSubStringPointer("ALID", name);
	}

	public String getName () {
	    return subRecords.getSubString("ALID").print();
	}
    }

    public static class AliasLocation extends Alias {

	AliasLocation() {
	    super(aliasLocationProto);
	}

	public AliasLocation(String name) {
	    this();
	    setName(name);
	}

	public void setLocation(int loc) {
	    subRecords.setSubInt("ALLS", loc);
	}

	public int getLocation() {
	    return subRecords.getSubInt("ALLS").get();
	}
    }

    public static class AliasReference extends Alias {

	AliasReference() {
	    super(aliasReferenceProto);
	}

	public AliasReference(String name) {
	    this();
	    setName(name);
	}

	public void setReference(int ref) {
	    subRecords.setSubInt("ALST", ref);
	}

	public int getReference() {
	    return subRecords.getSubInt("ALST").get();
	}
    }

    static class DNAM extends SubRecordTyped {

	LFlags flags1 = new LFlags(1);
	LFlags flags2 = new LFlags(1);
	byte priority = 0;
	byte unknown = 0;
	int unknown2 = 0;
	int questType = 0;

	DNAM() {
	    super("DNAM");
	}

	@Override
	SubRecord getNew(String type) {
	    return new DNAM();
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 12;
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(flags1.export());
	    out.write(flags2.export());
	    out.write(priority, 1);
	    out.write(unknown, 1);
	    out.write(unknown2);
	    out.write(questType);
	}

	@Override
	void parseData(LChannel in) throws BadRecord, BadParameter, DataFormatException {
	    super.parseData(in);
	    flags1.set(in.extract(1));
	    flags2.set(in.extract(1));
	    priority = in.extract(1)[0];
	    unknown = in.extract(1)[0];
	    unknown2 = in.extractInt(4);
	    questType = in.extractInt(4);
	}
    }

    static class INDX extends SubRecordTyped {

	int index = 0;
	LFlags flags = new LFlags(1);

	INDX() {
	    super("INDX");
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(index, 2);
	    out.write(flags.export());
	}

	@Override
	void parseData(LChannel in) throws BadRecord, BadParameter, DataFormatException {
	    super.parseData(in);
	    index = in.extractInt(2);
	    flags.set(in.extract(2));
	}

	@Override
	SubRecord getNew(String type) {
	    return new INDX();
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 4;
	}
    }

    public static class QuestStage extends SubShellBulkType {

	public QuestStage() {
	    super(questStageProto, false);
	}

	INDX getINDX() {
	    return (INDX) subRecords.get("INDX");
	}

	public int getJournalIndex() {
	    return getINDX().index;
	}

	public void setJournalIndex(int value) {
	    getINDX().index = value;
	}

	public boolean get(QuestStageFlags flag) {
	    return getINDX().flags.get(flag.ordinal() + 1);
	}

	public void set(QuestStageFlags flag, boolean on) {
	    getINDX().flags.set(flag.ordinal() + 1, on);
	}

	public ArrayList<QuestLogEntry> getLogEntries() {
	    return subRecords.getSubList("QSDT").toPublic();
	}

	public void addLogEntry(QuestLogEntry entry) {
	    subRecords.getSubList("QSDT").add(entry);
	}
    }

    public static class QuestLogEntry extends SubShell {

	public QuestLogEntry() {
	    super(questLogEntryProto);
	}

	@Override
	SubRecord getNew(String type) {
	    return new QuestLogEntry();
	}

	public void set(QuestLogFlags flag, boolean on) {
	    subRecords.setSubFlag("QSDT", flag.ordinal(), on);
	}

	public boolean get(QuestLogFlags flag) {
	    return subRecords.getSubFlag("QSDT").is(flag.ordinal());
	}

	/**
	 *
	 * @return
	 */
	public ArrayList<Condition> getConditions() {
	    return subRecords.getSubList("CTDA").toPublic();
	}

	/**
	 *
	 * @param c
	 */
	public void addCondition(Condition c) {
	    subRecords.getSubList("CTDA").add(c);
	}

	/**
	 *
	 * @param c
	 */
	public void removeCondition(Condition c) {
	    subRecords.getSubList("CTDA").remove(c);
	}
    }

    public static class QuestObjective extends SubShellBulkType {

	QuestObjective() {
	    super(questObjectiveProto, false);
	}

	public QuestObjective(int index, String name) {
	    this();
	    setIndex(index);
	    setName(name);
	}

	@Override
	SubRecord getNew(String type) {
	    return new QuestObjective();
	}

	public final void setIndex(int index) {
	    subRecords.setSubInt("QOBJ", index);
	}

	public int getIndex() {
	    return subRecords.getSubInt("QOBJ").get();
	}

	public final void setName(String in) {
	    subRecords.setSubStringPointer("NNAM", in);
	}

	public String getName() {
	    return subRecords.getSubStringPointer("NNAM").print();
	}

	public ArrayList<QuestTarget> getTargets() {
	    return subRecords.getSubList("QSTA").toPublic();
	}

	public void addTarget(QuestTarget target) {
	    subRecords.getSubList("QSTA").add(target);
	}
    }

    static class QuestTargetData extends SubRecordTyped {

	int targetAlias = 0;
	LFlags flags = new LFlags(4);

	QuestTargetData() {
	    super("QSDT");
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(targetAlias);
	    out.write(flags.export());
	}

	@Override
	void parseData(LChannel in) throws BadRecord, BadParameter, DataFormatException {
	    super.parseData(in);
	    targetAlias = in.extractInt(4);
	    flags.set(in.extract(4));
	}

	@Override
	SubRecord getNew(String type) {
	    return new QuestTargetData();
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 8;
	}
    }

    public static class QuestTarget extends SubShell {

	QuestTarget() {
	    super(questTargetProto);
	}

	public QuestTarget(int aliasID) {
	    this();
	    setTargetAlias(aliasID);
	}

	@Override
	SubRecord getNew(String type) {
	    return new QuestTarget();
	}

	QuestTargetData getData() {
	    return (QuestTargetData) subRecords.get("QSDT");
	}

	public final void setTargetAlias(int aliasID) {
	    getData().targetAlias = aliasID;

	}

	public int getTargetAlias() {
	    return getData().targetAlias;
	}

	/**
	 *
	 * @return
	 */
	public ArrayList<Condition> getConditions() {
	    return subRecords.getSubList("CTDA").toPublic();
	}

	/**
	 *
	 * @param c
	 */
	public void addCondition(Condition c) {
	    subRecords.getSubList("CTDA").add(c);
	}

	/**
	 *
	 * @param c
	 */
	public void removeCondition(Condition c) {
	    subRecords.getSubList("CTDA").remove(c);
	}
    }

    // Enums
    public enum QuestStageFlags {

	StartUpStage,
	ShutDownStage,
	KeepInstanceDataFromHereOn;
    }

    public enum QuestLogFlags {

	CompleteQuest,
	FailQuest;
    }

    QUST() {
	super();
	subRecords.setPrototype(QUSTproto);
    }

    QUST(Mod modToOriginateFrom, String edid) {
	this();
	originateFrom(modToOriginateFrom, edid);
	DNAM dnam = (DNAM) subRecords.get("DNAM");
	dnam.flags1.set(0, true);
	dnam.flags1.set(4, true);
	dnam.flags2.set(0, true);
	subRecords.getSubInt("ANAM").set(0);
    }

    @Override
    Record getNew() {
	return new QUST();
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("QUST");
    }

    /**
     *
     * @return
     */
    public ScriptPackage getScriptPackage() {
	return subRecords.getScripts();
    }

    /**
     *
     * @return
     */
    public ArrayList<Condition> getConditions() {
	return subRecords.getSubList("CTDA").toPublic();
    }

    /**
     *
     * @param c
     */
    public void addCondition(Condition c) {
	subRecords.getSubList("CTDA").add(c);
    }

    /**
     *
     * @param c
     */
    public void removeCondition(Condition c) {
	subRecords.getSubList("CTDA").remove(c);
    }

    public ArrayList<QuestStage> getQuestStages() {
	return subRecords.getSubList("INDX").toPublic();
    }

    public void addQuestStage(QuestStage stage) {
	subRecords.getSubList("INDX").add(stage);
    }

    public ArrayList<AliasReference> getReferenceAliases() {
	return subRecords.getSubList("ALST").toPublic();
    }

    public ArrayList<AliasLocation> getLocationAliases() {
	return subRecords.getSubList("ALLS").toPublic();
    }
}
