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

import java.util.LinkedList;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import static java.awt.event.KeyEvent.*;
public class KeyStrokeListener implements KeyListener{
	
	public LinkedList<KeyStrokeData> keyStrokes;
	public LinkedList<Key> shortcutKeys;
	
	public KeyStrokeListener(){
		keyStrokes = new LinkedList<>();
		shortcutKeys = new LinkedList<>();
		
		shortcutKeys.add(new Key(VK_CONTROL));
		shortcutKeys.add(new Key(VK_SHIFT));
		shortcutKeys.add(new Key(VK_ALT));
		shortcutKeys.add(new Key(VK_WINDOWS));
	}
	
	public KeyStrokeData putKeyStroke(Runnable pressAction, int... keys){
		keyStrokes.add(new KeyStrokeData(pressAction, keys));
		return keyStrokes.getLast();
	}
	
	public KeyStrokeData putShortcutKeyStroke(Runnable pressAction, int... keys){
		return putKeyStroke(pressAction, keys).setBeShortcutAware(true);
	}
	
	public KeyStrokeData putAutoResetShortcutKeyStroke(Runnable pressAction, int... keys){
		return putKeyStroke(pressAction, keys).setBeShortcutAware(true).setResetOnActionTrigger(true);
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		shortcutKeys.forEach(key->key.checkPressed(e.getKeyCode(), true));
		keyStrokes.forEach(keyStrokeData->keyStrokeData.toggle(e, e.getKeyCode(), true));
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		shortcutKeys.forEach(key->key.checkPressed(e.getKeyCode(), false));
		keyStrokes.forEach(keyStrokeData->keyStrokeData.toggle(e, e.getKeyCode(), false));
	}
	
	public class Key {
		public int key;
		public volatile boolean pressed = false;
		
		public Key(int key){
			this.key = key;
		}
		
		public void checkPressed(int key, boolean pressed){
			if(this.key == key)
				setPressed(pressed);
		}
		
		public void setPressed(boolean pressed){
			this.pressed = pressed;
		}
		
		public boolean isPressed(){
			return pressed;
		}
	}
	
	public class KeyStrokeData {
		
		public LinkedList<Key> keys = new LinkedList<>();
		public LinkedList<Key> stopKeys = new LinkedList<>();
		
		public Runnable pressAction = ()->{};
		
		public volatile boolean beShortcutAware = false;
		public volatile boolean resetOnActionTrigger = false;
		
		public KeyStrokeData(Runnable pressAction, int... keys){
			if(keys != null && keys.length > 0){
				for(int key : keys)
					this.keys.add(new Key(key));
			}
			
			this.pressAction = pressAction;
		}
		
		public KeyStrokeData setStopKeys(int... keys){
			for(int kx : keys){
				stopKeys.add(new Key(kx));
			}
			return this;
		}
		
		public KeyStrokeData setBeShortcutAware(boolean value){
			this.beShortcutAware = value;
			return this;
		}
		
		public KeyStrokeData setResetOnActionTrigger(boolean value){
			this.resetOnActionTrigger = value;
			return this;
		}
		
		public void toggle(KeyEvent e, int keyCode, boolean isPressed){
			for(Key kx : stopKeys){
				if(kx.key == keyCode)
					kx.setPressed(isPressed);
			}
			if(beShortcutAware){
				for(Key kx : shortcutKeys){
					if(!containsKey(kx.key) && kx.isPressed())
						return;
				}
			}
			for(Key kx : keys){
				if(kx.key == keyCode)
					kx.setPressed(isPressed);
			}
			if(isStrokable()){
				System.out.println("Running ...");
				e.consume();
				pressAction.run();
				if(resetOnActionTrigger)
					resetStrokes();
			}
		}
		
		public void resetStrokes(){
			for(Key kx : keys)
				kx.setPressed(false);
		}
		
		public boolean containsKey(int key){
			for(Key kx : keys){
				if(kx.key == key)
					return true;
			}
			return false;
		}
		
		public boolean isStrokable(){
			for(Key kx : keys){
				if(!kx.isPressed())
					return false;
			}
			for(Key kx : stopKeys){
				if(kx.isPressed())
					return false;
			}
			return true;
		}
		
	}
}
