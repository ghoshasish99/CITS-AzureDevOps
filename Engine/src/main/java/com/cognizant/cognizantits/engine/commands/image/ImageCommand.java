/*
 * Copyright 2014 - 2017 Cognizant Technology Solutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognizant.cognizantits.engine.commands.image;

import com.cognizant.cognizantits.datalib.or.common.ObjectGroup;
import com.cognizant.cognizantits.datalib.or.image.ImageORObject;
import com.cognizant.cognizantits.engine.commands.Command;
import com.cognizant.cognizantits.engine.core.CommandControl;
import com.cognizant.cognizantits.engine.execution.exception.UnCaughtException;
import com.cognizant.cognizantits.engine.support.Flag;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.sikuli.basics.Settings;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Key;
import org.sikuli.script.KeyModifier;
import org.sikuli.script.Match;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

public class ImageCommand extends Command {

    Robot robot;

    static final Screen SCREEN = new Screen();
    static int smartIndex = 0;
    int index = 0;
    Object target, droptarget;
    File tmp;
    Region r;
    List<Match> res;
    Flag iflag;

    public ImageCommand(CommandControl cc) {
        super(cc);
        try {
            robot = new Robot();
        } catch (AWTException ex) {
            Logger.getLogger(ImageCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
        Settings.ActionLogs = true;
        Settings.InfoLogs = true;
        Settings.MoveMouseDelay = 0.1f;
        Settings.SlowMotionDelay = 0.1f;
        Settings.DebugLogs = true;
        Settings.OcrTextRead = true;
        Settings.OcrTextSearch = true;
    }

    Object getPattern(ImageORObject obj, Flag... flag) throws UnCaughtException {
        String location = obj.getRepLocation() + File.separator + obj.getImageLocation();
        tmp = new File(location);
        iflag = Flag.IMAGE_AND_TEXT;
        if (flag.length > 0) {
            iflag = flag[0];
        }
        boolean validfile = tmp.exists() && tmp.isFile();

        if (iflag == Flag.TEXT_ONLY) {
            if (!"".equals(obj.getText())) {
                return obj.getText();
            } else {
                throw new UnCaughtException("Empty Text is Given",
                        "The Object '" + obj.getName() + "' contains Empty Text!!!");
            }
        } else if (iflag == Flag.IMAGE_ONLY) {
            if (validfile) {
                return new Pattern(location).targetOffset(obj.getOffset().x,
                        obj.getOffset().y);
            } else {
                throw new UnCaughtException("File Not Found", location
                        + " is Missing!!!");
            }
        } else if (validfile) {
            return new Pattern(location).targetOffset(obj.getOffset().x,
                    obj.getOffset().y);
        } else if (!"".equals(obj.getText())) {
            return obj.getText();
        } else {
            throw new UnCaughtException("Empty Text is Given",
                    "The Object '" + obj.getName() + "' contains Empty Text!!!");
        }
    }

    public int getKeyModifier() {
        if (Data != null) {
            switch (Data.toUpperCase()) {
                case "SHIFT":
                    return KeyModifier.SHIFT;
                case "CTRL":
                    return KeyModifier.CTRL;
                case "ALT":
                    return KeyModifier.ALT;
                case "START":
                    return KeyModifier.WIN;
                default:
                    return 0;
            }
        } else {
            return 0;
        }
    }

    public String getKeyCode(String data) {
        switch (data.toUpperCase()) {
            case "TAB":
                return Key.TAB;
            case "ENTER":
                return Key.ENTER;
            case "SHIFT":
                return Key.SHIFT;
            case "CTRL":
                return Key.CTRL;
            case "ALT":
                return Key.ALT;
            case "START":
                return Key.WIN;
            case "ESC":
                return Key.ESC;
            case "DELETE":
                return Key.DELETE;
            case "BACKSPACE":
                return Key.BACKSPACE;
            case "HOME":
                return Key.HOME;
            case "CAPS_LOCK":
            case "CAPS LOCK":
                return Key.CAPS_LOCK;
            case "PAGE_UP":
            case "PAGEUP":
                return Key.PAGE_UP;
            case "PAGE_DOWN":
            case "PAGEDOWN":
                return Key.PAGE_DOWN;
            case "UP":
                return Key.UP;
            case "DOWN":
                return Key.DOWN;
            case "LEFT":
                return Key.LEFT;
            case "RIGHT":
                return Key.RIGHT;
            default:
                return data;

        }

    }

    public int getKeyEvent(String key) {
        switch (key.toUpperCase()) {
            case "TAB":
                return KeyEvent.VK_TAB;
            case "ENTER":
                return KeyEvent.VK_ENTER;
            case "SHIFT":
                return KeyEvent.VK_SHIFT;
            case "CTRL":
                return KeyEvent.VK_CONTROL;
            case "ALT":
                return KeyEvent.VK_ALT;
            case "START":
                return KeyEvent.VK_WINDOWS;
            case "DELETE":
                return KeyEvent.VK_DELETE;
            case "BACKSPACE":
                return KeyEvent.VK_BACK_SPACE;
            case "HOME":
                return KeyEvent.VK_HOME;
            case "PAGE_UP":
            case "PAGEUP":
                return KeyEvent.VK_PAGE_UP;
            case "PAGE_DOWN":
            case "PAGEDOWN":
                return KeyEvent.VK_PAGE_DOWN;
            case "UP":
                return KeyEvent.VK_UP;
            case "DOWN":
                return KeyEvent.VK_DOWN;
            case "LEFT":
                return KeyEvent.VK_LEFT;
            case "RIGHT":
                return KeyEvent.VK_RIGHT;
            default:
                return KeyEvent.VK_ESCAPE;
        }
    }

    public void shortcutKeys(ArrayList<String> keys) {
        try {
            int s = keys.size();
            String end = keys.get(s - 1).toLowerCase();
            keys.remove(end);
            pressKeys(keys);
            SCREEN.type(getKeyCode(end));
            releaseKeys(keys);
        } catch (Exception ex) {
            Logger.getLogger(ImageCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void pressKeys(ArrayList<String> keys) {
        keys.forEach((key) -> {
            robot.keyPress(getKeyEvent(key));
        });
    }

    public void releaseKeys(List<String> keys) {
        keys.forEach((key) -> {
            robot.keyRelease(getKeyEvent(key));
        });
    }

    public double parseToDouble(String data, double val) {
        if (data != null && !data.equals("")) {
            try {
                val = Double.valueOf(data);
            } catch (Exception ex) {
                Logger.getLogger(ImageCommand.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return val;
    }

    public int parseToInt(String data, int val) {
        if (data != null && !data.equals("")) {
            try {
                val = Integer.valueOf(data);
            } catch (Exception ex) {
                Logger.getLogger(ImageCommand.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return val;
    }

    @SuppressWarnings("unchecked")
    Object findTarget(ObjectGroup<ImageORObject> imageObjectGroup, Flag offsetFlag,
            Flag coordinateFlag, Flag... searchModeFlag) throws UnCaughtException {
        if (imageObjectGroup == null || imageObjectGroup.getObjects().isEmpty()) {
            r = null;
            return r;
        }
        Object cTarget;
        int currIndex = -1, dx = 0;
        if (smartIndex > 0 && smartIndex < imageObjectGroup.getObjects().size()) {
            imageObjectGroup.getObjects().add(0, imageObjectGroup.getObjects().remove(smartIndex));
            dx = 1;
        }

        for (ImageORObject obj : imageObjectGroup.getObjects()) {
            try {
                currIndex++;
                cTarget = getPattern(obj, searchModeFlag);
                setSettings(obj);
                res = (List<Match>) getList(SCREEN.findAll(cTarget));
                if (res != null) {
                    r = getRegion();
                    if (offsetFlag == Flag.SET_OFFSET) {
                        r.x += obj.getOffset().getX();
                        r.y += obj.getOffset().getY();
                    }
                    if (currIndex > 0) {
                        smartIndex = currIndex - dx;
                    }
                    return r;
                }
            } catch (FindFailed | UnCaughtException ex) {
                Logger.getLogger(ImageCommand.class.getName())
                        .log(Level.WARNING, ex.getMessage(), ex);
            } catch (Exception ex) {
                throw new UnCaughtException(ex.getMessage());
            }
        }

        if (coordinateFlag == Flag.SET_COORDINATES) {
            r = Region.create(imageObjectGroup.getObjects().get(0).getCoordinates());
        } else {
            r = null;
        }
        return r;
    }

    Region getRegion() {
        if (res.size() < index) {
            index = res.size() - 1;
        }
        return res.get(index);
    }

    Region getRegion(Region r, String where, Integer... size) {

        switch (where.toUpperCase()) {
            case "RIGHT":
                return r.right(size.length == 1 ? size[0] : r.w);
            case "LEFT":
                return r.left(size.length == 1 ? size[0] : r.w);
            case "ABOVE":
                return r.above(size.length == 1 ? size[0] : r.h);
            case "BELOW":
                return r.below(size.length == 1 ? size[0] : r.h);
            default:
                return r;
        }
    }

    void setSettings(ImageORObject obj) {
        SCREEN.setROI(obj.getRoi());
        org.sikuli.basics.Settings.MinSimilarity = obj.getPrecision();
        index = obj.getIndex();
    }

    List<?> getList(Iterator<?> it) {
        if (!it.hasNext()) {
            return null;
        }
        List<Object> ls = new ArrayList<>();
        while (it.hasNext()) {
            ls.add(it.next());
        }
        return ls;
    }

    public List<String> getObjectAreas(Object target, String name) {
        List<Map<String, String>> objectList = new ArrayList<>();
        if (target instanceof Region) {
            Region region = (Region) target;
            Map<String, String> obMap = new HashMap<>();
            obMap.put("name", name);
            obMap.put("area", "[" + region.x + "," + region.y + "," + region.w + "," + region.h + "]");
            objectList.add(obMap);
        }
        try {
            if (!objectList.isEmpty()) {
                return Arrays.asList(new ObjectMapper().writeValueAsString(objectList));
            }
        } catch (JsonProcessingException ex) {
            Logger.getLogger(ImageCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public boolean isHeadless() {
        return getDriverControl().isHeadless();
    }

    public void pageDownBrowser(int dh) {
        if (isHeadless()) {
            SCREEN.type(Key.PAGE_DOWN);
        } else {
            dh = Math.max(0, dh);
            JavascriptExecutor jse = ((JavascriptExecutor) Driver);
            jse.executeScript(String.format("window.scrollBy(0, window.innerHeight-%s)", dh));
        }
    }

}
