/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.zip.DataFormatException;
import lev.LExporter;
import lev.LFlags;
import lev.LShrinkArray;
import lev.Ln;
import skyproc.EmbeddedScripts.Script;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
class Condition extends SubShell {

    static Type[] types = {Type.CTDA, Type.CIS1, Type.CIS2};
    Cond cond = new Cond();
    SubString CIS1 = new SubString(Type.CIS1, true);
    SubString CIS2 = new SubString(Type.CIS2, true);

    Condition() {
	super(types);

	subRecords.add(cond);
	subRecords.add(CIS1);
	subRecords.add(CIS2);
    }

    @Override
    SubRecord getNew(Type type) {
	return new Condition();
    }

    @Override
    Boolean isValid() {
	return cond.isValid();
    }

    class Cond extends SubRecord {

	Operator operator;
	LFlags flags = new LFlags(1);
	byte[] fluff;
	FormID comparisonValueForm = new FormID();
	float comparisonValueFloat;
	int functionIndex;
	byte[] padding;
	FormID param1form = new FormID();
	int param1int;
	FormID param2form = new FormID();
	int param2int;
	RunOnType runType;
	FormID reference = new FormID();
	FormID param3form = new FormID();
	int param3int;
	boolean param1formF = false;
	boolean param2formF = false;
	boolean param3formF = false;

	Cond() {
	    super(Type.CTDA);
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    LFlags tmp = new LFlags(Ln.toByteArray(operator.ordinal(), 1));
	    for (int i = 3 ; i < 8 ; i++) {
		tmp.set(i, flags.is(i));
	    }
	    out.write(tmp.export(),1);
	    out.write(fluff, 3);

	    if (get(CondFlag.UseGlobal)) {
		comparisonValueForm.export(out);
	    } else {
		out.write(comparisonValueFloat);
	    }

	    out.write(functionIndex, 2);
	    out.write(padding, 2);

	    if (param1formF) {
		param1form.export(out);
	    } else {
		out.write(param1int);
	    }

	    if (param2formF) {
		param2form.export(out);
	    } else {
		out.write(param2int);
	    }

	    out.write(runType.ordinal());
	    reference.export(out);

	    if (param3formF) {
		param3form.export(out);
	    } else {
		out.write(param3int);
	    }
	}

	@Override
	void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);
	    // First byte is both operator and flags, ugly.
	    flags.set(in.extract(1));
	    LFlags tmp = new LFlags(flags.export());
	    for (int i = 3 ; i < 8 ; i++) {
		tmp.set(i, false);
	    }
	    operator = Operator.values()[Ln.arrayToInt(tmp.export())];
	    fluff = in.extract(3);

	    if (get(CondFlag.UseGlobal)) {
		comparisonValueForm.setInternal(in.extract(4));
	    } else {
		comparisonValueFloat = in.extractFloat();
	    }

	    functionIndex = in.extractInt(2);
	    padding = in.extract(2);

	    Script function = EmbeddedScripts.getScript(functionIndex);
	    param1formF = function.isForm(0);
	    param2formF = function.isForm(1);
	    param3formF = function.isForm(2);

	    if (param1formF) {
		param1form.setInternal(in.extract(4));
	    } else {
		param1int = in.extractInt(4);
	    }

	    if (param2formF) {
		param2form.setInternal(in.extract(4));
	    } else {
		param2int = in.extractInt(4);
	    }

	    runType = RunOnType.values()[in.extractInt(4)];
	    reference.setInternal(in.extract(4));

	    if (param3formF) {
		param3form.setInternal(in.extract(4));
	    } else {
		param3int = in.extractInt(4);
	    }

	    if (SPGlobal.logging()) {
		logSync("", "New Condition.  Function: " + function.name + ", index: " + functionIndex);
		logSync("", "  Operator: " + operator + ", flags: " + flags);
		logSync("", "  Comparison Val: " + comparisonValueForm + "|" + comparisonValueFloat + ", Param 1: " + param1form + "|" + param1int);
		logSync("", "  Param 2: " + param2form + "|" + param2int + ", Param 3: " + param3form + "|" + param3int);
		logSync("", "  Run Type:" + runType + ", Reference: " + reference);
	    }
	}

	@Override
	void standardizeMasters(Mod srcMod) {
	    super.standardizeMasters(srcMod);
	    comparisonValueForm.standardize(srcMod);
	    param1form.standardize(srcMod);
	    param2form.standardize(srcMod);
	    reference.standardize(srcMod);
	    param3form.standardize(srcMod);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new Condition();
	}

	@Override
	public void clear() {
	    throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	Boolean isValid() {
	    return true;
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 32;
	}
    }

    public boolean get(CondFlag flag) {
	return cond.flags.is(flag.value);
    }

    public void set(CondFlag flag, boolean on) {
	cond.flags.set(flag.value, on);
    }

    public enum CondFlag {

	OR(3),
	UseAliases(4),
	UseGlobal(5),
	UsePackData(6),
	SwapSubjectAndTarget(7);

	int value;

	CondFlag (int value) {
	    this.value = value;
	}
    }

    public enum RunOnType {

	Subject,
	Target,
	Reference,
	CombatTarget,
	LinkedRef,
	QuestAlias,
	PackageData,
	EventData;
    }

    public enum Operator {

	EqualTo,
	NotEqualTo,
	GreaterThan,
	GreaterThanOrEqual,
	LessThan,
	LessThanOrEqual;
    }
}
