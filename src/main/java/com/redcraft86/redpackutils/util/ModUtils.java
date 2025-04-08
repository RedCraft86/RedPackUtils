package com.redcraft86.redpackutils.util;

import java.util.List;
import java.util.ArrayList;

public class ModUtils {
    public static List<String> processTooltipKeys(List<String> tooltipKeys) {
        List<String> toReturn = new ArrayList<>();
        for (String tooltip : tooltipKeys) {
            toReturn.add("tooltip.redpackutils." + tooltip);
        }
        return toReturn;
    }
}
