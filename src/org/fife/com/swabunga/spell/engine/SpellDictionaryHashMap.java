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
/*
 * put your module comment here
 * formatted with JxBeauty (c) johann.langhofer@nextra.at
 */

package org.fife.com.swabunga.spell.engine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

/**
 * The SpellDictionaryHashMap holds the dictionary
 * <p/>
 * There are many open source dictionary files. For just a few see:
 * http://wordlist.sourceforge.net/
 * <p/>
 * This dictionary class reads words one per line. Make sure that your word list
 * is formatted in this way (most are).
 * <p/>
 * Note that you must create the dictionary with a word list for the added
 * words to persist.
 */
// robert: Converted use of Vectors to ArrayLists; we're single-threaded
public class SpellDictionaryHashMap extends SpellDictionaryASpell {

  /** A field indicating the initial hash map capacity (16KB) for the main
   *  dictionary hash map. Interested to see what the performance of a
   *  smaller initial capacity is like.
   */
  private static final int INITIAL_CAPACITY = 16 * 1024;

  /**
   * The hashmap that contains the word dictionary. The map is hashed on the doublemeta
   * code. The map entry contains a LinkedList of words that have the same double meta code.
   */
  private HashMap<String, List<String>> mainDictionary = new HashMap<>(
    INITIAL_CAPACITY
  );

  /** Holds the dictionary file for appending*/
  private File dictFile = null;

  /**
   * Dictionary Constructor.
   * @throws java.io.IOException indicates a problem with the file system
   */
  public SpellDictionaryHashMap() throws IOException {
    super((File) null);
  }

  /**
   * Dictionary Constructor.
   * @param wordList The file containing the words list for the dictionary
   * @throws java.io.IOException indicates problems reading the words list
   * file
   */
  public SpellDictionaryHashMap(Reader wordList) throws IOException {
    super((File) null);
    createDictionary(new BufferedReader(wordList));
  }

  /**
   * Dictionary convenience Constructor.
   * @param wordList The file containing the words list for the dictionary
   * @throws java.io.FileNotFoundException indicates problems locating the
   * words list file on the system
   * @throws java.io.IOException indicates problems reading the words list
   * file
   */
  public SpellDictionaryHashMap(File wordList)
    throws FileNotFoundException, IOException {
    this(new FileReader(wordList));
    dictFile = wordList;
  }

  /**
   * Dictionary constructor that uses an aspell phonetic file to
   * build the transformation table.
   * @param wordList The file containing the words list for the dictionary
   * @param phonetic The file to use for phonetic transformation of the
   * wordlist.
   * @throws java.io.FileNotFoundException indicates problems locating the
   * file on the system
   * @throws java.io.IOException indicates problems reading the words list
   * file
   */
  public SpellDictionaryHashMap(File wordList, File phonetic)
    throws FileNotFoundException, IOException {
    super(phonetic);
    dictFile = wordList;
    createDictionary(new BufferedReader(new FileReader(wordList)));
  }

  /**
   * Dictionary constructor that uses an aspell phonetic file to
   * build the transformation table. Encoding is used for phonetic file only;
   * default encoding is used for wordList
   * @param wordList The file containing the words list for the dictionary
   * @param phonetic The file to use for phonetic transformation of the
   * wordlist.
   * @param phoneticEncoding Uses the character set encoding specified
   * @throws java.io.FileNotFoundException indicates problems locating the
   * file on the system
   * @throws java.io.IOException indicates problems reading the words list
   * or phonetic information
   */
  public SpellDictionaryHashMap(
    File wordList,
    File phonetic,
    String phoneticEncoding
  ) throws FileNotFoundException, IOException {
    super(phonetic, phoneticEncoding);
    dictFile = wordList;
    createDictionary(new BufferedReader(new FileReader(wordList)));
  }

  /**
   * Dictionary constructor that uses an aspell phonetic file to
   * build the transformation table.
   * @param wordList The file containing the words list for the dictionary
   * @param phonetic The reader to use for phonetic transformation of the
   * wordlist.
   * @throws java.io.IOException indicates problems reading the words list
   * or phonetic information
   */
  public SpellDictionaryHashMap(Reader wordList, Reader phonetic)
    throws IOException {
    super(phonetic);
    dictFile = null;
    createDictionary(new BufferedReader(wordList));
  }

