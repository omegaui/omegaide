/*
Jazzy - a Java library for Spell Checking
Copyright (C) 2001 Mindaugas Idzelis
Full text of license can be found in LICENSE.txt

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/
package org.fife.com.swabunga.spell.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Yet another <code>SpellDictionary</code> this one is based on Damien Guillaume's
 * Diskbased dictionary but adds a cache to try to improve a bit on performance.
 *
 * @author Robert Gustavsson
 * @version 0.01
 */

public class SpellDictionaryCachedDichoDisk extends SpellDictionaryDichoDisk {

  // Only used for testing to measure the effectiveness of the cache.
  public static int hits = 0;
  public static int codes = 0;

  public static final String JAZZY_DIR = ".jazzy";
  public static final String PRE_CACHE_FILE_EXT = ".pre";

  private static int MAX_CACHED = 10000;

  private HashMap<String, CacheObject> suggestionCache = new HashMap<>(
    MAX_CACHED
  );
  private String preCacheFileName;
  private String preCacheDir;

  /**
   * Dictionary Convenience Constructor.
   */
  public SpellDictionaryCachedDichoDisk(File wordList)
    throws FileNotFoundException, IOException {
    super(wordList);
    loadPreCache(wordList);
  }

  /**
   * Dictionary Convenience Constructor.
   */
  public SpellDictionaryCachedDichoDisk(File wordList, String encoding)
    throws FileNotFoundException, IOException {
    super(wordList, encoding);
    loadPreCache(wordList);
  }

  /**
   * Dictionary constructor that uses an aspell phonetic file to
   * build the transformation table.
   */

  public SpellDictionaryCachedDichoDisk(File wordList, File phonetic)
    throws FileNotFoundException, IOException {
    super(wordList, phonetic);
    loadPreCache(wordList);
  }

  /**
   * Dictionary constructor that uses an aspell phonetic file to
   * build the transformation table.
   */
  public SpellDictionaryCachedDichoDisk(
    File wordList,
    File phonetic,
    String encoding
  ) throws FileNotFoundException, IOException {
    super(wordList, phonetic, encoding);
    loadPreCache(wordList);
  }

  /**
   * Add a word permanently to the dictionary (and the dictionary file).
   * <i>not implemented !</i>
   */
  @Override
  public boolean addWord(String word) {
    System.err.println(
      "error: addWord is not implemented for SpellDictionaryCachedDichoDisk"
    );
    return false;
  }

  /**
   * Clears the cache.
   */
  public void clearCache() {
    suggestionCache.clear();
  }

  /**
   * Returns a list of strings (words) for the code.
   */
  @Override
  public List<String> getWords(String code) {
    List<String> list;
    codes++;
    if (suggestionCache.containsKey(code)) {
      hits++;
      list = getCachedList(code);
      return list;
    }
    list = super.getWords(code);
    addToCache(code, list);

    return list;
  }

  /**
   * This method returns the cached suggestionlist and also moves the code to
   * the top of the codeRefQueue to indicate this code has recently been
   * referenced.
   */
  private List<String> getCachedList(String code) {
    CacheObject obj = suggestionCache.get(code);
    obj.setRefTime();
    return obj.getSuggestionList();
  }

  /**
   * Adds a code and it's suggestion list to the cache.
   */
  private void addToCache(String code, List<String> l) {
    String c = null;
    String lowestCode = null;
    long lowestTime = Long.MAX_VALUE;
    Iterator<String> it;
    CacheObject obj;

    if (suggestionCache.size() >= MAX_CACHED) {
      it = suggestionCache.keySet().iterator();
      while (it.hasNext()) {
        c = it.next();
        obj = suggestionCache.get(c);
        if (obj.getRefTime() == 0) {
          lowestCode = c;
          break;
        }
        if (lowestTime > obj.getRefTime()) {
          lowestCode = c;
          lowestTime = obj.getRefTime();
        }
      }
      suggestionCache.remove(lowestCode);
    }
    suggestionCache.put(code, new CacheObject(l));
  }

  /**
   * Load the cache from file. The cache file has the same name as the
   * dico file with the .pre extension added.
   */
  @SuppressWarnings("unchecked")
  private void loadPreCache(File dicoFile) throws IOException {
    String code;
    List<String> suggestions;
    long size, time;
    File preFile;
    ObjectInputStream in;

    preCacheDir = System.getProperty("user.home") + "/" + JAZZY_DIR;
    preCacheFileName =
      preCacheDir + "/" + dicoFile.getName() + PRE_CACHE_FILE_EXT;
    //System.out.println(preCacheFileName);
    preFile = new File(preCacheFileName);
    if (!preFile.exists()) {
      System.err.println("No precache file");
      return;
    }
    //System.out.println("Precaching...");
    in = new ObjectInputStream(new FileInputStream(preFile));
    try {
      size = in.readLong();
      for (int i = 0; i < size; i++) {
        code = (String) in.readObject();
        time = in.readLong();
        suggestions = (List<String>) in.readObject();
        suggestionCache.put(code, new CacheObject(suggestions, time));
      }
    } catch (ClassNotFoundException ex) {
      System.out.println(ex.getMessage());
    }
    in.close();
  }

  /**
   * Saves the current cache to file.
   */
  public void saveCache() throws IOException {
    String code;
    CacheObject obj;
    File preFile, preDir;
    ObjectOutputStream out;
    Iterator<String> it;

    if (preCacheFileName == null || preCacheDir == null) {
      System.err.println("Precache filename has not been set.");
      return;
    }
    //System.out.println("Saving cache to precache file...");
    preDir = new File(preCacheDir);
    if (!preDir.exists()) preDir.mkdir();
    preFile = new File(preCacheFileName);
    out = new ObjectOutputStream(new FileOutputStream(preFile));
    it = suggestionCache.keySet().iterator();
    out.writeLong(suggestionCache.size());
    while (it.hasNext()) {
      code = it.next();
      obj = suggestionCache.get(code);
      out.writeObject(code);
      out.writeLong(obj.getRefTime());
      out.writeObject(obj.getSuggestionList());
    }
    out.close();
  }

  // INNER CLASSES
  // ------------------------------------------------------------------------
  private static class CacheObject implements Serializable { // robert: static

    private List<String> suggestions = null;
    private long refTime = 0;

    public CacheObject(List<String> list) {
      this.suggestions = list;
    }

    public CacheObject(List<String> list, long time) {
      this.suggestions = list;
      this.refTime = time;
    }

    public List<String> getSuggestionList() {
      return suggestions;
    }

    public void setRefTime() {
      refTime = System.currentTimeMillis();
    }

    public long getRefTime() {
      return refTime;
    }
  }
}
