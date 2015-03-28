package br.com.vluzrmos.sharedtexteditor.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SharedFilesHelper {
    private Context context ;
    protected static final String SET_FILENAMES_KEY = "files";
    protected static final String SHARED_PREFS_FILENAME = "SharedFilesHelper";

    public static final String FILENAME_KEY = "filename";

    public static final String CONTENT_KEY = "content";

    protected SharedPreferences prefs;
    protected SharedPreferences.Editor prefsEditor;

    protected String[] filenames;

    /**
     * Shared files helper
     * @param context Context of shared files
     */
    public SharedFilesHelper(Context context){
        this.context = context;
    }

    /**
     * Get the shared prefs for context
     * @return
     */
    public SharedPreferences getPrefs(){
        if(prefs==null){
            prefs = context.getSharedPreferences(SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);
        }

        return prefs;
    }

    /**
     * get shared prefs editor for context
     * @return
     */
    public SharedPreferences.Editor getPrefsEditor(){
        if(prefsEditor==null){
            prefsEditor = getPrefs().edit();
        }

        return prefsEditor;
    }

    /**
     * Load filenames on shared prefs
     * @return
     */
    private String[] loadFilenames(){
        Set<String> stringSet = getPrefs().getStringSet(SET_FILENAMES_KEY, new HashSet<String>());

        String[] strings = stringSet.toArray(new String[stringSet.size()]);

        Arrays.sort(strings);

        return strings;
    }

    /**
     * Create a new shared file
     * @param name
     * @param contents
     */
    public void addSharedFile(String name, String contents){
        String slug = Slug.makeSlug(name);

        addFilename(slug);

        SharedPreferences newPrefs = getSharedFile(slug);
        SharedPreferences.Editor newEditor = newPrefs.edit();

        newEditor.putString(FILENAME_KEY, name);
        newEditor.putString(CONTENT_KEY, contents);

        newEditor.commit();
    }

    /**
     * Verify if a shared file exists
     * @param name
     * @return
     */
    public boolean sharedFileExists(String name){
        File file = new File(getSharedFilePath(name));

        return file.exists();
    }

    /**
     * Get path to shard filename
     * @param filename
     * @return
     */
    private String getSharedFilePath(String filename){
        String slug = Slug.makeSlug(filename);
        String path = "/data/data/%s/shared_prefs/%s.xml";

        return String.format(path, context.getPackageName(), slug);
    }

    /**
     * Check filename
     * @param text
     * @return
     */
    public static boolean isValidName(String text)
    {
        Pattern pattern = Pattern.compile("^(?!(?:CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9])(?:\\.[^.]*)?$)[^<>:\"/\\\\|?*\\x00-\\x1F]*[^<>:\"/\\\\|?*\\x00-\\x1F\\.]$",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.COMMENTS);
        Matcher matcher = pattern.matcher(text);
        boolean isMatch = matcher.matches();
        return isMatch;
    }

    /**
     * Add a filename to sharedprefs
     * @param name
     */
    private void addFilename(String name){
        String[] filenames = getFilenames();
        int size = filenames.length+1;

        String[] newFilenames = Arrays.copyOf(filenames, size);

        newFilenames[size-1] = name;

        setFilenames(newFilenames);
    }

    /**
     * Remove a filename of a sharedfiles
     * @param position
     */
    public void removeSharedFile(int position){
        String[] strings = new String[filenames.length-1];

        for(int i = 0, j=0; i < filenames.length; i++){
            if(i!=position){
                strings[j++] = filenames[i];
            }
        }

        File file = new File("/data/data/"+context.getPackageName()+"/shared_prefs/"+filenames[position]+".xml");

        file.delete();

        setFilenames(strings);
    }

    /**
     * Set a new list of filenames
     * @param filenames
     */
    public void setFilenames(String [] filenames){
        Arrays.sort(filenames);

        Set<String> newSet = new HashSet<>(Arrays.asList(filenames));

        getPrefsEditor().putStringSet(SET_FILENAMES_KEY, newSet);

        getPrefsEditor().commit();

        this.filenames = filenames;
    }

    public String[] getFilenames(){
        if(filenames==null){
            filenames = loadFilenames();
        }

        return filenames;
    }

    public SharedPreferences getSharedFile(String name){
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public SharedPreferences getSharedFile(int position){
        return getSharedFile(getFilenames()[position]);
    }
}