  /**
   * Add words from a file to existing dictionary hashmap.
   * This function can be called as many times as needed to
   * build the internal word list. Duplicates are not added.
   * <p>
   * Note that adding a dictionary does not affect the target
   * dictionary file for the addWord method. That is, addWord() continues
   * to make additions to the dictionary file specified in createDictionary()
   * <P>
   * @param wordList a File object that contains the words, on word per line.
   * @throws FileNotFoundException
   * @throws IOException
   */
  public void addDictionary(File wordList)
    throws FileNotFoundException, IOException {
    addDictionaryHelper(new BufferedReader(new FileReader(wordList)));
  }

  /**
   * Add words from a Reader to existing dictionary hashmap.
   * This function can be called as many times as needed to
   * build the internal word list. Duplicates are not added.
   * <p>
   * Note that adding a dictionary does not affect the target
   * dictionary file for the addWord method. That is, addWord() continues
   * to make additions to the dictionary file specified in createDictionary()
   * <P>
   * @param wordList a Reader object that contains the words, on word per line.
   * @throws IOException
   */
  public void addDictionary(Reader wordList) throws IOException {
    addDictionaryHelper(new BufferedReader(wordList));
  }

  /**
   * Add a word permanently to the dictionary (and the dictionary file).
   * <p>This needs to be made thread safe (synchronized)</p>
   */
  @Override
  public boolean addWord(String word) {
    putWord(word);
    if (dictFile != null) {
      try {
        // Append new word to user's word file
        BufferedWriter w = new BufferedWriter(new FileWriter(dictFile, true));
        w.write(word);
        w.write("\n");
        w.close();
        return true;
      } catch (IOException ex) {
        System.out.println("Error writing to dictionary file");
        ex.printStackTrace();
      }
    }
    // Only return true if added to dictionary file.
    return false;
  }

  /**
   * Constructs the dictionary from a word list file.
   * <p>
   * Each word in the reader should be on a separate line.
   * <p>
   * This is a very slow function. On my machine it takes quite a while to
   * load the data in. I suspect that we could speed this up quite allot.
   */
  protected void createDictionary(BufferedReader in) throws IOException {
    // robert: Optimized ever-so-slightly
    String line;
    while ((line = in.readLine()) != null) {
      if (line.length() > 0) {
        putWord(line);
      }
    }
  }

  /**
   * Adds to the existing dictionary from a word list file. If the word
   * already exists in the dictionary, a new entry is not added.
   * <p>
   * Each word in the reader should be on a separate line.
   * <p>
   * Note: for whatever reason that I haven't yet looked into, the phonetic codes
   * for a particular word map to a vector of words rather than a hash table.
   * This is a drag since in order to check for duplicates you have to iterate
   * through all the words that use the phonetic code.
   * If the vector-based implementation is important, it may be better
   * to subclass for the cases where duplicates are bad.
   */
  protected void addDictionaryHelper(BufferedReader in) throws IOException {
    // robert: Optimized ever-so-slightly
    String line;
    while ((line = in.readLine()) != null) {
      if (line.length() > 0) {
        putWordUnique(line);
      }
    }
  }

  /**
   * Allocates a word in the dictionary
   * @param word The word to add
   */
  protected void putWord(String word) {
    String code = getCode(word);
    List<String> list = mainDictionary.get(code);
    if (list == null) {
      list = new ArrayList<>();
      mainDictionary.put(code, list);
    }
    list.add(word);
  }

  /**
   * Allocates a word, if it is not already present in the dictionary. A word
   * with a different case is considered the same.
   * @param word The word to add
   */
  protected void putWordUnique(String word) {
    String code = getCode(word);
    List<String> list = mainDictionary.get(code);

    if (list != null) {
      boolean isAlready = false;

      for (String s : list) {
        if (word.equalsIgnoreCase(s)) {
          isAlready = true;
          break;
        }
      }

      if (!isAlready) list.add(word);
    } else {
      list = new ArrayList<>();
      list.add(word);
      mainDictionary.put(code, list);
    }
  }

  /**
   * Returns a list of strings (words) for the code.
   */
  @Override
  public List<String> getWords(String code) {
    //Check the main dictionary.
    List<String> mainDictResult = mainDictionary.get(code);
    if (mainDictResult == null) return Collections.emptyList();
    return mainDictResult;
  }

  /**
   * Returns true if the word is correctly spelled against the current word list.
   */
  @Override
  public boolean isCorrect(String word) {
    List<String> possible = getWords(getCode(word));
    if (possible.contains(word)) return true;
    //JMH should we always try the lowercase version. If I dont then capitalized
    //words are always returned as incorrect.
    else if (possible.contains(word.toLowerCase())) return true;
    return false;
  }
}
