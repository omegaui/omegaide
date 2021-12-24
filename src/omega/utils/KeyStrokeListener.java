/**
* The IDE 's Key Input Listener -- Currently Unstable
* Copyright (C) 2021 Omega UI

* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.

* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.

* You should have received a copy of the GNU General Public License
* along with this program.  If not, see http://www.gnu.org/licenses/.
*/

package omega.utils;

import static java.awt.event.KeyEvent.*;

import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;

public class KeyStrokeListener implements KeyListener {

  public LinkedList<Key> keys = new LinkedList<>();
  public LinkedList<KeyStrokeData> keyStrokes = new LinkedList<>();

  public KeyStrokeListener(Component c) {
    c.addFocusListener(
      new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent e) {
          keyStrokes.forEach(keyStrokeData -> keyStrokeData.autoReset());
        }
      }
    );
  }

  public KeyStrokeData putKeyStroke(
    KeyStrokeDataListener listener,
    int... key
  ) {
    var stroke = new KeyStrokeData(listener, key);
    keyStrokes.add(stroke);
    return stroke;
  }

  public boolean offerKey(Key key) {
    for (Key kx : keys) {
      if (kx.key == key.key) return false;
    }
    this.keys.add(key);
    return true;
  }

  public Key huntKey(int key) {
    for (Key kx : keys) {
      if (kx.key == key) return kx;
    }
    return new Key(key);
  }

  @Override
  public void keyTyped(KeyEvent e) {}

  @Override
  public void keyPressed(KeyEvent e) {
    keys.forEach(key -> key.checkPressed(e.getKeyCode(), true));
    keyStrokes.forEach(keyStrokeData -> keyStrokeData.stroke(e));
  }

  @Override
  public void keyReleased(KeyEvent e) {
    keys.forEach(key -> key.checkPressed(e.getKeyCode(), false));
  }

  public interface KeyStrokeDataListener {
    void listen(KeyEvent e);
  }

  public class KeyStrokeData {

    public LinkedList<Key> keys = new LinkedList<>();
    public LinkedList<Key> stopKeys = new LinkedList<>();
    public KeyStrokeDataListener listener;
    public volatile boolean useAutoReset = false;

    public KeyStrokeData(KeyStrokeDataListener listener, int... keys) {
      this.listener = listener;
      for (int key : keys) {
        this.keys.add(huntKey(key));
      }
    }

    public KeyStrokeData setStopKeys(int... keys) {
      for (int key : keys) this.stopKeys.add(huntKey(key));
      return this;
    }

    public KeyStrokeData useAutoReset() {
      this.useAutoReset = true;
      return this;
    }

    public synchronized void stroke(KeyEvent e) {
      if (isStrokable()) {
        listener.listen(e);
        if (useAutoReset) autoReset();
      }
    }

    public void autoReset() {
      for (Key kx : this.keys) {
        huntKey(kx.key).setPressed(false);
      }
    }

    public boolean containsStrokeKey(Key key) {
      for (Key kx : this.keys) {
        if (kx.key == key.key) return true;
      }
      return false;
    }

    public boolean containsStopKey(Key key) {
      for (Key kx : this.stopKeys) {
        if (kx.key == key.key) return true;
      }
      return false;
    }

    public boolean isStrokable() {
      int strokeKeysLength = 0;
      int stopKeysLength = 0;
      for (Key kx : KeyStrokeListener.this.keys) {
        if (kx.isPressed()) {
          if (containsStrokeKey(kx)) strokeKeysLength++; else if (
            containsStopKey(kx)
          ) stopKeysLength++;
        }
      }
      return (strokeKeysLength == this.keys.size()) && stopKeysLength == 0;
    }
  }

  public class Key {

    public int key;
    public volatile boolean pressed = false;

    public Key(int key) {
      this.key = key;
      offerKey(this);
    }

    public void checkPressed(int key, boolean pressed) {
      if (this.key == key) {
        setPressed(pressed);
      }
    }

    public void setPressed(boolean pressed) {
      this.pressed = pressed;
    }

    public boolean isPressed() {
      return pressed;
    }

    @Override
    public String toString() {
      return KeyEvent.getKeyText(key);
    }
  }
}
