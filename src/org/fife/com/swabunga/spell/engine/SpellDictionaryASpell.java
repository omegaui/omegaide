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
/* Created by bgalbs on Jan 30, 2003 at 11:45:25 PM */
package org.fife.com.swabunga.spell.engine;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.security.InvalidParameterException;
import java.util.*;

/**
 * Container for various methods that any <code>SpellDictionary</code> will use.
 * This class is based on the original Jazzy aspell port.
 * <p/>
 * Derived classes will need words list files as spell checking reference.
 * Words list file is a dictionary with one word per line. There are many
 * open source dictionary files, see:
 * <a href="http://wordlist.sourceforge.net/">
 * http://wordlist.sourceforge.net/</a>
 * <p/>
 * You can choose words lists form <a href="http://aspell.net/">aspell</a>
 * many differents languages dictionaries. To grab some, install
 * <code>aspell</code> and the dictionaries you require. Then run aspell
 * specifying the name of the dictionary and the words list file to dump it
 * into, for example:
 * <pre>
 * aspell --master=fr-40 dump master &gt; fr-40.txt
 * </pre>
 * Note: the number following the language is the size indicator. A bigger
 * number gives a more extensive language coverage. Size 40 is more than
 * adequate for many usages.
 * <p/>
 * For some languages, Aspell can also supply you with the phonetic file.
 * On Windows, go into aspell <code>data</code> directory and copy the
 * phonetic file corresponding to your language, for example the
 * <code>fr_phonet.dat</code> for the <code>fr</code> language. The phonetic
 * file should be in directory <code>/usr/share/aspell</code> on Unix.
 *
 * @see GenericTransformator GenericTransformator for information on
 * phonetic files.
 */
public abstract class SpellDictionaryASpell implements SpellDictionary {

  /** The reference to a Transformator, used to transform a word into it's phonetic code. */
  private Transformator tf;

  /**
   * Constructs a new SpellDictionaryASpell
   * @param phonetic The file to use for phonetic transformation of the
   * words list. If <code>phonetic</code> is null, the the transformation
   * uses {@link DoubleMeta} transformation.
   * @throws java.io.IOException  indicates problems reading the phonetic
   * information
   */
  public SpellDictionaryASpell(File phonetic) throws IOException {
    if (phonetic == null) tf = new DoubleMeta(); else tf =
      new GenericTransformator(phonetic);
  }

  /**
   * Constructs a new SpellDictionaryASpell
   * @param phonetic The file to use for phonetic transformation of the
   * words list. If <code>phonetic</code> is null, the the transformation
   * uses {@link DoubleMeta} transformation.
   * @param encoding Uses the character set encoding specified
   * @throws java.io.IOException  indicates problems reading the phonetic
   * information
   */
  public SpellDictionaryASpell(File phonetic, String encoding)
    throws IOException {
    if (phonetic == null) tf = new DoubleMeta(); else tf =
      new GenericTransformator(phonetic, encoding);
  }

  /**
   * Constructs a new SpellDictionaryASpell
   * @param phonetic The Reader to use for phonetic transformation of the
   * words list. If <code>phonetic</code> is null, the the transformation
   * uses {@link DoubleMeta} transformation.
   * @throws java.io.IOException  indicates problems reading the phonetic
   * information
   */
  public SpellDictionaryASpell(Reader phonetic) throws IOException {
    if (phonetic == null) tf = new DoubleMeta(); else tf =
      new GenericTransformator(phonetic);
  }

  /**
   * Returns a list of Word objects that are the suggestions to an
   * incorrect word.
   * <p>
   * This method is only needed to provide backward compatibility.
   * @see #getSuggestions(String, int, int[][])
   * @param word Suggestions for given misspelt word
   * @param threshold The lower boundary of similarity to misspelt word
   * @return a List of suggestions
   */
  @Override
  public List<Word> getSuggestions(String word, int threshold) {
    return getSuggestions(word, threshold, null);
  }

