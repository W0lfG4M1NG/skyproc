/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import lev.LExporter;
import lev.Ln;
import lev.debug.LDebug;
import skyproc.exceptions.BadMod;
import skyproc.exceptions.BadRecord;
import skyproc.gui.SPDefaultGUI;
import skyproc.gui.SPProgressBarPlug;

/**
 *
 * @author Justin Swanson
 */
public class SkyProcTester {

    static ArrayList<String> badIDs;

    /**
     */
    public static void runTests() {
	setSkyProcGlobal();

	try {

	    SPGlobal.testing = true;
	    SPDefaultGUI gui = new SPDefaultGUI("Tester Program", "A tester program meant to flex SkyProc.");
	    validate();
	    gui.finished();

	} catch (Exception e) {
	    SPGlobal.logException(e);
	}
	try {
	    LDebug.wrapUp();
	} catch (IOException ex) {
	}
    }

    private static void validate() throws Exception {

	badIDs = new ArrayList<String>();
	badIDs.add("0010B115");  //EnchSilverSword
	badIDs.add("0010A27F");  //TrapLightningRune
	badIDs.add("0010A27E");  //TrapFrostRune
	badIDs.add("00073328");  //TrapFireRune
	badIDs.add("00018A45");  //RiverwoodZone
	badIDs.add("0000001E");  //NoZoneZone

	SubStringPointer.shortNull = false;

//	GRUP_TYPE[] types = {GRUP_TYPE.PERK};
	GRUP_TYPE[] types = GRUP_TYPE.values();

	FormID.allIDs.clear();
	SPImporter importer = new SPImporter();
	importer.importMod(new ModListing("Skyrim.esm"), SPGlobal.pathToData, types);
	boolean idFail = false;
	for (FormID id : FormID.allIDs) {
	    if (!id.isNull() && id.getMaster() == null && !badIDs.contains(id.toString())) {
		System.out.println("A bad id: " + id);
		idFail = true;
		break;
	    }
	}
	if (idFail) {
	    System.out.println("Some FormIDs were unstandardized!!");
	    return;
	} else {
	    System.out.println("All FormIDs properly standardized.");
	}
	
	SPProgressBarPlug.progress.reset();
	SPProgressBarPlug.progress.setMax(types.length);
	
	for (GRUP_TYPE g : types) {
	    if (!test(g)) {
		SPProgressBarPlug.progress.setStatus("FAILED: " + g);
		break;
	    }
	    SPProgressBarPlug.progress.setStatus("Validating DONE");
	}

    }

    private static boolean test(GRUP_TYPE type) throws IOException, BadRecord, BadMod {
	System.out.println("Testing " + type);
	SPProgressBarPlug.progress.setStatus("Validating " + type);
	SPProgressBarPlug.progress.pause(true);

	boolean passed = true;
	Mod patch = new Mod(new ModListing("Test.esp"));
	patch.setFlag(Mod.Mod_Flags.STRING_TABLED, false);
	patch.addAsOverrides(SPGlobal.getDB(), type);
	patch.setAuthor("Leviathan1753");
	patch.export(new LExporter(SPGlobal.pathToData + patch.getName()), patch);
	if (type != GRUP_TYPE.ENCH) {
	    passed = passed && SPExporter.validateRecordLengths(SPGlobal.pathToData + "Test.esp", 10);
	}
	File validF = new File("Validation Files/" + type.toString() + "validation.esp");
	if (validF.isFile()) {
	    passed = Ln.validateCompare(SPGlobal.pathToData + "Test.esp", validF.getPath(), 10) && passed;
	} else {
	    System.out.println("Didn't have a source file to validate bytes to.");
	}

	SPProgressBarPlug.progress.pause(false);
	SPProgressBarPlug.progress.incrementBar();
	return passed;
    }

    private static void setSkyProcGlobal() {
	SPGlobal.createGlobalLog();
	LDebug.timeElapsed = true;
	SPGlobal.setGlobalPatch(new Mod(new ModListing("Test", false)));
    }
}
