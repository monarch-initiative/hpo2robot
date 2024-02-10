package org.monarchinitiative.hpo2robot.controller.services;

import javafx.scene.control.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class TemplateService {


    private static final String HIGH_IN_BLOOD = "The concentration of --- in the blood circulation is above the upper limit of normal";
    private static final String LOW_IN_BLOOD = "The concentration of --- in the blood circulation is below the lower limit of normal";
    private static final String HIGH_IN_URINE = "The amount of --- in the urine, normalized for urine concentration, is above the upper limit of normal";
    private static final String LOW_IN_URINE = "The amount of --- in the urine, normalized for urine concentration, is above the upper limit of normal";
    private static final String HIGH_IN_CSF = "The concentration of --- in the cerebrospinal fluid (CSF) is above the upper limit of normal";
    private static final String LOW_IN_CSF = "The concentration of --- in the cerebrospinal fluid (CSF) is below the lower limit of normal";

    private static final String MASS = "A swelling or enlargment localized to the --. The word mass is usually used at an early stage of the diagnostic workup before the precise nature of the swelling has been determined.";

    private static final String T1_HYPER = "A lighter than expected T1 signal on magnetic resonance imaging (MRI) of the ---. This term refers to a localized hyperintensity affecting a particular region of the ---.";
    private static final String T2_HYPER = "A lighter than expected T2 signal on magnetic resonance imaging (MRI) of the ---. This term refers to a localized hyperintensity affecting a particular region of the ---.";

    private static final String ABSCESS = "An abscess (i.e., a circumscribed area of pus or necrotic debris) localized in the ---.";

    private static final Map<String, String> labelDefinitionMap;

    static {
        labelDefinitionMap = new HashMap<>();
        labelDefinitionMap.put("high in blood", HIGH_IN_BLOOD);
        labelDefinitionMap.put("low in blood", LOW_IN_BLOOD);
        labelDefinitionMap.put("high in urine", HIGH_IN_URINE);
        labelDefinitionMap.put("low in urine", LOW_IN_URINE);
        labelDefinitionMap.put("high in CSF", HIGH_IN_CSF);
        labelDefinitionMap.put("low in CSF", LOW_IN_CSF);
        labelDefinitionMap.put("mass", MASS);
        labelDefinitionMap.put("T1 hyperintensity", T1_HYPER);
        labelDefinitionMap.put("T2 hyperintensity", T2_HYPER);
        labelDefinitionMap.put("abscess", ABSCESS);
    }

    public static List<MenuItem> getTemplateMenuItems() {
        List<MenuItem> menuItemList = new ArrayList<>();
        for (var e : labelDefinitionMap.entrySet()) {
            String label = e.getKey();
            String template = e.getValue();
            MenuItem mitem = new MenuItem(label);
            mitem.setOnAction(event -> {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(template);
                clipboard.setContent(content);
            });
            menuItemList.add(mitem);
        }
        return menuItemList;
    }

}
