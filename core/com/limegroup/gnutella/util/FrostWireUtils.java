package com.limegroup.gnutella.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.limewire.setting.SettingsFactory;
import org.limewire.util.CommonUtils;
import org.limewire.util.OSUtils;
import org.limewire.util.SystemUtils;
import org.limewire.util.SystemUtils.SpecialLocations;
import org.limewire.util.VersionUtils;

import com.limegroup.gnutella.GUID;
import com.limegroup.gnutella.settings.ApplicationSettings;


/**
 * This class handles common utility functions that many classes
 * may want to access.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public final class FrostWireUtils {

	/** 
	 * Constant for the current version of FrostWire.
	 */
	private static final String FROSTWIRE_VERSION = "5.2.10";

    /**
     * The cached value of the major revision number.
     */
    private static final int _majorVersionNumber = 
        getMajorVersionNumberInternal(FROSTWIRE_VERSION);

    /**
     * The cached value of the minor revision number.
     */
    private static final int _minorVersionNumber = 
        getMinorVersionNumberInternal(FROSTWIRE_VERSION);
        
    /**
     * The cached value of the really minor version number.
     */
    private static final int _serviceVersionNumber =
        getServiceVersionNumberInternal(FROSTWIRE_VERSION);

    /**
     * The cached value of the GUESS major revision number.
     */
    private static final int _guessMajorVersionNumber = 0;

    /**
     * The cached value of the GUESS minor revision number.
     */
    private static final int _guessMinorVersionNumber = 1;

    /**
     * The cached value of the Ultrapeer major revision number.
     */
    private static final int _upMajorVersionNumber = 0;

    /**
     * The cached value of the Ultrapeer minor revision number.
     */
    private static final int _upMinorVersionNumber = 1;
    
    /**
     * The vendor code for QHD.  WARNING: to avoid character
     * encoding problems, this is hard-coded in QueryReply as well.  So if you
     * change this, you must change QueryReply.
     */
    public static final String QHD_VENDOR_NAME = "LIME";
     
	/**
	 * Cached constant for the HTTP Server: header value.
	 */
	private static final String HTTP_SERVER;

    /** Whether or not a temporary directory is in use. */
    private static boolean temporaryDirectoryInUse;

	/**
	 * Make sure the constructor can never be called.
	 */
	private FrostWireUtils() {}
    
	/**
	 * Initialize the settings statically. 
	 */
	static {
		if(!FROSTWIRE_VERSION.endsWith("Pro")) {
			HTTP_SERVER = "LimeWire/" + FROSTWIRE_VERSION;
		}
		else {
			HTTP_SERVER = ("LimeWire/"+FROSTWIRE_VERSION.
                           substring(0, FROSTWIRE_VERSION.length()-4)+" (Pro)");
		}
	}
	
	/** Gets the major version of GUESS supported.
     */
    public static int getGUESSMajorVersionNumber() {    
        return _guessMajorVersionNumber;
    }
    
    /** Gets the minor version of GUESS supported.
     */
    public static int getGUESSMinorVersionNumber() {
        return _guessMinorVersionNumber;
    }

    /** Gets the major version of Ultrapeer Protocol supported.
     */
    public static int getUPMajorVersionNumber() {    
        return _upMajorVersionNumber;
    }
    
    /** Gets the minor version of Ultrapeer Protocol supported.
     */
    public static int getUPMinorVersionNumber() {
        return _upMinorVersionNumber;
    }

	/**
	 * Returns the current version number of FrostWire as
     * a string, e.g., "5.2.9".
	 */
	public static String getFrostWireVersion() {
        return FROSTWIRE_VERSION;
	}

    /** Gets the major version of LimeWire.
     */
    public static int getMajorVersionNumber() {    
        return _majorVersionNumber;
    }
    
    /** Gets the minor version of LimeWire.
     */
    public static int getMinorVersionNumber() {
        return _minorVersionNumber;
    }
    
    /** Gets the minor minor version of LimeWire.
     */
   public static int getServiceVersionNumber() {
        return _serviceVersionNumber;
   }

    static int getMajorVersionNumberInternal(String version) {
        int firstDot = version.indexOf(".");
        String majorStr = version.substring(0, firstDot);
        return new Integer(majorStr).intValue();
    }

    static int getMinorVersionNumberInternal(String version) {
        int firstDot = version.indexOf(".");
        String minusMajor = version.substring(firstDot + 1);
        int secondDot = minusMajor.indexOf(".");
        String minorStr = minusMajor.substring(0, secondDot);
        return new Integer(minorStr).intValue();
    }

    static int getServiceVersionNumberInternal(String version) {
        int firstDot = version.indexOf(".");
        int secondDot = version.indexOf(".", firstDot + 1);

        int p = secondDot + 1;
        int q = p;

        while (q < version.length() && Character.isDigit(version.charAt(q))) {
            q++;
        }

        if (p != q) {
            String service = version.substring(p, q);
            return new Integer(service).intValue();
        }

        return 0;
    }

	/**
	 * Returns the string for the server that should be reported in the HTTP
	 * "Server: " tag.
	 * 
	 * @return the HTTP "Server: " header value
	 */
	public static String getHttpServer() {
		return HTTP_SERVER;
	}
    
    /**
     * Updates a URL to contain common information about the LW installation.
     */
    public static String addLWInfoToUrl(String url, byte[] myClientGUID) {
        if(url.indexOf('?') == -1)
            url += "?";
        else
            url += "&";
        url += "guid=" + EncodingUtils.encode(new GUID(myClientGUID).toHexString())+ 
            "&lang=" + EncodingUtils.encode(ApplicationSettings.getLanguage()) +
            "&lv="   + EncodingUtils.encode(FrostWireUtils.getFrostWireVersion()) +
            "&jv="   + EncodingUtils.encode(VersionUtils.getJavaVersion()) +
            "&os="   + EncodingUtils.encode(OSUtils.getOS()) +
            "&osv="  + EncodingUtils.encode(OSUtils.getOSVersion());
               
        return url;
    }

    /** Returns whether or not a temporary directory is in use. */
    public static boolean isTemporaryDirectoryInUse() {
        return temporaryDirectoryInUse;
    }
    
    /** Returns whether or not failures were encountered in load/save settings on startup. */
    public static boolean hasSettingsLoadSaveFailures() {
        return SettingsFactory.hasLoadSaveFailure();
    }


    /** Sets whether or not a temporary directory is in use. */
    public static void setTemporaryDirectoryInUse(boolean inUse) {
        temporaryDirectoryInUse = inUse;
    }
    
    public static void resetSettingsLoadSaveFailures() {
        SettingsFactory.resetLoadSaveFailure();
    }
    
    /**
     * Returns the path of the FrostWire.jar executable.
     * For a windows binary distribution this is the same path as FrostWire.exe since those files live together.
     * @return
     */
    public static String getFrostWireJarPath() {
    	return new File(FrostWireUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
    }
    
    /**
     * Returns the root folder from which all Saved/Shared/etc..
     * folders should be placed.
     */
    public static File getFrostWireRootFolder() {
        String root = null;
        
        if (OSUtils.isWindowsVista() || OSUtils.isWindows7()) {
        	root = SystemUtils.getSpecialPath(SpecialLocations.DOWNLOADS);
        } else if(OSUtils.isWindows()) {
            root = SystemUtils.getSpecialPath(SpecialLocations.DOCUMENTS);
        }
        
        if(root == null || "".equals(root))
            root = CommonUtils.getUserHomeDir().getPath();
        
        return new File(root, "FrostWire");
    }
    
    public static Tagged<String> getArg(Map<String, String> args, String name, String action) {
        String res = args.get(name);
        if (res == null || res.equals("")) {
            //String detail = "Invalid '" + name + "' while " + action;                                                                       
            return new Tagged<String>("missing.callback.parameter", false);
        }
        String result = res;
        try {
            result = URLDecoder.decode(res);
        } catch (IOException e) {
            // no the end of the world                                                                                                        
        }
        return new Tagged<String>(result, true);
    }
    
    public static interface IndexedMapFunction<T> {
    	public void map(int i, T obj);
    }
    
	/**
	 * Make sure your List implements RandomAccess.
	 * 
	 * @param <T>
	 * @param list
	 * @param mapFunction
	 */
	public static <T> void map(List<T> list, IndexedMapFunction<T> mapFunction) {
		int size = list.size();
		for (int i = 0; i < size; i++) {
			mapFunction.map(i, list.get(i));
		}
	}
	
    public static Set<File> getFrostWire4SaveDirectories() {
        Set<File> result = new HashSet<File>();

        try {
            File settingFile = new File(CommonUtils.getFrostWire4UserSettingsDir(), "frostwire.props");
            Properties props = new Properties();
            props.load(new FileInputStream(settingFile));

            if (props.containsKey("DIRECTORY_FOR_SAVING_FILES")) {
                result.add(new File(props.getProperty("DIRECTORY_FOR_SAVING_FILES")));
            }

            String[] types = new String[] { "document", "application", "audio", "video", "image" };

            for (String type : types) {
                String key = "DIRECTORY_FOR_SAVING_" + type + "_FILES";
                if (props.containsKey(key)) {
                    result.add(new File(props.getProperty(key)));
                }
            }

        } catch (Exception e) {
            // ignore
        }

        return result;
    }
    
    public static File getUserMusicFolder() {
        File musicFile = null;
        if (OSUtils.isMacOSX()) {
            musicFile = new File(CommonUtils.getUserHomeDir(), "Music");
        } else if (OSUtils.isWindowsXP()) {
            musicFile = new File(CommonUtils.getUserHomeDir(), "My Documents" + File.separator + "My Music");
        } else if (OSUtils.isWindowsVista() || OSUtils.isWindows7()) {
            musicFile = new File(CommonUtils.getUserHomeDir(), "Music");
        } else if (OSUtils.isUbuntu()) {
            musicFile = new File(CommonUtils.getUserHomeDir(), "Music");
        } else {
            musicFile = new File(CommonUtils.getUserHomeDir(), "Music");
        }

        return musicFile;
    }
}