  /**
   * Returns a list of Word objects that are the suggestions to an
   * incorrect word.
   * <p>
   * @param word Suggestions for given misspelt word
   * @param threshold The lower boundary of similarity to misspelt word
   * @param matrix Two dimensional int array used to calculate
   * edit distance. Allocating this memory outside of the function will greatly improve efficiency.
   * @return a List of suggestions
   */
  @Override
  public List<Word> getSuggestions(String word, int threshold, int[][] matrix) {
    int i;
    int j;

    if (matrix == null) matrix = new int[0][0];

    HashMap<String, String> nearmisscodes = new HashMap<>();
    String code = getCode(word);

    // add all words that have the same phonetics
    nearmisscodes.put(code, code);
    List<Word> phoneticList = getWordsFromCode(word, nearmisscodes);

    // do some transformations to pick up more results
    //interchange
    nearmisscodes = new HashMap<>();
    char[] charArray = word.toCharArray();
    char a;
    char b;

    for (i = 0; i < word.length() - 1; i++) {
      a = charArray[i];
      b = charArray[i + 1];
      charArray[i] = b;
      charArray[i + 1] = a;
      String s = getCode(new String(charArray));
      nearmisscodes.put(s, s);
      charArray[i] = a;
      charArray[i + 1] = b;
    }

    char[] replacelist = tf.getReplaceList();

    //change
    charArray = word.toCharArray();
    char original;
    for (i = 0; i < word.length(); i++) {
      original = charArray[i];
      for (j = 0; j < replacelist.length; j++) {
        charArray[i] = replacelist[j];
        String s = getCode(new String(charArray));
        nearmisscodes.put(s, s);
      }
      charArray[i] = original;
    }

    //add
    charArray = (word += " ").toCharArray();
    int iy = charArray.length - 1;
    while (true) {
      for (j = 0; j < replacelist.length; j++) {
        charArray[iy] = replacelist[j];
        String s = getCode(new String(charArray));
        nearmisscodes.put(s, s);
      }
      if (iy == 0) break;
      charArray[iy] = charArray[iy - 1];
      --iy;
    }

    //delete
    word = word.trim();
    charArray = word.toCharArray();
    char[] charArray2 = new char[charArray.length - 1];
    System.arraycopy(charArray, 0, charArray2, 0, charArray2.length);

    a = charArray[charArray.length - 1];
    int ii = charArray2.length;
    while (true) {
      String s = getCode(new String(charArray));
      nearmisscodes.put(s, s);
      if (ii == 0) break;
      b = a;
      a = charArray2[ii - 1];
      charArray2[ii - 1] = b;
      --ii;
    }

    nearmisscodes.remove(code); //already accounted for in phoneticList

    List<Word> wordlist = getWordsFromCode(word, nearmisscodes);

    if (wordlist.size() == 0 && phoneticList.size() == 0) addBestGuess(
      word,
      phoneticList,
      matrix
    );

    // We sort a Vector at the end instead of maintaining a
    // continuously sorted TreeSet because every time you add a collection
    // to a tree set it has to be resorted. It's better to do this operation
    // once at the end.

    phoneticList.sort(new Word()); //always sort phonetic matches along the top
    wordlist.sort(new Word()); //the non-phonetic matches can be listed below

    phoneticList.addAll(wordlist);
    return phoneticList;
  }

  /**
   * When we don't come up with any suggestions (probably because the threshold was too strict),
   * then pick the best guesses from the those words that have the same phonetic code.
   * @param word - the word we are trying spell correct
   * @param wordList Two dimensional array of int used to calculate
   * edit distance. Allocating this memory outside of the function will greatly improve efficiency.
   * @param wordList - the linked list that will get the best guess
   */
  private void addBestGuess(String word, List<Word> wordList, int[][] matrix) {
    if (matrix == null) matrix = new int[0][0];

    if (wordList.size() != 0) throw new InvalidParameterException(
      "the wordList vector must be empty"
    );

    int bestScore = Integer.MAX_VALUE;

    String code = getCode(word);
    List<String> simwordlist = getWords(code);

    LinkedList<Word> candidates = new LinkedList<>();

    for (String similar : simwordlist) {
      int distance = EditDistance.getDistance(word, similar, matrix);
      if (distance <= bestScore) {
        bestScore = distance;
        candidates.add(new Word(similar, distance));
      }
    }

    //now, only pull out the guesses that had the best score
    for (Word candidate : candidates) {
      if (candidate.getCost() == bestScore) wordList.add(candidate);
    }
  }

  private List<Word> getWordsFromCode(
    String word,
    HashMap<String, String> codes
  ) {
    Configuration config = Configuration.getConfiguration();
    List<Word> result = new ArrayList<>();
    int[][] matrix = new int[0][0];
    final int configDistance = config.getInteger(Configuration.SPELL_THRESHOLD);

    for (String code : codes.keySet()) {
      List<String> simwordlist = getWords(code);
      for (String similar : simwordlist) {
        int distance = EditDistance.getDistance(word, similar, matrix);
        if (distance < configDistance) {
          result.add(new Word(similar, distance));
        }
      }
    }
    return result;
  }

  /**
   * Returns the phonetic code representing the word.
   * @param word The word we want the phonetic code.
   * @return The value of the phonetic code for the word.
   */
  public String getCode(String word) {
    return tf.transform(word);
  }

  /**
   * Returns a list of words that have the same phonetic code.
   * @param phoneticCode The phonetic code common to the list of words
   * @return A list of words having the same phonetic code
   */
  protected abstract List<String> getWords(String phoneticCode);

  /**
   * Returns true if the word is correctly spelled against the current word list.
   */
  @Override
  public boolean isCorrect(String word) {
    List<String> possible = getWords(getCode(word));
    if (possible.contains(word)) return true;
    //JMH should we always try the lowercase version. If I don't then
    // capitalized words are always returned as incorrect.
    else if (possible.contains(word.toLowerCase())) return true;
    return false;
  }
}
